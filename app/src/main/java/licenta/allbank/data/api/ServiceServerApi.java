package licenta.allbank.data.api;

import licenta.allbank.data.model.allbank.AccountsResponse;
import licenta.allbank.data.model.allbank.ApiData;
import licenta.allbank.data.model.allbank.BodyPatchToken;
import licenta.allbank.data.model.allbank.auth.LoginResponse;
import licenta.allbank.data.model.allbank.others.BudgetAddedResponse;
import licenta.allbank.data.model.allbank.others.PaymentResponse;
import licenta.allbank.data.model.bcr.TokenBcr;
import licenta.allbank.data.model.bcr.accounts.AccountBcrAddedResponse;
import licenta.allbank.data.model.bt.PaymentResponseBt;
import licenta.allbank.data.model.bt.TokenBt;
import licenta.allbank.data.model.bt.accounts.AccountBtAddedResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

import static licenta.allbank.utils.endpoints.ENDPOINTS.ACCOUNTS_DATA_ENDPOINT;
import static licenta.allbank.utils.endpoints.ENDPOINTS.ADD_BCR_ACCOUNT_ENDPOINT;
import static licenta.allbank.utils.endpoints.ENDPOINTS.ADD_BT_ACCOUNT_ENDPOINT;
import static licenta.allbank.utils.endpoints.ENDPOINTS.ADD_TRANSACTION_CATEGORY_TO_DB_ENDPOINT;
import static licenta.allbank.utils.endpoints.ENDPOINTS.BCR_ACCESS_TOKEN_ENDPOINT;
import static licenta.allbank.utils.endpoints.ENDPOINTS.BT_ACCESS_TOKEN_ENDPOINT;
import static licenta.allbank.utils.endpoints.ENDPOINTS.CONFIRM_TRANSACTION_ENDPOINT;
import static licenta.allbank.utils.endpoints.ENDPOINTS.EDIT_BUDGET_ENDPOINT;
import static licenta.allbank.utils.endpoints.ENDPOINTS.EDIT_PROFILE_ENDPOINT;
import static licenta.allbank.utils.endpoints.ENDPOINTS.LOGIN_ENDPOINT;
import static licenta.allbank.utils.endpoints.ENDPOINTS.NEW_BUDGET_ENDPOINT;
import static licenta.allbank.utils.endpoints.ENDPOINTS.REGISTER_USER_ENDPOINT;

public interface ServiceServerApi {
    @POST("init_startup")
    Call<ApiData> getApiData();

    //DONE
    @FormUrlEncoded
    @POST(BCR_ACCESS_TOKEN_ENDPOINT)
    Call<TokenBcr> getAccessTokenBcr(
            @Header("digest") String digest,
            @Header("date") String date,
            @Header("signature") String signature,
            @Field("code") String authorizationCode
    );

    //DONE
    @FormUrlEncoded
    @POST(BT_ACCESS_TOKEN_ENDPOINT)
    Call<TokenBt> getTokenBt(
            @Header("digest") String digest,
            @Header("date") String date,
            @Header("signature") String signature,
            @Field("code") String code
    );

    // SHOULD I DELETE THIS?
    @FormUrlEncoded
    @POST("bt_post_payment")
    Call<PaymentResponseBt> postPaymentBt(
            @Field("debtor_iban") String debtorIban,
            @Field("debtor_id") String debtorId,
            @Field("creditor_iban") String creditorIban,
            @Field("creditor_name") String creditorName,
            @Field("currency") String currency,
            @Field("amount") String amount,
            @Field("endToEndIdentification") String endToEndIdentification,
            @Field("details") String transactionDetails
    );

    // SHOULD I DELETE THIS?
    @PATCH("access_token/{accountId}")
    Call<ResponseBody> updateAccessToken(
            @Header("authorization") String token,
            @Path("accountId") String accountId,
            @Body BodyPatchToken body
    );

    // DONE
    @FormUrlEncoded
    @POST(LOGIN_ENDPOINT)
    Call<LoginResponse> login(
            @Header("digest") String digest,
            @Header("date") String date,
            @Header("signature") String signature,
            @Field("username") String username,
            @Field("password") String password
    );

    // DONE
    @FormUrlEncoded
    @POST(CONFIRM_TRANSACTION_ENDPOINT)
    Call<PaymentResponse> confirmTransaction(
            @Header("authorization") String token,
            @Header("digest") String digest,
            @Header("date") String date,
            @Header("signature") String signature,
            @Field("username") String username,
            @Field("password") String password,
            @Field("myName") String myName,
            @Field("myIban") String myIban,
            @Field("myAccountId") String myAccountId,
            @Field("creditorIban") String creditorIban,
            @Field("creditorName") String creditorName,
            @Field("amount") String amount,
            @Field("currency") String currency,
            @Field("category") String category,
            @Field("details") String details,
            @Field("bank") String bank
    );

    // DONE
    @GET(ACCOUNTS_DATA_ENDPOINT)
    Call<AccountsResponse> getAccountsData(
            @Header("authorization") String token,
            @Header("digest") String digest,
            @Header("date") String date,
            @Header("signature") String signature
    );

    // DONE
    @FormUrlEncoded
    @POST(REGISTER_USER_ENDPOINT)
    Call<LoginResponse> registerUser(
            @Header("digest") String digest,
            @Header("date") String date,
            @Header("signature") String signature,
            @Field("username") String username,
            @Field("password") String password,
            @Field("firstName") String firstName,
            @Field("lastName") String lastName,
            @Field("phone") String phone,
            @Field("email") String email
    );

    //DONE
    @FormUrlEncoded
    @POST(NEW_BUDGET_ENDPOINT)
    Call<BudgetAddedResponse> newBudget(
            @Header("authorization") String token,
            @Header("digest") String digest,
            @Header("date") String date,
            @Header("signature") String signature,
            @Field("category") String category,
            @Field("startDate") String startDate,
            @Field("endDate") String endDate,
            @Field("budget") String budget
    );

    //DONE
    @FormUrlEncoded
    @PATCH(EDIT_BUDGET_ENDPOINT)
    Call<ResponseBody> editBudget(
            @Header("authorization") String token,
            @Header("digest") String digest,
            @Header("date") String date,
            @Header("signature") String signature,
            @Field("budgetId") String budgetId,
            @Field("category") String category,
            @Field("startDate") String startDate,
            @Field("endDate") String endDate,
            @Field("budget") String budget,
            @Field("spent") String spent
    );

    //DONE
    @FormUrlEncoded
    @POST(ADD_BCR_ACCOUNT_ENDPOINT)
    Call<AccountBcrAddedResponse> addBankNewBcrAccount(
            @Header("authorization") String token,
            @Header("digest") String digest,
            @Header("date") String date,
            @Header("signature") String signature,
            @Field("accessToken") String accessToken,
            @Field("refreshToken") String refreshToken,
            @Field("consent") String consent
    );

    @FormUrlEncoded
    @POST(ADD_BT_ACCOUNT_ENDPOINT)
    Call<AccountBtAddedResponse> addBankNewBtAccount(
            @Header("authorization") String token,
            @Header("digest") String digest,
            @Header("date") String date,
            @Header("signature") String signature,
            @Field("accessToken") String accessToken,
            @Field("refreshToken") String refreshToken,
            @Field("consent") String consent
    );

    @FormUrlEncoded
    @POST(ADD_TRANSACTION_CATEGORY_TO_DB_ENDPOINT)
    Call<ResponseBody> addTransactionCategoryToDb(
            @Header("authorization") String token,
            @Header("digest") String digest,
            @Header("date") String date,
            @Header("signature") String signature,
            @Field("transactionId") String transactionId,
            @Field("category") String category
    );

    @FormUrlEncoded
    @PATCH(EDIT_PROFILE_ENDPOINT)
    Call<ResponseBody> editProfile(
            @Header("authorization") String token,
            @Header("digest") String digest,
            @Header("date") String date,
            @Header("signature") String signature,
            @Field("email") String email,
            @Field("phone") String phone,
            @Field("firstName") String firstName,
            @Field("lastName") String lastName,
            @Field("currentPassword") String currentPassword,
            @Field("newPassword") String newPassword
    );
}