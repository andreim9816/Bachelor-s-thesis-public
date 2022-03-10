package licenta.allbank.ui.transactions;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputLayout;

import licenta.allbank.R;
import licenta.allbank.data.model.allbank.UserData;
import licenta.allbank.data.model.allbank.others.NewTransaction;
import licenta.allbank.data.model.allbank.others.PaymentResponse;
import licenta.allbank.service.ServiceBcr;
import licenta.allbank.service.ServiceBt;
import licenta.allbank.service.ServiceServer;
import licenta.allbank.utils.messages.MessageDisplay;
import licenta.allbank.utils.messages.TransactionMessage;
import licenta.allbank.utils.server.CallbackGenericResponse;

import static licenta.allbank.service.ServiceServer.ERROR_MESSAGE_SERVER;
import static licenta.allbank.ui.home.HomeFragment.navigateToNextFragment;
import static licenta.allbank.ui.transactions.newTransaction.NewTransactionFragment.NEW_TRANSACTION;

public class ConfirmTransactionFragment extends Fragment {
    private static PaymentResponse response;

    private TextInputLayout allbankInputLayout;
    private EditText allbankEditText;
    private Button confirmButton;

    private boolean justStarted = true;
    private NewTransaction newTransaction;
    private NavController navController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        Bundle bundle = getArguments();
        if (bundle != null) {
            newTransaction = bundle.getParcelable(NEW_TRANSACTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirm, container, false);

        /* Init UI elements */
        initUiElements(view);

        /* Init the buttons behaviour */
        initBehaviours();

        return view;
    }

    private void initUiElements(View view) {
        allbankEditText = view.findViewById(R.id.edit_text_confirm_password_allbank);
        allbankInputLayout = view.findViewById(R.id.text_input_layout_confirm_password_allbank);

        confirmButton = view.findViewById(R.id.button_confirm);
    }

    private void initBehaviours() {
        confirmButton.setOnClickListener(v -> {
            if (checkEditTextNotNull(allbankEditText)) {
                try {
                    ServiceServer serviceServer = ServiceServer.getInstance(getContext());

                    String username = UserData.getUsername();
                    char[] allbankPassword = allbankEditText.getText().toString().toCharArray();

                    serviceServer.confirmTransaction(ServiceServer.getAccessToken(), username, allbankPassword, newTransaction, new CallbackGenericResponse<PaymentResponse>() {
                        @Override
                        public void onSuccess(PaymentResponse response) {
                            setPaymentResponse(response);

                            if (newTransaction.getBank().equalsIgnoreCase(ServiceBcr.BCR)) {
                                /* User has to login in again to proof its identity, then he is redirected to payment signing */
                                ServiceBcr.getAuthAccessCodeBcr(requireContext(), ServiceBcr.STATE_LOGIN_FOR_PAYMENT);
                            } else if (newTransaction.getBank().equalsIgnoreCase(ServiceBt.BT)) {
                                ServiceBt.getAuthAccessCodeBT(requireContext(), ServiceBt.LOGIN_FOR_PAYMENT_CONFIRM);
                            } else {
                                MessageDisplay.showLongMessage(getContext(), TransactionMessage.ERROR_TRANSACTION_ADDED);
                                navigateToNextFragment(navController, R.id.action_confirmTransactionFragment_to_newTransactionSuccessFragment, null);
                            }
                        }

                        @Override
                        public void onError(Throwable t) {
                            MessageDisplay.showLongMessage(getContext(), t.getMessage());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    MessageDisplay.showLongMessage(getContext(), ERROR_MESSAGE_SERVER);
                }
            }
        });
    }

    private boolean checkEditTextNotNull(EditText editText) {
        return !TextUtils.isEmpty(editText.getText());
    }

    public static PaymentResponse getResponse() {
        return response;
    }

    private static void setPaymentResponse(PaymentResponse paymentResponse) {
        response = paymentResponse;
    }

    @Override
    public void onResume() {
        super.onResume();
        /* Handling for navigation to the next fragment */
        if (!justStarted) {
            justStarted = true;
            Bundle bundle = new Bundle();
            bundle.putParcelable(NEW_TRANSACTION, newTransaction);
            navigateToNextFragment(navController, R.id.action_confirmTransactionFragment_to_newTransactionSuccessFragment, bundle);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        justStarted = false;
    }
}
