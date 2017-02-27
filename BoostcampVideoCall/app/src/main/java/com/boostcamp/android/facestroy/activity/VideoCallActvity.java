package com.boostcamp.android.facestroy.activity;

import android.animation.ObjectAnimator;

import com.boostcamp.android.facestroy.R;
import com.boostcamp.android.facestroy.db.CallLog;
import com.boostcamp.android.facestroy.db.Member;
import com.boostcamp.android.facestroy.db.MyInfo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.TextView;

import com.boostcamp.android.facestroy.effect.HeartTreeEffectForMeThread;
import com.boostcamp.android.facestroy.effect.HeartTreeEffectForOtherThread;
import com.boostcamp.android.facestroy.effect.MustacheEffectForMeThread;
import com.boostcamp.android.facestroy.effect.MustacheEffectForOtherThread;
import com.boostcamp.android.facestroy.effect.RabbitEffectForMeThread;
import com.boostcamp.android.facestroy.effect.RabbitEffectForOtherThread;
import com.boostcamp.android.facestroy.utill.Constant;
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
import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by Jusung on 2017. 2. 18..
 */

public class VideoCallActvity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {


    private static final String TAG = "VideoCallActvity";

    private PlayRTCObserver mPlayrtcObserver;
    private PlayRTC mPlayrtc;

    //뷰 관련 멤버 변수
    public static PlayRTCVideoView mLocalView, mRemoteView;
    private String mToken, mName, mPhoneNumber, mChannelId;

    private Realm mRealm;
    private ShimmerFrameLayout mEndCall;

    public static Context mContext;
    private MediaPlayer mPlayer;
    private long mStartTime, mEndTime;
    private Date mDate;

    private RelativeLayout mMenuButton, mEffectButton;
    public static RelativeLayout mMyVideoViewGroup;
    private LinearLayout mEffectHeart, mEffectRabbit, mEffectMustache, mEffectExit, mBtnExit, mBtnEffect, mBtnRotation;
    private List<LinearLayout> mEffectList = new LinkedList<>();
    private int mLocation[] = new int[2];


    private boolean mEffectFlag = false;

    private HeartTreeEffectForMeThread mHeartTreeEffectForMeThread;
    private MustacheEffectForMeThread mMustacheEffectForMeThread;
    private RabbitEffectForMeThread mRabbitEffectForMeThread;

    public static HeartTreeEffectForOtherThread mHeartTreeEffectForOtherThread;
    public static MustacheEffectForOtherThread mMustacheEffectForOtherThread;
    public static RabbitEffectForOtherThread mRabbitEffectForOtherThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.myNoActionBar);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_videocall_send);
        mMenuButton = (RelativeLayout) findViewById(R.id.menu_button);
        mEffectButton = (RelativeLayout) findViewById(R.id.effect_button);
        mMenuButton.bringToFront();
        mEffectButton.bringToFront();

        mPlayer = new MediaPlayer();
        mContext = getApplication();
        mEndCall = (ShimmerFrameLayout) findViewById(R.id.shimmer_end_call);
        mEndCall.setDuration(2000);
        mEndCall.setRepeatMode(ObjectAnimator.REVERSE);
        mEndCall.bringToFront();
        mEndCall.setOnClickListener(this);

        mBtnEffect = (LinearLayout) findViewById(R.id.btn_effect);
        mBtnExit = (LinearLayout) findViewById(R.id.btn_exit);
        mBtnRotation = (LinearLayout) findViewById(R.id.btn_rotation);


        mEffectHeart = (LinearLayout) findViewById(R.id.effect_heartTree);
        mEffectMustache = (LinearLayout) findViewById(R.id.effect_mustache);
        mEffectRabbit = (LinearLayout) findViewById(R.id.effect_rabbit);
        mEffectExit = (LinearLayout) findViewById(R.id.btn_effect_exit);

        mEffectList.add(mEffectHeart);
        mEffectList.add(mEffectMustache);
        mEffectList.add(mEffectRabbit);
        mEffectHeart.setOnClickListener(this);
        mEffectRabbit.setOnClickListener(this);
        mEffectMustache.setOnClickListener(this);
        mEffectExit.setOnClickListener(this);


        mBtnEffect.setOnClickListener(this);
        mBtnExit.setOnClickListener(this);
        mBtnRotation.setOnClickListener(this);

        Intent intent = this.getIntent();
        mName = intent.getStringExtra("name");
        mToken = intent.getStringExtra("token");
        mPhoneNumber = intent.getStringExtra("phoneNumber");
        mRealm = Realm.getDefaultInstance();

        MyInfo myInfo = mRealm.where(MyInfo.class).findFirst();

        createPlayRTCInstance();
        createChannel();
        Utill.startRingtone(mContext, mPlayer);
    }


    private void createPlayRTCInstance() {
        try {
            PlayRTCConfig setting = setPlayRTCConfiguration();

            createPlayRTCObserverInstance();
            //옵저버 생성

            mPlayrtc = PlayRTCFactory.createPlayRTC(setting, mPlayrtcObserver);
            Log.d(TAG, mPlayrtc.toString());

            //플레이 rtc객체 생성

        } catch (UnsupportedPlatformVersionException e) {
            e.printStackTrace();
        } catch (RequiredParameterMissingException e) {
            e.printStackTrace();
        }
    }

    private PlayRTCConfig setPlayRTCConfiguration() {
        PlayRTCConfig settings = PlayRTCFactory.createConfig();

        settings.setAndroidContext(getApplicationContext());

        settings.setPrevUserMediaEnable(true);
        settings.setProjectId(Constant.KEY);

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
        mPlayrtcObserver = new PlayRTCObserver() {
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


                if (mLocalView != null) {
                    long delayTime = 0;
                    mLocalView.show(delayTime);
                    playRTCMedia.setVideoRenderer(mLocalView.getVideoRenderer());

                    mMyVideoViewGroup = (RelativeLayout) findViewById(R.id.video_view_group);
                    mHeartTreeEffectForMeThread = new HeartTreeEffectForMeThread(mLocalView, mRemoteView, mContext, mMyVideoViewGroup);
                    mMustacheEffectForMeThread = new MustacheEffectForMeThread(mLocalView, mRemoteView, mContext, mMyVideoViewGroup);
                    mRabbitEffectForMeThread = new RabbitEffectForMeThread(mLocalView, mRemoteView, mContext, mMyVideoViewGroup);
                    //애니메이션 효과 있을시
                    makeHeartThread();
                    makeMustacheThread();
                    makeRabbitThread();
                    mLocalView.getHolder().addCallback(new SurfaceCallback());
                }

            }

            @Override
            public void onAddRemoteStream(PlayRTC playRTC, String s, String s1, PlayRTCMedia playRTCMedia) {
                changeLocalVidoeView();
                long delayTime = 100;
                mRemoteView.show(delayTime);
                mEndCall.setVisibility(View.GONE);

                playRTCMedia.setVideoRenderer(mRemoteView.getVideoRenderer());
                Utill.stopRington(mPlayer);
            }

            @Override
            public void onDisconnectChannel(PlayRTC playRTC, String s) {
                finish();
                if (mPlayrtc != null) {

                }
            }

            @Override
            public void onOtherDisconnectChannel(PlayRTC playRTC, String s, String s1) {
                finish();
                if (mPlayrtc != null) {
                    mPlayrtc.close();
                }
            }
        };
    }

    private void createVideoView() {
        RelativeLayout myVideoViewGroup = (RelativeLayout) findViewById(R.id.video_view_group);

        if (mLocalView != null) {
            return;
        }
        Point myViewDimensions = new Point();
        myViewDimensions.x = myVideoViewGroup.getWidth();
        myViewDimensions.y = myVideoViewGroup.getHeight();

        if (mRemoteView == null) {
            createRemoteVideoView(myViewDimensions, myVideoViewGroup);
        }
        if (mLocalView == null) {
            createLocalVideoView(myViewDimensions, myVideoViewGroup);
        }
    }

    private void createLocalVideoView(Point parentViewDimensions, RelativeLayout parentVideoViewGroup) {
        if (mLocalView == null) {
            Point myVideoSize = new Point();
            myVideoSize.x = parentViewDimensions.x;
            myVideoSize.y = parentViewDimensions.y;


            RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            mLocalView = new PlayRTCVideoView(parentVideoViewGroup.getContext());
            mLocalView.setMirror(false);


            mLocalView.setLayoutParams(param);
            parentVideoViewGroup.addView(mLocalView);
            parentVideoViewGroup.setOnTouchListener(this);
            mLocalView.setZOrderMediaOverlay(true);
            mLocalView.initRenderer();
            mLocalView.setOnTouchListener(this);
        }
    }

    private void changeLocalVidoeView() {
        RelativeLayout myVideoViewGroup = (RelativeLayout) findViewById(R.id.video_view_group);
        Point myViewDimensions = new Point();
        myViewDimensions.x = (int) (myVideoViewGroup.getWidth() * 0.3);
        myViewDimensions.y = (int) (myVideoViewGroup.getHeight() * 0.3);
        if (mLocalView == null) {
            Log.d(TAG, "로컬뷰가 생성되지 않음");
            return;
        }
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(myViewDimensions.x, myViewDimensions.y);
        param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        param.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        param.setMargins(30, 30, 30, 30);
        mLocalView.setLayoutParams(param);

        mLocalView.getLocationOnScreen(mLocation);
        Log.d(TAG, mLocation[0] + " " + mLocation[1]);
    }

    private void createRemoteVideoView(final Point parentViewDimensions, RelativeLayout parentVideoViewGroup) {

        if (mRemoteView == null) {
            Point myVideoSize = new Point();
            myVideoSize.x = parentViewDimensions.x;
            myVideoSize.y = parentViewDimensions.y;

            RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

            mRemoteView = new PlayRTCVideoView(parentVideoViewGroup.getContext());
            mRemoteView.setMirror(false);

            mRemoteView.setLayoutParams(param);

            parentVideoViewGroup.addView(mRemoteView);
            mRemoteView.initRenderer();
            mRemoteView.setOnTouchListener(this);
        }


    }

    private void createChannel() {
        try {

            JSONObject parameters = new JSONObject();
            // 채널정보를 정의한다.
            JSONObject channel = new JSONObject();

            try {
                channel.put("channelName", "");
                parameters.put("channel", channel);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONObject peer = new JSONObject();
            try {
                peer.put("uid", "");
                parameters.put("peer", peer);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "playRTC.createChannel " + parameters.toString());
            mPlayrtc.createChannel(parameters);
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
        String name = mName;
        String token = mToken;
        String phoneNumber = mPhoneNumber;
        String channel = channelId;
        sendFcmInDevice(name, token, phoneNumber, channel);
    }

    public void sendFcmInDevice(String name, String token, String phoneNumber, String channelId) {
        new SendFcmAsyncTask().execute(name, token, phoneNumber, channelId);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if (mEffectButton.getVisibility() == View.VISIBLE) {
            mEffectButton.setVisibility(View.GONE);

        } else {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                if (mMenuButton.getVisibility() == View.GONE) {
                    mMenuButton.setVisibility(View.VISIBLE);
                } else if (mMenuButton.getVisibility() == View.INVISIBLE) {
                    mMenuButton.setVisibility(View.VISIBLE);
                } else {
                    mMenuButton.setVisibility(View.GONE);
                }
                return true;
            }
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
        String sender = FirebaseInstanceId.getInstance().getToken();
        String url = "http://1-dot-boostcamp-jusung.appspot.com/effect";
        String point = "sender";
        String effect, check;
        switch (view.getId()) {
            case R.id.shimmer_end_call:

                mPlayrtc.deleteChannel();
                finish();
                break;
            case R.id.btn_exit:
                mPlayrtc.deleteChannel();
                finish();
                break;
            case R.id.btn_effect:
                mMenuButton.setVisibility(View.GONE);
                mEffectButton.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_rotation:
                //전환
                break;
            //쓰레드 종료 재시작 로직!!!
            case R.id.effect_heartTree:

                if (mHeartTreeEffectForMeThread != null) {
                    effect = "heart";
                    check = "start";
                    mHeartTreeEffectForMeThread.start();
                    new SenderAsync().execute(url, mToken, effect, sender, check, point);

                }

                btnSetEnabled(Constant.EFFECT_TREEHEART);
                break;
            case R.id.effect_mustache:

                if (mMustacheEffectForMeThread != null) {
                    effect = "mustache";
                    check = "start";
                    mMustacheEffectForMeThread.start();
                    new SenderAsync().execute(url, mToken, effect, sender, check, point);
                    btnSetEnabled(Constant.EFFECT_MUSTACHE);
                }
                break;
            case R.id.effect_rabbit:
                if (mRabbitEffectForMeThread != null) {
                    effect = "rabbit";
                    check = "start";
                    mRabbitEffectForMeThread.start();
                    new SenderAsync().execute(url, mToken, effect, sender, check, point);
                    btnSetEnabled(Constant.EFFECT_RABBIT);
                }

                break;
            case R.id.btn_effect_exit:
                exitThread();
                allBtnSetEnabled();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exitThread();
        exitOtherThread();
        Utill.stopRington(mPlayer);
        mLocalView = null;
        mRemoteView = null;

        if(mPlayer!=null)
            mPlayrtc.close();
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
        Log.d(TAG, "To:" + FirebaseInstanceId.getInstance().getToken());
        Log.d(TAG, "To:" + mToken);
        callLog.setFrom(mToken);
        callLog.setTime(time);
        mRealm.insert(callLog);
        mRealm.commitTransaction();
        if (mPlayrtc != null) {
            mPlayrtc.close();
        }
    }

    private class SurfaceCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {

        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        }
    }

    public void btnSetEnabled(int effect) {
        for(int i=0;i<mEffectList.size();i++){
            if(effect==i)
                Utill.setButtonUseColor(mContext,mEffectList.get(i), i);
            else
                Utill.setButtonNotUseColor(mContext,mEffectList.get(i),i);
            mEffectList.get(i).setEnabled(false);
        }
    }

    public void allBtnSetEnabled() {
        for(int i=0;i<mEffectList.size();i++){
            Utill.setButtonDefaultColor(mContext,mEffectList.get(i),i);
            mEffectList.get(i).setEnabled(true);
        }
    }

    public void exitThread() {
        String sender = FirebaseInstanceId.getInstance().getToken();
        String url = "http://1-dot-boostcamp-jusung.appspot.com/effect";
        String point = "sender";

        String effect, check;
        if (mHeartTreeEffectForMeThread != null) {
            effect = "heart";
            check = "end";

            mHeartTreeEffectForMeThread.stopThread();
            mHeartTreeEffectForMeThread.effectOff();

            mHeartTreeEffectForMeThread = new HeartTreeEffectForMeThread(mLocalView, mRemoteView, mContext, mMyVideoViewGroup);
            new SenderAsync().execute(url, mToken, effect, sender, check, point);
        }
        if (mMustacheEffectForMeThread != null) {
            effect = "mustache";
            check = "end";

            mMustacheEffectForMeThread.stopThread();
            mMustacheEffectForMeThread.effectOff();
            mMustacheEffectForMeThread = new MustacheEffectForMeThread(mLocalView, mRemoteView, mContext, mMyVideoViewGroup);
            new SenderAsync().execute(url, mToken, effect, sender, check, point);
        }

        if (mRabbitEffectForMeThread != null) {
            effect = "rabbit";
            check = "end";
            mRabbitEffectForMeThread.stopThread();
            mRabbitEffectForMeThread.effectOff();
            mRabbitEffectForMeThread = new RabbitEffectForMeThread(mLocalView, mRemoteView, mContext, mMyVideoViewGroup);
            new SenderAsync().execute(url, mToken, effect, sender, check, point);
        }

    }

    private class SenderAsync extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... data) {
            String url = data[0];
            String token = data[1];
            String effect = data[2];
            String sender = data[3];
            String check = data[4];
            String point = data[5];
            Utill.requestEffect(url, token, effect, sender, check, point);
            return null;
        }
    }

    public static void makeHeartThread() {
        mHeartTreeEffectForOtherThread = new HeartTreeEffectForOtherThread(mLocalView, mRemoteView, mContext, mMyVideoViewGroup);

    }

    public static void makeMustacheThread() {
        mMustacheEffectForOtherThread = new MustacheEffectForOtherThread(mLocalView, mRemoteView, mContext, mMyVideoViewGroup);

    }

    public static void makeRabbitThread() {
        mRabbitEffectForOtherThread = new RabbitEffectForOtherThread(mLocalView, mRemoteView, mContext, mMyVideoViewGroup);

    }

    public static void exitOtherThread() {
        if (mMustacheEffectForOtherThread != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    mMustacheEffectForOtherThread.effectOff();
                    mMustacheEffectForOtherThread.stopThread();
                    makeMustacheThread();
                }
            }.execute();

        }
        if (mRabbitEffectForOtherThread != null) {

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    mRabbitEffectForOtherThread.effectOff();
                    mRabbitEffectForOtherThread.stopThread();
                    makeRabbitThread();
                }
            }.execute();

        }
        if (mHeartTreeEffectForOtherThread != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    mHeartTreeEffectForOtherThread.effectOff();
                    mHeartTreeEffectForOtherThread.stopThread();
                    makeHeartThread();
                }
            }.execute();

        }
    }
}
