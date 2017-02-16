package android.boostcamp.com.boostcampvideocall;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

public class DefaultActivity extends AppCompatActivity {

    // 앱 실행시킬 때 화면 및
    // 각종 네트워킹 및 DB 작업이 이루어지는 액티비티

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Toast.makeText(this,refreshedToken, Toast.LENGTH_SHORT).show();

        String name="Test1";
        String phoneNumber="010-1234-5678";
        String token=refreshedToken;
        Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
        insertDB(name,token,phoneNumber);


    }

    public void insertDB(String name,String token,String phoneNumber){
        new InsertDBAsyncTask().execute(name,token,phoneNumber);
    }
    private class InsertDBAsyncTask extends AsyncTask<String,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... datas) {
            String name=datas[0];
            String token=datas[1];
            String phoneNumber=datas[2];
            String url="http://1-dot-boostcamp-jusung.appspot.com/boostcamp_selectOne";
            UtillGAE.requestMemberGAE(name,token,phoneNumber,url);
            return null;
        }
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
