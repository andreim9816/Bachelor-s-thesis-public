package licenta.allbank.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.security.NoSuchAlgorithmException;

import licenta.allbank.R;
import licenta.allbank.data.model.allbank.others.PaymentResponse;
import licenta.allbank.data.model.bcr.TokenBcr;
import licenta.allbank.data.model.bt.TokenBt;
import licenta.allbank.service.ServiceBcr;
import licenta.allbank.service.ServiceBt;
import licenta.allbank.service.ServiceServer;
import licenta.allbank.ui.home.HomeViewModel;
import licenta.allbank.ui.transactions.ConfirmTransactionFragment;
import licenta.allbank.utils.Logging;
import licenta.allbank.utils.bcr.GetTokenCallbackBcr;
import licenta.allbank.utils.bt.GetTokenCallbackBt;
import licenta.allbank.utils.messages.AccountMessage;
import licenta.allbank.utils.messages.MessageDisplay;
import licenta.allbank.utils.messages.TransactionMessage;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private final static String TAG = "MainActivity";
    private static final String errorMessage = "Auth could not be completed! Please try again later!";
    private HomeViewModel homeViewModel;
    private NavController navController;
    public static PaymentResponse paymentResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_statistics,
                R.id.navigation_transactions,
                R.id.navigation_profile,
                R.id.homeAdvancedFragment)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        try {
            dealWithResponse(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    private void dealWithResponse(Intent intent) throws Exception {
        Uri uri = intent.getData();
        if (uri != null) {
            String fragmentState = uri.getFragment();
            if (fragmentState != null) {
                /* Custom handling for bcr redirect response */
                if (fragmentState.equals("state=" + ServiceBcr.STATE_PAYMENT)) {
                    MessageDisplay.showLongMessage(MainActivity.this, TransactionMessage.TRANSACTION_ADDED);
                }
            } else
                switch (uri.getQueryParameter("state")) {

                    case ServiceBcr.STATE_LOGIN_FOR_PAYMENT:
                        /* When the user entered again into its account, then he is redirected to the payment confirmation */
                        paymentResponse = ConfirmTransactionFragment.getResponse();
                        ServiceBcr.signPayment(this, paymentResponse.getScaRedirect());
                        break;

                    case ServiceBt.LOGIN_FOR_PAYMENT_CONFIRM:
                        /* When the user entered again into its account, then he is redirected to the payment confirmation */
                        paymentResponse = ConfirmTransactionFragment.getResponse();
                        ServiceBt.signPayment(this, paymentResponse.getPaymentId());
                        break;

                    case ServiceBcr.STATE_TOKEN: {
                        String code = uri.getQueryParameter("code");
                        ServiceServer.getInstance(this).getAccessTokenBcr(code, new GetTokenCallbackBcr() {
                            @Override
                            public void onSuccess(TokenBcr tokenBcr) {

                                String accessToken = tokenBcr.getToken();
                                String refreshToken = tokenBcr.getRefreshToken();
                                ServiceBcr.setAccessToken(accessToken);
                                ServiceBcr.setRefreshToken(refreshToken);
                            }

                            @Override
                            public void onError(Throwable t) {
                                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                            }
                        });
                        break;
                    }

                    case ServiceBt.STATE_AIS: {
                        String code = uri.getQueryParameter("code");
                        ServiceServer.getInstance(this).getAccessTokenBt(code, new GetTokenCallbackBt() {
                            @Override
                            public void onSuccess(TokenBt tokenBt) {
                                String accessToken = tokenBt.getAccessToken();
                                String refreshToken = tokenBt.getRefreshToken();
                                String consentId = tokenBt.getConsentId();
                                ServiceBt.setAccessToken(accessToken);
                                ServiceBt.setRefreshToken(refreshToken);
                                ServiceBt.setIdConsentAccounts(consentId);
                            }

                            @Override
                            public void onError(Throwable t) {
                                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                            }
                        });
                        break;
                    }

                    case ServiceBt.STATE_PIS: {
                        String code = uri.getQueryParameter("code");
                        ServiceServer.getInstance(this).getAccessTokenBt(code, new GetTokenCallbackBt() {
                            @Override
                            public void onSuccess(TokenBt tokenBt) {
                                MessageDisplay.showLongMessage(MainActivity.this, TransactionMessage.TRANSACTION_ADDED);
                            }

                            @Override
                            public void onError(Throwable t) {
                                MessageDisplay.showLongMessage(MainActivity.this, TransactionMessage.ERROR_TRANSACTION_ADDED);
                            }
                        });

                        break;
                    }

                    case ServiceBcr.STATE_ADD_ACCOUNT: {
                        String code = uri.getQueryParameter("code");
                        ServiceServer serviceServer = ServiceServer.getInstance(this);

                        serviceServer.getAccessTokenBcr(code, new GetTokenCallbackBcr() {
                            @Override
                            public void onSuccess(TokenBcr tokenBcr) {
                                String accessToken = tokenBcr.getToken();
                                String refreshToken = tokenBcr.getRefreshToken();
                                try {
                                    serviceServer.addBankNewBcrAccount(MainActivity.this, accessToken, refreshToken, "", homeViewModel);
                                } catch (NoSuchAlgorithmException e) {
                                    MessageDisplay.showErrorMessage(MainActivity.this, "There was a problem with the banking servers. Please try again later!");
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Throwable t) {
                                MessageDisplay.showLongMessage(MainActivity.this, AccountMessage.ERROR_ACCOUNT_ADDED);
                            }
                        });
                        break;
                    }

                    case ServiceBt.STATE_ADD_ACCOUNT: {
                        String code = uri.getQueryParameter("code");
                        ServiceServer serviceServer = ServiceServer.getInstance(this);

                        serviceServer.getAccessTokenBt(code, new GetTokenCallbackBt() {
                            @Override
                            public void onSuccess(TokenBt tokenBt) {
                                String accessToken = tokenBt.getAccessToken();
                                String refreshToken = tokenBt.getRefreshToken();
                                String consent = tokenBt.getConsentId();
                                try {
                                    serviceServer.addBankNewBtAccount(MainActivity.this, accessToken, refreshToken, consent, homeViewModel);
                                } catch (NoSuchAlgorithmException e) {
                                    MessageDisplay.showErrorMessage(MainActivity.this, "There was a problem with the banking servers. Please try again later!");
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Throwable t) {
                                MessageDisplay.showErrorMessage(MainActivity.this, errorMessage);
                            }
                        });
                        break;
                    }

                    default:
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        break;
                }
        }
    }

    @Override
    protected void onDestroy() {
        /* When app is destroyed, make sure the database content is deleted*/
        super.onDestroy();
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.deleteDatabase();

        Logging.show("MainActivity", " --- ON DESTROY ---");
        Logging.show("MainActivity", "S_A STERS DE CE S A STERS");

    }
}