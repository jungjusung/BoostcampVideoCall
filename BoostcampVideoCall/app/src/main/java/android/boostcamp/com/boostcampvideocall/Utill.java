package android.boostcamp.com.boostcampvideocall;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * Created by Jusung on 2017. 2. 15..
 */

public class Utill {

    public static InputFilter filterNum = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[0-9]*$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };
    public static InputFilter filterAlphaKor = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            Pattern ps = Pattern.compile("^[ㄱ-ㅣ가-힣a-zA-Z0-9_\\s\\.\\?\\!\\-\\,|\u318D\u119E\u11A2\u2022\u2025a\u00B7\uFE55]*$");

            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    public static void registMemberInfo(String name, String phoneNumber, String token,String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);

            BufferedWriter buffw=new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
            buffw.write("name="+name+"&token="+token+"&phoneNumber="+phoneNumber);
            buffw.flush();

            con.getResponseCode();
            con.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void requestFCM(String name, String token, String phoneNumber,String urlString,String channelId) {
        BufferedWriter buffw=null;
        try{
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);

            buffw=new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
            buffw.write("name="+name+"&token="+token+"&phoneNumber="+phoneNumber+"&channelId="+channelId);
            buffw.flush();
            con.getResponseCode();
            con.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (buffw!=null){
                try {
                    buffw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
