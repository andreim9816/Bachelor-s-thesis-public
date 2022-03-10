package licenta.allbank.ui.budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.ramijemli.percentagechartview.PercentageChartView;

import org.joda.time.DateTime;

import licenta.allbank.R;
import licenta.allbank.data.model.database.Budget;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static licenta.allbank.ui.budget.BudgetConfirmFragment.remainingTime;
import static licenta.allbank.ui.homeAdvanced.HomeAdvancedFragment.roundTwoDecimals;
import static licenta.allbank.ui.homeAdvanced.HomeAdvancedFragment.setTextViewText;
import static licenta.allbank.ui.statistics.StatisticsBudgetingFragment.BUDGET;
import static licenta.allbank.ui.statistics.StatisticsCategoryFragment.getCategoryImageSelected;
import static licenta.allbank.ui.statistics.StatisticsCategoryFragment.getCategoryImageUnselected;
import static licenta.allbank.utils.DateFormatGMT.convertDateToString;
import static licenta.allbank.utils.DateFormatGMT.getTodayDate;
import static licenta.allbank.utils.DateFormatGMT.maxDate;
import static org.joda.time.Days.daysBetween;

public class BudgetFragment extends Fragment {
    private TextView budgetTextView, spentTextView, remainingTextView;
    private TextView startDateTextView, endDateTextView, timeLeftTextView;
    private TextView dailyTextView, weeklyTextView, monthlyTextView;
    private ImageView categoryImageView;
    private PercentageChartView progressChart;
    private MaterialButton backButton;

    private Budget budget;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Take budget object from bundle */
        Bundle bundle = getArguments();
        if (bundle != null) {
            budget = bundle.getParcelable(BUDGET);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* Inflate the layout for this fragment */
        View view = inflater.inflate(R.layout.fragment_budget_confirm, container, false);

        /* Init UI elements */
        initUiElements(view);

        /* Modify view elements */
        modifyViewElements();

        /* Init fields data */
        initData();

        /* Init the buttons behaviour */
        initBehaviours();

        return view;
    }

    private void initUiElements(View view) {
        this.budgetTextView = view.findViewById(R.id.text_view_budget_confirm_budget);
        this.spentTextView = view.findViewById(R.id.text_view_budget_confirm_spent);
        this.remainingTextView = view.findViewById(R.id.text_view_budget_confirm_remaining);

        this.startDateTextView = view.findViewById(R.id.text_view_budget_confirm_start_date);
        this.endDateTextView = view.findViewById(R.id.text_view_budget_confirm_end_date);
        this.timeLeftTextView = view.findViewById(R.id.text_view_budget_confirm_time_left);

        this.dailyTextView = view.findViewById(R.id.text_view_budget_confirm_daily);
        this.weeklyTextView = view.findViewById(R.id.text_view_budget_confirm_weekly);
        this.monthlyTextView = view.findViewById(R.id.text_view_budget_confirm_monthly);

        this.categoryImageView = view.findViewById(R.id.image_view_budget_confirm_category);

        this.progressChart = view.findViewById(R.id.progress_chart_budget_confirm);
        this.backButton = view.findViewById(R.id.button_budget_confirm_confirm);
    }

    private void modifyViewElements() {
        backButton.setText("Back");
    }

    private void initBehaviours() {
        initBackButton();
    }

    private void initBackButton() {
        /* Go back to Statistics fragment */
        backButton.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
    }

    private void initData() {
        //TODO PIE CHART
        float remaining = roundTwoDecimals(max(0f, budget.getBudget() - budget.getSpent()));
        String currency = " RON"; //TODO

        DateTime startDate = budget.getStartDate();
        DateTime endDate = budget.getEndDate();

        int daysRemaining = daysBetween(maxDate(startDate, getTodayDate()), endDate).getDays() + 1;

        /* Progress should be maximum 100 */
        float percentage = roundTwoDecimals((float) budget.getSpent() / (float) budget.getBudget());
        float progress = min(100f, (float) 100f * percentage);
        progressChart.setProgress(progress, true);

        /* Don't display any number on the chart */
        progressChart.setTextFormatter(progress1 -> "");

        setTextViewText(budgetTextView, budget.getBudget() + currency);
        setTextViewText(spentTextView, budget.getSpent() + currency);
        setTextViewText(remainingTextView, remaining + currency);

        setTextViewText(startDateTextView, convertDateToString(startDate));
        setTextViewText(endDateTextView, convertDateToString(endDate));

        setTextViewText(timeLeftTextView, remainingTime(maxDate(startDate, getTodayDate()), endDate));

        float daily = roundTwoDecimals((float) remaining / (float) daysRemaining);
        float weekly = roundTwoDecimals((float) (remaining * 7f) / (float) daysRemaining);
        float monthly = roundTwoDecimals((float) (remaining * 30f) / (float) daysRemaining);

        setTextViewText(dailyTextView, daily + currency);
        setTextViewText(weeklyTextView, weekly + currency);
        setTextViewText(monthlyTextView, monthly + currency);

        /* Set progress chart image category */
        categoryImageView.setBackgroundResource(getCategoryImageSelected(budget.getCategory()));
    }
}
