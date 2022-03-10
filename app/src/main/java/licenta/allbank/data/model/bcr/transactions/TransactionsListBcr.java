package licenta.allbank.data.model.bcr.transactions;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TransactionsListBcr {
    @SerializedName("booked")
    private List<TransactionDetailsBcr> transactionsBookedListBcr;
    @SerializedName("pending")
    private List<TransactionDetailsBcr> transactionsPendingListBcr;

    public List<TransactionDetailsBcr> getTransactionsBookedListBcr() {
        return transactionsBookedListBcr;
    }

    public void setTransactionsBookedListBcr(List<TransactionDetailsBcr> transactionsBookedListBcr) {
        this.transactionsBookedListBcr = transactionsBookedListBcr;
    }

    public List<TransactionDetailsBcr> getTransactionsPendingListBcr() {
        return transactionsPendingListBcr;
    }

    public void setTransactionsPendingListBcr(List<TransactionDetailsBcr> transactionsPendingListBcr) {
        this.transactionsPendingListBcr = transactionsPendingListBcr;
    }
}
