package info.knightrcom.model.game.qiongwin;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.GameRecord;
import info.knightrcom.data.metadata.PlayerProfile;
import info.knightrcom.data.metadata.PlayerProfileDAO;
import info.knightrcom.data.metadata.PlayerScore;
import info.knightrcom.model.game.Game;
import info.knightrcom.model.global.Player;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.hibernate.criterion.Restrictions;

/**
 * 穷胡
 */
public class QiongWinGame extends Game<QiongWinGameSetting> {

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
        gameRecord.setGameType(QiongWinGame.class.getSimpleName());
        gameRecord.setGameSetting((short)this.getSetting().ordinal());
        gameRecord.setWinnerNumbers(this.getWinnerNumbers());
        // TODO DROP THIS LINE => gameRecord.setScore(score);
        // THE FOLLOWING LINES WERE SET IN THE LAST LINE OF EACH PRIVATE METHOD 
        // gameRecord.setPlayers();
        // gameRecord.setSysScore(systemScore);
        gameRecord.setRecord(this.getGameRecord());
        gameRecord.setCreateTime(this.getCreateTime());
        // 保存游戏历史记录
        HibernateSessionFactory.getSession().merge(gameRecord);
        // 根据当前游戏规则进行分数计算
        if (QiongWinGameSetting.NARROW_VICTORY.equals(this.getSetting())) {
            // 点泡
            String winnerNumber = getWinnerNumbers().substring(0, 1);
            String loserNumber = getWinnerNumbers().substring(2, 3);
            persistNarrowWinScore(itr, gameRecord, winnerNumber, loserNumber);
        } else {
            // 自摸
            persistClearWinScore(itr, gameRecord, getWinnerNumbers());
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
        int resultScore = 0;
        int systemScore = 0;
        int points = 0;
        String playerIds = "";

        while (itr.hasNext()) {
            // 取得玩家信息
            Player player = itr.next();
            String playerId = player.getId();
            PlayerProfile playerProfile = (PlayerProfile) HibernateSessionFactory.getSession().createCriteria(PlayerProfile.class).add(Restrictions.eq(PlayerProfileDAO.USER_ID, playerId)).uniqueResult();
            playerIds += player.getCurrentNumber() + "~" + playerId + "~";

            // 计算番数
            points = getPoints();
            resultScore = basicScore + pointScore * points;

            // 设置本场比赛得分
            if (winnerNumber.equals(player.getCurrentNumber())) {
                // 为获胜玩家设置积分
                playerProfile.setCurrentScore(playerProfile.getCurrentScore() + resultScore);
            } else if (loserNumber.equals(player.getCurrentNumber())) {
                // 为失败玩家设置积分
                playerProfile.setCurrentScore(playerProfile.getCurrentScore() - resultScore);
            } else {
            	// 为其他玩家设置积分
            	playerProfile.setCurrentScore(playerProfile.getCurrentScore());
            }

            // 保存玩家得分信息
            PlayerScore playerScore = new PlayerScore();
            playerScore.setScoreId(UUID.randomUUID().toString());
            playerScore.setProfileId(playerProfile.getProfileId());
            playerScore.setGameId(gameRecord.getGameId());
            playerScore.setUserId(playerProfile.getUserId());
            playerScore.setCurrentNumber(player.getCurrentNumber());
            if (winnerNumber.equals(player.getCurrentNumber())) {
            	playerScore.setCurScore(resultScore); // 玩家当前得分
            } else if (loserNumber.equals(player.getCurrentNumber())) {
            	playerScore.setCurScore(-1 * resultScore); // 玩家当前得分
            } else {
            	playerScore.setCurScore(0); // 玩家当前得分
            }
            playerScore.setSysScore(systemScore); // 系统当前得分
            playerScore.setOrgScores(playerProfile.getCurrentScore()); // 玩家原始总积分
            playerScore.setCurScores(playerProfile.getCurrentScore() + resultScore); // 玩家当前总积分
            HibernateSessionFactory.getSession().merge(playerProfile);
            HibernateSessionFactory.getSession().merge(playerScore);
            // 保存内存模型玩家得分信息
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
        int resultScore = 0;
        int systemScore = 0;
        int points = 0;
        String playerIds = "";
        while (itr.hasNext()) {
            // 取得玩家信息
            Player player = itr.next();
            String playerId = player.getId();
            PlayerProfile playerProfile = (PlayerProfile) HibernateSessionFactory.getSession().createCriteria(PlayerProfile.class).add(Restrictions.eq(PlayerProfileDAO.USER_ID, playerId)).uniqueResult();
            playerIds += player.getCurrentNumber() + "~" + playerId + "~";
            
            // 计算番数
            points = getPoints();
            resultScore = basicScore + pointScore * points;

            // 设置本场比赛得分
            if (winnerNumber.equals(player.getCurrentNumber())) {
                // 为自摸玩家设置积分
                playerProfile.setCurrentScore((playerProfile.getCurrentScore() + resultScore) * 3);
            } else {
                // 为其他玩家设置积分
                playerProfile.setCurrentScore(playerProfile.getCurrentScore() - resultScore);
            }

            // 保存玩家得分信息
            PlayerScore playerScore = new PlayerScore();
            playerScore.setScoreId(UUID.randomUUID().toString());
            playerScore.setProfileId(playerProfile.getProfileId());
            playerScore.setGameId(gameRecord.getGameId());
            playerScore.setUserId(playerProfile.getUserId());
            playerScore.setCurrentNumber(player.getCurrentNumber());
            if (winnerNumber.equals(player.getCurrentNumber())) {
            	playerScore.setCurScore(resultScore * 3); // 玩家当前得分
            } else {
            	playerScore.setCurScore(resultScore * -1); // 玩家当前得分
            }
            playerScore.setSysScore(systemScore); // 系统当前得分
            playerScore.setOrgScores(playerProfile.getCurrentScore()); // 玩家原始总积分
            playerScore.setCurScores(playerProfile.getCurrentScore() + resultScore); // 玩家当前总积分
            HibernateSessionFactory.getSession().merge(playerProfile);
            HibernateSessionFactory.getSession().merge(playerScore);
            // 保存内存模型玩家得分信息
            getPlayerNumberMap().get(player.getCurrentNumber()).setCurrentScore(playerScore.getCurScore());
            getPlayerNumberMap().get(player.getCurrentNumber()).setSystemScore(playerScore.getSysScore());
        }
        gameRecord.setPlayers(playerIds);
    }

	/**
	 * 番数计算
	 */
	private int getPoints() {
		if (points > 0) {
			return points;
		}
		// this.winnerMahjongSeq : split
		return (points = new Random().nextInt(10));
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
		// TODO Auto-generated method stub

		// 掉线玩家需要为其他玩家补偿积分，补偿标准为基本分 × 当前设置等级
		// int deductedMark = this.getGameMark() * deductStandard;
		// 另拿出一份基本分作为系统分
	}

}
