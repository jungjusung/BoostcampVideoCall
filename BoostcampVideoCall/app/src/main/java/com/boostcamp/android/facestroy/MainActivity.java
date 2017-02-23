package com.boostcamp.android.facestroy;

import com.boostcamp.android.facestroy.db.CallLog;
import com.boostcamp.android.facestroy.db.Member;
import com.boostcamp.android.facestroy.db.MyInfo;
import com.boostcamp.android.facestroy.db.calllog.CallLogAdapter;
import com.boostcamp.android.facestroy.db.memberinfo.MemberAdapter;
import com.bumptech.glide.Glide;
import com.google.firebase.iid.FirebaseInstanceId;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;
import devlight.io.library.ntb.NavigationTabBar;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements MemberAdapter.ListItemClickListener, CallLogAdapter.ListItemClickListener, View.OnClickListener {


    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_GALLERY = 1;
    private ViewPager mViewPager;
    private static final int VIEW_PAGER_SIZE = 3;

    private NavigationTabBar navigationTabBar;
    private Realm realm;
    private int[] icons = {R.drawable.ic_contacts, R.drawable.ic_video_call, R.drawable.ic_theaters};
    private String[] colors = {"#f9bb72", "#dd6495", "#72d3b4"};
    private String[] titles = {"연락처", "통화기록", "프로필"};


    //profile 변경관련
    private CircleImageView mCircleGallery, mProfileImage;
    private Uri mProfileUri;
    private String mImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.myNoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        realm = Realm.getDefaultInstance();
        initUI();
    }

    public void initUI() {
        mViewPager = (ViewPager) findViewById(R.id.vp_list);
        navigationTabBar = (NavigationTabBar) findViewById(R.id.nav_facestory);

        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return VIEW_PAGER_SIZE;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view.equals(object);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                ((ViewPager) container).removeView((View) object);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                final View viewMember = LayoutInflater.from(
                        getBaseContext()).inflate(R.layout.item_rv_list, null, false);

                final View viewCallLog = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_rv_log, null, false);

                final RecyclerView recyclerViewMember = (RecyclerView) viewMember.findViewById(R.id.rv_facestory);
                final RecyclerView recyclerCallLog = (RecyclerView) viewCallLog.findViewById(R.id.rv_call_log);

                final View status = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_status, null, false);

                recyclerViewMember.setHasFixedSize(true);
                recyclerViewMember.setLayoutManager(new LinearLayoutManager(
                        getBaseContext(), LinearLayoutManager.VERTICAL, false)
                );

                recyclerCallLog.setHasFixedSize(true);
                recyclerCallLog.setLayoutManager(new LinearLayoutManager(
                        getBaseContext(), LinearLayoutManager.VERTICAL, false)
                );

                if (position == 0) {
                    final RealmResults<Member> list = realm.where(Member.class).findAll();
                    for (Member call : list) {
                        Log.d(TAG, call.getTime() + "");
                        Log.d(TAG, call.getCount() + "");
                    }
                    recyclerViewMember.setAdapter(new MemberAdapter(getApplicationContext(), MainActivity.this, list, true));
                    container.addView(viewMember);
                    return viewMember;
                } else if (position == 1) {
                    RealmResults<CallLog> list = realm.where(CallLog.class).findAll();

                    recyclerCallLog.setAdapter(new CallLogAdapter(getApplicationContext(), MainActivity.this, list, true));
                    container.addView(viewCallLog);
                    return viewCallLog;
                } else {

                    mProfileImage = (CircleImageView) status.findViewById(R.id.profile_image);
                    Log.d(TAG, realm.where(MyInfo.class).findFirst().getUrl());
                    MyInfo info = realm.where(MyInfo.class).findFirst();
                    if (!info.getUrl().equals("")) {
                        Glide.with(getApplicationContext()).load(info.getUrl()).into(mProfileImage);
                    }

                    mCircleGallery = (CircleImageView) status.findViewById(R.id.cv_gallery);
                    EditText eName = (EditText) status.findViewById(R.id.et_name);
                    EditText eStatus = (EditText) status.findViewById(R.id.et_status);

                    mCircleGallery.setOnClickListener(MainActivity.this);
                    container.addView(status);
                    return status;
                }
            }
        });


        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        for (int i = 0; i < VIEW_PAGER_SIZE; i++) {

            if (android.os.Build.VERSION.SDK_INT >= 21) {
                models.add(new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(icons[i], null),
                        Color.parseColor(colors[i]))
                        .title(titles[i])
                        .build());
            } else {
                models.add(new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(icons[i]),
                        Color.parseColor(colors[i]))
                        .title(titles[i])
                        .build());
            }
        }
        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(mViewPager, 0);
        navigationTabBar.setBehaviorEnabled(true);
        navigationTabBar.setOnTabBarSelectedIndexListener(new NavigationTabBar.OnTabBarSelectedIndexListener() {
            @Override
            public void onStartTabSelected(final NavigationTabBar.Model model, final int index) {
            }

            @Override
            public void onEndTabSelected(final NavigationTabBar.Model model, final int index) {
                model.hideBadge();
            }
        });
        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(final int position) {

            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });

    }

    @Override
    public void onListItemClick(int clickedItemIndex) {

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
            realm.beginTransaction();
            MyInfo info = realm.where(MyInfo.class).findFirst();
            info.setUrl(s);
            realm.copyToRealmOrUpdate(info);
            realm.commitTransaction();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        final RealmResults<Member> list = realm.where(Member.class).findAll();
//        try {
//            new AsyncTask<List, Void, List>() {
//                @Override
//                protected List doInBackground(List... data) {
//                    List<Member> mList = new LinkedList<Member>();
//                    for (Object m : data[0]) {
//                        mList.add(Utill.getMember(((Member) m).getToken(), "http://1-dot-boostcamp-jusung.appspot.com/boostcampSelectOne"));
//                    }
//                    return mList;
//                }
//
//                @Override
//                protected void onPostExecute(List mList) {
//                    super.onPostExecute(mList);
//
//                    for (int i = 0; i < mList.size(); i++) {
//                        realm.beginTransaction();
//                        Member bMember = list.get(i);
//                        bMember = (Member) mList.get(i);
//                        realm.insertOrUpdate(bMember);
//                        realm.commitTransaction();
//                    }
//                }
//            }.execute(list).get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
    }
}
