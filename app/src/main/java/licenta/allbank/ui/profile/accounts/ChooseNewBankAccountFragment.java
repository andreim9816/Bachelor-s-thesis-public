package licenta.allbank.ui.profile.accounts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import licenta.allbank.R;
import licenta.allbank.service.ServiceBcr;
import licenta.allbank.service.ServiceBt;
import licenta.allbank.utils.messages.MessageDisplay;

import static licenta.allbank.ui.home.HomeFragment.navigateToNextFragment;


public class ChooseNewBankAccountFragment extends Fragment {
    private static final String ERROR_MESSAGE = "Please choose a bank!";

    private NavController navController;
    private ImageView bcrImageView, btImageView;
    private CardView bcrCardView, btCardView;
    private Button nextButton;
    private boolean justStarted = true;
    private String bankSelected = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_new_bank_account, container, false);

        /* Init UI elements */
        initUiElements(view);

        /* Init the buttons behaviour */
        initBehaviours();

        /* Init default values */
        initDefaultValues();

        return view;
    }

    private void initUiElements(View view) {
        bcrImageView = view.findViewById(R.id.image_view_choose_new_bank_account_bcr);
        btImageView = view.findViewById(R.id.image_view_choose_new_bank_account_bt);
        nextButton = view.findViewById(R.id.button_choose_new_bank_account_next);
        bcrCardView = view.findViewById(R.id.material_card_view_choose_new_bank_account_bcr);
        btCardView = view.findViewById(R.id.material_card_view_choose_new_bank_account_bt);
    }

    private void initBehaviours() {
        initBcrImageView();
        initBtImageView();
        initNextButton();
    }

    private void initDefaultValues() {
        bcrCardView.setCardBackgroundColor(0);
        btCardView.setCardBackgroundColor(0);
        bankSelected = "";
    }

    private void initBcrImageView() {
        bcrImageView.setOnClickListener(v -> {
            btCardView.setCardBackgroundColor(0);
            if (bankSelected.equalsIgnoreCase("BCR")) {
                bankSelected = "";
                bcrCardView.setCardBackgroundColor(0);
            } else {
                bankSelected = "BCR";
                bcrCardView.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary_light));
            }
        });
    }

    private void initBtImageView() {
        btImageView.setOnClickListener(v -> {
            bcrCardView.setCardBackgroundColor(0);
            if (bankSelected.equalsIgnoreCase("BT")) {
                bankSelected = "";
                btCardView.setCardBackgroundColor(0);
            } else {
                bankSelected = "BT";
                btCardView.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary_light));
            }
        });
    }

    private void initNextButton() {
        nextButton.setOnClickListener(v -> {
            if (!bankSelected.isEmpty()) {
                if (bankSelected.equalsIgnoreCase("BCR")) {
                    ServiceBcr.getAuthAccessCodeBcr(requireContext(), ServiceBcr.STATE_ADD_ACCOUNT);
                } else {
                    ServiceBt.getAuthAccessCodeBT(requireContext(), ServiceBt.STATE_ADD_ACCOUNT);
                }
            } else {
                MessageDisplay.showLongMessage(requireContext(), ERROR_MESSAGE);
            }

            bankSelected = "";
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        /* Handling for navigation to the next fragment */
        if (!justStarted) {
            justStarted = true;
            navigateToNextFragment(navController, R.id.action_chooseNewBankAccountFragment_to_navigation_profile, null);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        justStarted = false;
    }
}