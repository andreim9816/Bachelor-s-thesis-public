package licenta.allbank.data.model.bcr.transactions;

import com.google.gson.annotations.SerializedName;

public class TransactionDetailsBcr {
    @SerializedName("transactionId")
    private String id;
    private String bookingDate;
    private String valueDate;
    @SerializedName("remittanceInformationUnstructured")
    private String details;
    private TransactionAmountBcr transactionAmount;
    private String creditorName;
    private CreditorAccountBcr creditorAccount;
    private String debtorName;
    private DebtorAccountBcr debtorAccount;
    @SerializedName("transactionCategory")
    private String category;

    public TransactionDetailsBcr(String id, String bookingDate, String valueDate, String details, TransactionAmountBcr transactionAmount, String creditorName, CreditorAccountBcr creditorAccount, String debtorName, DebtorAccountBcr debtorAccount, String category) {
        this.id = id;
        this.bookingDate = bookingDate;
        this.valueDate = valueDate;
        this.details = details;
        this.transactionAmount = transactionAmount;
        this.creditorName = creditorName;
        this.creditorAccount = creditorAccount;
        this.debtorName = debtorName;
        this.debtorAccount = debtorAccount;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public TransactionAmountBcr getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(TransactionAmountBcr transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getCreditorName() {
        return creditorName;
    }

    public void setCreditorName(String creditorName) {
        this.creditorName = creditorName;
    }

    public CreditorAccountBcr getCreditorAccount() {
        return creditorAccount;
    }

    public void setCreditorAccount(CreditorAccountBcr creditorAccount) {
        this.creditorAccount = creditorAccount;
    }

    public String getDebtorName() {
        return debtorName;
    }

    public void setDebtorName(String debtorName) {
        this.debtorName = debtorName;
    }

    public DebtorAccountBcr getDebtorAccount() {
        return debtorAccount;
    }

    public void setDebtorAccount(DebtorAccountBcr debtorAccount) {
        this.debtorAccount = debtorAccount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
