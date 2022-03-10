package licenta.allbank.utils.server;

import licenta.allbank.data.model.allbank.ApiData;

public interface GetApiDataCallbackServer {
    void onSuccess(ApiData apiData);

    void onError(Throwable t);
}
