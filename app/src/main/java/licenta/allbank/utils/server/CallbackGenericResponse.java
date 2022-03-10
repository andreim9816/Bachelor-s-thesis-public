package licenta.allbank.utils.server;

public interface CallbackGenericResponse<T> {
    void onSuccess(T response);

    void onError(Throwable t);
}
