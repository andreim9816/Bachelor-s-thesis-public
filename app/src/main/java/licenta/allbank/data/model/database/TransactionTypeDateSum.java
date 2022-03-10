package licenta.allbank.data.model.database;

import org.joda.time.DateTime;

public class TransactionTypeDateSum {
    private String transactionCategory;
    private DateTime bookingDate;
    private float sum;

    public TransactionTypeDateSum(String transactionCategory, DateTime bookingDate, float sum) {
        this.transactionCategory = transactionCategory;
        this.bookingDate = bookingDate;
        this.sum = sum;
    }

    public String getTransactionCategory() {
        return transactionCategory;
    }

    public DateTime getBookingDate() {
        return bookingDate;
    }

    public float getSum() {
        return sum;
    }
}
