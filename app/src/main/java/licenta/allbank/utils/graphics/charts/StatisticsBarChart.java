package licenta.allbank.utils.graphics.charts;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import licenta.allbank.R;
import licenta.allbank.data.adapter.TransactionListAdapter;
import licenta.allbank.data.model.database.TransactionTypeDateSum;
import licenta.allbank.ui.statistics.StatisticsCategoryFragment;
import licenta.allbank.ui.statistics.StatisticsViewModel;
import licenta.allbank.utils.DateFormatGMT;

import static licenta.allbank.ui.homeAdvanced.HomeAdvancedFragment.setTransactions;
import static licenta.allbank.ui.statistics.StatisticsCategoryFragment.getSelectedCategoryList;
import static org.joda.time.Days.daysBetween;

public class StatisticsBarChart {
    private BarChart barChart;
    private StatisticsViewModel statisticsViewModel;
    private StatisticsCategoryFragment fragment;
    private TransactionListAdapter transactionListAdapter;

    private DateTime dateFrom, dateTo;
    private int daysBetween;
    private String[] labels;
    /* List of DateTime that stores date intervals in the barChart */
    private List<DateTime> labelIntervals;

    float BAR_SPACE = 0.05f;
    float BAR_WIDTH = 0.16f;
    float GROUP_SPACE = 0.16f;

    public StatisticsBarChart(BarChart barChart, StatisticsViewModel statisticsViewModel, StatisticsCategoryFragment fragment, TransactionListAdapter transactionListAdapter) {

        /* Init UI elements */
        initUiElements(barChart, statisticsViewModel, fragment, transactionListAdapter);

        /* Init its visual behaviours */
        initBarVisualBehaviours();

        /* Init on click behaviour */
        initOnClickBehaviour();
    }

    private void initUiElements(BarChart barChart, StatisticsViewModel statisticsViewModel, StatisticsCategoryFragment fragment, TransactionListAdapter transactionListAdapter) {
        this.barChart = barChart;
        this.statisticsViewModel = statisticsViewModel;
        this.fragment = fragment;
        this.transactionListAdapter = transactionListAdapter;
    }

    private void initBarVisualBehaviours() {
        barChart.setDrawBorders(false);
        barChart.setDrawGridBackground(false);

        barChart.getXAxis().setAxisMinimum(0);
        barChart.getAxisLeft().setAxisMinimum(0);

        barChart.getDescription().setEnabled(false);

        /* Set gesture options */
        barChart.setTouchEnabled(true);
        barChart.setPinchZoom(false);
        barChart.setScaleEnabled(false);

        /* Set legend position */
        Legend legend = barChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);

        legend.setYOffset(0f);
        legend.setXOffset(6f);
        legend.setYEntrySpace(0f);
        legend.setTextSize(14f);
        legend.setTextColor(ContextCompat.getColor(fragment.requireContext(), R.color.text_color));

        /* X axis visual */
        XAxis xAxis = barChart.getXAxis();
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setXOffset(10f);
        xAxis.setTextColor(ContextCompat.getColor(fragment.requireContext(), R.color.text_color));

        /* Y axis visual */
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextColor(ContextCompat.getColor(fragment.requireContext(), R.color.text_color));

        /* Disable right Y Axis label */
        YAxis rightYAxis = barChart.getAxisRight();
        rightYAxis.setEnabled(false);

        barChart.setDragEnabled(true);
        barChart.setVisibleXRangeMaximum(3);
    }

    private void initOnClickBehaviour() {
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                /* When clicking on bar chart (and it's not empty), display the payments made in that interval of time */
                if (e.getY() != 0) {
                    int index = (int) e.getX();
                    int unitTime = getUnitTime(dateFrom, dateTo);
                    DateTime startDate, endDate;
                    if (unitTime == 1 || labelIntervals.get(index).equals(labelIntervals.get(index + 1))) {
                        /* If unit time is 1 (one day), then startDate = endDate
                           or if there is only one day remaining in the last interval */
                        startDate = endDate = labelIntervals.get(index);
                    } else if (index == labelIntervals.size() - 2) {
                        /* For the last interval, endDate is calculated different */
                        startDate = labelIntervals.get(index);
                        endDate = labelIntervals.get(index + 1);
                    } else {
                        startDate = labelIntervals.get(index);
                        endDate = labelIntervals.get(index + 1).minusDays(1);
                    }

                    String category = barChart.getLegend().getEntries()[h.getDataSetIndex()].label;
                    statisticsViewModel.getTransactionsByCategory(startDate, endDate, category).observe(fragment.getViewLifecycleOwner(), transactions -> setTransactions(transactionListAdapter, transactions));
                }
            }

            @Override
            public void onNothingSelected() {
                /* Update Transaction recycler view. Check whether to show all categories or just part of it */
                List<String> transactionsCategoryList = getSelectedCategoryList();
                statisticsViewModel.getTransactionsByCategory(dateFrom, dateTo, transactionsCategoryList).observe(fragment.getViewLifecycleOwner(), transactions -> setTransactions(transactionListAdapter, transactions));
            }
        });
    }

    public void setData(DateTime dateFrom, DateTime dateTo, List<TransactionTypeDateSum> transactionTypeDateSumList, String chartLabel, boolean refresh) {
        this.dateTo = dateTo;
        this.dateFrom = dateFrom;
        this.daysBetween = Math.max(daysBetween(dateFrom, dateTo).getDays() + 1, 1);

        /* Get unit time and number of groups in the BarChart*/
        int unitTime = getUnitTime(dateFrom, dateTo);
        int noOfGroups;

        if (unitTime == 1) {
            noOfGroups = daysBetween;
        } else {
            if (daysBetween % unitTime == 0) {
                noOfGroups = daysBetween / unitTime;
            } else {
                noOfGroups = daysBetween / unitTime + 1;
            }
        }

        /* Add all categories to a HashMap and get its size */
        HashMap<String, List<Float>> hashMapCategorySpending = new HashMap<>();
        for (TransactionTypeDateSum transactionTypeDateSum : transactionTypeDateSumList) {
            String category = transactionTypeDateSum.getTransactionCategory();
            if (category != null) {
                List<Float> list = new ArrayList<>(Collections.nCopies(noOfGroups, 0f));
                hashMapCategorySpending.put(category, list);
            }
        }

        int noOfCategories = hashMapCategorySpending.size();
        /* For each category, iterate through the array with transactions group per day and category and update its array value */
        for (Map.Entry<String, List<Float>> entry : hashMapCategorySpending.entrySet()) {
            String category = entry.getKey();
            for (TransactionTypeDateSum transactionTypeDateSum : transactionTypeDateSumList) {
                if (transactionTypeDateSum.getTransactionCategory() != null && transactionTypeDateSum.getTransactionCategory().equalsIgnoreCase(category)) {
                    int index = getIntervalIndex(transactionTypeDateSum.getBookingDate(), dateFrom, noOfGroups, unitTime);
                    float value = entry.getValue().get(index);
                    entry.getValue().set(index, value + transactionTypeDateSum.getSum());
                }
            }
        }

        /* Create a list of noOfCategories BarDataSet elements */
        List<IBarDataSet> barDataSetList = new ArrayList<>();
        for (Map.Entry<String, List<Float>> entry : hashMapCategorySpending.entrySet()) {
            String category = entry.getKey();
            List<Float> values = entry.getValue();
            List<BarEntry> valuesConverted = convertToBarEntryArray(values);

            BarDataSet barDataSet = new BarDataSet(valuesConverted, category);
            barDataSet.setColor(StatisticsCategoryFragment.categoryColorHashMap.getOrDefault(category, 0));
            barDataSetList.add(barDataSet);
        }

        /* Set data to chart */
        BarData barData = new BarData(barDataSetList);
        barData.setValueTextSize(12.5f);
        barChart.setData(barData);

        /* Set color */
        barData.setValueTextColor(ContextCompat.getColor(fragment.requireContext(), R.color.text_color));

        /* Set X axis labels */
        this.labels = getLabels(dateFrom, dateTo, unitTime, noOfGroups);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));

        /* Set bar chart dimensions */
        setBarChartDimensions(noOfCategories);

        barData.setBarWidth(BAR_WIDTH);
        barChart.getXAxis().setAxisMaximum(0 + barChart.getBarData().getGroupWidth(GROUP_SPACE, BAR_SPACE) * labels.length);

        if (barDataSetList.size() > 1) {
            barChart.groupBars(0, GROUP_SPACE, BAR_SPACE);
        }

        barChart.setDragEnabled(true);
        barChart.setVisibleXRangeMaximum(3);

        /* Move to first entry. Invalidate is automatically called */
        barChart.moveViewToX(0);
    }

    /**
     * @param dateFrom starting date
     * @param dateTo   ending date
     * @return Unit time (in days) for grouping data in BarChart
     */
    private int getUnitTime(DateTime dateFrom, DateTime dateTo) {
        int unitTime;
        int daysBetween = daysBetween(dateFrom, dateTo).getDays() + 1;

        if (daysBetween < 14) {
            unitTime = 1;
        } else if (daysBetween < 60) {
            unitTime = 7;
        } else {
            unitTime = 30;
        }
        return unitTime;
    }

    /**
     * @param list of numbers
     * @return Converts an Array of Float values to BarEntry
     */
    private static List<BarEntry> convertToBarEntryArray(List<Float> list) {
        List<BarEntry> result = new ArrayList<>();
        for (int index = 0; index < list.size(); index++) {
            result.add(new BarEntry(index, list.get(index)));
        }
        return result;
    }

    /**
     * @param dateFrom   starting date
     * @param dateTo     ending date
     * @param unitTime   unit of time in days (eg 1, 7, 30 etc)
     * @param noOfGroups number of total interval date groups
     * @return An array of string used for bar chart labels starting from dateFrom and to dateTo with the specified unitTime and noOfGroups
     */
    private String[] getLabels(DateTime dateFrom, DateTime dateTo, int unitTime, int noOfGroups) {
        String[] result = new String[noOfGroups];
        DateTime startDate = dateFrom, endDate;
        labelIntervals = new ArrayList<>();
        labelIntervals.add(startDate);
        for (int index = 0; index < noOfGroups - 1; index++) {
            endDate = startDate.plusDays(unitTime - 1);
            if (unitTime != 1) {
                result[index] = DateFormatGMT.convertDateToStringBarChartLabel(startDate) + " - " + DateFormatGMT.convertDateToStringBarChartLabel(endDate);
            } else {
                result[index] = DateFormatGMT.convertDateToStringBarChartLabel(startDate);
            }
            startDate = endDate.plusDays(1);
            labelIntervals.add(startDate);
        }
        if (unitTime != 1 && !dateTo.equals(startDate)) {
            result[noOfGroups - 1] = DateFormatGMT.convertDateToStringBarChartLabel(startDate) + " - " + DateFormatGMT.convertDateToStringBarChartLabel(dateTo);
        } else {
            result[noOfGroups - 1] = DateFormatGMT.convertDateToStringBarChartLabel(startDate);
        }
        labelIntervals.add(dateTo);

        return result;
    }

    /**
     * Sets the bar chart dimensions (width, group space, bar space) depending on the number of bars in one group
     *
     * @param noOfCategories number of bars in one group
     */
    private void setBarChartDimensions(int noOfCategories) {
        switch (noOfCategories) {
            case 1:
                BAR_WIDTH = 0.35f;
                BAR_SPACE = 0.55f;
                GROUP_SPACE = 0.1f;
            case 2:
                BAR_WIDTH = 0.33f;
                BAR_SPACE = 0.12f;
                GROUP_SPACE = 0.1f;
                break;
            case 3:
                BAR_WIDTH = 0.2f;
                BAR_SPACE = 0.09f;
                GROUP_SPACE = 0.13f;
                break;
            case 4:
                BAR_WIDTH = 0.18f;
                BAR_SPACE = 0.05f;
                GROUP_SPACE = 0.08f;
                break;
            case 5:
                BAR_WIDTH = 0.15f;
                BAR_SPACE = 0.04f;
                GROUP_SPACE = 0.05f;
                break;
            case 6:
                BAR_WIDTH = 0.12f;
                BAR_SPACE = 0.03f;
                GROUP_SPACE = 0.1f;
                break;
            case 7:
                BAR_WIDTH = 0.11f;
                BAR_SPACE = 0.02f;
                GROUP_SPACE = 0.09f;
                break;
            case 8:
                BAR_WIDTH = 0.10f;
                BAR_SPACE = 0.02f;
                GROUP_SPACE = 0.04f;
                break;
        }
    }

    private int getIntervalIndex(DateTime date, DateTime dateFrom, int noOfGroups, int unitTime) {
        DateTime beginInterval = dateFrom, endInterval = dateFrom.plusDays(unitTime - 1);
        for (int index = 0; index < noOfGroups; index++) {
            if (beginInterval.compareTo(date) <= 0 && date.compareTo(endInterval) <= 0) {
                return index;
            }
            beginInterval = endInterval.plusDays(1);
            endInterval = beginInterval.plusDays(unitTime - 1);
        }

        return -1;
    }
}
