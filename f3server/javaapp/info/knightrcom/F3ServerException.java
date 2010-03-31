package info.knightrcom;

/**
 * 应用异常
 */
public class F3ServerException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 3276015861587836150L;

    /**
     * 
     * @param message
     *            the detail message. The detail message is saved for later
     *            retrieval by the {@link #getMessage()} method.
     */
    public F3ServerException(String message) {
        super(message);
    }
}
