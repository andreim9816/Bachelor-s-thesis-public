package licenta.allbank.data.model.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "accounts_table")
public class Account {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    private String accountId;
    private String iban;
    private String bank;
    private String name;
    private String details;
    private String currency;
    private float balance;

    private boolean enabled;

    public Account(@NonNull String accountId, String iban, String bank, String name, String details, String currency, float balance, boolean enabled) {
        this.accountId = accountId;
        this.iban = iban;
        this.bank = bank;
        this.name = name;
        this.details = details;
        this.currency = currency;
        this.balance = balance;
        this.enabled = enabled;
    }

    @NonNull
    public String getAccountId() {
        return accountId;
    }

    public String getIban() {
        return iban;
    }

    public String getBank() {
        return bank;
    }

    public String getName() {
        return name;
    }

    public String getDetails() {
        return details;
    }

    public String getCurrency() {
        return currency;
    }

    public float getBalance() {
        return balance;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
