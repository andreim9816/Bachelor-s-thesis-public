package licenta.allbank.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import licenta.allbank.R;
import licenta.allbank.data.model.database.Transaction;
import licenta.allbank.service.ServiceBcr;
import licenta.allbank.utils.click_interface.ClickInterfaceTransaction;

import static licenta.allbank.ui.homeAdvanced.HomeAdvancedFragment.roundTwoDecimals;
import static licenta.allbank.ui.homeAdvanced.HomeAdvancedFragment.setTextViewText;
import static licenta.allbank.utils.DateFormatGMT.convertDateToString;

public class TransactionListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static class LoadingHolder extends RecyclerView.ViewHolder {
        public LoadingHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CardView cardView;
        private TextView transactionCategoryTextView, transactionSumTextView, transactionDateTextView, detailsTextView;
        private ImageView transactionBankImageView;
        private final ClickInterfaceTransaction clickInterface;

        public TransactionViewHolder(@NonNull View itemView, ClickInterfaceTransaction clickInterface) {
            super(itemView);
            this.clickInterface = clickInterface;

            // init all the UI elements
            initUiElements(itemView);

            // attach onClickListener to cardView
            cardView.setOnClickListener(this);
        }

        private void initUiElements(View view) {
            this.cardView = view.findViewById(R.id.cardView_home_transaction);
            this.transactionBankImageView = view.findViewById(R.id.imageView_recycler_transaction_bank);
            this.transactionCategoryTextView = view.findViewById(R.id.textView_home_transaction_category);
            this.transactionSumTextView = view.findViewById(R.id.textView_home_transaction_sum);
            this.transactionDateTextView = view.findViewById(R.id.textView_home_transaction_date);
            this.detailsTextView = view.findViewById(R.id.textView_home_transaction_details);
        }

        @Override
        public void onClick(View v) {
            clickInterface.onClickTransaction(getAdapterPosition());
        }
    }

    private final int VIEW_TYPE_LOADING = 0;
    private final int VIEW_TYPE_ITEM = 1;
    private final LayoutInflater inflater;
    private final Context context;
    private final ClickInterfaceTransaction clickInterface;
    private List<Transaction> transactionsFiltered; // Filtered transactions based on the current choice

    public TransactionListAdapter(Context context, ClickInterfaceTransaction clickInterface) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.clickInterface = clickInterface;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = inflater.inflate(R.layout.recyclerview_item_transaction, parent, false);
            return new TransactionViewHolder(view, clickInterface);
        } else {
            View view = inflater.inflate(R.layout.item_loading, parent, false);
            return new LoadingHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (transactionsFiltered != null && viewHolder instanceof TransactionViewHolder) {
            TransactionViewHolder holder = (TransactionViewHolder) viewHolder;
            Transaction transaction = transactionsFiltered.get(position);
            if (transaction.getBank().equalsIgnoreCase(ServiceBcr.BCR)) {
                holder.transactionBankImageView.setBackgroundResource(R.drawable.logo_bcr);
            } else {
                holder.transactionBankImageView.setBackgroundResource(R.drawable.logo_bt);
            }
            setTextViewText(holder.transactionCategoryTextView, transaction.getTransactionCategory());
            setTextViewText(holder.transactionSumTextView, roundTwoDecimals(transaction.getTransactionAmount().getAmount()));
            setTextViewText(holder.transactionDateTextView, convertDateToString(transaction.getBookingDate()));

            /* No details provided */
            if (transaction.getDetails() == null || transaction.getDetails().length() == 0) {
                setTextViewText(holder.detailsTextView, "No details provided");
                holder.detailsTextView.setTextColor(ContextCompat.getColor(context, R.color.text_color_grey));
            } else {
                setTextViewText(holder.detailsTextView, transaction.getDetails());
                holder.detailsTextView.setTextColor(ContextCompat.getColor(context, R.color.text_color));
            }
            /* Set color depending on the transaction type */
            if (transaction.getTransactionAmount().getAmount() < 0) {
                holder.transactionSumTextView.setTextColor(ContextCompat.getColor(context, R.color.expenses_red));
            } else {
                holder.transactionSumTextView.setTextColor(ContextCompat.getColor(context, R.color.income_green));
            }

            /* Set transaction category as INCOME if it's the case */
            if (transaction.getTransactionAmount().getAmount() > 0) {
                setTextViewText(holder.transactionCategoryTextView, "Income");
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getTransaction(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        if (transactionsFiltered != null) {
            return transactionsFiltered.size();
        }
        return 0;
    }


    public void setTransactions(List<Transaction> transactions) {
        /* Notify UI whenever transaction list has been changed */
        this.transactionsFiltered = transactions;
        notifyDataSetChanged();
    }

    public Transaction getTransaction(int position) {
        return transactionsFiltered.get(position);
    }


    public int getFilteredTransactionsSize() {
        if (transactionsFiltered == null) {
            return 0;
        }
        return transactionsFiltered.size();
    }
}
