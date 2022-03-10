package licenta.allbank.utils.ing;

import licenta.allbank.data.model.ing.AuthUrlResponse;

public interface GetAuthUrlCallbackIng {
    void onSuccess(AuthUrlResponse authUrlResponse);

    void onError(Throwable t);
}
