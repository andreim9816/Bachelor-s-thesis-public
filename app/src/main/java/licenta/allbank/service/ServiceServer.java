package licenta.allbank.service;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import licenta.allbank.R;
import licenta.allbank.data.api.ServiceServerApi;
import licenta.allbank.data.model.allbank.AccountsResponse;
import licenta.allbank.data.model.allbank.ApiData;
import licenta.allbank.data.model.allbank.UserData;
import licenta.allbank.data.model.allbank.UserDataResponse;
import licenta.allbank.data.model.allbank.auth.LoginResponse;
import licenta.allbank.data.model.allbank.others.BudgetAddedResponse;
import licenta.allbank.data.model.allbank.others.BudgetResponse;
import licenta.allbank.data.model.allbank.others.NewTransaction;
import licenta.allbank.data.model.allbank.others.PaymentResponse;
import licenta.allbank.data.model.bcr.TokenBcr;
import licenta.allbank.data.model.bcr.accounts.AccountBcrAddedResponse;
import licenta.allbank.data.model.bcr.accounts.AccountDetailsBcr;
import licenta.allbank.data.model.bcr.transactions.TransactionAmountBcr;
import licenta.allbank.data.model.bcr.transactions.TransactionDetailsBcr;
import licenta.allbank.data.model.bcr.transactions.TransactionsObjectBcr;
import licenta.allbank.data.model.bt.TokenBt;
import licenta.allbank.data.model.bt.accounts.AccountBtAddedResponse;
import licenta.allbank.data.model.bt.accounts.AccountDetailsBt;
import licenta.allbank.data.model.bt.transactions.TransactionAmountBt;
import licenta.allbank.data.model.bt.transactions.TransactionDetailsBt;
import licenta.allbank.data.model.bt.transactions.TransactionsObjectBt;
import licenta.allbank.data.model.database.Account;
import licenta.allbank.data.model.database.Budget;
import licenta.allbank.data.model.database.Transaction;
import licenta.allbank.data.model.error.ErrorRequest;
import licenta.allbank.ui.home.HomeViewModel;
import licenta.allbank.ui.statistics.StatisticsCategoryFragment;
import licenta.allbank.utils.DateFormatGMT;
import licenta.allbank.utils.Encrypt;
import licenta.allbank.utils.auth.GetAuthTokenCallbackServer;
import licenta.allbank.utils.bcr.GetTokenCallbackBcr;
import licenta.allbank.utils.bt.GetTokenCallbackBt;
import licenta.allbank.utils.endpoints.ENDPOINTS;
import licenta.allbank.utils.messages.AccountMessage;
import licenta.allbank.utils.messages.MessageDisplay;
import licenta.allbank.utils.security.HttpSigning;
import licenta.allbank.utils.server.CallbackGenericResponse;
import licenta.allbank.utils.server.CallbackNoContent;
import licenta.allbank.utils.server.GetAccountsDataCallbackServer;
import licenta.allbank.utils.server.RegisterUserCallbackServer;
import licenta.allbank.utils.truststore.TrustCertificateServer;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static licenta.allbank.ui.home.HomeFragment.EXPENSES_TRANSACTIONS;
import static licenta.allbank.ui.home.HomeFragment.INCOME_TRANSACTIONS;
import static licenta.allbank.utils.DateFormatGMT.convertStringToDate;

public class ServiceServer {
    public static final String ERROR_MESSAGE_SERVER = "There was a problem with the server. Please try again later!";
    public final static String ENDPOINT = "https://192.168.0.241:3443/"; // ip-ul din cmd. Atentie HTTP sau HTTPS!
    public final static String REDIRECT_URI = "https://callback";
    public static ServiceServer serviceServer;
    private static char[] ACCESS_TOKEN;
    private final ServiceServerApi serviceServerApi;

    private static char[] KEYSTORE_SECRET_SERVER;

    private ServiceServer(ServiceServerApi serviceServerApi) {
        this.serviceServerApi = serviceServerApi;
    }

    public static ServiceServer getInstance(Context context) throws Exception {
        if (serviceServer == null) {
            TrustCertificateServer trustCertificateServer = new TrustCertificateServer("secret".toCharArray(), context);

            // Creating an SSLSocketFactory that uses our TrustManager
            SSLContext sslContext = trustCertificateServer.clientSSLContext();

            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustCertificateServer.getTrustManagers()[0])
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .connectTimeout(625, TimeUnit.SECONDS) //TODO CHANGE
                    .readTimeout(625, TimeUnit.SECONDS) //TODO CHANGE
                    .writeTimeout(625, TimeUnit.SECONDS) //TODO CHANGE
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
            serviceServer = new ServiceServer(retrofit.create(ServiceServerApi.class));
        }
        return serviceServer;
    }

    public void getAccessTokenBcr(String authorizationCode, final GetTokenCallbackBcr callback) throws NoSuchAlgorithmException {
        Map<String, String> map = Map.of(
                "authorizationCode", authorizationCode
        );
        String dateHeader = DateFormatGMT.getCurrentTime();
        String payload = HttpSigning.generatePayload(map);
        String signatureHeader = HttpSigning.generateSignatureHeader(ENDPOINTS.BCR_ACCESS_TOKEN_METHOD, ENDPOINTS.BCR_ACCESS_TOKEN_ENDPOINT, payload, dateHeader);
        String digestHeader = HttpSigning.generateDigestHeader(HttpSigning.generateDigest(payload));

        serviceServerApi.getAccessTokenBcr(digestHeader, dateHeader, signatureHeader, authorizationCode)
                .enqueue(new Callback<TokenBcr>() {
                    @Override
                    public void onResponse(@NotNull Call<TokenBcr> call, @NotNull Response<TokenBcr> response) {
                        if (response.isSuccessful()) {
                            TokenBcr tokenBcr = response.body();
                            if (tokenBcr != null) {
                                callback.onSuccess(tokenBcr);
                            } else {
                                callback.onError(new ErrorRequest());
                            }
                        } else {
                            callback.onError(new ErrorRequest());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<TokenBcr> call, @NotNull Throwable t) {
                        callback.onError(new ErrorRequest());
                    }
                });
    }

    public void getAccessTokenBt(String authorizationCode, final GetTokenCallbackBt callback) throws NoSuchAlgorithmException {
        Map<String, String> map = Map.of(
                "authorizationCode", authorizationCode
        );
        String dateHeader = DateFormatGMT.getCurrentTime();
        String payload = HttpSigning.generatePayload(map);
        String signatureHeader = HttpSigning.generateSignatureHeader(ENDPOINTS.BT_ACCESS_TOKEN_METHOD, ENDPOINTS.BT_ACCESS_TOKEN_ENDPOINT, payload, dateHeader);
        String digestHeader = HttpSigning.generateDigestHeader(HttpSigning.generateDigest(payload));

        serviceServerApi.getTokenBt(digestHeader, dateHeader, signatureHeader, authorizationCode)
                .enqueue(new Callback<TokenBt>() {
                    @Override
                    public void onResponse(@NotNull Call<TokenBt> call, @NotNull Response<TokenBt> response) {
                        if (response.isSuccessful()) {
                            TokenBt tokenBt = response.body();
                            if (tokenBt != null) {
                                callback.onSuccess(tokenBt);

                            } else {
                                callback.onError(new ErrorRequest());
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<TokenBt> call, @NotNull Throwable t) {
                        callback.onError(new ErrorRequest());
                    }
                });
    }


//    public void getNewAccessTokenBcr(final GetTokenCallbackBcr callback) {
//    // se foloseste la login dupa expirarea refresh_token
//        String refreshToken = ServiceBcr.getRefreshToken();
//        serviceServerApi.getNewAccessTokenBcr(refreshToken)
//                .enqueue(new Callback<TokenBcr>() {
//                    @Override
//                    public void onResponse(Call<TokenBcr> call, Response<TokenBcr> response) {
//                        if (response.isSuccessful()) {
//                            TokenBcr tokenBcr = response.body();
//                            if (tokenBcr != null) {
//                                callback.onSuccess(tokenBcr);
//                            } else {
//                                callback.onError(new ErrorRequest());
//                            }
//                        } else {
//                            callback.onError(new ErrorRequest());
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<TokenBcr> call, Throwable t) {
//                        callback.onError(new ErrorRequest());
//                    }
//                });
//    }

    public void addBankNewBcrAccount(Context context, String accessToken, String refreshToken, String consent, HomeViewModel homeViewModel) throws NoSuchAlgorithmException {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("accessToken", accessToken);
        map.put("refreshToken", refreshToken);
        map.put("consent", consent);

        String dateHeader = DateFormatGMT.getCurrentTime();
        String payload = HttpSigning.generatePayload(map);
        String signatureHeader = HttpSigning.generateSignatureHeader(ENDPOINTS.ADD_BCR_ACCOUNT_METHOD, ENDPOINTS.ADD_BCR_ACCOUNT_ENDPOINT, payload, dateHeader);
        String digestHeader = HttpSigning.generateDigestHeader(HttpSigning.generateDigest(payload));

        serviceServerApi.addBankNewBcrAccount(ServiceServer.getAccessToken(), digestHeader, dateHeader, signatureHeader, accessToken, refreshToken, consent)
                .enqueue(new Callback<AccountBcrAddedResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<AccountBcrAddedResponse> call, @NotNull Response<AccountBcrAddedResponse> response) {
                        if (response.isSuccessful()) {
                            AccountBcrAddedResponse accountsResponse = response.body();
                            if (accountsResponse != null) {
                                /* Add new data to database. No need to update budgets */
                                addNewBcrAccountsDataInDatabase(accountsResponse, homeViewModel);
                                MessageDisplay.showLongMessage(context, AccountMessage.ACCOUNT_ADDED);
                            } else {
                                displayToastErrorServer(context);
                            }
                        } else {
                            displayToastErrorServer(context);
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<AccountBcrAddedResponse> call, @NotNull Throwable t) {
                        displayToastErrorServer(context);
                    }
                });
    }

    public void addBankNewBtAccount(Context context, String accessToken, String refreshToken, String consent, HomeViewModel homeViewModel) throws NoSuchAlgorithmException {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("accessToken", accessToken);
        map.put("refreshToken", refreshToken);
        map.put("consent", consent);

        String dateHeader = DateFormatGMT.getCurrentTime();
        String payload = HttpSigning.generatePayload(map);
        String signatureHeader = HttpSigning.generateSignatureHeader(ENDPOINTS.ADD_BT_ACCOUNT_METHOD, ENDPOINTS.ADD_BT_ACCOUNT_ENDPOINT, payload, dateHeader);
        String digestHeader = HttpSigning.generateDigestHeader(HttpSigning.generateDigest(payload));

        serviceServerApi.addBankNewBtAccount(ServiceServer.getAccessToken(), digestHeader, dateHeader, signatureHeader, accessToken, refreshToken, consent)
                .enqueue(new Callback<AccountBtAddedResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<AccountBtAddedResponse> call, @NotNull Response<AccountBtAddedResponse> response) {
                        if (response.isSuccessful()) {
                            AccountBtAddedResponse accountsResponse = response.body();
                            if (accountsResponse != null) {
                                /* Add new data to database. No need to update budgets */
                                addNewBtAccountsDataInDatabase(accountsResponse, homeViewModel);
                                MessageDisplay.showLongMessage(context, AccountMessage.ACCOUNT_ADDED);
                            } else {
                                displayToastErrorServer(context);
                            }
                        } else {
                            displayToastErrorServer(context);
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<AccountBtAddedResponse> call, @NotNull Throwable t) {
                        displayToastErrorServer(context);
                    }
                });
    }

    /**
     * Function that gets Apis data, stored on the server-side. Called at the application start-up
     *
     * @param context       Context where the function was called
     * @param token         User's access token
     * @param dialog        Dialog object that is being showed, closed after receiving data
     * @param homeViewModel ViewModel object used for inserting data into database
     * @param fragment      Fragment object used for ViewModel querying
     */
    public void getApiDataServer(Context context, String token, Dialog dialog, HomeViewModel homeViewModel, Fragment fragment) {
        serviceServerApi.getApiData()
                .enqueue(new Callback<ApiData>() {
                    @Override
                    public void onResponse(@NotNull Call<ApiData> call, @NotNull Response<ApiData> response) {
                        if (response.isSuccessful()) {
                            ApiData apiData = response.body();
                            if (apiData != null) {
                                /* Set the new api data */
                                setClientData(apiData);
                                try {
                                    ServiceServer.getInstance(context).getAccountsData(token, new GetAccountsDataCallbackServer() {
                                        @Override
                                        public void onSuccess(AccountsResponse accountsResponse) {
                                            addAccountsDataInDatabase(accountsResponse, homeViewModel, fragment, true);
                                            /* Store user data */
                                            setUserData(accountsResponse.getUserDataResponse());
                                        }

                                        @Override
                                        public void onError(Throwable t) {
                                            closeDialogProgressBar(dialog);
                                            displayToastErrorServer(context);
                                            //TODO log out of application
                                        }
                                    });
                                } catch (Exception e) {
                                    displayToastErrorServer(context);
                                    //TODO log out of application
                                }
                            } else {
                                displayToastErrorServer(context);
                                //TODO log out of application
                            }
                        } else {
                            displayToastErrorServer(context, response.message());
                            //TODO log out of application
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<ApiData> call, @NotNull Throwable t) {
                        displayToastErrorServer(context);
                    }
                });
    }

    public void login(String username, char[] password, GetAuthTokenCallbackServer callback) throws NoSuchAlgorithmException {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("username", username);
        map.put("password", new String(password));

        String dateHeader = DateFormatGMT.getCurrentTime();
        String payload = HttpSigning.generatePayload(map);
        String signatureHeader = HttpSigning.generateSignatureHeader(ENDPOINTS.LOGIN_METHOD, ENDPOINTS.LOGIN_ENDPOINT, payload, dateHeader);
        String digestHeader = HttpSigning.generateDigestHeader(HttpSigning.generateDigest(payload));

        Log.v("ServiceServer", dateHeader);
        Log.v("ServiceServer", payload);
        Log.v("ServiceServer", signatureHeader);
        Log.v("ServiceServer", digestHeader);

        serviceServerApi.login(digestHeader, dateHeader, signatureHeader, username, new String(password))
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<LoginResponse> call, @NotNull Response<LoginResponse> response) {
                        if (response.isSuccessful()) {
                            LoginResponse loginResponse = response.body();
                            if (loginResponse != null) {
                                callback.onSuccess(loginResponse);
                            } else {
                                callback.onError(new ErrorRequest());
                            }
                        } else {
                            String errorMessage = getErrorMessageFromResponse(response);
                            callback.onError(new ErrorRequest(errorMessage));
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<LoginResponse> call, @NotNull Throwable t) {
                        callback.onError(new ErrorRequest(t.getMessage()));
                    }
                });
    }

    public void confirmTransaction(String accessToken, String username, char[] passwordAllbank, NewTransaction newTransaction, CallbackGenericResponse<PaymentResponse> callback) throws NoSuchAlgorithmException {
        String myName = newTransaction.getMyName();
        String myIban = newTransaction.getMyIban();
        String myAccountId = newTransaction.getMyAccountId();
        String creditorIban = newTransaction.getCreditorIban();
        String creditorName = newTransaction.getCreditorName();
        String amount = newTransaction.getAmount() + "";
        String currency = newTransaction.getCurrency();
        String category = newTransaction.getCategory();
        String details = newTransaction.getDetails();
        String bank = newTransaction.getBank();

        Map<String, String> map = new LinkedHashMap<>();
        map.put("username", username);
        map.put("password", new String(passwordAllbank));
        map.put("myName", myName);
        map.put("myIban", myIban);
        map.put("myAccountId", myAccountId);
        map.put("creditorIban", creditorIban);
        map.put("creditorName", creditorName);
        map.put("amount", amount);
        map.put("currency", currency);
        map.put("category", category);
        map.put("details", details);
        map.put("bank", bank);

        String dateHeader = DateFormatGMT.getCurrentTime();
        String payload = HttpSigning.generatePayload(map);
        String signatureHeader = HttpSigning.generateSignatureHeader(ENDPOINTS.CONFIRM_TRANSACTION_METHOD, ENDPOINTS.CONFIRM_TRANSACTION_ENDPOINT, payload, dateHeader);
        String digestHeader = HttpSigning.generateDigestHeader(HttpSigning.generateDigest(payload));

        serviceServerApi.confirmTransaction(accessToken, digestHeader, dateHeader, signatureHeader, username, new String(passwordAllbank), myName, myIban, myAccountId, creditorIban, creditorName, amount, currency, category, details, bank)
                .enqueue(new Callback<PaymentResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<PaymentResponse> call, @NotNull Response<PaymentResponse> response) {
                        if (response.isSuccessful()) {
                            PaymentResponse paymentResponse = response.body();
                            callback.onSuccess(paymentResponse);
                        } else {
                            String errorMessage = getErrorMessageFromResponse(response);
                            callback.onError(new ErrorRequest(errorMessage));
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<PaymentResponse> call, @NotNull Throwable t) {
                        callback.onError(t);
                    }
                });
    }

    /**
     * @param response received from the server
     * @return the error message received from the server. If there is none, send the default message <code>ErrorRequest.ERROR_MSG</code>
     */
    public static <T> String getErrorMessageFromResponse(Response<T> response) {
        try {
            JSONObject jObjError = new JSONObject(response.errorBody().string());
            return jObjError.getString("message");
        } catch (JSONException | IOException e) {
            return ErrorRequest.ERROR_MSG;
        }
    }

    public void newBudget(String accessToken, Budget budget, CallbackGenericResponse<BudgetAddedResponse> callback) throws NoSuchAlgorithmException {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("category", budget.getCategory());
        map.put("startDate", budget.getStartDate() + "");
        map.put("endDate", budget.getEndDate() + "");
        map.put("budget", budget.getBudget() + "");

        String dateHeader = DateFormatGMT.getCurrentTime();
        String payload = HttpSigning.generatePayload(map);
        String signatureHeader = HttpSigning.generateSignatureHeader(ENDPOINTS.NEW_BUDGET_METHOD, ENDPOINTS.NEW_BUDGET_ENDPOINT, payload, dateHeader);
        String digestHeader = HttpSigning.generateDigestHeader(HttpSigning.generateDigest(payload));

        serviceServerApi.newBudget(accessToken, digestHeader, dateHeader, signatureHeader, budget.getCategory(), budget.getStartDate() + "", budget.getEndDate() + "", budget.getBudget() + "")
                .enqueue(new Callback<BudgetAddedResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<BudgetAddedResponse> call, @NotNull Response<BudgetAddedResponse> response) {
                        if (response.isSuccessful()) {
                            BudgetAddedResponse budgetAddedResponse = response.body();
                            if (budgetAddedResponse != null) {
                                callback.onSuccess(budgetAddedResponse);
                            } else {
                                callback.onError(new ErrorRequest());
                            }
                        } else {
                            String errorMessage = getErrorMessageFromResponse(response);
                            callback.onError(new ErrorRequest(errorMessage));
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<BudgetAddedResponse> call, @NotNull Throwable t) {
                        callback.onError(t);
                    }
                });
    }

    public void editBudget(String accessToken, Budget budget, CallbackNoContent callback) throws NoSuchAlgorithmException {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("budgetId", budget.getBudgetId() + "");
        map.put("category", budget.getCategory());
        map.put("startDate", budget.getStartDate() + "");
        map.put("endDate", budget.getEndDate() + "");
        map.put("budget", budget.getBudget() + "");
        map.put("spent", budget.getSpent() + "");

        String dateHeader = DateFormatGMT.getCurrentTime();
        String payload = HttpSigning.generatePayload(map);
        String signatureHeader = HttpSigning.generateSignatureHeader(ENDPOINTS.EDIT_BUDGET_METHOD, ENDPOINTS.EDIT_BUDGET_ENDPOINT, payload, dateHeader);
        String digestHeader = HttpSigning.generateDigestHeader(HttpSigning.generateDigest(payload));

        serviceServerApi.editBudget(accessToken, digestHeader, dateHeader, signatureHeader, budget.getBudgetId() + "", budget.getCategory(), budget.getStartDate() + "", budget.getEndDate() + "", budget.getBudget() + "", budget.getSpent() + "")
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            callback.onSuccess();
                        } else {
                            String errorMessage = getErrorMessageFromResponse(response);
                            callback.onError(new ErrorRequest(errorMessage));
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                        callback.onError(t);
                    }
                });
    }

    public void getAccountsData(String token, GetAccountsDataCallbackServer callback) throws NoSuchAlgorithmException {
        String authorizationHeader = Encrypt.buildBearerTokenHeader(token);

        String dateHeader = DateFormatGMT.getCurrentTime();
        String payload = HttpSigning.generatePayload(new HashMap<>());
        String signatureHeader = HttpSigning.generateSignatureHeader(ENDPOINTS.ACCOUNTS_DATA_METHOD, ENDPOINTS.ACCOUNTS_DATA_ENDPOINT, payload, dateHeader);
        String digestHeader = HttpSigning.generateDigestHeader(HttpSigning.generateDigest(payload));

        serviceServerApi.getAccountsData(authorizationHeader, digestHeader, dateHeader, signatureHeader)
                .enqueue(new Callback<AccountsResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<AccountsResponse> call, @NotNull Response<AccountsResponse> response) {
                        if (response.isSuccessful()) {
                            AccountsResponse accountsResponse = response.body();
                            if (accountsResponse != null) {
                                callback.onSuccess(accountsResponse);
                            } else {
                                callback.onError(new ErrorRequest());
                            }
                        } else {
                            String errorMessage = getErrorMessageFromResponse(response);
                            callback.onError(new ErrorRequest(errorMessage));
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<AccountsResponse> call, @NotNull Throwable t) {
                        callback.onError(new ErrorRequest(t.getMessage()));
                    }
                });
    }

    public void registerUser(String username, char[] password, String firstname, String lastname, String phone, String email, RegisterUserCallbackServer callback) throws NoSuchAlgorithmException {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("username", username);
        map.put("password", new String(password));
        map.put("firstname", firstname);
        map.put("lastname", lastname);
        map.put("phone", phone);
        map.put("email", email);

        String dateHeader = DateFormatGMT.getCurrentTime();
        String payload = HttpSigning.generatePayload(map);
        String signatureHeader = HttpSigning.generateSignatureHeader(ENDPOINTS.REGISTER_USER_METHOD, ENDPOINTS.REGISTER_USER_ENDPOINT, payload, dateHeader);
        String digestHeader = HttpSigning.generateDigestHeader(HttpSigning.generateDigest(payload));

        serviceServerApi.registerUser(username, digestHeader, dateHeader, signatureHeader, new String(password), firstname, lastname, phone, email)
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<LoginResponse> call, @NotNull Response<LoginResponse> response) {
                        if (response.isSuccessful()) {
                            LoginResponse loginResponse = response.body();
                            if (loginResponse != null) {
                                callback.onSuccess(loginResponse);
                            } else {
                                callback.onError(new ErrorRequest());
                            }
                        } else {
                            String errorMessage = getErrorMessageFromResponse(response);
                            callback.onError(new ErrorRequest(errorMessage));
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<LoginResponse> call, @NotNull Throwable t) {
                        callback.onError(new ErrorRequest(t.getMessage()));
                    }
                });
    }

    public void editProfile(String accessToken, String email, String phone, String lastName, String firstName, char[] currentPassword, char[] newPassword, CallbackNoContent callback) throws NoSuchAlgorithmException {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("email", email);
        map.put("phone", phone);
        map.put("firstName", firstName);
        map.put("lastName", lastName);
        map.put("currentPassword", new String(currentPassword));
        map.put("newPassword", new String(newPassword));

        String dateHeader = DateFormatGMT.getCurrentTime();
        String payload = HttpSigning.generatePayload(map);
        String signatureHeader = HttpSigning.generateSignatureHeader(ENDPOINTS.EDIT_PROFILE_METHOD, ENDPOINTS.EDIT_PROFILE_ENDPOINT, payload, dateHeader);
        String digestHeader = HttpSigning.generateDigestHeader(HttpSigning.generateDigest(payload));

        serviceServerApi.editProfile(accessToken, digestHeader, dateHeader, signatureHeader, email, phone, firstName, lastName, new String(currentPassword), new String(newPassword))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            callback.onSuccess();
                        } else {
                            String errorMessage = getErrorMessageFromResponse(response);
                            callback.onError(new ErrorRequest(errorMessage));
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                        callback.onError(t);
                    }
                });
    }

    /**
     * Function that adds the received data response from the server and processes it in order to add it to the database
     *
     * @param accountsResponse Response object received from the server
     * @param viewModel        ViewModel object used for inserting data into database
     * @param fragment         Fragment object used for ViewModel querying
     * @param modifyBudgets    Whether to update Budgets database. Should be set to true only at startup when the server send all data
     */
    private static void addAccountsDataInDatabase(AccountsResponse accountsResponse, HomeViewModel viewModel, Fragment fragment, boolean modifyBudgets) {
        /* For each account, insert it into the database along with its transactions */
        List<AccountDetailsBcr> accountDetailsBcrList = accountsResponse.getAccountDetailsBcrList();
        List<AccountDetailsBt> accountDetailsBtList = accountsResponse.getAccountDetailsBtList();
        List<BudgetResponse> budgetListResponse = accountsResponse.getBudgets();

        List<Account> accountList = new ArrayList<>();
        List<Transaction> transactionList = new ArrayList<>();
        List<Budget> budgetList = new ArrayList<>();

        if (modifyBudgets) {
            if (budgetListResponse != null) {
                for (BudgetResponse budgetResponse : budgetListResponse) {
                    Budget budget = new Budget(budgetResponse.getBudgetId(), convertStringToDate(budgetResponse.getStartDate()), convertStringToDate(budgetResponse.getEndDate()), budgetResponse.getBudget(), budgetResponse.getCategory());
                    budgetList.add(budget);

                    DateTime startDate = budget.getStartDate();
                    DateTime endDate = budget.getEndDate();
                    String category = budget.getCategory();

                    viewModel.getTransactionsSumForCategory(startDate, endDate, category).observe(fragment.getViewLifecycleOwner(), spent -> {
                        if (spent == null) {
                            budget.setSpent(0f);
                        } else {
                            budget.setSpent(spent);
                        }
                        viewModel.update(budget);
                    });
                }
            }
        }

        if (accountDetailsBcrList != null) {
            for (AccountDetailsBcr accountBcr : accountDetailsBcrList) {
                String iban = accountBcr.getIban();
                String name = accountBcr.getName();
                String accountId = accountBcr.getResourceId();
                String details = accountBcr.getDetails();
                String currency = accountBcr.getCurrency();
                float balance = accountBcr.getBalance();
                TransactionsObjectBcr transactionsObjectBcr = accountBcr.getTransactions();

                Account account = new Account(accountId, iban, ServiceBcr.BCR, name, details, currency, balance, true /* TODO CHANGE */);
                accountList.add(account);

                List<TransactionDetailsBcr> bookedTransactions = null;
                List<TransactionDetailsBcr> pendingTransactions = null;

                if (transactionsObjectBcr != null) {
                    bookedTransactions = transactionsObjectBcr.getBooked();
                    pendingTransactions = transactionsObjectBcr.getPending();
                }

                if (bookedTransactions != null) {
                    for (TransactionDetailsBcr transactionBcr : bookedTransactions) {
                        Transaction transaction = convertToDbTransactionFromBcrTransaction(transactionBcr, accountId, "BOOKED");
                        transactionList.add(transaction);
                    }
                }

                if (pendingTransactions != null) {
                    for (TransactionDetailsBcr transactionBcr : pendingTransactions) {
                        Transaction transaction = convertToDbTransactionFromBcrTransaction(transactionBcr, accountId, "PENDING");
                        transactionList.add(transaction);
                    }
                }
            }
        }

        if (accountDetailsBtList != null) {
            for (AccountDetailsBt accountBt : accountDetailsBtList) {
                String iban = accountBt.getIban();
                String name = accountBt.getName();
                String accountId = accountBt.getResourceId();
                String details = accountBt.getProduct();
                String currency = accountBt.getCurrency();
                float balance = accountBt.getBalance();
                TransactionsObjectBt transactionsObjectBt = accountBt.getTransactions();

                Account account = new Account(accountId, iban, ServiceBt.BT, name, details, currency, balance, true /* TODO CHANGE */);
                accountList.add(account);

                List<TransactionDetailsBt> bookedTransactions = null;
                List<TransactionDetailsBt> pendingTransactions = null;
                if (transactionsObjectBt != null) {
                    bookedTransactions = transactionsObjectBt.getBooked();
                    pendingTransactions = transactionsObjectBt.getPending();
                }

                if (bookedTransactions != null) {
                    for (TransactionDetailsBt transactionBt : bookedTransactions) {
                        Transaction transaction = convertToDbTransactionFromBtTransaction(transactionBt, accountId, "BOOKED");
                        transactionList.add(transaction);
                    }
                }
                if (pendingTransactions != null) {
                    for (TransactionDetailsBt transactionBt : pendingTransactions) {
                        Transaction transaction = convertToDbTransactionFromBtTransaction(transactionBt, accountId, "PENDING");
                        transactionList.add(transaction);
                    }
                }
            }
        }

        viewModel.insertAccountsBudgetsTransactions(accountList, transactionList, budgetList);
    }

    private static void addNewBcrAccountsDataInDatabase(AccountBcrAddedResponse accountBcrAddedResponse, HomeViewModel viewModel) {
        List<Account> accountList = new ArrayList<>();
        List<Transaction> transactionList = new ArrayList<>();

        List<AccountDetailsBcr> accountDetailsBcrList = accountBcrAddedResponse.getAccounts();

        if (accountDetailsBcrList != null) {
            for (AccountDetailsBcr accountBcr : accountDetailsBcrList) {
                String iban = accountBcr.getIban();
                String name = accountBcr.getName();
                String accountId = accountBcr.getResourceId();
                String details = accountBcr.getDetails();
                String currency = accountBcr.getCurrency();
                float balance = accountBcr.getBalance();
                TransactionsObjectBcr transactionsObjectBcr = accountBcr.getTransactions();

                Account account = new Account(accountId, iban, ServiceBcr.BCR, name, details, currency, balance, true /* TODO CHANGE */);
                accountList.add(account);

                List<TransactionDetailsBcr> bookedTransactions = null;
                List<TransactionDetailsBcr> pendingTransactions = null;

                if (transactionsObjectBcr != null) {
                    bookedTransactions = transactionsObjectBcr.getBooked();
                    pendingTransactions = transactionsObjectBcr.getPending();
                }

                if (bookedTransactions != null) {
                    for (TransactionDetailsBcr transactionBcr : bookedTransactions) {
                        Transaction transaction = convertToDbTransactionFromBcrTransaction(transactionBcr, accountId, "BOOKED");
                        transactionList.add(transaction);
                    }
                }

                if (pendingTransactions != null) {
                    for (TransactionDetailsBcr transactionBcr : pendingTransactions) {
                        Transaction transaction = convertToDbTransactionFromBcrTransaction(transactionBcr, accountId, "PENDING");
                        transactionList.add(transaction);
                    }
                }
            }
        }
        viewModel.insertAccountsBudgetsTransactions(accountList, transactionList, new ArrayList<>());
    }

    private static void addNewBtAccountsDataInDatabase(AccountBtAddedResponse accountBtAddedResponse, HomeViewModel viewModel) {
        List<Account> accountList = new ArrayList<>();
        List<Transaction> transactionList = new ArrayList<>();

        List<AccountDetailsBt> accountDetailsBtList = accountBtAddedResponse.getAccounts();

        if (accountDetailsBtList != null) {
            for (AccountDetailsBt accountBt : accountDetailsBtList) {
                String iban = accountBt.getIban();
                String name = accountBt.getName();
                String accountId = accountBt.getResourceId();
                String details = accountBt.getProduct();
                String currency = accountBt.getCurrency();
                float balance = accountBt.getBalance();
                TransactionsObjectBt transactionsObjectBt = accountBt.getTransactions();

                Account account = new Account(accountId, iban, ServiceBt.BT, name, details, currency, balance, true /* TODO CHANGE */);
                accountList.add(account);

                List<TransactionDetailsBt> bookedTransactions = null;
                List<TransactionDetailsBt> pendingTransactions = null;

                if (transactionsObjectBt != null) {
                    bookedTransactions = transactionsObjectBt.getBooked();
                    pendingTransactions = transactionsObjectBt.getPending();
                }

                if (bookedTransactions != null) {
                    for (TransactionDetailsBt transactionBt : bookedTransactions) {
                        Transaction transaction = convertToDbTransactionFromBtTransaction(transactionBt, accountId, "BOOKED");
                        transactionList.add(transaction);
                    }
                }

                if (pendingTransactions != null) {
                    for (TransactionDetailsBt transactionBt : pendingTransactions) {
                        Transaction transaction = convertToDbTransactionFromBtTransaction(transactionBt, accountId, "PENDING");
                        transactionList.add(transaction);
                    }
                }
            }
        }
        viewModel.insertAccountsBudgetsTransactions(accountList, transactionList, new ArrayList<>());
    }

    /**
     * Function that adds the transaction category to server DB
     *
     * @param token         access token
     * @param transactionId new transaction's id
     * @param category      category of the transaction
     * @param callback      method callback
     * @throws NoSuchAlgorithmException
     */
    public void addTransactionCategoryToDb(String token, String transactionId, String category, CallbackNoContent callback) throws NoSuchAlgorithmException {
        String authorizationHeader = Encrypt.buildBearerTokenHeader(token);
        Map<String, String> map = new LinkedHashMap<>();
        map.put("transactionId", transactionId);
        map.put("category", category);

        String dateHeader = DateFormatGMT.getCurrentTime();
        String payload = HttpSigning.generatePayload(map);
        String signatureHeader = HttpSigning.generateSignatureHeader(ENDPOINTS.ACCOUNTS_DATA_METHOD, ENDPOINTS.ADD_TRANSACTION_CATEGORY_TO_DB_METHOD, payload, dateHeader);
        String digestHeader = HttpSigning.generateDigestHeader(HttpSigning.generateDigest(payload));


        serviceServerApi.addTransactionCategoryToDb(authorizationHeader, digestHeader, dateHeader, signatureHeader, transactionId, category)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            callback.onSuccess();
                        } else {
                            callback.onError(new ErrorRequest());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                        callback.onError(new ErrorRequest(t.getMessage()));
                    }
                });
    }

    public static char[] getKeystoreSecretServer() {
        return KEYSTORE_SECRET_SERVER;
    }

    public static void setKeystoreSecretServer(char[] keystoreSecretServer) {
        KEYSTORE_SECRET_SERVER = keystoreSecretServer;
    }

    public static String getAccessToken() {
        return new String(ACCESS_TOKEN);
    }

    public static void setAccessToken(String accessToken) {
        ACCESS_TOKEN = accessToken.toCharArray();
    }

    public static void displayDialogProgressBar(Dialog dialog) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    public static void closeDialogProgressBar(Dialog dialog) {
        if (dialog != null)
            dialog.cancel();
    }

    private void setClientData(ApiData apiData) {
        ServiceServer.setKeystoreSecretServer(apiData.getKeyStoreSecretServer().toCharArray());
        ServiceBt.setIdConsentAccounts(apiData.getConsentIdBt());
        ServiceBcr.setBcrClientId(apiData.getBcrClientId());
    }

    public static void displayToastErrorServer(Context context) {
        Toast.makeText(context, ERROR_MESSAGE_SERVER, Toast.LENGTH_LONG).show();
    }

    public static void displayToastErrorServer(Context context, String errorMessage) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
    }

    /**
     * Function that converts a BCR transaction into a database Transaction object
     *
     * @param transactionBcr Transaction that is being processed and added in database
     * @param accountId      Transaction account id where transaction belongs to
     * @param status         Transaction status (BOOKED or PENDING)
     * @return Database Transaction object
     */
    private static Transaction convertToDbTransactionFromBcrTransaction(TransactionDetailsBcr transactionBcr, String accountId, String status) {
        String transactionId = transactionBcr.getId();
        String bookingDate = transactionBcr.getBookingDate();
        String valueDate = transactionBcr.getValueDate();
        String creditorName = transactionBcr.getCreditorName();
        String debtorName = transactionBcr.getDebtorName();
        TransactionAmountBcr transactionAmount = transactionBcr.getTransactionAmount();
        String creditor = transactionBcr.getCreditorAccount().getIban();
        String debtor = transactionBcr.getDebtorAccount().getIban();
        String details = transactionBcr.getDetails();
        String category = transactionBcr.getCategory();
        boolean enabled = true;

        /* Check the type of the payment */
        String transactionType;
        if (transactionAmount.getAmount() < 0) {
            transactionType = EXPENSES_TRANSACTIONS;
            /* If the transaction was not categorized, then we put it in "Others" category. This
             * is done only for expenses transactions */
            if (category == null) {
                category = StatisticsCategoryFragment.CATEGORY_OTHERS;
            }
        } else {
            transactionType = INCOME_TRANSACTIONS;
        }
        return new Transaction(transactionId, convertStringToDate(bookingDate), convertStringToDate(valueDate), transactionAmount, debtor, creditor, debtorName, creditorName, accountId, status, transactionType, category, details, ServiceBcr.BCR, enabled);

    }

    /**
     * Function that converts a BT transaction into a database Transaction object
     *
     * @param transactionBt Transaction that is being processed and added in database
     * @param accountId     Transaction account id where transaction belongs to
     * @param status        Transaction status (BOOKED or PENDING)
     * @return Database Transaction object
     */
    private static Transaction convertToDbTransactionFromBtTransaction(TransactionDetailsBt transactionBt, String accountId, String status) {
        String transactionId = transactionBt.getTransactionId();
        String bookingDate = transactionBt.getBookingDate();
        String valueDate = transactionBt.getValueDate();
        String transactionDetails = transactionBt.getDetails();
        String creditorIban = transactionBt.getCreditorAccount().getIban();
        String creditorName = transactionBt.getCreditorName();
        TransactionAmountBt transactionAmountBt = transactionBt.getTransactionAmount();
        TransactionAmountBcr transactionAmount = new TransactionAmountBcr(transactionAmountBt.getCurrency(), (float) transactionAmountBt.getAmount() / (float) 10f);
        String category = transactionBt.getCategory();
        boolean enabled = true;

        /* Check the type of the payment */
        String transactionType;
        if (transactionAmount.getAmount() < 0) {
            transactionType = EXPENSES_TRANSACTIONS;
            /* If the transaction was not categorized, then we put it in "Others" category. This
             * is done only for expenses transactions */
            if (category == null) {
                category = StatisticsCategoryFragment.CATEGORY_OTHERS;
            }
        } else {
            transactionType = INCOME_TRANSACTIONS;
        }
        return new Transaction(transactionId, convertStringToDate(bookingDate), convertStringToDate(valueDate), transactionAmount, "", creditorIban, "", creditorName, accountId, status, transactionType, category, transactionDetails, ServiceBt.BT, enabled);
    }

    /**
     * Function that stores user's data
     *
     * @param userData
     */
    public static void setUserData(UserDataResponse userData) {
        UserData.setEmail(userData.getEmail());
        UserData.setFirstName(userData.getFirstName());
        UserData.setLastName(userData.getLastName());
        UserData.setPhone(userData.getPhone());
        UserData.setUsername(userData.getUsername());
    }
}