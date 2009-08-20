package info.knightrcom.web.service;

import info.knightrcom.F3ServerProxy;
import info.knightrcom.F3ServerProxy.FeedbackStatus;
import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.GameFeedback;
import info.knightrcom.data.metadata.GameRecordDAO;
import info.knightrcom.web.model.EntityInfo;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;

/**
 *
 */
public class SystemInfoService extends F3SWebServiceAdaptor<GameFeedback> {

	@Override
	public Class<?>[] getAliasTypes() {
		return new Class[]{GameFeedback.class, Object[].class};
	}

    @Override
    public ResultTransformer getResultTransformer() {
        return Transformers.ALIAS_TO_ENTITY_MAP;
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

    /**
     * 查询游戏运行时信息
     * 
     * @param request
     * @param response
     * @return
     */
    public String RETRIEVE_GAME_RUNTIME_INFO(HttpServletRequest request, HttpServletResponse response) {
        // ROOM_NAME, PLAYING_NUM, WAITING_NUM, WONDER_NUM, TOTAL_NUM
        // 10 3 HU
//        ModelUtil.
//        Map<String, String> gameRunningInfo = new HashMap<String, String>();
//        EntityInfo<GameRecord> info = new EntityInfo<GameRecord>();
//        info.setEntity(entity)
//        return toXML();
        return null;
    }
}
