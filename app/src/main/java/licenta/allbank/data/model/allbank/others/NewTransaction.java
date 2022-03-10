package licenta.allbank.data.model.allbank.others;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

import licenta.allbank.data.model.bcr.transactions.TransactionAmountBcr;
import licenta.allbank.data.model.database.Transaction;
import licenta.allbank.utils.DateFormatGMT;

import static licenta.allbank.ui.home.HomeFragment.EXPENSES_TRANSACTIONS;

public class NewTransaction implements Parcelable {
    private String id;
    private String myName;
    private String myIban;
    private String myAccountId;
    private String creditorIban;
    private String creditorName;
    private float amount;
    private String currency;
    private String category;
    private String details;
    private String bank;

    public NewTransaction(String myIban, String creditorIban, String creditorName, float amount, String currency, String details, String bank, String category) {
        this.myIban = myIban;
        this.creditorIban = creditorIban;
        this.creditorName = creditorName;
        this.amount = amount;
        this.currency = currency;
        this.details = details;
        this.bank = bank;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMyAccountId(String myAccountId) {
        this.myAccountId = myAccountId;
    }

    public String getMyAccountId() {
        return myAccountId;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }

    public String getMyName() {
        return myName;
    }

    public void setMyIban(String myIban) {
        this.myIban = myIban;
    }

    public void setCreditorIban(String creditorIban) {
        this.creditorIban = creditorIban;
    }

    public void setCreditorName(String creditorName) {
        this.creditorName = creditorName;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getMyIban() {
        return myIban;
    }

    public String getCreditorIban() {
        return creditorIban;
    }

    public String getCreditorName() {
        return creditorName;
    }

    public float getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getDetails() {
        return details;
    }

    public String getBank() {
        return bank;
    }

    public static Transaction convertNewTransactionDataToTransactionObject(NewTransaction newTransaction) {
        String transactionId = newTransaction.getId();
        DateTime bookingDate = DateFormatGMT.getTodayDate();
        DateTime valueDate = DateFormatGMT.getTodayDate();
        String creditorName = newTransaction.getCreditorName();
        String debtorName = newTransaction.getMyName();
        TransactionAmountBcr transactionAmount = new TransactionAmountBcr(newTransaction.getCurrency(), (-1) * newTransaction.getAmount());
        String creditorIban = newTransaction.getCreditorIban();
        String debtorIban = newTransaction.getMyIban();
        String myAccountId = newTransaction.getMyAccountId();
        String details = newTransaction.getDetails();
        String category = newTransaction.getCategory();
        String transactionType = EXPENSES_TRANSACTIONS;
        String bank = newTransaction.getBank();
        return new Transaction(transactionId, bookingDate, valueDate, transactionAmount, debtorIban, creditorIban, debtorName, creditorName, myAccountId, "BOOKED", transactionType, category, details, bank, true);
    }

    public static final Parcelable.Creator<NewTransaction> CREATOR = new Parcelable.Creator<NewTransaction>() {
        @Override
        public NewTransaction createFromParcel(Parcel in) {
            return new NewTransaction(in);
        }

        @Override
        public NewTransaction[] newArray(int size) {
            return new NewTransaction[size];
        }
    };

    public static Parcelable.Creator<NewTransaction> getCREATOR() {
        return CREATOR;
    }

    public NewTransaction(Parcel in) {
        this.id = in.readString();
        this.myName = in.readString();
        this.myIban = in.readString();
        this.myAccountId = in.readString();
        this.creditorIban = in.readString();
        this.creditorName = in.readString();
        this.amount = in.readFloat();
        this.currency = in.readString();
        this.category = in.readString();
        this.details = in.readString();
        this.bank = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(myName);
        dest.writeString(myIban);
        dest.writeString(myAccountId);
        dest.writeString(creditorIban);
        dest.writeString(creditorName);
        dest.writeFloat(amount);
        dest.writeString(currency);
        dest.writeString(category);
        dest.writeString(details);
        dest.writeString(bank);
    }
}
