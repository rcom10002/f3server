package info.knightrcom.data.metadata;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * GameFeedback entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "game_feedback", catalog = "f3s")
public class GameFeedback extends info.knightrcom.data.SimplePojoImpl implements java.io.Serializable {

    // Fields

    private String feedbackId;

    private String gameId;

    private String number;

    private String cheatDesc;

    private Short status;

    private Date createTime;

    private String createBy;

    private Date updateTime;

    private String updateBy;

    // Constructors

    /** default constructor */
    public GameFeedback() {
    }

    /** minimal constructor */
    public GameFeedback(String feedbackId) {
        this.feedbackId = feedbackId;
    }

    /** full constructor */
    public GameFeedback(String feedbackId, String gameId, String number, String cheatDesc, Short status, Date createTime, String createBy, Date updateTime, String updateBy) {
        this.feedbackId = feedbackId;
        this.gameId = gameId;
        this.number = number;
        this.cheatDesc = cheatDesc;
        this.status = status;
        this.createTime = createTime;
        this.createBy = createBy;
        this.updateTime = updateTime;
        this.updateBy = updateBy;
    }

    // Property accessors
    @Id
    @Column(name = "FEEDBACK_ID", unique = true, nullable = false, length = 100)
    public String getFeedbackId() {
        return this.feedbackId;
    }

    public void setFeedbackId(String feedbackId) {
        this.feedbackId = feedbackId;
    }

    @Column(name = "GAME_ID", length = 100)
    public String getGameId() {
        return this.gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    @Column(name = "NUMBER", length = 100)
    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Column(name = "CHEAT_DESC", length = 2000)
    public String getCheatDesc() {
        return this.cheatDesc;
    }

    public void setCheatDesc(String cheatDesc) {
        this.cheatDesc = cheatDesc;
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
