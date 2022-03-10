package licenta.allbank.ui.transactions.generateQr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import licenta.allbank.R;
import licenta.allbank.data.adapter.AccountTransactionsAdapter;
import licenta.allbank.data.model.allbank.others.QrData;
import licenta.allbank.data.model.database.Account;
import licenta.allbank.ui.profile.accounts.AccountsViewModel;
import licenta.allbank.utils.click_interface.ClickInterface;
import licenta.allbank.utils.messages.MessageDisplay;

import static licenta.allbank.ui.home.HomeFragment.navigateToNextFragment;
import static licenta.allbank.ui.transactions.generateQr.GenerateCodeQrFragment.QR_DATA;

public class GenerateCodeQrAccountsFragment extends Fragment implements ClickInterface {
    private RecyclerView accountsRecyclerView;
    private AccountTransactionsAdapter accountAdapter;

    private AccountsViewModel accountsViewModel;
    private NavController navController;
    private QrData qrData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        accountsViewModel = new ViewModelProvider(this).get(AccountsViewModel.class);

        Bundle bundle = getArguments();
        if (bundle != null) {
            qrData = bundle.getParcelable(QR_DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_generate_code_qr_accounts, container, false);

        /* Init UI elements */
        initUiElements(view);

        /* Init the buttons behaviour */
        initBehaviours();

        /* Init default values */
        initDefaultValues();

        return view;
    }

    private void initUiElements(View view) {
        accountsRecyclerView = view.findViewById(R.id.recycler_view_generate_code_qr_accounts);
    }

    private void initBehaviours() {
        initRecyclerViewAccounts();
    }

    private void initDefaultValues() {
        /* Display data on recycler view */
        accountsViewModel.getAllAccounts().observe(getViewLifecycleOwner(), accounts -> {
            accountAdapter.setAccounts(accounts);
        });
    }

    private void initRecyclerViewAccounts() {
        setLayoutManagerRecyclerViewAccounts();
        setAdapterRecyclerViewAccounts();
    }

    private void setLayoutManagerRecyclerViewAccounts() {
        accountsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
    }

    private void setAdapterRecyclerViewAccounts() {
        accountAdapter = new AccountTransactionsAdapter(getContext(), this);
        accountsRecyclerView.setAdapter(accountAdapter);
    }

    @Override
    public void onClick(int position) {
        Account account = accountAdapter.getAccount(position);
        if (account.isEnabled()) {
            String iban = account.getIban();

            qrData.setCreditorIban(iban);

            Bundle bundle = new Bundle();
            bundle.putParcelable(QR_DATA, qrData);

            navigateToNextFragment(navController, R.id.action_generateCodeQrAccountsFragment_to_imageCodeQrFragment, bundle);
        } else {
            MessageDisplay.showLongMessage(getContext(), "You cannot choose this account. It is disabled!");
        }
    }
}