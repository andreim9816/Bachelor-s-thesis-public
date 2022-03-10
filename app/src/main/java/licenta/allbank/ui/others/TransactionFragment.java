package licenta.allbank.ui.others;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.switchmaterial.SwitchMaterial;

import licenta.allbank.R;
import licenta.allbank.data.model.allbank.others.TransactionDetails;
import licenta.allbank.ui.home.HomeViewModel;
import licenta.allbank.utils.DateFormatGMT;

import static licenta.allbank.ui.home.HomeFragment.TRANSACTION_DETAILS;
import static licenta.allbank.ui.homeAdvanced.HomeAdvancedFragment.roundTwoDecimals;
import static licenta.allbank.ui.homeAdvanced.HomeAdvancedFragment.setTextViewText;

public class TransactionFragment extends Fragment {
    private static final String UNAVAILABLE = "Unavailable";
    private static final String NOT_PROVIDED = "Not provided";

    private TextView ibanFromTextView, ibanToTextView, nameFromTextView, nameToTextView;
    private TextView detailsTextView, amountTextView, currencyTextView, categoryTextView, dateTextView;
    private TextView bankTextView;
    private SwitchMaterial switchButton;

    private TransactionDetails transactionDetails;
    private HomeViewModel homeViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        Bundle bundle = getArguments();
        if (bundle != null) {
            transactionDetails = bundle.getParcelable(TRANSACTION_DETAILS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* Inflate the layout for this fragment */
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);

        /* Init UI elements */
        initUiElements(view);

        /* Init default values */
        initDefaultValues();

        /* Init the buttons behaviour */
        initBehaviours();

        return view;
    }

    private void initUiElements(View view) {
        amountTextView = view.findViewById(R.id.text_view_transaction_amount);
        currencyTextView = view.findViewById(R.id.text_view_transaction_currency);
        bankTextView = view.findViewById(R.id.text_view_transaction_bank);

        nameFromTextView = view.findViewById(R.id.text_view_transaction_from);
        nameToTextView = view.findViewById(R.id.text_view_transaction_to);
        ibanFromTextView = view.findViewById(R.id.text_view_transaction_iban_from);
        ibanToTextView = view.findViewById(R.id.text_view_transaction_iban_to);

        categoryTextView = view.findViewById(R.id.text_view_transaction_category);
        detailsTextView = view.findViewById(R.id.text_view_transaction_details);
        dateTextView = view.findViewById(R.id.text_view_transaction_date);

        switchButton = view.findViewById(R.id.switch_transaction_enabled);
    }

    private void initBehaviours() {
        initSwitchButton();
    }

    private void initSwitchButton() {
        switchButton.setOnCheckedChangeListener((buttonView, isChecked) -> homeViewModel.updateTransactionState(transactionDetails.getTransactionId(), isChecked));
    }

    private void initDefaultValues() {
        setTextViewText(amountTextView, roundTwoDecimals(transactionDetails.getAmount()));
        setTextViewText(currencyTextView, transactionDetails.getCurrency() == null ? UNAVAILABLE : transactionDetails.getCurrency());

        setTextViewText(nameFromTextView, transactionDetails.getNameFrom() == null || transactionDetails.getNameFrom().length() == 0 ? UNAVAILABLE : transactionDetails.getNameFrom());
        setTextViewText(nameToTextView, transactionDetails.getNameTo() == null ? NOT_PROVIDED : transactionDetails.getNameTo());
        setTextViewText(ibanFromTextView, transactionDetails.getIbanFrom() == null || transactionDetails.getIbanFrom().length() == 0 ? UNAVAILABLE : transactionDetails.getIbanFrom());
        setTextViewText(ibanToTextView, transactionDetails.getIbanTo() == null ? NOT_PROVIDED : transactionDetails.getIbanTo());

        setTextViewText(categoryTextView, transactionDetails.getCategory() == null ? NOT_PROVIDED : transactionDetails.getCategory());
        setTextViewText(detailsTextView, transactionDetails.getDetails() == null ? NOT_PROVIDED : transactionDetails.getDetails());
        setTextViewText(dateTextView, transactionDetails.getDate() == null ? UNAVAILABLE : DateFormatGMT.convertDateToString(transactionDetails.getDate()));
        setTextViewText(bankTextView, transactionDetails.getBank() == null ? UNAVAILABLE : transactionDetails.getBank());

        switchButton.setChecked(transactionDetails.isEnabled());
    }
}
