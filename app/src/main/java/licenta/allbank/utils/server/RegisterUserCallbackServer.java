package licenta.allbank.utils.server;

import licenta.allbank.data.model.allbank.auth.LoginResponse;

public interface RegisterUserCallbackServer {
    void onSuccess(LoginResponse loginResponse);

    void onError(Throwable t);
}
