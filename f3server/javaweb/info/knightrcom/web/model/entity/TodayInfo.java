package info.knightrcom.web.model.entity;

public class TodayInfo {

    private String winandlose;

    private int score;

    private int systemscore;

    private String gameId;

    private String createTime;

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
    public int getScore() {
        return score;
    }

    /**
     * @param score the score to set
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * @return the systemscore
     */
    public int getSystemscore() {
        return systemscore;
    }

    /**
     * @param systemscore the systemscore to set
     */
    public void setSystemscore(int systemscore) {
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

}
