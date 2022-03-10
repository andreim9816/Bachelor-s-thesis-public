package licenta.allbank.utils.auth;

import licenta.allbank.data.model.ing.AuthUrlResponse;

public interface GetAuthUrlCallbackIng {
    void onSuccess(AuthUrlResponse authUrlResponse);

    void onError(Throwable t);
}
