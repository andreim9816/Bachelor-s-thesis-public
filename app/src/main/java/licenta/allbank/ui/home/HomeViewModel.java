package licenta.allbank.ui.home;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.joda.time.DateTime;

import java.util.List;

import licenta.allbank.data.model.database.Account;
import licenta.allbank.data.model.database.Budget;
import licenta.allbank.data.model.database.Transaction;
import licenta.allbank.data.model.database.TransactionTypeAverageBank;
import licenta.allbank.data.repository.AccountRepository;
import licenta.allbank.data.repository.BudgetRepository;
import licenta.allbank.data.repository.TransactionRepository;

public class HomeViewModel extends AndroidViewModel {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final BudgetRepository budgetRepository;
    private LiveData<List<Transaction>> allTransactions;

    public HomeViewModel(Application application) {
        super(application);
        transactionRepository = new TransactionRepository(application);
        accountRepository = new AccountRepository(application);
        budgetRepository = new BudgetRepository(application);
        allTransactions = transactionRepository.getAllTransactions();
    }

    public LiveData<List<Transaction>> getAllTransactions() {
        return allTransactions;
    }

    public LiveData<List<Transaction>> getTransactionsByTransactionType(DateTime startDate, DateTime endDate, String transactionType) {
        return transactionRepository.getTransactionsByTransactionType(startDate, endDate, transactionType);
    }

    public LiveData<List<Transaction>> getTransactionsByBank(DateTime startDate, DateTime endDate, String bank) {
        return transactionRepository.getTransactionsByBank(startDate, endDate, bank);
    }

    public LiveData<List<Transaction>> getTransactionsByBankAndTransactionType(DateTime startDate, DateTime endDate, String bank, String transactionType) {
        return transactionRepository.getTransactionsByBankAndTransactionType(startDate, endDate, bank, transactionType);
    }

    public LiveData<List<TransactionTypeAverageBank>> getAverageTransaction(DateTime startDate, DateTime endDate) {
        return transactionRepository.getAverageTransaction(startDate, endDate);
    }

    public LiveData<List<Transaction>> getTransactions(DateTime startDate, DateTime endDate) {
        return transactionRepository.getTransactions(startDate, endDate);
    }

    public LiveData<List<Transaction>> getTransactions(String transactionType) {
        return transactionRepository.getTransactions(transactionType);
    }

    /**
     * @return total balance of the enabled accounts
     */
    public LiveData<Float> getTotalBalance() {
        return accountRepository.getTotalBalance();
    }

    /**
     * @return Last month total expenses (of enabled accounts)
     */
    public LiveData<Float> getLastMonthExpenses() {
        return accountRepository.getLastMonthExpenses();
    }

    /**
     * @return Last month total income (of enabled accounts)
     */
    public LiveData<Float> getLastMonthIncome() {
        return accountRepository.getLastMonthIncome();
    }

    /**
     * @param startDate starting date
     * @param endDate   ending date
     * @param category  transactions category
     * @return the sum of all expenses between two dates and which are of a given category
     */
    public LiveData<Float> getTransactionsSumForCategory(DateTime startDate, DateTime endDate, String category) {
        return transactionRepository.getTransactionsSumForCategory(startDate, endDate, category);
    }

    /**
     * Function that updates a transaction state
     *
     * @param transactionId
     * @param enabled
     */
    public void updateTransactionState(String transactionId, boolean enabled) {
        transactionRepository.updateTransactionState(transactionId, enabled);
    }

    public void insert(Transaction transaction) {
        transactionRepository.insert(transaction);
    }

    public void insert(Account account) {
        accountRepository.insert(account);
    }

    public void update(Budget budget) {
        budgetRepository.update(budget);
    }

    public void insertAccountsBudgetsTransactions(List<Account> accountList, List<Transaction> transactionList, List<Budget> budgetList) {
        accountRepository.insertAccounts(accountList);
        transactionRepository.insertTransactions(transactionList);
        budgetRepository.insertBudgets(budgetList);
    }

    public void deleteDatabase() {
        accountRepository.deleteDatabase();
        transactionRepository.deleteDatabase();
        budgetRepository.deleteDatabase();
    }
}