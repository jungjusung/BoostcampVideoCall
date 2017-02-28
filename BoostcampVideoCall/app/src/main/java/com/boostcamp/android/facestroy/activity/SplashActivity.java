package com.boostcamp.android.facestroy.activity;

import android.animation.ObjectAnimator;

import com.boostcamp.android.facestroy.R;
import com.boostcamp.android.facestroy.db.MyInfo;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


import com.boostcamp.android.facestroy.utill.Utill;
import com.facebook.shimmer.ShimmerFrameLayout;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Jusung on 2017. 2. 15..
 */

public class SplashActivity extends AppCompatActivity {

    private static final String TAG="SplashActivity";
    private Realm mRealm;
    private ShimmerFrameLayout mLogoText;

    private int mInfoSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.myNoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mLogoText = (ShimmerFrameLayout) findViewById(R.id.shimmer_text);
        mLogoText.setDuration(2000);
        mLogoText.setRepeatMode(ObjectAnimator.REVERSE);

        mRealm = Realm.getDefaultInstance();
        mInfoSize = getInfoSize();
        openSignUpActivity();
    }
    public int getInfoSize() {
        RealmResults<MyInfo> list = mRealm.where(MyInfo.class).findAll();
        return list.size();
    }
    public void openSignUpActivity(){
        new AsyncSavePhoneInfo().execute();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mInfoSize == 0) {
                    Intent intent = new Intent(getApplicationContext(), SingUpActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    //회원 정보가 등록되었을 경우
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },6000);
    }


    @Override
    public void onBackPressed() {
    }
    @Override
    public void onResume() {
        super.onResume();
        mLogoText.startShimmerAnimation();
    }
    @Override
    public void onPause() {
        super.onPause();
        mLogoText.stopShimmerAnimation();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class AsyncSavePhoneInfo extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            Utill.savePhoneInfoToRealm(getApplicationContext(), mRealm);
            return null;
        }
    }

}
