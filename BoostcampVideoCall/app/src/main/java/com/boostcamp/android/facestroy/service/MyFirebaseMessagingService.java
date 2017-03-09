package com.boostcamp.android.facestroy.service;

import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.boostcamp.android.facestroy.activity.ReceiveCallActivity;
import com.boostcamp.android.facestroy.activity.ReceiveCheckActivity;
import com.boostcamp.android.facestroy.activity.VideoCallActvity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by Jusung on 2017. 2. 8..
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static String TAG = "MyFirebaseService";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        Map<String, String> data = remoteMessage.getData();
        String effect = data.get("effect");
        String check = data.get("check");
        String point = data.get("point");
        Log.d(TAG, effect + "");
        if (effect == null) {
            Intent intent = new Intent(this.getApplicationContext(), ReceiveCheckActivity.class);

            String channelId = data.get("channelId");
            String name = data.get("name");
            String phoneNumber = data.get("phoneNumber");
            String token = data.get("sender");
            String sender = data.get("sender");

            intent.putExtra("channelId", channelId);
            intent.putExtra("name", name);
            intent.putExtra("phoneNumber", phoneNumber);
            intent.putExtra("token", token);
            intent.putExtra("sender", sender);
            PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 1000, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            try {
                pendingIntent.send();
                Log.d(TAG, "pending");
            } catch (PendingIntent.CanceledException e) {
                Log.e(TAG, e.getMessage());
            }
        } else {
            switch (effect) {
                case "heart":
                    heartEffect(check, point);
                    break;
                case "mustache":
                    mustacheEffect(check, point);
                    break;
                case "rabbit":
                    rabbitEffect(check, point);
                    break;

            }
        }
    }

    public void heartEffect(String check, String point) {
        if (check.equals("start")) {
            if (point.equals("sender")) {
                if (ReceiveCallActivity.mHeartTreeEffectForOtherThread!=null) {
                    if(ReceiveCallActivity.mHeartTreeEffectForOtherThread.isRunning())
                        ReceiveCallActivity.mHeartTreeEffectForOtherThread.start();
                    else{
                        ReceiveCallActivity.mHeartTreeEffectForOtherThread.effectOn();
                        ReceiveCallActivity.mHeartTreeEffectForOtherThread.restartThread();
                    }
                }

            } else {
                // 수신
                if (VideoCallActvity.mHeartTreeEffectForOtherThread!=null) {
                    if(VideoCallActvity.mHeartTreeEffectForOtherThread.isRunning())
                        VideoCallActvity.mHeartTreeEffectForOtherThread.start();
                    else{
                        VideoCallActvity.mHeartTreeEffectForOtherThread.effectOn();
                        VideoCallActvity.mHeartTreeEffectForOtherThread.restartThread();
                    }
                }
            }
        } else {
            // 종료!!
            if (point.equals("sender")) {
                ReceiveCallActivity.exitOtherThread();
            } else {
                VideoCallActvity.exitOtherThread();
            }
        }
    }

    public void mustacheEffect(String check, String point) {
        if (check.equals("start")) {
            if (point.equals("sender")) {
                if (ReceiveCallActivity.mMustacheEffectForOtherThread!=null)
                    if(ReceiveCallActivity.mMustacheEffectForOtherThread.isRunning())
                        ReceiveCallActivity.mMustacheEffectForOtherThread.start();
                    else{
                        ReceiveCallActivity.mMustacheEffectForOtherThread.effectOn();
                        ReceiveCallActivity.mMustacheEffectForOtherThread.restartThread();
                    }

            } else {
                // 수신
                if (VideoCallActvity.mMustacheEffectForOtherThread!=null)
                    if(VideoCallActvity.mMustacheEffectForOtherThread.isRunning())
                        VideoCallActvity.mMustacheEffectForOtherThread.start();
                    else{
                        VideoCallActvity.mMustacheEffectForOtherThread.effectOn();
                        VideoCallActvity.mMustacheEffectForOtherThread.restartThread();
                    }
            }
        } else {
            // 종료!!
            if (point.equals("sender")) {
                ReceiveCallActivity.exitOtherThread();
            } else {
                VideoCallActvity.exitOtherThread();
            }
        }
    }

    public void rabbitEffect(String check, String point) {
        if (check.equals("start")) {
            if (point.equals("sender")) {
                if (ReceiveCallActivity.mRabbitEffectForOtherThread!=null)
                    if(ReceiveCallActivity.mRabbitEffectForOtherThread.isRunning())
                        ReceiveCallActivity.mRabbitEffectForOtherThread.start();
                    else{
                        ReceiveCallActivity.mRabbitEffectForOtherThread.effectOn();
                        ReceiveCallActivity.mRabbitEffectForOtherThread.restartThread();
                    }

            } else {
                // 수신
                if (VideoCallActvity.mRabbitEffectForOtherThread!=null)
                    if(VideoCallActvity.mRabbitEffectForOtherThread.isRunning())
                        VideoCallActvity.mRabbitEffectForOtherThread.start();
                    else{
                        VideoCallActvity.mRabbitEffectForOtherThread.effectOn();
                        VideoCallActvity.mRabbitEffectForOtherThread.restartThread();
                    }
            }
        } else {
            // 종료!!
            if (point.equals("sender")) {
                ReceiveCallActivity.exitOtherThread();
            } else {
                VideoCallActvity.exitOtherThread();
            }
        }
    }
}