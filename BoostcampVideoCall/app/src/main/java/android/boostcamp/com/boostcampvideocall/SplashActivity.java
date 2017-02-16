package android.boostcamp.com.boostcampvideocall;

import android.animation.ObjectAnimator;
import android.boostcamp.com.boostcampvideocall.DB.MyInfo;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.Toast;


import com.facebook.shimmer.ShimmerFrameLayout;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Jusung on 2017. 2. 15..
 */

public class SplashActivity extends AppCompatActivity {

    private Realm realm;
    private ShimmerFrameLayout logoText;
    private Thread animationThread;
    private int infoSize;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        logoText = (ShimmerFrameLayout) findViewById(R.id.shimmer_text);
        logoText.setDuration(2000);
        logoText.setRepeatMode(ObjectAnimator.REVERSE);

        realm = Realm.getDefaultInstance();
        infoSize = getInfoSize();
        openSignUpActivity();
    }


    public int getInfoSize() {
        RealmResults<MyInfo> list = realm.where(MyInfo.class).findAll();
        Toast.makeText(this, "리스트 크기 :" + list.size(), Toast.LENGTH_SHORT).show();
        return list.size();
    }
    public void openSignUpActivity(){
        animationThread=new Thread(){
            public void run() {
                try {
                    sleep(6000);
                    if (infoSize == 0) {
                        Intent intent = new Intent(getApplicationContext(), SingUpActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        //회원 정보가 등록되었을 경우
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
        animationThread.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
}
