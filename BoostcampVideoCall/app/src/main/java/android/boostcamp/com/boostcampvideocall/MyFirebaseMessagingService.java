package android.boostcamp.com.boostcampvideocall;

import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

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
//        Map<String, String> data = remoteMessage.getData();
//        Intent intent=new Intent(this.getApplicationContext(),TestActivity.class);
//        Log.d(TAG,data.toString());
//        String channelId=data.get("channelId");
//        Log.d(TAG,channelId);
//        intent.putExtra("channelId",channelId);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent, 0);
//        try {
//            pendingIntent.send();
//        } catch (PendingIntent.CanceledException e) {
//            e.printStackTrace();
//        }
//        if(data!=null){
//            Log.d(TAG, "data = " + data);
//        }
//
//        RemoteMessage.Notification noti = remoteMessage.getNotification();
//        if(noti != null){
//            String title = noti.getTitle();
//            Log.d(TAG, "title = " + title);
//
//            String body = noti.getBody();
//            //Toast.makeText(this, body, Toast.LENGTH_SHORT).show();
//            Log.d(TAG, "body = " + body);
//        }
    }
}
