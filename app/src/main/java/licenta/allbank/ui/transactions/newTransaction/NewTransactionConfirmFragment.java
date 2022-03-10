package licenta.allbank.ui.transactions.newTransaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import licenta.allbank.R;
import licenta.allbank.data.model.allbank.others.NewTransaction;
import licenta.allbank.utils.DateFormatGMT;

import static licenta.allbank.ui.home.HomeFragment.navigateToNextFragment;
import static licenta.allbank.ui.homeAdvanced.HomeAdvancedFragment.setTextViewText;
import static licenta.allbank.ui.transactions.newTransaction.NewTransactionFragment.NEW_TRANSACTION;

public class NewTransactionConfirmFragment extends Fragment {

    private TextView amountTextView, currencyTextView, myIbanTextView, receiverIbanTextView, firstNameTextView, lastNameTextView, detailsTextView;
    private Button nextButton;
    private NewTransaction newTransaction;
    private NavController navController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        Bundle bundle = getArguments();
        if (bundle != null) {
            newTransaction = bundle.getParcelable(NEW_TRANSACTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_transaction_confirm, container, false);

        /* Init UI elements */
        initUiElements(view);

        /* Init default values */
        initDefaultValues();

        /* Init the buttons behaviour */
        initBehaviours();

        return view;
    }

    private void initUiElements(View view) {
        amountTextView = view.findViewById(R.id.text_view_read_transaction_amount);
        currencyTextView = view.findViewById(R.id.text_view_new_transaction_confirm_currency);
        myIbanTextView = view.findViewById(R.id.text_view_new_transaction_confirm_my_iban);
        receiverIbanTextView = view.findViewById(R.id.text_view_new_transaction_confirm_receiver_iban);
        firstNameTextView = view.findViewById(R.id.text_view_new_transaction_confirm_first_name);
        detailsTextView = view.findViewById(R.id.text_view_new_transaction_success_details);
        nextButton = view.findViewById(R.id.button_new_transaction_next);
    }

    private void initDefaultValues() {
        setTextViewText(amountTextView, newTransaction.getAmount());
        setTextViewText(currencyTextView, newTransaction.getCurrency());
        setTextViewText(myIbanTextView, newTransaction.getMyIban());
        setTextViewText(receiverIbanTextView, newTransaction.getCreditorIban());
        setTextViewText(firstNameTextView, newTransaction.getCreditorName());
        setTextViewText(detailsTextView, newTransaction.getDetails());
    }

    private void initBehaviours() {
        nextButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable(NEW_TRANSACTION, newTransaction);
            navigateToNextFragment(navController, R.id.action_newTransactionConfirmFragment_to_confirmTransactionFragment, bundle);
        });
    }
}