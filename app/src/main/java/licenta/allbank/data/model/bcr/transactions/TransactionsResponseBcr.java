package licenta.allbank.data.model.bcr.transactions;

import com.google.gson.annotations.SerializedName;

import licenta.allbank.data.model.bcr.transactions.TransactionsListBcr;

public class TransactionsResponseBcr {
    @SerializedName("transactions")
    private TransactionsListBcr transactionsListBcr;

    public TransactionsListBcr getTransactionsListBcr() {
        return transactionsListBcr;
    }

    public void setTransactionsListBcr(TransactionsListBcr transactionsListBcr) {
        this.transactionsListBcr = transactionsListBcr;
    }
}
