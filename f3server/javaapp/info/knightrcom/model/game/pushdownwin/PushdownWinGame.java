package info.knightrcom.model.game.pushdownwin;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.GameRecord;
import info.knightrcom.data.metadata.PlayerProfile;
import info.knightrcom.data.metadata.PlayerProfileDAO;
import info.knightrcom.data.metadata.PlayerScore;
import info.knightrcom.model.game.Game;
import info.knightrcom.model.global.Player;
import info.knightrcom.model.plaything.MahjongWinningRule;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.hibernate.criterion.Restrictions;

/**
 * 推到胡
 */
public class PushdownWinGame extends Game<PushdownWinGameSetting> {

    /**
     * 玩家个数
     */
    public static final int PLAYER_COGAME_NUMBER = 4;

    /** 番分 */
    private int pointMark;

    /** 番数 */
    private int points = 0;

    /** 获胜者牌序 */
    private String winnerMahjongSeq;

    @Override
	public void persistScore() {
        log.debug("计算积分 START");
        // 取得玩家以及游戏等信息
        List<Player> players = this.getPlayers();
        // boolean isFinalSettingPlayerWon = this.getSetting().getPlayerNumber().equals(winnerNumber);
        Iterator<Player> itr = players.iterator();
        // 创建游戏记录
        GameRecord gameRecord = new GameRecord();
        gameRecord.setGameId(UUID.randomUUID().toString());
        gameRecord.setGameId(this.getId());
        gameRecord.setGameType(PushdownWinGame.class.getSimpleName());
        gameRecord.setGameSetting((short)this.getSetting().ordinal());
        gameRecord.setWinnerNumbers(this.getWinnerNumbers());
        // TODO DROP THIS LINE => gameRecord.setScore(score);
        // THE FOLLOWING LINES WERE SET IN THE LAST LINE OF EACH PRIVATE METHOD 
        // gameRecord.setPlayers();
        // gameRecord.setSysScore(systemScore);
        gameRecord.setRecord(this.getGameRecord());
        gameRecord.setScore(this.getGameMark());
        gameRecord.setCreateTime(this.getCreateTime());
        // 保存游戏历史记录
        HibernateSessionFactory.getSession().merge(gameRecord);
        // 根据当前游戏规则进行分数计算
        if (PushdownWinGameSetting.NARROW_VICTORY.equals(this.getSetting())) {
            // 点泡
            String winnerNumber = getWinnerNumbers().substring(0, 1);
            String loserNumber = getWinnerNumbers().substring(2, 3);
            persistNarrowWinScore(itr, gameRecord, winnerNumber, loserNumber);
        } else if (PushdownWinGameSetting.CLEAR_VICTORY.equals(this.getSetting())) {
            // 自摸
            persistClearWinScore(itr, gameRecord, getWinnerNumbers());
        } else {
            persistDummyScore(itr, gameRecord);
        }
        log.debug("计算积分 END");
	}

    /**
     * 计算点炮时的游戏积分
     * 
     * @param itr
     * @param gameRecord
     * @param winnerNumber
     * @param loserNumber
     */
    private void persistNarrowWinScore(Iterator<Player> itr, GameRecord gameRecord, String winnerNumber, String loserNumber) {
        // 点炮 => 取得底分与每番分
        final int basicScore = 20;
        final int pointScore = 10;
        double resultScore = 0;
        double points = 0;
        String playerIds = "";

        while (itr.hasNext()) {
            // 取得玩家信息
            Player player = itr.next();
            String playerId = player.getId();
            PlayerProfile playerProfile = (PlayerProfile) HibernateSessionFactory.getSession().createCriteria(PlayerProfile.class).add(Restrictions.eq(PlayerProfileDAO.USER_ID, playerId)).uniqueResult();
            playerIds += player.getCurrentNumber() + "~" + playerId + "~";

            // 计算番数
            points = getPoints(gameRecord);
            // 计算得分
            resultScore = basicScore + pointScore * points;
            if (loserNumber.equals(player.getCurrentNumber())) {
                resultScore *= -1;
            } else if (winnerNumber.equals(player.getCurrentNumber())) {
                resultScore = 0;
            }

            // 在赢分玩家中直接从本局当前得分中扣除系统分
            double currentSystemScore = getCustomSystemScore(resultScore);
            if (resultScore > 0) {
                resultScore -= currentSystemScore;
            }

            // 设置本场比赛得分
            playerProfile.setCurrentScore(playerProfile.getCurrentScore() + resultScore);
            // 保存玩家得分信息
            PlayerScore playerScore = new PlayerScore();
            playerScore.setScoreId(UUID.randomUUID().toString());
            playerScore.setProfileId(playerProfile.getProfileId());
            playerScore.setGameId(gameRecord.getGameId());
            playerScore.setUserId(playerProfile.getUserId());
            playerScore.setCurrentNumber(player.getCurrentNumber());
            playerScore.setCurScore(resultScore); // 玩家当前得分
            playerScore.setSysScore(currentSystemScore); // 系统当前得分
            playerScore.setOrgScores(playerProfile.getCurrentScore() - resultScore); // 玩家原始总积分
            playerScore.setCurScores(playerProfile.getCurrentScore()); // 玩家当前总积分
            HibernateSessionFactory.getSession().merge(playerProfile);
            HibernateSessionFactory.getSession().merge(playerScore);
            // 保存内存模型玩家得分信息
            getPlayerNumberMap().get(player.getCurrentNumber()).setPlayerCurrentScore(playerProfile.getCurrentScore());
            getPlayerNumberMap().get(player.getCurrentNumber()).setCurrentScore(playerScore.getCurScore());
            getPlayerNumberMap().get(player.getCurrentNumber()).setSystemScore(playerScore.getSysScore());
        }
        gameRecord.setPlayers(playerIds);
    }

    /**
     * 计算自摸时的游戏积分
     * 
     * @param itr
     * @param gameRecord
     * @param winnerNumber
     * 
     */
    private void persistClearWinScore(Iterator<Player> itr, GameRecord gameRecord, String winnerNumber) {
        // 自摸 => 取得底分与每番分
        final int basicScore = 20;
        final int pointScore = 10;
        double resultScore = 0;
        double points = 0;
        String playerIds = "";
        while (itr.hasNext()) {
            // 取得玩家信息
            Player player = itr.next();
            String playerId = player.getId();
            PlayerProfile playerProfile = (PlayerProfile) HibernateSessionFactory.getSession().createCriteria(PlayerProfile.class).add(Restrictions.eq(PlayerProfileDAO.USER_ID, playerId)).uniqueResult();
            playerIds += player.getCurrentNumber() + "~" + playerId + "~";
            
            // 计算番数
            points = getPoints(gameRecord);
            // 计算得分
            resultScore = basicScore + pointScore * points;
            if (winnerNumber.equals(player.getCurrentNumber())) {
                resultScore *= 3;
            } else {
                resultScore *= -1;
            }

            // 在赢分玩家中直接从本局当前得分中扣除系统分
            double currentSystemScore = getCustomSystemScore(resultScore);
            if (resultScore > 0) {
                resultScore -= currentSystemScore;
            }

            // 设置本场比赛得分
            playerProfile.setCurrentScore(playerProfile.getCurrentScore() + resultScore);
            // 保存玩家得分信息
            PlayerScore playerScore = new PlayerScore();
            playerScore.setScoreId(UUID.randomUUID().toString());
            playerScore.setProfileId(playerProfile.getProfileId());
            playerScore.setGameId(gameRecord.getGameId());
            playerScore.setUserId(playerProfile.getUserId());
            playerScore.setCurrentNumber(player.getCurrentNumber());
            playerScore.setCurScore(resultScore); // 玩家当前得分
            playerScore.setSysScore(currentSystemScore); // 系统当前得分
            playerScore.setOrgScores(playerProfile.getCurrentScore() - resultScore); // 玩家原始总积分
            playerScore.setCurScores(playerProfile.getCurrentScore()); // 玩家当前总积分
            HibernateSessionFactory.getSession().merge(playerProfile);
            HibernateSessionFactory.getSession().merge(playerScore);
            // 保存内存模型玩家得分信息
            getPlayerNumberMap().get(player.getCurrentNumber()).setPlayerCurrentScore(playerProfile.getCurrentScore());
            getPlayerNumberMap().get(player.getCurrentNumber()).setCurrentScore(playerScore.getCurScore());
            getPlayerNumberMap().get(player.getCurrentNumber()).setSystemScore(playerScore.getSysScore());
        }
        gameRecord.setPlayers(playerIds);
    }

    /**
     * 计算流局时的游戏积分
     * 
     * @param itr
     * @param gameRecord
     */
    public void persistDummyScore(Iterator<Player> itr, GameRecord gameRecord) {
        // 自摸 => 取得底分与每番分
        double resultScore = 0;
        String playerIds = "";
        while (itr.hasNext()) {
            // 取得玩家信息
            Player player = itr.next();
            String playerId = player.getId();
            PlayerProfile playerProfile = (PlayerProfile) HibernateSessionFactory.getSession().createCriteria(PlayerProfile.class).add(Restrictions.eq(PlayerProfileDAO.USER_ID, playerId)).uniqueResult();
            playerIds += player.getCurrentNumber() + "~" + playerId + "~";

            // 设置本场比赛得分
            playerProfile.setCurrentScore(playerProfile.getCurrentScore() + resultScore);
            // 保存玩家得分信息
            PlayerScore playerScore = new PlayerScore();
            playerScore.setScoreId(UUID.randomUUID().toString());
            playerScore.setProfileId(playerProfile.getProfileId());
            playerScore.setGameId(gameRecord.getGameId());
            playerScore.setUserId(playerProfile.getUserId());
            playerScore.setCurrentNumber(player.getCurrentNumber());
            playerScore.setCurScore(resultScore); // 玩家当前得分
            playerScore.setSysScore(0d); // 系统当前得分
            playerScore.setOrgScores(playerProfile.getCurrentScore() - resultScore); // 玩家原始总积分
            playerScore.setCurScores(playerProfile.getCurrentScore()); // 玩家当前总积分
            HibernateSessionFactory.getSession().merge(playerProfile);
            HibernateSessionFactory.getSession().merge(playerScore);
            // 保存内存模型玩家得分信息
            getPlayerNumberMap().get(player.getCurrentNumber()).setPlayerCurrentScore(playerProfile.getCurrentScore());
            getPlayerNumberMap().get(player.getCurrentNumber()).setCurrentScore(playerScore.getCurScore());
            getPlayerNumberMap().get(player.getCurrentNumber()).setSystemScore(playerScore.getSysScore());
        }
        gameRecord.setPlayers(playerIds);
    }

	/**
	 * 番数计算
	 */
	private int getPoints(GameRecord gameRecord) {
        // 记录格式说明：常规记录~最后一条常规记录~(逗号分隔的玩家手中牌;逗号分隔的玩家亮出牌;){4}
        // 4~W3~1~21;1~W1;1~W1~2;2~T7;2~T7~3;3~W2;3~W2~4;4~W5;4~W5~1;1~EAST;1~EAST~2;2~T8;2~T8~3;3~W1;3~W1~4;4~B5;4~B5~1;1~T1;1~T1~2;2~B7;2~B7~3;1~B7,B7,B7~2~2~B7~2;1~B9~2;2~W7;2~W7~3;3~T1;3~T1~4;4~W9;4~W9~1;1~NORTH;1~NORTH~2;2~WEST;2~WEST~3;3~T2;3~T2~4;1~T2~3~
        // B6,B6,T3,T4;W2,W3,W4,W8,W8,W8,B7,B7,B7;EAST,EAST,WEST,RED,WHITE,W3,W3,W8,B2,B6,B7,T5,T7;;SOUTH,NORTH,W2,B1,B1,B2,B4,B5,T1,T4,T6,T6,T7;;SOUTH,WEST,RED,WHITE,W6,W6,W6,B8,B8,T3,T4,T8,T9;;

		String result = gameRecord.getRecord().replaceAll(".*#(.*)", "$1").replaceAll(";$", "");
		if (points == 0) {
			points = 1;
		}
		if (MahjongWinningRule.十三幺(result)) {
			points *= 100;
			return points;
		}
		if (MahjongWinningRule.字一色(result)) {
			points *= 8;
			return points;
		} 
		if (MahjongWinningRule.碰碰和(result)) {
			points *= 4;
		}
		if (MahjongWinningRule.清一色(result)) {
			points *= 4;
		}
		if (MahjongWinningRule.七对(result)) {
			points *= 8;
		}
		return points;
		/*
	    if (true) {
	        return 0;
	    }
		if (points > 0) {
			return points;
		}
		// this.winnerMahjongSeq : split
		return (points = new Random().nextInt(10));
		*/
	}
	
	/**
	 * FIXME drop this method or make this an interface for testing
	 * 
	 * @param gameRecord
	 * @return
	 * 
     * @deprecated
	 */
	public int testGetPoints(GameRecord gameRecord) {
		return this.getPoints(gameRecord);
	}

	/**
	 * @return
	 */
	public int getPointMark() {
		return pointMark;
	}

	/**
	 * @param pointMark
	 */
	public void setPointMark(int pointMark) {
		this.pointMark = pointMark;
	}

	/**
	 * @return
	 */
	public String getWinnerMahjongSeq() {
		return winnerMahjongSeq;
	}

	/**
	 * @param winnerMahjongSeq
	 */
	public void setWinnerMahjongSeq(String winnerMahjongSeq) {
		this.winnerMahjongSeq = winnerMahjongSeq;
	}

	@Override
	public void persistDisconnectScore(Player disconnectedPlayer) {
	    // TODO test this function
        // 创建游戏记录
        GameRecord gameRecord = new GameRecord();
        gameRecord.setGameId(UUID.randomUUID().toString());
        gameRecord.setGameId(this.getId());
        gameRecord.setGameType(PushdownWinGame.class.getSimpleName());
//        if (this.getSetting() != null) {
//            // this.setSetting(PushdownWinGameSetting.); TODO delete this line ?
//            gameRecord.setGameSetting((short)this.getSetting().ordinal());
//        } else {
//            // this.setSetting(Red5GameSetting.NO_RUSH);
//            gameRecord.setGameSetting((short)Red5GameSetting.NO_RUSH.ordinal());
//        }
        gameRecord.setWinnerNumbers(this.getWinnerNumbers());
        gameRecord.setSystemScore(this.getGameMark());
        gameRecord.setStatus("DISCONNECTED");
        gameRecord.setRecord(this.getGameRecord());
        gameRecord.setCreateTime(this.getCreateTime());
        // 保存游戏历史记录
        HibernateSessionFactory.getSession().merge(gameRecord);
        // 根据当前游戏设置，取得惩罚标准，默认扣一倍
        double deductStandard = 1;
//        if (this.getSetting().equals(Red5GameSetting.RUSH)) {
//            // 独牌
//            deductStandard = this.getLowLevelMark();
//        } else if (this.getSetting().equals(Red5GameSetting.DEADLY_RUSH)) {
//            // 天独
//            deductStandard = this.getMidLevelMark();
//        } else if (this.getSetting().equals(Red5GameSetting.EXTINCT_RUSH)) {
//            // 天外天
//            deductStandard = this.getHighLevelMark();
//        }
        synchronized(this.getPlayers()) {
            String playerIds = "";
            String playerId = null;
            double resultScore = 0;
            // 掉线玩家需要为其他玩家补偿积分，补偿标准为基本分 × 当前设置等级
            double deductedMark = this.getGameMark() * deductStandard;
            for (Player player : this.getPlayers()) {
                playerId = player.getId();
                PlayerProfile playerProfile = (PlayerProfile) HibernateSessionFactory.getSession().createCriteria(PlayerProfile.class).add(Restrictions.eq("userId", playerId)).uniqueResult();
                playerIds += player.getCurrentNumber() + "~" + player.getId() + "~";
                if (player.getId().equals(disconnectedPlayer.getId())) {
                    // 掉线玩家
                    resultScore = -1 * (deductedMark * 3);
                } else {
                    // 非掉线玩家
                    resultScore = deductedMark;
                }
    
                // 在赢分玩家中直接从本局当前得分中扣除系统分
                double currentSystemScore = getCustomSystemScore(resultScore);
                if (resultScore > 0) {
                    resultScore -= currentSystemScore;
                }

                playerProfile.setCurrentScore(playerProfile.getCurrentScore() + resultScore);
                // 保存玩家得分信息
                PlayerScore playerScore = new PlayerScore();
                playerScore.setScoreId(UUID.randomUUID().toString());
                playerScore.setProfileId(playerProfile.getProfileId());
                playerScore.setGameId(gameRecord.getGameId());
                playerScore.setUserId(playerProfile.getUserId());
                playerScore.setCurrentNumber(player.getCurrentNumber());
                playerScore.setCurScore(resultScore); // 玩家当前得分
                playerScore.setSysScore(currentSystemScore); // 系统当前得分
                playerScore.setOrgScores(playerProfile.getCurrentScore() - resultScore); // 玩家原始总积分
                playerScore.setCurScores(playerProfile.getCurrentScore()); // 玩家当前总积分
                HibernateSessionFactory.getSession().merge(playerProfile);
                HibernateSessionFactory.getSession().merge(playerScore);
            }
            gameRecord.setPlayers(playerIds);
        }
	}

	public static class NativeRule {
        public static boolean 天和(String mahjongs) {
            return false;
        }
        public static boolean 地和(String mahjongs) {
            return false;
        }
	}
}
