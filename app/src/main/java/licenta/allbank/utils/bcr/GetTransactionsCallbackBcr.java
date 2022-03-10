package licenta.allbank.utils.bcr;

import licenta.allbank.data.model.bcr.transactions.TransactionsResponseBcr;

public interface GetTransactionsCallbackBcr {
    void onSuccess(TransactionsResponseBcr transactionsResponseBcr);

    void onError(Throwable t);
}
