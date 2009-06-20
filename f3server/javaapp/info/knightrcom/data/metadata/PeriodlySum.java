package info.knightrcom.data.metadata;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * PeriodlySum entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "periodly_sum", catalog = "f3s")
public class PeriodlySum extends info.knightrcom.data.SimplePojoImpl implements java.io.Serializable {

    // Fields

    private String periodlyId;

    private String profileId;

    private String number;

    private Date startDate;

    private Date endDate;

    private Integer score;

    private Integer systemScore;

    private Short status;

    private Date createTime;

    private String createBy;

    private Date updateTime;

    private String updateBy;

    // Constructors

    /** default constructor */
    public PeriodlySum() {
    }

    /** minimal constructor */
    public PeriodlySum(String periodlyId) {
        this.periodlyId = periodlyId;
    }

    /** full constructor */
    public PeriodlySum(String periodlyId, String profileId, String number, Date startDate, Date endDate, Integer score, Integer systemScore, Short status, Date createTime, String createBy, Date updateTime, String updateBy) {
        this.periodlyId = periodlyId;
        this.profileId = profileId;
        this.number = number;
        this.startDate = startDate;
        this.endDate = endDate;
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
    @Column(name = "PERIODLY_ID", unique = true, nullable = false, length = 100)
    public String getPeriodlyId() {
        return this.periodlyId;
    }

    public void setPeriodlyId(String periodlyId) {
        this.periodlyId = periodlyId;
    }

    @Column(name = "PROFILE_ID", length = 100)
    public String getProfileId() {
        return this.profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    @Column(name = "NUMBER", length = 100)
    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE", length = 10)
    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE", length = 10)
    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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
