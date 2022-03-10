package licenta.allbank.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import org.joda.time.DateTime;

import java.util.List;

import licenta.allbank.data.model.database.Account;

@Dao
public interface AccountDao {
    @Transaction
    @Query("SELECT * " +
            "FROM accounts_table")
    LiveData<List<Account>> getAllAccounts();

    /**
     * Function that calculates total balance of the enabled accounts
     */
    @Query("SELECT ABS(COALESCE(SUM(balance),0)) " +
            "FROM accounts_table " +
            "WHERE enabled = 1")
    LiveData<Float> getTotalBalance();

    /**
     * @param startDate
     * @param endDate
     * @return Sum of expenses between two dates
     */
    @Query("SELECT ABS(COALESCE(SUM(amount), 0)) " +
            "FROM transactions_table " +
            "WHERE enabled = 1 AND bookingDate >= :startDate AND bookingDate <= :endDate AND amount < 0 " +
            "AND accountId IN (SELECT accountId FROM accounts_table WHERE enabled = 1)"
    )
    LiveData<Float> getExpensesBetweenDates(DateTime startDate, DateTime endDate);

    /**
     * @param startDate
     * @param endDate
     * @return Sum of income between two dates
     */
    @Query("SELECT COALESCE(SUM(amount), 0) " +
            "FROM transactions_table " +
            "WHERE enabled = 1 AND bookingDate >= :startDate AND bookingDate <= :endDate AND amount > 0 " +
            "AND accountId IN (SELECT accountId FROM accounts_table WHERE enabled = 1)"
    )
    LiveData<Float> getIncomeBetweenDates(DateTime startDate, DateTime endDate);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Account account);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAccounts(List<Account> accountList);

    @Transaction
    @Insert
    void insertAccountWithTransactions(Account account, List<licenta.allbank.data.model.database.Transaction> transactions);

    @Update()
    void update(Account account);

    @Query("UPDATE accounts_table " +
            "SET balance = balance + :amount " +
            "WHERE iban = :iban")
    void updateBalance(String iban, float amount);

    @Query("DELETE FROM accounts_table")
    void deleteDatabase();
}
