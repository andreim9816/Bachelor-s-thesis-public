package licenta.allbank.ui.transactions.generateQr;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputLayout;

import licenta.allbank.R;
import licenta.allbank.data.model.allbank.UserData;
import licenta.allbank.data.model.allbank.others.QrData;

import static licenta.allbank.ui.home.HomeFragment.navigateToNextFragment;

public class GenerateCodeQrFragment extends Fragment {
    public static final String QR_DATA = "QR_DATA";

    private static final String ERROR_AMOUNT = "Please fill amount!";
    private static final String ERROR_DETAILS = "Please fill transaction details!";

    private EditText amountEditText, detailsEditText;
    private TextInputLayout ibanInputLayout, amountInputLayout, detailsInputLayout;
    private Button generateQrButton;

    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_generate_code_qr, container, false);

        /* Init Ui elements */
        initUiElements(view);

        /* Init behaviours */
        initBehaviours();

        return view;
    }

    private void initUiElements(View view) {
        amountEditText = view.findViewById(R.id.edit_text_generate_qr_amount);
        detailsEditText = view.findViewById(R.id.edit_text_generate_qr_details);

        ibanInputLayout = view.findViewById(R.id.text_input_layout_confirm_password_allbank);
        amountInputLayout = view.findViewById(R.id.text_input_layout_generate_qr_amount);
        detailsInputLayout = view.findViewById(R.id.text_input_layout_generate_qr_details);

        generateQrButton = view.findViewById(R.id.button_generate_qr_confirm);
    }

    private void initBehaviours() {
        initGenerateQrButton();
    }

    private void initGenerateQrButton() {
        generateQrButton.setOnClickListener(v -> {
            if (checkEditTextCompleted(amountInputLayout, amountEditText, ERROR_AMOUNT)
                    & checkEditTextCompleted(detailsInputLayout, detailsEditText, ERROR_DETAILS)) {

                String details = detailsEditText.getText().toString().trim();
                String currency = "RON"; //TODO change
                float amount = Float.parseFloat(amountEditText.getText().toString().trim());

                QrData qrData = new QrData(currency, details, UserData.getFirstName(), UserData.getLastName(), amount);
                Bundle bundle = new Bundle();
                bundle.putParcelable(QR_DATA, qrData);

                navigateToNextFragment(navController, R.id.action_navigation_transactions_to_generateCodeQrAccountsFragment, bundle);
            }
        });
    }

    public static boolean checkEditTextCompleted(TextInputLayout inputLayout, EditText editText, String error) {
        if (TextUtils.isEmpty(editText.getText())) {
            inputLayout.setError(error);
            return false;
        }
        inputLayout.setErrorEnabled(false);
        return true;
    }

    private void initDefaultValues() {
        amountEditText.setText(null);
        detailsEditText.setText(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        initDefaultValues();
    }
}