package com.boostcamp.android.facestroy;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.boostcamp.android.facestroy.db.MyInfo;
import com.boostcamp.android.facestroy.utill.Utill;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Jusung on 2017. 2. 23..
 */

public class ModMyInfoActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG="ModMyInfoActivity";
    private static final int REQUEST_CODE_GALLERY = 1;
    private Uri mProfileUri;
    private CircleImageView mCircleGallery, mProfileImage;
    private Realm mRealm;
    private EditText mName,mStatus;
    private String mImagePath;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.myNoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        mRealm=Realm.getDefaultInstance();
        mProfileImage = (CircleImageView)findViewById(R.id.profile_image);
        MyInfo info = mRealm.where(MyInfo.class).findFirst();

        Glide.with(getApplicationContext())
                .load(info.getUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.sample)
                .into(mProfileImage);

        mCircleGallery = (CircleImageView)findViewById(R.id.cv_gallery);
        mName = (EditText)findViewById(R.id.et_name);
        mStatus = (EditText)findViewById(R.id.et_status);

        mCircleGallery.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.cv_gallery) {
            getGallery();
        }

    }
    public void getGallery() {
        Intent intent = null;
        if (Build.VERSION.SDK_INT >= 19) {
            intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        }
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;

        if (requestCode == REQUEST_CODE_GALLERY) {
            mProfileUri = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(mProfileUri, filePathColumn, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            mImagePath = cursor.getString(columnIndex);
            // Set the Image in ImageView for Previewing the Media
            mProfileImage.setImageBitmap(BitmapFactory.decodeFile(mImagePath));
            cursor.close();
            Log.d(TAG, mProfileUri.toString());

            String url = "http://1-dot-boostcamp-jusung.appspot.com/upload";
            new UploadImageAsync().execute(url);
        }
    }

    public String getUrlFromServer(String sUrl) {
        BufferedReader buffr = null;
        String uploadUrl = null;
        try {
            URL url = new URL(sUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            int responseCode = con.getResponseCode();
            Log.d(TAG, "응답코드" + responseCode);
            if (responseCode == 200) { // 정상 호출
                buffr = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                buffr = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = buffr.readLine()) != null) {
                response.append(inputLine);
            }

            try {
                JSONObject json = new JSONObject(response.toString());
                Log.d(TAG, response.toString());
                uploadUrl = json.getString("url");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uploadUrl;
    }

    public String uploadImageToServer(String uploadUrl) {
        String imgUrl = null;
        File file = new File(mImagePath);
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("myFile", file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file))
                .build();
        Request request = new Request.Builder()
                .url(uploadUrl)
                .post(body)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            JSONObject json = new JSONObject(response.body().string());
            imgUrl = json.getString("url");


        } catch (IOException e) {
            Log.d(TAG, "Exception" + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imgUrl;
    }


    private class UploadImageAsync extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... datas) {
            String uploadUrl = getUrlFromServer(datas[0]);
            String str = uploadImageToServer(uploadUrl);

            String token = FirebaseInstanceId.getInstance().getToken();
            Log.d(TAG, token);
            Log.d(TAG, str);
            Utill.updateMemberInfo(token.trim(), "http://1-dot-boostcamp-jusung.appspot.com/update", "", str);
            return str;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mRealm.beginTransaction();
            MyInfo info = mRealm.where(MyInfo.class).findFirst();
            info.setUrl(s);
            mRealm.copyToRealmOrUpdate(info);
            mRealm.commitTransaction();
            Glide.with(getApplicationContext()).load(info.getUrl()).diskCacheStrategy(DiskCacheStrategy.ALL).into(mProfileImage);
        }
    }

}
