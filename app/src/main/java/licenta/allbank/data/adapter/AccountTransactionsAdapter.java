package licenta.allbank.data.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;

import licenta.allbank.R;
import licenta.allbank.data.model.database.Account;
import licenta.allbank.service.ServiceBcr;
import licenta.allbank.utils.click_interface.ClickInterface;

import static licenta.allbank.ui.homeAdvanced.HomeAdvancedFragment.setTextViewText;

public class AccountTransactionsAdapter extends RecyclerView.Adapter<AccountTransactionsAdapter.AccountViewHolder> {
    static class AccountViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView bankLogoImageView;
        private TextView ibanTextView, balanceTextView, currencyTextView, bankTextView;
        private CardView cardView;
        private ClickInterface clickInterface;
        private SwitchMaterial enableSwitch;

        public AccountViewHolder(@NonNull View itemView, ClickInterface clickInterface) {
            super(itemView);
            this.bankLogoImageView = itemView.findViewById(R.id.image_view_accounts_recycler_view_bank_logo);
            this.ibanTextView = itemView.findViewById(R.id.text_view_accounts_recycler_view_iban);
            this.balanceTextView = itemView.findViewById(R.id.text_view_accounts_recycler_view_balance);
            this.currencyTextView = itemView.findViewById(R.id.text_view_accounts_recycler_view_currency);
            this.bankTextView = itemView.findViewById(R.id.text_view_accounts_recycler_view_bank);
            this.enableSwitch = itemView.findViewById(R.id.switch_accounts_recycler_view_enable);
            this.cardView = itemView.findViewById(R.id.card_view_accounts);

            this.clickInterface = clickInterface;
            this.cardView.setOnClickListener(this);
            this.enableSwitch.setFocusable(false);
            this.enableSwitch.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View v) {
            clickInterface.onClick(getAdapterPosition());
        }
    }

    private final LayoutInflater inflater;
    private final Context context;
    private List<Account> accountList;
    private ClickInterface clickInterface;

    public AccountTransactionsAdapter(Context context, ClickInterface clickInterface) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.clickInterface = clickInterface;
    }

    public void setAccounts(List<Account> accountList) {
        this.accountList = accountList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_item_profile_account, parent, false);
        return new AccountViewHolder(view, clickInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        if (accountList != null) {
            Account account = accountList.get(position);

            String currency = "RON"; // account.getCurrency()
            setTextViewText(holder.balanceTextView, account.getBalance());
            setTextViewText(holder.currencyTextView, currency);
            setTextViewText(holder.bankTextView, account.getName());
            setTextViewText(holder.ibanTextView, account.getIban());

            if (account.getBank().equalsIgnoreCase(ServiceBcr.BCR)) {
                holder.bankLogoImageView.setBackgroundResource(R.drawable.logo_bcr);
            } else {
                holder.bankLogoImageView.setBackgroundResource(R.drawable.logo_bt);
            }

            holder.enableSwitch.setChecked(account.isEnabled());
            if (!account.isEnabled()) {
                holder.cardView.setCardBackgroundColor(Color.parseColor("#c9c9c9"));
                holder.cardView.setAlpha(0.4f);
            } else {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary_dark));
                holder.cardView.setAlpha(1f);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (accountList != null) {
            return accountList.size();
        }
        return 0;
    }

    public Account getAccount(int position) {
        return accountList.get(position);
    }
}
