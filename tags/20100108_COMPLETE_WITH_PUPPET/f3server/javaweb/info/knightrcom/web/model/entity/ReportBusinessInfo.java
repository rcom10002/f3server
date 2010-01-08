package info.knightrcom.web.model.entity;

public class ReportBusinessInfo {

	private String userId;
	
    private String winandlose;

    private String score;

    private String systemscore;

    private String gameId;
    
    private String gameType;

    private String createTime;
    
    private String startTime;
    
    private String endTime;
    

    /**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
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
	 * @return the gameType
	 */
	public String getGameType() {
		return gameType;
	}

	/**
	 * @param gameType the gameType to set
	 */
	public void setGameType(String gameType) {
		this.gameType = gameType;
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

	/**
	 * @return the startTime
	 */
	public String getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the endTime
	 */
	public String getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

}
