package licenta.allbank.ui.transactions.newTransaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import licenta.allbank.R;
import licenta.allbank.data.model.allbank.others.NewTransaction;
import licenta.allbank.data.model.database.Transaction;
import licenta.allbank.service.ServiceServer;
import licenta.allbank.ui.home.HomeViewModel;
import licenta.allbank.ui.profile.accounts.AccountsViewModel;
import licenta.allbank.ui.statistics.StatisticsViewModel;
import licenta.allbank.utils.DateFormatGMT;
import licenta.allbank.utils.server.CallbackNoContent;

import static licenta.allbank.activity.MainActivity.paymentResponse;
import static licenta.allbank.ui.home.HomeFragment.navigateToNextFragment;
import static licenta.allbank.ui.homeAdvanced.HomeAdvancedFragment.setTextViewText;
import static licenta.allbank.ui.transactions.newTransaction.NewTransactionFragment.NEW_TRANSACTION;
import static licenta.allbank.utils.DateFormatGMT.convertDateToStringTransactionDetails;


public class NewTransactionSuccessFragment extends Fragment {

    private TextView amountTextView, creditorNameTextView, creditorIbanTextView;
    private TextView dateTextView, detailsTextView, bankTextView, debtorIbanTextView;
    private Button button;
    private NewTransaction newTransaction;
    private NavController navController;
    AccountsViewModel accountsViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        StatisticsViewModel statisticsViewModel = new ViewModelProvider(this).get(StatisticsViewModel.class);
        accountsViewModel = new ViewModelProvider(this).get(AccountsViewModel.class);

        Bundle bundle = getArguments();
        if (bundle != null) {

            newTransaction = bundle.getParcelable(NEW_TRANSACTION);
            newTransaction.setId(paymentResponse.getPaymentId());

            /* Add transaction to local database */
            Transaction transaction = NewTransaction.convertNewTransactionDataToTransactionObject(newTransaction);
            homeViewModel.insert(transaction);

            /* Add transaction amount to budgets if needed to */
            statisticsViewModel.addTransactionToBudgetCategory(newTransaction.getCategory(), newTransaction.getAmount());

            /* Add transaction category to server database */
            try {
                ServiceServer.getInstance(getContext()).addTransactionCategoryToDb(ServiceServer.getAccessToken(), newTransaction.getId(), newTransaction.getCategory(), new CallbackNoContent() {
                    @Override
                    public void onSuccess() {
                        /* Update balances */
                        String creditorIban = creditorIbanTextView.getText().toString().trim();
                        String debtorIban = debtorIbanTextView.getText().toString().trim();
                        float amount = newTransaction.getAmount();

                        accountsViewModel.updateBalance(creditorIban, amount);
                        accountsViewModel.updateBalance(debtorIban, -amount);
                    }

                    @Override
                    public void onError(Throwable t) {

                    }
                });
            } catch (Exception e) {
                // TODO If I couldn't add transaction category to db, dont display any error
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_transaction_success, container, false);

        /* Init UI elements */
        initUiElements(view);

        /* Init default values */
        initDefaultValues();

        /* Init the buttons behaviour */
        initBehaviours();

        return view;
    }

    private void initUiElements(View view) {
        amountTextView = view.findViewById(R.id.text_view_new_transaction_success_amount);
        creditorNameTextView = view.findViewById(R.id.text_view_new_transaction_success_to);
        creditorIbanTextView = view.findViewById(R.id.text_view_new_transaction_success_creditor_iban);
        dateTextView = view.findViewById(R.id.text_view_transaction_date);
        detailsTextView = view.findViewById(R.id.text_view_new_transaction_success_details);
        bankTextView = view.findViewById(R.id.text_view_new_transaction_success_bank);
        debtorIbanTextView = view.findViewById(R.id.text_view_new_transaction_success_your_iban);
        button = view.findViewById(R.id.button_transaction_edit);
    }

    private void initDefaultValues() {
        setTextViewText(amountTextView, newTransaction.getAmount());
        setTextViewText(creditorNameTextView, newTransaction.getCreditorName());
        setTextViewText(creditorIbanTextView, newTransaction.getCreditorIban());
        setTextViewText(dateTextView, convertDateToStringTransactionDetails(DateFormatGMT.getTodayDate()));
        setTextViewText(detailsTextView, newTransaction.getDetails());
        setTextViewText(debtorIbanTextView, newTransaction.getMyIban());
        setTextViewText(bankTextView, newTransaction.getBank());
    }

    private void initBehaviours() {
        button.setOnClickListener(v -> {
            navigateToNextFragment(navController, R.id.action_newTransactionSuccessFragment_to_navigation_transactions, null);
        });
    }
}