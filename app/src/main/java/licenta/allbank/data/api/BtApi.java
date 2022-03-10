package licenta.allbank.data.api;

import licenta.allbank.data.model.bcr.TokenBcr;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface BtApi {
    @POST("oauth/token")
    Call<TokenBcr> getAccessTokenBt(
            @Query("client_id") String clientId,
            @Query("code") String code,
            @Query("grant_type") String grantType,
            @Query("redirect_uri") String redirectUri,
            @Query("client_secret") String clientSecret
    );
}
