package info.knightrcom.data.metadata;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * RechargeHistory entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "recharge_history", catalog = "f3s")
public class RechargeHistory extends info.knightrcom.data.SimplePojoImpl implements java.io.Serializable {

    // Fields

    private String rechargeId;

    private String profileId;

    private String operator;

    private Integer score;

    private Date bizDate;

    private Date createTime;

    private String createBy;

    private Date updateTime;

    private String updateBy;

    // Constructors

    /** default constructor */
    public RechargeHistory() {
    }

    /** minimal constructor */
    public RechargeHistory(String rechargeId) {
        this.rechargeId = rechargeId;
    }

    /** full constructor */
    public RechargeHistory(String rechargeId, String profileId, String operator, Integer score, Date bizDate, Date createTime, String createBy, Date updateTime, String updateBy) {
        this.rechargeId = rechargeId;
        this.profileId = profileId;
        this.operator = operator;
        this.score = score;
        this.bizDate = bizDate;
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

    @Column(name = "PROFILE_ID", length = 100)
    public String getProfileId() {
        return this.profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    @Column(name = "OPERATOR", length = 100)
    public String getOperator() {
        return this.operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Column(name = "SCORE")
    public Integer getScore() {
        return this.score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "BIZ_DATE", length = 10)
    public Date getBizDate() {
        return this.bizDate;
    }

    public void setBizDate(Date bizDate) {
        this.bizDate = bizDate;
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
