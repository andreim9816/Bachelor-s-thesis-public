package licenta.allbank.ui.home;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import licenta.allbank.R;
import licenta.allbank.data.adapter.TransactionListAdapter;
import licenta.allbank.data.model.allbank.others.TransactionDetails;
import licenta.allbank.data.model.database.Transaction;
import licenta.allbank.service.ServiceServer;
import licenta.allbank.utils.Logging;
import licenta.allbank.utils.click_interface.ClickInterfaceTransaction;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static licenta.allbank.service.ServiceServer.ERROR_MESSAGE_SERVER;
import static licenta.allbank.service.ServiceServer.closeDialogProgressBar;
import static licenta.allbank.service.ServiceServer.displayDialogProgressBar;
import static licenta.allbank.ui.homeAdvanced.HomeAdvancedFragment.roundTwoDecimals;

public class HomeFragment extends Fragment implements ClickInterfaceTransaction {

    public static final String INCOME_TRANSACTIONS = "INCOME";
    public static final String EXPENSES_TRANSACTIONS = "EXPENSE";
    public static final String ALL_TRANSACTIONS = "ALL_TRANSACTIONS";
    public static final String TRANSACTION_DETAILS = "TRANSACTION_DETAILS";
    public static final int VISIBLE_TRANSACTIONS = 10;

    public static boolean appJustStarted;
    public static int loadedAccountsData = 2;
    private boolean isLoading = false;

    private Button allButton, incomeButton, expensesButton;
    private SwitchMaterial switchButton;
    private TextView balanceTextView, lastMonthIncomeTextView, lastMonthExpensesTextView;
    private ImageView loadingLogoImageView;

    private HomeViewModel homeViewModel;
    private NavController navController;

    private RecyclerView recyclerView;
    private TransactionListAdapter adapter;
    private static List<Transaction> currentTransactions;
    private static List<Transaction> currentTransactionsDisplayed;


    private Dialog mDialog;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        if (appJustStarted) {
            try {
                appJustStarted = false;
                homeViewModel.deleteDatabase();
                Context context = getContext();
                ServiceServer serviceServer = ServiceServer.getInstance(getContext());
                serviceServer.getApiDataServer(context, ServiceServer.getAccessToken(), mDialog, homeViewModel, this);
            } catch (Exception e) {
                Toast.makeText(getContext(), ERROR_MESSAGE_SERVER, Toast.LENGTH_LONG).show();
            }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        /* Init UI elements */
        initUiElements(view);

        /* Init the buttons behaviour */
        initBehaviours();

        /* Init default values */
        initDefaultValues();

        allButton.performClick(); // display the transactions

        return view;
    }

    private void initDefaultValues() {
        addScrollListener();
        setTotalBalance();
        setLastMonthExpenses();
        setLastMonthIncome();
    }

    private void initRecyclerViewTransactions() {
        setLayoutManagerRecyclerTransactions();
        setAdapterRecyclerTransactions();
    }

    private void setLayoutManagerRecyclerTransactions() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public void onLayoutCompleted(RecyclerView.State state) {
                super.onLayoutCompleted(state);
                if (loadedAccountsData > 0) {
                    loadedAccountsData--;
                } else {
                    closeDialogProgressBar(mDialog);
                    scrollUp(recyclerView);
                    clearLoadingLogo(loadingLogoImageView);
                }
            }
        });
    }

    private void setAdapterRecyclerTransactions() {
        adapter = new TransactionListAdapter(getContext(), this);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Function that displays the next transactions in the recycler view
     *
     * @param adapter      Adapter of the recycler view that is being updated
     * @param transactions List of **ALL** <code>Transaction</code> objects that are displayed in the recycler view
     */
    private void getMoreData(TransactionListAdapter adapter, List<Transaction> transactions) {
        currentTransactionsDisplayed.add(null);
        adapter.notifyItemInserted(currentTransactionsDisplayed.size() - 1);
        currentTransactionsDisplayed.remove(currentTransactionsDisplayed.size() - 1);
        int size = currentTransactionsDisplayed.size();
        List<Transaction> subList = transactions.subList(size, min(size + VISIBLE_TRANSACTIONS, transactions.size()));
        currentTransactionsDisplayed.addAll(subList);
        adapter.notifyDataSetChanged();
        isLoading = false;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    private void initUiElements(View view) {
        allButton = view.findViewById(R.id.button_home_all);
        switchButton = view.findViewById(R.id.switch_home);
        expensesButton = view.findViewById(R.id.button_home_expenses);
        incomeButton = view.findViewById(R.id.button_home_income);
        balanceTextView = view.findViewById(R.id.textView_home_balance);
        lastMonthIncomeTextView = view.findViewById(R.id.textView_home_income_number);
        lastMonthExpensesTextView = view.findViewById(R.id.textView_home_expenses_number);
        loadingLogoImageView = view.findViewById(R.id.image_view_home_background);
        recyclerView = view.findViewById(R.id.recyclerview_home);
    }

    private void initBehaviours() {
        initAllTransactionButton();
        initExpensesButton();
        initIncomeButton();
        initSwitchButton();
        initRecyclerViewTransactions();
    }

    private void setTransactions(TransactionListAdapter adapter, List<Transaction> transactions) {
        /* Update the current transactions that are being displayed */
        currentTransactions = transactions;
        /* Display only few transactions */
        currentTransactionsDisplayed = new ArrayList<>();
        if (transactions != null && transactions.size() > 0) {
            for (int index = 0; index <= min(VISIBLE_TRANSACTIONS, transactions.size()); index++) {
                currentTransactionsDisplayed.add(transactions.get(index));
            }
        }
        adapter.setTransactions(currentTransactionsDisplayed);
    }

    private void setIncomeTransactions() {
        mDialog = new Dialog(getContext());
        displayDialogProgressBar(mDialog);
        homeViewModel.getTransactions(INCOME_TRANSACTIONS).observe(getViewLifecycleOwner(), transactions -> setTransactions(adapter, transactions));
    }

    private void setExpensesTransactions() {
        mDialog = new Dialog(getContext());
        displayDialogProgressBar(mDialog);
        homeViewModel.getTransactions(EXPENSES_TRANSACTIONS).observe(getViewLifecycleOwner(), transactions -> setTransactions(adapter, transactions));
    }

    private void setAllTransactions() {
        /* Check what dialog needs to be displayed: the classic loading one or the 'fetching data from web servers' one */
        if (loadedAccountsData <= 0) {
            mDialog = new Dialog(getContext());
            displayDialogProgressBar(mDialog);
        } else {
            mDialog = new Dialog(getContext());
            displayLoadingScreenWhileFetchingBankingData(mDialog);
        }
        homeViewModel.getAllTransactions().observe(getViewLifecycleOwner(), transactions -> setTransactions(adapter, transactions));
    }

    private void setTotalBalance() {
        homeViewModel.getTotalBalance().observe(getViewLifecycleOwner(), total -> {
            String currency = "RON";
            balanceTextView.setText(roundTwoDecimals(total) + " " + currency);
        });
    }

    private void setLastMonthIncome() {
        homeViewModel.getLastMonthIncome().observe(getViewLifecycleOwner(), total -> {
            String currency = "RON";
            lastMonthIncomeTextView.setText(roundTwoDecimals(total) + " " + currency);
        });
    }

    private void setLastMonthExpenses() {
        homeViewModel.getLastMonthExpenses().observe(getViewLifecycleOwner(), total -> {
            String currency = "RON";
            lastMonthExpensesTextView.setText(roundTwoDecimals(total) + " " + currency);
        });
    }

    public static void scrollUp(RecyclerView recyclerView) {
        recyclerView.smoothScrollToPosition(0);
    }

    public static void navigateToNextFragment(NavController navController, @IdRes int id, Bundle bundle) {
        navController.navigate(id, bundle);
    }

    private void initSwitchButton() {
        switchButton.setChecked(false);
        switchButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        navigateToNextFragment(navController, R.id.action_navigation_home_to_homeAdvancedFragment, null);
                    }
                }
        );
    }

    private void initExpensesButton() {
        expensesButton.setOnClickListener(v -> setExpensesTransactions());
    }

    private void initAllTransactionButton() {
        allButton.setOnClickListener(v -> setAllTransactions());
    }

    private void initIncomeButton() {
        incomeButton.setOnClickListener(v -> setIncomeTransactions());
    }

    @Override
    public void onClickTransaction(int position) {
        Transaction transaction = adapter.getTransaction(position);
        TransactionDetails transactionDetails = convertTransactionToTransactionDetailsObject(transaction);

        Bundle bundle = new Bundle();
        bundle.putParcelable(TRANSACTION_DETAILS, transactionDetails);
        navigateToNextFragment(navController, R.id.action_navigation_home_to_transactionFragment, bundle);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logging.show("HomeFragment", " ----- ON DESTROY -----");
    }

    /**
     * @param transaction Transaction object to be converted
     * @return TransactionDetails object used for sending data via bundle between fragments
     */
    public static TransactionDetails convertTransactionToTransactionDetailsObject(Transaction transaction) {
        String transactionId = transaction.getTransactionId();
        String ibanFrom = transaction.getDebtorAccount();
        String ibanTo = transaction.getCreditorAccount();
        String nameFrom = transaction.getDebtorName();
        String nameTo = transaction.getCreditorName();
        String details = transaction.getDetails();
        String bank = transaction.getBank();
        DateTime date = transaction.getBookingDate();
        float amount = transaction.getTransactionAmount().getAmount();
        String currency = transaction.getTransactionAmount().getCurrency();
        String category = transaction.getTransactionCategory();
        boolean enabled = transaction.isEnabled();

        return new TransactionDetails(transactionId, ibanFrom, ibanTo, nameFrom, nameTo, details, bank, date, amount, currency, category, enabled);
    }

    /**
     * Function that makes the imageView invisible
     *
     * @param imageView
     */
    private void clearLoadingLogo(ImageView imageView) {
        imageView.setVisibility(View.GONE);
    }

    /**
     * Function that displays a dialog while fetching data from the banking servers
     *
     * @param dialog Dialog object that is being customized
     */
    private static void displayLoadingScreenWhileFetchingBankingData(Dialog dialog) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_startup);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * Function that adds the scroll listener on the recycler view. Updates the recycler view whenever needs to
     */
    private void addScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && linearLayoutManager != null && currentTransactionsDisplayed != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == currentTransactionsDisplayed.size() - 2) {
                    isLoading = true;
                    getMoreData(adapter, currentTransactions);
                }
            }
        });
    }
}