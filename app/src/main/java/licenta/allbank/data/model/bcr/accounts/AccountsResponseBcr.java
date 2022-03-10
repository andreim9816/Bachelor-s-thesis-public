package licenta.allbank.data.model.bcr.accounts;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AccountsResponseBcr {
    @SerializedName("accounts")
    private List<AccountDetailsBcr> accountDetailsBcrs;

    public List<AccountDetailsBcr> getAccountDetailsBcrs() {
        return accountDetailsBcrs;
    }

    public void setAccountDetailsBcrs(List<AccountDetailsBcr> accountDetailsBcrs) {
        this.accountDetailsBcrs = accountDetailsBcrs;
    }
}
