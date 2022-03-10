package licenta.allbank.data.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import licenta.allbank.ui.statistics.StatisticsBudgetingFragment;
import licenta.allbank.ui.statistics.StatisticsCategoryFragment;

public class StatisticsFragmentAdapter extends FragmentStateAdapter {
    private final int NUMBER_OF_FRAGMENTS = 2;

    public StatisticsFragmentAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new StatisticsCategoryFragment();
            case 1:
                return new StatisticsBudgetingFragment();
            default:
                return new StatisticsCategoryFragment();
        }
    }

    @Override
    public int getItemCount() {
        return NUMBER_OF_FRAGMENTS;
    }
}
