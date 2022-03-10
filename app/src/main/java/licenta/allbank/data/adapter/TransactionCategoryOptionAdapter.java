package licenta.allbank.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import licenta.allbank.R;
import licenta.allbank.data.model.allbank.others.CategoryOption;
import licenta.allbank.utils.click_interface.ClickInterfaceCategory;

import static licenta.allbank.ui.budget.NewBudgetFragment.NEW_BUDGET;

public class TransactionCategoryOptionAdapter extends RecyclerView.Adapter<TransactionCategoryOptionAdapter.TransactionCategoryOptionViewHolder> {
    static class TransactionCategoryOptionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ClickInterfaceCategory clickInterface;
        private final LinearLayout linearLayout;
        private final TextView categoryTextView;
        private final ImageView categoryImageView;

        public TransactionCategoryOptionViewHolder(@NonNull View itemView, ClickInterfaceCategory clickInterface) {
            super(itemView);
            this.clickInterface = clickInterface;
            this.linearLayout = itemView.findViewById(R.id.linear_layout_statistics_recycler_view_item);
            this.categoryTextView = itemView.findViewById(R.id.text_view_statistics_recycler_view_item);
            this.categoryImageView = itemView.findViewById(R.id.image_view_statistics_recycler_view_item);
            this.linearLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickInterface.onClickCategory(getAdapterPosition());
        }
    }

    private List<CategoryOption> categoryOptions;
    private final ClickInterfaceCategory clickInterface;
    private final LayoutInflater inflater;
    private final String itemView;

    public TransactionCategoryOptionAdapter(Context context, List<CategoryOption> categoryOptions, ClickInterfaceCategory clickInterface, String itemView) {
        this.inflater = LayoutInflater.from(context);
        this.clickInterface = clickInterface;
        this.categoryOptions = categoryOptions;
        this.itemView = itemView;
    }

    @NonNull
    @Override
    public TransactionCategoryOptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (itemView.equals(NEW_BUDGET)) {
            view = inflater.inflate(R.layout.recycler_view_item_new_budget, parent, false);
        } else {
            view = inflater.inflate(R.layout.recycler_view_item_statistics_category, parent, false);
        }

        return new TransactionCategoryOptionViewHolder(view, clickInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionCategoryOptionViewHolder holder, int position) {
        if (categoryOptions != null) {
            CategoryOption categoryOption = categoryOptions.get(position);

            holder.categoryTextView.setText(categoryOption.getCategoryType());

            if (categoryOption.isSelected()) {
                holder.categoryImageView.setBackgroundResource(R.drawable.rounded_medium_selected);
                holder.categoryImageView.setImageResource(categoryOption.getCategoryImageViewSelected());
            } else {
                holder.categoryImageView.setBackgroundResource(R.drawable.rounded_medium);
                holder.categoryImageView.setImageResource(categoryOption.getCategoryImageViewUnselected());
            }
        }
    }

    @Override
    public int getItemCount() {
        if (categoryOptions != null) {
            return categoryOptions.size();
        }
        return 0;
    }

    public CategoryOption getCategoryOption(int position) {
        return categoryOptions.get(position);
    }

    public List<CategoryOption> getCategoryOptions() {
        return categoryOptions;
    }

    public void updateUI() {
        notifyDataSetChanged();
    }

    public void setCategoryOptions(List<CategoryOption> categoryOptions) {
        /* Notify UI whenever transaction list has been changed */
        this.categoryOptions = categoryOptions;
        updateUI();
    }
}
