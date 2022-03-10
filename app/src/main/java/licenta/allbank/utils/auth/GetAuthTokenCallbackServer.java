package licenta.allbank.utils.auth;

import licenta.allbank.data.model.allbank.auth.LoginResponse;

public interface GetAuthTokenCallbackServer {
    void onSuccess(LoginResponse loginResponse);

    void onError(Throwable t);
}
