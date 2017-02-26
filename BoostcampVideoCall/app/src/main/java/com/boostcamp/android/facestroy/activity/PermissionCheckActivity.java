package com.boostcamp.android.facestroy.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.boostcamp.android.facestroy.R;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

/**
 * Created by Jusung on 2017. 2. 15..
 */

public class PermissionCheckActivity extends AppCompatActivity {

    private final String TAG = "PermissionCheckActivity";

    // 해당 액티비티에서 권한 설정
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.myNoActionBar);
        setContentView(R.layout.activity_permission);

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            checkPermission();
        } else {
            permissionsGranted();
        }
    }


    public void checkPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                permissionsGranted();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                finish();
            }
        };
        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage(getResources().getString(R.string.permission))
                .setPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.GET_ACCOUNTS,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS,
                        Manifest.permission.WAKE_LOCK)
                .check();
    }


    public void permissionsGranted() {
        Intent intent = new Intent(this, SplashActivity.class);
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
