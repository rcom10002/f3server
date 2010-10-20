package info.knightrcom.model.game.pushdownwin;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.GameRecord;
import info.knightrcom.data.metadata.PlayerProfile;
import info.knightrcom.data.metadata.PlayerProfileDAO;
import info.knightrcom.data.metadata.PlayerScore;
import info.knightrcom.model.game.Game;
import info.knightrcom.model.global.Player;
import info.knightrcom.model.plaything.MahjongPointCalculator;
import info.knightrcom.model.plaything.MahjongWinningRule;
import info.knightrcom.model.plaything.MahjongWinningRule.FullRecordSupport;

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
    private double pointMark;

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
        double resultScore = 0;
        String playerIds = "";

        while (itr.hasNext()) {
            // 取得玩家信息
            Player player = itr.next();
            String playerId = player.getId();
            PlayerProfile playerProfile = (PlayerProfile) HibernateSessionFactory.getSession().createCriteria(PlayerProfile.class).add(Restrictions.eq(PlayerProfileDAO.USER_ID, playerId)).uniqueResult();
            playerIds += player.getCurrentNumber() + "~" + playerId + "~";

            // 计算得分
            resultScore = this.getGameMark() + getPointScore(gameRecord);
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
        double resultScore = 0;
        String playerIds = "";
        while (itr.hasNext()) {
            // 取得玩家信息
            Player player = itr.next();
            String playerId = player.getId();
            PlayerProfile playerProfile = (PlayerProfile) HibernateSessionFactory.getSession().createCriteria(PlayerProfile.class).add(Restrictions.eq(PlayerProfileDAO.USER_ID, playerId)).uniqueResult();
            playerIds += player.getCurrentNumber() + "~" + playerId + "~";
            
            // 计算番数
            // 计算得分
            resultScore = this.getGameMark() + getPointScore(gameRecord);
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
	 * 
	 * @param gameRecord
	 * @return
	 */
	private double getPointScore(GameRecord gameRecord) {
        // 记录格式说明：常规记录~最后一条常规记录~(玩家胡牌记录)?
	    String commonRulePropertiesPath = "/info/knightrcom/model/game/pushdownwin/pushdownwin.common.rule.properties";
	    String nativeRulePropertiesPath = "/info/knightrcom/model/game/pushdownwin/pushdownwin.native.rule.properties";
	    double standardMark = MahjongPointCalculator.calculatePointMark(gameRecord.getRecord(), this.getGameMark(), commonRulePropertiesPath, MahjongWinningRule.class);
	    standardMark += MahjongPointCalculator.calculatePointMark(gameRecord.getRecord(), this.getGameMark(), nativeRulePropertiesPath, NativeRule.class);
	    return standardMark;
	}

	/**
	 * @return
	 */
	public double getPointMark() {
		return pointMark;
	}

	/**
	 * @param pointMark
	 */
	public void setPointMark(double pointMark) {
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
        @FullRecordSupport
        public static boolean 天和(String mahjongs) {
            return false;
        }

        @FullRecordSupport
        public static boolean 地和(String mahjongs) {
            return false;
        }
    }
}
