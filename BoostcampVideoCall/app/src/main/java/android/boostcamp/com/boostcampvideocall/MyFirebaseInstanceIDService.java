package android.boostcamp.com.boostcampvideocall;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Jusung on 2017. 2. 8..
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
    }

    public static void registMemberInfo(String name, String token, String phoneNumber,String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);

            BufferedWriter buffw=new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
            buffw.write("name="+name+"&token="+token+"&phoneNumber="+phoneNumber);
            buffw.write("\n");
            buffw.flush();
            Log.d(TAG,"서버 갱신 성공!");
            Log.d(TAG,name);
            Log.d(TAG,token);
            Log.d(TAG,phoneNumber);
            Log.d(TAG,urlString);
            con.getResponseCode();
            con.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void requestFCM(String name, String token, String phoneNumber,String urlString,String channelId) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);

            BufferedWriter buffw=new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
            buffw.write("name="+name+"&token="+token+"&phoneNumber="+phoneNumber+"&channelId="+channelId);
            buffw.write("\n");
            buffw.flush();
            Log.d(TAG,"서버 갱신 성공!");
            Log.d(TAG,name);
            Log.d(TAG,token);
            Log.d(TAG,phoneNumber);
            Log.d(TAG,urlString);
            con.getResponseCode();
            con.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
