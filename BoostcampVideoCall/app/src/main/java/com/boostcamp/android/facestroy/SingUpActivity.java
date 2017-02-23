package com.boostcamp.android.facestroy;

import com.boostcamp.android.facestroy.db.MyInfo;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.boostcamp.android.facestroy.utill.Utill;
import com.google.firebase.iid.FirebaseInstanceId;
import java.util.Locale;
import io.realm.Realm;

/**
 * Created by Jusung on 2017. 2. 16..
 */

public class SingUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEditName, mEditPhone;
    private Button mRegistBtn;
    private Realm realm;
    private String token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.myNoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        token = FirebaseInstanceId.getInstance().getToken();


        mEditName = (EditText) findViewById(R.id.et_name);
        mEditPhone = (EditText) findViewById(R.id.et_phone);

        mEditName.setMaxLines(1);
        mEditName.setFilters(new InputFilter[]{Utill.filterAlphaKor,new InputFilter.LengthFilter(25)});
        mEditName.setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mEditPhone.setMaxWidth(11);
        mEditPhone.setFilters(new InputFilter[]{Utill.filterNum,new InputFilter.LengthFilter(11)});
        mEditPhone.setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mRegistBtn = (Button) findViewById(R.id.bt_regist);
        mRegistBtn.setOnClickListener(this);
        editTextSetting();
    }
    public void editTextSetting() {
        TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String myNumber = tManager.getLine1Number();
        if (myNumber != null &&!myNumber.equals("")) {
            myNumber = myNumber.replace("+82","0");
            myNumber=myNumber.replace("-","");

            mEditPhone.setText(myNumber);
            mEditPhone.setClickable(false);
            mEditPhone.setEnabled(false);
            mEditPhone.setFocusable(false);
            mEditPhone.setFocusableInTouchMode(false);
        }
    }

    public String editPhoneNumberValid(String phoneNumber) {
        String valiedNumber = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            valiedNumber = PhoneNumberUtils.formatNumber(phoneNumber, Locale.getDefault().getCountry());
        } else {
            valiedNumber = PhoneNumberUtils.formatNumber(phoneNumber);
        }
        return valiedNumber;
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bt_regist) {
            String name = mEditName.getText().toString();
            String phoneNumber = mEditPhone.getText().toString();
            if(name.equals("")){
                Toast.makeText(this, "사용자의 이름을 입력 하세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if(phoneNumber.equals("")){
                Toast.makeText(this, "연락처를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            phoneNumber = editPhoneNumberValid(phoneNumber);
            insertToRealmInfo(name,phoneNumber,token);
            insertDB(name,phoneNumber,token);
        }
    }
    public void insertToRealmInfo(String name,String phoneNumber,String token){
        realm=Realm.getDefaultInstance();
        MyInfo info=new MyInfo();
        realm.beginTransaction();
        info.setName(name);
        info.setPhoneNumber(phoneNumber);
        if(token==null||token.equals(""))
            info.setToken(FirebaseInstanceId.getInstance().getToken());
        else
            info.setToken(token);
        info.setUrl("");
        info.setStatus("");

        realm.insert(info);
        realm.commitTransaction();
    }
    public void insertDB(String name,String phoneNumber,String token){
        new InsertDBAsyncTask().execute(name,phoneNumber,token);
    }
    private class InsertDBAsyncTask extends AsyncTask<String,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... datas) {
            String name=datas[0];
            String phoneNumber=datas[1];
            String token=datas[2];
            if(token==null||token.equals(""))
                token=FirebaseInstanceId.getInstance().getToken();

            String url="http://1-dot-boostcamp-jusung.appspot.com/boostcampDB";
            Utill.registMemberInfo(name,phoneNumber,token,url);
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
