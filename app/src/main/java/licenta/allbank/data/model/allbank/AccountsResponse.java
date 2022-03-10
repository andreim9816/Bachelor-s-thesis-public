package licenta.allbank.data.model.allbank;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import licenta.allbank.data.model.allbank.others.BudgetResponse;
import licenta.allbank.data.model.bcr.accounts.AccountDetailsBcr;
import licenta.allbank.data.model.bt.accounts.AccountDetailsBt;

public class AccountsResponse {
    @SerializedName("bcrAccounts")
    private List<AccountDetailsBcr> accountDetailsBcrList;
    @SerializedName("btAccounts")
    private List<AccountDetailsBt> accountDetailsBtList;
    @SerializedName("budgets")
    private List<BudgetResponse> budgets;
    @SerializedName("user")
    private UserDataResponse userDataResponse;

    public List<AccountDetailsBcr> getAccountDetailsBcrList() {
        return accountDetailsBcrList;
    }

    public List<AccountDetailsBt> getAccountDetailsBtList() {
        return accountDetailsBtList;
    }

    public List<BudgetResponse> getBudgets() {
        return budgets;
    }

    public UserDataResponse getUserDataResponse() {
        return userDataResponse;
    }
}
