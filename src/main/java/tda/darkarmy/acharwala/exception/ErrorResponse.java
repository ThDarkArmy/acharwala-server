package tda.darkarmy.acharwala.exception;

public class ErrorResponse {
    private int statusCode;
    private String errorMessage;
    private Object errorBody;

    public ErrorResponse() {
    }

    public ErrorResponse(int statusCode, String errorMessage, Object errorBody) {
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
        this.errorBody = errorBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Object getErrorBody() {
        return errorBody;
    }

    public void setErrorBody(Object errorBody) {
        this.errorBody = errorBody;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "statusCode=" + statusCode +
                ", errorMessage='" + errorMessage + '\'' +
                ", errorBody=" + errorBody +
                '}';
    }
}
