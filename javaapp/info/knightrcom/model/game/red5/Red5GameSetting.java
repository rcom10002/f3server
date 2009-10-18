package info.knightrcom.model.game.red5;


/**
 * 游戏开局设置
 */
public enum Red5GameSetting {

    /** 不独 */
    NO_RUSH("不独"),

    /** 独牌 */
    RUSH("独牌"),

    /** 天独 */
    DEADLY_RUSH("天独"),

    /** 天外天 */
    EXTINCT_RUSH("天外天");

    private String playerNumber;

    private String displayName;

    Red5GameSetting(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return the playerNumber
     */
    public String getPlayerNumber() {
        return playerNumber;
    }

    /**
     * @param playerNumber
     *            the playerNumber to set
     */
    public void setPlayerNumber(String playerNumber) {
        this.playerNumber = playerNumber;
    }

    /**
     * @return
     */
    public String getDisplayName() {
        return this.displayName;
    }
    
    /**
     * 按照指定的索引号来取得游戏设置对象
     * 
     * @param ordinal
     * @return
     */
    public static Red5GameSetting fromOrdinal(int ordinal) {
        if (ordinal == NO_RUSH.ordinal()) {
            return NO_RUSH;
        } else if (ordinal == RUSH.ordinal()) {
            return RUSH;
        } else if (ordinal == DEADLY_RUSH.ordinal()) {
            return DEADLY_RUSH;
        } else if (ordinal == EXTINCT_RUSH.ordinal()) {
            return EXTINCT_RUSH;
        }
        throw new RuntimeException("游戏设置出错！");
    }
}
