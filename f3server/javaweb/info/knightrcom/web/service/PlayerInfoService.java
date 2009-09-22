package info.knightrcom.web.service;

import info.knightrcom.data.metadata.PlayerScore;
import info.knightrcom.util.StringHelper;
import info.knightrcom.web.model.entity.TodayInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;

public class PlayerInfoService extends F3SWebService<PlayerScore> {

    public void processQuerySetting(Query query, HttpServletRequest request) {
        query.setString(0, request.getParameter("CURRENT_PROFILE_ID"));
        query.setTimestamp(1, StringHelper.toTimeStamp(request.getParameter("FROM_DATE"), "yyyyMMdd"));
        query.setTimestamp(2, StringHelper.toTimeStamp(request.getParameter("TO_DATE"), "yyyyMMdd"));
        query.setInteger(3, new Integer(request.getParameter("SHOW_CONDITION")));
        query.setInteger(4, new Integer(request.getParameter("SHOW_CONDITION")));
        query.setString(5, request.getParameter("GAME_TYPE"));
        query.setString(6, request.getParameter("GAME_TYPE"));
    }

    public String getNamedQuery() {
        return "SCORE_INFO";
    }

    public String getNamedQueryForCount() {
        return "SCORE_INFO_COUNT";
    }

    public ResultTransformer getResultTransformer() {
        return Transformers.aliasToBean(TodayInfo.class);
    }

    @Override
    public Class<?>[] getAliasTypes() {
        return new Class<?>[] {PlayerScore.class, TodayInfo.class};
    }

    /**
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String HISTORY_RECHARGE_INFO(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}

    /**
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String CHANGE_PASSWORD(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}
}
