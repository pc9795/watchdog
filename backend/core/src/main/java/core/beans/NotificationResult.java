package core.beans;

/**
 * Created By: Prashant Chaubey
 * Created On: 27-12-2019 04:04
 * Purpose: TODO:
 **/
public class NotificationResult {
    private boolean success;
    private String errorMessage;

    public NotificationResult() {
        this.success = true;
    }

    public NotificationResult(String errorMessage) {
        this.success = false;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "NotificationResult{" +
                "success=" + success +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
