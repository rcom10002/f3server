package info.knightrcom.model.game.fightlandlord;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.GameRecord;
import info.knightrcom.data.metadata.PlayerProfile;
import info.knightrcom.data.metadata.PlayerProfileDAO;
import info.knightrcom.data.metadata.PlayerScore;
import info.knightrcom.model.game.Game;
import info.knightrcom.model.global.Player;
import info.knightrcom.model.plaything.PokerColor;
import info.knightrcom.model.plaything.PokerValue;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.hibernate.criterion.Restrictions;

/**
 * Players: P1, P2, P3
 * 
 * 游戏的规则需要在客户端实现
 */
public class FightLandlordGame extends Game<FightLandlordGameSetting> {

    /**
     * 玩家个数
     */
    public static final int PLAYER_COGAME_NUMBER = 3;
    
    /** 游戏积分倍数 */
    private int multiple = 1;

    /**
     * 首发扑克：红桃3
     */
    public static final FightLandlordPoker START_POKER = new FightLandlordPoker(PokerColor.HEART, PokerValue.V3);

    public FightLandlordGame() {
    }

    /**
     * @return
     */
    @Override
    public void persistScore() {
    	log.debug("计算积分 START");
        // 取得玩家以及游戏等信息
    	List<Player> players = this.getPlayers();
        String winnerNumber = getWinnerNumbers().substring(0, 1);
        boolean isFinalSettingPlayerWon = this.getSetting().getPlayerNumber().equals(winnerNumber);
        Iterator<Player> itr = players.iterator();
        // 创建游戏记录
        GameRecord gameRecord = new GameRecord();
        gameRecord.setGameId(UUID.randomUUID().toString());
        gameRecord.setGameId(this.getId());
        gameRecord.setGameType(FightLandlordGame.class.getSimpleName());
        gameRecord.setGameSetting((short)this.getSetting().ordinal());
        gameRecord.setWinnerNumbers(this.getWinnerNumbers());
        // TODO DROP THIS LINE => gameRecord.setScore(score);
        // THE FOLLOWING LINES WERE SET IN THE LAST LINE OF EACH PRIVATE METHOD 
        // gameRecord.setPlayers();
        // gameRecord.setSystemScore(systemScore);
        gameRecord.setRecord(this.getGameRecord());
        gameRecord.setCreateTime(this.getCreateTime());
        // 保存游戏历史记录
        HibernateSessionFactory.getSession().merge(gameRecord);
        // 根据当前游戏规则进行分数计算
        /**
         * 一幅牌计分：
         * 基础分：叫牌的底分，有“1分”“2分”“3分”；
         * 地主胜：地主得 2 × 叫牌的底分，其余两家各得： —叫牌的底分；
         * 地主败：地主得 —2 × 叫牌的底分，其余两家各得： 叫牌的底分；
         * 每出一个有效炸弹（或火箭），分数×2 ；
         * 地主把牌出完，其余两家一张牌都没出，分数×2 ；
         * 两家中有一家出完牌，而地主仅仅出过一手牌，分数×2 。 
         */
        // 在相应几分房间的分数 * 当前局叫的牌的底分
        if (FightLandlordGameSetting.NO_RUSH.equals(this.getSetting())) {
        	persistRushScore(itr, gameRecord, isFinalSettingPlayerWon, this.getLowLevelMark() * getMultiple());
        } else if (FightLandlordGameSetting.ONE_RUSH.equals(this.getSetting())) {
        	persistRushScore(itr, gameRecord, isFinalSettingPlayerWon, this.getLowLevelMark() * getMultiple());
        } else if (FightLandlordGameSetting.TWO_RUSH.equals(this.getSetting())) {
        	persistRushScore(itr, gameRecord, isFinalSettingPlayerWon, this.getMidLevelMark() * getMultiple());
        } else if (FightLandlordGameSetting.THREE_RUSH.equals(this.getSetting())) {
        	persistRushScore(itr, gameRecord, isFinalSettingPlayerWon, this.getHighLevelMark() * getMultiple());
        }
        setMultiple(1);
        log.debug("计算积分 END");
    }

    /**
     * 计算独牌时的游戏积分
     * 
     * @param itr
     * @param gameRecord
     * @param isFinalSettingPlayerWon
     * @param pointMark
     */
    private void persistRushScore(Iterator<Player> itr, GameRecord gameRecord, boolean isFinalSettingPlayerWon, int pointMark) {
        // 独牌
        int gameMark = this.getGameMark();
        String playerIds = "";
        // 假设此局的大小为“X”，如果叫到“独牌”的玩家胜，那么叫牌者赢到2X+2X，反之叫牌者输4X
        while (itr.hasNext()) {
            // 取得玩家信息
            Player player = itr.next();
            String playerId = player.getId();
            PlayerProfile playerProfile = (PlayerProfile) HibernateSessionFactory.getSession().createCriteria(PlayerProfile.class).add(Restrictions.eq(PlayerProfileDAO.USER_ID, playerId)).uniqueResult();
            playerIds += player.getCurrentNumber() + "~" + playerId + "~";
            int resultScore = 0;
            if (isFinalSettingPlayerWon) {
                // 独牌成功
                if (getSetting().getPlayerNumber().equals(player.getCurrentNumber())) {
                    // 为独牌玩家设置积分
                    resultScore = 1 * 2 * gameMark * pointMark;
                } else {
                    // 为其他玩家设置积分
                    resultScore = -1 * gameMark * pointMark;
                }
            } else {
                // 独牌失败
                if (!this.getSetting().getPlayerNumber().equals(player.getCurrentNumber())) {
                    // 为其他玩家设置积分
                    resultScore = 1 * gameMark * pointMark;
                } else {
                    // 为独牌玩家设置积分
                    resultScore = -1 * 2 * gameMark * pointMark;
                }
            }
            // 保存玩家得分信息
            PlayerScore playerScore = new PlayerScore();
            playerScore.setScoreId(UUID.randomUUID().toString());
            playerScore.setProfileId(playerProfile.getProfileId());
            playerScore.setGameId(gameRecord.getGameId());
            playerScore.setUserId(playerProfile.getUserId());
            playerScore.setCurrentNumber(player.getCurrentNumber());
            playerScore.setCurScore(resultScore); // 玩家当前得分
            playerScore.setSysScore(getCustomSystemScore(resultScore)); // 系统当前得分
            playerScore.setOrgScores(playerProfile.getCurrentScore()); // 玩家原始总积分
            playerScore.setCurScores(playerProfile.getCurrentScore() + resultScore); // 玩家当前总积分
            playerProfile.setCurrentScore(playerProfile.getCurrentScore() + resultScore);
            HibernateSessionFactory.getSession().merge(playerProfile);
            HibernateSessionFactory.getSession().merge(playerScore);
            // 保存内存模型玩家得分信息
            getPlayerNumberMap().get(player.getCurrentNumber()).setCurrentScore(playerScore.getCurScore());
            getPlayerNumberMap().get(player.getCurrentNumber()).setSystemScore(playerScore.getSysScore());
        }
        gameRecord.setPlayers(playerIds);
    }

    /**
	 * @return the multiple
	 */
	public int getMultiple() {
		return multiple;
	}

	/**
	 * @param multiple the multiple to set
	 */
	public void setMultiple(int multiple) {
		multiple = this.multiple;
	}
	
	/**
	 * 翻倍积分
	 */
	public void addMultiple() {
		multiple *= 2;
	}

	/* (non-Javadoc)
	 * @see info.knightrcom.model.game.Game#persistDisconnectScore(info.knightrcom.model.global.Player)
	 */
	@Override
	public void persistDisconnectScore(Player disconnectedPlayer) {
		// 创建游戏记录
        GameRecord gameRecord = new GameRecord();
        gameRecord.setGameId(UUID.randomUUID().toString());
        gameRecord.setGameId(this.getId());
        gameRecord.setGameType(FightLandlordGame.class.getSimpleName());
        gameRecord.setGameSetting((short)this.getSetting().ordinal());
        gameRecord.setWinnerNumbers(this.getWinnerNumbers());
        gameRecord.setSystemScore(this.getGameMark());
        gameRecord.setStatus("DISCONNECTED");
        gameRecord.setRecord(this.getGameRecord());
        gameRecord.setCreateTime(this.getCreateTime());
        // 保存游戏历史记录
        HibernateSessionFactory.getSession().merge(gameRecord);
		// 取得当前游戏设置
		int deductStandard = 0;
		if (FightLandlordGameSetting.NO_RUSH.equals(this.getSetting())) {
			// 不叫
			deductStandard = this.getLowLevelMark();
        } else if (FightLandlordGameSetting.ONE_RUSH.equals(this.getSetting())) {
        	// 青龙
			deductStandard = this.getLowLevelMark();
        } else if (FightLandlordGameSetting.TWO_RUSH.equals(this.getSetting())) {
        	// 白虎
			deductStandard = this.getMidLevelMark();
        } else if (FightLandlordGameSetting.THREE_RUSH.equals(this.getSetting())) {
        	// 朱雀
			deductStandard = this.getHighLevelMark();
        }
		
		synchronized(this.getPlayers()) {
			String playerIds = "";
			String playerId = null;
			int resultScore = 0;
			// 掉线玩家需要为其他玩家补偿积分，补偿标准为基本分 × 当前设置等级 × 翻倍 
			int deductedMark = this.getGameMark() * deductStandard * getMultiple();
			// 另拿出一份基本分作为系统分
			int systemScore = this.getGameMark();
			for (Player player : this.getPlayers()) {
				playerId = player.getId();
				PlayerProfile playerProfile = (PlayerProfile) HibernateSessionFactory.getSession().createCriteria(PlayerProfile.class).add(Restrictions.eq("userId", playerId)).uniqueResult();
				playerIds += player.getCurrentNumber() + "~" + player.getId() + "~";
				if (player.getId().equals(disconnectedPlayer.getId())) {
					// 掉线玩家
					resultScore = -1 * (deductedMark * 2 + this.getGameMark());
				} else {
					// 非掉线玩家
					resultScore = deductedMark;
				}
				 // 保存玩家得分信息
	            PlayerScore playerScore = new PlayerScore();
	            playerScore.setScoreId(UUID.randomUUID().toString());
	            playerScore.setProfileId(playerProfile.getProfileId());
	            playerScore.setGameId(gameRecord.getGameId());
	            playerScore.setUserId(playerProfile.getUserId());
	            playerScore.setCurrentNumber(player.getCurrentNumber());
	            playerScore.setCurScore(resultScore); // 玩家当前得分
	            playerScore.setOrgScores(playerProfile.getCurrentScore() - resultScore); // 玩家原始总积分
	            playerScore.setCurScores(playerProfile.getCurrentScore()); // 玩家当前总积分
	            playerProfile.setCurrentScore(playerProfile.getCurrentScore() + resultScore);
	            if (player.getId().equals(disconnectedPlayer.getId())) {
	            	// 掉线玩家
		            playerScore.setSysScore(systemScore); // 系统当前得分
	            } else {
		            playerScore.setSysScore(0); // 系统当前得分
	            }
	            HibernateSessionFactory.getSession().merge(playerProfile);
	            HibernateSessionFactory.getSession().merge(playerScore);
			}
			gameRecord.setPlayers(playerIds);
		}
	}
}
