package info.knightrcom.web.service;

import info.knightrcom.data.metadata.PlayerScore;
import info.knightrcom.util.StringHelper;
import info.knightrcom.web.model.entity.TodayInfo;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Query;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;

public class ReportScoreService extends F3SWebService<PlayerScore> {

	public void processQuerySetting(Query query, HttpServletRequest request) {
        query.setString(0, request.getParameter("CURRENT_PROFILE_ID"));
        query.setString(1, request.getParameter("CURRENT_PROFILE_ID"));
        query.setTimestamp(2, StringHelper.toTimeStamp(request.getParameter("FROM_DATE"), "yyyyMMdd"));
        query.setTimestamp(3, StringHelper.toTimeStamp(request.getParameter("TO_DATE"), "yyyyMMdd"));
        query.setInteger(4, new Integer(request.getParameter("SHOW_CONDITION")));
        query.setInteger(5, new Integer(request.getParameter("SHOW_CONDITION")));
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
    	return Transformers.aliasToBean(TodayInfo.class);
    }


    @Override
    public Class<?>[] getAliasTypes() {
    	return new Class<?>[] {PlayerScore.class, TodayInfo.class};
    }
}
