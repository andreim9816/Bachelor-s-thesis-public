package licenta.allbank.ui.homeAdvanced;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.joda.time.DateTime;

import java.util.List;

import licenta.allbank.data.model.database.Transaction;
import licenta.allbank.data.model.database.TransactionTypeAverageBank;
import licenta.allbank.data.model.database.TransactionTypeSum;
import licenta.allbank.data.repository.TransactionRepository;

public class HomeAdvancedViewModel extends AndroidViewModel {
    private final TransactionRepository transactionRepository;
    private LiveData<List<Transaction>> allTransactions;

    public HomeAdvancedViewModel(Application application) {
        super(application);
        transactionRepository = new TransactionRepository(application);
        allTransactions = transactionRepository.getAllTransactions();
    }

    public LiveData<List<Transaction>> getAllTransactions() {
        return allTransactions;
    }

    public LiveData<List<Transaction>> getAllTransactions(DateTime startDate, DateTime endDate) {
        return transactionRepository.getAllTransactions(startDate, endDate);
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

    public LiveData<List<Transaction>> getTransactions(DateTime startDate, DateTime endDate) {
        return transactionRepository.getAllTransactions(startDate, endDate);
    }

    public LiveData<List<TransactionTypeSum>> getTransactionsTypeSum(DateTime startDate, DateTime endDate) {
        return transactionRepository.getTransactionsTypeSum(startDate, endDate);
    }

    public LiveData<List<TransactionTypeAverageBank>> getAverageTransaction(DateTime startDate, DateTime endDate) {
        return transactionRepository.getAverageTransaction(startDate, endDate);
    }

    public LiveData<List<TransactionTypeSum>> getTransactionsTypeSum(DateTime startDate, DateTime endDate, String bank) {
        return transactionRepository.getTransactionsTypeSum(startDate, endDate, bank);
    }

    public void insert(Transaction transaction) {
        transactionRepository.insert(transaction);
    }
}
