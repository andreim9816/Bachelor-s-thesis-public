package licenta.allbank.utils.truststore;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import licenta.allbank.R;

public class TrustCertificateServer {
    private final char[] keyStorePassword;
    private final Context context;
    private TrustManager[] trustManagers;

    public TrustCertificateServer(char[] keyStorePassword, Context context) throws Exception {
        if (keyStorePassword.length == 0 || context == null) {
            throw new Exception("KeyStore is empty or context was not provided!");
        }
        this.keyStorePassword = keyStorePassword;
        this.context = context;
    }

    public SSLContext clientSSLContext() throws Exception {
        try {
            TrustManagerFactory trustManagerFactory = getTrustManagerFactory(context);
            KeyManagerFactory keyManagerFactory = getKeyManagerFactory(keyStorePassword);

            return getSSLContext(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers());
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | CertificateException | KeyStoreException | IOException | KeyManagementException e) {
            throw new Exception(e);
        }
    }

    private SSLContext getSSLContext(KeyManager[] keyManagers, TrustManager[] trustManagers) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(keyManagers, trustManagers, null);
        this.trustManagers = trustManagers;
        return sslContext;
    }

    private KeyManagerFactory getKeyManagerFactory(char[] keystorePassword) throws Exception {
        KeyStore keyStore = loadKeyStore(keystorePassword);
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, keystorePassword);
        return keyManagerFactory;
    }

    private TrustManagerFactory getTrustManagerFactory(Context context) throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        InputStream caInput1 = context.getResources().openRawResource(R.raw.server_cert);

        Certificate ca1 = null;
        try {
            ca1 = cf.generateCertificate(caInput1);
        } catch (CertificateException e) {
            e.printStackTrace();
        } finally {
            caInput1.close();

        }

        String keyStoreType = KeyStore.getDefaultType();
        KeyStore trustStore = KeyStore.getInstance(keyStoreType);
        trustStore.load(null, null);
        trustStore.setCertificateEntry("ca1", ca1);


        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm);
        trustManagerFactory.init(trustStore);

        trustManagerFactory.init(trustStore);
        return trustManagerFactory;
    }

    private KeyStore loadKeyStore(char[] keystorePassword) throws Exception {
        InputStream keystoreInputStream = context.getResources().openRawResource(R.raw.keystore_client);
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        if (keystorePassword == null) {
            keystore.load(keystoreInputStream, null);
        } else {
            keystore.load(keystoreInputStream, keystorePassword);
        }
        return keystore;
    }

    public TrustManager[] getTrustManagers() {
        return trustManagers;
    }
}