package licenta.allbank.utils.bcr;

import licenta.allbank.data.model.bcr.accounts.AccountsResponseBcr;

public interface GetAccountsCallbackBcr {
    void onSuccess(AccountsResponseBcr transactionsResponseBcr);

    void onError(Throwable t);
}
