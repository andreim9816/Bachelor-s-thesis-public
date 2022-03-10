package licenta.allbank.data.model.bcr.transactions;

import java.util.List;

public class TransactionsObjectBcr {
    private List<TransactionDetailsBcr> booked;
    private List<TransactionDetailsBcr> pending;

    public List<TransactionDetailsBcr> getBooked() {
        return booked;
    }

    public List<TransactionDetailsBcr> getPending() {
        return pending;
    }
}
