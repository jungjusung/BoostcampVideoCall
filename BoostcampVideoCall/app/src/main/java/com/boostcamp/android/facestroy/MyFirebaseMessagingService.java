package com.boostcamp.android.facestroy;

import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import android.widget.VideoView;

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
                if (ReceiveCallActivity.heartTreeEffectForOtherThread.getState() == Thread.State.NEW)
                    ReceiveCallActivity.heartTreeEffectForOtherThread.start();
                else if (ReceiveCallActivity.heartTreeEffectForOtherThread.getState() == Thread.State.TERMINATED) {
                    //재생성
                    ReceiveCallActivity.makeThread();
                    ReceiveCallActivity.heartTreeEffectForOtherThread.start();
                }
            } else {
                // 수신
                if (VideoCallActvity.heartTreeEffectForOtherThread.getState() == Thread.State.NEW)
                    VideoCallActvity.heartTreeEffectForOtherThread.start();
                else if (VideoCallActvity.heartTreeEffectForOtherThread.getState() == Thread.State.TERMINATED) {

                }
            }
        } else {
            // 종료!!
            if (point.equals("sender")) {
                ReceiveCallActivity.exitThread();
            } else {
//                if (VideoCallActvity.heartTreeEffectForOtherThread.getState() == Thread.State.RUNNABLE) {
//                    VideoCallActvity.heartTreeEffectForOtherThread.effectOff();
//                    VideoCallActvity.heartTreeEffectForOtherThread.stopThread();
//                }
            }
        }
    }

    public void mustacheEffect(String check, String point) {
        if (check.equals("start")) {
            if (point.equals("sender")) {
                if (ReceiveCallActivity.mustacheEffectForOtherThread.getState() == Thread.State.NEW)
                    ReceiveCallActivity.mustacheEffectForOtherThread.start();
                else if (ReceiveCallActivity.mustacheEffectForOtherThread.getState() == Thread.State.TERMINATED) {
                    //재생성
                    ReceiveCallActivity.makeThread();
                    ReceiveCallActivity.mustacheEffectForOtherThread.start();
                }
            } else {
                // 수신
                if (VideoCallActvity.mustacheEffectForOtherThread.getState() == Thread.State.NEW)
                    VideoCallActvity.mustacheEffectForOtherThread.stopThread();
                else if (VideoCallActvity.mustacheEffectForOtherThread.getState() == Thread.State.TERMINATED) {

                }
            }
        } else {
            // 종료!!
            if (point.equals("sender")) {
                ReceiveCallActivity.exitThread();
            } else {
//                    if (VideoCallActvity.mustacheEffectForOtherThread.getState() == Thread.State.RUNNABLE) {
//                        VideoCallActvity.mustacheEffectForOtherThread.effectOff();
//                        VideoCallActvity.mustacheEffectForOtherThread.stopThread();
//                    }
            }
        }
    }

    public void rabbitEffect(String check, String point) {
        if (check.equals("start")) {
            if (point.equals("sender")) {
                if (ReceiveCallActivity.rabbitEffectForOtherThread.getState() == Thread.State.NEW)
                    ReceiveCallActivity.rabbitEffectForOtherThread.start();
                else if (ReceiveCallActivity.rabbitEffectForOtherThread.getState() == Thread.State.TERMINATED) {
                    //재생성
                    ReceiveCallActivity.makeThread();
                    ReceiveCallActivity.rabbitEffectForOtherThread.start();
                }
            } else {
                // 수신
//                if (VideoCallActvity.rabbitEffectForOtherThread.getState() == Thread.State.NEW)
//                    VideoCallActvity.rabbitEffectForOtherThread.stopThread();
//                else if (VideoCallActvity.rabbitEffectForOtherThread.getState() == Thread.State.TERMINATED) {
//
//                }
            }
        } else {
            // 종료!!
            if (point.equals("sender")) {
                ReceiveCallActivity.exitThread();
            } else {
//                    if (VideoCallActvity.rabbitEffectForOtherThread!=null&&VideoCallActvity.rabbitEffectForOtherThread.getState() == Thread.State.RUNNABLE) {
//                        VideoCallActvity.rabbitEffectForOtherThread.effectOff();
//                        VideoCallActvity.rabbitEffectForOtherThread.stopThread();
//                    }
            }
        }
    }
}