package licenta.allbank.ui.budget;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.button.MaterialButton;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.List;

import licenta.allbank.R;
import licenta.allbank.data.adapter.TransactionCategoryOptionAdapter;
import licenta.allbank.data.model.allbank.others.CategoryOption;
import licenta.allbank.data.model.database.Budget;
import licenta.allbank.service.ServiceServer;
import licenta.allbank.ui.statistics.StatisticsBudgetingViewModel;
import licenta.allbank.utils.click_interface.ClickInterfaceCategory;
import licenta.allbank.utils.DateFormatGMT;
import licenta.allbank.utils.messages.BudgetMessage;
import licenta.allbank.utils.messages.MessageDisplay;
import licenta.allbank.utils.server.CallbackNoContent;

import static licenta.allbank.service.ServiceServer.ERROR_MESSAGE_SERVER;
import static licenta.allbank.ui.budget.NewBudgetFragment.NEW_BUDGET;
import static licenta.allbank.ui.home.HomeFragment.navigateToNextFragment;
import static licenta.allbank.ui.homeAdvanced.HomeAdvancedFragment.formatDateToString;
import static licenta.allbank.ui.homeAdvanced.HomeAdvancedFragment.setTextViewText;
import static licenta.allbank.ui.statistics.StatisticsBudgetingFragment.BUDGET;
import static licenta.allbank.ui.statistics.StatisticsCategoryFragment.copyCategoryListObjects;
import static licenta.allbank.utils.DateFormatGMT.convertDateToString;

public class EditBudgetFragment extends Fragment implements ClickInterfaceCategory {
    private TransactionCategoryOptionAdapter transactionCategoryOptionAdapter;
    private RecyclerView recyclerViewCategory;
    private MaterialButton confirmButton;
    private EditText editTextStartDate, editTextEndDate, editTextBudget;

    private DateTime dateFrom, dateTo;
    private String categorySelected;
    private Budget budgetObj;
    private float budget;
    private List<CategoryOption> categoryOptionList;
    private StatisticsBudgetingViewModel statisticsBudgetingViewModel;
    private NavController navController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        statisticsBudgetingViewModel = new ViewModelProvider(this).get(StatisticsBudgetingViewModel.class);

        Bundle bundle = getArguments();
        if (bundle != null) {
            budgetObj = bundle.getParcelable(BUDGET);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* Inflate the layout for this fragment */
        View view = inflater.inflate(R.layout.fragment_new_budget, container, false);

        /* Init UI elements */
        initUiElements(view);

        /* Modify view elements */
        modifyViewElements();

        /* Init default values */
        initDefaultValues();

        /* Init the buttons behaviour */
        initBehaviours();

        return view;
    }

    private void initUiElements(View view) {
        this.recyclerViewCategory = view.findViewById(R.id.recycler_view_new_budget);
        this.confirmButton = view.findViewById(R.id.button_new_budget_next);

        this.editTextStartDate = view.findViewById(R.id.edit_text_new_budget_start_date_picker);
        this.editTextEndDate = view.findViewById(R.id.edit_text_new_budget_end_date_picker);
        this.editTextBudget = view.findViewById(R.id.edit_text_new_budget_sum);
    }

    private void initBehaviours() {
        initRecyclerViewCategories();
        initStartDatePicker();
        initEndDatePicker();
        initConfirmButton();
    }

    private void initDefaultValues() {
        /* Set the selected category in recycler */
        categoryOptionList = copyCategoryListObjects();
        categoryOptionList.remove(0);

        for (CategoryOption categoryOption : categoryOptionList) {
            if (categoryOption.getCategoryType().equalsIgnoreCase(budgetObj.getCategory())) {
                categoryOption.setSelected(true);
                categorySelected = categoryOption.getCategoryType();
            }
        }

        /* Set startDate */
        dateFrom = budgetObj.getStartDate();
        dateTo = budgetObj.getEndDate();

        editTextStartDate.setText(convertDateToString(budgetObj.getStartDate()));
        editTextEndDate.setText(convertDateToString(budgetObj.getEndDate()));

        /* Set budget */
        budget = budgetObj.getBudget();
        setTextViewText(editTextBudget, budget);
    }

    private void initRecyclerViewCategories() {
        setLayoutManagerRecyclerViewCategory();
        setAdapterRecyclerViewCategory(categoryOptionList);
    }

    private void modifyViewElements() {
        confirmButton.setText("Edit");
    }

    private void initConfirmButton() {
        confirmButton.setOnClickListener(v -> {
            /* Check if all fields were completed */
            if (checkEditTextNotNull(editTextStartDate) && checkEditTextNotNull(editTextEndDate) && checkEditTextNotNull(editTextBudget)) {
                budget = Float.parseFloat(editTextBudget.getText().toString().trim());

                budgetObj.setBudget(budget);
                budgetObj.setCategory(categorySelected);
                budgetObj.setEndDate(dateTo);
                budgetObj.setStartDate(dateFrom);

                /* Save into local database and send it to the server */
                try {
                    ServiceServer serviceServer = ServiceServer.getInstance(getContext());
                    serviceServer.editBudget(ServiceServer.getAccessToken(), budgetObj, new CallbackNoContent() {
                        @Override
                        public void onSuccess() {
                            /* Add in local database and go back to menu */
                            statisticsBudgetingViewModel.update(budgetObj);

                            MessageDisplay.showLongMessage(getContext(), BudgetMessage.BUDGET_EDITED);
//                        getActivity().onBackPressed();
                            navigateToNextFragment(navController, R.id.action_editBudgetFragment_to_navigation_statistics, null);
                        }

                        @Override
                        public void onError(Throwable t) {
                            String errorMessage;
                            if (t.getMessage() != null && !t.getMessage().isEmpty()) {
                                errorMessage = t.getMessage();
                            } else {
                                errorMessage = BudgetMessage.BUDGET_NOT_ADDED;
                            }

                            MessageDisplay.showLongMessage(getContext(), errorMessage);
//                        getActivity().onBackPressed();
                            navigateToNextFragment(navController, R.id.action_editBudgetFragment_to_navigation_statistics, null);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    MessageDisplay.showLongMessage(getContext(), ERROR_MESSAGE_SERVER);
                    navigateToNextFragment(navController, R.id.action_editBudgetFragment_to_navigation_statistics, null);
                }
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
                datePickerDialog.getDatePicker().setMaxDate(dateTo.getMillis());
            } else if (datePicker == editTextEndDate && dateFrom != null) {
                datePickerDialog.getDatePicker().setMinDate(dateFrom.getMillis());
            }
            datePickerDialog.show();
        });
    }

    private void setLayoutManagerRecyclerViewCategory() {
        FlexboxLayoutManager manager = new FlexboxLayoutManager(getContext(), FlexDirection.ROW);
        manager.setJustifyContent(JustifyContent.CENTER);
        GridLayoutManager gridLayoutManager = (new GridLayoutManager(getContext(), 3));
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

    private boolean checkEditTextNotNull(EditText editText) {
        String ERROR_MESSAGE = "Please fill in all the dates!";
        if (editText == editTextStartDate && dateFrom != null) {
            return true;
        }
        if (editText == editTextEndDate && dateTo != null) {
            return true;
        }

        if (editText == editTextBudget && editTextBudget.getText().toString() != null && !editTextBudget.getText().toString().isEmpty()) {
            return true;
        }
        Toast.makeText(getContext(), ERROR_MESSAGE, Toast.LENGTH_LONG).show();
        return false;
    }
}
