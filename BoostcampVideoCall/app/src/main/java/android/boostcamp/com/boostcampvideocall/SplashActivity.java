package android.boostcamp.com.boostcampvideocall;

import android.animation.ObjectAnimator;
import android.boostcamp.com.boostcampvideocall.db.CallLog;
import android.boostcamp.com.boostcampvideocall.db.Member;
import android.boostcamp.com.boostcampvideocall.db.MemberService;
import android.boostcamp.com.boostcampvideocall.db.MyInfo;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jusung on 2017. 2. 15..
 */

public class SplashActivity extends AppCompatActivity {

    private Realm realm;
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

        realm = Realm.getDefaultInstance();
        infoSize = getInfoSize();
        openSignUpActivity();
    }
    public int getInfoSize() {
        RealmResults<MyInfo> list = realm.where(MyInfo.class).findAll();
        RealmResults<CallLog> list2=realm.where(CallLog.class).findAll();
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
            savePhoneInfoToRealm();
            return null;
        }
    }
    public void savePhoneInfoToRealm() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://1-dot-boostcamp-jusung.appspot.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final Cursor myPhoneInfo = getContentResolver()
                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);


        MemberService service = retrofit.create(MemberService.class);
        Call<List<Member>> memberList = service.listMember();

        memberList.enqueue(new Callback<List<Member>>() {
            @Override
            public void onResponse(Call<List<Member>> call, Response<List<Member>> response) {
                if (response.isSuccessful()) {
                    final List<Member> list = response.body();

                    while (myPhoneInfo.moveToNext()) {
                        String mName = myPhoneInfo.getString(myPhoneInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        String mPhoneNumber = myPhoneInfo.getString(myPhoneInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        for (Member member : list) {
                            String sPhoneNumber = member.getPhoneNumber();

                            if (sPhoneNumber.equals(mPhoneNumber)) {

                                realm.beginTransaction();
                                Member newMember = new Member();
                                newMember.setName(mName);
                                newMember.setPhoneNumber(mPhoneNumber);
                                newMember.setToken(member.getToken());
                                newMember.setId(member.getId());
                                realm.insertOrUpdate(newMember);
                                realm.commitTransaction();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Member>> call, Throwable t) {
            }
        });


    }
}
