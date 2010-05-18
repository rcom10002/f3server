package info.knightrcom.web.model.entity;

public class TodayInfo {

    private String gametype;
    
    private String winandlose;

    private String score;

    private String systemscore;

    private String gameId;

    private String createTime;
    
    private String memo;

    /**
	 * @return the gametype
	 */
	public String getGametype() {
		return gametype;
	}

	/**
	 * @param gametype the gametype to set
	 */
	public void setGametype(String gametype) {
		this.gametype = gametype;
	}

	/**
     * @return the winandlose
     */
    public String getWinandlose() {
        return winandlose;
    }

    /**
     * @param winandlose
     *            the winandlose to set
     */
    public void setWinandlose(String winandlose) {
        this.winandlose = winandlose;
    }

    /**
     * @return the score
     */
    public String getScore() {
        return score;
    }

    /**
     * @param score the score to set
     */
    public void setScore(String score) {
        this.score = score;
    }

    /**
     * @return the systemscore
     */
    public String getSystemscore() {
        return systemscore;
    }

    /**
     * @param systemscore the systemscore to set
     */
    public void setSystemscore(String systemscore) {
        this.systemscore = systemscore;
    }

    /**
     * @return the gameId
     */
    public String getGameId() {
        return gameId;
    }

    /**
     * @param gameId the gameId to set
     */
    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    /**
     * @return the createTime
     */
    public String getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime the createTime to set
     */
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

}
