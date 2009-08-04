package info.knightrcom.model.game;

import info.knightrcom.model.global.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class Game<T> {

    protected Log log = LogFactory.getLog(Game.class);

    /** 游戏玩家 */
    private List<Player> players = Collections.synchronizedList(new ArrayList<Player>());

    /** 游戏玩家与玩家编号映射关系 */
    private Map<String, Player> playerNumberMap = Collections.synchronizedMap(new HashMap<String, Player>());

    /** 游戏记录 */
    private StringBuilder gameRecord = new StringBuilder();

    /** 游戏ID */
    private final String id = UUID.randomUUID().toString();

    /** 获胜者名单 */
    private StringBuilder winnerNumbers = new StringBuilder();

    /** 当前房间每局游戏所需要的分数 */
    private int gameMark;
    
    /** 扑克类专用 【红五 独牌】或【斗地主 青龙】记分 */
    private int lowLevelMark;
    
    /** 扑克类专用 【红五 天独】或【斗地主 白虎】记分 */
    private int midLevelMark;
    
    /** 扑克类专用 【红五 天外天】或【斗地主 朱雀】记分 */
    private int highLevelMark;

    /** 游戏进行所需要的最少底分 */
    private int minGameStartMark;

    /** 游戏设置 */
    private T setting; 

    /** 自定义结构，用于表现游戏积分明细 */
    private String gameDetailScore;

    /**
     * @return
     */
    public T getSetting() {
        return this.setting;
    }

    /**
     * @param setting
     */
    public void setSetting(T setting) {
        this.setting = setting;
    }

    /**
     * @param player
     */
    public void involvePlayer(Player player) {
        players.add(player);
    }

    /**
     * @return the players
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * @return the gameRecord
     */
    public String getGameRecord() {
        return gameRecord.toString();
    }

    /**
     * @param gameRecord
     *            the gameRecord to set
     */
    public void appendGameRecord(String gameRecord) {
        this.gameRecord.append(gameRecord).append(";");
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param winner
     * @return
     */
    public String getWinnerNumbers() {
        return winnerNumbers.toString().replaceFirst("~$", "");
    }

    /**
     * @param winner
     * @param playerNumber
     */
    public void addWinnerNumber(String playerNumber) {
        if (winnerNumbers.indexOf(playerNumber) > -1) {
            throw new RuntimeException("该玩家已经在获胜者名单中！");
        }
        winnerNumbers.append(playerNumber).append("~");
    }

    /**
     * @return the gameMark
     */
    public int getGameMark() {
        return gameMark;
    }

    /**
     * @param gameMark the gameMark to set
     */
    public void setGameMark(int gameMark) {
        this.gameMark = gameMark;
    }

    /**
	 * @return the lowLevelMark
	 */
	public int getLowLevelMark() {
		return lowLevelMark;
	}

	/**
	 * @param lowLevelMark the lowLevelMark to set
	 */
	public void setLowLevelMark(int lowLevelMark) {
		this.lowLevelMark = lowLevelMark;
	}

	/**
	 * @return the midLevelMark
	 */
	public int getMidLevelMark() {
		return midLevelMark;
	}

	/**
	 * @param midLevelMark the midLevelMark to set
	 */
	public void setMidLevelMark(int midLevelMark) {
		this.midLevelMark = midLevelMark;
	}

	/**
	 * @return the highLevelMark
	 */
	public int getHighLevelMark() {
		return highLevelMark;
	}

	/**
	 * @param highLevelMark the highLevelMark to set
	 */
	public void setHighLevelMark(int highLevelMark) {
		this.highLevelMark = highLevelMark;
	}

	/**
     * @return the minGameStartMark
     */
    public int getMinGameStartMark() {
        return minGameStartMark;
    }

    /**
     * @param minGameStartMark the minGameStartMark to set
     */
    public void setMinGameStartMark(int minGameStartMark) {
        this.minGameStartMark = minGameStartMark;
    }

    /**
     * @return the playerNumberMap
     */
    public Map<String, Player> getPlayerNumberMap() {
        return playerNumberMap;
    }

    /**
     * 计算游戏积分并保存到数据库中
     */
    public abstract void persistScore();

    /**
     * 为客户端提供游戏积分明细
     * 
     * @return the gameResultScore
     */
    public String getGameDetailScore() {
        if (gameDetailScore == null) {
            gameDetailScore = "";
            Iterator<String> keyItr  = playerNumberMap.keySet().iterator();
            while (keyItr.hasNext()) {
                String key = keyItr.next();
                int currentScore = playerNumberMap.get(key).getCurrentScore();
                int systemScore = playerNumberMap.get(key).getSystemScore();
                gameDetailScore += String.format("%1$s,%2$s,%3$s;", key, currentScore, systemScore);
            }
            gameDetailScore = gameDetailScore.replaceFirst(";$", "");
        }
        return gameDetailScore;
    }
}
