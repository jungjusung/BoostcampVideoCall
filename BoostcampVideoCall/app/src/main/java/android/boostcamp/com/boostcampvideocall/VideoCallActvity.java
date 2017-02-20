package android.boostcamp.com.boostcampvideocall;

import android.animation.ObjectAnimator;

import android.boostcamp.com.boostcampvideocall.db.MyInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.sktelecom.playrtc.PlayRTC;
import com.sktelecom.playrtc.PlayRTCFactory;
import com.sktelecom.playrtc.config.PlayRTCConfig;
import com.sktelecom.playrtc.config.PlayRTCVideoConfig;
import com.sktelecom.playrtc.connector.servicehelper.PlayRTCServiceHelperListener;
import com.sktelecom.playrtc.exception.RequiredConfigMissingException;
import com.sktelecom.playrtc.exception.RequiredParameterMissingException;
import com.sktelecom.playrtc.exception.UnsupportedPlatformVersionException;
import com.sktelecom.playrtc.observer.PlayRTCObserver;
import com.sktelecom.playrtc.stream.PlayRTCMedia;
import com.sktelecom.playrtc.util.ui.PlayRTCVideoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import io.realm.Realm;

/**
 * Created by Jusung on 2017. 2. 18..
 */

public class VideoCallActvity extends AppCompatActivity implements View.OnClickListener{


    public static String TAG;
    private static final String KEY = "60ba608a-e228-4530-8711-fa38004719c1";
    private PlayRTCObserver playrtcObserver = null;
    private PlayRTC playrtc = null;

    //뷰 관련 멤버 변수
    private PlayRTCVideoView localView;
    private PlayRTCVideoView remoteView;

    private String mToken;
    private String mName;
    private String mPhoneNumber;
    private String mChannelId;
    private Realm mRealm;
    private ShimmerFrameLayout mEndCall;

    private Context mContext;
    private WindowManager mWidnowMnager;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.myNoActionBar);
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getName();
        setContentView(R.layout.activity_videocall_send);

        mContext=getApplication();
        mEndCall = (ShimmerFrameLayout) findViewById(R.id.shimmer_end_call);
        mEndCall.setDuration(2000);
        mEndCall.setRepeatMode(ObjectAnimator.REVERSE);
        mEndCall.bringToFront();
        mEndCall.setOnClickListener(this);

        Intent intent = this.getIntent();
        mName = intent.getStringExtra("name");
        mToken = intent.getStringExtra("token");
        mPhoneNumber = intent.getStringExtra("phoneNumber");
        mRealm = Realm.getDefaultInstance();

        MyInfo myInfo = mRealm.where(MyInfo.class).findFirst();
        Log.d(TAG,"비디오뷰생성");
        createPlayRTCInstance();
        createChannel(myInfo.getToken());
    }




    private void createPlayRTCInstance() {
        try {
            PlayRTCConfig setting = setPlayRTCConfiguration();

            createPlayRTCObserverInstance();
            //옵저버 생성

            playrtc = PlayRTCFactory.createPlayRTC(setting, playrtcObserver);
            Log.d(TAG,playrtc.toString());
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

    //
    private void createPlayRTCObserverInstance() {
        playrtcObserver = new PlayRTCObserver() {
            @Override
            public void onConnectChannel(PlayRTC playRTC, String channelId, String reason) {
                super.onConnectChannel(playRTC, channelId, reason);
                mChannelId=channelId;
                callToPeer(channelId);
                Log.d(TAG,"id"+channelId);
            }

            @Override
            public void onAddLocalStream(PlayRTC playRTC, PlayRTCMedia playRTCMedia) {

                long delayTime = 0;
                localView.show(delayTime);
                Toast.makeText(VideoCallActvity.this, "로컬", Toast.LENGTH_SHORT).show();
                // Link the media stream to the view.
                playRTCMedia.setVideoRenderer(localView.getVideoRenderer());

                RelativeLayout myVideoViewGroup = (RelativeLayout) findViewById(R.id.video_view_group);
                if(localView!=null) {

                    //애니메이션 효과 있을시
                    //FaceTrackerThread thread = new FaceTrackerThread(localView, remoteView, mContext, myVideoViewGroup);
                    //thread.start();
                }
                localView.getHolder().addCallback(new SurfaceCallback());
            }

            @Override
            public void onAddRemoteStream(PlayRTC playRTC, String s, String s1, PlayRTCMedia playRTCMedia) {
                changeLocalVidoeView();
                long delayTime = 100;
                remoteView.show(delayTime);
                Toast.makeText(VideoCallActvity.this, "리모트", Toast.LENGTH_SHORT).show();
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

    private void createVideoView() {
        RelativeLayout myVideoViewGroup = (RelativeLayout) findViewById(R.id.video_view_group);

        if (localView != null) {
            return;
        }
        Point myViewDimensions = new Point();
        myViewDimensions.x = myVideoViewGroup.getWidth();
        myViewDimensions.y = myVideoViewGroup.getHeight();

        if (remoteView == null) {
            createRemoteVideoView(myViewDimensions, myVideoViewGroup);
        }
        if (localView == null) {
            createLocalVideoView(myViewDimensions, myVideoViewGroup);
        }
    }

    private void createLocalVideoView(Point parentViewDimensions, RelativeLayout parentVideoViewGroup) {
        if (localView == null) {
            Point myVideoSize = new Point();
            myVideoSize.x = parentViewDimensions.x;
            myVideoSize.y = parentViewDimensions.y;


            RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            localView = new PlayRTCVideoView(parentVideoViewGroup.getContext());
            localView.setMirror(false);


            localView.setLayoutParams(param);
            parentVideoViewGroup.addView(localView);
            localView.setZOrderMediaOverlay(true);
            localView.initRenderer();
        }
    }

    private void changeLocalVidoeView() {
        RelativeLayout myVideoViewGroup = (RelativeLayout) findViewById(R.id.video_view_group);
        Point myViewDimensions = new Point();
        myViewDimensions.x = (int) (myVideoViewGroup.getWidth() * 0.3);
        myViewDimensions.y = (int) (myVideoViewGroup.getHeight() * 0.3);
        if (localView == null) {
            Log.d(TAG, "로컬뷰가 생성되지 않음");
            return;
        }
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(myViewDimensions.x, myViewDimensions.y);
        param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        param.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        param.setMargins(30, 30, 30, 30);
        localView.setLayoutParams(param);
    }

    private void createRemoteVideoView(final Point parentViewDimensions, RelativeLayout parentVideoViewGroup) {

        if (remoteView == null) {
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

    private void createChannel(String name) {
        try {
            // createChannel must have a JSON Object
            JSONObject parameters = new JSONObject();
            // 채널정보를 정의한다.
            JSONObject channel = new JSONObject();

            try {
                channel.put("channelName", "");
                parameters.put("channel", channel);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            // 채널 사용자에 대한 정보를 정의한다.
            JSONObject peer = new JSONObject();
            try {
                peer.put("uid", "");
                parameters.put("peer", peer);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "playRTC.createChannel " + parameters.toString());
            playrtc.createChannel(parameters);
        } catch (RequiredConfigMissingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        createVideoView();
    }

    public void callToPeer(String channelId) {
        Toast.makeText(mContext, "전화걸기!!", Toast.LENGTH_SHORT).show();
        String name = mName;
        String token = mToken;
        String phoneNumber = mPhoneNumber;
        String channel = channelId;

        Toast.makeText(mContext,token, Toast.LENGTH_SHORT).show();
        sendFcmInDevice(name, token, phoneNumber, channel);
    }

    public void sendFcmInDevice(String name, String token, String phoneNumber, String channelId) {
        new SendFcmAsyncTask().execute(name, token, phoneNumber, channelId);
    }


    private class SendFcmAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... datas) {
            String name = datas[0];
            String token = datas[1];
            String phoneNumber = datas[2];
            String channelID = datas[3];
            Log.d(TAG,"Fcm"+channelID);
            String url = "http://1-dot-boostcamp-jusung.appspot.com/boostcampFCM";
            requestFCM(name, token, phoneNumber, url, channelID);
            return null;
        }
    }

    public void onResume() {
        super.onResume();
        mEndCall.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        super.onPause();
        mEndCall.stopShimmerAnimation();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.shimmer_end_call){
            playrtc.deleteChannel();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(playrtc!=null){
            playrtc.close();
        }
    }
    private class SurfaceCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            Log.d(TAG,"생성됨");
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
            Log.d(TAG,"변경됨");

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        }
    }
    public static void requestFCM(String name, String token, String phoneNumber,String urlString,String channelId) {

        try{
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);

            BufferedWriter buffw=new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
            buffw.write("name="+name+"&token="+token+"&phoneNumber="+phoneNumber+"&channelId="+channelId);
            buffw.flush();
            Log.d(TAG,"name="+name+"&token="+token+"&phoneNumber="+phoneNumber+"&channelId="+channelId);
            con.getResponseCode();
            con.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
