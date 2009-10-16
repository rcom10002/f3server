package info.knightrcom.data.metadata;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * RechargeRecord entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "recharge_record")
public class RechargeRecord extends info.knightrcom.data.SimplePojoImpl implements java.io.Serializable {

    // Fields

    /**
	 * 
	 */
	private static final long serialVersionUID = -6021040286898388999L;

	private String rechargeId;

    private String fromPlayer;

    private Double fromOrgScore;

    private Double fromCurScore;

    private Double score;

    private String toPlayer;

    private Double toOrgScore;

    private Double toCurScore;

    private String memo;

    private String status;

    private Date createTime;

    private String createBy;

    private Date updateTime;

    private String updateBy;

    // Constructors

    /** default constructor */
    public RechargeRecord() {
    }

    /** minimal constructor */
    public RechargeRecord(String rechargeId, String fromPlayer, Double fromOrgScore, Double fromCurScore, Double score, String toPlayer, Double toOrgScore, Double toCurScore) {
        this.rechargeId = rechargeId;
        this.fromPlayer = fromPlayer;
        this.fromOrgScore = fromOrgScore;
        this.fromCurScore = fromCurScore;
        this.score = score;
        this.toPlayer = toPlayer;
        this.toOrgScore = toOrgScore;
        this.toCurScore = toCurScore;
    }

    /** full constructor */
    public RechargeRecord(String rechargeId, String fromPlayer, Double fromOrgScore, Double fromCurScore, Double score, String toPlayer, Double toOrgScore, Double toCurScore, String memo, String status, Date createTime, String createBy, Date updateTime, String updateBy) {
        this.rechargeId = rechargeId;
        this.fromPlayer = fromPlayer;
        this.fromOrgScore = fromOrgScore;
        this.fromCurScore = fromCurScore;
        this.score = score;
        this.toPlayer = toPlayer;
        this.toOrgScore = toOrgScore;
        this.toCurScore = toCurScore;
        this.memo = memo;
        this.status = status;
        this.createTime = createTime;
        this.createBy = createBy;
        this.updateTime = updateTime;
        this.updateBy = updateBy;
    }

    // Property accessors
    @Id
    @Column(name = "RECHARGE_ID", unique = true, nullable = false, length = 100)
    public String getRechargeId() {
        return this.rechargeId;
    }

    public void setRechargeId(String rechargeId) {
        this.rechargeId = rechargeId;
    }

    @Column(name = "FROM_PLAYER", nullable = false, length = 100)
    public String getFromPlayer() {
        return this.fromPlayer;
    }

    public void setFromPlayer(String fromPlayer) {
        this.fromPlayer = fromPlayer;
    }

    @Column(name = "FROM_ORG_SCORE", nullable = false)
    public Double getFromOrgScore() {
        return this.fromOrgScore;
    }

    public void setFromOrgScore(Double fromOrgScore) {
        this.fromOrgScore = fromOrgScore;
    }

    @Column(name = "FROM_CUR_SCORE", nullable = false)
    public Double getFromCurScore() {
        return this.fromCurScore;
    }

    public void setFromCurScore(Double fromCurScore) {
        this.fromCurScore = fromCurScore;
    }

    
    @Column(name = "SCORE", nullable = false)
	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	@Column(name = "TO_PLAYER", nullable = false, length = 100)
    public String getToPlayer() {
        return this.toPlayer;
    }

    public void setToPlayer(String toPlayer) {
        this.toPlayer = toPlayer;
    }

    @Column(name = "TO_ORG_SCORE", nullable = false)
    public Double getToOrgScore() {
        return this.toOrgScore;
    }

    public void setToOrgScore(Double toOrgScore) {
        this.toOrgScore = toOrgScore;
    }

    @Column(name = "TO_CUR_SCORE", nullable = false)
    public Double getToCurScore() {
        return this.toCurScore;
    }

    public void setToCurScore(Double toCurScore) {
        this.toCurScore = toCurScore;
    }

    @Column(name = "MEMO", length = 1000)
    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
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
