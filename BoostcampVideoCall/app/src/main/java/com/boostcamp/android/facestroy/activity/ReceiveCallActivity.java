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

public class ReceiveCallActivity extends AppCompatActivity implements View.OnTouchListener,View.OnClickListener{

    private static final String TAG = "ReceiveCallActivity";

    private PlayRTCObserver mPlayrtcObserver;
    private PlayRTC mPlayrtc;

    //뷰 관련 멤버 변수
    public static PlayRTCVideoView mLocalView;
    public static PlayRTCVideoView mRemoteView;
    private RelativeLayout mAfterLayout,mMenuButton,mEffectButton;
    private LinearLayout mBtnExit,mBtnEffect, mBtnRotation,mEffectHeart, mEffectRabbit, mEffectMustache, mEffectExit;
    private String mName,mPhoneNumber,mToken,mChannelId,mSender;
    private Realm mRealm;
    private Date mDate;
    private long mStartTime,mEndTime;
    private List<LinearLayout> mEffectList =new LinkedList<>();
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


        mMenuButton=(RelativeLayout)findViewById(R.id.menu_button);
        mAfterLayout=(RelativeLayout)findViewById(R.id.video_view_group);

        mEffectButton=(RelativeLayout) findViewById(R.id.effect_button);
        mEffectButton.bringToFront();

        mBtnEffect=(LinearLayout)findViewById(R.id.btn_effect);
        mBtnExit=(LinearLayout)findViewById(R.id.btn_exit);
        mBtnRotation =(LinearLayout)findViewById(R.id.btn_rotation);
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

        Intent intent=getIntent();
        mName=intent.getStringExtra("name");
        mPhoneNumber=intent.getStringExtra("phoneNumber");
        mToken=intent.getStringExtra("token");
        mChannelId=intent.getStringExtra("channelId");
        mSender=intent.getStringExtra("sender");
        mRealm=Realm.getDefaultInstance();
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
                    mRemoteView.show(delayTime);
                    mLocalView.show(delayTime);
                    Log.d(TAG, channelId);
                    String channel = channelId.trim();
                    Log.d(TAG, channel);
                }
                if (reason.equals("connect")) {
                    mStartTime=System.currentTimeMillis();
                    Log.d(TAG, "connect");
                }
            }

            @Override
            public void onAddLocalStream(PlayRTC playRTC, PlayRTCMedia playRTCMedia) {
                long delayTime = 0;
                mLocalView.show(delayTime);
                playRTCMedia.setVideoRenderer(mLocalView.getVideoRenderer());
            }

            @Override
            public void onAddRemoteStream(PlayRTC playRTC, String s, String s1, PlayRTCMedia playRTCMedia) {
                long delayTime = 0;
                mRemoteView.show(delayTime);
                mMyVideoViewGroup = (RelativeLayout) findViewById(R.id.video_view_group);
                if (mLocalView != null) {
                    makeThread();
                }
                playRTCMedia.setVideoRenderer(mRemoteView.getVideoRenderer());
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
        if (mLocalView != null) {
            return;
        }
        Point myViewDimensions = new Point();
        myViewDimensions.x = mAfterLayout.getWidth();
        myViewDimensions.y = mAfterLayout.getHeight();
        if (mRemoteView == null) {
            createRemoteVideoView(myViewDimensions, mAfterLayout);
        }
        if (mLocalView == null) {
            createLocalVideoView(myViewDimensions, mAfterLayout);
        }
    }

    private void createLocalVideoView(Point parentViewDimensions, RelativeLayout parentVideoViewGroup) {
        if (mLocalView == null) {
            Point myVideoSize = new Point();
            myVideoSize.x = (int) (parentViewDimensions.x * 0.3);
            myVideoSize.y = (int) (parentViewDimensions.y * 0.3);

            RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(myVideoSize.x, myVideoSize.y);
            param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            param.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            param.setMargins(30, 30, 30, 30);

            mLocalView = new PlayRTCVideoView(parentVideoViewGroup.getContext());
            mLocalView.setMirror(false);


            mLocalView.setLayoutParams(param);
            parentVideoViewGroup.addView(mLocalView);
            mLocalView.setZOrderMediaOverlay(true);
            mLocalView.initRenderer();
            mLocalView.setOnTouchListener(this);
        }

    }

    private void createRemoteVideoView(final Point parentViewDimensions, RelativeLayout parentVideoViewGroup) {

        if (mRemoteView == null) {
            Point myVideoSize = new Point();
            myVideoSize.x = parentViewDimensions.x;
            myVideoSize.y = parentViewDimensions.y;

            RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            // Create the mRemoteView.
            mRemoteView = new PlayRTCVideoView(parentVideoViewGroup.getContext());
            mRemoteView.setMirror(false);
            // Set the layout parameters.
            mRemoteView.setLayoutParams(param);

            // Add the view to the videoViewGroup.
            parentVideoViewGroup.addView(mRemoteView);

            mRemoteView.initRenderer();
            mRemoteView.setOnTouchListener(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mEndTime=System.currentTimeMillis();
        mDate=new Date(mEndTime);

        mRealm.beginTransaction();
        long time=mEndTime-mStartTime;
        Member member=mRealm.where(Member.class).equalTo("token",mSender).findFirst();
        if(member!=null) {
            member.setCount(member.getCount() + 1);
            member.setTime(member.getTime() + time);
            mRealm.copyToRealmOrUpdate(member);
        }

        int sequenceNum=0;
        if(mRealm.where(CallLog.class).max("id")!=null)
            sequenceNum=mRealm.where(CallLog.class).max("id").intValue()+1;
        CallLog callLog=new CallLog();
        callLog.setId(sequenceNum);
        callLog.setDate(mDate);
        callLog.setTo(FirebaseInstanceId.getInstance().getToken());
        callLog.setFrom(mToken);
        callLog.setTime(time);
        mRealm.insert(callLog);
        mRealm.commitTransaction();

        if(mPlayrtc !=null){
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
                    mMenuButton.setVisibility(View.INVISIBLE);
                }
                return true;
            }
        }
        return super.onTouchEvent(motionEvent);
    }



    @Override
    public void onClick(View view) {
        String from=FirebaseInstanceId.getInstance().getToken();
        String url="http://1-dot-boostcamp-jusung.appspot.com/mEffect";
        String effect,check;
        String postion="receiver";
        switch (view.getId()) {
            case R.id.btn_exit:
                mPlayrtc.deleteChannel();
                finish();
                break;
            case R.id.btn_effect:
                //효과
                break;
            case R.id.btn_rotation:
                //전환
                break;
            case R.id.effect_heartTree:

                if (mHeartTreeEffectForMeThread.getState() == Thread.State.NEW) {
                    effect="heart";
                    check="start";
                    Utill.requestEffect(url,mToken,effect,from,check,postion);
                    mHeartTreeEffectForMeThread.start();
                } else if (mHeartTreeEffectForMeThread.getState() == Thread.State.RUNNABLE) {
                    effect="heart";
                    check="end";
                    Utill.requestEffect(url,mToken,effect,from,check,postion);

                    mHeartTreeEffectForMeThread.effectOff();
                    mHeartTreeEffectForMeThread.stopThread();
                }else if(mHeartTreeEffectForMeThread.getState() == Thread.State.TERMINATED){
                    mHeartTreeEffectForMeThread.effectOn();
                    mHeartTreeEffectForMeThread.restartThread();
                }
                btnSetEnabled(Constant.EFFECT_TREEHEART);
                break;
            case R.id.effect_mustache:

                if (mMustacheEffectForMeThread.getState() == Thread.State.NEW) {
                    effect="mustache";
                    check="start";
                    Utill.requestEffect(url,mToken,effect,from,check,postion);
                    mMustacheEffectForMeThread.start();
                } else if (mMustacheEffectForMeThread.getState() == Thread.State.RUNNABLE) {
                    effect="mustache";
                    check="end";
                    Utill.requestEffect(url,mToken,effect,from,check,postion);
                    mMustacheEffectForMeThread.effectOff();
                    mMustacheEffectForMeThread.stopThread();
                }else if(mMustacheEffectForMeThread.getState() == Thread.State.TERMINATED){
                    mMustacheEffectForMeThread.effectOn();
                    mMustacheEffectForMeThread.restartThread();
                }
                btnSetEnabled(Constant.EFFECT_MUSTACHE);
                break;
            case R.id.effect_rabbit:
                if (mRabbitEffectForMeThread.getState() == Thread.State.NEW) {
                    effect="rabbit";
                    check="start";
                    Utill.requestEffect(url,mToken,effect,from,check,postion);
                    mMustacheEffectForMeThread.effectOff();
                    mRabbitEffectForMeThread.start();
                } else if (mRabbitEffectForMeThread.getState() == Thread.State.RUNNABLE) {
                    effect="rabbit";
                    check="end";
                    Utill.requestEffect(url,mToken,effect,from,check,postion);
                    mRabbitEffectForMeThread.effectOff();
                    mRabbitEffectForMeThread.stopThread();
                }else if(mRabbitEffectForMeThread.getState() == Thread.State.TERMINATED){
                    mRabbitEffectForMeThread =new RabbitEffectForMeThread(mLocalView, mRemoteView, mContext, mMyVideoViewGroup);
                    mRabbitEffectForMeThread.start();
                }
                btnSetEnabled(Constant.EFFECT_RABBIT);
                break;
            case R.id.btn_effect_exit:
                exitThread();
                allBtnSetEnabled();
                break;
        }
    }
    public void btnSetEnabled(int flag){
        for(int i=0;i<mEffectList.size();i++){
            if(flag==i)
                mEffectList.get(i).setEnabled(true);

            mEffectList.get(i).setEnabled(false);
        }
    }
    public void allBtnSetEnabled(){
        for(LinearLayout btn:mEffectList)
            btn.setEnabled(true);
    }

    public static void exitThread() {

        if (mMustacheEffectForOtherThread != null) {
            new AsyncTask<Void,Void,Void>(){
                @Override
                protected Void doInBackground(Void... voids) {
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    mMustacheEffectForOtherThread.effectOff();
                }
            }.execute();
            mMustacheEffectForOtherThread.stopThread();
        }
        if (mRabbitEffectForOtherThread != null) {

            new AsyncTask<Void,Void,Void>(){
                @Override
                protected Void doInBackground(Void... voids) {
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    mRabbitEffectForOtherThread.effectOff();
                }
            }.execute();
            mRabbitEffectForOtherThread.stopThread();
        }
        if (mHeartTreeEffectForOtherThread != null) {
            new AsyncTask<Void,Void,Void>(){
                @Override
                protected Void doInBackground(Void... voids) {
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    mHeartTreeEffectForOtherThread.effectOff();
                }
            }.execute();

            mHeartTreeEffectForOtherThread.stopThread();
        }
    }
    public static void makeThread(){
        mMustacheEffectForOtherThread = new MustacheEffectForOtherThread(mLocalView, mRemoteView, mContext, mMyVideoViewGroup);
        mHeartTreeEffectForOtherThread = new HeartTreeEffectForOtherThread(mLocalView, mRemoteView, mContext, mMyVideoViewGroup);
        mRabbitEffectForOtherThread = new RabbitEffectForOtherThread(mLocalView, mRemoteView, mContext, mMyVideoViewGroup);
    }

}
