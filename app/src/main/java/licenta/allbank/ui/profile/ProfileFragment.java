package licenta.allbank.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import licenta.allbank.R;
import licenta.allbank.activity.LoginActivity;

import static licenta.allbank.ui.home.HomeFragment.navigateToNextFragment;
import static licenta.allbank.ui.login.LoginFragment.startNewActivity;

public class ProfileFragment extends Fragment {

    private Button editProfileButton, accountsButton, aboutButton, privacyButton, logoutButton;
    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        /* Init UI elements */
        initUiElements(view);

        /* Init the buttons behaviour */
        initBehaviours();

        return view;
    }

    private void initUiElements(View view) {
        editProfileButton = view.findViewById(R.id.button_profile_edit_profile);
        accountsButton = view.findViewById(R.id.button_profile_accounts);
        aboutButton = view.findViewById(R.id.button_profile_about);
        privacyButton = view.findViewById(R.id.button_profile_privacy);
        logoutButton = view.findViewById(R.id.button_profile_log_out);
    }

    private void initBehaviours() {
        initEditProfileButton();
        initAccountsButton();
        initAboutButton();
        initPrivacyButton();
        initLogOutButton();
    }

    private void initEditProfileButton() {
        editProfileButton.setOnClickListener(v -> navigateToNextFragment(navController, R.id.action_navigation_profile_to_editProfileFragment, null));
    }

    private void initAccountsButton() {
        accountsButton.setOnClickListener(v -> navigateToNextFragment(navController, R.id.action_navigation_profile_to_accountsFragment, null));
    }

    private void initAboutButton() {
        aboutButton.setOnClickListener(v -> navigateToNextFragment(navController, R.id.action_navigation_profile_to_aboutFragment, null));
    }

    private void initPrivacyButton() {
        privacyButton.setOnClickListener(v -> navigateToNextFragment(navController, R.id.action_navigation_profile_to_privacyFragment, null));
    }

    private void initLogOutButton() {
        logoutButton.setOnClickListener(v -> {
            startNewActivity(getContext(), LoginActivity.class);
            requireActivity().finish();
        });
    }
}