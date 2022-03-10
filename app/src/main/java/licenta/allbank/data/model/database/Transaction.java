package licenta.allbank.data.model.database;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.joda.time.DateTime;

import licenta.allbank.data.model.bcr.transactions.TransactionAmountBcr;

@Entity(tableName = "transactions_table")
public class Transaction {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    private String transactionId;
    @TypeConverters(DateConverter.class)
    private DateTime bookingDate;
    @TypeConverters(DateConverter.class)
    private DateTime valueDate;
    @Embedded
    private TransactionAmountBcr transactionAmount;
    private String debtorAccount;
    private String creditorAccount;

    private String debtorName;
    private String creditorName;

    private String details;
    private String accountId;
    private String status; // booked or pending
    private String transactionType; // expense or income
    private String transactionCategory; // shopping, rent, transport, education, others etc
    private String bank; // BCR or BT
    private boolean enabled;

    public Transaction(@NonNull String transactionId, DateTime bookingDate, DateTime valueDate, TransactionAmountBcr transactionAmount, String debtorAccount, String creditorAccount, String debtorName, String creditorName, String accountId, String status, String transactionType, String transactionCategory, String details, String bank, boolean enabled) {
        this.transactionId = transactionId;
        this.bookingDate = bookingDate;
        this.valueDate = valueDate;
        this.transactionAmount = transactionAmount;
        this.debtorAccount = debtorAccount;
        this.creditorAccount = creditorAccount;
        this.debtorName = debtorName;
        this.creditorName = creditorName;
        this.accountId = accountId;
        this.details = details;
        this.status = status;
        this.transactionType = transactionType;
        this.transactionCategory = transactionCategory;
        this.bank = bank;
        this.enabled = enabled;
    }

    public static final Parcelable.Creator<Transaction> CREATOR = new Parcelable.Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    public Transaction(Parcel in) {
        this.transactionId = in.readString();
        this.bookingDate = new DateTime(in.readLong());
        this.valueDate = new DateTime(in.readLong());
        this.debtorAccount = in.readString();
        this.creditorAccount = in.readString();
        this.transactionAmount = (TransactionAmountBcr) in.readValue(TransactionAmountBcr.class.getClassLoader());
        this.debtorName = in.readString();
        this.creditorName = in.readString();
        this.accountId = in.readString();
        this.details = in.readString();
        this.status = in.readString();
        this.transactionType = in.readString();
        this.transactionCategory = in.readString();
        this.bank = in.readString();
        this.enabled = in.readBoolean();
    }

    @NonNull
    public String getTransactionId() {
        return transactionId;
    }

    public String getAccountId() {
        return accountId;
    }

    public DateTime getBookingDate() {
        return bookingDate;
    }

    public DateTime getValueDate() {
        return valueDate;
    }

    public TransactionAmountBcr getTransactionAmount() {
        return transactionAmount;
    }

    public String getDebtorAccount() {
        return debtorAccount;
    }

    public String getCreditorAccount() {
        return creditorAccount;
    }

    public String getDebtorName() {
        return debtorName;
    }

    public String getCreditorName() {
        return creditorName;
    }

    public String getStatus() {
        return status;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getTransactionCategory() {
        return transactionCategory;
    }

    public static Parcelable.Creator<Transaction> getCREATOR() {
        return CREATOR;
    }

    public String getDetails() {
        return details;
    }

    public String getBank() {
        return bank;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
