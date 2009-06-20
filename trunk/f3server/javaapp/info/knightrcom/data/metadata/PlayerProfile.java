package info.knightrcom.data.metadata;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * PlayerProfile entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "player_profile", catalog = "f3s")
public class PlayerProfile extends info.knightrcom.data.SimplePojoImpl implements java.io.Serializable {

    // Fields

    private String profileId;

    private String number;

    private String name;

    private String userId;

    private String password;

    private Integer currentScore;

    private Integer initLimit;

    private Integer level;

    private String rlsPath;

    private Short role;

    private Short status;

    private Date createTime;

    private String createBy;

    private Date updateTime;

    private String updateBy;

    // Constructors

    /** default constructor */
    public PlayerProfile() {
    }

    /** minimal constructor */
    public PlayerProfile(String profileId) {
        this.profileId = profileId;
    }

    /** full constructor */
    public PlayerProfile(String profileId, String number, String name, String userId, String password, Integer currentScore, Integer initLimit, Integer level, String rlsPath, Short role, Short status, Date createTime, String createBy, Date updateTime, String updateBy) {
        this.profileId = profileId;
        this.number = number;
        this.name = name;
        this.userId = userId;
        this.password = password;
        this.currentScore = currentScore;
        this.initLimit = initLimit;
        this.level = level;
        this.rlsPath = rlsPath;
        this.role = role;
        this.status = status;
        this.createTime = createTime;
        this.createBy = createBy;
        this.updateTime = updateTime;
        this.updateBy = updateBy;
    }

    // Property accessors
    @Id
    @Column(name = "PROFILE_ID", unique = true, nullable = false, length = 100)
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

    @Column(name = "NAME", length = 100)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "USER_ID", length = 16)
    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name = "PASSWORD", length = 16)
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "CURRENT_SCORE")
    public Integer getCurrentScore() {
        return this.currentScore;
    }

    public void setCurrentScore(Integer currentScore) {
        this.currentScore = currentScore;
    }

    @Column(name = "INIT_LIMIT")
    public Integer getInitLimit() {
        return this.initLimit;
    }

    public void setInitLimit(Integer initLimit) {
        this.initLimit = initLimit;
    }

    @Column(name = "LEVEL")
    public Integer getLevel() {
        return this.level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @Column(name = "RLS_PATH", length = 1000)
    public String getRlsPath() {
        return this.rlsPath;
    }

    public void setRlsPath(String rlsPath) {
        this.rlsPath = rlsPath;
    }

    @Column(name = "ROLE")
    public Short getRole() {
        return this.role;
    }

    public void setRole(Short role) {
        this.role = role;
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
