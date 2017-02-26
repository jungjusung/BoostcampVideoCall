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
        String effect = data.get("mEffect");
        String check = data.get("check");
        String point = data.get("point");
        Log.d(TAG, effect + "");
        if (effect == null) {
            Intent intent = new Intent(this.getApplicationContext(), ReceiveCheckActivity.class);

            String channelId = data.get("channelId");
            String name = data.get("name");
            String phoneNumber = data.get("phoneNumber");
            String token = data.get("token");
            String sender = data.get("sender");
            Log.d(TAG, channelId + " " + name + " " + phoneNumber + " ");
            intent.putExtra("channelId", channelId);
            intent.putExtra("name", name);
            intent.putExtra("phoneNumber", phoneNumber);
            intent.putExtra("token", token);
            intent.putExtra("sender", sender);
            PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 1000, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //startActivity(intent);

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
                if (ReceiveCallActivity.mHeartTreeEffectForOtherThread.getState() == Thread.State.NEW)
                    ReceiveCallActivity.mHeartTreeEffectForOtherThread.start();
                else if (ReceiveCallActivity.mHeartTreeEffectForOtherThread.getState() == Thread.State.TERMINATED) {
                    //재생성
                    ReceiveCallActivity.makeThread();
                    ReceiveCallActivity.mHeartTreeEffectForOtherThread.start();
                }
            } else {
                // 수신
                if (VideoCallActvity.mHeartTreeEffectForOtherThread.getState() == Thread.State.NEW)
                    VideoCallActvity.mHeartTreeEffectForOtherThread.start();
                else if (VideoCallActvity.mHeartTreeEffectForOtherThread.getState() == Thread.State.TERMINATED) {

                }
            }
        } else {
            // 종료!!
            if (point.equals("sender")) {
                ReceiveCallActivity.exitThread();
            } else {
//                if (VideoCallActvity.mHeartTreeEffectForOtherThread.getState() == Thread.State.RUNNABLE) {
//                    VideoCallActvity.mHeartTreeEffectForOtherThread.effectOff();
//                    VideoCallActvity.mHeartTreeEffectForOtherThread.stopThread();
//                }
            }
        }
    }

    public void mustacheEffect(String check, String point) {
        if (check.equals("start")) {
            if (point.equals("sender")) {
                if (ReceiveCallActivity.mMustacheEffectForOtherThread.getState() == Thread.State.NEW)
                    ReceiveCallActivity.mMustacheEffectForOtherThread.start();
                else if (ReceiveCallActivity.mMustacheEffectForOtherThread.getState() == Thread.State.TERMINATED) {
                    //재생성
                    ReceiveCallActivity.makeThread();
                    ReceiveCallActivity.mMustacheEffectForOtherThread.start();
                }
            } else {
                // 수신
                if (VideoCallActvity.mMustacheEffectForOtherThread.getState() == Thread.State.NEW)
                    VideoCallActvity.mMustacheEffectForOtherThread.stopThread();
                else if (VideoCallActvity.mMustacheEffectForOtherThread.getState() == Thread.State.TERMINATED) {

                }
            }
        } else {
            // 종료!!
            if (point.equals("sender")) {
                ReceiveCallActivity.exitThread();
            } else {
//                    if (VideoCallActvity.mMustacheEffectForOtherThread.getState() == Thread.State.RUNNABLE) {
//                        VideoCallActvity.mMustacheEffectForOtherThread.effectOff();
//                        VideoCallActvity.mMustacheEffectForOtherThread.stopThread();
//                    }
            }
        }
    }

    public void rabbitEffect(String check, String point) {
        if (check.equals("start")) {
            if (point.equals("sender")) {
                if (ReceiveCallActivity.mRabbitEffectForOtherThread.getState() == Thread.State.NEW)
                    ReceiveCallActivity.mRabbitEffectForOtherThread.start();
                else if (ReceiveCallActivity.mRabbitEffectForOtherThread.getState() == Thread.State.TERMINATED) {
                    //재생성
                    ReceiveCallActivity.makeThread();
                    ReceiveCallActivity.mRabbitEffectForOtherThread.start();
                }
            } else {
                // 수신
//                if (VideoCallActvity.mRabbitEffectForOtherThread.getState() == Thread.State.NEW)
//                    VideoCallActvity.mRabbitEffectForOtherThread.stopThread();
//                else if (VideoCallActvity.mRabbitEffectForOtherThread.getState() == Thread.State.TERMINATED) {
//
//                }
            }
        } else {
            // 종료!!
            if (point.equals("sender")) {
                ReceiveCallActivity.exitThread();
            } else {
//                    if (VideoCallActvity.mRabbitEffectForOtherThread!=null&&VideoCallActvity.mRabbitEffectForOtherThread.getState() == Thread.State.RUNNABLE) {
//                        VideoCallActvity.mRabbitEffectForOtherThread.effectOff();
//                        VideoCallActvity.mRabbitEffectForOtherThread.stopThread();
//                    }
            }
        }
    }
}