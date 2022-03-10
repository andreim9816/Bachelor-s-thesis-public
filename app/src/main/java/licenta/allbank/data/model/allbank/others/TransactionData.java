package licenta.allbank.data.model.allbank.others;

import android.os.Parcel;
import android.os.Parcelable;

public class TransactionData implements Parcelable {
    private String details;
    private String creditorName;
    private String creditorIban;
    private String currency;
    private float amount;
    private String myIban;

    public TransactionData(String details, String creditorIban, String creditorName, String currency, float amount) {
        this.details = details;
        this.creditorName = creditorName;
        this.creditorIban = creditorIban;
        this.currency = currency;
        this.amount = amount;
    }

    protected TransactionData(Parcel in) {
        details = in.readString();
        creditorName = in.readString();
        creditorIban = in.readString();
        currency = in.readString();
        amount = in.readFloat();
    }

    public static final Creator<TransactionData> CREATOR = new Creator<TransactionData>() {
        @Override
        public TransactionData createFromParcel(Parcel in) {
            return new TransactionData(in);
        }

        @Override
        public TransactionData[] newArray(int size) {
            return new TransactionData[size];
        }
    };

    public void setMyIban(String iban) {
        this.myIban = iban;
    }

    public String getDetails() {
        return details;
    }

    public String getCreditorName() {
        return creditorName;
    }

    public String getCurrency() {
        return currency;
    }

    public float getAmount() {
        return amount;
    }

    public String getCreditorIban() {
        return creditorIban;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(details);
        dest.writeString(creditorName);
        dest.writeString(creditorIban);
        dest.writeString(currency);
        dest.writeFloat(amount);
    }
}
