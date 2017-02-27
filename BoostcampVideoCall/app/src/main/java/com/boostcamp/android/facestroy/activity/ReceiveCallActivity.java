package com.boostcamp.android.facestroy.activity;

import com.boostcamp.android.facestroy.R;
import com.boostcamp.android.facestroy.db.CallLog;
import com.boostcamp.android.facestroy.db.Member;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.boostcamp.android.facestroy.effect.HeartTreeEffectForMeThread;
import com.boostcamp.android.facestroy.effect.HeartTreeEffectForOtherThread;
import com.boostcamp.android.facestroy.effect.MustacheEffectForMeThread;
import com.boostcamp.android.facestroy.effect.MustacheEffectForOtherThread;
import com.boostcamp.android.facestroy.effect.RabbitEffectForMeThread;
import com.boostcamp.android.facestroy.effect.RabbitEffectForOtherThread;
import com.boostcamp.android.facestroy.utill.Constant;
import com.boostcamp.android.facestroy.utill.Utill;
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

import org.json.JSONObject;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by Jusung on 2017. 2. 20..
 */

public class ReceiveCallActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {

    private static final String TAG = "ReceiveCallActivity";

    private PlayRTCObserver mPlayrtcObserver;
    private PlayRTC mPlayrtc;

    //뷰 관련 멤버 변수
    public static PlayRTCVideoView mReceiveLocalView;
    public static PlayRTCVideoView mReceiveRemoteView;
    private RelativeLayout mAfterLayout, mMenuButton, mEffectButton;
    private LinearLayout mBtnExit, mBtnEffect, mBtnRotation, mEffectHeart, mEffectRabbit, mEffectMustache, mEffectExit;
    private String mName, mPhoneNumber, mToken, mChannelId, mSender;
    private Realm mRealm;
    private Date mDate;
    private long mStartTime, mEndTime;
    private List<LinearLayout> mEffectList = new LinkedList<>();
    private HeartTreeEffectForMeThread mHeartTreeEffectForMeThread;
    private MustacheEffectForMeThread mMustacheEffectForMeThread;
    private RabbitEffectForMeThread mRabbitEffectForMeThread;

    public static Context mContext;
    public static RelativeLayout mMyVideoViewGroup;
    public static HeartTreeEffectForOtherThread mHeartTreeEffectForOtherThread;
    public static MustacheEffectForOtherThread mMustacheEffectForOtherThread;
    public static RabbitEffectForOtherThread mRabbitEffectForOtherThread;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.myNoActionBar);
        setContentView(R.layout.activity_videocall_confirm);
        mContext = getApplication();


        mMenuButton = (RelativeLayout) findViewById(R.id.menu_button);
        mAfterLayout = (RelativeLayout) findViewById(R.id.video_view_group);

        mEffectButton = (RelativeLayout) findViewById(R.id.effect_button);
        mEffectButton.bringToFront();

        mBtnEffect = (LinearLayout) findViewById(R.id.btn_effect);
        mBtnExit = (LinearLayout) findViewById(R.id.btn_exit);
        mBtnRotation = (LinearLayout) findViewById(R.id.btn_rotation);
        mBtnEffect.setOnClickListener(this);
        mBtnExit.setOnClickListener(this);
        mBtnRotation.setOnClickListener(this);


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


        Intent intent = getIntent();
        mName = intent.getStringExtra("name");
        mPhoneNumber = intent.getStringExtra("phoneNumber");
        mToken = intent.getStringExtra("token");
        mChannelId = intent.getStringExtra("channelId");
        mSender = intent.getStringExtra("sender");
        mRealm = Realm.getDefaultInstance();
        createPlayRTCInstance();
        connectChannel(mChannelId);
    }

    private void createPlayRTCInstance() {
        try {
            PlayRTCConfig setting = setPlayRTCConfiguration();

            createPlayRTCObserverInstance();
            //옵저버 생성

            mPlayrtc = PlayRTCFactory.createPlayRTC(setting, mPlayrtcObserver);

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

    private void createPlayRTCObserverInstance() {
        mPlayrtcObserver = new PlayRTCObserver() {
            @Override
            public void onConnectChannel(PlayRTC playRTC, String channelId, String reason) {
                super.onConnectChannel(playRTC, channelId, reason);
                if (channelId != null) {

                    long delayTime = 0;
                    mReceiveRemoteView.show(delayTime);
                    mReceiveLocalView.show(delayTime);
                    Log.d(TAG, channelId);
                    String channel = channelId.trim();
                    Log.d(TAG, channel);
                }
                if (reason.equals("connect")) {
                    mStartTime = System.currentTimeMillis();
                    Log.d(TAG, "connect");
                }
            }

            @Override
            public void onAddLocalStream(PlayRTC playRTC, PlayRTCMedia playRTCMedia) {
                long delayTime = 0;
                mReceiveLocalView.show(delayTime);
                playRTCMedia.setVideoRenderer(mReceiveLocalView.getVideoRenderer());
            }

            @Override
            public void onAddRemoteStream(PlayRTC playRTC, String s, String s1, PlayRTCMedia playRTCMedia) {
                long delayTime = 0;
                mReceiveRemoteView.show(delayTime);
                mMyVideoViewGroup = (RelativeLayout) findViewById(R.id.video_view_group);

                if (mReceiveLocalView != null) {
                    makeHeartThread();
                    makeMustacheThread();
                    makeRabbitThread();
                }
                playRTCMedia.setVideoRenderer(mReceiveRemoteView.getVideoRenderer());
            }

            @Override
            public void onDisconnectChannel(PlayRTC playRTC, String s) {
                finish();
                if (mPlayrtc != null) {
                    mPlayrtc.close();

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

    private void connectChannel(String responseChannel) {
        try {

            mPlayrtc.connectChannel(responseChannel.trim(), new JSONObject());

        } catch (RequiredConfigMissingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        createVideoView();

    }

    private void createVideoView() {
        if (mReceiveLocalView != null) {
            return;
        }
        Point myViewDimensions = new Point();
        myViewDimensions.x = mAfterLayout.getWidth();
        myViewDimensions.y = mAfterLayout.getHeight();
        if (mReceiveRemoteView == null) {
            createRemoteVideoView(myViewDimensions, mAfterLayout);
        }
        if (mReceiveLocalView == null) {
            createLocalVideoView(myViewDimensions, mAfterLayout);
        }
    }

    private void createLocalVideoView(Point parentViewDimensions, RelativeLayout parentVideoViewGroup) {
        if (mReceiveLocalView == null) {
            Point myVideoSize = new Point();
            myVideoSize.x = (int) (parentViewDimensions.x * 0.3);
            myVideoSize.y = (int) (parentViewDimensions.y * 0.3);

            RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(myVideoSize.x, myVideoSize.y);
            param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            param.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            param.setMargins(30, 30, 30, 30);

            mReceiveLocalView = new PlayRTCVideoView(parentVideoViewGroup.getContext());
            mReceiveLocalView.setMirror(false);


            mReceiveLocalView.setLayoutParams(param);
            parentVideoViewGroup.addView(mReceiveLocalView);
            mReceiveLocalView.setZOrderMediaOverlay(true);
            mReceiveLocalView.initRenderer();
            mReceiveLocalView.setOnTouchListener(this);
        }

    }

    private void createRemoteVideoView(final Point parentViewDimensions, RelativeLayout parentVideoViewGroup) {

        if (mReceiveRemoteView == null) {
            Point myVideoSize = new Point();
            myVideoSize.x = parentViewDimensions.x;
            myVideoSize.y = parentViewDimensions.y;

            RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            // Create the mReceiveRemoteView.
            mReceiveRemoteView = new PlayRTCVideoView(parentVideoViewGroup.getContext());
            mReceiveRemoteView.setMirror(false);
            // Set the layout parameters.
            mReceiveRemoteView.setLayoutParams(param);

            // Add the view to the videoViewGroup.
            parentVideoViewGroup.addView(mReceiveRemoteView);

            mReceiveRemoteView.initRenderer();
            mReceiveRemoteView.setOnTouchListener(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mReceiveLocalView = null;
        mReceiveRemoteView = null;

        mEndTime = System.currentTimeMillis();
        mDate = new Date(mEndTime);

        mRealm.beginTransaction();
        long time = mEndTime - mStartTime;
        Member member = mRealm.where(Member.class).equalTo("token", mSender).findFirst();
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

        if (mPlayrtc != null) {
            mPlayrtc.close();
        }
    }

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


    @Override
    public void onClick(View view) {
        String sender = FirebaseInstanceId.getInstance().getToken();
        String url = "http://1-dot-boostcamp-jusung.appspot.com/effect";
        String point = "receiver";
        String effect, check;

        switch (view.getId()) {
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
            case R.id.effect_heartTree:
                if (mHeartTreeEffectForMeThread != null) {
                    effect = "heart";
                    check = "start";
                    mHeartTreeEffectForMeThread.start();
                    new SenderAsync().execute(url, mToken, effect, sender, check, point);
                    btnSetEnabled(Constant.EFFECT_TREEHEART);
                }
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
        String point = "receiver";

        String effect, check;
        if (mHeartTreeEffectForMeThread != null) {
            effect = "heart";
            check = "end";

            mHeartTreeEffectForMeThread.stopThread();
            mHeartTreeEffectForMeThread.effectOff();
            mHeartTreeEffectForMeThread = new HeartTreeEffectForMeThread(mReceiveLocalView, mReceiveRemoteView, mContext, mMyVideoViewGroup);
            new SenderAsync().execute(url, mToken, effect, sender, check, point);
        }
        if (mMustacheEffectForMeThread != null) {
            effect = "mustache";
            check = "end";

            mMustacheEffectForMeThread.stopThread();
            mMustacheEffectForMeThread.effectOff();
            mMustacheEffectForMeThread = new MustacheEffectForMeThread(mReceiveLocalView, mReceiveRemoteView, mContext, mMyVideoViewGroup);
            new SenderAsync().execute(url, mToken, effect, sender, check, point);
        }

        if (mRabbitEffectForMeThread != null) {
            effect = "rabbit";
            check = "end";
            mRabbitEffectForMeThread.stopThread();
            mRabbitEffectForMeThread.effectOff();
            mRabbitEffectForMeThread = new RabbitEffectForMeThread(mReceiveLocalView, mReceiveRemoteView, mContext, mMyVideoViewGroup);
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

    public static void makeHeartThread() {
        mHeartTreeEffectForOtherThread = new HeartTreeEffectForOtherThread(mReceiveLocalView, mReceiveRemoteView, mContext, mMyVideoViewGroup);

    }

    public static void makeMustacheThread() {
        mMustacheEffectForOtherThread = new MustacheEffectForOtherThread(mReceiveLocalView, mReceiveRemoteView, mContext, mMyVideoViewGroup);

    }

    public static void makeRabbitThread() {
        mRabbitEffectForOtherThread = new RabbitEffectForOtherThread(mReceiveLocalView, mReceiveRemoteView, mContext, mMyVideoViewGroup);

    }


}

