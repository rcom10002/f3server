package info.knightrcom.web.service;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.PeriodlySum;
import info.knightrcom.util.StringHelper;
import info.knightrcom.web.model.entity.ReportScoreInfo;

import java.math.BigInteger;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;

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
        String userId = StringHelper.escapeSQL(request.getParameter("USER_ID")) == null ? "" : StringHelper.escapeSQL(request.getParameter("USER_ID"));
        query.setString(10, "%" + userId + "%");
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
}
