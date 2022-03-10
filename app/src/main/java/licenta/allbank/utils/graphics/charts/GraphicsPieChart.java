package licenta.allbank.utils.graphics.charts;

import android.graphics.Color;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import licenta.allbank.R;
import licenta.allbank.data.adapter.TransactionListAdapter;
import licenta.allbank.data.model.database.Transaction;
import licenta.allbank.service.ServiceBcr;
import licenta.allbank.service.ServiceBt;
import licenta.allbank.ui.homeAdvanced.HomeAdvancedFragment;
import licenta.allbank.ui.homeAdvanced.HomeAdvancedViewModel;

import static licenta.allbank.ui.home.HomeFragment.EXPENSES_TRANSACTIONS;
import static licenta.allbank.ui.home.HomeFragment.INCOME_TRANSACTIONS;
import static licenta.allbank.ui.homeAdvanced.HomeAdvancedFragment.roundTwoDecimals;
import static licenta.allbank.ui.homeAdvanced.HomeAdvancedFragment.setTextViewText;
import static licenta.allbank.ui.homeAdvanced.HomeAdvancedFragment.setTextViewTextWithCurrency;
import static licenta.allbank.ui.homeAdvanced.HomeAdvancedFragment.setTransactions;
import static org.joda.time.Days.daysBetween;

public class GraphicsPieChart {
    private TextView textViewIncomeText, textViewExpensesText, textViewTotalText;
    private TextView textViewIncome, textViewExpenses, textViewTotal;
    private PieChart pieChart;
    private HomeAdvancedFragment fragment;
    private HomeAdvancedViewModel homeAdvancedViewModel;
    private TransactionListAdapter transactionListAdapter;
    private DateTime dateFrom, dateTo;
    private float income, expenses;
    private int daysBetween;
    private float pastThreeMonthsIncomeAll, pastThreeMonthsIncomeBcr, pastThreeMonthsIncomeBt;
    private float pastThreeMonthsExpenseAll, pastThreeMonthsExpenseBcr, pastThreeMonthsExpenseBt;
    private String accountsBankFilterOption;

    public GraphicsPieChart(PieChart chart, TransactionListAdapter transactionListAdapter, List<Transaction> currentTransactions, HomeAdvancedFragment fragment, HomeAdvancedViewModel homeAdvancedViewModel, TextView textViewTotal, TextView textViewIncome, TextView textViewExpenses, TextView textViewTotalText, TextView textViewIncomeText, TextView textViewExpensesText) {

        /* Init UI elements */
        initUiElements(chart, transactionListAdapter, currentTransactions, fragment, homeAdvancedViewModel, textViewTotal, textViewIncome, textViewExpenses, textViewTotalText, textViewIncomeText, textViewExpensesText);

        /* Init its visual behaviours */
        initPieVisualBehaviours(pieChart);

        /* Init on click behaviour */
        initOnClickBehaviour();
    }

    public void setData(String accountsBankFilterOption, DateTime dateFrom, DateTime dateTo, String label1, float value1, String label2, float value2, String chartLabel, boolean refresh) {
        this.income = value1;
        this.expenses = value2;
        this.dateTo = dateTo;
        this.dateFrom = dateFrom;
        this.daysBetween = Math.max(daysBetween(dateFrom, dateTo).getDays() + 1, 1);
        this.accountsBankFilterOption = accountsBankFilterOption;

        List<PieEntry> entries = Arrays.asList(
                new PieEntry(value1, label1),
                new PieEntry(value2, label2)
        );

        /* Set data and styling for slices */
        PieDataSet dataSet = new PieDataSet(entries, chartLabel);
        dataSet.setValueFormatter(new PercentFormatter(pieChart));
        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(0f);
        dataSet.setSelectionShift(10f);

        /* Add chart colors */
        ArrayList<Integer> colors = new ArrayList<>(
                Arrays.asList(
                        ContextCompat.getColor(fragment.getContext(), R.color.income_green),
                        ContextCompat.getColor(fragment.getContext(), R.color.expenses_red)
                )
        );
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);

        if (refresh) {
            /* Add animation to the chart */
            animate(pieChart);

            /* Add data to chart */
            pieChart.setData(data);
        }
    }

    public void setStatisticsData(float pastThreeMonthsIncomeAll, float pastThreeMonthsIncomeBcr, float pastThreeMonthsIncomeBt, float pastThreeMonthsExpenseAll, float pastThreeMonthsExpenseBcr, float pastThreeMonthsExpenseBt) {
        this.pastThreeMonthsIncomeAll = pastThreeMonthsIncomeAll;
        this.pastThreeMonthsIncomeBcr = pastThreeMonthsIncomeBcr;
        this.pastThreeMonthsIncomeBt = pastThreeMonthsIncomeBt;

        this.pastThreeMonthsExpenseAll = pastThreeMonthsExpenseAll;
        this.pastThreeMonthsExpenseBcr = pastThreeMonthsExpenseBcr;
        this.pastThreeMonthsExpenseBt = pastThreeMonthsExpenseBt;
    }

    private void initOnClickBehaviour() {
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                PieEntry pieEntry = (PieEntry) e;
                float difference = 0;

                /* Update textView titles */
                if (pieEntry.getLabel().equalsIgnoreCase(INCOME_TRANSACTIONS)) {
                    /* Check the bank filter */
                    if (accountsBankFilterOption.equalsIgnoreCase("ALL")) {
                        homeAdvancedViewModel.getTransactionsByTransactionType(dateFrom, dateTo, INCOME_TRANSACTIONS).observe(fragment.getViewLifecycleOwner(), transactions -> setTransactions(transactionListAdapter, transactions));
                    } else {
                        homeAdvancedViewModel.getTransactionsByBankAndTransactionType(dateFrom, dateTo, accountsBankFilterOption, INCOME_TRANSACTIONS).observe(fragment.getViewLifecycleOwner(), transactions -> setTransactions(transactionListAdapter, transactions));
                    }

                    /* Update textViews */
                    setTextViewText(textViewTotalText, "Income");
                    setTextViewText(textViewIncomeText, "Average daily income");
                    setTextViewText(textViewExpensesText, "Average last 3 months income difference");

                    setTextViewTextWithCurrency(textViewTotal, income);
                    setTextViewTextWithCurrency(textViewIncome, roundTwoDecimals(income / daysBetween));

                    /* Calculate the difference for third statistics */
                    if (accountsBankFilterOption.equalsIgnoreCase(ServiceBcr.BCR)) {
                        difference = income - pastThreeMonthsIncomeBcr;
                    } else if (accountsBankFilterOption.equalsIgnoreCase(ServiceBt.BT)) {
                        difference = income - pastThreeMonthsIncomeBt;
                    } else {
                        difference = income - pastThreeMonthsIncomeAll;
                    }
                    setTextViewTextWithCurrency(textViewExpenses, roundTwoDecimals(difference));
                    textViewTotal.setTextColor(ContextCompat.getColor(fragment.getContext(), R.color.income_green));

                } else if (pieEntry.getLabel().equalsIgnoreCase(EXPENSES_TRANSACTIONS)) {
                    /* Check the bank filter */
                    if (accountsBankFilterOption.equalsIgnoreCase("ALL")) {
                        homeAdvancedViewModel.getTransactionsByTransactionType(dateFrom, dateTo, EXPENSES_TRANSACTIONS).observe(fragment.getViewLifecycleOwner(), transactions -> setTransactions(transactionListAdapter, transactions));
                    } else {
                        homeAdvancedViewModel.getTransactionsByBankAndTransactionType(dateFrom, dateTo, accountsBankFilterOption, EXPENSES_TRANSACTIONS).observe(fragment.getViewLifecycleOwner(), transactions -> setTransactions(transactionListAdapter, transactions));
                    }

                    /* Update textViews */
                    setTextViewText(textViewTotalText, "Expenses");
                    setTextViewText(textViewIncomeText, "Average daily expenses");
                    setTextViewText(textViewExpensesText, "Average last 3 months expenses difference");

                    setTextViewTextWithCurrency(textViewTotal, expenses);
                    setTextViewTextWithCurrency(textViewIncome, roundTwoDecimals(expenses / daysBetween));

                    /* Calculate the difference for third statistics */
                    if (accountsBankFilterOption.equalsIgnoreCase(ServiceBcr.BCR)) {
                        difference = expenses - pastThreeMonthsExpenseBcr;
                    } else if (accountsBankFilterOption.equalsIgnoreCase(ServiceBt.BT)) {
                        difference = expenses - pastThreeMonthsExpenseBt;
                    } else {
                        difference = expenses - pastThreeMonthsExpenseAll;
                    }
                    setTextViewTextWithCurrency(textViewExpenses, roundTwoDecimals(difference));
                    textViewTotal.setTextColor(ContextCompat.getColor(fragment.getContext(), R.color.expenses_red));
                }

                /* Update textView color depending on the difference */
                if (difference < 0) {
                    textViewExpenses.setTextColor(ContextCompat.getColor(fragment.getContext(), R.color.expenses_red));
                } else if (difference > 0) {
                    textViewExpenses.setTextColor(ContextCompat.getColor(fragment.getContext(), R.color.income_green));
                }
            }

            @Override
            public void onNothingSelected() {
                /* Update transactions list */
                if (accountsBankFilterOption.equalsIgnoreCase("ALL")) {
                    homeAdvancedViewModel.getTransactions(dateFrom, dateTo).observe(fragment.getViewLifecycleOwner(), transactions -> setTransactions(transactionListAdapter, transactions));
                } else {
                    homeAdvancedViewModel.getTransactionsByBank(dateFrom, dateTo, accountsBankFilterOption).observe(fragment.getViewLifecycleOwner(), transactions -> setTransactions(transactionListAdapter, transactions));
                }

                /* Update textView statistics titles */
                setTextViewText(textViewIncomeText, fragment.getResources().getString(R.string.total_income));
                setTextViewText(textViewExpensesText, fragment.getResources().getString(R.string.total_expenses));
                setTextViewText(textViewTotalText, fragment.getResources().getString(R.string.total));

                /* Update textView color depending on the difference */
                float totalDifference = income - expenses;
                if (totalDifference < 0) {
                    textViewTotal.setTextColor(ContextCompat.getColor(fragment.requireContext(), R.color.expenses_red));
                } else if (totalDifference > 0) {
                    textViewTotal.setTextColor(ContextCompat.getColor(fragment.requireContext(), R.color.income_green));
                }
                setTextViewDefaultColor(textViewIncome);
                setTextViewDefaultColor(textViewExpenses);

                /* Update textViews */
                setTextViewTextWithCurrency(textViewTotal, totalDifference);
                setTextViewTextWithCurrency(textViewIncome, income);
                setTextViewTextWithCurrency(textViewExpenses, expenses);

                setData(accountsBankFilterOption, dateFrom, dateTo, INCOME_TRANSACTIONS, income, EXPENSES_TRANSACTIONS, expenses, "", false);
            }
        });
    }

    public static void animate(PieChart chart) {
        /* Chart animation*/
        chart.animateX(1400, Easing.EaseInOutQuad);
        chart.animateY(1400, Easing.EaseInOutQuad);
    }

    private void initUiElements(PieChart pieChart, TransactionListAdapter transactionListAdapter, List<Transaction> currentTransactionsList, HomeAdvancedFragment fragment, HomeAdvancedViewModel homeAdvancedViewModel, TextView textViewTotal, TextView textViewIncome, TextView textViewExpenses, TextView textViewTotalText, TextView textViewIncomeText, TextView textViewExpensesText) {

        this.textViewTotalText = textViewTotalText;
        this.textViewExpensesText = textViewExpensesText;
        this.textViewIncomeText = textViewIncomeText;

        this.textViewTotal = textViewTotal;
        this.textViewExpenses = textViewExpenses;
        this.textViewIncome = textViewIncome;

        this.pieChart = pieChart;
        this.fragment = fragment;
        this.homeAdvancedViewModel = homeAdvancedViewModel;
        this.transactionListAdapter = transactionListAdapter;
    }

    public static void initPieVisualBehaviours(PieChart pieChart) {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);

        pieChart.setDrawHoleEnabled(false);
        pieChart.setTransparentCircleColor(Color.BLACK);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setTransparentCircleRadius(0f);

        /* Chart rotation */
        pieChart.setRotationEnabled(false);
        pieChart.setHighlightPerTapEnabled(true);

        /* Don't display any legend */
        pieChart.getLegend().setEnabled(false);
    }

    public static void setTextViewDefaultColor(TextView textView) {
        textView.setTextColor(Color.WHITE);
    }
}
