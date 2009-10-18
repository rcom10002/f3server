package info.knightrcom.web.service;

import info.knightrcom.data.metadata.LogInfo;
import info.knightrcom.util.StringHelper;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Query;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;

public class SystemLogService extends F3SWebService<LogInfo> {

	public void processQuerySetting(Query query, HttpServletRequest request) {
        query.setString(0, request.getParameter("SHOW_CONDITION"));
        query.setString(1, request.getParameter("SHOW_CONDITION"));
		query.setTimestamp(2, StringHelper.toTimeStamp(request.getParameter("FROM_DATE"), "yyyyMMdd"));
        query.setTimestamp(3, StringHelper.toTimeStamp(request.getParameter("TO_DATE"), "yyyyMMdd"));
        query.setString(4, StringHelper.escapeSQL(request.getParameter("CAPTION")) == null ? "%%" : "%" + StringHelper.escapeSQL(request.getParameter("CAPTION")) + "%");
	}
	
    @Override
    public String getNamedQuery() {
    	return "LOG_INFO";
    }

    @Override
    public String getNamedQueryForCount() {
    	return "LOG_INFO_COUNT";
    }

    @Override
    public ResultTransformer getResultTransformer() {
    	return Transformers.ALIAS_TO_ENTITY_MAP;
    }


    @Override
    public Class<?>[] getAliasTypes() {
    	return new Class<?>[] {LogInfo.class};
    }
    
}
