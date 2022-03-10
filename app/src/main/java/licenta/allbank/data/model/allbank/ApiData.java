package licenta.allbank.data.model.allbank;

import com.google.gson.annotations.SerializedName;
//TODO see what you can delete here
public class ApiData {
    @SerializedName("bcr_client_id")
    private String bcrClientId;
    @SerializedName("keystore_secret_bcr")
    private String keyStoreSecretBcr;
    @SerializedName("keystore_secret_ing")
    private String keyStoreSecretIng;
    @SerializedName("keystore_secret_server")
    private String keyStoreSecretServer;
    @SerializedName("consentId")
    private String consentIdBt;

    public String getConsentIdBt() {
        return consentIdBt;
    }

    public String getBcrClientId() {
        return bcrClientId;
    }

    public String getKeyStoreSecretBcr() {
        return keyStoreSecretBcr;
    }

    public String getKeyStoreSecretIng() {
        return keyStoreSecretIng;
    }

    public String getKeyStoreSecretServer() {
        return keyStoreSecretServer;
    }
}
