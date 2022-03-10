package licenta.allbank.ui.budget;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.List;

import licenta.allbank.R;
import licenta.allbank.data.adapter.TransactionCategoryOptionAdapter;
import licenta.allbank.data.model.allbank.others.CategoryOption;
import licenta.allbank.data.model.database.Budget;
import licenta.allbank.utils.DateFormatGMT;
import licenta.allbank.utils.click_interface.ClickInterfaceCategory;

import static licenta.allbank.ui.home.HomeFragment.navigateToNextFragment;
import static licenta.allbank.ui.homeAdvanced.HomeAdvancedFragment.formatDateToString;
import static licenta.allbank.ui.statistics.StatisticsBudgetingFragment.BUDGET;
import static licenta.allbank.ui.statistics.StatisticsCategoryFragment.copyCategoryListObjects;
import static licenta.allbank.ui.transactions.generateQr.GenerateCodeQrFragment.checkEditTextCompleted;

public class NewBudgetFragment extends Fragment implements ClickInterfaceCategory {
    public static final String NEW_BUDGET = "NEW_BUDGET";
    private static final String ERROR_AMOUNT = "Please fill in amount!";
    private static final String ERROR_DATE = "Please fill in date!";

    private RecyclerView recyclerViewCategory;
    private TransactionCategoryOptionAdapter transactionCategoryOptionAdapter;
    private EditText editTextStartDate, editTextEndDate, editTextBudget;
    private TextInputLayout startDateInputLayout, endDateInputLayout, budgetInputLayout;

    private MaterialButton nextButton;

    private DateTime dateFrom, dateTo;
    private String categorySelected;
    private float budget;
    private List<CategoryOption> categoryOptionList;
    private NavController navController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* Inflate the layout for this fragment */
        View view = inflater.inflate(R.layout.fragment_new_budget, container, false);

        /* Init UI elements */
        initUiElements(view);

        /* Init default values */
        initDefaultValues();

        /* Init the buttons behaviour */
        initBehaviours();

        return view;
    }

    private void initUiElements(View view) {
        this.recyclerViewCategory = view.findViewById(R.id.recycler_view_new_budget);
        this.nextButton = view.findViewById(R.id.button_new_budget_next);

        this.editTextStartDate = view.findViewById(R.id.edit_text_new_budget_start_date_picker);
        this.editTextEndDate = view.findViewById(R.id.edit_text_new_budget_end_date_picker);
        this.editTextBudget = view.findViewById(R.id.edit_text_new_budget_sum);

        this.startDateInputLayout = view.findViewById(R.id.text_input_layout_new_budget_start_date);
        this.endDateInputLayout = view.findViewById(R.id.text_input_layout_new_budget_end_date);
        this.budgetInputLayout = view.findViewById(R.id.text_input_layout_new_budget_amount);
    }

    private void initBehaviours() {
        initRecyclerViewCategories();
        initStartDatePicker();
        initEndDatePicker();
        initNextButton();
    }

    private void initDefaultValues() {
        /* By default, set OTHERS category selected */
        categoryOptionList = copyCategoryListObjects();
        categoryOptionList.remove(0);
        categoryOptionList.get(6).setSelected(true);
        categorySelected = categoryOptionList.get(6).getCategoryType();
    }

    private void initRecyclerViewCategories() {
        setLayoutManagerRecyclerViewCategory();
        setAdapterRecyclerViewCategory(categoryOptionList);
    }

    private void initNextButton() {
        nextButton.setOnClickListener(v -> {
            /* Check if all fields were completed */
            if (checkEditTextCompleted(startDateInputLayout, editTextStartDate, ERROR_DATE) & checkEditTextCompleted(endDateInputLayout, editTextEndDate, ERROR_DATE) & checkEditTextCompleted(budgetInputLayout, editTextBudget, ERROR_AMOUNT)) {
                budget = Float.parseFloat(editTextBudget.getText().toString().trim());

                Bundle bundle = new Bundle();
                Budget budgetObject = new Budget(-1, dateFrom, dateTo, budget, categorySelected);
                bundle.putParcelable(BUDGET, budgetObject);

                navigateToNextFragment(navController, R.id.action_newBudget_to_budgetConfirmFragment, bundle);
            }
        });
    }

    private void initStartDatePicker() {
        initDatePicker(editTextStartDate);
    }

    private void initEndDatePicker() {
        initDatePicker(editTextEndDate);
    }

    private void initDatePicker(EditText datePicker) {
        final Calendar myCalendar = Calendar.getInstance();

        /* Create DatePickerDialog and its behaviour */
        DatePickerDialog.OnDateSetListener date = (view, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            datePicker.setText(formatDateToString(dayOfMonth, month + 1, year));

            if (datePicker == editTextStartDate) {
                dateFrom = DateFormatGMT.convertDataToDate(dayOfMonth, month + 1, year);
            } else {
                dateTo = DateFormatGMT.convertDataToDate(dayOfMonth, month + 1, year);
            }
        };

        /* Add onClickListener on DatePicker */
        datePicker.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));

            /* Disable any past dates */
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

            /* Set only valid intervals */
            if (datePicker == editTextStartDate && dateTo != null) {
                datePickerDialog.getDatePicker().setMaxDate(dateTo.minusDays(1).getMillis());
            } else if (datePicker == editTextEndDate && dateFrom != null) {
                datePickerDialog.getDatePicker().setMinDate(dateFrom.getMillis());
            }
            datePickerDialog.show();
        });
    }

    private void setLayoutManagerRecyclerViewCategory() {
        FlexboxLayoutManager manager = new FlexboxLayoutManager(getContext(), FlexDirection.ROW);
        manager.setJustifyContent(JustifyContent.CENTER);
        recyclerViewCategory.setLayoutManager(manager);
    }

    private void setAdapterRecyclerViewCategory(List<CategoryOption> categoryOptions) {
        transactionCategoryOptionAdapter = new TransactionCategoryOptionAdapter(getContext(), categoryOptions, this, NEW_BUDGET);
        recyclerViewCategory.setAdapter(transactionCategoryOptionAdapter);
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
