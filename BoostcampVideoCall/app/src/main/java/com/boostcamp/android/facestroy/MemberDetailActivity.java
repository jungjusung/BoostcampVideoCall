package com.boostcamp.android.facestroy;

import com.boostcamp.android.facestroy.db.Member;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.boostcamp.android.facestroy.db.MemberService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

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
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jusung on 2017. 2. 20..
 */

public class MemberDetailActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "MemberDetailActivity";
    private ViewPager mDetailViewPager;
    private Realm mRealm;
    private CircleImageView mBtnPrevious, mBtnNext;
    private ImageView mBtnBackspace;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.myNoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_detail);
        mRealm = Realm.getDefaultInstance();

        mDetailViewPager = (ViewPager) findViewById(R.id.vp_detail);
        mBtnPrevious = (CircleImageView) findViewById(R.id.bt_previous);
        mBtnNext = (CircleImageView) findViewById(R.id.bt_next);

        mBtnBackspace = (ImageView) findViewById(R.id.bt_backspace);
        mBtnPrevious.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
        mBtnBackspace.setOnClickListener(this);
        mBtnBackspace.bringToFront();

        DetailAdapter detailAdapter = new DetailAdapter(getLayoutInflater());
        mDetailViewPager.setAdapter(detailAdapter);

    }

    @Override
    public void onClick(View view) {

        int position;
        switch (view.getId()) {
            case R.id.bt_next:
                position = mDetailViewPager.getCurrentItem();
                mDetailViewPager.setCurrentItem(position + 1, true);
                break;
            case R.id.bt_previous:
                position = mDetailViewPager.getCurrentItem();
                mDetailViewPager.setCurrentItem(position - 1, true);
                break;
            case R.id.bt_backspace:
                onBackPressed();
                break;
        }
    }

    public class DetailAdapter extends PagerAdapter {

        private LayoutInflater mInflater;
        private CircleImageView mProfile;
        private TextView mName, mPhoneNumber, mCount, mTime, mStatus;
        private RealmResults<Member> list;
        private Member member;

        public DetailAdapter(LayoutInflater mInflater) {
            this.mInflater = mInflater;
            list = mRealm.where(Member.class).findAll();
            Log.d(TAG, "여기는??");
        }

        @Override
        public int getCount() {
            return mRealm.where(Member.class).findAll().size();
        }


        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = null;
            view = mInflater.inflate(R.layout.item_detail, null);
            mProfile = (CircleImageView) view.findViewById(R.id.profile_image);
            mName = (TextView) view.findViewById(R.id.tv_name);
            mPhoneNumber = (TextView) view.findViewById(R.id.tv_phone);
            mCount = (TextView) view.findViewById(R.id.tv_count);
            mTime = (TextView) view.findViewById(R.id.tv_total_time);
            mStatus = (TextView) view.findViewById(R.id.tv_status);

            member = list.get(position);

            if (member.getUrl() == "") {
                //디폴트 이미지 !!
            } else {
                Glide.with(getApplicationContext())
                        .load(member.getUrl())
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>(100, 100) {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                mProfile.setImageBitmap(resource);
                            }
                        });
            }

            mName.setText(member.getName());
            mPhoneNumber.setText(member.getPhoneNumber());
            mStatus.setText(member.getStatus());
            mCount.setText(member.getCount() + "회");
            mTime.setText(Utill.timeToString(member.getTime()));

            Log.d(TAG, member.getToken());
            Log.d(TAG, "포지션" + position);
            notifyDataSetChanged();
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
