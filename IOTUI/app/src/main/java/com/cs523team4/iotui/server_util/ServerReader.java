package com.cs523team4.iotui.server_util;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.util.Log;

import com.cs523team4.iotui.MainActivity;
import com.cs523team4.iotui.R;
import com.cs523team4.iotui.data_access.AppDatabase;
import com.cs523team4.iotui.data_model.AccessPermission;
import com.cs523team4.iotui.data_model.DataRequest;
import com.cs523team4.iotui.data_model.DataRequester;
import com.cs523team4.iotui.data_model.DataSource;
import com.cs523team4.iotui.data_model.Device;
import com.cs523team4.iotui.data_model.DeviceDataSummary;
import com.cs523team4.iotui.util.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import static com.cs523team4.iotui.data_access.AppDatabase.DB_NAME;

/**
 * Helper class to read data from the server and store it in our databases.
 * Created by aravind on 12/4/17.
 */

public class ServerReader {
    public static final String ACTION_ACCEPT = "accept";
    public static final String ACTION_DENY = "deny";

    private static final String NOTIFY_URL = "https://35.167.25.135/notify";
    private static final String ACTIONS_URL = "https://35.167.25.135/actions";

    @SuppressLint("SimpleDateFormat")
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("m/d/y");

    private static final HashMap<String, Integer> dMap = new HashMap<>();

    private static HttpsURLConnection getUrlConnection(Context c, String urlStr) throws IOException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException, CertificateException {
        Log.d("ServerReader", "Starting");
        // Load CAs from an InputStream
        // (could be from a resource or ByteArrayInputStream or ...)
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
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

        URL url = new URL(urlStr);
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

        return urlConnection;
    }

    public static void readServerData(Context c) throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException, JSONException, ParseException {
        HttpsURLConnection urlConnection = getUrlConnection(c, NOTIFY_URL);

        DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
        String jsonParamsString = new JSONObject()
                .put("username", PreferenceHelper.getString(c, R.string.pref_key_username, ""))
                .put("password", PreferenceHelper.getString(c, R.string.pref_key_password, ""))
                .toString();
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
        in.close();

        String data = total.toString();
        Log.d("ServerReader", data);
        parseAndStore(data, c);
    }

    public static void postDataRequestAction(Context c, DataRequest request, String action)
            throws KeyManagementException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        postAccessPolicyAction(c, request.requestId, action, "pendingDataRequest");
    }

    public static void postAccessPermissionAction(Context c, AccessPermission permission, String action)
            throws KeyManagementException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        postAccessPolicyAction(c, permission.accessPermissionId, action, "grantedDataRequest");
    }

    private static void postAccessPolicyAction(Context c, int policyId, String action, String policyType)
            throws KeyManagementException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        HttpsURLConnection urlConnection = getUrlConnection(c, ACTIONS_URL);
        DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
        String jsonParamsString = "";
        try {
            jsonParamsString = new JSONObject()
                    .put("username", PreferenceHelper.getString(c, R.string.pref_key_username, ""))
                    .put("password", PreferenceHelper.getString(c, R.string.pref_key_password, ""))
                    .put("request_id", policyId)
                    .put("action", action)
                    .put("type", policyType)
                    .toString();
        } catch (JSONException e) {
            // Should not happen.
            e.printStackTrace();
        }
        Log.d("json params", jsonParamsString);
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
        in.close();

        String data = total.toString();
        Log.d("ServerReader", data);
    }

    private static void parseAndStore(String data, Context context) throws JSONException, ParseException {
        dMap.clear();
        dMap.put("Camera", R.drawable.ic_menu_camera);
        dMap.put("GPS", R.drawable.ic_location_on_black_24dp);
        dMap.put("temperature", R.drawable.ic_ac_unit_black_24dp);
        AppDatabase db = AppDatabase.getInstance(context);
        db.appDao().deleteAll();
        JSONObject jObject = new JSONObject(data);

        // Insert data sources.
        JSONArray dataSources = jObject.getJSONArray("dataSource");
        for (int i = 0; i < dataSources.length(); i++) {
            JSONObject source = dataSources.getJSONObject(i);
            db.appDao().insertDataSource(new DataSource(source.getInt("srcID"), source.getString("name"), "ABCD"));
        }

        // Insert data requesters.
        JSONArray requesters = jObject.getJSONArray("requester");
        for (int i = 0; i < requesters.length(); i++) {
            JSONObject r = requesters.getJSONObject(i);
            db.appDao().insertDataRequester(new DataRequester(
                    r.getInt("ID"),
                    r.getString("publicKey"),
                    r.getString("name"),
                    R.drawable.ic_cloud
            ));
        }

        // Insert devices.
        JSONArray devices = jObject.getJSONArray("device");
        for (int i = 0; i < devices.length(); i++) {
            JSONObject device = devices.getJSONObject(i);
            db.appDao().insertDevice(new Device(
                    device.getInt("ID"),
                    device.getString("name"),
                    device.getString("type"),
                    device.getString("location"),
                    device.getInt("srcID"),
                    device.getString("dataSize"),
                    dMap.get(device.getString("type"))
            ));
        }

        // Insert device summaries.
        JSONArray summaries = jObject.getJSONArray("deviceSummary");
        for (int i = 0; i < summaries.length(); i++) {
            JSONObject s = summaries.getJSONObject(i);
            db.appDao().insertDeviceDataSummary(new DeviceDataSummary(
                    s.getInt("ID"),
                    s.getInt("deviceID"),
                    s.getString("accessDuration")
            ));
        }

        // Insert access permissions.
        JSONArray permissions = jObject.getJSONArray("grantedDataRequest");
        for (int i = 0; i < permissions.length(); i++) {
            JSONObject p = permissions.getJSONObject(i);
            db.appDao().insertAccessPermission(new AccessPermission(
                    p.getInt("ID"),
                    p.getInt("requesterID"),
                    p.getInt("deviceSummaryID"),
                    DATE_FORMAT.parse(p.getString("accessStartDate")),
                    DATE_FORMAT.parse(p.getString("accessEndDate"))
            ));
        }

        // Insert requests.
        JSONArray requests = jObject.getJSONArray("pendingDataRequest");
        for (int i = 0; i < requests.length(); i++) {
            JSONObject p = requests.getJSONObject(i);
            db.appDao().insertDataRequest(new DataRequest(
                    p.getInt("ID"),
                    p.getInt("requesterID"),
                    p.getInt("deviceSummaryID"),
                    DATE_FORMAT.parse(p.getString("accessStartDate")),
                    DATE_FORMAT.parse(p.getString("accessEndDate"))
            ));
        }

    }
}
