package licenta.allbank.ui.profile.accounts;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import licenta.allbank.data.model.database.Account;
import licenta.allbank.data.repository.AccountRepository;

public class AccountsViewModel extends AndroidViewModel {
    private final AccountRepository accountRepository;
    private LiveData<List<Account>> allAccounts;

    public AccountsViewModel(@NonNull Application application) {
        super(application);
        accountRepository = new AccountRepository(application);
        allAccounts = accountRepository.getAllAccounts();
    }

    public LiveData<List<Account>> getAllAccounts() {
        return allAccounts;
    }

    public void update(Account account) {
        accountRepository.update(account);
    }

    public void updateBalance(String iban, float amount) {
        accountRepository.updateBalance(iban, amount);
    }
}
