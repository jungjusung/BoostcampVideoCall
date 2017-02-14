package android.boostcamp.com.boostcampvideocall;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

/**
 * Created by Jusung on 2017. 2. 15..
 */

public class PermissionCheckActivity extends AppCompatActivity {

    public static final String[] MANDATORY_PERMISSIONS = {
            "android.permission.INTERNET",
            "android.permission.CAMERA",
            "android.permission.RECORD_AUDIO",
            "android.permission.MODIFY_AUDIO_SETTINGS",
            "android.permission.ACCESS_NETWORK_STATE",
            "android.permission.CHANGE_WIFI_STATE",
            "android.permission.ACCESS_WIFI_STATE",
            "android.permission.READ_PHONE_STATE",
            "android.permission.BLUETOOTH",
            "android.permission.BLUETOOTH_ADMIN",
            "android.permission.WRITE_EXTERNAL_STORAGE"
    };


    private final String TAG = "PermissionCheckActivity";

    // 해당 액티비티에서 권한 설정
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                permissionsGranted();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                //Toast.makeText(PermissionCheckActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                finish();
            }
        };


        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("해당 앱을 사용하려면 권한이 필요합니다.\n\n설정 버튼을 눌러, 권한을 확인해주세요.")
                .setPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                .check();
    }

    public void permissionsGranted() {
        Intent intent = new Intent(this, DefaultActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
