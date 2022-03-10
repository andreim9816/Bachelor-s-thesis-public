package licenta.allbank.data.model.ing.accounts;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AccountsResponseIng {
    @SerializedName("accounts")
    private List<AccountDetailsIng> accountDetailsList;

    public List<AccountDetailsIng> getAccountDetailsList() {
        return accountDetailsList;
    }
}
