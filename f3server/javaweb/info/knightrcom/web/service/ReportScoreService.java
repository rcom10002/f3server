package info.knightrcom.web.service;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.GlobalConfig;
import info.knightrcom.data.metadata.PeriodlySum;
import info.knightrcom.data.metadata.PlayerProfile;
import info.knightrcom.util.StringHelper;
import info.knightrcom.web.constant.GameConfigureConstant;
import info.knightrcom.web.model.EntityInfo;
import info.knightrcom.web.model.entity.ReportScoreInfo;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

public class ReportScoreService extends F3SWebService<PeriodlySum> {

	/* 
	 * 准备查询时要用的参数
	 * @see info.knightrcom.web.service.F3SWebService#processQuerySetting(org.hibernate.Query, javax.servlet.http.HttpServletRequest)
	 */
	public void processQuerySetting(Query query, HttpServletRequest request) {
		query.setTimestamp(0, StringHelper.toTimeStamp(request.getParameter("FROM_DATE"), "yyyyMMdd"));
        query.setTimestamp(1, StringHelper.toTimeStamp(request.getParameter("TO_DATE"), "yyyyMMdd"));
        String userId = StringHelper.escapeSQL(request.getParameter("USER_ID")) == null ? "" : StringHelper.escapeSQL(request.getParameter("USER_ID"));
        query.setString(2, "%" + userId + "%");
        final PlayerProfile profile = (PlayerProfile)HibernateSessionFactory.getSession().createCriteria(
                PlayerProfile.class).add(Restrictions.eq("userId", request.getParameter("CURRENT_USER_ID"))).uniqueResult();
        if ("GroupUser".equals(profile.getRole())) {
        	query.setString(3, profile.getUserId());
        } else {
        	query.setString(3, null);
        }
        query.setString(4, profile.getUserId() + "%");
	}
	
	/**
	 * 准备批量插入要用的参数
	 * @param query
	 * @param request
	 */
	private void processInsertSetting(Query query, HttpServletRequest request, List<String> users) {
		for (int i = 0; i < 14; i++) {
			if (i%2 == 0){
				query.setTimestamp(i, StringHelper.toTimeStamp(request.getParameter("FROM_DATE"), "yyyyMMdd"));
			} else {
				query.setTimestamp(i, StringHelper.toTimeStamp(request.getParameter("TO_DATE"), "yyyyMMdd"));
			}
		}
		query.setParameterList("USERLIST", users);
	}
	
    @Override
    public String getNamedQuery() {
    	return "SELECT_PERIODLY_SUM_EXT";
    }

    @Override
    public String getNamedQueryForCount() {
    	return "SELECT_PERIODLY_SUM_EXT_COUNT";
    }

    @Override
    public ResultTransformer getResultTransformer() {
    	return Transformers.aliasToBean(ReportScoreInfo.class);
    }


    @Override
    public Class<?>[] getAliasTypes() {
    	return new Class<?>[] {PeriodlySum.class, ReportScoreInfo.class};
    }
    
    @SuppressWarnings("unchecked")
	public String READ_PERIODLY_SUM(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	EntityInfo<PeriodlySum> info = new EntityInfo<PeriodlySum>();
    
    	// 查看是否曾经查询过
    	Query query = HibernateSessionFactory.getSession().getNamedQuery(getNamedQueryForCount());
        this.processQuerySetting(query, request);
        int recordCount = ((Map<String, BigInteger>)query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).uniqueResult()).get("RECORD_COUNT").intValue();
        // 未查询过时进行积分统计
        if (recordCount == 0) {
        	// 查询范围是否将所有数据筛选出来
        	// 读取顶级用户
        	List<String> users = getUserRelationsShip(request.getParameter("CURRENT_USER_ID"));
        	java.sql.Timestamp fromDate = StringHelper.toTimeStamp(request.getParameter("FROM_DATE"), "yyyyMMdd");
        	java.sql.Timestamp toDate = StringHelper.toTimeStamp(request.getParameter("TO_DATE"), "yyyyMMdd");
        	// 判断数据是否全部包含
        	boolean bool = isFullResultSet(fromDate, toDate, users);
        	if (!bool) {
        		info.setResult(F3SWebServiceResult.WARNING);
        		return toXML(info, new Class[] {PeriodlySum.class});
        	}
        	// 将统计出的结果手插入到periodly_sum_ext表 [防止重复查询]
        	query = HibernateSessionFactory.getSession().getNamedQuery("INSERT_PERIODLY_SUM_EXT");
        	processInsertSetting(query, request, users);
    		query.executeUpdate();
        }
    	return serializeResponseStream(request, response);
    }
    
    /**
     * 报表导出
     * @param request
     * @param response
     * @return
     * @throws IOException 
     */
	@SuppressWarnings("unchecked")
	public String CSV_EXPORT(HttpServletRequest request, HttpServletResponse response) throws IOException {
		EntityInfo<PeriodlySum> info = new EntityInfo<PeriodlySum>();
    	Query query = HibernateSessionFactory.getSession().getNamedQuery(getNamedQuery());
    	processQuerySetting(query, request);
    	query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
    	List<Map> list = (List<Map>)query.list();
    	String url = request.getSession().getServletContext().getRealPath("/");
    	String filename = "PERIODLY_SUM_" + request.getParameter("FROM_DATE") + "-" + request.getParameter("TO_DATE") + ".csv";
    	ICsvMapWriter writer = new CsvMapWriter(new FileWriter(url + GameConfigureConstant.DOWNLOAD_PATH + filename),
				CsvPreference.EXCEL_PREFERENCE);
    	try {
			final String[] header = { "用户ID", "总次数", "总积分", "获胜次数", "获胜积分", "失败次数", "失败积分", "平局次数" , "平局积分", "总系统分", "玩家当前分", "玩家初始分", "总充值分", "净收益分", "开始统计时间", "结束统计时间"};
			final String[] mapKeys = {"userId", "totalTimes", "totalScores", "winTimes", "winScores", "loseTimes", "loseScores",
					"drawTimes", "drawScores", "totalSystemScore", "currentScore", "playerLimitScore", "rechargeSum", "resultScore", "startDate", "endDate"};
			// the actual writing
			writer.writeHeader(header);
			for (Map map : list) {
				// set up some data to write
				final HashMap<String, ? super Object> data = new HashMap<String, Object>();
				for (int i = 0; i < mapKeys.length; i++) {
					data.put(header[i], map.get(mapKeys[i]));
				}
				writer.write(data, header);
			}
			info.setTag(request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/" + request.getContextPath() + "/" + GameConfigureConstant.DOWNLOAD_PATH + filename);
			info.setResult(F3SWebServiceResult.SUCCESS);
		} finally {
			writer.close();
		}
		
        return toXML(info, new Class[] {PeriodlySum.class});
    }
	
	/**
	 * 获取查询期间起始[周X]
	 * @param request
	 * @param response
	 * @return
	 */
	public String GET_SEARCH_PERIOD(HttpServletRequest request, HttpServletResponse response) {
		EntityInfo<PeriodlySum> info = new EntityInfo<PeriodlySum>();
		final GlobalConfig globalconfig = (GlobalConfig)HibernateSessionFactory.getSession().createCriteria(GlobalConfig.class).add(
                Restrictions.and(Property.forName("type").eq(GameConfigureConstant.SERVER_PARAM_NAME), 
                        Property.forName("name").eq("SEARCH_PERIOD"))).uniqueResult();
		Calendar calendar = Calendar.getInstance();
		// 默认查询期间为1星期
		int period = 1;
		if (globalconfig != null) {
			int startWeek = Integer.valueOf(globalconfig.getValue());
			int distanceDay = startWeek - (calendar.get(Calendar.DAY_OF_WEEK) - 1);
			calendar.add(Calendar.DATE, distanceDay);
		}
		String startDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
		calendar.add(Calendar.WEEK_OF_MONTH, period);
		String endDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
		info.setTag(startDate + "~" + endDate);
		info.setResult(F3SWebServiceResult.SUCCESS);
		return toXML(info, new Class[] {PeriodlySum.class});
	}
	
	/**
	 * 获取当前用户的下一级组的所有用户
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<String> getUserRelationsShip(String userId) {
		Query query = HibernateSessionFactory.getSession().getNamedQuery("USER_RELATIONS_SHIP");
		final PlayerProfile profile = (PlayerProfile)HibernateSessionFactory.getSession().createCriteria(
                PlayerProfile.class).add(Restrictions.eq("userId", userId)).uniqueResult();
		if (profile != null) {
	        if ("Administrator".equals(profile.getRlsPath())) {
				query.setString(0, "null");
				query.setString(1, "null");
				
			} else {
				query.setString(0, profile.getUserId());
				query.setString(1, profile.getUserId());
			}
		} else {
			query.setString(0, "null");
			query.setString(1, "null");
		}
		List<String> resultList = (List<String>)query.list();
		return resultList;
	}
	
	/**
	 * 查询范围是否将所有数据筛选出来
	 * @param fromDate
	 * @param toDate
	 * @param userIds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean isFullResultSet(Timestamp fromDate, Timestamp toDate, List users) {
		// 玩家详细积分
		Query query = HibernateSessionFactory.getSession().getNamedQuery("PLAYER_SCORE_RESULT_COUNT");
		query.setTimestamp(0, fromDate);
		query.setTimestamp(1, toDate);
		query.setParameterList("USERLIST", users);
		List<Object> result = query.list();
		int var[] = new int[2];
		int j = 0;
		for (Object obj : result) {
			var[j++] = ((BigInteger) ((Object[])obj)[1]).intValue(); 
		}
		if (var[0] < var[1]){
			return false;
		}
		// 玩家充值积分
		query = HibernateSessionFactory.getSession().getNamedQuery("RECHARGE_RECORD_RESULT_COUNT");
		query.setTimestamp(0, fromDate);
		query.setTimestamp(1, toDate);
		query.setParameterList("USERLIST", users);
		result = query.list();
		j = 0;
		for (Object obj : result) {
			var[j++] = ((BigInteger) ((Object[])obj)[1]).intValue(); 
		}
		if (var[0] < var[1]){
			return false;
		}
    	return true;
	}
}
