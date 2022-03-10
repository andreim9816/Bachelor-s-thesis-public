package licenta.allbank.ui.statistics;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.joda.time.DateTime;

import java.util.List;

import licenta.allbank.data.model.database.Budget;
import licenta.allbank.data.repository.BudgetRepository;
import licenta.allbank.data.repository.TransactionRepository;

public class StatisticsBudgetingViewModel extends AndroidViewModel {
    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;

    public StatisticsBudgetingViewModel(@NonNull Application application) {
        super(application);
        budgetRepository = new BudgetRepository(application);
        transactionRepository = new TransactionRepository(application);
    }

    public LiveData<List<Budget>> getCurrentBudgets() {
        return budgetRepository.getCurrentBudgets();
    }

    /**
     * @param transactionCategoryList list of the budget categories
     * @return list of all filtered budgets
     */
    public LiveData<List<Budget>> getFilteredBudgets(List<String> transactionCategoryList) {
        return budgetRepository.getFilteredBudgets(transactionCategoryList);
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

    public void insert(Budget budget) {
        budgetRepository.insert(budget);
    }

    public void update(Budget budget) {
        budgetRepository.update(budget);
    }

    public void deleteDatabase() {
        budgetRepository.deleteDatabase();
    }
}
