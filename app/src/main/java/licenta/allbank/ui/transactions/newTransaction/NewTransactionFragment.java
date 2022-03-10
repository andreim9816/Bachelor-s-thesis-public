package licenta.allbank.ui.transactions.newTransaction;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import licenta.allbank.R;
import licenta.allbank.data.adapter.IbanNameSearchAdapter;
import licenta.allbank.data.adapter.TransactionCategoryOptionAdapter;
import licenta.allbank.data.model.allbank.others.CategoryOption;
import licenta.allbank.data.model.allbank.others.IbanNameSearchItem;
import licenta.allbank.data.model.allbank.others.NewTransaction;
import licenta.allbank.utils.click_interface.ClickInterfaceCategory;

import static licenta.allbank.ui.budget.NewBudgetFragment.NEW_BUDGET;
import static licenta.allbank.ui.home.HomeFragment.navigateToNextFragment;
import static licenta.allbank.ui.home.HomeFragment.scrollUp;
import static licenta.allbank.ui.statistics.StatisticsCategoryFragment.copyCategoryListObjects;
import static licenta.allbank.ui.transactions.generateQr.GenerateCodeQrFragment.checkEditTextCompleted;
import static licenta.allbank.utils.validation.Validator.REGEX_IBAN;

public class NewTransactionFragment extends Fragment implements ClickInterfaceCategory {
    public static String NEW_TRANSACTION = "NEW_TRANSACTION";
    private static final String ERROR_IBAN = "Please fill in iban!";
    private static final String ERROR_MATCH_IBAN = "Incorrect iban!";
    private static final String ERROR_DETAILS = "Please fill in transaction details!";
    private static final String ERROR_CREDITOR_NAME = "Please fill in receiver name!";
    private static final String ERROR_AMOUNT = "Please fill in amount!";

    private Button nextButton;
    private TextView currencyTextView;

    private TextInputLayout ibanInputLayout, nameInputLayout, detailsInputLayout, amountInputLayout;
    private EditText ibanSearchEditText, nameSearchEditText, detailsEditText, amountEditText;

    private IbanNameSearchAdapter ibanNameSearchAdapterIban, ibanNameSearchAdapterName;
    private RecyclerView ibanNameRecyclerViewIban, ibanNameRecyclerViewName;

    private TransactionCategoryOptionAdapter transactionCategoryOptionAdapter;
    private RecyclerView recyclerViewCategoryOption;
    private List<CategoryOption> categoryOptionList;
    private String categorySelected;

    private TransactionViewModel transactionViewModel;
    private NavController navController;
    private boolean itemClicked = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        categoryOptionList = copyCategoryListObjects();
        categoryOptionList.remove(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_transaction, container, false);

        /* Init UI elements */
        initUiElements(view);

        /* Init default values */
        initDefaultValues();

        /* Init the buttons behaviour */
        initBehaviours();

        return view;
    }

    private void initUiElements(View view) {
        ibanSearchEditText = view.findViewById(R.id.edit_text_new_transaction_creditor_iban);
        nameSearchEditText = view.findViewById(R.id.edit_text_new_transaction_creditor_name);
        detailsEditText = view.findViewById(R.id.edit_text_new_transaction_creditor_details);
        amountEditText = view.findViewById(R.id.edit_text_new_transaction_amount);
        nextButton = view.findViewById(R.id.button_new_transaction_next);

        ibanNameRecyclerViewIban = view.findViewById(R.id.recycler_view_new_transaction_creditor_iban);
        ibanNameRecyclerViewName = view.findViewById(R.id.recycler_view_new_transaction_creditor_name);

        detailsInputLayout = view.findViewById(R.id.text_input_layout_new_transaction_creditor_details);
        ibanInputLayout = view.findViewById(R.id.text_input_layout_new_transaction_creditor_iban);
        nameInputLayout = view.findViewById(R.id.text_input_layout_new_transaction_creditor_name);
        amountInputLayout = view.findViewById(R.id.text_input_layout_new_transaction_amount);

        recyclerViewCategoryOption = view.findViewById(R.id.recycler_view_new_transaction_category);
    }

    @Override
    public void onPause() {
        super.onPause();
        initDefaultValues();
        transactionCategoryOptionAdapter.updateUI();
        scrollUp(recyclerViewCategoryOption);
    }

    private void initDefaultValues() {
        ibanSearchEditText.setText("");
        nameSearchEditText.setText("");
        detailsEditText.setText("");
        amountEditText.setText("");
        itemClicked = false;

        /* By default, set first category to be selected */
        for (CategoryOption categoryOption : categoryOptionList) {
            categoryOption.setSelected(false);
        }

        categoryOptionList.get(0).setSelected(true);
        categorySelected = categoryOptionList.get(0).getCategoryType();
    }

    private void initBehaviours() {
        initRecyclerViewIbanNameListIban();
        initRecyclerViewIbanNameListName();

        setEditTextSearch(ibanSearchEditText);
        setEditTextSearch(nameSearchEditText);

        setEditTextOnLostFocus(ibanSearchEditText);
        setEditTextOnLostFocus(nameSearchEditText);

        initNextButton();
        initRecyclerViewCategories();
//        ibanSearchEditText.setFilters(new InputFilter[]{EditTextIbanField.getInstance()}); // overrides maxlength = 24s
    }

    private void initRecyclerViewIbanNameListIban() {
        setLayoutManagerRecyclerViewIbanNameListIban();
        setAdapterRecyclerViewIbanNameListIban();
    }

    private void initRecyclerViewIbanNameListName() {
        setLayoutManagerRecyclerViewIbanNameListName();
        setAdapterRecyclerViewIbanNameListName();
    }

    private void initRecyclerViewCategories() {
        setLayoutManagerRecyclerViewCategory();
        setAdapterRecyclerViewCategory(categoryOptionList);
    }

    private void setLayoutManagerRecyclerViewIbanNameListIban() {
        ibanNameRecyclerViewIban.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
    }

    private void setLayoutManagerRecyclerViewIbanNameListName() {
        ibanNameRecyclerViewName.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
    }

    private void setLayoutManagerRecyclerViewCategory() {
        recyclerViewCategoryOption.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
    }

    private void setAdapterRecyclerViewIbanNameListIban() {
        ibanNameSearchAdapterIban = new IbanNameSearchAdapter(getContext(), position -> {
            IbanNameSearchItem ibanNameSearchItem = ibanNameSearchAdapterIban.getIbanNameSearchItem(position);
            onClickSearchItem(ibanNameSearchItem);
        });
        ibanNameRecyclerViewIban.setAdapter(ibanNameSearchAdapterIban);
    }

    private void setAdapterRecyclerViewIbanNameListName() {
        ibanNameSearchAdapterName = new IbanNameSearchAdapter(getContext(), position -> {
            IbanNameSearchItem ibanNameSearchItem = ibanNameSearchAdapterName.getIbanNameSearchItem(position);
            onClickSearchItem(ibanNameSearchItem);
        });
        ibanNameRecyclerViewName.setAdapter(ibanNameSearchAdapterName);
    }

    private void setAdapterRecyclerViewCategory(List<CategoryOption> categoryOptions) {
        transactionCategoryOptionAdapter = new TransactionCategoryOptionAdapter(getContext(), categoryOptions, this, NEW_BUDGET);
        recyclerViewCategoryOption.setAdapter(transactionCategoryOptionAdapter);
    }

    private void initNextButton() {
        nextButton.setOnClickListener(v -> {
            if (checkEditTextCompleted(detailsInputLayout, detailsEditText, ERROR_DETAILS)
                    & checkEditTextCompleted(ibanInputLayout, ibanSearchEditText, ERROR_IBAN)
                    & checkEditTextCompleted(nameInputLayout, nameSearchEditText, ERROR_CREDITOR_NAME)
                    & checkEditTextCompleted(amountInputLayout, amountEditText, ERROR_AMOUNT)
                    & checkIbanEditText()) {

                String details = detailsEditText.getText().toString().trim();
                String creditorName = nameSearchEditText.getText().toString().trim();
                String creditorIban = ibanSearchEditText.getText().toString().trim();
                String currency = "RON"; //TODO
                float amount = Float.parseFloat(amountEditText.getText().toString().trim());

                NewTransaction newTransaction = new NewTransaction("", creditorIban, creditorName, amount, currency, details, "", categorySelected);
                Bundle bundle = new Bundle();
                bundle.putParcelable(NEW_TRANSACTION, newTransaction);

                navigateToNextFragment(navController, R.id.action_navigation_transactions_to_newTransactionAccountsFragment, bundle);
            }
        });
    }

    private void setEditTextSearch(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editText.setSelection(s.length());

                if (editText.hasFocus() && !itemClicked) {
                    String query = s.toString();
                    if (query.length() > 0) {
                        if (editText == ibanSearchEditText && query.length() != 24) {
                            filterTransactionsDatabaseByIbanQuery(query);
                        } else {
                            filterTransactionsDatabaseByNameQuery(query);
                        }
                    } else {
                        emptyRecyclerViews();
                    }
                } else {
                    emptyRecyclerViews();
                    if (editText == nameSearchEditText) {
                        itemClicked = false;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setEditTextOnLostFocus(EditText editText) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (editText == ibanSearchEditText) {
                        ibanNameSearchAdapterIban.setIbanNameSearchItemList(new ArrayList<>());
                    }
                    if (editText == nameSearchEditText) {
                        ibanNameSearchAdapterName.setIbanNameSearchItemList(new ArrayList<>());
                    }
                }
            }
        });
    }

    /**
     * Queries transactions database and returns <code>IbanNameSearchItem</code> objects by looking at
     * user's iban transactions history
     *
     * @param query Query string that is being searched in database
     */
    private void filterTransactionsDatabaseByIbanQuery(String query) {
        transactionViewModel.filterTransactionsDatabaseByIbanQuery(query).observe(getViewLifecycleOwner(), ibanNameSearchItems -> {
            ibanNameSearchAdapterIban.setIbanNameSearchItemList(ibanNameSearchItems);
        });
    }

    /**
     * Queries transactions database and returns <code>IbanNameSearchItem</code> objects by looking at
     * user's creditor name transaction history
     *
     * @param query Query string that is being searched in database
     */
    private void filterTransactionsDatabaseByNameQuery(String query) {
        transactionViewModel.filterTransactionsDatabaseByNameQuery(query).observe(getViewLifecycleOwner(), ibanNameSearchItems -> {
            ibanNameSearchAdapterName.setIbanNameSearchItemList(ibanNameSearchItems);
        });
    }

    private void onClickSearchItem(IbanNameSearchItem ibanNameSearchItem) {
        emptyRecyclerViews();
        itemClicked = true;
        ibanSearchEditText.setText(ibanNameSearchItem.getIban());
        nameSearchEditText.setText(ibanNameSearchItem.getName());
    }

    private void emptyRecyclerViews() {
        ibanNameSearchAdapterIban.setIbanNameSearchItemList(new ArrayList<>());
        ibanNameSearchAdapterName.setIbanNameSearchItemList(new ArrayList<>());
    }

    private boolean checkIbanEditText() {
        ibanInputLayout.setErrorEnabled(false);
        String ibanInput = Objects.requireNonNull(ibanSearchEditText.getText()).toString();
        String ibanFormatted = ibanInput.replaceAll(" ", "");

        if (ibanFormatted.matches(REGEX_IBAN)) {
            return true;
        }
        ibanInputLayout.setError(ERROR_MATCH_IBAN);
        return false;
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