package android.boostcamp.com.boostcampvideocall;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;
import java.util.regex.Pattern;


import io.realm.Realm;

/**
 * Created by Jusung on 2017. 2. 16..
 */

public class SingUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEditName, mEditPhone;
    private Button mRegistBtn;
    private Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mEditName = (EditText) findViewById(R.id.et_name);
        mEditPhone = (EditText) findViewById(R.id.et_phone);

        mEditName.setMaxLines(1);
        mEditName.setFilters(new InputFilter[]{filterName,new InputFilter.LengthFilter(25)});
        mEditName.setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mEditPhone.setMaxWidth(11);
        mEditPhone.setFilters(new InputFilter[]{filterNum,new InputFilter.LengthFilter(11)});
        mEditPhone.setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mRegistBtn = (Button) findViewById(R.id.bt_regist);
        mRegistBtn.setOnClickListener(this);
        editTextSetting();
    }
    public void editTextSetting() {
        TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String myNumber = tManager.getLine1Number();
        if (myNumber != null && !myNumber.equals("")) {
            myNumber = editPhoneNumberValid(myNumber);
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

    public void showAlert(String title,String context,String btn){
        Toast.makeText(this,"경고 : "+title+"\ncontext : "+context, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bt_regist) {
            String name = mEditName.getText().toString();
            String myNumber = mEditPhone.getText().toString();
            if(name.equals("")){
                showAlert("경고","사용자의 이름을 입력 하세요.","확인");
                return;
            }
            if(myNumber.equals("")){
                showAlert("경고","사용자의 연락처를 입력 하세요.","확인");
                return;
            }

            myNumber = editPhoneNumberValid(myNumber);
            Toast.makeText(this, myNumber, Toast.LENGTH_SHORT).show();
        }
    }

    public InputFilter filterNum = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[0-9]*$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };
    public InputFilter filterName = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            Pattern ps = Pattern.compile("^[a-zA-Zㄱ-가-힣]*$");

            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

}
