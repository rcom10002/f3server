package info.knightrcom.data.metadata;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * GameRecord entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "game_record", catalog = "f3s")
public class GameRecord extends info.knightrcom.data.SimplePojoImpl implements java.io.Serializable {

    // Fields

    private String gameId;

    private String gameType;

    private Short gameSetting;

    private String winnerNumbers;

    private String players;

    private Integer score;

    private Integer systemScore;

    private String record;

    private Short status;

    private Date createTime;

    private String createBy;

    private Date updateTime;

    private String updateBy;

    // Constructors

    /** default constructor */
    public GameRecord() {
    }

    /** minimal constructor */
    public GameRecord(String gameId, String gameType, String record) {
        this.gameId = gameId;
        this.gameType = gameType;
        this.record = record;
    }

    /** full constructor */
    public GameRecord(String gameId, String gameType, Short gameSetting, String winnerNumbers, String players, Integer score, Integer systemScore, String record, Short status, Date createTime, String createBy, Date updateTime, String updateBy) {
        this.gameId = gameId;
        this.gameType = gameType;
        this.gameSetting = gameSetting;
        this.winnerNumbers = winnerNumbers;
        this.players = players;
        this.score = score;
        this.systemScore = systemScore;
        this.record = record;
        this.status = status;
        this.createTime = createTime;
        this.createBy = createBy;
        this.updateTime = updateTime;
        this.updateBy = updateBy;
    }

    // Property accessors
    @Id
    @Column(name = "GAME_ID", unique = true, nullable = false, length = 100)
    public String getGameId() {
        return this.gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    @Column(name = "GAME_TYPE", nullable = false, length = 100)
    public String getGameType() {
        return this.gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    @Column(name = "GAME_SETTING")
    public Short getGameSetting() {
        return this.gameSetting;
    }

    public void setGameSetting(Short gameSetting) {
        this.gameSetting = gameSetting;
    }

    @Column(name = "WINNER_NUMBERS", length = 100)
    public String getWinnerNumbers() {
        return this.winnerNumbers;
    }

    public void setWinnerNumbers(String winnerNumbers) {
        this.winnerNumbers = winnerNumbers;
    }

    @Column(name = "PLAYERS", length = 100)
    public String getPlayers() {
        return this.players;
    }

    public void setPlayers(String players) {
        this.players = players;
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

    @Column(name = "RECORD", nullable = false, length = 65535)
    public String getRecord() {
        return this.record;
    }

    public void setRecord(String record) {
        this.record = record;
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
