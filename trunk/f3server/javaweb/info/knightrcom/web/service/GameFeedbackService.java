package info.knightrcom.web.service;

import info.knightrcom.F3ServerProxy.FeedbackStatus;
import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.GameFeedback;
import info.knightrcom.data.metadata.GameFeedbackDAO;
import info.knightrcom.data.metadata.GameRecordDAO;
import info.knightrcom.data.metadata.PlayerScore;
import info.knightrcom.util.StringHelper;
import info.knightrcom.web.model.EntityInfo;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;

public class GameFeedbackService extends F3SWebService<GameFeedback>{

    @Override
    public Class<?>[] getAliasTypes() {
        return new Class[]{GameFeedback.class};
    }

    @Override
    public String getNamedQuery() {
        return "GAME_FEEDBACK";
    }

    @Override
    public String getNamedQueryForCount() {
        return "GAME_FEEDBACK_COUNT";
    }

    @Override
    public ResultTransformer getResultTransformer() {
    	return Transformers.ALIAS_TO_ENTITY_MAP;
    }

    @Override
    public void processQuerySetting(Query query, HttpServletRequest request) {
    	query.setString(0, request.getParameter("SHOW_CONDITION"));
        query.setString(1, request.getParameter("SHOW_CONDITION"));
		query.setTimestamp(2, StringHelper.toTimeStamp(request.getParameter("FROM_DATE"), "yyyyMMdd"));
        query.setTimestamp(3, StringHelper.toTimeStamp(request.getParameter("TO_DATE"), "yyyyMMdd"));
        query.setString(4, StringHelper.escapeSQL(request.getParameter("CAPTION")) == null ? "%%" : "%" + StringHelper.escapeSQL(request.getParameter("CAPTION")) + "%");
    }

    /**
     * 获取参与游戏的人员信息
     * @param request
     * @param response
     * @return
     */
    @SuppressWarnings("unchecked")
	public String GET_JOIN_GAME_PLAYER_INFO(HttpServletRequest request, HttpServletResponse response) {
    	Criteria criteria = HibernateSessionFactory.getSession().createCriteria(PlayerScore.class);
        criteria.add(Expression.eq("gameId", request.getParameter("GAME_ID")));
        List<PlayerScore> result = criteria.list();
        StringBuffer strBuf = new StringBuffer();
        for (PlayerScore player : result) {
        	strBuf.append(player.getUserId());
        	strBuf.append(",");
        }
        String playerIds = strBuf.toString().replaceAll(",$", "");
        EntityInfo<GameFeedback> info = new EntityInfo<GameFeedback>();
        info.setTag(playerIds + "~" +result.size());
        info.setResult(F3SWebServiceResult.SUCCESS);
        return toXML(info, new Class[] {GameFeedback.class});
    }
    
    /**
     * 审核举报信息
     * @param request
     * @param response
     * @return
     */
	public String AUDIT_GAME_FEEDBACK(HttpServletRequest request, HttpServletResponse response) {
    	GameFeedback gameFeedback = new GameFeedbackDAO().findById(request.getParameter("FEEDBACK_ID"));
    	gameFeedback.setStatus(request.getParameter("STATUS"));
    	gameFeedback.setUpdateTime(new Date());
    	gameFeedback.setUpdateBy("SYSTEM");
    	HibernateSessionFactory.getSession().update(gameFeedback);
        EntityInfo<GameFeedback> info = new EntityInfo<GameFeedback>();
        info.setResult(F3SWebServiceResult.SUCCESS);
        return toXML(info, new Class[] {GameFeedback.class});
    }

    /**
     * 提交游戏反馈信息
     * 
     * @param request
     * @param response
     * @return
     */
    public String COMMIT_GAME_FEEDBACK(HttpServletRequest request, HttpServletResponse response) {
    	if (new GameRecordDAO().findById(request.getParameter("GAME_ID")) == null) {
    		return toXML(createEntityInfo(null, F3SWebServiceResult.WARNING), getAliasTypes());
    	}
        GameFeedback gameFeedback = new GameFeedback();
        gameFeedback.setFeedbackId(UUID.randomUUID().toString());
        gameFeedback.setGameId(request.getParameter("GAME_ID"));
        gameFeedback.setTitle(request.getParameter("TITLE"));
        gameFeedback.setStatus(FeedbackStatus.NEW_ARRIVAL.name());
        gameFeedback.setDescription(request.getParameter("DESCRIPTION"));
        gameFeedback.setCreateBy(request.getParameter("CURRENT_USER_ID"));
        HibernateSessionFactory.getSession().save(gameFeedback);
        EntityInfo<GameFeedback> info = createEntityInfo(null, F3SWebServiceResult.SUCCESS);
        return toXML(info, getAliasTypes());
    }

    /**
     * 查询反馈历史
     * 
     * @param request
     * @param response
     * @return
     */
    @SuppressWarnings("unchecked")
	public String RETRIEVE_FEEDBACK_HISTORY(HttpServletRequest request, HttpServletResponse response) {
    	Query query = HibernateSessionFactory.getSession().getNamedQuery("RETRIEVE_FEEDBACK_HISTORY");
        query.setResultTransformer(this.getResultTransformer());
        query.setString(0, FeedbackStatus.DONE.name());
        final List result = query.list();
        EntityInfo<GameFeedback> info = createEntityInfo(null, F3SWebServiceResult.SUCCESS);
        info.setTag(result);
        return toXML(info, getAliasTypes());
    }
}
