package info.knightrcom.model.game.fightlandlord;


/**
 * 游戏开局设置
 */
public enum FightLandlordGameSetting {

    /** 不叫 */
    NO_RUSH("不叫"),

    /** 1分 */
    ONE_RUSH("1分"),

    /** 2分 */
    TWO_RUSH("2分"),

    /** 3分 */
    THREE_RUSH("3分");

    private String playerNumber;

    private String displayName;
    
    FightLandlordGameSetting(String displayName) {
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
    public static FightLandlordGameSetting fromOrdinal(int ordinal) {
        if (ordinal == NO_RUSH.ordinal()) {
            return NO_RUSH;
        } else if (ordinal == ONE_RUSH.ordinal()) {
            return ONE_RUSH;
        } else if (ordinal == TWO_RUSH.ordinal()) {
            return TWO_RUSH;
        } else if (ordinal == THREE_RUSH.ordinal()) {
            return THREE_RUSH;
        }
        throw new RuntimeException("游戏设置出错！");
    }

}
