package info.knightrcom.data.metadata;
// default package

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * PeriodlySumExt entity.
 * 
 * @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "periodly_sum_ext")
public class PeriodlySumExt extends info.knightrcom.data.SimplePojoImpl implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -2085158393447664297L;
	private String periodlyId;
	private String profileId;
	private String number;
	private String title;
	private Date startDate;
	private Date endDate;
	private Integer winTimes;
	private Double winScores;
	private Integer loseTimes;
	private Double loseScores;
	private Integer drawTimes;
	private Double drawScores;
	private Integer totalTimes;
	private Double totalScores;
	private Double totalSystemScore;
	private String status;
	private Double currentScore;
	private Double rechargeSum;
	private String expression;
	private Double resultScore;
	private Date createTime;
	private String createBy;
	private Date updateTime;
	private String updateBy;

	// Constructors

	/** default constructor */
	public PeriodlySumExt() {
	}

	/** minimal constructor */
	public PeriodlySumExt(String periodlyId, String title, Date startDate,
			Date endDate, Integer winTimes, Double winScores,
			Integer loseTimes, Double loseScores, Integer drawTimes,
			Double drawScores, Integer totalTimes, Double totalScores,
			Double totalSystemScore) {
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
	public PeriodlySumExt(String periodlyId, String profileId, String number,
			String title, Date startDate, Date endDate, Integer winTimes,
			Double winScores, Integer loseTimes, Double loseScores,
			Integer drawTimes, Double drawScores, Integer totalTimes,
			Double totalScores, Double totalSystemScore, String status,
			Double currentScore, Double rechargeSum, String expression,
			Double resultScore, Date createTime, String createBy,
			Date updateTime, String updateBy) {
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
		this.currentScore = currentScore;
		this.rechargeSum = rechargeSum;
		this.expression = expression;
		this.resultScore = resultScore;
		this.createTime = createTime;
		this.createBy = createBy;
		this.updateTime = updateTime;
		this.updateBy = updateBy;
	}

	// Property accessors
	@Id
	@Column(name = "PERIODLY_ID", unique = true, nullable = false, insertable = true, updatable = true, length = 100)
	public String getPeriodlyId() {
		return this.periodlyId;
	}

	public void setPeriodlyId(String periodlyId) {
		this.periodlyId = periodlyId;
	}

	@Column(name = "PROFILE_ID", unique = false, nullable = true, insertable = true, updatable = true, length = 100)
	public String getProfileId() {
		return this.profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	@Column(name = "NUMBER", unique = false, nullable = true, insertable = true, updatable = true, length = 100)
	public String getNumber() {
		return this.number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	@Column(name = "TITLE", unique = false, nullable = false, insertable = true, updatable = true, length = 100)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "START_DATE", unique = false, nullable = false, insertable = true, updatable = true, length = 10)
	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "END_DATE", unique = false, nullable = false, insertable = true, updatable = true, length = 10)
	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Column(name = "WIN_TIMES", unique = false, nullable = false, insertable = true, updatable = true)
	public Integer getWinTimes() {
		return this.winTimes;
	}

	public void setWinTimes(Integer winTimes) {
		this.winTimes = winTimes;
	}

	@Column(name = "WIN_SCORES", unique = false, nullable = false, insertable = true, updatable = true, precision = 10)
	public Double getWinScores() {
		return this.winScores;
	}

	public void setWinScores(Double winScores) {
		this.winScores = winScores;
	}

	@Column(name = "LOSE_TIMES", unique = false, nullable = false, insertable = true, updatable = true)
	public Integer getLoseTimes() {
		return this.loseTimes;
	}

	public void setLoseTimes(Integer loseTimes) {
		this.loseTimes = loseTimes;
	}

	@Column(name = "LOSE_SCORES", unique = false, nullable = false, insertable = true, updatable = true, precision = 10)
	public Double getLoseScores() {
		return this.loseScores;
	}

	public void setLoseScores(Double loseScores) {
		this.loseScores = loseScores;
	}

	@Column(name = "DRAW_TIMES", unique = false, nullable = false, insertable = true, updatable = true)
	public Integer getDrawTimes() {
		return this.drawTimes;
	}

	public void setDrawTimes(Integer drawTimes) {
		this.drawTimes = drawTimes;
	}

	@Column(name = "DRAW_SCORES", unique = false, nullable = false, insertable = true, updatable = true, precision = 10)
	public Double getDrawScores() {
		return this.drawScores;
	}

	public void setDrawScores(Double drawScores) {
		this.drawScores = drawScores;
	}

	@Column(name = "TOTAL_TIMES", unique = false, nullable = false, insertable = true, updatable = true)
	public Integer getTotalTimes() {
		return this.totalTimes;
	}

	public void setTotalTimes(Integer totalTimes) {
		this.totalTimes = totalTimes;
	}

	@Column(name = "TOTAL_SCORES", unique = false, nullable = false, insertable = true, updatable = true, precision = 10)
	public Double getTotalScores() {
		return this.totalScores;
	}

	public void setTotalScores(Double totalScores) {
		this.totalScores = totalScores;
	}

	@Column(name = "TOTAL_SYSTEM_SCORE", unique = false, nullable = false, insertable = true, updatable = true, precision = 10)
	public Double getTotalSystemScore() {
		return this.totalSystemScore;
	}

	public void setTotalSystemScore(Double totalSystemScore) {
		this.totalSystemScore = totalSystemScore;
	}

	@Column(name = "STATUS", unique = false, nullable = true, insertable = true, updatable = true, length = 100)
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "CURRENT_SCORE", unique = false, nullable = true, insertable = true, updatable = true, precision = 10)
	public Double getCurrentScore() {
		return this.currentScore;
	}

	public void setCurrentScore(Double currentScore) {
		this.currentScore = currentScore;
	}

	@Column(name = "RECHARGE_SUM", unique = false, nullable = true, insertable = true, updatable = true, precision = 10)
	public Double getRechargeSum() {
		return this.rechargeSum;
	}

	public void setRechargeSum(Double rechargeSum) {
		this.rechargeSum = rechargeSum;
	}

	@Column(name = "EXPRESSION", unique = false, nullable = true, insertable = true, updatable = true, length = 100)
	public String getExpression() {
		return this.expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	@Column(name = "RESULT_SCORE", unique = false, nullable = true, insertable = true, updatable = true, precision = 10)
	public Double getResultScore() {
		return this.resultScore;
	}

	public void setResultScore(Double resultScore) {
		this.resultScore = resultScore;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "CREATE_TIME", unique = false, nullable = true, insertable = true, updatable = true, length = 19)
	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "CREATE_BY", unique = false, nullable = true, insertable = true, updatable = true, length = 100)
	public String getCreateBy() {
		return this.createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "UPDATE_TIME", unique = false, nullable = true, insertable = true, updatable = true, length = 19)
	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Column(name = "UPDATE_BY", unique = false, nullable = true, insertable = true, updatable = true, length = 100)
	public String getUpdateBy() {
		return this.updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

}