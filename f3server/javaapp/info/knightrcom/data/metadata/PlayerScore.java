package info.knightrcom.data.metadata;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * PlayerScore entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "player_score")
public class PlayerScore extends info.knightrcom.data.SimplePojoImpl implements java.io.Serializable {

    // Fields

    /**
	 * 
	 */
	private static final long serialVersionUID = 8938630300025162857L;

	private String scoreId;

    private String profileId;

    private String gameId;

    private String userId;

    private String currentNumber;

    private Integer curScore;

    private Integer sysScore;

    private Integer orgScores;

    private Integer curScores;

    private String status;

    private Date createTime;

    private String createBy;

    private Date updateTime;

    private String updateBy;

    // Constructors

    /** default constructor */
    public PlayerScore() {
    }

    /** minimal constructor */
    public PlayerScore(String scoreId, String profileId, String gameId, String userId, String currentNumber, Integer curScore, Integer sysScore, Integer orgScores, Integer curScores) {
        this.scoreId = scoreId;
        this.profileId = profileId;
        this.gameId = gameId;
        this.userId = userId;
        this.currentNumber = currentNumber;
        this.curScore = curScore;
        this.sysScore = sysScore;
        this.orgScores = orgScores;
        this.curScores = curScores;
    }

    /** full constructor */
    public PlayerScore(String scoreId, String profileId, String gameId, String userId, String currentNumber, Integer curScore, Integer sysScore, Integer orgScores, Integer curScores, String status, Date createTime, String createBy, Date updateTime, String updateBy) {
        this.scoreId = scoreId;
        this.profileId = profileId;
        this.gameId = gameId;
        this.userId = userId;
        this.currentNumber = currentNumber;
        this.curScore = curScore;
        this.sysScore = sysScore;
        this.orgScores = orgScores;
        this.curScores = curScores;
        this.status = status;
        this.createTime = createTime;
        this.createBy = createBy;
        this.updateTime = updateTime;
        this.updateBy = updateBy;
    }

    // Property accessors
    @Id
    @Column(name = "SCORE_ID", unique = true, nullable = false, length = 100)
    public String getScoreId() {
        return this.scoreId;
    }

    public void setScoreId(String scoreId) {
        this.scoreId = scoreId;
    }

    @Column(name = "PROFILE_ID", nullable = false, length = 100)
    public String getProfileId() {
        return this.profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    @Column(name = "GAME_ID", nullable = false, length = 100)
    public String getGameId() {
        return this.gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    @Column(name = "USER_ID", nullable = false, length = 100)
    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name = "CURRENT_NUMBER", nullable = false, length = 1)
    public String getCurrentNumber() {
        return this.currentNumber;
    }

    public void setCurrentNumber(String currentNumber) {
        this.currentNumber = currentNumber;
    }

    @Column(name = "CUR_SCORE", nullable = false)
    public Integer getCurScore() {
        return this.curScore;
    }

    public void setCurScore(Integer curScore) {
        this.curScore = curScore;
    }

    @Column(name = "SYS_SCORE", nullable = false)
    public Integer getSysScore() {
        return this.sysScore;
    }

    public void setSysScore(Integer sysScore) {
        this.sysScore = sysScore;
    }

    @Column(name = "ORG_SCORES", nullable = false)
    public Integer getOrgScores() {
        return this.orgScores;
    }

    public void setOrgScores(Integer orgScores) {
        this.orgScores = orgScores;
    }

    @Column(name = "CUR_SCORES", nullable = false)
    public Integer getCurScores() {
        return this.curScores;
    }

    public void setCurScores(Integer curScores) {
        this.curScores = curScores;
    }

    @Column(name = "STATUS", length = 100)
    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_TIME", length = 19)
    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(name = "CREATE_BY", length = 100)
    public String getCreateBy() {
        return this.createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATE_TIME", length = 19)
    public Date getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Column(name = "UPDATE_BY", length = 100)
    public String getUpdateBy() {
        return this.updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

}
