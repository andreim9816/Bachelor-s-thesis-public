package licenta.allbank.data.api;

import licenta.allbank.data.model.bcr.TokenBcr;
import licenta.allbank.data.model.bcr.accounts.AccountsResponseBcr;
import licenta.allbank.data.model.bcr.transactions.TransactionsResponseBcr;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BcrApi {
    @Headers("Content-Type: application/json")
    @GET("aisp/v1/accounts/{accountId}/transactions")
    Call<TransactionsResponseBcr> getTransactionsBcr(
            @Path("accountId") String accountId,
            @Header("Authorization") String token,
            @Header("web-api-key") String webApiKey,
            @Header("x-request-id") String xRequestId
    );

    @Headers("Content-Type: application/json")
    @GET("aisp/v1/accounts")
    Call<AccountsResponseBcr> getAccounts(
            @Header("Authorization") String token,
            @Header("web-api-key") String webApiKey,
            @Header("x-request-id") String xRequestId
    );
}
