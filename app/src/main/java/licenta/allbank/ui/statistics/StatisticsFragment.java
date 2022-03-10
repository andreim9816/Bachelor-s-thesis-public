package licenta.allbank.ui.statistics;

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
import licenta.allbank.data.adapter.StatisticsFragmentAdapter;

public class StatisticsFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout_statistics);
        ViewPager2 viewPager = view.findViewById(R.id.viewPager_statistics);

        StatisticsFragmentAdapter viewPagerAdapter = new StatisticsFragmentAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        /* Disable swiping */
        viewPager.setUserInputEnabled(false);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Statistics");
            } else if (position == 1) {
                tab.setText("Savings");
            }
        }).attach();
    }
}