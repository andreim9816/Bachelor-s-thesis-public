package licenta.allbank.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import licenta.allbank.R;
import licenta.allbank.data.model.allbank.others.IbanNameSearchItem;
import licenta.allbank.utils.click_interface.ClickInterface;

import static licenta.allbank.ui.homeAdvanced.HomeAdvancedFragment.setTextViewText;

public class IbanNameSearchAdapter extends RecyclerView.Adapter<IbanNameSearchAdapter.IbanNameViewHolder> {
    static class IbanNameViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView ibanTextView;
        private final TextView nameTextView;
        private final CardView cardView;
        private final ClickInterface clickInterface;

        public IbanNameViewHolder(@NonNull View itemView, ClickInterface clickInterface) {
            super(itemView);
            this.ibanTextView = itemView.findViewById(R.id.text_view_recycler_view_item_iban_name_search_iban);
            this.nameTextView = itemView.findViewById(R.id.text_view_recycler_view_item_iban_name_search_name);
            this.cardView = itemView.findViewById(R.id.card_view_recycler_view_item_iban_name_search);
            this.clickInterface = clickInterface;

            this.cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickInterface.onClick(getAdapterPosition());
        }
    }

    private final LayoutInflater inflater;
    private List<IbanNameSearchItem> ibanNameSearchItemList;
    private final ClickInterface clickInterface;

    public IbanNameSearchAdapter(Context context, ClickInterface clickInterface) {
        inflater = LayoutInflater.from(context);
        this.clickInterface = clickInterface;
    }

    @NonNull
    @Override
    public IbanNameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_item_iban_name_search, parent, false);
        return new IbanNameViewHolder(view, clickInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull IbanNameViewHolder holder, int position) {
        if (ibanNameSearchItemList != null) {
            IbanNameSearchItem ibanNameSearchItem = ibanNameSearchItemList.get(position);

            setTextViewText(holder.ibanTextView, ibanNameSearchItem.getIban());
            setTextViewText(holder.nameTextView, ibanNameSearchItem.getName());
        }
    }

    @Override
    public int getItemCount() {
        if (ibanNameSearchItemList != null) {
            return ibanNameSearchItemList.size();
        }
        return 0;
    }

    public void setIbanNameSearchItemList(List<IbanNameSearchItem> ibanNameSearchItemList) {
        this.ibanNameSearchItemList = ibanNameSearchItemList;
        notifyDataSetChanged();
    }

    public IbanNameSearchItem getIbanNameSearchItem(int position) {
        return ibanNameSearchItemList.get(position);
    }
}
