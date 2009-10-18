package info.knightrcom.model.global;

public enum GameStatus {
    /** 原始状态，只是为辅助程序运行，不参与具体业务，可忽略 */
    RAW,
    /** 未加入任何游戏房间，仍在徘徊中 */
    WANDER,
    /** 已加入游戏房间，未进入系统配对环节 */
    IDLE,
    /** 已加入游戏房间，正在系统配对 */
    MATCHING,
    /** 游戏进行中 */
    PLAYING;
}