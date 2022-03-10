package licenta.allbank.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import licenta.allbank.R;
import licenta.allbank.data.model.allbank.others.TransactionOption;
import licenta.allbank.ui.homeAdvanced.HomeAdvancedFragment;
import licenta.allbank.utils.click_interface.ClickInterfaceTimeOption;

public class TransactionOptionAdapter extends RecyclerView.Adapter<TransactionOptionAdapter.TransactionOptionViewHolder> {

    static class TransactionOptionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ClickInterfaceTimeOption clickInterface;
        private final Button optionButton;

        public TransactionOptionViewHolder(@NonNull View itemView, ClickInterfaceTimeOption clickInterface) {
            super(itemView);
            this.clickInterface = clickInterface;
            optionButton = itemView.findViewById(R.id.button_recycler_view_transactions_option);
            optionButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickInterface.onClickTimeOption(getAdapterPosition());
        }
    }

    private final List<TransactionOption> transactionOptions;
    private final ClickInterfaceTimeOption clickInterface;
    private final LayoutInflater inflater;
    private final Context context;
    private final Fragment fragment;
    private int indexOptionSelected = -1;

    public TransactionOptionAdapter(Context context, Fragment fragment, List<TransactionOption> transactionOptions, ClickInterfaceTimeOption clickInterface) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.fragment = fragment;
        this.clickInterface = clickInterface;
        this.transactionOptions = transactionOptions;
        this.indexOptionSelected = -1;
    }

    @NonNull
    @Override
    public TransactionOptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recyclerview_item_transactions_option, parent, false);
        return new TransactionOptionViewHolder(view, clickInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionOptionViewHolder holder, int position) {
        if (transactionOptions != null) {
            TransactionOption option = transactionOptions.get(position);
            holder.optionButton.setText(option.getText());

            if (position == indexOptionSelected) {
                /* Color is changed just for HomeAdvancedFragment */
                if (fragment instanceof HomeAdvancedFragment) {
                    holder.optionButton.setBackgroundColor(ContextCompat.getColor(context, R.color.primary_dark));
                } else {
                    holder.optionButton.setBackgroundColor(ContextCompat.getColor(context, R.color.primary_light));
                }
                holder.optionButton.setTextColor(ContextCompat.getColor(context, R.color.text_color));
            } else {
                holder.optionButton.setBackgroundColor(ContextCompat.getColor(context, R.color.text_color));
                holder.optionButton.setTextColor(ContextCompat.getColor(context, R.color.primary_dark));
            }
        }
    }

    @Override
    public int getItemCount() {
        if (transactionOptions != null) {
            return transactionOptions.size();
        }
        return 0;
    }

    public TransactionOption getTransactionOption(int position) {
        return transactionOptions.get(position);
    }

    public void updateUI(int position) {
        this.indexOptionSelected = position;
        notifyDataSetChanged();
    }
}
