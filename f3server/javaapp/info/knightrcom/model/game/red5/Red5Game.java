package info.knightrcom.model.game.red5;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.GameRecord;
import info.knightrcom.data.metadata.PlayerProfile;
import info.knightrcom.data.metadata.PlayerProfileDAO;
import info.knightrcom.data.metadata.PlayerScore;
import info.knightrcom.model.game.Game;
import info.knightrcom.model.global.Player;
import info.knightrcom.model.plaything.PokerColor;
import info.knightrcom.model.plaything.PokerValue;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.criterion.Restrictions;

/**
 * Players: P1, P2, P3, P4
 * 
 * 游戏的规则需要在客户端实现
 */
public class Red5Game extends Game<Red5GameSetting> {

    /**
     * 玩家个数
     */
    public static final int PLAYER_COGAME_NUMBER = 4;

    /**
     * 首发扑克：红桃10
     */
    public static final Red5Poker START_POKER = new Red5Poker(PokerColor.HEART, PokerValue.V10);

    public Red5Game() {
    }

    @Override
    public void persistScore() {
        log.debug("计算积分 START");
        if (this.getSetting() == null) {
            // FIXME http://code.google.com/p/f3server/issues/detail?id=26
            return;
        }
        // 取得玩家以及游戏等信息
        List<Player> players = this.getPlayers();
        String winnerNumber = getWinnerNumbers().substring(0, 1);
        boolean isFinalSettingPlayerWon = this.getSetting().getPlayerNumber().equals(winnerNumber);
        Iterator<Player> itr = players.iterator();
        // 创建游戏记录
        GameRecord gameRecord = new GameRecord();
        // FIXME DROP THIS LINE
        gameRecord.setGameId(UUID.randomUUID().toString());
        gameRecord.setGameId(this.getId());
        gameRecord.setGameType(Red5Game.class.getSimpleName());
        gameRecord.setGameSetting((short)this.getSetting().ordinal());
        gameRecord.setWinnerNumbers(this.getWinnerNumbers());
        // TODO DROP THIS LINE => gameRecord.setCurScore(score);
        // THE FOLLOWING LINES WERE SET IN THE LAST LINE OF EACH PRIVATE METHOD 
        // gameRecord.setPlayers();
        // gameRecord.setSysScore(systemScore);
        gameRecord.setRecord(this.getGameRecord());
        gameRecord.setScore(this.getGameMark());
        gameRecord.setCreateTime(this.getCreateTime());
        // 保存游戏历史记录
        HibernateSessionFactory.getSession().merge(gameRecord);
        // 根据当前游戏规则进行分数计算
        if (Red5GameSetting.NO_RUSH.equals(this.getSetting())) {
            persistNoRushScore(itr, gameRecord);
        } else if (Red5GameSetting.RUSH.equals(this.getSetting())) {
            persistRushScore(itr, gameRecord, isFinalSettingPlayerWon);
        } else if (Red5GameSetting.DEADLY_RUSH.equals(this.getSetting())) {
            persistDeadlyRushScore(itr, gameRecord, isFinalSettingPlayerWon);
        } else if (Red5GameSetting.EXTINCT_RUSH.equals(this.getSetting())) {
            persistExtinctRushScore(itr, gameRecord, isFinalSettingPlayerWon);
        }
        log.debug("计算积分 END");
    }

    /**
     * 保存七独八天积分
     * 
     * @param winnerNumber 七独八天的玩家
     * @param record 抓牌记录
     * @param setting 独牌或天独
     */
    public String persistGameDeadly7Extinct8() {
        // 取得玩家以及游戏等信息
        List<Player> players = this.getPlayers();
        Iterator<Player> itr = players.iterator();
        // 分析游戏设置
        // (.*?)(~(\d=\d*[,;]){4})
        Pattern pattern = Pattern.compile("(.*?)(~(\\d=\\d*[,;]){4})");
        Matcher matcher = pattern.matcher(this.getGameRecord());
        String[] initPokers = new String[4];
        int i = 0;
        Red5GameSetting localSetting = null;
        String localNumber = null;
        while (matcher.find()) {
            // 去除大小王和5后进行排序
            initPokers[i++] = matcher.group(1);
            initPokers[i - 1] = initPokers[i - 1].replaceAll("0V[XY]|\\dV5", "").replaceAll(",{2,}", ",").replaceAll("^,|,$", "");
            String[] eachPokers = initPokers[i - 1].split(",");
            Arrays.sort(eachPokers, new Comparator<String>() {
                public int compare(String o1, String o2) {
                    // 去花色后比较大小
                    o1 = o1.replaceFirst("\\d", "");
                    o2 = o2.replaceFirst("\\d", "");
                    int p1 = Red5Poker.PRIORITY_SEQUENCE.indexOf(o1);
                    int p2 = Red5Poker.PRIORITY_SEQUENCE.indexOf(o2);
                    if (p1 > p2) {
                        return 1;
                    } else if (p1 < p2) {
                        return -1;
                    }
                    return 0;
                }
            });
            // 去花色并使每个元素后面有个逗号，(\dV\d*,)+
            initPokers[i - 1] = Arrays.toString(eachPokers).replaceAll("[\\s\\[\\]]|\\b\\d", "") + ",";
        }
        // 查找八天
        for (i = 0; i < initPokers.length; i++) {
            if (initPokers[i].matches("^.*(V\\d,)\\1{7}.*$")) {
                localSetting = Red5GameSetting.DEADLY_RUSH;
                localNumber = String.valueOf(i + 1);
                break;
            }
        }
        if (i == initPokers.length) {
            // 查找七独
            for (i = 0; i < initPokers.length; i++) {
                if (initPokers[i].matches("^.*(V\\d,)\\1{6}.*$")) {
                    localSetting = Red5GameSetting.RUSH;
                    localNumber = String.valueOf(i + 1);
                    break;
                }
            }
        }
        if (i == initPokers.length) {
            return null;
        }
        // 创建游戏记录
        GameRecord gameRecord = new GameRecord();
        gameRecord.setGameId(UUID.randomUUID().toString());
        gameRecord.setGameType(Red5Game.class.getSimpleName());
        gameRecord.setGameSetting((short)localSetting.ordinal());
        gameRecord.setWinnerNumbers(localNumber);
        gameRecord.setRecord(this.getGameRecord());
        gameRecord.setScore(this.getGameMark());
        gameRecord.setStatus("GAME_DEADLY7_EXTINCT8");
        // 保存游戏历史记录
        HibernateSessionFactory.getSession().merge(gameRecord);
        // 根据当前游戏规则进行分数计算
        if (Red5GameSetting.RUSH.equals(localSetting)) {
            persistDeadlyRushScore(itr, gameRecord, true);
        } else if (Red5GameSetting.DEADLY_RUSH.equals(localSetting)) {
            persistExtinctRushScore(itr, gameRecord, true);
        }
        return localNumber + "~" + localSetting.ordinal();
    }

    /**
     * 计算非独牌时的游戏积分
     * 
     * @param itr
     * @param gameRecord
     */
    private void persistNoRushScore(Iterator<Player> itr, GameRecord gameRecord) {
        // 不独
        double gameMark = this.getGameMark();
        String playerIds = "";
        while (itr.hasNext()) {
            // 取得玩家信息
            Player player = itr.next();
            String playerId = player.getId();
            PlayerProfile playerProfile = (PlayerProfile) HibernateSessionFactory.getSession().createCriteria(PlayerProfile.class).add(Restrictions.eq(PlayerProfileDAO.USER_ID, playerId)).uniqueResult();
            int playerPlace = getWinnerNumbers().replace("~", "").indexOf(player.getCurrentNumber()) + 1;
            playerIds += player.getCurrentNumber() + "~" + playerId + "~";
            // 假设此局的大小为“X”，如果没有玩家叫牌，那么
            // 第一个出完牌的玩家赢2X，第二位出完牌的玩家赢X，第三为出完牌的玩家输X，最后一 位玩家输2X
            double resultScore = 0;
            switch (playerPlace) {
            case 1:
                // 第一名
                resultScore = 2 * gameMark;
                break;
            case 2:
                // 第二名
                resultScore = 1 * gameMark;
                break;
            case 3:
                // 第三名
                resultScore = -1 * gameMark;
                break;
            case 4:
                // 第四名
                resultScore = -2 * gameMark;
                break;
            default:
                break;
            }

            // 在赢分玩家中直接从本局当前得分中扣除系统分
            double currentSystemScore = getCustomSystemScore(resultScore);
            if (resultScore > 0) {
            	resultScore -= currentSystemScore;
            }

            // 保存玩家得分信息
            PlayerScore playerScore = new PlayerScore();
            playerScore.setScoreId(UUID.randomUUID().toString());
            playerScore.setProfileId(playerProfile.getProfileId());
            playerScore.setGameId(gameRecord.getGameId());
            playerScore.setUserId(playerProfile.getUserId());
            playerScore.setCurrentNumber(player.getCurrentNumber());
            playerScore.setCurScore(resultScore); // 玩家当前得分
            playerScore.setSysScore(currentSystemScore); // 系统当前得分
            playerScore.setOrgScores(playerProfile.getCurrentScore()); // 玩家原始总积分
            playerScore.setCurScores(playerProfile.getCurrentScore() + resultScore); // 玩家当前总积分
            playerProfile.setCurrentScore(playerProfile.getCurrentScore() + resultScore); // 玩家当前总分数
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
     * 计算独牌时的游戏积分
     * 
     * @param itr
     * @param gameRecord
     * @param isFinalSettingPlayerWon
     */
    private void persistRushScore(Iterator<Player> itr, GameRecord gameRecord, boolean isFinalSettingPlayerWon) {
        // 独牌
        double gameMark = this.getGameMark();
        String playerIds = "";
        // 假设此局的大小为“X”，如果叫到“独牌”的玩家胜，那么叫牌者赢到3X+3X+3X，反之叫牌者输9X
        while (itr.hasNext()) {
            // 取得玩家信息
            Player player = itr.next();
            String playerId = player.getId();
            PlayerProfile playerProfile = (PlayerProfile) HibernateSessionFactory.getSession().createCriteria(PlayerProfile.class).add(Restrictions.eq(PlayerProfileDAO.USER_ID, playerId)).uniqueResult();
            playerIds += player.getCurrentNumber() + "~" + playerId + "~";
            double resultScore = 0;
            if (isFinalSettingPlayerWon) {
                // 独牌成功
                if (this.getSetting().getPlayerNumber().equals(player.getCurrentNumber())) {
                    // 为独牌玩家设置积分
                    resultScore = 1 * 3 * gameMark * this.getLowLevelMark();
                } else {
                    // 为其他玩家设置积分
                    resultScore = -1 * gameMark * this.getLowLevelMark();
                }
            } else {
                // 独牌失败
                if (!this.getSetting().getPlayerNumber().equals(player.getCurrentNumber())) {
                    // 为其他玩家设置积分
                    resultScore = 1 * gameMark * this.getLowLevelMark();
                } else {
                    // 为独牌玩家设置积分
                    resultScore = -1 * 3 * gameMark * this.getLowLevelMark();
                }
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
            // 保存内存模型玩家得分信息
            getPlayerNumberMap().get(player.getCurrentNumber()).setPlayerCurrentScore(playerProfile.getCurrentScore());
            getPlayerNumberMap().get(player.getCurrentNumber()).setCurrentScore(playerScore.getCurScore());
            getPlayerNumberMap().get(player.getCurrentNumber()).setSystemScore(playerScore.getSysScore());
        }
        gameRecord.setPlayers(playerIds);
    }

    /**
     * 计算天独时的游戏积分
     * 
     * @param itr
     * @param gameRecord
     * @param isFinalSettingPlayerWon
     */
    private void persistDeadlyRushScore(Iterator<Player> itr, GameRecord gameRecord, boolean isFinalSettingPlayerWon) {
        // 天独
        double gameMark = this.getGameMark();
        String playerIds = "";
        // 假设此局的大小为“X”，如果叫到“天独”的玩家胜，那么叫牌者赢到5X+5X+5X，反之叫牌者输15X
        while (itr.hasNext()) {
            // 取得玩家信息
            Player player = itr.next();
            String playerId = player.getId();
            PlayerProfile playerProfile = (PlayerProfile) HibernateSessionFactory.getSession().createCriteria(PlayerProfile.class).add(Restrictions.eq("userId", playerId)).uniqueResult();
            playerIds += player.getCurrentNumber() + "~" + playerId + "~";
            double resultScore = 0;
            if (isFinalSettingPlayerWon) {
                // 独牌成功
                if (this.getSetting().getPlayerNumber().equals(player.getCurrentNumber())) {
                    // 为独牌玩家设置积分
                    resultScore = 1 * 3 * gameMark * this.getMidLevelMark();
                } else {
                    // 为其他玩家设置积分
                    resultScore = -1 * gameMark * this.getMidLevelMark();
                }
            } else {
                // 独牌失败
                if (!this.getSetting().getPlayerNumber().equals(player.getCurrentNumber())) {
                    // 为其他玩家设置积分
                    resultScore = 1 * gameMark * this.getMidLevelMark();
                } else {
                    // 为独牌玩家设置积分
                    resultScore = -1 * 3 * gameMark * this.getMidLevelMark();
                }
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
            // 保存内存模型玩家得分信息
            getPlayerNumberMap().get(player.getCurrentNumber()).setPlayerCurrentScore(playerProfile.getCurrentScore());
            getPlayerNumberMap().get(player.getCurrentNumber()).setCurrentScore(playerScore.getCurScore());
            getPlayerNumberMap().get(player.getCurrentNumber()).setSystemScore(playerScore.getSysScore());
        }
        gameRecord.setPlayers(playerIds);
    }

    /**
     * 计算天外天时的游戏积分
     * 
     * @param itr
     * @param gameRecord
     * @param isFinalSettingPlayerWon
     */
    private void persistExtinctRushScore(Iterator<Player> itr, GameRecord gameRecord, boolean isFinalSettingPlayerWon) {
        // 天外天
        double gameMark = this.getGameMark();
        String playerIds = "";
        // 假设此局的大小为“X”，如果叫到“天独”的玩家胜，那么叫牌者赢到10X+10X+10X，反之叫牌者输50X
        while (itr.hasNext()) {
            // 取得玩家信息
            Player player = itr.next();
            String playerId = player.getId();
            PlayerProfile playerProfile = (PlayerProfile) HibernateSessionFactory.getSession().createCriteria(PlayerProfile.class).add(Restrictions.eq("userId", playerId)).uniqueResult();
            playerIds += player.getCurrentNumber() + "~" + playerId + "~";
            double resultScore = 0;
            if (isFinalSettingPlayerWon) {
                // 独牌成功
                if (this.getSetting().getPlayerNumber().equals(player.getCurrentNumber())) {
                    // 为独牌玩家设置积分
                    resultScore = 1 * 3 * gameMark * this.getHighLevelMark();
                } else {
                    // 为其他玩家设置积分
                    resultScore = -1 * gameMark * this.getHighLevelMark();
                }
            } else {
                // 独牌失败
                if (!this.getSetting().getPlayerNumber().equals(player.getCurrentNumber())) {
                    // 为其他玩家设置积分
                    resultScore = 1 * gameMark * this.getHighLevelMark();
                } else {
                    // 为独牌玩家设置积分
                    resultScore = -1 * 3 * gameMark * this.getHighLevelMark();
                }
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
            // 保存内存模型玩家得分信息
            getPlayerNumberMap().get(player.getCurrentNumber()).setPlayerCurrentScore(playerProfile.getCurrentScore());
            getPlayerNumberMap().get(player.getCurrentNumber()).setCurrentScore(playerScore.getCurScore());
            getPlayerNumberMap().get(player.getCurrentNumber()).setSystemScore(playerScore.getSysScore());
        }
        gameRecord.setPlayers(playerIds);
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
        gameRecord.setGameType(Red5Game.class.getSimpleName());
        if (this.getSetting() != null) {
            this.setSetting(Red5GameSetting.fromOrdinal((short)this.getSetting().ordinal()));
            gameRecord.setGameSetting((short)this.getSetting().ordinal());
        } else {
            this.setSetting(Red5GameSetting.NO_RUSH);
            gameRecord.setGameSetting((short)Red5GameSetting.NO_RUSH.ordinal());
        }
        gameRecord.setWinnerNumbers(this.getWinnerNumbers());
        gameRecord.setSystemScore(this.getGameMark());
        gameRecord.setStatus("DISCONNECTED");
        gameRecord.setRecord(this.getGameRecord());
        gameRecord.setCreateTime(this.getCreateTime());
        // 保存游戏历史记录
        HibernateSessionFactory.getSession().merge(gameRecord);
		// 根据当前游戏设置，取得惩罚标准，默认扣一倍
        double deductStandard = 1;
		if (this.getSetting().equals(Red5GameSetting.RUSH)) {
			// 独牌
			deductStandard = this.getLowLevelMark();
		} else if (this.getSetting().equals(Red5GameSetting.DEADLY_RUSH)) {
			// 天独
			deductStandard = this.getMidLevelMark();
		} else if (this.getSetting().equals(Red5GameSetting.EXTINCT_RUSH)) {
			// 天外天
			deductStandard = this.getHighLevelMark();
		}
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
}
