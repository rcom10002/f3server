package info.knightrcom.web.service;

import info.knightrcom.data.metadata.PlayerScore;
import info.knightrcom.util.StringHelper;
import info.knightrcom.web.model.entity.ReportBusinessInfo;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Query;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;

public class ReportBusinessService extends F3SWebService<PlayerScore> {

    public void processQuerySetting(Query query, HttpServletRequest request) {

        query.setTimestamp(0, StringHelper.toTimeStamp(request.getParameter("FROM_DATE"), "yyyyMMdd"));
        query.setTimestamp(1, StringHelper.toTimeStamp(request.getParameter("TO_DATE"), "yyyyMMdd"));
        query.setString(2, request.getParameter("USER_ID"));
        query.setString(3, request.getParameter("USER_ID"));
        query.setString(4, request.getParameter("USER_ID"));
        query.setTimestamp(5, StringHelper.toTimeStamp(request.getParameter("FROM_DATE"), "yyyyMMdd"));
        query.setTimestamp(6, StringHelper.toTimeStamp(request.getParameter("TO_DATE"), "yyyyMMdd"));
        query.setInteger(7, new Integer(request.getParameter("SHOW_CONDITION")));
        query.setInteger(8, new Integer(request.getParameter("SHOW_CONDITION")));
    }

    public String getNamedQuery() {
        return "BUSINESS_INFO";
    }

    public String getNamedQueryForCount() {
        return "BUSINESS_INFO_COUNT";
    }

    public ResultTransformer getResultTransformer() {
        return Transformers.aliasToBean(ReportBusinessInfo.class);
    }

    @Override
    public Class<?>[] getAliasTypes() {
        return new Class<?>[] {PlayerScore.class, ReportBusinessInfo.class};
    }
}
