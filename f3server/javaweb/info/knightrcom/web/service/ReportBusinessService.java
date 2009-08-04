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
        String userId = StringHelper.escapeSQL(request.getParameter("USER_ID")) == null ? "" : StringHelper.escapeSQL(request.getParameter("USER_ID"));
        query.setString(2, "%" + userId + "%");
        query.setTimestamp(3, StringHelper.toTimeStamp(request.getParameter("FROM_DATE"), "yyyyMMdd"));
        query.setTimestamp(4, StringHelper.toTimeStamp(request.getParameter("TO_DATE"), "yyyyMMdd"));
        query.setInteger(5, new Integer(request.getParameter("SHOW_CONDITION")));
        query.setInteger(6, new Integer(request.getParameter("SHOW_CONDITION")));
        query.setString(7, StringHelper.escapeSQL(request.getParameter("USER_ID")));
        query.setString(8, userId + "%");
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
