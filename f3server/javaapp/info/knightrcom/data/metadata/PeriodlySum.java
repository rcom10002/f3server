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

    private String title;

    private Date startDate;

    private Date endDate;

    private Integer winTimes;

    private Integer winScores;

    private Integer loseTimes;

    private Integer loseScores;

    private Integer drawTimes;

    private Integer drawScores;

    private Integer totalTimes;

    private Integer totalScores;

    private Integer totalSystemScore;

    private String status;

    private Date createTime;

    private String createBy;

    private Date updateTime;

    private String updateBy;

    // Constructors

    /** default constructor */
    public PeriodlySum() {
    }

    /** minimal constructor */
    public PeriodlySum(String periodlyId, String title, Date startDate, Date endDate, Integer winTimes, Integer winScores, Integer loseTimes, Integer loseScores, Integer drawTimes, Integer drawScores, Integer totalTimes, Integer totalScores, Integer totalSystemScore) {
        this.periodlyId = periodlyId;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.winTimes = winTimes;
        this.winScores = winScores;
        this.loseTimes = loseTimes;
        this.loseScores = loseScores;
        this.drawTimes = drawTimes;
        this.drawScores = drawScores;
        this.totalTimes = totalTimes;
        this.totalScores = totalScores;
        this.totalSystemScore = totalSystemScore;
    }

    /** full constructor */
    public PeriodlySum(String periodlyId, String profileId, String number, String title, Date startDate, Date endDate, Integer winTimes, Integer winScores, Integer loseTimes, Integer loseScores, Integer drawTimes, Integer drawScores, Integer totalTimes, Integer totalScores, Integer totalSystemScore, String status, Date createTime, String createBy, Date updateTime, String updateBy) {
        this.periodlyId = periodlyId;
        this.profileId = profileId;
        this.number = number;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.winTimes = winTimes;
        this.winScores = winScores;
        this.loseTimes = loseTimes;
        this.loseScores = loseScores;
        this.drawTimes = drawTimes;
        this.drawScores = drawScores;
        this.totalTimes = totalTimes;
        this.totalScores = totalScores;
        this.totalSystemScore = totalSystemScore;
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

    @Column(name = "TITLE", nullable = false, length = 100)
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE", nullable = false, length = 10)
    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE", nullable = false, length = 10)
    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Column(name = "WIN_TIMES", nullable = false)
    public Integer getWinTimes() {
        return this.winTimes;
    }

    public void setWinTimes(Integer winTimes) {
        this.winTimes = winTimes;
    }

    @Column(name = "WIN_SCORES", nullable = false)
    public Integer getWinScores() {
        return this.winScores;
    }

    public void setWinScores(Integer winScores) {
        this.winScores = winScores;
    }

    @Column(name = "LOSE_TIMES", nullable = false)
    public Integer getLoseTimes() {
        return this.loseTimes;
    }

    public void setLoseTimes(Integer loseTimes) {
        this.loseTimes = loseTimes;
    }

    @Column(name = "LOSE_SCORES", nullable = false)
    public Integer getLoseScores() {
        return this.loseScores;
    }

    public void setLoseScores(Integer loseScores) {
        this.loseScores = loseScores;
    }

    @Column(name = "DRAW_TIMES", nullable = false)
    public Integer getDrawTimes() {
        return this.drawTimes;
    }

    public void setDrawTimes(Integer drawTimes) {
        this.drawTimes = drawTimes;
    }

    @Column(name = "DRAW_SCORES", nullable = false)
    public Integer getDrawScores() {
        return this.drawScores;
    }

    public void setDrawScores(Integer drawScores) {
        this.drawScores = drawScores;
    }

    @Column(name = "TOTAL_TIMES", nullable = false)
    public Integer getTotalTimes() {
        return this.totalTimes;
    }

    public void setTotalTimes(Integer totalTimes) {
        this.totalTimes = totalTimes;
    }

    @Column(name = "TOTAL_SCORES", nullable = false)
    public Integer getTotalScores() {
        return this.totalScores;
    }

    public void setTotalScores(Integer totalScores) {
        this.totalScores = totalScores;
    }

    @Column(name = "TOTAL_SYSTEM_SCORE", nullable = false)
    public Integer getTotalSystemScore() {
        return this.totalSystemScore;
    }

    public void setTotalSystemScore(Integer totalSystemScore) {
        this.totalSystemScore = totalSystemScore;
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
