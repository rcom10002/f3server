package info.knightrcom.model.global;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 游戏房间
 */
public class Room extends AbstractModel<Lobby, Player> {

    private Map<GameStatus, Integer> gameStatusNumber = Collections.synchronizedMap(new HashMap<GameStatus, Integer>());

    /** 扑克类专用 【红五 独牌】或【斗地主 青龙】记分 */
    private String lowLevelMark;
    
    /** 扑克类专用 【红五 天独】或【斗地主 白虎】记分 */
    private String midLevelMark;
    
    /** 扑克类专用 【红五 天外天】或【斗地主 朱雀】记分 */
    private String highLevelMark;
    
    /** 每番记分 */
    private String pointMark;

    /** 当前房间每局游戏所需要的分数 */
    private String roundMark;

    /** 游戏进行所需要的最少底分 */
    private String minMarks;

	/**
     * @return
     */
    public Lobby getCurrentLobby() {
        return this.parent;
    }

    /**
     * @return
     */
    public Platform getCurrentPlatform() {
        return this.parent.getParent();
    }

    /**
     * @param player
     */
    synchronized void updateGameStatusNumber(Player player) {
        GameStatus orgStatus = player.getOrgStatus();
        GameStatus newStatus = player.getCurrentStatus();
        int orgValue = gameStatusNumber.get(orgStatus) - 1;
        int newValue = gameStatusNumber.get(newStatus) == null ? 1 : gameStatusNumber.get(newStatus) + 1;
        gameStatusNumber.put(orgStatus, orgValue);
        gameStatusNumber.put(newStatus, newValue);
    }

    @Override
    public synchronized void addChild(String key, Player value) {
        if (this.childContainer.get(key) == null) {
            int currentValue = gameStatusNumber.get(value.getCurrentStatus()) == null ? 
                    1 : gameStatusNumber.get(value.getCurrentStatus()) + 1;
            gameStatusNumber.put(value.getCurrentStatus(), currentValue);
        } else {
            throw new RuntimeException("The child you wanna add is already existing!");
        }
        super.addChild(key, value);
    }

    @Override
    public synchronized void removeChild(String key) {
        Player player = this.childContainer.get(key);
        if (player == null) {
            throw new RuntimeException("The child you wanna remove is not existing!");
        }
        int currentValue = gameStatusNumber.get(player.getCurrentStatus()) - 1;
        gameStatusNumber.put(player.getCurrentStatus(), currentValue);
        super.removeChild(key);
    }

    /**
     * @param status
     * @return
     */
    public synchronized int getGameStatusNumber(GameStatus status) {
        Integer statusNumber = gameStatusNumber.get(status);
        return statusNumber == null ? 0 : statusNumber.intValue();
    }

    /**
	 * @return the pointMark
	 */
	public int getGamePointMark() {
		return new Integer(pointMark).intValue();
	}

	/**
	 * @param pointMark the pointMark to set
	 */
	public void setPointMark(String pointMark) {
		this.pointMark = pointMark;
	}

    /**
     * @param roundMark the roundMark to set
     */
    public void setRoundMark(String roundMark) {
        this.roundMark = roundMark;
    }

    /**
     * @return the roundMark
     */
    public int getGameMark() {
        return new Integer(roundMark).intValue();
    }

    /**
     * @param minMarks the minMarks to set
     */
    public void setMinMarks(String minMarks) {
        this.minMarks = minMarks;
    }

    /**
     * @return the minMarks
     */
    public int getMinGameMarks() {
        return new Integer(minMarks).intValue();
    }

	/**
	 * @return the lowLevelMark
	 */
	public int getGameLowLevelMark() {
		return new Integer(lowLevelMark).intValue();
	}

	/**
	 * @param lowLevelMark the lowLevelMark to set
	 */
	public void setLowLevelMark(String lowLevelMark) {
		this.lowLevelMark = lowLevelMark;
	}

	/**
	 * @return the midLevelMark
	 */
	public int getGameMidLevelMark() {
		return new Integer(midLevelMark).intValue();
	}

	/**
	 * @param midLevelMark the midLevelMark to set
	 */
	public void setMidLevelMark(String midLevelMark) {
		this.midLevelMark = midLevelMark;
	}

	/**
	 * @return the highLevelMark
	 */
	public int getGameHighLevelMark() {
		return new Integer(highLevelMark).intValue();
	}

	/**
	 * @param highLevelMark the highLevelMark to set
	 */
	public void setHighLevelMark(String highLevelMark) {
		this.highLevelMark = highLevelMark;
	}

}
