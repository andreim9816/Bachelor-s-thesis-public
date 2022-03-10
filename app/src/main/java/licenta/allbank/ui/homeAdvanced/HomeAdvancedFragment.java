package licenta.allbank.ui.homeAdvanced;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import licenta.allbank.R;
import licenta.allbank.data.adapter.TransactionListAdapter;
import licenta.allbank.data.adapter.TransactionOptionAdapter;
import licenta.allbank.data.model.allbank.others.TransactionDetails;
import licenta.allbank.data.model.allbank.others.TransactionOption;
import licenta.allbank.data.model.database.Transaction;
import licenta.allbank.data.model.database.TransactionTypeAverageBank;
import licenta.allbank.data.model.database.TransactionTypeSum;
import licenta.allbank.service.ServiceBcr;
import licenta.allbank.service.ServiceBt;
import licenta.allbank.utils.DateFormatGMT;
import licenta.allbank.utils.click_interface.ClickInterfaceTimeOption;
import licenta.allbank.utils.click_interface.ClickInterfaceTransaction;
import licenta.allbank.utils.graphics.charts.GraphicsPieChart;

import static licenta.allbank.service.ServiceServer.closeDialogProgressBar;
import static licenta.allbank.service.ServiceServer.displayDialogProgressBar;
import static licenta.allbank.ui.home.HomeFragment.EXPENSES_TRANSACTIONS;
import static licenta.allbank.ui.home.HomeFragment.INCOME_TRANSACTIONS;
import static licenta.allbank.ui.home.HomeFragment.TRANSACTION_DETAILS;
import static licenta.allbank.ui.home.HomeFragment.convertTransactionToTransactionDetailsObject;
import static licenta.allbank.ui.home.HomeFragment.navigateToNextFragment;
import static licenta.allbank.utils.graphics.charts.GraphicsPieChart.setTextViewDefaultColor;

public class HomeAdvancedFragment extends Fragment implements ClickInterfaceTimeOption, ClickInterfaceTransaction {
    private Button textViewAllAccounts, textViewBcrAccounts, textViewBtAccounts;
    private TextView textViewIncome, textViewExpenses, textViewTotal;
    private TextView textViewIncomeText, textViewExpensesText, textViewTotalText;
    private EditText editTextStartDate, editTextEndDate;
    private SwitchMaterial switchButton;
    private RecyclerView recyclerViewFilter, recyclerViewTransactions;
    private NestedScrollView nestedScrollView;

    private TransactionListAdapter transactionListAdapter;
    private static List<Transaction> currentTransactions;

    private TransactionOptionAdapter transactionOptionAdapter;
    private NavController navController;

    private final String pieChartLabel = "Transactions chart";

    private PieChart pieChart;
    private GraphicsPieChart graphicsPieChart;
    private HomeAdvancedViewModel homeAdvancedViewModel;

    /* Variables used for transactions filter */
    private String accountsBankFilterOption;
    private boolean transactionsFilterSelected;
    private int transactionsFilterOptionDaysInThePast;
    /* Current date interval for transactions history */
    private DateTime dateFrom, dateTo;
    /* Dialog pop-up being showed*/
    private Dialog dialog;

    private float pastThreeMonthsIncomeBcr = 0, pastThreeMonthsIncomeBt = 0, pastThreeMonthsIncomeAll = 0;
    private float pastThreeMonthsExpenseBcr = 0, pastThreeMonthsExpenseBt = 0, pastThreeMonthsExpenseAll = 0;

    public enum accountsFilter {
        ALL {
            @NonNull
            @Override
            public String toString() {
                return "ALL";
            }
        },
        BCR {
            @NonNull
            @Override
            public String toString() {
                return ServiceBcr.BCR;
            }
        },
        BT {
            @NonNull
            @Override
            public String toString() {
                return ServiceBt.BT;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeAdvancedViewModel = new ViewModelProvider(this).get(HomeAdvancedViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* Inflate the layout for this fragment */
        View view = inflater.inflate(R.layout.fragment_home_advanced, container, false);

        /* Init UI elements */
        initUiElements(view);

        /* Init the buttons behaviour */
        initBehaviours();

        /* Init default values */
        initDefaultValues();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        dialog = new Dialog(getContext());
        displayDialogProgressBar(dialog);
        homeAdvancedViewModel.getTransactions(dateFrom, dateTo).observe(getViewLifecycleOwner(), transactions -> setTransactions(transactionListAdapter, transactions));
        homeAdvancedViewModel.getTransactionsTypeSum(dateFrom, dateTo).observe(getViewLifecycleOwner(), this::setPieChartData);
    }

    private void initUiElements(View view) {
        switchButton = view.findViewById(R.id.switch_home_advanced);
//        nestedScrollView = view.findViewById(R.id.nested_scroll_view_home_advanced);
        recyclerViewFilter = view.findViewById(R.id.recyclerview_filter_home_advanced);
        recyclerViewTransactions = view.findViewById(R.id.recyclerview_transactions_home_advanced);
        editTextStartDate = view.findViewById(R.id.editText_home_advanced_start_date_picker);
        editTextEndDate = view.findViewById(R.id.editText_home_advanced_end_date_picker);
        textViewAllAccounts = view.findViewById(R.id.textView_home_advanced_all);
        textViewBcrAccounts = view.findViewById(R.id.textView_home_advanced_bcr);
        textViewBtAccounts = view.findViewById(R.id.textView_home_advanced_bt);

        textViewIncome = view.findViewById(R.id.text_view_home_advanced_income);
        textViewExpenses = view.findViewById(R.id.text_view_home_advanced_expenses);
        textViewTotal = view.findViewById(R.id.text_view_home_advanced_total_difference);

        textViewIncomeText = view.findViewById(R.id.text_view_home_advanced_total_income);
        textViewExpensesText = view.findViewById(R.id.text_view_home_advanced_total_expenses);
        textViewTotalText = view.findViewById(R.id.text_view_home_advanced_total);

        pieChart = view.findViewById(R.id.pieChart_home_advanced);
    }

    private void initBehaviours() {
        initSwitchButton();
        initRecyclerViewTransactionOptions();
        initRecyclerViewTransactions();
        initStartDatePicker();
        initEndDatePicker();
        initPieChart();
        initFilterAccounts(textViewAllAccounts);
        initFilterAccounts(textViewBcrAccounts);
        initFilterAccounts(textViewBtAccounts);
    }

    private void initDefaultValues() {
        /* Bank account type */
        unSelectButton(textViewBcrAccounts);
        unSelectButton(textViewBtAccounts);
        selectButton(textViewAllAccounts);
        accountsBankFilterOption = textViewAllAccounts.getText().toString();

        /* Shortcut for the transactions period */
        transactionsFilterOptionDaysInThePast = 2;
        transactionOptionAdapter.updateUI(2);
        transactionsFilterSelected = true;

        /* Display last month payments*/
        dateFrom = DateFormatGMT.getDatePastDays(30 - 1);
        dateTo = DateFormatGMT.getTodayDate();

        /* Init statistics TextView */
        setStatisticsTextView();

        /* Get statistics for the past three months */
        DateTime today = DateFormatGMT.getTodayDate();
        DateTime threeMonthsPast = DateFormatGMT.getDatePastDays(90 - 1);

        homeAdvancedViewModel.getAverageTransaction(threeMonthsPast, today).observe(getViewLifecycleOwner(), transactionTypeAverageBanks -> {
            for (TransactionTypeAverageBank t : transactionTypeAverageBanks) {
                if (t.getTransactionType().equalsIgnoreCase(INCOME_TRANSACTIONS)) {
                    if (t.getBank().equalsIgnoreCase(ServiceBcr.BCR)) {
                        pastThreeMonthsIncomeBcr = roundTwoDecimals(t.getTransactionAvg());
                    } else if (t.getBank().equalsIgnoreCase(ServiceBt.BT)) {
                        pastThreeMonthsIncomeBt = roundTwoDecimals(t.getTransactionAvg());
                    }
                } else {
                    if (t.getBank().equalsIgnoreCase(ServiceBcr.BCR)) {
                        pastThreeMonthsExpenseBcr = roundTwoDecimals(t.getTransactionAvg());
                    } else if (t.getBank().equalsIgnoreCase(ServiceBt.BT)) {
                        pastThreeMonthsExpenseBt = roundTwoDecimals(t.getTransactionAvg());
                    }
                }
            }

            pastThreeMonthsExpenseAll = roundTwoDecimals(pastThreeMonthsExpenseBcr + pastThreeMonthsExpenseBt);
            pastThreeMonthsIncomeAll = roundTwoDecimals(pastThreeMonthsIncomeBcr + pastThreeMonthsIncomeBt);

            graphicsPieChart.setStatisticsData(pastThreeMonthsIncomeAll, pastThreeMonthsIncomeBcr, pastThreeMonthsIncomeBt, pastThreeMonthsExpenseAll, pastThreeMonthsExpenseBcr, pastThreeMonthsExpenseBt);
        });
    }

    private void initSwitchButton() {
        switchButton.setChecked(true);
        switchButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked)
                navigateToNextFragment(navController, R.id.action_homeAdvancedFragment_to_navigation_home, null);
        });
    }

    private void initRecyclerViewTransactionOptions() {
        List<TransactionOption> transactionOptions = new ArrayList<>(
                Arrays.asList(
                        new TransactionOption(1, "Today"),
                        new TransactionOption(7, "Last week"),
                        new TransactionOption(30, "Last month"),
                        new TransactionOption(90, "Last 3 months"),
                        new TransactionOption(180, "Last 6 months"),
                        new TransactionOption(365, "Last year"))
        );
        setLayoutManagerRecyclerFilter();
        setAdapterRecyclerFilter(transactionOptions);
    }

    private void initRecyclerViewTransactions() {
        setLayoutManagerRecyclerTransactions();
        setAdapterRecyclerTransactions();
    }

    private void initDatePicker(EditText datePicker) {
        final Calendar myCalendar = Calendar.getInstance();

        /* Create DatePickerDialog and its behaviour */
        DatePickerDialog.OnDateSetListener date = (view, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month + 1);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            datePicker.setText(formatDateToString(dayOfMonth, month + 1, year));

            clearRecyclerViewTransactionsFilterOptions();
            if (datePicker == editTextStartDate) {
                dateFrom = DateFormatGMT.convertDataToDate(dayOfMonth, month + 1, year);
            } else {
                dateTo = DateFormatGMT.convertDataToDate(dayOfMonth, month + 1, year);
            }

            if (dateTo != null && dateFrom != null && !transactionsFilterSelected) {
                dialog = new Dialog(getContext());
                displayDialogProgressBar(dialog);
                if (accountsBankFilterOption.equalsIgnoreCase(accountsFilter.ALL.toString())) {
                    homeAdvancedViewModel.getTransactions(dateFrom, dateTo).observe(getViewLifecycleOwner(), transactions -> setTransactions(transactionListAdapter, transactions));
                    homeAdvancedViewModel.getTransactionsTypeSum(dateFrom, dateTo).observe(getViewLifecycleOwner(), this::setPieChartData);
                } else {
                    homeAdvancedViewModel.getTransactionsByBank(dateFrom, dateTo, accountsBankFilterOption).observe(getViewLifecycleOwner(), transactions -> setTransactions(transactionListAdapter, transactions));
                    homeAdvancedViewModel.getTransactionsTypeSum(dateFrom, dateTo, accountsBankFilterOption).observe(getViewLifecycleOwner(), this::setPieChartData);
                }
                setStatisticsTextView();
            }
            transactionsFilterSelected = false;
        };

        /* Add onClickListener on DatePicker */
        datePicker.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));

            /* Disable any future dates */
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

            /* Set only valid intervals */
            if (datePicker == editTextStartDate && dateTo != null) {
                datePickerDialog.getDatePicker().setMaxDate(dateTo.minusDays(1).getMillis());
            } else if (datePicker == editTextEndDate && dateFrom != null && !transactionsFilterSelected) {
                datePickerDialog.getDatePicker().setMinDate(dateFrom.getMillis());
            }
            datePickerDialog.show();
        });
    }

    private void initStartDatePicker() {
        initDatePicker(editTextStartDate);
    }

    private void initEndDatePicker() {
        initDatePicker(editTextEndDate);
    }

    private void initPieChart() {
        graphicsPieChart = new GraphicsPieChart(pieChart, transactionListAdapter, currentTransactions, this, homeAdvancedViewModel, textViewTotal, textViewIncome, textViewExpenses, textViewTotalText, textViewIncomeText, textViewExpensesText);
    }

    private void initFilterAccounts(Button button) {
        button.setOnClickListener(v -> {
            /* deselect all the textView options */
            unSelectButton(textViewAllAccounts);
            unSelectButton(textViewBcrAccounts);
            unSelectButton(textViewBtAccounts);

            /* Select the current textView */
            selectButton(button);

            /* Update the current choice */
            accountsBankFilterOption = button.getText().toString();

            /* Clear textView colors */
            setTextViewsDefaultColor();

            if (dateFrom != null && dateTo != null) {
                dialog = new Dialog(getContext());
                displayDialogProgressBar(dialog);
                if (!accountsBankFilterOption.equalsIgnoreCase(accountsFilter.ALL.toString())) {
                    homeAdvancedViewModel.getTransactionsByBank(dateFrom, dateTo, accountsBankFilterOption).observe(getViewLifecycleOwner(), transactions -> setTransactions(transactionListAdapter, transactions));
                    homeAdvancedViewModel.getTransactionsTypeSum(dateFrom, dateTo, accountsBankFilterOption).observe(getViewLifecycleOwner(), this::setPieChartData);
                } else {
                    homeAdvancedViewModel.getTransactions(dateFrom, dateTo).observe(getViewLifecycleOwner(), transactions -> setTransactions(transactionListAdapter, transactions));
                    homeAdvancedViewModel.getTransactionsTypeSum(dateFrom, dateTo).observe(getViewLifecycleOwner(), this::setPieChartData);
                }
                setStatisticsTextView();
                clearPieChart();
            }
        });
    }

    private void setPieChartData(String accountsBankFilterOption, DateTime dateFrom, DateTime dateTo, GraphicsPieChart graphicsPieChart, String label1, float value1, String label2,
                                 float value2, String chartLabel) {
        graphicsPieChart.setData(accountsBankFilterOption, dateFrom, dateTo, label1, value1, label2, value2, chartLabel, true);
    }

    private void setLayoutManagerRecyclerFilter() {
        recyclerViewFilter.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
    }

    private void setLayoutManagerRecyclerTransactions() {
        recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false) {
            @Override
            public void onLayoutCompleted(RecyclerView.State state) {
                super.onLayoutCompleted(state);
                closeDialogProgressBar(dialog);
                recyclerViewTransactions.smoothScrollToPosition(0);
//                nestedScrollView.smoothScrollTo(0, 0);
            }
        });
    }

    private void setAdapterRecyclerTransactions() {
        transactionListAdapter = new TransactionListAdapter(getContext(), this);
        recyclerViewTransactions.setAdapter(transactionListAdapter);
    }

    private void setAdapterRecyclerFilter(List<TransactionOption> transactionOptions) {
        transactionOptionAdapter = new TransactionOptionAdapter(getContext(), this, transactionOptions, this);
        recyclerViewFilter.setAdapter(transactionOptionAdapter);
    }

    public static String formatDateToString(int day, int month, int year) {
        return day + "/" + month + "/" + year;
    }

    public void unSelectButton(Button button) {
        button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.text_color));
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_dark));
    }

    public void selectButton(Button button) {
        button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary_dark));
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color));
    }

    @Override
    public void onClickTimeOption(int position) {
        Log.i("HomeAdvanced", "" + position);
        TransactionOption transactionOption = transactionOptionAdapter.getTransactionOption(position);
        transactionOptionAdapter.updateUI(position);
        transactionsFilterOptionDaysInThePast = transactionOption.getPastDays();

        /* Clear the UI editTexts and their internal variables */
        editTextStartDate.setText("");
        editTextEndDate.setText("");
        transactionsFilterSelected = true;

        /* Clear textView colors */
        setTextViewsDefaultColor();

        dateFrom = DateFormatGMT.getDatePastDays(transactionsFilterOptionDaysInThePast - 1);
        dateTo = DateFormatGMT.getTodayDate(); // DateFormatGMT.getNextDay(DateFormatGMT.getTodayDate());

        dialog = new Dialog(getContext());
        displayDialogProgressBar(dialog);

        if (accountsBankFilterOption.equalsIgnoreCase(accountsFilter.ALL.toString())) {
            homeAdvancedViewModel.getTransactions(dateFrom, dateTo).observe(getViewLifecycleOwner(), transactions -> setTransactions(transactionListAdapter, transactions));
            homeAdvancedViewModel.getTransactionsTypeSum(dateFrom, dateTo).observe(getViewLifecycleOwner(), this::setPieChartData);
        } else {
            homeAdvancedViewModel.getTransactionsByBank(dateFrom, dateTo, accountsBankFilterOption).observe(getViewLifecycleOwner(), transactions -> setTransactions(transactionListAdapter, transactions));
            homeAdvancedViewModel.getTransactionsTypeSum(dateFrom, dateTo, accountsBankFilterOption).observe(getViewLifecycleOwner(), this::setPieChartData);
        }
        setStatisticsTextView();
        clearPieChart();
    }

    @Override
    public void onClickTransaction(int position) {
        Transaction transaction = transactionListAdapter.getTransaction(position);
        TransactionDetails transactionDetails = convertTransactionToTransactionDetailsObject(transaction);

        Bundle bundle = new Bundle();
        bundle.putParcelable(TRANSACTION_DETAILS, transactionDetails);
        navigateToNextFragment(navController, R.id.action_homeAdvancedFragment_to_transactionFragment, bundle);
    }

    private void clearRecyclerViewTransactionsFilterOptions() {
        transactionsFilterOptionDaysInThePast = -1;
        transactionOptionAdapter.updateUI(transactionsFilterOptionDaysInThePast);
    }

    private void setPieChartData(List<TransactionTypeSum> list) {
        float totalIncome = 0f, totalExpense = 0f, total = 0f;
        if (!(list == null || list.size() == 0)) {
            if (list.get(0).getTransactionType().equalsIgnoreCase(EXPENSES_TRANSACTIONS)) {
                totalExpense = (-1) * roundTwoDecimals(list.get(0).getSum());
                if (list.size() > 1) {
                    totalIncome = roundTwoDecimals(list.get(1).getSum());
                }
            } else {
                if (list.size() > 1) {
                    totalExpense = (-1) * roundTwoDecimals(list.get(1).getSum());
                }
                totalIncome = roundTwoDecimals(list.get(0).getSum());
            }
        }

        total = totalIncome - totalExpense;
        setTextViewTextWithCurrency(textViewTotal, total);
        setTextViewTextWithCurrency(textViewIncome, totalIncome);
        setTextViewTextWithCurrency(textViewExpenses, totalExpense);

        if (total > 0) {
            textViewTotal.setTextColor(Color.GREEN);
        } else if (total < 0) {
            textViewTotal.setTextColor(Color.RED);
        }
        clearPieChart();
        setPieChartData(accountsBankFilterOption, dateFrom, dateTo, graphicsPieChart, INCOME_TRANSACTIONS, totalIncome, EXPENSES_TRANSACTIONS, totalExpense, pieChartLabel);
    }

    public static void setTextViewText(TextView textView, String text) {
        textView.setText(text);
    }

    public static void setTextViewText(TextView textView, float number) {
        setTextViewText(textView, number + "");
    }

    public static void setTextViewTextWithCurrency(TextView textView, String text) {
        setTextViewText(textView, text + " RON");
    }

    public static void setTextViewTextWithCurrency(TextView textView, float text) {
        setTextViewText(textView, text + " RON");
    }

    /**
     * Function that rounds a float number to 2 digits
     *
     * @param number
     * @return
     */
    public static float roundTwoDecimals(float number) {
        return (float) (Math.round(number * 100.00) / 100.00);
    }

    private void setStatisticsTextView() {
        setTextViewText(textViewIncomeText, getResources().getString(R.string.total_income));
        setTextViewText(textViewExpensesText, getResources().getString(R.string.total_expenses));
        setTextViewText(textViewTotalText, getResources().getString(R.string.total));
    }

    private void clearPieChart() {
        pieChart.highlightValues(null);
        pieChart.invalidate();
    }

    private void setTextViewsDefaultColor() {
        setTextViewDefaultColor(textViewTotal);
        setTextViewDefaultColor(textViewIncome);
        setTextViewDefaultColor(textViewExpenses);
    }

    /**
     * Function that sets the transactions list to the given adapter
     *
     * @param adapter
     * @param transactions
     */
    public static void setTransactions(TransactionListAdapter adapter, List<Transaction> transactions) {
        adapter.setTransactions(transactions);
    }
}