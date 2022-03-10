package licenta.allbank.ui.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import licenta.allbank.R;
import licenta.allbank.activity.MainActivity;
import licenta.allbank.data.model.allbank.auth.LoginResponse;
import licenta.allbank.service.ServiceServer;
import licenta.allbank.utils.server.RegisterUserCallbackServer;
import licenta.allbank.utils.validation.Validator;

import static licenta.allbank.ui.login.LoginFragment.startNewActivity;

public class RegisterFragment extends Fragment {
    EditText usernameEditText, phoneEditText, emailEditText, firstNameEditText, lastNameEditText, passwordConfirmEditText, passwordEditText;
    Button registerButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUiElements(view);
        initRegisterButton();
    }

    private void initUiElements(View view) {
        registerButton = view.findViewById(R.id.button_register);
        usernameEditText = view.findViewById(R.id.edit_text_register_username);
        passwordEditText = view.findViewById(R.id.edit_text_register_password);
        passwordConfirmEditText = view.findViewById(R.id.edit_text_register_password_confirm);
        emailEditText = view.findViewById(R.id.edit_text_register_email);
        phoneEditText = view.findViewById(R.id.edit_text_register_phone);
        lastNameEditText = view.findViewById(R.id.edit_text_register_last_name);
        firstNameEditText = view.findViewById(R.id.edit_text_register_first_name);
    }

    private void initRegisterButton() {
        registerButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String firstName = firstNameEditText.getText().toString().trim();
            String lastName = lastNameEditText.getText().toString().trim();
            char[] password = passwordEditText.getText().toString().trim().toCharArray();

            if (validateFields()) {
                try {
                    ServiceServer.getInstance(getContext()).registerUser(username, password, firstName, lastName, phone, email, new RegisterUserCallbackServer() {
                        @Override
                        public void onSuccess(LoginResponse loginResponse) {
                            ServiceServer.setAccessToken(loginResponse.getAccessToken());
                            startNewActivity(getContext(), MainActivity.class);
                        }

                        @Override
                        public void onError(Throwable t) {

                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean validateFields() {
        String usernameValid = Validator.notMatches(getContext(), usernameEditText.getText().toString().trim(), Validator.type.USERNAME);
        String passwordValid = Validator.notMatches(getContext(), passwordEditText.getText().toString().trim(), Validator.type.PASSWORD);
        boolean valid = true;

        if (usernameValid != null) {
            usernameEditText.setError(usernameValid);
            valid = false;
        }
        if (passwordValid != null) {
            passwordEditText.setError(passwordValid);
            valid = false;
        }

        return valid;
    }
}