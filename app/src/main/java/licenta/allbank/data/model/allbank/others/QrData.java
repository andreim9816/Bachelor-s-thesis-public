package licenta.allbank.data.model.allbank.others;

import android.os.Parcel;
import android.os.Parcelable;

public class QrData implements Parcelable {
    private String creditorIban;
    private final String currency;
    private final String details;
    private final String firstName;
    private final String lastName;
    private final float amount;

    public QrData(String creditorIban, String currency, String details, String firstName, String lastName, float amount) {
        this.creditorIban = creditorIban;
        this.currency = currency;
        this.details = details;
        this.firstName = firstName;
        this.lastName = lastName;
        this.amount = amount;
    }

    public QrData(String currency, String details, String firstName, String lastName, float amount) {
        this.currency = currency;
        this.details = details;
        this.firstName = firstName;
        this.lastName = lastName;
        this.amount = amount;
    }

    public String getCreditorIban() {
        return creditorIban;
    }

    public String getCurrency() {
        return currency;
    }

    public String getDetails() {
        return details;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public float getAmount() {
        return amount;
    }

    public void setCreditorIban(String creditorIban) {
        this.creditorIban = creditorIban;
    }

    public static final Parcelable.Creator<QrData> CREATOR = new Parcelable.Creator<QrData>() {
        @Override
        public QrData createFromParcel(Parcel in) {
            return new QrData(in);
        }

        @Override
        public QrData[] newArray(int size) {
            return new QrData[size];
        }
    };

    public static Parcelable.Creator<QrData> getCREATOR() {
        return CREATOR;
    }

    public QrData(Parcel in) {
        this.creditorIban = in.readString();
        this.currency = in.readString();
        this.details = in.readString();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.amount = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(creditorIban);
        dest.writeString(currency);
        dest.writeString(details);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeFloat(amount);
    }
}
