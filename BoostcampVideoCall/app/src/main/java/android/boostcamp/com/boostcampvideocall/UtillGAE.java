package android.boostcamp.com.boostcampvideocall;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Jusung on 2017. 2. 15..
 */

public class UtillGAE {

    public static void requestMemberGAE(String name, String token, String phoneNumber,String urlString) {
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
            con.getResponseCode();
            con.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
