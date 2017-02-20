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

        Map<String, String> data = remoteMessage.getData();
        Intent intent=new Intent(this.getApplicationContext(),ReceiveCheckActivity.class);

        String channelId=data.get("channelId");

        String name=data.get("name");
        String phoneNumber=data.get("phoneNumber");
        String token=data.get("token");
        String sender=data.get("sender");
        Log.d(TAG,channelId+" "+name+" "+phoneNumber+" ");
        intent.putExtra("channelId",channelId);
        intent.putExtra("name",name);
        intent.putExtra("phoneNumber",phoneNumber);
        intent.putExtra("token",token);
        intent.putExtra("sender",sender);
        PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(),1000, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //startActivity(intent);

        try {
            pendingIntent.send();
            Log.d(TAG,"pending");
        } catch (PendingIntent.CanceledException e) {
            Log.e(TAG,e.getMessage());
        }

    }
}