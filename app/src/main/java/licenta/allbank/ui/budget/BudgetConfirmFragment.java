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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.ramijemli.percentagechartview.PercentageChartView;

import org.joda.time.DateTime;

import licenta.allbank.R;
import licenta.allbank.data.model.allbank.others.BudgetAddedResponse;
import licenta.allbank.data.model.database.Budget;
import licenta.allbank.service.ServiceServer;
import licenta.allbank.ui.statistics.StatisticsBudgetingViewModel;
import licenta.allbank.utils.DateFormatGMT;
import licenta.allbank.utils.messages.BudgetMessage;
import licenta.allbank.utils.messages.MessageDisplay;
import licenta.allbank.utils.server.CallbackGenericResponse;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static licenta.allbank.service.ServiceServer.ERROR_MESSAGE_SERVER;
import static licenta.allbank.ui.home.HomeFragment.navigateToNextFragment;
import static licenta.allbank.ui.homeAdvanced.HomeAdvancedFragment.roundTwoDecimals;
import static licenta.allbank.ui.homeAdvanced.HomeAdvancedFragment.setTextViewText;
import static licenta.allbank.ui.statistics.StatisticsBudgetingFragment.BUDGET;
import static licenta.allbank.ui.statistics.StatisticsCategoryFragment.getCategoryImageUnselected;
import static licenta.allbank.utils.DateFormatGMT.convertDateToString;
import static licenta.allbank.utils.DateFormatGMT.getTodayDate;
import static licenta.allbank.utils.DateFormatGMT.maxDate;
import static org.joda.time.Days.daysBetween;

public class BudgetConfirmFragment extends Fragment {
    private TextView budgetTextView, spentTextView, remainingTextView;
    private TextView startDateTextView, endDateTextView, timeLeftTextView;
    private TextView dailyTextView, weeklyTextView, monthlyTextView;
    private MaterialButton confirmButton;

    private PercentageChartView progressChart;
    private ImageView categoryImageView;

    private StatisticsBudgetingViewModel statisticsBudgetingViewModel;
    private NavController navController;
    private Budget budget;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statisticsBudgetingViewModel = new ViewModelProvider(this).get(StatisticsBudgetingViewModel.class);
        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        /* Take budget object from bundle */
        Bundle bundle = getArguments();
        budget = bundle.getParcelable(BUDGET);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget_confirm, container, false);
        /* Init UI elements */
        initUiElements(view);

        /* Init the buttons behaviour */
        initBehaviours();

        /* Init fields data */
        initData();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        this.confirmButton = view.findViewById(R.id.button_budget_confirm_confirm);
    }

    private void initData() {
        /* Set background resource and how much was spent for the new budget */
        statisticsBudgetingViewModel.getSpentOnCategoryBetweenDates(budget.getStartDate(), budget.getEndDate(), budget.getCategory()).observe(getViewLifecycleOwner(), spent -> {
            DateTime endDate = budget.getEndDate();
            DateTime startDate = budget.getStartDate();
            int daysRemaining = daysBetween(maxDate(startDate, getTodayDate()), endDate).getDays() + 1;
            float remaining = max(0f, budget.getBudget() - spent);

            String currency = " RON"; //TODO
            float percentage = roundTwoDecimals((float) spent / (float) budget.getBudget());
            float progress = min(100f, (float) 100f * percentage);
            progressChart.setProgress(progress, true);

            /* Don't display any number on the chart */
            progressChart.setTextFormatter(progress1 -> "");

            /* Set progress chart image category */
            categoryImageView.setBackgroundResource(getCategoryImageUnselected(budget.getCategory()));

            setTextViewText(budgetTextView, budget.getBudget() + currency);
            setTextViewText(spentTextView, spent + currency);
            setTextViewText(remainingTextView, remaining + currency);

            setTextViewText(startDateTextView, convertDateToString(startDate));
            setTextViewText(endDateTextView, convertDateToString(endDate));
            setTextViewText(timeLeftTextView, remainingTime(maxDate(startDate,getTodayDate()), endDate));

            float daily = roundTwoDecimals((float) remaining / (float) daysRemaining);
            float weekly = roundTwoDecimals((float) (remaining * 7f) / (float) daysRemaining);
            float monthly = roundTwoDecimals((float) (remaining * 30f) / (float) daysRemaining);

            setTextViewText(dailyTextView, daily + currency);
            setTextViewText(weeklyTextView, weekly + currency);
            setTextViewText(monthlyTextView, monthly + currency);
        });
    }

    private void initBehaviours() {
        initConfirmButton();
    }

    private void initConfirmButton() {
        confirmButton.setOnClickListener(v -> {
            try {
                ServiceServer serviceServer = ServiceServer.getInstance(getContext());
                serviceServer.newBudget(ServiceServer.getAccessToken(), budget, new CallbackGenericResponse<BudgetAddedResponse>() {
                    @Override
                    public void onSuccess(BudgetAddedResponse response) {
                        /* Add in local database and go back to menu */
                        budget.setBudgetId(response.getId());
                        statisticsBudgetingViewModel.insert(budget);

                        MessageDisplay.showLongMessage(getContext(), BudgetMessage.BUDGET_ADDED);
//                        getActivity().onBackPressed();
                        navigateToNextFragment(navController, R.id.action_budgetConfirmFragment_to_navigation_statistics, null);
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
                        navigateToNextFragment(navController, R.id.action_budgetConfirmFragment_to_navigation_statistics, null);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MessageDisplay.showLongMessage(getContext(), ERROR_MESSAGE_SERVER);
                navigateToNextFragment(navController, R.id.action_budgetConfirmFragment_to_navigation_statistics, null);
            }
        });
    }

    public static String remainingTime(DateTime dateTime1, DateTime dateTime2) {
        String result;
        int monthsBetween = DateFormatGMT.monthsDifference(dateTime1, dateTime2);
        if (monthsBetween == 1) {
            result = "1 month ";
        } else if (monthsBetween > 1) {
            result = monthsBetween + " months ";
        } else {
            result = "";
        }

        DateTime nextDate = dateTime1.plusMonths(monthsBetween);
        int weeksBetween = DateFormatGMT.weeksDifference(nextDate, dateTime2);

        if (weeksBetween == 1) {
            result += "1 week ";
        } else if (weeksBetween > 1) {
            result += weeksBetween + " weeks ";
        }

        nextDate = nextDate.plusWeeks(weeksBetween);
        int daysBetween = DateFormatGMT.daysDifference(nextDate, dateTime2);

        if (daysBetween == 1) {
            result += "1 day ";
        } else if (daysBetween > 1) {
            result += daysBetween + " days ";
        }

        return result + "to go";
    }
}
