package info.knightrcom.web.service;

import info.knightrcom.F3ServerProxy.LogType;
import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.GameRecord;
import info.knightrcom.data.metadata.GameRecordDAO;
import info.knightrcom.data.metadata.LogInfo;
import info.knightrcom.data.metadata.PlayerProfile;
import info.knightrcom.data.metadata.PlayerProfileDAO;
import info.knightrcom.data.metadata.PlayerScore;
import info.knightrcom.util.StringHelper;
import info.knightrcom.util.SystemLogger;
import info.knightrcom.web.model.EntityInfo;

import java.util.Date;
import java.util.List;
import java.util.UUID;

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
    @SuppressWarnings("unchecked")
	public String LOAD_GAME_RECORD(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	GameRecord gameRecord = new GameRecordDAO().findById(request.getParameter("GAME_ID"));
    	PlayerProfile playerProfile = new PlayerProfileDAO().findById(request.getParameter("CURRENT_PROFILE_ID"));
    	// 更新看过录像标识
		List<PlayerScore> playerScores = HibernateSessionFactory.getSession().createCriteria(
				PlayerScore.class).add(
                        Restrictions.eq("profileId", request.getParameter("CURRENT_PROFILE_ID"))).add(
                        Restrictions.eq("gameId", request.getParameter("GAME_ID"))).add(
                        Restrictions.eq("status", "PLAYVEDIO")).list();
    	// 查看录像是否扣底分
    	if (playerScores == null || playerScores.size() == 0) {
    		// 在用户信息中保存总积分
    		playerProfile.setCurrentScore(playerProfile.getCurrentScore() - 2 * gameRecord.getScore());
    		HibernateSessionFactory.getSession().save(playerProfile);
    		// 将扣分记录写入用户积分中
            PlayerScore playerScore = new PlayerScore();
            playerScore.setScoreId(UUID.randomUUID().toString());
            playerScore.setProfileId(playerProfile.getProfileId());
            playerScore.setGameId(gameRecord.getGameId());
            playerScore.setUserId(playerProfile.getUserId());
            playerScore.setCurrentNumber("-1");
            playerScore.setCurScore(0d); // 玩家当前得分
            playerScore.setSysScore(2 * gameRecord.getScore()); // 系统当前得分
            playerScore.setOrgScores(playerProfile.getCurrentScore() + 2 * gameRecord.getScore()); // 玩家原始总积分
            playerScore.setCurScores(playerProfile.getCurrentScore()); // 玩家当前总积分
    		playerScore.setStatus("PLAYVEDIO");
    		HibernateSessionFactory.getSession().save(playerScore);
    	}
        EntityInfo<GameRecord> info = new EntityInfo<GameRecord>();
        info.setEntity(gameRecord);
        // 看录像需要写日志
        LogInfo logInfo = SystemLogger.createLog("SystemInfoService Successfully", null, "user [" + playerProfile.getUserId() +"] at system time [" + StringHelper.formatTimeStamp(new Date()) + "] view vedio, game id is [" + gameRecord.getGameId() + "]", LogType.SYSTEM_LOG);
        HibernateSessionFactory.getSession().save(logInfo);
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
     * 读取游戏信息（游戏底分信息等）
     * 
     * @param request
     * @param response
     * @return
     * 
     * ゲームビデオの放送について
     * 
     * このユーザはゲームを参加しない時、いろいろな情報を取得しない。
     * ゲームを参加しないユーザはビデオの放送を使用できる。
     * 正確の検索条件はユーザのプロファイルIDとゲームIDとPLAYVEDIO状態を使用する。
     * 検索件数は一件以上の時、ログを記録だけ。
     * ゼロ件数を検索する時、ユーザプロファイルテーブルのレコードの分数フィールドを更新して、
     * 分数テーブルのに新しいレコードを追加する、ログを記録する。
     * 
     * 業務をよく考えして、テストをして下さいな！
     */
    public String READ_GAME_INFO(HttpServletRequest request, HttpServletResponse response) {
    	EntityInfo<GameRecord> info = new EntityInfo<GameRecord>();
    	info.setEntity(new GameRecordDAO().findById(request.getParameter("GAME_ID")));
        return toXML(info, GameRecord.class);
    }
    
    
    
}
