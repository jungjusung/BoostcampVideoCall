package android.boostcamp.com.boostcampvideocall;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DefaultActivity extends AppCompatActivity {

    // 앱 실행시킬 때 화면 및
    // 각종 네트워킹 및 DB 작업이 이루어지는 액티비티

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
