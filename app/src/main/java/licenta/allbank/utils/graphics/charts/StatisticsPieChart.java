package licenta.allbank.utils.graphics.charts;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import licenta.allbank.R;
import licenta.allbank.data.model.database.CategoryTypeSum;
import licenta.allbank.ui.statistics.StatisticsCategoryFragment;
import licenta.allbank.ui.statistics.StatisticsViewModel;
import licenta.allbank.utils.DateFormatGMT;

import static licenta.allbank.ui.homeAdvanced.HomeAdvancedFragment.roundTwoDecimals;
import static licenta.allbank.ui.statistics.StatisticsCategoryFragment.allCategoriesStringList;
import static licenta.allbank.ui.statistics.StatisticsCategoryFragment.getSelectedCategoryList;
import static licenta.allbank.utils.graphics.charts.GraphicsPieChart.animate;
import static org.joda.time.Days.daysBetween;

public class StatisticsPieChart {
    private PieChart pieChart;

    private TextView intervalTextView, textViewTotalSumSpent1, textViewTotalSumSpent2, textViewPercentage;
    private TextView dailyTextView, weeklyTextView, monthlyTextView;
    private ImageView categoryImageView;
    private View view;
    private StatisticsCategoryFragment fragment;

    private StatisticsViewModel statisticsViewModel;
    private DateTime dateFrom, dateTo;
    private int daysBetween;

    public StatisticsPieChart(PieChart pieChart, StatisticsViewModel statisticsViewModel, View view, StatisticsCategoryFragment fragment) {

        /* Init UI elements */
        initUiElements(pieChart, statisticsViewModel, view, fragment);

        /* Init its visual behaviours */
        initPieVisualBehaviours();

        /* Init on click behaviour */
        initOnClickBehaviour();
    }

    public void setData(DateTime dateFrom, DateTime dateTo, List<CategoryTypeSum> categoryTypeSumList, String chartLabel, boolean refresh) {
        this.dateTo = dateTo;
        this.dateFrom = dateFrom;
        this.daysBetween = Math.max(daysBetween(dateFrom, dateTo).getDays() + 1, 1);

        ArrayList<Integer> colors = new ArrayList<>();
        List<PieEntry> entries = new ArrayList<>();

        /* For each pair of transaction category and its total, see if it is categorized and add it to the pieChart with its color */
        for (CategoryTypeSum categoryTypeSum : categoryTypeSumList) {
            String category = categoryTypeSum.getCategoryType();
            if (category != null) {
                float totalSum = categoryTypeSum.getSum();
                entries.add(new PieEntry(totalSum, category));
                colors.add(StatisticsCategoryFragment.categoryColorHashMap.get(category));
            }
        }


        /* Set data and styling for slices */
        PieDataSet dataSet = new PieDataSet(entries, chartLabel);
        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(20f);

        /* Add chart colors */
        dataSet.setColors(colors);

        /* Add the entries to the chart */
        PieData data = new PieData(dataSet);
        data.setValueTextSize(12f);

        if (refresh) {
            /* Add animation to the chart */
            animate(pieChart);

            /* Add data to chart */
            pieChart.setData(data);

            /* Don't display data labels or values on the pieChart */
            pieChart.getData().setDrawValues(false);
        }
    }

    private void initUiElements(PieChart pieChart, StatisticsViewModel statisticsViewModel, View view, StatisticsCategoryFragment fragment) {
        this.pieChart = pieChart;
        this.statisticsViewModel = statisticsViewModel;
        this.view = view;
        this.fragment = fragment;

        this.textViewTotalSumSpent1 = view.findViewById(R.id.text_view_statistics_total_sum_spent_1);
        this.textViewTotalSumSpent2 = view.findViewById(R.id.text_view_statistics_total_sum_spent_2);
        this.textViewPercentage = view.findViewById(R.id.text_view_statistics_total_percentage_difference);
        this.categoryImageView = view.findViewById(R.id.shapeableImageView);
        this.intervalTextView = view.findViewById(R.id.text_view_statisics_category_interval);

        this.dailyTextView = view.findViewById(R.id.text_view_statistics_average_daily);
        this.weeklyTextView = view.findViewById(R.id.text_view_statistics_average_weekly);
        this.monthlyTextView = view.findViewById(R.id.text_view_statistics_average_monthly);
    }

    private void initPieVisualBehaviours() {
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(80f);
        pieChart.setHoleColor(ContextCompat.getColor(fragment.requireContext(), R.color.primary));

        /* Set hole total spent */
        if (dateFrom == null) {
            statisticsViewModel.getTransactionsSumForCategories(DateFormatGMT.getDatePastDays(30 - 1), DateFormatGMT.getTodayDate(), getSelectedCategoryList()).observe(fragment, sum -> {
                pieChart.setCenterText(roundTwoDecimals(sum) + " RON");
            });
        } else {
            statisticsViewModel.getTransactionsSumForCategories(dateFrom, dateTo, getSelectedCategoryList()).observe(fragment, sum -> {
                pieChart.setCenterText(roundTwoDecimals(sum) + " RON");
            });
        }
        pieChart.setCenterTextSize(22f);
        pieChart.setCenterTextColor(ContextCompat.getColor(fragment.requireContext(), R.color.text_color));
        pieChart.setDrawHoleEnabled(true);
        pieChart.setTransparentCircleColor(Color.BLACK);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setTransparentCircleRadius(40f);

        /* Don't display any label or values on the chart */
        pieChart.setDrawEntryLabels(false);

        pieChart.setDrawRoundedSlices(true);

        /* Chart rotation */
        pieChart.setRotationEnabled(false);
        pieChart.setHighlightPerTapEnabled(true);

        /* Display legend */
        pieChart.setExtraOffsets(-10, 8, 60, 8);

        Legend legend = pieChart.getLegend();
        legend.setTextColor(ContextCompat.getColor(fragment.requireContext(), R.color.text_color));
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);

        legend.setTextSize(18f);
        legend.setXOffset(22f);
        legend.setYOffset(5f);
    }

    private void initOnClickBehaviour() {
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                PieEntry pieEntry = (PieEntry) e;

                //TODO in functie de startDate, endDate si categoria selectata, schimba:
                /**
                 * percentage
                 * imaginea
                 * totalspent1 si totalspent2
                 */
                StatisticsCategoryFragment.pieChartCategorySelected = pieEntry.getLabel();
                StatisticsCategoryFragment.updateSelectedCategoryStatistics(categoryImageView, textViewPercentage, intervalTextView, textViewTotalSumSpent1, textViewTotalSumSpent2, statisticsViewModel, fragment);

                /* Display the selected category that was clicked on the pie chart */
//                textViewCategorySelected.setVisibility(View.VISIBLE);
//                setTextViewText(textViewCategorySelected, pieEntry.getLabel());

                List<String> categorySelected = new ArrayList<>(List.of(
                        pieEntry.getLabel()
                ));

                List<String> categoriesSelected = getSelectedCategoryList();

                /* Update text views */
                statisticsViewModel.getTransactionsSumForCategories(dateFrom, dateTo, categorySelected).observe(fragment.getViewLifecycleOwner(), sum -> {
                    if (sum == null) {
                        sum = 0f;
                    }

                    /* Display data on the 3 cardViews: daily, weekly, monthly */
                    fragment.displayCardViewStatistics(sum);

//                    setTextViewTextWithCurrency(textViewTotalSumSpent1, roundTwoDecimals(sum));
                });
                statisticsViewModel.getTransactionsSumForCategories(dateFrom, dateTo, categoriesSelected).observe(fragment.getViewLifecycleOwner(), sum -> {
                    if (sum == null) {
                        sum = 0f;
                    }
//                    setTextViewTextWithCurrency(textViewTotalSumSpent2, roundTwoDecimals(sum));
                });

                // TODO display percentage
            }

            @Override
            public void onNothingSelected() {
                Log.i("Statistics", "ON NOTHING");
//                textViewCategorySelected.setVisibility(View.GONE);

                List<String> categoryList = getSelectedCategoryList();

                StatisticsCategoryFragment.pieChartCategorySelected = null;
                StatisticsCategoryFragment.updateSelectedCategoryStatistics(categoryImageView, textViewPercentage, intervalTextView, textViewTotalSumSpent1, textViewTotalSumSpent2, statisticsViewModel, fragment);


                statisticsViewModel.getTransactionsSumForCategories(dateFrom, dateTo, categoryList).observe(fragment.getViewLifecycleOwner(), sum -> {
                    if (sum == null) {
                        sum = 0f;
                    }

                    /* Display data on the 3 cardViews: daily, weekly, monthly */
                    fragment.displayCardViewStatistics(sum);

//                    setTextViewTextWithCurrency(textViewTotalSumSpent1, roundTwoDecimals(sum));
                });
                statisticsViewModel.getTransactionsSumForCategories(dateFrom, dateTo, allCategoriesStringList).observe(fragment.getViewLifecycleOwner(), sum -> {
                    if (sum == null) {
                        sum = 0f;
                    }

//                    setTextViewTextWithCurrency(textViewTotalSumSpent2, roundTwoDecimals(sum));
                });
                // TODO display percentage
            }
        });
    }
}