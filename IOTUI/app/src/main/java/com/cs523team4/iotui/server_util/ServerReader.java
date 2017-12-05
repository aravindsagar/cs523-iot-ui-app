package com.cs523team4.iotui.server_util;

import android.content.Context;
import android.util.Log;

import com.cs523team4.iotui.MainActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by aravind on 12/4/17.
 */

public class ServerReader {


    public static void readServerData(Context c) throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        Log.d("ServerReader", "Starting");
        // Load CAs from an InputStream
        // (could be from a resource or ByteArrayInputStream or ...)
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        // My CRT file that I put in the assets folder
        // I got this file by following these steps:
        // * Go to https://littlesvr.ca using Firefox
        // * Click the padlock/More/Security/View Certificate/Details/Export
        // * Saved the file as littlesvr.crt (type X.509 Certificate (PEM))
        // The MainActivity.context is declared as:
        // public static Context context;
        // And initialized in MainActivity.onCreate() as:
        // MainActivity.context = getApplicationContext();
        InputStream caInput = new BufferedInputStream(c.getAssets().open("CS523_iot_server.crt"));
        Certificate ca = cf.generateCertificate(caInput);
        Log.d("ServerReader", "ca=" + ((X509Certificate) ca).getSubjectDN());

        // Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Create an SSLContext that uses our TrustManager
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, tmf.getTrustManagers(), null);

        URL url = new URL("https://35.167.25.135:5000/notify");
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setSSLSocketFactory(context.getSocketFactory());
        // We don't have a hostname, and directly use ip address of our server.
        // So disable hostname verification for now.
        urlConnection.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        // POST data to server.
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoOutput(true);

        DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
        String jsonParamsString = "{\"username\":\"Aravind\", \"password\":\"Sagar\"}";
        outputStream.writeBytes(jsonParamsString);
        outputStream.flush();
        outputStream.close();

        InputStream in = urlConnection.getInputStream();
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line).append('\n');
        }
        Log.d("ServerReader", total.toString());
    }
}
