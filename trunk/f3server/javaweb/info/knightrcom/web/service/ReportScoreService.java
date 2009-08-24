package info.knightrcom.web.service;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.PeriodlySum;
import info.knightrcom.data.metadata.PlayerProfile;
import info.knightrcom.util.StringHelper;
import info.knightrcom.web.model.EntityInfo;
import info.knightrcom.web.model.entity.ReportScoreInfo;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
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
        PlayerProfile profile = (PlayerProfile) request.getSession().getAttribute("PROFILE");
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
	public void processInsertSetting(Query query, HttpServletRequest request) {
		query.setTimestamp(0, StringHelper.toTimeStamp(request.getParameter("FROM_DATE"), "yyyyMMdd"));
        query.setTimestamp(1, StringHelper.toTimeStamp(request.getParameter("TO_DATE"), "yyyyMMdd"));
		query.setTimestamp(2, StringHelper.toTimeStamp(request.getParameter("FROM_DATE"), "yyyyMMdd"));
        query.setTimestamp(3, StringHelper.toTimeStamp(request.getParameter("TO_DATE"), "yyyyMMdd"));
        query.setTimestamp(4, StringHelper.toTimeStamp(request.getParameter("FROM_DATE"), "yyyyMMdd"));
        query.setTimestamp(5, StringHelper.toTimeStamp(request.getParameter("TO_DATE"), "yyyyMMdd"));
        query.setTimestamp(6, StringHelper.toTimeStamp(request.getParameter("FROM_DATE"), "yyyyMMdd"));
        query.setTimestamp(7, StringHelper.toTimeStamp(request.getParameter("TO_DATE"), "yyyyMMdd"));
        query.setTimestamp(8, StringHelper.toTimeStamp(request.getParameter("FROM_DATE"), "yyyyMMdd"));
        query.setTimestamp(9, StringHelper.toTimeStamp(request.getParameter("TO_DATE"), "yyyyMMdd"));
        query.setTimestamp(10, StringHelper.toTimeStamp(request.getParameter("FROM_DATE"), "yyyyMMdd"));
        query.setTimestamp(11, StringHelper.toTimeStamp(request.getParameter("TO_DATE"), "yyyyMMdd"));
		
        String userId = StringHelper.escapeSQL(request.getParameter("USER_ID")) == null ? "" : StringHelper.escapeSQL(request.getParameter("USER_ID"));
        query.setString(12, "%" + userId + "%");
        PlayerProfile profile = (PlayerProfile) request.getSession().getAttribute("PROFILE");
        if ("GroupUser".equals(profile.getRole())) {
        	query.setString(13, profile.getUserId());
        } else {
        	query.setString(13, null);
        }
        query.setString(14, profile.getUserId() + "%");
	}
	
    @Override
    public String getNamedQuery() {
//        return "REPORT_SCORE_INFO";
    	return "SELECT_PERIODLY_SUM";
    }

    @Override
    public String getNamedQueryForCount() {
//        return "REPORT_SCORE_INFO_COUNT";
    	return "SELECT_PERIODLY_SUM_COUNT";
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
        	query = HibernateSessionFactory.getSession().getNamedQuery("INSERT_PERIODLY_SUM");
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
    	String filename = "PERIODLY_SUM_" + new java.util.Date().getTime() + ".csv";
    	ICsvMapWriter writer = new CsvMapWriter(new FileWriter(url + filename),
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
			info.setTag("http://" + request.getServerName() + ":" + request.getServerPort() + "/" + request.getContextPath() + "/"+ filename);
			info.setResult(F3SWebServiceResult.SUCCESS);
		} finally {
			writer.close();
		}
		
        return toXML(info, new Class[] {PeriodlySum.class});
    }
}
