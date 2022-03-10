package licenta.allbank.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import org.joda.time.DateTime;

import java.util.List;

import licenta.allbank.data.dao.TransactionDao;
import licenta.allbank.data.database.AccountsRoomDatabase;
import licenta.allbank.data.model.allbank.others.IbanNameSearchItem;
import licenta.allbank.data.model.database.CategoryTypeSum;
import licenta.allbank.data.model.database.Transaction;
import licenta.allbank.data.model.database.TransactionTypeAverageBank;
import licenta.allbank.data.model.database.TransactionTypeDateSum;
import licenta.allbank.data.model.database.TransactionTypeSum;

public class TransactionRepository {
    private final TransactionDao transactionDao;
    private LiveData<List<Transaction>> transactionList;
    private final AccountsRoomDatabase accountsRoomDatabase;

    public TransactionRepository(Application application) {
        accountsRoomDatabase = AccountsRoomDatabase.getDatabase(application);
        transactionDao = accountsRoomDatabase.transactionDao();
        transactionList = transactionDao.getAllTransactions();
    }

    public LiveData<List<Transaction>> getAllTransactions() {
        return transactionList;
    }

    public LiveData<List<Transaction>> getAllTransactions(DateTime startDate, DateTime endDate) {
        return transactionDao.getAllTransactions(startDate, endDate);
    }

    public LiveData<List<Transaction>> getTransactionsByTransactionType(DateTime startDate, DateTime endDate, String transactionType) {
        return transactionDao.getTransactionsByTransactionType(startDate, endDate, transactionType);
    }

    public LiveData<List<Transaction>> getTransactionsByBank(DateTime startDate, DateTime endDate, String bank) {
        return transactionDao.getTransactionsByBank(startDate, endDate, bank);
    }

    public LiveData<List<Transaction>> getTransactionsByBankAndTransactionType(DateTime startDate, DateTime endDate, String bank, String transactionType) {
        return transactionDao.getTransactionsByBankAndTransactionType(startDate, endDate, bank, transactionType);
    }

    public LiveData<List<Transaction>> getTransactions(DateTime startDate, DateTime endDate) {
        return transactionDao.getEnabledTransactions(startDate, endDate);
    }

    public LiveData<List<Transaction>> getTransactions(String transactionType) {
        return transactionDao.getEnabledTransactions(transactionType);
    }

    public LiveData<List<TransactionTypeSum>> getTransactionsTypeSum(DateTime startDate, DateTime endDate) {
        return transactionDao.getTransactionsTypeSum(startDate, endDate);
    }

    public LiveData<List<TransactionTypeSum>> getTransactionsTypeSum(DateTime startDate, DateTime endDate, String bank) {
        return transactionDao.getTransactionsTypeSum(startDate, endDate, bank);
    }

    public LiveData<List<TransactionTypeAverageBank>> getAverageTransaction(DateTime startDate, DateTime endDate) {
        return transactionDao.getAverageTransaction(startDate, endDate);
    }

    public LiveData<List<CategoryTypeSum>> getTransactionStatisticsByCategory(DateTime startDate, DateTime endDate, List<String> categoryList) {
        return transactionDao.getTransactionStatisticsByCategory(startDate, endDate, categoryList);
    }

    public LiveData<List<TransactionTypeDateSum>> getTransactionStatisticsByCategoryAndDate(DateTime startDate, DateTime endDate, List<String> categoryList) {
        return transactionDao.getTransactionStatisticsByCategoryAndDate(startDate, endDate, categoryList);
    }

    public LiveData<List<Transaction>> getTransactionsByCategory(DateTime startDate, DateTime endDate, List<String> categoryList) {
        return transactionDao.getTransactionsByCategory(startDate, endDate, categoryList);
    }

    public LiveData<List<Transaction>> getTransactionsByCategory(DateTime startDate, DateTime endDate, String category) {
        return transactionDao.getTransactionsByCategory(startDate, endDate, category);
    }

    /**
     * Queries transactions database and returns <code>IbanNameSearchItem</code> objects by looking at
     * user's iban transactions history
     *
     * @param query Query string that is being searched in database
     * @return List of <code>IbanNameSearchItem</code> objects that were filtered
     */
    public LiveData<List<IbanNameSearchItem>> filterTransactionsDatabaseByIbanQuery(String query) {
        String uppercaseQuery = '%' + query.toUpperCase() + '%';
        return transactionDao.filterTransactionsDatabaseByIbanQuery(uppercaseQuery);
    }

    /**
     * Queries transactions database and returns <code>IbanNameSearchItem</code> objects by looking at
     * user's creditors transactions history
     *
     * @param query Query string that is being searched in database
     * @return List of <code>IbanNameSearchItem</code> objects that were filtered
     */
    public LiveData<List<IbanNameSearchItem>> filterTransactionsDatabaseByNameQuery(String query) {
        String uppercaseQuery = '%' + query.toUpperCase() + '%';
        return transactionDao.filterTransactionsDatabaseByNameQuery(uppercaseQuery);
    }

    /**
     * @param startDate    starting date
     * @param endDate      ending date
     * @param categoryList list of category types
     * @return the sum of absolute values of transactions between two dates and are part of a given category list
     */
    public LiveData<Float> getTransactionsSumForCategories(DateTime startDate, DateTime endDate, List<String> categoryList) {
        return transactionDao.getTransactionsSumForCategories(startDate, endDate, categoryList);
    }

    /**
     * @param startDate starting date
     * @param endDate   ending date
     * @param category  transactions category
     * @return the sum of all expenses between two dates and which are of a given category
     */
    public LiveData<Float> getTransactionsSumForCategory(DateTime startDate, DateTime endDate, String category) {
        return transactionDao.getTransactionsSumForCategory(startDate, endDate, category);
    }

    /**
     * @param startDate starting date
     * @param endDate   ending date
     * @return the sum of all expenses between two dates
     */
    public LiveData<Float> getTotalSpentBetweenDates(DateTime startDate, DateTime endDate) {
        return transactionDao.getTotalSpentBetweenDates(startDate, endDate);
    }


    /**
     * Function that updates a transaction state
     *
     * @param transactionId
     * @param enabled
     */
    public void updateTransactionState(String transactionId, boolean enabled) {
        AccountsRoomDatabase.getDatabaseWriteExecutor().execute(() -> transactionDao.updateTransactionState(transactionId, enabled));
    }

    public void insert(Transaction transaction) {
        AccountsRoomDatabase.getDatabaseWriteExecutor().execute(() -> transactionDao.insert(transaction));
    }

    public void insertTransactions(List<Transaction> transactionList) {
        AccountsRoomDatabase.getDatabaseWriteExecutor().execute(() -> transactionDao.insertTransactions(transactionList));
    }

    public void update(Transaction transaction) {
        AccountsRoomDatabase.getDatabaseWriteExecutor().execute(() -> transactionDao.update(transaction));
    }

    public void deleteDatabase() {
        AccountsRoomDatabase.getDatabaseWriteExecutor().execute(transactionDao::deleteDatabase);
    }
}
