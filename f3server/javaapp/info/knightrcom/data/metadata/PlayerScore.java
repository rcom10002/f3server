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
@Table(name = "player_score", catalog = "f3s")
public class PlayerScore extends info.knightrcom.data.SimplePojoImpl implements java.io.Serializable {

    // Fields

    private String scoreId;

    private String profileId;

    private String gameId;

    private String userId;

    private String currentNumber;

    private Integer score;

    private Integer systemScore;

    private Short status;

    private Date createTime;

    private String createBy;

    private Date updateTime;

    private String updateBy;

    // Constructors

    /** default constructor */
    public PlayerScore() {
    }

    /** minimal constructor */
    public PlayerScore(String scoreId) {
        this.scoreId = scoreId;
    }

    /** full constructor */
    public PlayerScore(String scoreId, String profileId, String gameId, String userId, String currentNumber, Integer score, Integer systemScore, Short status, Date createTime, String createBy, Date updateTime, String updateBy) {
        this.scoreId = scoreId;
        this.profileId = profileId;
        this.gameId = gameId;
        this.userId = userId;
        this.currentNumber = currentNumber;
        this.score = score;
        this.systemScore = systemScore;
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

    @Column(name = "PROFILE_ID", length = 100)
    public String getProfileId() {
        return this.profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    @Column(name = "GAME_ID", length = 100)
    public String getGameId() {
        return this.gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    @Column(name = "USER_ID", length = 100)
    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name = "CURRENT_NUMBER", length = 1)
    public String getCurrentNumber() {
        return this.currentNumber;
    }

    public void setCurrentNumber(String currentNumber) {
        this.currentNumber = currentNumber;
    }

    @Column(name = "SCORE")
    public Integer getScore() {
        return this.score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    @Column(name = "SYSTEM_SCORE")
    public Integer getSystemScore() {
        return this.systemScore;
    }

    public void setSystemScore(Integer systemScore) {
        this.systemScore = systemScore;
    }

    @Column(name = "STATUS")
    public Short getStatus() {
        return this.status;
    }

    public void setStatus(Short status) {
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
