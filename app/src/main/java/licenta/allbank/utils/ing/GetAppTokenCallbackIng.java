package licenta.allbank.utils.ing;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import licenta.allbank.data.model.ing.AppTokenResponse;

public interface GetAppTokenCallbackIng {
    void onSuccess(AppTokenResponse appTokenResponse) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableEntryException, SignatureException, InvalidKeyException, IOException;

    void onError(Throwable t);
}
