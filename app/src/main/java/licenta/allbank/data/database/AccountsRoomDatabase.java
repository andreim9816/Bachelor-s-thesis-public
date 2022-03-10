package licenta.allbank.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import licenta.allbank.data.dao.AccountDao;
import licenta.allbank.data.dao.BudgetDao;
import licenta.allbank.data.dao.TransactionDao;
import licenta.allbank.data.model.database.Account;
import licenta.allbank.data.model.database.Budget;
import licenta.allbank.data.model.database.DateConverter;
import licenta.allbank.data.model.database.Transaction;

@Database(entities = {Transaction.class, Account.class, Budget.class}, version = 1, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class AccountsRoomDatabase extends RoomDatabase {
    public abstract TransactionDao transactionDao();

    public abstract AccountDao accountDao();

    public abstract BudgetDao budgetDao();

    private static volatile AccountsRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    private static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static ExecutorService getDatabaseWriteExecutor() {
        return databaseWriteExecutor;
    }

    public static AccountsRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AccountsRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AccountsRoomDatabase.class, "account_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
