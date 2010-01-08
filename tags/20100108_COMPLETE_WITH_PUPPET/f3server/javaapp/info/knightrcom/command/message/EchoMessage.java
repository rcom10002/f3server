package info.knightrcom.command.message;

public interface EchoMessage {

    public static enum MessagePropery {
        TYPE, NUMBER;
    }

    int getType();

    long getNumber();

    void setResult(String result);

    String getResult();

    void setContent(String content);

    String getContent();

}
