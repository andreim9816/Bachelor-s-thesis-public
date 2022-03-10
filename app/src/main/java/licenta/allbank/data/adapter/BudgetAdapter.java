package licenta.allbank.data.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.joda.time.DateTime;

import java.util.List;

import licenta.allbank.R;
import licenta.allbank.data.model.database.Budget;
import licenta.allbank.ui.statistics.StatisticsBudgetingViewModel;
import licenta.allbank.ui.statistics.StatisticsCategoryFragment;
import licenta.allbank.utils.click_interface.ClickLongClickInterface;

import static java.lang.Math.max;
import static licenta.allbank.ui.homeAdvanced.HomeAdvancedFragment.roundTwoDecimals;
import static licenta.allbank.ui.homeAdvanced.HomeAdvancedFragment.setTextViewText;
import static licenta.allbank.utils.DateFormatGMT.getTodayDate;
import static licenta.allbank.utils.DateFormatGMT.maxDate;
import static org.joda.time.Days.daysBetween;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {
    static class BudgetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView categoryTextView, dailySpendingTextView, spendingStatusTextView;
        private ImageView categoryImageView, spendingStatusImageView;
        private final LinearProgressIndicator linearProgress;
        private final MaterialCardView materialCardView;
        private final ClickLongClickInterface clickLongClickInterface;

        public BudgetViewHolder(@NonNull View itemView, ClickLongClickInterface clickInterface) {
            super(itemView);
            this.categoryTextView = itemView.findViewById(R.id.text_view_statistics_budgeting_recycler_view_category);
            this.dailySpendingTextView = itemView.findViewById(R.id.text_view_statistics_budgeting_recycler_view_daily_spending);
            this.spendingStatusTextView = itemView.findViewById(R.id.text_view_statistics_budgeting_spending_status);

            this.categoryImageView = itemView.findViewById(R.id.image_view_statistics_budgeting_category_image);
            this.spendingStatusImageView = itemView.findViewById(R.id.image_view_statistics_budgeting_spending_status);

            this.linearProgress = itemView.findViewById(R.id.bar_chart_statistics_budgeting);
            this.materialCardView = itemView.findViewById(R.id.material_card_view_budgeting_recycler_view_item);
            this.clickLongClickInterface = clickInterface;

            materialCardView.setOnClickListener(this);
            materialCardView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickLongClickInterface.onClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            clickLongClickInterface.onLongClick(getAdapterPosition());
            return true;
        }
    }

    private final LayoutInflater inflater;
    private final Context context;
    private List<Budget> budgetList;
    private final ClickLongClickInterface clickLongClickInterface;
    private final StatisticsBudgetingViewModel statisticsBudgetingViewModel;
    private final Fragment fragment;

    public BudgetAdapter(Context context, ClickLongClickInterface clickLongClickInterface, Fragment fragment) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.clickLongClickInterface = clickLongClickInterface;
        this.statisticsBudgetingViewModel = new ViewModelProvider(fragment).get(StatisticsBudgetingViewModel.class);
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_item_statistics_budgeting_budget, parent, false);
        return new BudgetViewHolder(view, clickLongClickInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        if (budgetList != null) {

            Budget budget = budgetList.get(position);
            String currency = "RON"; // TODO add currency to Budget class

            statisticsBudgetingViewModel.getSpentOnCategoryBetweenDates(budget.getStartDate(), budget.getEndDate(), budget.getCategory()).observe(fragment.getViewLifecycleOwner(), spent -> {

                DateTime endDate = budget.getEndDate();
                DateTime startDate = budget.getStartDate();

                int daysRemaining = daysBetween(maxDate(startDate, getTodayDate()), endDate).getDays() + 1;
                float remaining = max(0f, budget.getBudget() - spent);
                float dailySpending = roundTwoDecimals((float) remaining / (float) daysRemaining);

                setTextViewText(holder.dailySpendingTextView, dailySpending + " " + currency + " ");
                setTextViewText(holder.categoryTextView, budget.getCategory());

                holder.categoryImageView.setImageResource(StatisticsCategoryFragment.getCategoryImageUnselected(budget.getCategory()));
                holder.categoryImageView.setBackgroundResource(R.drawable.rounded_medium);

                /* Set image and text status */
                setImageAndStatus(holder, budget);

                setProgressData(holder, budget);
            });
        }
    }

    @Override
    public int getItemCount() {
        if (budgetList != null) {
            return budgetList.size();
        }
        return 0;
    }

    public void setBudgets(List<Budget> budgetList) {
        /* Notify UI whenever transaction list has been changed */
        this.budgetList = budgetList;
        notifyDataSetChanged();
    }

    private void setProgressData(BudgetViewHolder holder, Budget budget) {
        holder.linearProgress.setMax((int) budget.getBudget());
        holder.linearProgress.setProgress((int) budget.getSpent());
    }

    private void setImageAndStatus(BudgetViewHolder holder, Budget budget) {
        String textStatus = "";
        float spent = budget.getSpent();
        float maxBudget = budget.getBudget();

        float percentage = roundTwoDecimals(100 * spent / maxBudget);

        if (percentage < 40) {
            textStatus = "Good job on your spending!";
//            holder.linearProgress.setIndicatorColor(ContextCompat.getColor(fragment.getContext(),R.color.red));
            holder.linearProgress.setIndicatorColor(ContextCompat.getColor(fragment.getContext(),R.color.statistics_budgeting_under_40));
        } else if (percentage < 80) {
            textStatus = "Your spending is still on track!";
            holder.linearProgress.setIndicatorColor(ContextCompat.getColor(fragment.getContext(),R.color.statistics_budgeting_under_80));
        } else if (percentage < 100) {
            holder.linearProgress.setIndicatorColor(ContextCompat.getColor(fragment.getContext(),R.color.statistics_budgeting_under_100));
            textStatus = "You are almost failing your budget!";
        } else if (percentage >= 100) {
            holder.linearProgress.setIndicatorColor(ContextCompat.getColor(fragment.getContext(),R.color.statistics_budgeting_over_100));
            textStatus = "You have spent too much money!";
        }

        setTextViewText(holder.spendingStatusTextView, textStatus);

        if (budget.getSpent() > budget.getBudget()) {
            holder.spendingStatusImageView.setBackgroundResource(R.drawable.ic_failed);
            holder.spendingStatusTextView.setTextColor(context.getColor(R.color.expenses_red));
        } else {
            holder.spendingStatusImageView.setBackgroundResource(R.drawable.ic_approved);
            holder.spendingStatusTextView.setTextColor(context.getColor(R.color.text_color));
        }
    }

    public Budget getBudget(int position) {
        return budgetList.get(position);
    }
}
