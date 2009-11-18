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
	private void processInsertSetting(Query query, HttpServletRequest request) {
		for (int i = 0; i < 14; i++) {
			if (i%2 == 0){
				query.setTimestamp(i, StringHelper.toTimeStamp(request.getParameter("FROM_DATE"), "yyyyMMdd"));
			} else {
				query.setTimestamp(i, StringHelper.toTimeStamp(request.getParameter("TO_DATE"), "yyyyMMdd"));
			}
		}
		
        String userId = StringHelper.escapeSQL(request.getParameter("USER_ID")) == null ? "" : StringHelper.escapeSQL(request.getParameter("USER_ID"));
        query.setString(14, "%" + userId + "%");
        final PlayerProfile profile = (PlayerProfile)HibernateSessionFactory.getSession().createCriteria(
                PlayerProfile.class).add(Restrictions.eq("userId", request.getParameter("CURRENT_USER_ID"))).uniqueResult();
        if ("GroupUser".equals(profile.getRole())) {
        	query.setString(15, profile.getUserId());
        } else {
        	query.setString(15, null);
        }
        query.setString(16, profile.getUserId() + "%");
	}
	
    @Override
    public String getNamedQuery() {
//        return "REPORT_SCORE_INFO";
//    	return "SELECT_PERIODLY_SUM";
    	return "SELECT_PERIODLY_SUM_EXT";
    }

    @Override
    public String getNamedQueryForCount() {
//        return "REPORT_SCORE_INFO_COUNT";
//    	return "SELECT_PERIODLY_SUM_COUNT";
    	return "SELECT_PERIODLY_SUM_EXT_COUNT";
    }

    @Override
    public ResultTransformer getResultTransformer() {
    	return Transformers.aliasToBean(ReportScoreInfo.class);
//    	return Transformers.ALIAS_TO_ENTITY_MAP;
    }


    @Override
    public Class<?>[] getAliasTypes() {
    	return new Class<?>[] {PeriodlySum.class, ReportScoreInfo.class};
//    	return new Class<?>[] {PeriodlySum.class};
    }
    
    @SuppressWarnings("unchecked")
	public String READ_PERIODLY_SUM(HttpServletRequest request, HttpServletResponse response) {
    	// 查看是否曾经查询过
    	Query query = HibernateSessionFactory.getSession().getNamedQuery(getNamedQueryForCount());
        this.processQuerySetting(query, request);
        int recordCount = ((Map<String, BigInteger>)query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).uniqueResult()).get("RECORD_COUNT").intValue();
        // 未查询过时进行积分统计
        if (recordCount == 0) {
        	// 将统计出的结果手插入到periodly_sum表 [防止重复查询]
//        	query = HibernateSessionFactory.getSession().getNamedQuery("INSERT_PERIODLY_SUM");
        	query = HibernateSessionFactory.getSession().getNamedQuery("INSERT_PERIODLY_SUM_EXT");
        	processInsertSetting(query, request);
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
			final String[] header = new String[] { "用户ID", "总次数", "总积分", "获胜次数", "获胜积分", "失败次数", "失败积分", "平局次数" , "平局积分", "总系统分", "开始统计时间", "结束统计时间"};
			// the actual writing
			writer.writeHeader(header);
			for (Map map : list) {
				// set up some data to write
				final HashMap<String, ? super Object> data = new HashMap<String, Object>();
				data.put(header[0], map.get("userId"));
				data.put(header[1], map.get("totalTimes"));
				data.put(header[2], map.get("totalScores"));
				data.put(header[3], map.get("winTimes"));
				data.put(header[4], map.get("winScores"));
				data.put(header[5], map.get("loseTimes"));
				data.put(header[6], map.get("loseScores"));
				data.put(header[7], map.get("drawTimes"));
				data.put(header[8], map.get("drawScores"));
				data.put(header[9], map.get("totalSystemScore"));
				data.put(header[10], map.get("startDate"));
				data.put(header[11], map.get("endDate"));
				
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
}
