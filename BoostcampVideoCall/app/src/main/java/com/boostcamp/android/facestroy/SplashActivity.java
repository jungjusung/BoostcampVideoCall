package com.boostcamp.android.facestroy;

import android.animation.ObjectAnimator;
import com.boostcamp.android.facestroy.db.CallLog;
import com.boostcamp.android.facestroy.db.MyInfo;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


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
    private ShimmerFrameLayout logoText;

    private int infoSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.myNoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        logoText = (ShimmerFrameLayout) findViewById(R.id.shimmer_text);
        logoText.setDuration(2000);
        logoText.setRepeatMode(ObjectAnimator.REVERSE);

        mRealm = Realm.getDefaultInstance();
        infoSize = getInfoSize();
        openSignUpActivity();
    }
    public int getInfoSize() {
        RealmResults<MyInfo> list = mRealm.where(MyInfo.class).findAll();
        RealmResults<CallLog> list2= mRealm.where(CallLog.class).findAll();
        Toast.makeText(this, "리스트 크기 :" + list2.size(), Toast.LENGTH_SHORT).show();
        return list.size();
    }
    public void openSignUpActivity(){
        new AsyncSavePhoneInfo().execute();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (infoSize == 0) {
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
        logoText.startShimmerAnimation();
    }
    @Override
    public void onPause() {
        super.onPause();
        logoText.stopShimmerAnimation();
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
