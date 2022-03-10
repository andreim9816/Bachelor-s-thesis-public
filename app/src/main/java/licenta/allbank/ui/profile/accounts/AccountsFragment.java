package licenta.allbank.ui.profile.accounts;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import licenta.allbank.R;
import licenta.allbank.data.adapter.AccountAdapter;
import licenta.allbank.data.model.database.Account;
import licenta.allbank.utils.click_interface.ClickInterface;

import static licenta.allbank.ui.home.HomeFragment.navigateToNextFragment;

public class AccountsFragment extends Fragment implements ClickInterface {

    private AccountAdapter accountAdapter;
    private RecyclerView accountsRecyclerView;
    private AccountsViewModel accountsViewModel;
    private FloatingActionButton fabButton;
    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        accountsViewModel = new ViewModelProvider(this).get(AccountsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accounts, container, false);

        /* Init UI elements */
        initUiElements(view);

        /* Init the buttons behaviour */
        initBehaviours();

        /* Init default values */
        initDefaultValues();

        return view;
    }

    private void initUiElements(View view) {
        accountsRecyclerView = view.findViewById(R.id.recycler_view_accounts);
        fabButton = view.findViewById(R.id.fab_accounts);
    }

    private void initBehaviours() {
        initRecyclerViewAccounts();
        initFabButton();
    }

    private void initFabButton() {
        fabButton.setOnClickListener(v -> {
            navigateToNextFragment(navController, R.id.action_accountsFragment2_to_chooseNewBankAccountFragment, null);
        });
    }

    private void initDefaultValues() {
        /* Display data on recycler view */
        accountsViewModel.getAllAccounts().observe(getViewLifecycleOwner(), accounts -> accountAdapter.setAccounts(accounts));
    }

    private void initRecyclerViewAccounts() {
        setLayoutManagerRecyclerViewAccounts();
        setAdapterRecyclerViewAccounts();
    }

    private void setLayoutManagerRecyclerViewAccounts() {
        accountsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
    }

    private void setAdapterRecyclerViewAccounts() {
        accountAdapter = new AccountAdapter(getContext(), this);
        accountsRecyclerView.setAdapter(accountAdapter);
    }

    @Override
    public void onClick(int position) {
        Account account = accountAdapter.getAccount(position);
        account.setEnabled(!account.isEnabled());
        accountsViewModel.update(account);
        accountAdapter.notifyItemChanged(position);
    }
}