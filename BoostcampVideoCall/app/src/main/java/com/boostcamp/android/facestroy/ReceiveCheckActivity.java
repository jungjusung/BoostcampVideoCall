package com.boostcamp.android.facestroy;

import android.animation.ObjectAnimator;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.boostcamp.android.facestroy.utill.PushWakeLock;
import com.boostcamp.android.facestroy.utill.Utill;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.io.IOException;

/**
 * Created by Jusung on 2017. 2. 20..
 */

public class ReceiveCheckActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG="CallReciveActivity";
    private static final int FACE_FRONT=1;

    private SurfaceView mFaceView;
    private ShimmerFrameLayout mShimmerLayout;
    private ImageView mImageCallOk,mImageCallEnd;
    private TextView mTextTitle,mTextName;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private String mName,mPhoneNumber,mToken,mChannelId,mSender;

    private Context mContext;
    private MediaPlayer mPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.myNoActionBar);
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_videocall_receive);

        mContext=getApplicationContext();
        mPlayer=new MediaPlayer();
        mShimmerLayout = (ShimmerFrameLayout) findViewById(R.id.shimmer_layout);
        mShimmerLayout.setDuration(2000);
        mShimmerLayout.setRepeatMode(ObjectAnimator.REVERSE);

        mImageCallOk=(ImageView)findViewById(R.id.shimmer_ok_call);
        mImageCallOk.bringToFront();
        mImageCallEnd=(ImageView)findViewById(R.id.shimmer_end_call);
        mImageCallOk.bringToFront();

        mImageCallEnd.setOnClickListener(this);
        mImageCallOk.setOnClickListener(this);

        mTextName=(TextView)findViewById(R.id.tv_send_name);
        mTextTitle=(TextView)findViewById(R.id.tv_send_title);


        Intent intent=getIntent();
        mName=intent.getStringExtra("name");
        mPhoneNumber=intent.getStringExtra("phoneNumber");
        mToken=intent.getStringExtra("token");
        mChannelId=intent.getStringExtra("channelId");
        mSender=intent.getStringExtra("sender");



        mTextName.setText(mName);
        mFaceView=(SurfaceView)findViewById(R.id.sv_face);
        mHolder=mFaceView.getHolder();
        mHolder.addCallback(faceListener);

        Utill.startRingtone(mContext,mPlayer);

        //createPlayRTCInstance();
        //connectChannel(mChannelId);
    }
    private SurfaceHolder.Callback faceListener=new SurfaceHolder.Callback(){
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            mCamera=Camera.open(FACE_FRONT);
            try {
                mCamera.setPreviewDisplay(surfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
            Camera.Parameters parameters=mCamera.getParameters();
            parameters.setRotation(90);
            parameters.setPreviewSize(width,height);
            mCamera.setDisplayOrientation(90);
            mCamera.setParameters(parameters);
            mCamera.startPreview();
            //PushWakeLock.acquireCpuWakeLock(mContext);
        }
        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            if(mCamera!=null) {
                mCamera.release();
                mCamera = null;
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        mShimmerLayout.stopShimmerAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mShimmerLayout.startShimmerAnimation();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.shimmer_end_call:
                finish();
                break;
            case R.id.shimmer_ok_call:
                Utill.stopRington(mPlayer);

                Intent intent=new Intent(getApplicationContext(),ReceiveCallActivity.class);
                intent.putExtra("name",mName);
                intent.putExtra("phoneNumber",mPhoneNumber);
                intent.putExtra("token",mToken);
                intent.putExtra("channelId",mChannelId);
                intent.putExtra("sender",mSender);
                startActivity(intent);

                finish();
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
