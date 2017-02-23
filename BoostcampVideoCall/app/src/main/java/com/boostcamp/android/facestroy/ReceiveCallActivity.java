package com.boostcamp.android.facestroy;

import com.boostcamp.android.facestroy.db.CallLog;
import com.boostcamp.android.facestroy.db.Member;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

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

import io.realm.Realm;

/**
 * Created by Jusung on 2017. 2. 20..
 */

public class ReceiveCallActivity extends AppCompatActivity implements View.OnTouchListener{

    private static final String TAG = "ReceiveCallActivity";
    private static final String KEY = "60ba608a-e228-4530-8711-fa38004719c1";

    private PlayRTCObserver playrtcObserver = null;
    private PlayRTC playrtc = null;
    private Context context;

    //뷰 관련 멤버 변수
    private PlayRTCVideoView localView;
    private PlayRTCVideoView remoteView;

    private RelativeLayout mAfterLayout;
    private String mName,mPhoneNumber,mToken,mChannelId,mSender;

    private Realm mRealm;
    private long mStartTime;
    private long mEndTime;
    private Date mDate;
    private RelativeLayout mMenuButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.myNoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videocall_confirm);

        mMenuButton=(RelativeLayout)findViewById(R.id.menu_button);
        mAfterLayout=(RelativeLayout)findViewById(R.id.video_view_group);

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
                if (reason.equals("connect")) {
                    mStartTime=System.currentTimeMillis();
                    Log.d(TAG, "connect");
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

            Log.d(TAG, "접속하는 채널:" + responseChannel.trim());
            playrtc.connectChannel(responseChannel.trim(), new JSONObject());

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
        if (localView != null) {
            return;
        }
        Point myViewDimensions = new Point();
        myViewDimensions.x = mAfterLayout.getWidth();
        myViewDimensions.y = mAfterLayout.getHeight();
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
            localView.setOnTouchListener(this);
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
            remoteView.setOnTouchListener(this);
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

        if(playrtc!=null){
            playrtc.close();
        }
    }
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){

            if(mMenuButton.getVisibility()==View.INVISIBLE){
                mMenuButton.setVisibility(View.VISIBLE);
            }else{
                mMenuButton.setVisibility(View.INVISIBLE);
            }
            return true;
        }
        return super.onTouchEvent(motionEvent);
    }
}
