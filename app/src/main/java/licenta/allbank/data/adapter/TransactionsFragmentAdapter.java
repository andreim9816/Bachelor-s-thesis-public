package licenta.allbank.data.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import licenta.allbank.ui.statistics.StatisticsCategoryFragment;
import licenta.allbank.ui.transactions.generateQr.GenerateCodeQrFragment;
import licenta.allbank.ui.transactions.newTransaction.NewTransactionConfirmFragment;
import licenta.allbank.ui.transactions.newTransaction.NewTransactionFragment;
import licenta.allbank.ui.transactions.scanQr.ScanCodeQrFragment;

public class TransactionsFragmentAdapter extends FragmentStateAdapter {
    private final int NUMBER_OF_FRAGMENTS = 3;

    public TransactionsFragmentAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new NewTransactionFragment();
            case 1:
                return new ScanCodeQrFragment();
            case 2:
                return new GenerateCodeQrFragment();
            default:
                return new StatisticsCategoryFragment();
        }
    }

    @Override
    public int getItemCount() {
        return NUMBER_OF_FRAGMENTS;
    }
}
