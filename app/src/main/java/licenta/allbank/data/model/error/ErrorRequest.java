package licenta.allbank.data.model.error;

public class ErrorRequest extends Throwable {
    public static final String ERROR_MSG = "An error has occurred!";
    private String errorMessage;

    public ErrorRequest() {
        super();
        setErrorMessage(ERROR_MSG);
    }

    public ErrorRequest(String errorMessage) {
        super();
        setErrorMessage(errorMessage);
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
