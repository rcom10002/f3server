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

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

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
    public static final Red5Poker START_POKER = new Red5Poker(PokerColor.HEART, PokerValue.V5);

    public Red5Game() {
    }

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
        gameRecord.setGameType(Red5Game.class.getSimpleName());
        gameRecord.setGameSetting((short)this.getSetting().ordinal());
        gameRecord.setWinnerNumbers(this.getWinnerNumbers());
        // TODO DROP THIS LINE => gameRecord.setScore(score);
        // THE FOLLOWING LINES WERE SET IN THE LAST LINE OF EACH PRIVATE METHOD 
        // gameRecord.setPlayers();
        // gameRecord.setSystemScore(systemScore);
        gameRecord.setRecord(this.getGameRecord());
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
     * 计算非独牌时的游戏积分
     * 
     * @param itr
     * @param gameRecord
     */
    private void persistNoRushScore(Iterator<Player> itr, GameRecord gameRecord) {
        // 不独
        int gameMark = this.getGameMark();
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
            int resultScore = 0;
            int systemScore = 0;
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
            // 保存玩家得分信息
            PlayerScore playerScore = new PlayerScore();
            playerScore.setScoreId(UUID.randomUUID().toString());
            playerScore.setProfileId(playerProfile.getProfileId());
            playerScore.setGameId(gameRecord.getGameId());
            playerScore.setUserId(playerProfile.getUserId());
            playerScore.setCurrentNumber(player.getCurrentNumber());
            playerScore.setScore(resultScore); // 玩家当前得分
            playerScore.setSystemScore(systemScore);
            playerProfile.setCurrentScore(resultScore + playerProfile.getCurrentScore().intValue()); // 玩家总分数
            HibernateSessionFactory.getSession().merge(playerProfile);
            HibernateSessionFactory.getSession().merge(playerScore);
            // 保存内存模型玩家得分信息
            getPlayerNumberMap().get(player.getCurrentNumber()).setCurrentScore(resultScore);
            getPlayerNumberMap().get(player.getCurrentNumber()).setSystemScore(systemScore);
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
        int gameMark = this.getGameMark();
        String playerIds = "";
        // 假设此局的大小为“X”，如果叫到“独牌”的玩家胜，那么叫牌者赢到3X+3X+3X，反之叫牌者输9X
        while (itr.hasNext()) {
            // 取得玩家信息
            Player player = itr.next();
            String playerId = player.getId();
            PlayerProfile playerProfile = (PlayerProfile) HibernateSessionFactory.getSession().createCriteria(PlayerProfile.class).add(Restrictions.eq(PlayerProfileDAO.USER_ID, playerId)).uniqueResult();
            playerIds += player.getCurrentNumber() + "~" + playerId + "~";
            int resultScore = 0;
            int systemScore = 0;
            if (isFinalSettingPlayerWon) {
                // 独牌成功
                if (getSetting().getPlayerNumber().equals(player.getCurrentNumber())) {
                    // 为独牌玩家设置积分
                    resultScore = 1 * 3 * gameMark * this.getLowLevelMark();
                    playerProfile.setCurrentScore(playerProfile.getCurrentScore().intValue() + resultScore);
                } else {
                    // 为其他玩家设置积分
                    resultScore = -1 * gameMark * this.getLowLevelMark();
                    playerProfile.setCurrentScore(playerProfile.getCurrentScore().intValue() + resultScore);
                }
            } else {
                // 独牌失败
                if (!this.getSetting().getPlayerNumber().equals(player.getCurrentNumber())) {
                    // 为其他玩家设置积分
                    resultScore = 1 * gameMark * this.getLowLevelMark();
                    playerProfile.setCurrentScore(playerProfile.getCurrentScore().intValue() + resultScore);
                } else {
                    // 为独牌玩家设置积分
                    resultScore = -1 * 3 * gameMark * this.getLowLevelMark();
                    playerProfile.setCurrentScore(playerProfile.getCurrentScore().intValue() + resultScore);
                }
            }
            // 保存玩家得分信息
            PlayerScore playerScore = new PlayerScore();
            playerScore.setScoreId(UUID.randomUUID().toString());
            playerScore.setProfileId(playerProfile.getProfileId());
            playerScore.setGameId(gameRecord.getGameId());
            playerScore.setUserId(playerProfile.getUserId());
            playerScore.setCurrentNumber(player.getCurrentNumber());
            playerScore.setScore(resultScore); // 玩家当前得分
            playerScore.setSystemScore(systemScore);
            playerProfile.setCurrentScore(resultScore + playerProfile.getCurrentScore().intValue()); // 玩家总得分
            HibernateSessionFactory.getSession().merge(playerProfile);
            HibernateSessionFactory.getSession().merge(playerScore);
            // 保存内存模型玩家得分信息
            getPlayerNumberMap().get(player.getCurrentNumber()).setCurrentScore(resultScore);
            getPlayerNumberMap().get(player.getCurrentNumber()).setSystemScore(systemScore);
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
        int gameMark = this.getGameMark();
        String playerIds = "";
        // 假设此局的大小为“X”，如果叫到“天独”的玩家胜，那么叫牌者赢到5X+5X+5X，反之叫牌者输15X
        while (itr.hasNext()) {
            Player player = itr.next();
            String playerId = player.getId();
            PlayerProfile playerProfile = (PlayerProfile) HibernateSessionFactory.getSession().createCriteria(PlayerProfile.class).add(Restrictions.eq("userId", playerId)).uniqueResult();
            playerIds += player.getCurrentNumber() + "~" + playerId + "~";
            int resultScore = 0;
            int systemScore = 0;
            if (isFinalSettingPlayerWon) {
                // 独牌成功
                if (this.getSetting().getPlayerNumber().equals(player.getCurrentNumber())) {
                    // 为独牌玩家设置积分
                    resultScore =  1 * 3 * gameMark * this.getMidLevelMark();
                    playerProfile.setCurrentScore(playerProfile.getCurrentScore().intValue() + resultScore);
                } else {
                    // 为其他玩家设置积分
                    resultScore =  -1 * gameMark * this.getMidLevelMark();
                    playerProfile.setCurrentScore(playerProfile.getCurrentScore().intValue() + resultScore);
                }
            } else {
                // 独牌失败
                if (!this.getSetting().getPlayerNumber().equals(player.getCurrentNumber())) {
                    // 为其他玩家设置积分
                    resultScore = 1 * gameMark * this.getMidLevelMark();
                    playerProfile.setCurrentScore(playerProfile.getCurrentScore().intValue() + resultScore);
                } else {
                    // 为独牌玩家设置积分
                    resultScore =  -1 * 3 * gameMark * this.getMidLevelMark();
                    playerProfile.setCurrentScore(playerProfile.getCurrentScore().intValue() + resultScore);
                }
            }
            // 保存玩家得分信息
            PlayerScore playerScore = new PlayerScore();
            playerScore.setScoreId(UUID.randomUUID().toString());
            playerScore.setProfileId(playerProfile.getProfileId());
            playerScore.setGameId(gameRecord.getGameId());
            playerScore.setUserId(playerProfile.getUserId());
            playerScore.setCurrentNumber(player.getCurrentNumber());
            playerScore.setScore(resultScore); // 玩家当前得分
            playerScore.setSystemScore(systemScore);
            playerProfile.setCurrentScore(resultScore + playerProfile.getCurrentScore().intValue()); // 玩家总得分
            HibernateSessionFactory.getSession().merge(playerProfile);
            HibernateSessionFactory.getSession().merge(playerScore);
            // 保存内存模型玩家得分信息
            getPlayerNumberMap().get(player.getCurrentNumber()).setCurrentScore(resultScore);
            getPlayerNumberMap().get(player.getCurrentNumber()).setSystemScore(systemScore);
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
        int gameMark = this.getGameMark() * this.getHighLevelMark();
        String playerIds = "";
        // 假设此局的大小为“X”，如果叫到“天独”的玩家胜，那么叫牌者赢到10X+10X+10X，反之叫牌者输50X
        while (itr.hasNext()) {
            Player player = itr.next();
            String playerId = player.getId();
            PlayerProfile playerProfile = (PlayerProfile) HibernateSessionFactory.getSession().createCriteria(PlayerProfile.class).add(Restrictions.eq("userId", playerId)).uniqueResult();
            playerIds += player.getCurrentNumber() + "~" + playerId + "~";
            int resultScore = 0;
            int systemScore = 0;
            if (isFinalSettingPlayerWon) {
                // 独牌成功
                if (this.getSetting().getPlayerNumber().equals(player.getCurrentNumber())) {
                    // 为独牌玩家设置积分
                    resultScore = 1 * 3 * gameMark * this.getMidLevelMark();
                    playerProfile.setCurrentScore(playerProfile.getCurrentScore().intValue() + resultScore);
                } else {
                    // 为其他玩家设置积分
                    resultScore = -1 * gameMark * this.getMidLevelMark();
                    playerProfile.setCurrentScore(playerProfile.getCurrentScore().intValue() + resultScore);
                }
            } else {
                // 独牌失败
                if (!this.getSetting().getPlayerNumber().equals(player.getCurrentNumber())) {
                    // 为其他玩家设置积分
                    resultScore = 1 * gameMark * this.getMidLevelMark();
                    playerProfile.setCurrentScore(playerProfile.getCurrentScore().intValue() + resultScore);
                } else {
                    // 为独牌玩家设置积分
                    resultScore = -1 * 3 * gameMark * this.getMidLevelMark();
                    playerProfile.setCurrentScore(playerProfile.getCurrentScore().intValue() + resultScore);
                }
            }
            // 保存玩家得分信息
            PlayerScore playerScore = new PlayerScore();
            playerScore.setScoreId(UUID.randomUUID().toString());
            playerScore.setProfileId(playerProfile.getProfileId());
            playerScore.setGameId(gameRecord.getGameId());
            playerScore.setUserId(playerProfile.getUserId());
            playerScore.setCurrentNumber(player.getCurrentNumber());
            playerScore.setScore(resultScore); // 玩家当前得分
            playerScore.setSystemScore(systemScore);
            playerProfile.setCurrentScore(resultScore + playerProfile.getCurrentScore().intValue()); // 玩家总得分
            HibernateSessionFactory.getSession().merge(playerProfile);
            HibernateSessionFactory.getSession().merge(playerScore);
            // 保存内存模型玩家得分信息
            getPlayerNumberMap().get(player.getCurrentNumber()).setCurrentScore(resultScore);
            getPlayerNumberMap().get(player.getCurrentNumber()).setSystemScore(systemScore);
        }
        gameRecord.setPlayers(playerIds);
    }
}
