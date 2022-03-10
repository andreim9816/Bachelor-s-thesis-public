package licenta.allbank.utils.ing;

import licenta.allbank.data.model.ing.TokenIng;

public interface GetTokenCallbackIng {
    void onSuccess(TokenIng tokenIng);

    void onError(Throwable t);
}