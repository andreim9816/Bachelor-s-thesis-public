package licenta.allbank.data.model.database;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

@Entity(tableName = "budgets_table")
public class Budget implements Parcelable {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @SerializedName("id")
    private int budgetId;

    private String category;

    @TypeConverters(DateConverter.class)
    private DateTime startDate;

    @TypeConverters(DateConverter.class)
    private DateTime endDate;

    private float budget;

    private float spent;

    public Budget(int budgetId, DateTime startDate, DateTime endDate, float budget, String category) {
        this.budgetId = budgetId;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budget = budget;
        this.spent = 0f;
    }

    public void setBudgetId(int id) {
        this.budgetId = id;
    }

    public int getBudgetId() {
        return budgetId;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    public float getBudget() {
        return budget;
    }

    public void setBudget(float budget) {
        this.budget = budget;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getSpent() {
        return spent;
    }

    public void setSpent(float spent) {
        this.spent = spent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(budgetId);
        dest.writeString(category);
        dest.writeLong(startDate.getMillis());
        dest.writeLong(endDate.getMillis());
        dest.writeFloat(budget);
        dest.writeFloat(spent);
    }

    public static final Parcelable.Creator<Budget> CREATOR = new Parcelable.Creator<Budget>() {
        @Override
        public Budget createFromParcel(Parcel in) {
            return new Budget(in);
        }

        @Override
        public Budget[] newArray(int size) {
            return new Budget[size];
        }
    };

    protected Budget(Parcel in) {
        budgetId = in.readInt();
        category = in.readString();
        startDate = new DateTime(in.readLong());//(DateTime) in.readValue(DateTime.class.getClassLoader());
        endDate = new DateTime(in.readLong());//(DateTime) in.readValue(DateTime.class.getClassLoader());
        budget = in.readFloat();
        spent = in.readFloat();
    }
}
