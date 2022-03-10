package licenta.allbank.ui.statistics;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.joda.time.DateTime;

import java.util.List;

import licenta.allbank.data.model.database.CategoryTypeSum;
import licenta.allbank.data.model.database.Transaction;
import licenta.allbank.data.model.database.TransactionTypeDateSum;
import licenta.allbank.data.repository.BudgetRepository;
import licenta.allbank.data.repository.TransactionRepository;

public class StatisticsViewModel extends AndroidViewModel {
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private LiveData<List<Transaction>> allTransactions;

    public StatisticsViewModel(@NonNull Application application) {
        super(application);
        transactionRepository = new TransactionRepository(application);
        budgetRepository = new BudgetRepository(application);
        allTransactions = transactionRepository.getAllTransactions();
    }

    public LiveData<List<Transaction>> getAllTransactions() {
        return allTransactions;
    }

    public LiveData<List<CategoryTypeSum>> getTransactionStatisticsByCategory(DateTime startDate, DateTime endDate, List<String> categoryList) {
        return transactionRepository.getTransactionStatisticsByCategory(startDate, endDate, categoryList);
    }

    public LiveData<List<Transaction>> getTransactionsByTransactionType(DateTime startDate, DateTime endDate, String transactionType) {
        return transactionRepository.getTransactionsByTransactionType(startDate, endDate, transactionType);
    }

    public LiveData<List<TransactionTypeDateSum>> getTransactionStatisticsByCategoryAndDate(DateTime startDate, DateTime endDate, List<String> categoryList) {
        return transactionRepository.getTransactionStatisticsByCategoryAndDate(startDate, endDate, categoryList);
    }

    public LiveData<List<Transaction>> getTransactionsByCategory(DateTime startDate, DateTime endDate, List<String> categoryList) {
        return transactionRepository.getTransactionsByCategory(startDate, endDate, categoryList);
    }

    public LiveData<List<Transaction>> getTransactionsByCategory(DateTime startDate, DateTime endDate, String category) {
        return transactionRepository.getTransactionsByCategory(startDate, endDate, category);
    }

    /**
     * @param startDate starting date
     * @param endDate   ending date
     * @param category  transactions category
     * @return the sum of all expenses of a given category between two dates
     */
    public LiveData<Float> getSpentOnCategoryBetweenDates(DateTime startDate, DateTime endDate, String category) {
        return transactionRepository.getTransactionsSumForCategory(startDate, endDate, category);
    }

    /**
     * Add a specific sum to all the budgets from a specific category and their interval corresponds to today's date
     *
     * @param category budget category that is being updated
     * @param sum      sum that is being added to a budget
     */
    public void addTransactionToBudgetCategory(String category, float sum) {
        budgetRepository.addTransactionToBudgetCategory(category, sum);
    }

    /**
     * @param startDate    starting date
     * @param endDate      ending date
     * @param categoryList list of category types
     * @return the sum of absolute values of transactions between two dates and are part of a given category list
     */
    public LiveData<Float> getTransactionsSumForCategories(DateTime startDate, DateTime endDate, List<String> categoryList) {
        return transactionRepository.getTransactionsSumForCategories(startDate, endDate, categoryList);
    }

    /**
     * @param startDate starting date
     * @param endDate   ending date
     * @return the sum of all expenses between two dates
     */
    public LiveData<Float> getTotalSpentBetweenDates(DateTime startDate, DateTime endDate) {
        return transactionRepository.getTotalSpentBetweenDates(startDate, endDate);
    }
}