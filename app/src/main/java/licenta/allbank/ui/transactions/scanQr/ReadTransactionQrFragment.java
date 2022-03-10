package licenta.allbank.ui.transactions.scanQr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import licenta.allbank.R;
import licenta.allbank.data.adapter.TransactionCategoryOptionAdapter;
import licenta.allbank.data.model.allbank.others.CategoryOption;
import licenta.allbank.data.model.allbank.others.NewTransaction;
import licenta.allbank.data.model.allbank.others.QrData;
import licenta.allbank.utils.click_interface.ClickInterfaceCategory;

import static licenta.allbank.ui.budget.NewBudgetFragment.NEW_BUDGET;
import static licenta.allbank.ui.home.HomeFragment.navigateToNextFragment;
import static licenta.allbank.ui.home.HomeFragment.scrollUp;
import static licenta.allbank.ui.homeAdvanced.HomeAdvancedFragment.setTextViewText;
import static licenta.allbank.ui.statistics.StatisticsCategoryFragment.copyCategoryListObjects;
import static licenta.allbank.ui.transactions.generateQr.GenerateCodeQrFragment.QR_DATA;
import static licenta.allbank.ui.transactions.newTransaction.NewTransactionFragment.NEW_TRANSACTION;

public class ReadTransactionQrFragment extends Fragment implements ClickInterfaceCategory {

    private TextView currencyTextView, amountTextView;
    private TextView lastNameTextView, firstNameTextView;
    private TextView ibanTextView, detailsTextView;
    private Button nextButton;

    private TransactionCategoryOptionAdapter transactionCategoryOptionAdapter;
    private RecyclerView recyclerViewCategoryOption;
    private List<CategoryOption> categoryOptionList;
    private String categorySelected;

    private NavController navController;
    private QrData qrData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        Bundle bundle = getArguments();
        if (bundle != null) {
            qrData = bundle.getParcelable(QR_DATA);
        }

        categoryOptionList = copyCategoryListObjects();
        categoryOptionList.remove(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_read_transaction_qr, container, false);

        /* Init UI elements */
        initUiElements(view);

        /* Init behaviours */
        initBehaviours();

        /* Init default values */
        initDefaultValues();

        return view;
    }

    private void initUiElements(View view) {
        this.currencyTextView = view.findViewById(R.id.text_view_read_transaction_qr_transaction_currency);
        this.amountTextView = view.findViewById(R.id.text_view_read_transaction_qr_transaction_amount);

        this.lastNameTextView = view.findViewById(R.id.text_view_read_transaction_qr_receiver_last_name);
        this.firstNameTextView = view.findViewById(R.id.text_view_read_transaction_qr_receiver_first_name);

        this.ibanTextView = view.findViewById(R.id.text_view_read_transaction_qr_iban);
        this.detailsTextView = view.findViewById(R.id.text_view_read_transaction_qr_details);

        this.nextButton = view.findViewById(R.id.button_read_transaction_qr);
        this.recyclerViewCategoryOption = view.findViewById(R.id.recycler_view_read_transaction_qr_category);
    }

    private void initBehaviours() {
        initNextButton();
        initRecyclerViewCategories();
    }

    private void initDefaultValues() {
        String creditorIban = qrData.getCreditorIban();
        String firstName = qrData.getFirstName();
        String lastName = qrData.getLastName();
        float amount = qrData.getAmount();
        String currency = qrData.getCurrency();
        String details = qrData.getDetails();

        setTextViewText(amountTextView, amount);
        setTextViewText(currencyTextView, currency);
        setTextViewText(detailsTextView, details);
        setTextViewText(ibanTextView, creditorIban);
        setTextViewText(firstNameTextView, firstName);
        setTextViewText(lastNameTextView, lastName);

        /* By default, set first category to be selected */
        for (CategoryOption categoryOption : categoryOptionList) {
            categoryOption.setSelected(false);
        }
        categoryOptionList.get(0).setSelected(true);
        categorySelected = categoryOptionList.get(0).getCategoryType();
    }

    private void initRecyclerViewCategories() {
        setLayoutManagerRecyclerViewCategory();
        setAdapterRecyclerViewCategory(categoryOptionList);
    }

    private void setAdapterRecyclerViewCategory(List<CategoryOption> categoryOptions) {
        transactionCategoryOptionAdapter = new TransactionCategoryOptionAdapter(getContext(), categoryOptions, this, NEW_BUDGET);
        recyclerViewCategoryOption.setAdapter(transactionCategoryOptionAdapter);
    }

    private void setLayoutManagerRecyclerViewCategory() {
        recyclerViewCategoryOption.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
    }

    private void initNextButton() {
        nextButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();

            String creditorIban = qrData.getCreditorIban();
            String creditorFirstName = qrData.getFirstName();
            String creditorLasttName = qrData.getLastName();
            String creditorName = creditorLasttName + " " + creditorFirstName;
            float amount = qrData.getAmount();
            String currency = qrData.getCurrency();
            String details = qrData.getDetails();

            NewTransaction newTransaction = new NewTransaction("", creditorIban, creditorName, amount, currency, details, "", categorySelected);

            bundle.putParcelable(NEW_TRANSACTION, newTransaction);
            navigateToNextFragment(navController, R.id.action_readTransactionQrFragment_to_newTransactionAccountsFragment, bundle);
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        initDefaultValues();
        transactionCategoryOptionAdapter.updateUI();
        scrollUp(recyclerViewCategoryOption);
    }

    @Override
    public void onClickCategory(int position) {
        for (CategoryOption categoryOption : categoryOptionList) {
            categoryOption.setSelected(false);
        }
        categoryOptionList.get(position).setSelected(true);
        categorySelected = categoryOptionList.get(position).getCategoryType();
        transactionCategoryOptionAdapter.updateUI();
    }
}