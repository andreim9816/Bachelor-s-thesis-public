package licenta.allbank.data.model.bt.transactions;

import java.util.List;

public class TransactionsObjectBt {
    private List<TransactionDetailsBt> booked;
    private List<TransactionDetailsBt> pending;

    public List<TransactionDetailsBt> getBooked() {
        return booked;
    }

    public void setBooked(List<TransactionDetailsBt> booked) {
        this.booked = booked;
    }

    public List<TransactionDetailsBt> getPending() {
        return pending;
    }

    public void setPending(List<TransactionDetailsBt> pending) {
        this.pending = pending;
    }
}
