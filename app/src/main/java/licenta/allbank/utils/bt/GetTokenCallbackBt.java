package licenta.allbank.utils.bt;

import licenta.allbank.data.model.bt.TokenBt;

public interface GetTokenCallbackBt {
    void onSuccess(TokenBt tokenBt);

    void onError(Throwable t);
}
