package licenta.allbank.ui.statistics;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import licenta.allbank.R;
import licenta.allbank.data.adapter.TransactionCategoryOptionAdapter;
import licenta.allbank.data.adapter.TransactionListAdapter;
import licenta.allbank.data.adapter.TransactionOptionAdapter;
import licenta.allbank.data.model.allbank.others.CategoryOption;
import licenta.allbank.data.model.allbank.others.TransactionDetails;
import licenta.allbank.data.model.allbank.others.TransactionOption;
import licenta.allbank.data.model.database.CategoryTypeSum;
import licenta.allbank.data.model.database.Transaction;
import licenta.allbank.data.model.database.TransactionTypeDateSum;
import licenta.allbank.utils.DateFormatGMT;
import licenta.allbank.utils.click_interface.ClickInterfaceCategory;
import licenta.allbank.utils.click_interface.ClickInterfaceTimeOption;
import licenta.allbank.utils.click_interface.ClickInterfaceTransaction;
import licenta.allbank.utils.graphics.charts.StatisticsBarChart;
import licenta.allbank.utils.graphics.charts.StatisticsPieChart;

import static licenta.allbank.ui.home.HomeFragment.TRANSACTION_DETAILS;
import static licenta.allbank.ui.home.HomeFragment.convertTransactionToTransactionDetailsObject;
import static licenta.allbank.ui.home.HomeFragment.navigateToNextFragment;
import static licenta.allbank.ui.homeAdvanced.HomeAdvancedFragment.formatDateToString;
import static licenta.allbank.ui.homeAdvanced.HomeAdvancedFragment.roundTwoDecimals;
import static licenta.allbank.ui.homeAdvanced.HomeAdvancedFragment.setTextViewText;
import static licenta.allbank.ui.homeAdvanced.HomeAdvancedFragment.setTextViewTextWithCurrency;
import static licenta.allbank.ui.homeAdvanced.HomeAdvancedFragment.setTransactions;
import static licenta.allbank.ui.statistics.StatisticsBudgetingFragment.STATISTICS_BUDGET;
import static licenta.allbank.utils.DateFormatGMT.convertDateToStringBarChartLabel;
import static org.joda.time.Days.daysBetween;

public class StatisticsCategoryFragment extends Fragment implements ClickInterfaceTimeOption, ClickInterfaceCategory, ClickInterfaceTransaction {
    public static final String CATEGORY_ALL = "All";
    public static final String CATEGORY_HOME = "Home";
    public static final String CATEGORY_FOOD = "Food";
    public static final String CATEGORY_FUN = "Fun";
    public static final String CATEGORY_SHOPPING = "Shopping";
    public static final String CATEGORY_STUDY = "Study";
    public static final String CATEGORY_TRAVEL = "Travel";
    public static final String CATEGORY_OTHERS = "Others";

    /* Mapper for transactionType -> color */
    public static final Map<String, Integer> categoryColorHashMap = Map.of(
            CATEGORY_HOME, Color.BLUE,
            CATEGORY_FOOD, Color.GREEN,
            CATEGORY_FUN, Color.RED,
            CATEGORY_SHOPPING, Color.MAGENTA,
            CATEGORY_STUDY, Color.YELLOW,
            CATEGORY_TRAVEL, Color.CYAN,
            CATEGORY_OTHERS, Color.GRAY
    );

    /* Mapper for transactionType category -> image */
    public static final Map<String, Integer> categoryImageHashMapUnselected = Map.of(
            CATEGORY_ALL, R.drawable.ic_category_all_unselected,
            CATEGORY_HOME, R.drawable.ic_category_home_unselected,
            CATEGORY_FOOD, R.drawable.ic_category_food_unselected,
            CATEGORY_FUN, R.drawable.ic_category_fun_unselected,
            CATEGORY_SHOPPING, R.drawable.ic_category_shopping_unselected,
            CATEGORY_STUDY, R.drawable.ic_category_study_unselected,
            CATEGORY_TRAVEL, R.drawable.ic_category_travel_unselected,
            CATEGORY_OTHERS, R.drawable.ic_category_others_unselected
    );

    public static final Map<String, Integer> categoryImageHashMapSelected = Map.of(
            CATEGORY_ALL, R.drawable.ic_category_all_selected,
            CATEGORY_HOME, R.drawable.ic_category_home_selected,
            CATEGORY_FOOD, R.drawable.ic_category_food_selected,
            CATEGORY_FUN, R.drawable.ic_category_fun_selected,
            CATEGORY_SHOPPING, R.drawable.ic_category_shopping_selected,
            CATEGORY_STUDY, R.drawable.ic_category_study_selected,
            CATEGORY_TRAVEL, R.drawable.ic_category_travel_selected,
            CATEGORY_OTHERS, R.drawable.ic_category_others_selected
    );

    /* List of categories for recycler view adapter */
    private static final List<CategoryOption> categoryOptions = new ArrayList<>(
            Arrays.asList(
                    new CategoryOption(CATEGORY_ALL, categoryImageHashMapUnselected.getOrDefault(CATEGORY_ALL, R.drawable.ic_home_black_24dp), categoryImageHashMapSelected.getOrDefault(CATEGORY_ALL, R.drawable.ic_home_black_24dp), true, categoryColorHashMap.getOrDefault(CATEGORY_ALL, 0)),
                    new CategoryOption(CATEGORY_FOOD, categoryImageHashMapUnselected.getOrDefault(CATEGORY_FOOD, R.drawable.ic_home_black_24dp), categoryImageHashMapSelected.getOrDefault(CATEGORY_FOOD, R.drawable.ic_home_black_24dp), false, categoryColorHashMap.getOrDefault(CATEGORY_FOOD, 0)),
                    new CategoryOption(CATEGORY_HOME, categoryImageHashMapUnselected.getOrDefault(CATEGORY_HOME, R.drawable.ic_home_black_24dp), categoryImageHashMapSelected.getOrDefault(CATEGORY_HOME, R.drawable.ic_home_black_24dp), false, categoryColorHashMap.getOrDefault(CATEGORY_HOME, 0)),
                    new CategoryOption(CATEGORY_FUN, categoryImageHashMapUnselected.getOrDefault(CATEGORY_FUN, R.drawable.ic_home_black_24dp), categoryImageHashMapSelected.getOrDefault(CATEGORY_FUN, R.drawable.ic_home_black_24dp), false, categoryColorHashMap.getOrDefault(CATEGORY_FUN, 0)),
                    new CategoryOption(CATEGORY_SHOPPING, categoryImageHashMapUnselected.getOrDefault(CATEGORY_SHOPPING, R.drawable.ic_home_black_24dp), categoryImageHashMapSelected.getOrDefault(CATEGORY_SHOPPING, R.drawable.ic_home_black_24dp), false, categoryColorHashMap.getOrDefault(CATEGORY_SHOPPING, 0)),
                    new CategoryOption(CATEGORY_STUDY, categoryImageHashMapUnselected.getOrDefault(CATEGORY_STUDY, R.drawable.ic_home_black_24dp), categoryImageHashMapSelected.getOrDefault(CATEGORY_STUDY, R.drawable.ic_home_black_24dp), false, categoryColorHashMap.getOrDefault(CATEGORY_STUDY, 0)),
                    new CategoryOption(CATEGORY_TRAVEL, categoryImageHashMapUnselected.getOrDefault(CATEGORY_TRAVEL, R.drawable.ic_home_black_24dp), categoryImageHashMapSelected.getOrDefault(CATEGORY_TRAVEL, R.drawable.ic_home_black_24dp), false, categoryColorHashMap.getOrDefault(CATEGORY_TRAVEL, 0)),
                    new CategoryOption(CATEGORY_OTHERS, categoryImageHashMapUnselected.getOrDefault(CATEGORY_OTHERS, R.drawable.ic_home_black_24dp), categoryImageHashMapSelected.getOrDefault(CATEGORY_OTHERS, R.drawable.ic_home_black_24dp), false, categoryColorHashMap.getOrDefault(CATEGORY_OTHERS, 0))
            )
    );

    /* List that stores all the category type names */
    public final static List<String> allCategoriesStringList = new ArrayList<>(
            Arrays.asList(
                    CATEGORY_HOME,
                    CATEGORY_FOOD,
                    CATEGORY_FUN,
                    CATEGORY_SHOPPING,
                    CATEGORY_STUDY,
                    CATEGORY_TRAVEL,
                    CATEGORY_OTHERS
            )
    );

    private StatisticsViewModel statisticsViewModel;
    private NavController navController;

    private RecyclerView recyclerViewCategoryFilter, recyclerViewDateFilter, recyclerViewTransactions;
    private TransactionOptionAdapter transactionOptionAdapter;
    private TransactionCategoryOptionAdapter transactionCategoryOptionAdapterFilter;
    private TransactionListAdapter transactionListAdapter;

    private PieChart pieChart;
    private StatisticsPieChart statisticsPieChart;

    private BarChart barChart;
    private StatisticsBarChart statisticsBarChart;

    private EditText editTextStartDate, editTextEndDate;
    private TextView textViewTotalSumSpent1, textViewTotalSumSpent2, textViewPercentage;
    private TextView dailyTextView, weeklyTextView, monthlyTextView;
    private TextView intervalTextView;
    private ImageView categoryImageView;


    /* Variables used for transactions filter */
    private boolean transactionsFilterSelected;
    private int transactionsFilterOptionDaysInThePast;
    /* Current date interval for transactions history */
    public static DateTime dateFrom, dateTo;

    /* List with the current chosen categories*/
    public static List<String> categorySelectedList = new ArrayList<>();

    /* Current categoryOption List that is being worked on */
    private List<CategoryOption> categoryOptionList;

    public static String pieChartCategorySelected = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statisticsViewModel = new ViewModelProvider(this).get(StatisticsViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* Inflate the layout for this fragment */
        View view = inflater.inflate(R.layout.fragment_statistics_category, container, false);

        /* Make a new copy of categoryOption objects */
        categoryOptionList = copyCategoryListObjects();

        /* categorySelectedList should be empty every time the view is created */
        categorySelectedList = new ArrayList<>();

        /* Init UI elements */
        initUiElements(view);

        /* Init the buttons behaviour */
        initBehaviours(view);

        /* Init default values */
        initDefaultValues();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        /* Get transactions for chart and recyclerView*/
        getTransactionsForRecyclerAndChart();
    }

    private void initUiElements(View view) {
        recyclerViewCategoryFilter = view.findViewById(R.id.recycler_view_statistics_category_filter);
        recyclerViewDateFilter = view.findViewById(R.id.recycler_view_statistics_date_filter);
        recyclerViewTransactions = view.findViewById(R.id.recyclerview_transactions_statistics);

        editTextStartDate = view.findViewById(R.id.edit_text_statistics_start_date_picker);
        editTextEndDate = view.findViewById(R.id.edit_text_statistics_end_date_picker);

        textViewTotalSumSpent1 = view.findViewById(R.id.text_view_statistics_total_sum_spent_1);
        textViewTotalSumSpent2 = view.findViewById(R.id.text_view_statistics_total_sum_spent_2);
        textViewPercentage = view.findViewById(R.id.text_view_statistics_total_percentage_difference);

        categoryImageView = view.findViewById(R.id.shapeableImageView);
        intervalTextView = view.findViewById(R.id.text_view_statisics_category_interval);

        dailyTextView = view.findViewById(R.id.text_view_statistics_average_daily);
        weeklyTextView = view.findViewById(R.id.text_view_statistics_average_weekly);
        monthlyTextView = view.findViewById(R.id.text_view_statistics_average_monthly);

        pieChart = view.findViewById(R.id.pie_chart_statistics);
        barChart = view.findViewById(R.id.bar_chart_statistics);
    }

    private void initBehaviours(View view) {
        initRecyclerViewDateFilter();
        initRecyclerViewCategoryFilter();
        initRecyclerViewTransactions();

        initStartDatePicker();
        initEndDatePicker();

        initPieChart(view);
        initBarChart();
    }

    private void initDefaultValues() {
        /* Default selected option is "Last month" */
        transactionsFilterOptionDaysInThePast = 30;
        transactionOptionAdapter.updateUI(2);
        transactionsFilterSelected = true;

        /* Current interval dates */
        /* VERY IMPORTANT. Change StatisticsPieChart initPieVisualBehaviours() function for dateFrom and dateTo */
        dateFrom = DateFormatGMT.getDatePastDays(transactionsFilterOptionDaysInThePast - 1);
        dateTo = DateFormatGMT.getTodayDate();

        /* By default no category is selected */
        pieChartCategorySelected = null;

        /* At first, the selected category is the ALL one */
        categorySelectedList.clear();
        categorySelectedList.add(CATEGORY_ALL);

        /* At first, display all transactions */
        statisticsViewModel.getTransactionsByCategory(dateFrom, dateTo, allCategoriesStringList).observe(getViewLifecycleOwner(), transactions -> setTransactions(transactionListAdapter, transactions));

        /* At first, display the spent sum on ALL categories in the past 30 days */
        setStatisticsSumTextView();

        /* Update dates text view */
        updateSelectedCategoryStatistics(categoryImageView, textViewPercentage, intervalTextView, textViewTotalSumSpent1, textViewTotalSumSpent2, statisticsViewModel, this);

        /* Update statistics displayed on the 3 cardViews */
        statisticsViewModel.getTransactionsSumForCategories(dateFrom, dateTo, getSelectedCategoryList()).observe(getViewLifecycleOwner(), this::displayCardViewStatistics);
    }

    private void setStatisticsSumTextView() {
        List<String> categoryList = getSelectedCategoryList();

        /* Don't display the category unless there is only one selected by clicking on pie chart */
//        textViewCategorySelected.setVisibility(View.GONE);

        /* Spent on the selected category/categories */
        statisticsViewModel.getTransactionsSumForCategories(dateFrom, dateTo, categoryList).observe(getViewLifecycleOwner(), sum -> {
            if (sum == null) {
                sum = 0f;
            }
            setTextViewTextWithCurrency(textViewTotalSumSpent1, roundTwoDecimals(sum));
        });

        /* Total spent */
        statisticsViewModel.getTransactionsSumForCategories(dateFrom, dateTo, allCategoriesStringList).observe(getViewLifecycleOwner(), sum -> {
            if (sum == null) {
                sum = 0f;
            }
            setTextViewTextWithCurrency(textViewTotalSumSpent2, roundTwoDecimals(sum));
        });
        // TODO display percentage
    }

    private static void setStatisticsDateTextView(TextView intervalTextView, DateTime dateFrom, DateTime dateTo) {
        setTextViewText(intervalTextView, convertDateToStringBarChartLabel(dateFrom) + " - " + convertDateToStringBarChartLabel(dateTo));
    }

    private void initRecyclerViewTransactions() {
        setLayoutManagerRecyclerTransactions();
        setAdapterRecyclerTransactions();
    }

    private void initRecyclerViewDateFilter() {
        List<TransactionOption> transactionOptions = new ArrayList<>(
                Arrays.asList(
                        new TransactionOption(1, "Today"),
                        new TransactionOption(7, "Last week"),
                        new TransactionOption(30, "Last month"),
                        new TransactionOption(90, "Last 3 months"),
                        new TransactionOption(180, "Last 6 months"),
                        new TransactionOption(365, "Last year"))
        );
        setLayoutManagerRecyclerViewDateFilter();
        setAdapterRecyclerViewDateFilter(transactionOptions);
    }

    private void initRecyclerViewCategoryFilter() {
        setLayoutManagerRecyclerViewCategoryFilter();
        setAdapterRecyclerViewCategoryFilter(categoryOptionList);
    }

    private void setLayoutManagerRecyclerViewDateFilter() {
        recyclerViewDateFilter.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
    }

    private void setLayoutManagerRecyclerViewCategoryFilter() {
        recyclerViewCategoryFilter.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
    }

    private void setLayoutManagerRecyclerTransactions() {
        recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false) {
            @Override
            public void onLayoutCompleted(RecyclerView.State state) {
                super.onLayoutCompleted(state);
                recyclerViewTransactions.smoothScrollToPosition(0);
            }
        });
    }

    private void setAdapterRecyclerViewDateFilter(List<TransactionOption> transactionOptions) {
        transactionOptionAdapter = new TransactionOptionAdapter(getContext(), this, transactionOptions, this);
        recyclerViewDateFilter.setAdapter(transactionOptionAdapter);
    }

    private void setAdapterRecyclerViewCategoryFilter(List<CategoryOption> categoryOptions) {
        transactionCategoryOptionAdapterFilter = new TransactionCategoryOptionAdapter(getContext(), categoryOptions, this, STATISTICS_BUDGET);
        recyclerViewCategoryFilter.setAdapter(transactionCategoryOptionAdapterFilter);
    }

    private void setAdapterRecyclerTransactions() {
        transactionListAdapter = new TransactionListAdapter(getContext(), this);
        recyclerViewTransactions.setAdapter(transactionListAdapter);
    }

    private void initPieChart(View view) {
        statisticsPieChart = new StatisticsPieChart(pieChart, statisticsViewModel, view, this);
    }

    private void setPieChartData(List<CategoryTypeSum> list) {
        statisticsPieChart.setData(dateFrom, dateTo, list, "", true);
    }

    private void initBarChart() {
        statisticsBarChart = new StatisticsBarChart(barChart, statisticsViewModel, this, transactionListAdapter);
    }

    private void setBarChartData(List<TransactionTypeDateSum> list) {
        statisticsBarChart.setData(dateFrom, dateTo, list, "", true);
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
                /* Get transactions for chart and recyclerView*/
                getTransactionsForRecyclerAndChart();

                /* Update the statistics text view*/
                setStatisticsSumTextView();

                /* Update dates text view */
                updateSelectedCategoryStatistics(categoryImageView, textViewPercentage, intervalTextView, textViewTotalSumSpent1, textViewTotalSumSpent2, statisticsViewModel, this);

                /* Update statistics displayed on the 3 cardViews */
                statisticsViewModel.getTransactionsSumForCategories(dateFrom, dateTo, getSelectedCategoryList()).observe(getViewLifecycleOwner(), this::displayCardViewStatistics);
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

    private void clearRecyclerViewTransactionsFilterOptions() {
        /* Function that clears ...daysInThePast value and updates recyclerView */
        transactionsFilterOptionDaysInThePast = -1;
        transactionOptionAdapter.updateUI(transactionsFilterOptionDaysInThePast);
    }

    @Override
    public void onClickTimeOption(int position) {
        TransactionOption transactionOption = transactionOptionAdapter.getTransactionOption(position);
        transactionOptionAdapter.updateUI(position);
        transactionsFilterOptionDaysInThePast = transactionOption.getPastDays();

        /* Clear the UI editTexts and their internal variables */
        editTextStartDate.setText("");
        editTextEndDate.setText("");
        transactionsFilterSelected = true;

        /* Update current interval dates */
        dateFrom = DateFormatGMT.getDatePastDays(transactionsFilterOptionDaysInThePast - 1);
        dateTo = DateFormatGMT.getTodayDate();

        /* Get transactions for chart and recyclerView*/
        getTransactionsForRecyclerAndChart();
        clearPieChart();

        /* Update the statistics text view*/
        setStatisticsSumTextView();

        /* Update dates text view */
        updateSelectedCategoryStatistics(categoryImageView, textViewPercentage, intervalTextView, textViewTotalSumSpent1, textViewTotalSumSpent2, statisticsViewModel, this);

        /* Update statistics displayed on the 3 cardViews */
        statisticsViewModel.getTransactionsSumForCategories(dateFrom, dateTo, getSelectedCategoryList()).observe(getViewLifecycleOwner(), this::displayCardViewStatistics);
    }

    @Override
    public void onClickCategory(int position) {
        /* If the selected category is not the ALL category, then update it */
        CategoryOption allCategoryOption = transactionCategoryOptionAdapterFilter.getCategoryOption(0);
        if (position != 0) {
            if (allCategoryOption.isSelected()) {
                allCategoryOption.setSelected(false);
                categorySelectedList.remove(CATEGORY_ALL);
            }

            CategoryOption categoryOption = transactionCategoryOptionAdapterFilter.getCategoryOption(position);
            boolean selected = categoryOption.isSelected();
            if (selected && (categorySelectedList.size() >= 3 && categorySelectedList.contains(CATEGORY_ALL) || categorySelectedList.size() >= 2 && !categorySelectedList.contains(CATEGORY_ALL))) {
                categorySelectedList.remove(categoryOption.getCategoryType());
                categoryOption.setSelected(false);
                /* Update the statistics text view*/
                setStatisticsSumTextView();
            } else if (!selected) {
                categorySelectedList.add(categoryOption.getCategoryType());
                categoryOption.setSelected(true);
                /* Update the statistics text view*/
                setStatisticsSumTextView();
            }
        } else {
            /* When the selected category is the ALL one, then clear the others */
            if (!allCategoryOption.isSelected()) {
                for (CategoryOption categoryOption : categoryOptionList) {
                    categoryOption.setSelected(false);
                }
                allCategoryOption.setSelected(true);
                categorySelectedList.clear();
                categorySelectedList.add(CATEGORY_ALL);
                /* Update the statistics text view*/
                setStatisticsSumTextView();
            }
        }

        /* Update dates text view */
        updateSelectedCategoryStatistics(categoryImageView, textViewPercentage, intervalTextView, textViewTotalSumSpent1, textViewTotalSumSpent2, statisticsViewModel, this);

        /* Update category recycler and charts data */
        transactionCategoryOptionAdapterFilter.updateUI();
        getTransactionsForRecyclerAndChart();

        /* Update statistics displayed on the 3 cardViews */
        statisticsViewModel.getTransactionsSumForCategories(dateFrom, dateTo, getSelectedCategoryList()).observe(getViewLifecycleOwner(), this::displayCardViewStatistics);
    }

    @Override
    public void onClickTransaction(int position) {
        Transaction transaction = transactionListAdapter.getTransaction(position);
        TransactionDetails transactionDetails = convertTransactionToTransactionDetailsObject(transaction);

        Bundle bundle = new Bundle();
        bundle.putParcelable(TRANSACTION_DETAILS, transactionDetails);
        navigateToNextFragment(navController, R.id.action_navigation_statistics_to_transactionFragment, bundle);
    }

    private void getTransactionsForRecyclerAndChart() {
        /* Update BarChart, PieChart and Transaction recycler view. Check whether to show all categories or just part of it */
        List<String> transactionsCategoryList = getSelectedCategoryList();

        /* Update PieChart */
        statisticsViewModel.getTransactionStatisticsByCategory(dateFrom, dateTo, transactionsCategoryList).observe(getViewLifecycleOwner(), this::setPieChartData);
        /* Update BarChart */
        statisticsViewModel.getTransactionStatisticsByCategoryAndDate(dateFrom, dateTo, transactionsCategoryList).observe(getViewLifecycleOwner(), this::setBarChartData);
        /* Update transaction recycler view */
        statisticsViewModel.getTransactionsByCategory(dateFrom, dateTo, transactionsCategoryList).observe(getViewLifecycleOwner(), transactions -> setTransactions(transactionListAdapter, transactions));
    }

    private void clearPieChart() {
        pieChart.highlightValues(null);
        pieChart.invalidate();
    }


    public void displayCardViewStatistics(float sum) {
        /* Display data on the 3 cardViews: daily, weekly, monthly. Data displayed for selected categories */
        /* Also, change pieChart center sum */
        int daysBetween = daysBetween(dateFrom, dateTo).getDays() + 1;
        float daily = roundTwoDecimals((float) sum / (float) daysBetween);
        float weekly = roundTwoDecimals((float) (sum * 7f) / (float) daysBetween);
        float monthly = roundTwoDecimals((float) (sum * 30f) / (float) daysBetween);
        String currency = "RON"; //TODO change

        pieChart.setCenterText(roundTwoDecimals(sum) + currency);
        setTextViewText(dailyTextView, daily);
        setTextViewText(weeklyTextView, weekly);
        setTextViewText(monthlyTextView, monthly);
    }

    public static List<String> getSelectedCategoryList() {
        if (categorySelectedList == null || categorySelectedList.size() == 0) {
            return allCategoriesStringList;
        }

        if (categorySelectedList.get(0).equalsIgnoreCase(CATEGORY_ALL)) {
            return allCategoriesStringList;
        }
        return categorySelectedList;
    }

    /**
     * Function that returns the category's selected drawable image
     *
     * @param category Category
     * @return
     */
    public static Integer getCategoryImageSelected(String category) {
        return categoryImageHashMapSelected.getOrDefault(category, 0);
    }

    /**
     * Function that returns the category's unselected drawable image
     *
     * @param category Category
     * @return category's unselected drawable image
     */
    public static Integer getCategoryImageUnselected(String category) {
        return categoryImageHashMapUnselected.getOrDefault(category, 0);
    }

    public static List<CategoryOption> copyCategoryListObjects() {
        List<CategoryOption> result = new ArrayList<>();
        for (CategoryOption categoryOption : categoryOptions) {
            result.add(new CategoryOption(categoryOption.getCategoryType(), categoryOption.getCategoryImageViewUnselected(), categoryOption.getCategoryImageViewSelected(), categoryOption.isSelected(), categoryOption.getColor()));
        }
        return result;
    }

    public static void updateSelectedCategoryStatistics(ImageView categoryImageView, TextView textViewPercentage, TextView intervalTextView, TextView selectedCategorySpentTextView, TextView totalSpentTextView, StatisticsViewModel statisticsViewModel, Fragment fragment) {
        setStatisticsDateTextView(intervalTextView, dateFrom, dateTo);

        if (pieChartCategorySelected != null) {
            categoryImageView.setBackgroundResource(getCategoryImageSelected(pieChartCategorySelected));

            statisticsViewModel.getSpentOnCategoryBetweenDates(dateFrom, dateTo, pieChartCategorySelected).observe(fragment.getViewLifecycleOwner(), sum1 -> {
                statisticsViewModel.getTransactionsSumForCategories(dateFrom, dateTo, getSelectedCategoryList()).observe(fragment.getViewLifecycleOwner(), sum2 -> {
                    float percentage = 0;
                    if (sum2 != 0) {
                        percentage = (float) 100f * (float) sum1 / (float) sum2;
                    }
                    setTextViewText(textViewPercentage, roundTwoDecimals(percentage) + "%");

                    setTextViewText(selectedCategorySpentTextView, sum1 + " RON");
                    setTextViewText(totalSpentTextView, sum2 + " RON");
                });
            });
        } else {
            categoryImageView.setBackgroundResource(getCategoryImageSelected(CATEGORY_ALL));

            statisticsViewModel.getTransactionsSumForCategories(dateFrom, dateTo, getSelectedCategoryList()).observe(fragment.getViewLifecycleOwner(), sum1 -> {
                statisticsViewModel.getTotalSpentBetweenDates(dateFrom, dateTo).observe(fragment.getViewLifecycleOwner(), sum2 -> {
                    float percentage = 0;
                    if (sum2 != 0) {
                        percentage = (float) 100f * (float) sum1 / (float) sum2;
                    }
                    setTextViewText(textViewPercentage, roundTwoDecimals(percentage) + "%");

                    setTextViewText(selectedCategorySpentTextView, sum1 + " RON");
                    setTextViewText(totalSpentTextView, sum2 + " RON");
                });
            });
        }
    }
}