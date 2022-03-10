package licenta.allbank.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import org.joda.time.DateTime;

import java.util.List;

import licenta.allbank.data.dao.AccountDao;
import licenta.allbank.data.database.AccountsRoomDatabase;
import licenta.allbank.data.model.database.Account;
import licenta.allbank.utils.DateFormatGMT;

public class AccountRepository {
    private AccountDao accountDao;
    private LiveData<List<Account>> accountList;

    public AccountRepository(Application application) {
        AccountsRoomDatabase accountsRoomDatabase = AccountsRoomDatabase.getDatabase(application);
        accountDao = accountsRoomDatabase.accountDao();
        accountList = accountDao.getAllAccounts();
    }

    public LiveData<List<Account>> getAllAccounts() {
        return accountList;
    }

    public LiveData<Float> getTotalBalance() {
        return accountDao.getTotalBalance();
    }

    /**
     * @return Last month total expenses (of enabled accounts)
     */
    public LiveData<Float> getLastMonthExpenses() {
        DateTime startDate = DateFormatGMT.getDatePastDays(30);
        DateTime endDate = DateFormatGMT.getTodayDate();
        return accountDao.getExpensesBetweenDates(startDate, endDate);
    }

    /**
     * @return Last month total income (of enabled accounts)
     */
    public LiveData<Float> getLastMonthIncome() {
        DateTime startDate = DateFormatGMT.getDatePastDays(30);
        DateTime endDate = DateFormatGMT.getTodayDate();
        return accountDao.getIncomeBetweenDates(startDate, endDate);
    }


    public void insert(Account account) {
        AccountsRoomDatabase.getDatabaseWriteExecutor().execute(() -> accountDao.insert(account));
    }

    public void insertAccounts(List<Account> accountList) {
        AccountsRoomDatabase.getDatabaseWriteExecutor().execute(() -> accountDao.insertAccounts(accountList));
    }

    public void update(Account account) {
        AccountsRoomDatabase.getDatabaseWriteExecutor().execute(() -> accountDao.update(account));
    }

    public void updateBalance(String iban, float amount) {
        AccountsRoomDatabase.getDatabaseWriteExecutor().execute(() -> accountDao.updateBalance(iban, amount));
    }

    public void deleteDatabase() {
        AccountsRoomDatabase.getDatabaseWriteExecutor().execute(() -> accountDao.deleteDatabase());
    }
}