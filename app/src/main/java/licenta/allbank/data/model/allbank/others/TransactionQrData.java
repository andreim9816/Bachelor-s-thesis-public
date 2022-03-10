package licenta.allbank.data.model.allbank.others;

import android.os.Parcel;
import android.os.Parcelable;

public class TransactionQrData implements Parcelable {
    private float amount;
    private String currency;
    private String details;

    public TransactionQrData(float amount, String currency, String details) {
        this.amount = amount;
        this.currency = currency;
        this.details = details;
    }

    protected TransactionQrData(Parcel in) {
        amount = in.readFloat();
        currency = in.readString();
        details = in.readString();
    }

    public static final Creator<TransactionQrData> CREATOR = new Creator<TransactionQrData>() {
        @Override
        public TransactionQrData createFromParcel(Parcel in) {
            return new TransactionQrData(in);
        }

        @Override
        public TransactionQrData[] newArray(int size) {
            return new TransactionQrData[size];
        }
    };

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(amount);
        dest.writeString(currency);
        dest.writeString(details);
    }
}
