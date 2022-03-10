package licenta.allbank.ui.transactions.newTransaction;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import licenta.allbank.data.model.allbank.others.IbanNameSearchItem;
import licenta.allbank.data.model.database.Transaction;
import licenta.allbank.data.repository.TransactionRepository;

public class TransactionViewModel extends AndroidViewModel {
    private TransactionRepository transactionRepository;
    private LiveData<List<Transaction>> transactionList;

    public TransactionViewModel(Application application) {
        super(application);
        transactionRepository = new TransactionRepository(application);
        transactionList = transactionRepository.getAllTransactions();
    }

    public LiveData<List<Transaction>> getAllTransactions() {
        return transactionList;
    }

    /**
     * Queries transactions database and returns <code>IbanNameSearchItem</code> objects by looking at
     * user's iban transactions history
     *
     * @param query Query string that is being searched in database
     * @return List of <code>IbanNameSearchItem</code> objects that were filtered
     */
    public LiveData<List<IbanNameSearchItem>> filterTransactionsDatabaseByIbanQuery(String query) {
        return transactionRepository.filterTransactionsDatabaseByIbanQuery(query);
    }

    /**
     * Queries transactions database and returns <code>IbanNameSearchItem</code> objects by looking at
     * user's creditors transactions history
     *
     * @param query Query string that is being searched in database
     * @return List of <code>IbanNameSearchItem</code> objects that were filtered
     */
    public LiveData<List<IbanNameSearchItem>> filterTransactionsDatabaseByNameQuery(String query) {
        return transactionRepository.filterTransactionsDatabaseByNameQuery(query);
    }

    public void insert(Transaction transaction) {
        transactionRepository.insert(transaction);
    }

    public void insertTransactions(List<Transaction> transactionList) {
        transactionRepository.insertTransactions(transactionList);
    }

    public void update(Transaction transaction) {
        transactionRepository.update(transaction);
    }
}
