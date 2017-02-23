package com.boostcamp.android.facestroy;

import android.animation.ObjectAnimator;

import com.boostcamp.android.facestroy.db.CallLog;
import com.boostcamp.android.facestroy.db.Member;
import com.boostcamp.android.facestroy.db.MyInfo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.boostcamp.android.facestroy.effect.MustacheEffectForMeThread;
import com.boostcamp.android.facestroy.effect.MustacheEffectForOtherThread;
import com.boostcamp.android.facestroy.utill.Utill;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.iid.FirebaseInstanceId;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import io.realm.Realm;
import io.realm.internal.Util;

/**
 * Created by Jusung on 2017. 2. 18..
 */

public class VideoCallActvity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {


    public static final String TAG="VideoCallActvity";
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
    private MediaPlayer mPlayer;
    private long mStartTime;
    private long mEndTime;
    private Date mDate;

    private RelativeLayout mMenuButton;
    private RelativeLayout mEffectButton;
    private LinearLayout mBtnExit, mBtnEffect, mBtnRotation;
    private LinearLayout mEffect1, mEffect3;
    private int mLocation[] = new int[2];
    public static MustacheEffectForMeThread mustacheEffectForMeThread;
    public static MustacheEffectForOtherThread mustacheEffectForOtherThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.myNoActionBar);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_videocall_send);
        mMenuButton = (RelativeLayout) findViewById(R.id.menu_button);
        mEffectButton = (RelativeLayout) findViewById(R.id.effect_button);
        mMenuButton.bringToFront();
        mEffectButton.bringToFront();

        mPlayer=new MediaPlayer();
        mContext = getApplication();
        mEndCall = (ShimmerFrameLayout) findViewById(R.id.shimmer_end_call);
        mEndCall.setDuration(2000);
        mEndCall.setRepeatMode(ObjectAnimator.REVERSE);
        mEndCall.bringToFront();
        mEndCall.setOnClickListener(this);

        mBtnEffect = (LinearLayout) findViewById(R.id.btn_effect);
        mBtnExit = (LinearLayout) findViewById(R.id.btn_exit);
        mBtnRotation = (LinearLayout) findViewById(R.id.btn_rotation);


        mEffect1 = (LinearLayout) findViewById(R.id.btn_effect1);
        mEffect3 = (LinearLayout) findViewById(R.id.btn_effect3);
        mEffect1.setOnClickListener(this);
        mEffect3.setOnClickListener(this);
        mBtnEffect.setOnClickListener(this);
        mBtnExit.setOnClickListener(this);
        mBtnRotation.setOnClickListener(this);

        Intent intent = this.getIntent();
        mName = intent.getStringExtra("name");
        mToken = intent.getStringExtra("token");
        mPhoneNumber = intent.getStringExtra("phoneNumber");
        mRealm = Realm.getDefaultInstance();

        Log.d(TAG, " 전송 로그 : " + mToken);
        MyInfo myInfo = mRealm.where(MyInfo.class).findFirst();
        Log.d(TAG, "비디오뷰생성");
        createPlayRTCInstance();
        createChannel(myInfo.getToken());
        Utill.startRingtone(mContext,mPlayer);
    }


    private void createPlayRTCInstance() {
        try {
            PlayRTCConfig setting = setPlayRTCConfiguration();

            createPlayRTCObserverInstance();
            //옵저버 생성

            playrtc = PlayRTCFactory.createPlayRTC(setting, playrtcObserver);
            Log.d(TAG, playrtc.toString());
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
                mChannelId = channelId;
                mStartTime = System.currentTimeMillis();
                callToPeer(channelId);
                Log.d(TAG, "id" + channelId);
            }

            @Override
            public void onAddLocalStream(PlayRTC playRTC, PlayRTCMedia playRTCMedia) {

                long delayTime = 0;
                localView.show(delayTime);
                Toast.makeText(VideoCallActvity.this, "로컬", Toast.LENGTH_SHORT).show();
                // Link the media stream to the view.
                playRTCMedia.setVideoRenderer(localView.getVideoRenderer());

                RelativeLayout myVideoViewGroup = (RelativeLayout) findViewById(R.id.video_view_group);
                if (localView != null) {

                    //애니메이션 효과 있을시
                    mustacheEffectForMeThread = new MustacheEffectForMeThread(localView, remoteView, mContext, myVideoViewGroup);
                    mustacheEffectForOtherThread = new MustacheEffectForOtherThread(localView, remoteView, mContext, myVideoViewGroup);
                }
                localView.getHolder().addCallback(new SurfaceCallback());
            }

            @Override
            public void onAddRemoteStream(PlayRTC playRTC, String s, String s1, PlayRTCMedia playRTCMedia) {
                changeLocalVidoeView();
                long delayTime = 100;
                remoteView.show(delayTime);
                Toast.makeText(VideoCallActvity.this, "리모트", Toast.LENGTH_SHORT).show();
                mEndCall.setVisibility(View.INVISIBLE);
                // Link the media stream to the view.
                playRTCMedia.setVideoRenderer(remoteView.getVideoRenderer());
                Utill.stopRington(mPlayer);
//                MustacheEffectForMeThread th=new MustacheEffectForMeThread(localView,remoteView,);
//                th.start();
            }

            @Override
            public void onDisconnectChannel(PlayRTC playRTC, String s) {
                finish();
                if (playrtc != null) {

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
            parentVideoViewGroup.setOnTouchListener(this);
            localView.setZOrderMediaOverlay(true);
            localView.initRenderer();
            localView.setOnTouchListener(this);
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

        localView.getLocationOnScreen(mLocation);
        Log.d(TAG, mLocation[0] + " " + mLocation[1]);
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
            remoteView.setOnTouchListener(this);
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

        Toast.makeText(mContext, token, Toast.LENGTH_SHORT).show();
        sendFcmInDevice(name, token, phoneNumber, channel);
    }

    public void sendFcmInDevice(String name, String token, String phoneNumber, String channelId) {
        new SendFcmAsyncTask().execute(name, token, phoneNumber, channelId);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {


        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

            if (mMenuButton.getVisibility() == View.GONE) {
                mMenuButton.setVisibility(View.VISIBLE);
            } else if (mMenuButton.getVisibility() == View.INVISIBLE) {
                mMenuButton.setVisibility(View.VISIBLE);
            } else {
                mMenuButton.setVisibility(View.INVISIBLE);
            }
            return true;
        }
        return super.onTouchEvent(motionEvent);
    }


    private class SendFcmAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... datas) {
            String name = datas[0];
            String token = datas[1];
            String phoneNumber = datas[2];
            String channelID = datas[3];
            Log.d(TAG, "Fcm" + channelID);
            String url = "http://1-dot-boostcamp-jusung.appspot.com/boostcampFCM";
            requestFCM(name, token, phoneNumber, url, channelID);
            return null;
        }
    }
    public void requestFCM(String name, String token, String phoneNumber, String urlString, String channelId) {

        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);

            BufferedWriter buffw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
            String sender = FirebaseInstanceId.getInstance().getToken();
            buffw.write("name=" + name + "&token=" + token + "&phoneNumber=" + phoneNumber + "&channelId=" + channelId + "&sender=" + sender);
            buffw.flush();
            con.getResponseCode();
            con.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
        switch (view.getId()) {
            case R.id.shimmer_end_call:
                playrtc.deleteChannel();
                finish();
                break;
            case R.id.btn_exit:
                playrtc.deleteChannel();
                finish();
                break;
            case R.id.btn_effect:
                mMenuButton.setVisibility(View.INVISIBLE);
                mEffectButton.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_rotation:
                //전환
                break;
            case R.id.btn_effect1:
                mustacheEffectForMeThread.start();
                mustacheEffectForOtherThread.start();
                mEffectButton.setVisibility(View.INVISIBLE);
                break;
            case R.id.btn_effect3:
                mustacheEffectForMeThread.effectOff();
                mustacheEffectForMeThread.stopThread();

                mustacheEffectForOtherThread.effectOff();
                mustacheEffectForOtherThread.stopThread();
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mustacheEffectForMeThread.effectOff();
        mustacheEffectForMeThread.stopThread();
        Utill.stopRington(mPlayer);

        playrtc.close();
        mEndTime = System.currentTimeMillis();
        mDate = new Date(mEndTime);

        mRealm.beginTransaction();
        Member member = mRealm.where(Member.class).equalTo("token", mToken).findFirst();

        long time = mEndTime - mStartTime;
        if (member != null) {
            member.setCount(member.getCount() + 1);
            member.setTime(member.getTime() + time);
            mRealm.copyToRealmOrUpdate(member);
        }
        int sequenceNum = 0;
        if (mRealm.where(CallLog.class).max("id") != null)
            sequenceNum = mRealm.where(CallLog.class).max("id").intValue() + 1;
        CallLog callLog = new CallLog();
        callLog.setId(sequenceNum);
        callLog.setDate(mDate);
        callLog.setTo(FirebaseInstanceId.getInstance().getToken());
        callLog.setFrom(mToken);
        callLog.setTime(time);
        mRealm.insert(callLog);
        mRealm.commitTransaction();
        if (playrtc != null) {
            playrtc.close();
        }
    }

    private class SurfaceCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            Log.d(TAG, "생성됨");
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
            Log.d(TAG, "변경됨");

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        }
    }

}
