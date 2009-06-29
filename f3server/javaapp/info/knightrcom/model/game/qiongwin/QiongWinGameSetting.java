package info.knightrcom.model.game.qiongwin;

import info.knightrcom.model.game.pushdownwin.PushdownWinGameSetting;

public enum QiongWinGameSetting {

	/** 点炮 */
	NARROW_VICTORY("点炮"),
	/** 自摸 */
	CLEAR_VICTORY("自摸"); 

    private String playerNumber;

    private String displayName;

    QiongWinGameSetting(String displayName) {
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
    public static QiongWinGameSetting fromOrdinal(int ordinal) {
        if (ordinal == NARROW_VICTORY.ordinal()) {
            return NARROW_VICTORY;
        } else if (ordinal == CLEAR_VICTORY.ordinal()) {
            return CLEAR_VICTORY;
        }
        throw new RuntimeException("游戏设置出错！");
    }
}
