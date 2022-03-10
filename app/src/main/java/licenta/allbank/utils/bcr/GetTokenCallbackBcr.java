package licenta.allbank.utils.bcr;

import licenta.allbank.data.model.bcr.TokenBcr;

public interface GetTokenCallbackBcr {
    void onSuccess(TokenBcr tokenBcr);

    void onError(Throwable t);
}
