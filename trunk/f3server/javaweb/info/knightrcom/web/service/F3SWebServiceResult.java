package info.knightrcom.web.service;

/**
 *
 */
public enum F3SWebServiceResult {

    SUCCESS, WARNING, FAIL, 
    CREATE_SUCCESS, UPDATE_SUCCESS, DELETE_SUCCESS, 
    CREATE_WARNING, UPDATE_WARNING, DELETE_WARNING, 
    CREATE_FAIL, UPDATE_FAIL, DELETE_FAIL;

    private String operation;

    /**
     * @return the operation
     */
    public String getOperation() {
        return operation;
    }

    /**
     * @param operation
     *            the operation to set
     */
    public void setOperation(String operation) {
        this.operation = operation;
    }
}
