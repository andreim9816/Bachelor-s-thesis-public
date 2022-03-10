package licenta.allbank.data.model.allbank.auth;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("token")
    String accessToken;

    public String getAccessToken() {
        return accessToken;
    }
}
