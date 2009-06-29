package info.knightrcom.command.message;

import info.knightrcom.command.handler.F3ServerInMessageHandler;
import info.knightrcom.command.message.game.FightLandlordGameMessage;
import info.knightrcom.command.message.game.PushdownWinGameMessage;
import info.knightrcom.command.message.game.QiongWinGameMessage;
import info.knightrcom.command.message.game.Red5GameMessage;
import info.knightrcom.util.HandlerDispatcher;

import java.util.Set;

import org.apache.mina.core.session.IoSession;

/**
 * 
 */
public abstract class F3ServerMessage {

    public static enum MessageType {
        /** 平台消息 */
        PLATFORM, 
        /** 玩家消息 */
        PLAYER, 
        /** 红五 */
        RED5GAME, 
        /** 斗地主 */
        FIGHT_LANDLORD,
        /** 推到胡 */
        PUSHDOWN_WIN, 
        /** 穷胡 */
        QIONG_WIN;
    }

    private IoSession currentSession;

    private Set<IoSession> sessions;

    private String content;

    private String signature;

    private long number;

    private int type;

    private EchoMessage echoMessage = new EchoMessage() {

        private String content;

        private String result = F3ServerInMessageHandler.NO_ECHO_RESULT;

        public int getType() {
            return type;
        }

        public String getContent() {
            return content;
        }

        public long getNumber() {
            return number;
        }

        public String getResult() {
            return result;
        }

        /**
         * @param content
         *            the content to set
         */
        public void setContent(String content) {
            this.content = content;
        }

        /**
         * @param result
         *            the result to set
         */
        public void setResult(String result) {
            this.result = result;
        }
        
        @Override
        public String toString() {
            return HandlerDispatcher.respondMessage(this);
        }

    };


    protected F3ServerMessage() { }

    /**
     * @return the number
     */
    public long getNumber() {
        return number;
    }

    /**
     * @param number the number to set
     */
    public void setNumber(long number) {
        this.number = number;
    }

    /**
     * @param messageType
     * @return
     */
    public static F3ServerMessage createInstance(int messageType) {
        F3ServerMessage resultMessage = null;
        if (MessageType.PLATFORM.ordinal() == messageType) {
            resultMessage = new PlatformMessage();
        } else if (MessageType.PLAYER.ordinal() == messageType) {
            resultMessage = new PlayerMessage();
        } else if (MessageType.RED5GAME.ordinal() == messageType) {
            resultMessage = new Red5GameMessage();
        } else if (MessageType.FIGHT_LANDLORD.ordinal() == messageType) {
            resultMessage = new FightLandlordGameMessage();
        } else if (MessageType.PUSHDOWN_WIN.ordinal() == messageType) {
            resultMessage = new PushdownWinGameMessage();
        } else if (MessageType.QIONG_WIN.ordinal() == messageType) {
            resultMessage = new QiongWinGameMessage();
        } 
        if (resultMessage == null) {
            throw new RuntimeException("Unsupport message type!!!");
        }
        resultMessage.setType(messageType);
        return resultMessage;
    }

    /**
     * @param messageType
     * @return
     */
    public static F3ServerMessage createInstance(MessageType type) {
        return createInstance(type.ordinal());
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content
     *            the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     * @param signature
     *            the signature to set
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }

    /**
     * @return the sessions
     */
    public Set<IoSession> getSessions() {
        return sessions;
    }

    /**
     * @param sessions
     *            the sessions to set
     */
    public void setSessions(Set<IoSession> sessions) {
        this.sessions = sessions;
    }

    /**
     * @return the currentSession
     */
    public IoSession getCurrentSession() {
        return currentSession;
    }

    /**
     * @param currentSession the currentSession to set
     */
    public void setCurrentSession(IoSession currentSession) {
        this.currentSession = currentSession;
    }

    /**
     * @return the echoMessage
     */
    public EchoMessage getEchoMessage() {
        return echoMessage;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

}
