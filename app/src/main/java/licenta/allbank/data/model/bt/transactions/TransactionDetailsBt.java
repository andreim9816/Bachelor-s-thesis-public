package licenta.allbank.data.model.bt.transactions;

import com.google.gson.annotations.SerializedName;

public class TransactionDetailsBt {
    private String transactionId;
    private String bookingDate;
    private String valueDate;
    @SerializedName("remittanceInformationUnstructured")
    private String details;
    private String creditorName;
    private CreditorAccountBt creditorAccount;
    private TransactionAmountBt transactionAmount;
    @SerializedName("transactionCategory")
    private String category;

    public TransactionDetailsBt(String transactionId, String bookingDate, String valueDate, String details, String creditorName, CreditorAccountBt creditorAccount, TransactionAmountBt transactionAmount, String category) {
        this.transactionId = transactionId;
        this.bookingDate = bookingDate;
        this.valueDate = valueDate;
        this.details = details;
        this.creditorName = creditorName;
        this.creditorAccount = creditorAccount;
        this.transactionAmount = transactionAmount;
        this.category = category;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getValueDate() {
        return valueDate;
    }

    public void setValueDate(String valueDate) {
        this.valueDate = valueDate;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getCreditorName() {
        return creditorName;
    }

    public void setCreditorName(String creditorName) {
        this.creditorName = creditorName;
    }

    public CreditorAccountBt getCreditorAccount() {
        return creditorAccount;
    }

    public void setCreditorAccount(CreditorAccountBt creditorAccount) {
        this.creditorAccount = creditorAccount;
    }

    public TransactionAmountBt getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(TransactionAmountBt transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
