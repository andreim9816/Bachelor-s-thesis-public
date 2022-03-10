package licenta.allbank.ui.login;

import android.content.Context;
import android.content.Intent;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputLayout;

import licenta.allbank.R;
import licenta.allbank.activity.MainActivity;
import licenta.allbank.data.model.allbank.auth.LoginResponse;
import licenta.allbank.service.ServiceServer;
import licenta.allbank.utils.auth.GetAuthTokenCallbackServer;
import licenta.allbank.utils.messages.MessageDisplay;

import static licenta.allbank.ui.home.HomeFragment.navigateToNextFragment;

public class LoginFragment extends Fragment {
    private static final String PASSWORD_EMPTY_ERROR = "Please enter password!";
    private static final String USERNAME_EMPTY_ERROR = "Please enter username!";

    private static final String USERNAME_TOO_SHORT_ERROR = "Username should have at least 8 characters!";
    private static final String PASSWORD_TOO_SHORT_ERROR = "Password should have at least 8 characters!";

    public static final int USERNAME_MIN_CHARS = 8;
    public static final int PASSWORD_MIN_CHARS = 8;

    private Button loginButton, registerButton;
    private EditText usernameEditText, passwordEditText;
    private TextInputLayout usernameTextInputLayout, passwordTextInputLayout;
    private NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        initUiElements(view);

        initBehaviours();

//        loginButton.performClick();
    }

    private void initUiElements(View view) {
        loginButton = view.findViewById(R.id.button_login);
        registerButton = view.findViewById(R.id.button_login_register);
        usernameEditText = view.findViewById(R.id.edit_text_login_username);
        passwordEditText = view.findViewById(R.id.edit_text_login_password);

        usernameTextInputLayout = view.findViewById(R.id.text_input_layout_login_username);
        passwordTextInputLayout = view.findViewById(R.id.text_input_layout_login_password);
    }

    private void initBehaviours() {
        initLoginButtonBehaviour();
        initRegisterButtonBehaviour();
    }

    @Override
    public void onResume() {
        super.onResume();

        usernameEditText.setText("");
        passwordEditText.setText("");
        usernameTextInputLayout.setErrorEnabled(false);
        passwordTextInputLayout.setErrorEnabled(false);
    }

    private void initLoginButtonBehaviour() {
        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            char[] password = passwordEditText.getText().toString().trim().toCharArray();

            try {
                ServiceServer.getInstance(getContext()).login(username, password, new GetAuthTokenCallbackServer() {
                    @Override
                    public void onSuccess(LoginResponse loginResponse) {
                        ServiceServer.setAccessToken(loginResponse.getAccessToken());
                        startNewActivity(getContext(), MainActivity.class);
                    }

                    @Override
                    public void onError(Throwable t) {
                        MessageDisplay.showErrorMessage(getContext(), t.getMessage());
                    }
                });

            } catch (Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initRegisterButtonBehaviour() {
        registerButton.setOnClickListener(v -> navigateToNextFragment(navController, R.id.action_loginFragment_to_registerFragment, null));
    }

    public static void startNewActivity(Context context, Class mClass) {
        Intent intent = new Intent(context, mClass);
        context.startActivity(intent);
    }
}