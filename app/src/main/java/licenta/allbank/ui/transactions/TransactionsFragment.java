package licenta.allbank.ui.transactions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import licenta.allbank.R;
import licenta.allbank.data.adapter.TransactionsFragmentAdapter;

public class TransactionsFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transactions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout_transactions);
        ViewPager2 viewPager = view.findViewById(R.id.viewPager_transactions);

        TransactionsFragmentAdapter viewPagerAdapter = new TransactionsFragmentAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        /* Disable swiping */
        viewPager.setUserInputEnabled(false);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("New");
            } else if (position == 1) {
                tab.setText("Scan QR");
            } else if (position == 2) {
                tab.setText("New QR");
            }
        }).attach();
    }
}