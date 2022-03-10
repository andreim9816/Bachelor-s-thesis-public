package licenta.allbank.utils.server;

import licenta.allbank.data.model.allbank.AccountsResponse;

public interface GetAccountsDataCallbackServer {
    void onSuccess(AccountsResponse accountsResponse);

    void onError(Throwable t);
}
