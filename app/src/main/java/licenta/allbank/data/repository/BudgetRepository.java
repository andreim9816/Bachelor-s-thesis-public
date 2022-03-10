package licenta.allbank.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import org.joda.time.DateTime;

import java.util.List;

import licenta.allbank.data.dao.BudgetDao;
import licenta.allbank.data.database.AccountsRoomDatabase;
import licenta.allbank.data.model.database.Budget;
import licenta.allbank.utils.DateFormatGMT;

public class BudgetRepository {
    private final BudgetDao budgetDao;
    private final AccountsRoomDatabase accountsRoomDatabase;
    private LiveData<List<Budget>> budgetList;

    public BudgetRepository(Application application) {
        DateTime now = DateFormatGMT.getTodayDate();
        accountsRoomDatabase = AccountsRoomDatabase.getDatabase(application);
        budgetDao = accountsRoomDatabase.budgetDao();
        budgetList = budgetDao.getCurrentBudgets(now);
    }

    public LiveData<List<Budget>> getCurrentBudgets() {
        return budgetList;
    }

    /**
     * @param transactionCategoryList list of the budget categories
     * @return list of all filtered budgets
     */
    public LiveData<List<Budget>> getFilteredBudgets(List<String> transactionCategoryList) {
        DateTime now = DateFormatGMT.getTodayDate();
        return accountsRoomDatabase.budgetDao().getFilteredBudgets(now, transactionCategoryList);
    }

    /**
     * Add a specific sum to all the budgets from a specific category and their interval corresponds to today's date
     *
     * @param category budget category that is being updated
     * @param sum      sum that is being added to a budget
     */
    public void addTransactionToBudgetCategory(String category, float sum) {
        DateTime now = DateFormatGMT.getTodayDate();
        AccountsRoomDatabase.getDatabaseWriteExecutor().execute(() -> budgetDao.addTransactionToBudgetCategory(category, sum, now));
    }

    public void insertBudgets(List<Budget> budgetList) {
        AccountsRoomDatabase.getDatabaseWriteExecutor().execute(() -> budgetDao.insertBudgets(budgetList));
    }

    public void insert(Budget budget) {
        AccountsRoomDatabase.getDatabaseWriteExecutor().execute(() -> budgetDao.insert(budget));
    }

    public void update(Budget budget) {
        AccountsRoomDatabase.getDatabaseWriteExecutor().execute(() -> budgetDao.update(budget));
    }

    public void deleteDatabase() {
        AccountsRoomDatabase.getDatabaseWriteExecutor().execute(budgetDao::deleteDatabase);
    }
}
