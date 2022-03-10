package licenta.allbank.data.model.ing;

import com.google.gson.annotations.SerializedName;

public class TokenIng {
    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("refresh_token")
    private String refreshToken;
    private String scope;
    @SerializedName("expires_in")
    private String expiresIn;
    @SerializedName("refresh_token_expires_in")
    private String refreshTokenExpiresIn;

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getScope() {
        return scope;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public String getRefreshTokenExpiresIn() {
        return refreshTokenExpiresIn;
    }
}
