package licenta.allbank.data.model.allbank.others;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

public class TransactionDetails implements Parcelable {
    private String transactionId;
    private String ibanFrom;
    private String ibanTo;
    private String nameFrom;
    private String nameTo;
    private String details;
    private String bank;
    private DateTime date;
    private float amount;
    private String currency;
    private String category;
    private boolean enabled;

    public TransactionDetails(String transactionId, String ibanFrom, String ibanTo, String nameFrom, String nameTo, String details, String bank, DateTime date, float amount, String currency, String category, boolean enabled) {
        this.transactionId = transactionId;
        this.ibanFrom = ibanFrom;
        this.ibanTo = ibanTo;
        this.nameFrom = nameFrom;
        this.nameTo = nameTo;
        this.details = details;
        this.bank = bank;
        this.date = date;
        this.amount = amount;
        this.currency = currency;
        this.category = category;
        this.enabled = enabled;
    }

    protected TransactionDetails(Parcel in) {
        transactionId = in.readString();
        ibanFrom = in.readString();
        ibanTo = in.readString();
        nameFrom = in.readString();
        nameTo = in.readString();
        details = in.readString();
        bank = in.readString();
        amount = in.readFloat();
        currency = in.readString();
        category = in.readString();
        enabled = in.readBoolean();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(transactionId);
        dest.writeString(ibanFrom);
        dest.writeString(ibanTo);
        dest.writeString(nameFrom);
        dest.writeString(nameTo);
        dest.writeString(details);
        dest.writeString(bank);
        dest.writeFloat(amount);
        dest.writeString(currency);
        dest.writeString(category);
        dest.writeBoolean(enabled);
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getIbanFrom() {
        return ibanFrom;
    }

    public String getIbanTo() {
        return ibanTo;
    }

    public String getNameFrom() {
        return nameFrom;
    }

    public String getNameTo() {
        return nameTo;
    }

    public String getDetails() {
        return details;
    }

    public String getBank() {
        return bank;
    }

    public DateTime getDate() {
        return date;
    }

    public float getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCategory() {
        return category;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public static Creator<TransactionDetails> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TransactionDetails> CREATOR = new Creator<TransactionDetails>() {
        @Override
        public TransactionDetails createFromParcel(Parcel in) {
            return new TransactionDetails(in);
        }

        @Override
        public TransactionDetails[] newArray(int size) {
            return new TransactionDetails[size];
        }
    };
}
