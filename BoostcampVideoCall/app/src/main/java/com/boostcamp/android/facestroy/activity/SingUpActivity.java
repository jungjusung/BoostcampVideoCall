package com.boostcamp.android.facestroy.activity;

import com.boostcamp.android.facestroy.R;
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

    private static final String TAG="SingUpActivity";
    private EditText mEditName, mEditPhone;
    private Button mRegistBtn;
    private Realm mRealm;
    private String mToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.myNoActionBar);
        setContentView(R.layout.activity_sign_up);
        mToken = FirebaseInstanceId.getInstance().getToken();


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
                Toast.makeText(this, getResources().getString(R.string.name_check), Toast.LENGTH_SHORT).show();
                return;
            }
            if(phoneNumber.equals("")){
                Toast.makeText(this, getResources().getString(R.string.phone_number), Toast.LENGTH_SHORT).show();
                return;
            }

            phoneNumber = editPhoneNumberValid(phoneNumber);
            insertToRealmInfo(name,phoneNumber, mToken);
            insertDB(name,phoneNumber, mToken);
        }
    }
    public void insertToRealmInfo(String name,String phoneNumber,String token){
        mRealm =Realm.getDefaultInstance();
        MyInfo info=new MyInfo();
        mRealm.beginTransaction();
        info.setName(name);
        info.setPhoneNumber(phoneNumber);
        if(token==null||token.equals(""))
            info.setToken(FirebaseInstanceId.getInstance().getToken());
        else
            info.setToken(token);
        info.setUrl("");
        info.setStatus("");
        mRealm.insert(info);
        mRealm.commitTransaction();
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
