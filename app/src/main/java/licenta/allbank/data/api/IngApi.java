package licenta.allbank.data.api;

import licenta.allbank.data.model.ing.AppTokenResponse;
import licenta.allbank.data.model.ing.AuthUrlResponse;
import licenta.allbank.data.model.ing.TokenIng;
import licenta.allbank.data.model.ing.accounts.AccountsResponseIng;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IngApi {
    @FormUrlEncoded
    @Headers({
            "TPP-Signature-Certificate: -----BEGIN CERTIFICATE-----MIIENjCCAx6gAwIBAgIEXkKZvjANBgkqhkiG9w0BAQsFADByMR8wHQYDVQQDDBZBcHBDZXJ0aWZpY2F0ZU1lYW5zQVBJMQwwCgYDVQQLDANJTkcxDDAKBgNVBAoMA0lORzESMBAGA1UEBwwJQW1zdGVyZGFtMRIwEAYDVQQIDAlBbXN0ZXJkYW0xCzAJBgNVBAYTAk5MMB4XDTIwMDIxMDEyMTAzOFoXDTIzMDIxMTEyMTAzOFowPjEdMBsGA1UECwwUc2FuZGJveF9laWRhc19xc2VhbGMxHTAbBgNVBGEMFFBTRE5MLVNCWC0xMjM0NTEyMzQ1MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkJltvbEo4/SFcvtGiRCar7Ah/aP0pY0bsAaCFwdgPikzFj+ij3TYgZLykz40EHODtG5Fz0iZD3fjBRRM/gsFPlUPSntgUEPiBG2VUMKbR6P/KQOzmNKF7zcOly0JVOyWcTTAi0VAl3MEO/nlSfrKVSROzdT4Aw/h2RVy5qlw66jmCTcp5H5kMiz6BGpG+K0dxqBTJP1WTYJhcEj6g0r0SYMnjKxBnztuhX5XylqoVdUy1a1ouMXU8IjWPDjEaM1TcPXczJFhakkAneoAyN6ztrII2xQ5mqmEQXV4BY/iQLT2grLYOvF2hlMg0kdtK3LXoPlbaAUmXCoO8VCfyWZvqwIDAQABo4IBBjCCAQIwNwYDVR0fBDAwLjAsoCqgKIYmaHR0cHM6Ly93d3cuaW5nLm5sL3Rlc3QvZWlkYXMvdGVzdC5jcmwwHwYDVR0jBBgwFoAUcEi7XgDA9Cb4xHTReNLETt+0clkwHQYDVR0OBBYEFLQI1Hig4yPUm6xIygThkbr60X8wMIGGBggrBgEFBQcBAwR6MHgwCgYGBACORgEBDAAwEwYGBACORgEGMAkGBwQAjkYBBgIwVQYGBACBmCcCMEswOTARBgcEAIGYJwEDDAZQU1BfQUkwEQYHBACBmCcBAQwGUFNQX0FTMBEGBwQAgZgnAQIMBlBTUF9QSQwGWC1XSU5HDAZOTC1YV0cwDQYJKoZIhvcNAQELBQADggEBAEW0Rq1KsLZooH27QfYQYy2MRpttoubtWFIyUV0Fc+RdIjtRyuS6Zx9j8kbEyEhXDi1CEVVeEfwDtwcw5Y3w6Prm9HULLh4yzgIKMcAsDB0ooNrmDwdsYcU/Oju23ym+6rWRcPkZE1on6QSkq8avBfrcxSBKrEbmodnJqUWeUv+oAKKG3W47U5hpcLSYKXVfBK1J2fnk1jxdE3mWeezoaTkGMQpBBARN0zMQGOTNPHKSsTYbLRCCGxcbf5oy8nHTfJpW4WO6rK8qcFTDOWzsW0sRxYviZFAJd8rRUCnxkZKQHIxeJXNQrrNrJrekLH3FbAm/LkyWk4Mw1w0TnQLAq+s=-----END CERTIFICATE-----"
    })
    @POST("oauth2/token")
    Call<AppTokenResponse> getAppTokenIng(
            @Header("Digest") String digest,
            @Header("Date") String date,
            @Header("Authorization") String authorization,
            @Field("grant_type") String grantType
    );

    @GET("/oauth2/authorization-server-url?scope=payment-accounts%3Abalances%3Aview%20payment-accounts%3Atransactions%3Aview&redirect_uri=https://callback")
    Call<AuthUrlResponse> getAuthUrlIng(
            @Header("Digest") String digest,
            @Header("Date") String date,
            @Header("Authorization") String authorization,
            @Header("Signature") String signature
    );

    @FormUrlEncoded
    @POST("/oauth2/token")
    Call<TokenIng> getAccessTokenIng(
            @Header("Digest") String digest,
            @Header("Date") String date,
            @Header("Authorization") String authorization,
            @Header("Signature") String signature,
            @Field("grant_type") String grantType,
            @Field("code") String authorizationCode
    );

    @FormUrlEncoded
    @POST("/oauth2/token")
    Call<TokenIng> getNewAccessToken(
            @Header("Date") String date,
            @Header("Digest") String digest,
            @Header("Authorization") String authorization,
            @Header("Signature") String signature,
            @Field("grant_type") String grantType,
            @Field("refresh_token") String refreshToken
    );

    @GET("/v3/accounts")
    Call<AccountsResponseIng> getAccounts(
            @Header("X-Request-ID") String requestId,
            @Header("Authorization") String authorization,
            @Header("Signature") String signature,
            @Header("Digest") String digest,
            @Header("Date") String date
    );
}