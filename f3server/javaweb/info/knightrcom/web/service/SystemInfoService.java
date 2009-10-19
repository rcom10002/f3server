package info.knightrcom.web.service;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.GameRecord;
import info.knightrcom.data.metadata.GameRecordDAO;
import info.knightrcom.data.metadata.PlayerProfile;
import info.knightrcom.data.metadata.PlayerProfileDAO;
import info.knightrcom.data.metadata.PlayerScore;
import info.knightrcom.data.metadata.PlayerScoreDAO;
import info.knightrcom.web.model.EntityInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;

/**
 *
 */
public class SystemInfoService extends F3SWebServiceAdaptor<GameRecord> {

	@Override
	public Class<?>[] getAliasTypes() {
		return new Class[]{GameRecord.class};
	}

    @Override
    public ResultTransformer getResultTransformer() {
        return Transformers.ALIAS_TO_ENTITY_MAP;
    }
    
    @Override
	public void processQuerySetting(Query query, HttpServletRequest request) {
    	query.setString(0, request.getParameter("CURRENT_PROFILE_ID"));
    	query.setString(1, request.getParameter("GAME_ID"));
        
	}
    
    @Override
    public String getNamedQuery() {
        return "READ_PLAYER_INFO";
    }

    /**
     * 获取游戏录像中的游戏记录信息
     * 
     * @param request
     * @param response
     * @return
     */
    public String LOAD_GAME_RECORD(HttpServletRequest request, HttpServletResponse response) {
    	GameRecord gameRecord = new GameRecordDAO().findById(request.getParameter("GAME_ID"));
    	// 查看录像是否扣底分
    	if (!"PLAYVEDIO".equals(request.getParameter("STATUS"))) {
    		PlayerProfile playerProfile = new PlayerProfileDAO().findById(request.getParameter("CURRENT_PROFILE_ID"));
    		playerProfile.setCurrentScore(playerProfile.getCurrentScore() - 2 * gameRecord.getScore());
    		HibernateSessionFactory.getSession().save(playerProfile);
    		// 更新看过录像标识
    		PlayerScore playerScore = (PlayerScore)HibernateSessionFactory.getSession().createCriteria(
    				PlayerScore.class).add(
                            Restrictions.eq("profileId", request.getParameter("CURRENT_PROFILE_ID"))).add(
                            Restrictions.eq("gameId", request.getParameter("GAME_ID"))).uniqueResult();
    		playerScore.setStatus("PLAYVEDIO");
    		HibernateSessionFactory.getSession().save(playerScore);
    	}
        EntityInfo<GameRecord> info = new EntityInfo<GameRecord>();
        info.setEntity(gameRecord);
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
    
    /**
     * 查看用户信息（用户当前积分，查看录像的底分，是否查看过录像等）
     * 
     * @param request
     * @param response
     * @return
     */
    public String READ_PLAYER_INFO(HttpServletRequest request, HttpServletResponse response) {
    	EntityInfo<GameRecord> info = new EntityInfo<GameRecord>();
    	Query query = HibernateSessionFactory.getSession().getNamedQuery(getNamedQuery());
    	processQuerySetting(query, request);
    	query.setResultTransformer(getResultTransformer());
    	info.setTag(query.uniqueResult());
        return toXML(info, getAliasTypes());
    }
    
    
    
}
