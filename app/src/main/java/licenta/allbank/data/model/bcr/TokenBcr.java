package licenta.allbank.data.model.bcr;

import com.google.gson.annotations.SerializedName;

public class TokenBcr {
    @SerializedName("access_token")
    private String token;
    @SerializedName("token_type")
    private String tokenType;
    @SerializedName("expires_in")
    private long expiresIn;
    private String scope;
    @SerializedName("refresh_token")
    private String refreshToken;

    public TokenBcr(String token, String tokenType, long expiresIn, String scope, String refreshToken) {
        this.token = token;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.scope = scope;
        this.refreshToken = refreshToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
