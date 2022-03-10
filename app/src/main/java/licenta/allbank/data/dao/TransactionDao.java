package licenta.allbank.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.joda.time.DateTime;

import java.util.List;

import licenta.allbank.data.model.allbank.others.IbanNameSearchItem;
import licenta.allbank.data.model.database.CategoryTypeSum;
import licenta.allbank.data.model.database.Transaction;
import licenta.allbank.data.model.database.TransactionTypeAverageBank;
import licenta.allbank.data.model.database.TransactionTypeDateSum;
import licenta.allbank.data.model.database.TransactionTypeSum;

@Dao
public interface TransactionDao {
    /**
     * Function that returns all the transactions from the enabled accounts. Disabled transactions are still returned!!
     *
     * @return
     */
    @Query("SELECT * " +
            "FROM transactions_table " +
            "WHERE accountId IN (SELECT accountId FROM accounts_table WHERE enabled = 1) " +
            "ORDER BY bookingDate DESC, transactionId DESC, valueDate DESC")
    LiveData<List<Transaction>> getAllTransactions();

    /**
     * Function that returns all the transactions from the enabled accounts between two dates. Disabled transactions are still returned!!
     *
     * @return
     */
    @Query("SELECT * " +
            "FROM transactions_table " +
            "WHERE bookingDate >= :startDate AND bookingDate <= :endDate AND accountId IN (SELECT accountId FROM accounts_table WHERE enabled = 1) " +
            "ORDER BY bookingDate DESC, transactionId DESC, valueDate DESC")
    LiveData<List<Transaction>> getAllTransactions(DateTime startDate, DateTime endDate);

    @Query("SELECT * " +
            "FROM transactions_table " +
            "WHERE enabled = 1 AND bookingDate >= :startDate AND bookingDate <= :endDate " +
            "AND accountId IN (SELECT accountId FROM accounts_table WHERE enabled = 1) " +
            "ORDER BY bookingDate DESC, transactionId DESC,valueDate DESC")
    LiveData<List<Transaction>> getEnabledTransactions(DateTime startDate, DateTime endDate);

    @Query("SELECT * " +
            "FROM transactions_table " +
            "WHERE enabled = 1 AND transactionType = :transactionType " +
            "AND accountId IN (SELECT accountId FROM accounts_table WHERE enabled = 1) " +
            "ORDER BY bookingDate DESC, transactionId DESC,valueDate DESC")
    LiveData<List<Transaction>> getEnabledTransactions(String transactionType);

    @Query("SELECT * " +
            "FROM transactions_table " +
            "WHERE enabled = 1 AND transactionType = :transactionType AND bookingDate >= :startDate AND bookingDate <= :endDate " +
            "AND accountId IN (SELECT accountId FROM accounts_table WHERE enabled = 1) " +
            "ORDER BY bookingDate DESC, transactionId DESC,valueDate DESC")
    LiveData<List<Transaction>> getTransactionsByTransactionType(DateTime startDate, DateTime endDate, String transactionType);

    @Query("SELECT * " +
            "FROM transactions_table " +
            "WHERE enabled = 1 AND bank = :bank AND bookingDate >= :startDate AND bookingDate <= :endDate " +
            "AND accountId IN (SELECT accountId FROM accounts_table WHERE enabled = 1) " +
            "ORDER BY bookingDate DESC, transactionId DESC,valueDate DESC")
    LiveData<List<Transaction>> getTransactionsByBank(DateTime startDate, DateTime endDate, String bank);

    @Query("SELECT * " +
            "FROM transactions_table " +
            "WHERE enabled = 1 AND bank = :bank AND transactionType = :transactionType AND bookingDate >= :startDate AND bookingDate <= :endDate " +
            "AND accountId IN (SELECT accountId FROM accounts_table WHERE enabled = 1) " +
            "ORDER BY bookingDate DESC, transactionId DESC,valueDate DESC")
    LiveData<List<Transaction>> getTransactionsByBankAndTransactionType(DateTime startDate, DateTime endDate, String bank, String transactionType);

    @Query("SELECT transactionType, SUM(amount) sum " +
            "FROM transactions_table " +
            "WHERE enabled = 1 AND bookingDate >= :startDate AND bookingDate <= :endDate " +
            "AND accountId IN (SELECT accountId FROM accounts_table WHERE enabled = 1) " +
            "GROUP BY transactionType")
    LiveData<List<TransactionTypeSum>> getTransactionsTypeSum(DateTime startDate, DateTime endDate);

    @Query("SELECT transactionType, SUM(amount) sum " +
            "FROM transactions_table " +
            "WHERE enabled = 1 AND bank = :bank AND bookingDate >= :startDate AND bookingDate <= :endDate " +
            "AND accountId IN (SELECT accountId FROM accounts_table WHERE enabled = 1) " +
            "GROUP BY transactionType")
    LiveData<List<TransactionTypeSum>> getTransactionsTypeSum(DateTime startDate, DateTime endDate, String bank);

    @Query("SELECT AVG(ABS(amount)) transactionAvg, transactionType, bank " +
            "FROM transactions_table " +
            "WHERE enabled = 1 AND bookingDate >= :startDate AND bookingDate <= :endDate " +
            "AND accountId IN (SELECT accountId FROM accounts_table WHERE enabled = 1) " +
            "GROUP BY transactionType, bank " +
            "ORDER BY bank, transactionType ")
    LiveData<List<TransactionTypeAverageBank>> getAverageTransaction(DateTime startDate, DateTime endDate);

    @Query("SELECT SUM(ABS(amount)) sum, transactionCategory " +
            "FROM transactions_table " +
            "WHERE enabled = 1 AND bookingDate >= :startDate AND bookingDate <= :endDate AND transactionCategory IN (:categoryList) " +
            "AND accountId IN (SELECT accountId FROM accounts_table WHERE enabled = 1) " +
            "GROUP BY transactionCategory " +
            "ORDER BY transactionCategory")
    LiveData<List<CategoryTypeSum>> getTransactionStatisticsByCategory(DateTime startDate, DateTime endDate, List<String> categoryList);

    @Query("SELECT SUM(ABS(amount)) sum, transactionCategory, bookingDate " +
            "FROM transactions_table " +
            "WHERE enabled = 1 AND bookingDate >= :startDate AND bookingDate <= :endDate AND transactionCategory IN (:categoryList) " +
            "AND accountId IN (SELECT accountId FROM accounts_table WHERE enabled = 1) " +
            "GROUP BY transactionCategory, bookingDate " +
            "ORDER BY bookingDate")
    LiveData<List<TransactionTypeDateSum>> getTransactionStatisticsByCategoryAndDate(DateTime startDate, DateTime endDate, List<String> categoryList);

    /**
     * @param startDate    starting date
     * @param endDate      ending date
     * @param categoryList category list of the transactions
     * @return transactions that were made between the given dates and are part of the specified categoriess
     */
    @Query("SELECT * " +
            "FROM transactions_table " +
            "WHERE enabled = 1 AND bookingDate >= :startDate AND bookingDate <= :endDate AND transactionCategory IN (:categoryList)" +
            "AND accountId IN (SELECT accountId FROM accounts_table WHERE enabled = 1) " +
            "ORDER BY bookingDate DESC, transactionId DESC")
    LiveData<List<Transaction>> getTransactionsByCategory(DateTime startDate, DateTime endDate, List<String> categoryList);

    /**
     * @param startDate starting date
     * @param endDate   ending date
     * @param category  category type of transactions
     * @return transactions that were made between the given dates and are of the specified category
     */
    @Query("SELECT * " +
            "FROM transactions_table " +
            "WHERE enabled = 1 AND transactionCategory = :category AND bookingDate >= :startDate AND bookingDate <= :endDate " +
            "AND accountId IN (SELECT accountId FROM accounts_table WHERE enabled = 1) " +
            "ORDER BY bookingDate DESC, transactionId DESC, valueDate DESC")
    LiveData<List<Transaction>> getTransactionsByCategory(DateTime startDate, DateTime endDate, String category);

    /**
     * @param startDate    starting date
     * @param endDate      ending date
     * @param categoryList list of category types
     * @return the sum of absolute values of transactions between two dates and are part of a given category list
     */
    @Query("SELECT COALESCE(SUM(ABS(amount)),0) " +
            "FROM transactions_table " +
            "WHERE enabled = 1 AND bookingDate >= :startDate AND bookingDate <= :endDate AND transactionCategory IN (:categoryList) " +
            "AND accountId IN (SELECT accountId FROM accounts_table WHERE enabled = 1) ")
    LiveData<Float> getTransactionsSumForCategories(DateTime startDate, DateTime endDate, List<String> categoryList);

    /**
     * @param startDate starting date
     * @param endDate   ending date
     * @param category  transactions category
     * @return the sum of all expenses between two dates and which are of a given category
     */
    @Query("SELECT COALESCE(SUM(ABS(amount)), 0) " +
            "FROM transactions_table " +
            "WHERE enabled = 1 AND transactionCategory = :category AND bookingDate >= :startDate AND bookingDate <= :endDate " +
            "AND accountId IN (SELECT accountId FROM accounts_table WHERE enabled = 1) " +
            "ORDER BY bookingDate DESC, transactionId DESC, valueDate DESC")
    LiveData<Float> getTransactionsSumForCategory(DateTime startDate, DateTime endDate, String category);

    /**
     * @param startDate
     * @param endDate
     * @return the sum of all expenses between two dates
     */
    @Query("SELECT COALESCE(SUM(ABS(amount)), 0) " +
            "FROM transactions_table " +
            "WHERE enabled = 1 AND bookingDate >= :startDate AND bookingDate <= :endDate " +
            "AND accountId IN (SELECT accountId FROM accounts_table WHERE enabled = 1) " +
            "AND amount < 0"
    )
    LiveData<Float> getTotalSpentBetweenDates(DateTime startDate, DateTime endDate);

    /**
     * Queries transactions database and returns <code>IbanNameSearchItem</code> objects by looking at
     * user's iban transactions history
     *
     * @param query Query string that is being searched in database
     * @return List of  <code>IbanNameSearchItem</code> objects that were filtered
     */
    @Query("SELECT DISTINCT(creditorAccount) iban, creditorName name " +
            "FROM transactions_table " +
            "WHERE enabled = 1 AND transactionType = 'EXPENSE' AND  iban LIKE :query " +
            "AND accountId IN (SELECT accountId FROM accounts_table WHERE enabled = 1)"
    )
    LiveData<List<IbanNameSearchItem>> filterTransactionsDatabaseByIbanQuery(String query);

    /**
     * Queries transactions database and returns <code>IbanNameSearchItem</code> objects by looking at
     * user's creditor transactions history
     *
     * @param query Query string that is being searched in database
     * @return List of  <code>IbanNameSearchItem</code> objects that were filtered
     */
    @Query("SELECT DISTINCT(creditorAccount) iban, creditorName name " +
            "FROM transactions_table " +
            "WHERE enabled = 1 AND transactionType = 'EXPENSE' AND  creditorName LIKE :query " +
            "AND accountId IN (SELECT accountId FROM accounts_table WHERE enabled = 1)")
    LiveData<List<IbanNameSearchItem>> filterTransactionsDatabaseByNameQuery(String query);

    /**
     * Function that updates a transaction state
     *
     * @param transactionId
     * @param enabled
     */
    @Query("UPDATE transactions_table " +
            "SET enabled = :enabled " +
            "WHERE transactionId = :transactionId")
    void updateTransactionState(String transactionId, boolean enabled);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Transaction transaction);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTransactions(List<Transaction> transactionList);

    @Update()
    void update(Transaction transaction);

    @Query("DELETE FROM transactions_table")
    void deleteDatabase();
}
