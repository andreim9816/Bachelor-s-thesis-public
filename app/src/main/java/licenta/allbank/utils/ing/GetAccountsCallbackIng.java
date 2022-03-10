package licenta.allbank.utils.ing;

import licenta.allbank.data.model.ing.accounts.AccountsResponseIng;

public interface GetAccountsCallbackIng {
    void onSuccess(AccountsResponseIng accountsResponseIng);

    void onError(Throwable t);
}
