package licenta.allbank.ui.profile;

import android.os.Bundle;
import android.text.TextUtils;
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
import licenta.allbank.service.ServiceServer;
import licenta.allbank.utils.messages.MessageDisplay;
import licenta.allbank.utils.server.CallbackNoContent;
import licenta.allbank.utils.validation.Validator;

import static licenta.allbank.ui.home.HomeFragment.navigateToNextFragment;

public class EditProfileFragment extends Fragment {
    private TextInputLayout emailTextInputLayout, phoneTextInputLayout, lastNameTextInputLayout, firstNameTextInputLayout, passwordTextInputLayout, newPasswordTextInputLayout, confirmPasswordTextInputLayout;
    private EditText emailEditText, phoneEditText, lastNameEditText, firstNameEditText, passwordEditText, newPasswordEditText, confirmPasswordEditText;
    private Button editButton;
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
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        /* Init UI elements */
        initUiElements(view);

        /* Init the buttons behaviour */
        initBehaviours();

        /* Init default values */
        initDefaultValues();

        return view;
    }

    private void initUiElements(View view) {
        emailEditText = view.findViewById(R.id.edit_text_edit_profile_email);
        phoneEditText = view.findViewById(R.id.edit_text_edit_profile_phone);
        lastNameEditText = view.findViewById(R.id.edit_text_edit_profile_last_name);
        firstNameEditText = view.findViewById(R.id.edit_text_edit_profile_first_name);
        passwordEditText = view.findViewById(R.id.edit_text_edit_profile_current_password);
        newPasswordEditText = view.findViewById(R.id.edit_text_edit_profile_new_password);
        confirmPasswordEditText = view.findViewById(R.id.edit_text_edit_profile_confirm);

        emailTextInputLayout = view.findViewById(R.id.text_input_layout_edit_profile_email);
        phoneTextInputLayout = view.findViewById(R.id.text_input_layout_edit_profile_phone);
        lastNameTextInputLayout = view.findViewById(R.id.text_input_layout_edit_profile_last_name);
        firstNameTextInputLayout = view.findViewById(R.id.text_input_layout_edit_profile_first_name);
        passwordTextInputLayout = view.findViewById(R.id.text_input_layout_edit_profile_current_password);
        newPasswordTextInputLayout = view.findViewById(R.id.text_input_layout_edit_profile_new_password);
        confirmPasswordTextInputLayout = view.findViewById(R.id.text_input_layout_edit_profile_password_confirm);

        editButton = view.findViewById(R.id.button_edit_profile);
    }

    private void initBehaviours() {
        initEditButton();
    }

    private void initDefaultValues() {
        emailEditText.setText(UserData.getEmail());
        phoneEditText.setText(UserData.getPhone());
        lastNameEditText.setText(UserData.getLastName());
        firstNameEditText.setText(UserData.getFirstName());
    }

    private void initEditButton() {
        editButton.setOnClickListener(v -> {
            if (validateFields()) {
                String email = emailEditText.getText().toString().trim();
                String phone = phoneEditText.getText().toString().trim();
                String lastName = lastNameEditText.getText().toString().trim();
                String firstName = firstNameEditText.getText().toString().trim();

                try {
                    ServiceServer.getInstance(requireContext()).editProfile(ServiceServer.getAccessToken(), email, phone, lastName, firstName, passwordEditText.getText().toString().toCharArray(), newPasswordEditText.getText().toString().toCharArray(), new CallbackNoContent() {
                        @Override
                        public void onSuccess() {
                            MessageDisplay.showLongMessage(getContext(), "Profile edited successfully!");
                            UserData.setPhone(phone);
                            UserData.setLastName(lastName);
                            UserData.setFirstName(firstName);
                            UserData.setEmail(email);
                            navigateToNextFragment(navController, R.id.action_editProfileFragment_to_navigation_profile, null);
                        }

                        @Override
                        public void onError(Throwable t) {
                            String errorMessage;
                            if (t.getMessage() != null && !t.getMessage().isEmpty()) {
                                errorMessage = t.getMessage();
                            } else {
                                errorMessage = "Could not edit profile. Please try again later!";
                            }
                            MessageDisplay.showErrorMessage(getContext(), errorMessage);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean validateFields() {
        boolean valid = true;

        String phoneValid = Validator.notMatches(getContext(), phoneEditText.getText().toString().trim(), Validator.type.PHONE);
        String emailValid = Validator.notMatches(getContext(), emailEditText.getText().toString().trim(), Validator.type.EMAIL);
        String firstNameValid = Validator.notMatches(getContext(), firstNameEditText.getText().toString().trim(), Validator.type.FIRST_NAME);
        String lastNameValid = Validator.notMatches(getContext(), lastNameEditText.getText().toString().trim(), Validator.type.LAST_NAME);

        String newPasswordValid = Validator.notMatches(getContext(), newPasswordEditText.getText().toString().trim(), Validator.type.PASSWORD);
        String confirmPasswordValid = Validator.notMatches(getContext(), confirmPasswordEditText.getText().toString().trim(), Validator.type.PASSWORD);

        setErrorOrClearMessageError(phoneTextInputLayout, phoneValid);
        setErrorOrClearMessageError(emailTextInputLayout, emailValid);
        setErrorOrClearMessageError(firstNameTextInputLayout, firstNameValid);
        setErrorOrClearMessageError(lastNameTextInputLayout, lastNameValid);

        if (!(!TextUtils.isEmpty(phoneEditText.getText())
                & !TextUtils.isEmpty(emailEditText.getText())
                & !TextUtils.isEmpty(firstNameEditText.getText())
                & !TextUtils.isEmpty(lastNameEditText.getText())
                & !TextUtils.isEmpty(passwordEditText.getText())
                & phoneValid == null
                & emailValid == null
                & firstNameValid == null
                & lastNameValid == null)) {
            valid = false;
        }

        /* If there is at least one password field that is not empty, all of them should be completed */
        boolean passwordFilled = !TextUtils.isEmpty(passwordEditText.getText());
        boolean newPasswordFilled = !TextUtils.isEmpty(confirmPasswordEditText.getText());
        boolean confirmNewPasswordFilled = !TextUtils.isEmpty(newPasswordEditText.getText());

        if (!passwordFilled) {
            MessageDisplay.showErrorMessage(requireContext(), "You must fill in the password!");
            return false;
        }

        if (newPasswordFilled || confirmNewPasswordFilled) {

            /* Case 1: not all password fields are completed */
            if (!(newPasswordFilled && confirmNewPasswordFilled)) {
                MessageDisplay.showErrorMessage(requireContext(), "You must fill in all the the password fields!");
                return false;
            }

            /* Case 2: passwords don't match */
            if (!newPasswordEditText.getText().toString().trim().equals(confirmPasswordEditText.getText().toString().trim())) {
                MessageDisplay.showErrorMessage(requireContext(), "Passwords don't match!");
                return false;
            }

            /* Case 3: passwords don't respect the regex */
            if (newPasswordValid != null || confirmPasswordValid != null) {
                if (newPasswordValid != null) {
                    newPasswordEditText.setError(newPasswordValid);
                }
                if (confirmPasswordValid != null) {
                    confirmPasswordEditText.setError(newPasswordValid);
                }
                return false;
            }
        }

        return valid;
    }

    public static void setErrorOrClearMessageError(TextInputLayout textInputLayout, String messageError) {
        if (messageError == null) {
            textInputLayout.setErrorEnabled(false);
        } else {
            textInputLayout.setError(messageError);
        }
    }
}