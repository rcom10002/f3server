package info.knightrcom.model.global;


import java.util.Date;
import java.util.UUID;

import org.apache.mina.core.session.IoSession;


/**
 *
 */
public class Player extends AbstractModel<Room, AbstractModel.DummyChildModel> {

    public static final String ATTR_NAME = "PLAYER";

    /** 玩家最后一次的游戏时间 */
    private long lastPlayTime = new Date().getTime();

    /** 当前游戏id */
    private String gameId;

    /** 当前游戏玩家在游戏中的序号 */
    private int currentNumber;

    /** 玩家本局得分 */
    private double currentScore;

    /** 玩家本局所产生的系统分 */
    private double systemScore; 

    /** 当前玩家所对应的会话 */
    private IoSession iosession;

    /** 玩家初始状态，辅助程序运行，不参与业务，可忽略该字段 */
    private GameStatus orgStatus = GameStatus.RAW;

    /** 当前游戏状态 */
    private GameStatus currentStatus = GameStatus.WANDER;
    /**
     * 
     */
    public Player() {
        super();
        this.id = UUID.randomUUID().toString();
    }

    /**
     * @return
     */
    public Room getCurrentRoom() {
        return this.parent;
    }

    /**
     * @return
     */
    public Lobby getCurrentLobby() {
        return this.parent.getParent();
    }

    /**
     * @return the orgStatus
     */
    public GameStatus getOrgStatus() {
        return orgStatus;
    }

    /**
     * @return the isGamePlaying
     */
    public GameStatus getCurrentStatus() {
        return currentStatus;
    }

    /**
     * @param isGamePlaying the isGamePlaying to set
     */
    public void setCurrentStatus(GameStatus status) {
        if (this.currentStatus.equals(status)) {
            // 状态未发生改变时，不做任何处理
            return;
        }
        this.orgStatus = currentStatus;
        this.currentStatus = status;
        if (this.parent != null) {
            if (GameStatus.PLAYING.equals(status)) {
                // 状态从非游戏状态转入游戏状态
                this.lastPlayTime = new Date().getTime();
            }
            this.parent.updateGameStatusNumber(this);
        }
    }
    
    /**
     * @return
     */
    public long getLastPlayTime() {
        return this.lastPlayTime;
    }

    /**
     * @return the gameId
     */
    public String getGameId() {
        return gameId;
    }

    /**
     * @param gameId the gameId to set
     */
    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    /**
     * @return the iosession
     */
    public IoSession getIosession() {
        return iosession;
    }

    /**
     * @param iosession the iosession to set
     */
    public void setIosession(IoSession iosession) {
        this.iosession = iosession;
    }

    /**
     * @return the currentNumber
     */
    public String getCurrentNumber() {
        return String.valueOf(currentNumber);
    }

    /**
     * @param currentNumber the currentSn to set
     */
    public void setCurrentNumber(int currentNumber) {
        this.currentNumber = currentNumber;
    }

    /**
     * @return the currentScore
     */
    public double getCurrentScore() {
        return currentScore;
    }

    /**
     * @param currentScore the currentScore to set
     */
    public void setCurrentScore(double currentScore) {
        this.currentScore = currentScore;
    }

    /**
     * @return the systemScore
     */
    public double getSystemScore() {
        return systemScore;
    }

    /**
     * @param systemScore the systemScore to set
     */
    public void setSystemScore(double systemScore) {
        this.systemScore = systemScore;
    }

}
