package info.knightrcom.web.service;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.PeriodlySum;
import info.knightrcom.data.metadata.PlayerScore;
import info.knightrcom.util.StringHelper;
import info.knightrcom.web.model.entity.ReportScoreInfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Query;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;

public class ReportScoreService extends F3SWebService<PlayerScore> {

	public void processQuerySetting(Query query, HttpServletRequest request) {
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
        query.setString(10, "%" + StringHelper.escapeSQL(request.getParameter("USER_ID")) + "%");
        query.setString(11, "%" + StringHelper.escapeSQL(request.getParameter("USER_ID")) + "%");
	}
	
    @Override
    public String getNamedQuery() {
        return "REPORT_SCORE_INFO";
    }

    @Override
    public String getNamedQueryForCount() {
        return "REPORT_SCORE_INFO_COUNT";
    }

    @Override
    public ResultTransformer getResultTransformer() {
    	return Transformers.aliasToBean(ReportScoreInfo.class);
    }


    @Override
    public Class<?>[] getAliasTypes() {
    	return new Class<?>[] {PlayerScore.class, ReportScoreInfo.class};
    }
    
    /**
     * 将统计出的结果手插入到periodly_sum表
     * 防止重复查询
     */
    public void insertPeriodlySum() {
    	List<ReportScoreInfo> result = HibernateSessionFactory.getSession().getNamedQuery(getNamedQuery()).list();
    	for (ReportScoreInfo rs : result) {
    		PeriodlySum periodSum = new PeriodlySum();
    		try {
				PropertyUtils.copyProperties(periodSum, rs);
			} catch (Exception e) {
				e.printStackTrace();
			}
    		HibernateSessionFactory.getSession().save(periodSum);
    	}
    }
}
