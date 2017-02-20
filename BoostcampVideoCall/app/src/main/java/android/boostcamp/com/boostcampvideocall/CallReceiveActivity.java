package android.boostcamp.com.boostcampvideocall;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.sktelecom.playrtc.PlayRTC;
import com.sktelecom.playrtc.PlayRTCFactory;
import com.sktelecom.playrtc.config.PlayRTCConfig;
import com.sktelecom.playrtc.config.PlayRTCVideoConfig;
import com.sktelecom.playrtc.exception.RequiredConfigMissingException;
import com.sktelecom.playrtc.exception.RequiredParameterMissingException;
import com.sktelecom.playrtc.exception.UnsupportedPlatformVersionException;
import com.sktelecom.playrtc.observer.PlayRTCObserver;
import com.sktelecom.playrtc.stream.PlayRTCMedia;
import com.sktelecom.playrtc.util.ui.PlayRTCVideoView;

import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Jusung on 2017. 2. 20..
 */

public class CallReceiveActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG="CallReciveActivity";
    private static final String KEY = "60ba608a-e228-4530-8711-fa38004719c1";
    private static final int FACE_FRONT=1;

    private PlayRTCObserver playrtcObserver = null;
    private PlayRTC playrtc = null;
    private Context context;

    //뷰 관련 멤버 변수
    private PlayRTCVideoView localView;
    private PlayRTCVideoView remoteView;


    private ShimmerFrameLayout mShimmerLayout;
    private ImageView mImageCallOk,mImageCallEnd;
    private FrameLayout mLayoutBefore;
    private TextView mTextTitle,mTextName;
    private SurfaceView mFaceView;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private String mName,mPhoneNumber,mToken,mChannelId;
    private RelativeLayout mAfterLayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.myNoActionBar);
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_videocall_receive);

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

        mLayoutBefore=(FrameLayout)findViewById(R.id.fl_before);
        mAfterLayout=(RelativeLayout)findViewById(R.id.video_view_group);

        mFaceView=(SurfaceView)findViewById(R.id.sv_face);
        mHolder=mFaceView.getHolder();
        mHolder.addCallback(faceListener);
        Intent intent=getIntent();
        mName=intent.getStringExtra("name");
        mPhoneNumber=intent.getStringExtra("phoneNumber");
        mToken=intent.getStringExtra("token");
        mChannelId=intent.getStringExtra("channelId");

        mTextName.setText(mName);
        createPlayRTCInstance();

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
            parameters.setPreviewSize(width,height);
            mCamera.startPreview();

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

    private void createPlayRTCInstance() {
        try {
            PlayRTCConfig setting = setPlayRTCConfiguration();

            createPlayRTCObserverInstance();
            //옵저버 생성

            playrtc = PlayRTCFactory.createPlayRTC(setting, playrtcObserver);
//            Log.d(TAG,playrtc.getPeerId().toString());

            Log.d(TAG, "playrtc생성");
            //플레이 rtc객체 생성

        } catch (UnsupportedPlatformVersionException e) {
            e.printStackTrace();
        } catch (RequiredParameterMissingException e) {
            e.printStackTrace();
        }
    }

    private PlayRTCConfig setPlayRTCConfiguration() {
        PlayRTCConfig settings = PlayRTCFactory.createConfig();

        // PlayRTC instance have to get the application context.
        settings.setAndroidContext(getApplicationContext());


        settings.setPrevUserMediaEnable(true);
        // T Developers Project Key.
        settings.setProjectId(KEY);

        // video는 기본 640x480 30 frame
        settings.video.setEnable(true);
        settings.video.setCameraType(PlayRTCVideoConfig.CameraType.Front);

        settings.audio.setEnable(true);
        settings.audio.setAudioManagerEnable(true); //음성 출력 장치 자동 선택 기눙 활성화
        settings.data.setEnable(false);

        return settings;
    }

    private void createPlayRTCObserverInstance() {
        playrtcObserver = new PlayRTCObserver() {
            @Override
            public void onConnectChannel(PlayRTC playRTC, String channelId, String reason) {
                super.onConnectChannel(playRTC, channelId, reason);
                if (channelId != null) {

                    long delayTime = 0;
                    remoteView.show(delayTime);
                    localView.show(delayTime);
                    Log.d(TAG, channelId);
                    String channel = channelId.trim();
                    Log.d(TAG, channel);
                }
                if(reason.equals("connect")){
                    Log.d(TAG,"connect");
                }
            }

            @Override
            public void onAddLocalStream(PlayRTC playRTC, PlayRTCMedia playRTCMedia) {
                long delayTime = 0;
                localView.show(delayTime);
                // Link the media stream to the view.
                playRTCMedia.setVideoRenderer(localView.getVideoRenderer());
            }

            @Override
            public void onAddRemoteStream(PlayRTC playRTC, String s, String s1, PlayRTCMedia playRTCMedia) {
                long delayTime = 0;
                remoteView.show(delayTime);
                // Link the media stream to the view.
                playRTCMedia.setVideoRenderer(remoteView.getVideoRenderer());
            }

            @Override
            public void onDisconnectChannel(PlayRTC playRTC, String s) {
                finish();
                if (playrtc != null) {
                    playrtc.close();

                }
            }

            @Override
            public void onOtherDisconnectChannel(PlayRTC playRTC, String s, String s1) {
                finish();
                if (playrtc != null) {
                    playrtc.close();

                }
            }
        };
    }

    private void connectChannel(String responseChannel) {
        try {

            mLayoutBefore.setVisibility(View.GONE);
            mCamera.release();
            if(mCamera!=null)
                mCamera=null;

            mAfterLayout.setVisibility(View.VISIBLE);
            mAfterLayout.bringToFront();
            createVideoView();

            Log.d(TAG, "접속하는 채널:" + responseChannel.trim());
            playrtc.connectChannel(responseChannel.trim(), new JSONObject());

        } catch (RequiredConfigMissingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

    }
    private void createVideoView() {

        Log.d("TAG","create View1");
        if (localView != null) {
            return;
        }
        Point myViewDimensions = new Point();
        myViewDimensions.x = mAfterLayout.getWidth();
        myViewDimensions.y = mAfterLayout.getHeight();
        Log.d("TAG","create View2");
        if (remoteView == null) {
            createRemoteVideoView(myViewDimensions, mAfterLayout);
        }
        if (localView == null) {
            createLocalVideoView(myViewDimensions, mAfterLayout);
        }
    }

    private void createLocalVideoView(Point parentViewDimensions, RelativeLayout parentVideoViewGroup) {
        if (localView == null) {
            Log.d(TAG, "LocalView생성");
            Point myVideoSize = new Point();
            myVideoSize.x = (int) (parentViewDimensions.x * 0.3);
            myVideoSize.y = (int) (parentViewDimensions.y * 0.3);

            RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(myVideoSize.x, myVideoSize.y);
            param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            param.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            param.setMargins(30, 30, 30, 30);


            localView = new PlayRTCVideoView(parentVideoViewGroup.getContext());
            localView.setMirror(false);


            localView.setLayoutParams(param);
            parentVideoViewGroup.addView(localView);
            localView.setZOrderMediaOverlay(true);
            localView.initRenderer();


        }

    }

    private void createRemoteVideoView(final Point parentViewDimensions, RelativeLayout parentVideoViewGroup) {

        if (remoteView == null) {
            Log.d(TAG, "리모트뷰 생성");
            Point myVideoSize = new Point();
            myVideoSize.x = parentViewDimensions.x;
            myVideoSize.y = parentViewDimensions.y;

            RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            // Create the remoteView.
            remoteView = new PlayRTCVideoView(parentVideoViewGroup.getContext());
            remoteView.setMirror(false);
            // Set the layout parameters.
            remoteView.setLayoutParams(param);

            // Add the view to the videoViewGroup.
            parentVideoViewGroup.addView(remoteView);

            remoteView.initRenderer();
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.shimmer_end_call:
                playrtc.deleteChannel();
                finish();
                break;
            case R.id.shimmer_ok_call:
                connectChannel(mChannelId);
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(playrtc!=null){
            playrtc.close();
        }
    }
}
