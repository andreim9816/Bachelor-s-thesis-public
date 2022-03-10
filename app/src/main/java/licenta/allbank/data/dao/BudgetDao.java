package licenta.allbank.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.joda.time.DateTime;

import java.util.List;

import licenta.allbank.data.model.database.Budget;

@Dao
public interface BudgetDao {
    /**
     * @return current budgets
     */
    @Query("SELECT * " +
            "FROM budgets_table " +
            "WHERE :now <= endDate")
    LiveData<List<Budget>> getCurrentBudgets(DateTime now);

    /**
     * @param transactionCategoryList list of the budget categories
     * @return list of all current filtered budgets
     */
    @Query("SELECT * " +
            "FROM budgets_table " +
            "WHERE :now <= endDate AND category in (:transactionCategoryList)")
    LiveData<List<Budget>> getFilteredBudgets(DateTime now, List<String> transactionCategoryList);

    /**
     * Add a specific sum to all the budgets from a specific category and their interval corresponds to today's date
     * @param category budget category that is being updated
     * @param sum      sum that is being added to a budget
     * @param today    today date
     */
    @Query("UPDATE budgets_table " +
            "SET spent = spent + :sum " +
            "WHERE category = :category AND :today <= endDate AND :today >= startDate")
    void addTransactionToBudgetCategory(String category, float sum, DateTime today);

    /**
     * Insert Budgets into Database
     *
     * @param budgetList list of Budgets that are being inserted
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBudgets(List<Budget> budgetList);

    @Update
    void update(Budget budget);

    @Insert
    void insert(Budget budget);

    /**
     * Deletes from local database
     */
    @Query("DELETE FROM budgets_table")
    void deleteDatabase();
}
