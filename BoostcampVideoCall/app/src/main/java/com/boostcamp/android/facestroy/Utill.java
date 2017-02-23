package com.boostcamp.android.facestroy;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;

import com.boostcamp.android.facestroy.db.Member;
import com.boostcamp.android.facestroy.db.MemberService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
        BufferedWriter buffw=null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);

            buffw=new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
            buffw.write("name="+name+"&token="+token+"&phoneNumber="+phoneNumber);
            buffw.flush();
            String TAG="Utill";
            Log.d(TAG,"성공");
            con.getResponseCode();
            con.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(buffw!=null){
                try {
                    buffw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void updateMemberInfo(String token,String urlString,String status,String imgUrl) {
        BufferedWriter buffw=null;
        try {
            String TAG="Utill";
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            Log.d(TAG,"url?"+imgUrl);
            buffw=new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
            buffw.write("token="+token.trim()+"&status="+status+"&url="+imgUrl);
            buffw.flush();
            con.getResponseCode();
            con.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(buffw!=null){
                try {
                    buffw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static String timeToString(long time){

        String TAG="Utill";
        long cTime=time/1000;
        long hours=0;
        long minutes=0;
        long seconds=0;
        String sHours;
        String sMinutes;
        String sSeconds;
        minutes=cTime/60;
        seconds=cTime%60;
        hours=minutes/60;
        minutes=minutes%60;

        if(hours<10)
            sHours="0"+hours;
        else
            sHours=hours+"";

        if(minutes<10)
            sMinutes="0"+minutes;
        else
            sMinutes=minutes+"";

        if(seconds<10)
            sSeconds="0"+seconds;
        else
            sSeconds=seconds+"";
        return sHours+":"+sMinutes+":"+sSeconds;
    }

    public static Member getMember(String token, String urlString) {
        BufferedReader buffr = null;
        BufferedWriter buffw = null;
        String TAG="Utill";
        try {

            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();


            buffw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));

            buffw.write("token=" + token);
            buffw.flush();


            buffr = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = buffr.readLine()) != null) {
                response.append(inputLine);
            }
            Log.d(TAG,response.toString());
            JSONObject json = new JSONObject(response.toString());
            Log.d(TAG,response.toString());
            Member member=new Member();
            member.setName(json.getString("name"));
            member.setPhoneNumber(json.getString("phoneNumber"));
            member.setToken(json.getString("token"));
            member.setTime(json.getLong("time"));
            member.setCount(json.getInt("count"));
            member.setStatus(json.getString("status"));
            member.setUrl(json.getString("url"));


            con.getResponseCode();
            con.disconnect();

            return member;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (buffw != null) {
                try {
                    buffw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
