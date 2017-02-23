package com.boostcamp.android.facestroy;

import com.boostcamp.android.facestroy.effect.EffectFirst;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.sktelecom.playrtc.util.ui.PlayRTCVideoView;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Jusung on 2017. 2. 14..
 */

public class FaceTrackerThread extends Thread {

    private PlayRTCVideoView localView;
    private static String TAG;
    private PlayRTCVideoView remoteView;
    private Bitmap mBitmap;
    private Context context;
    boolean flag = true;
    FaceDetector faceDetector;
    Detector<Face> safeDetector;
    Queue<Bitmap> bitmapQueue = new LinkedList<>();

    private WindowManager windowManager;
    private RelativeLayout mLayout;
    RelativeLayout.LayoutParams param;
    private EffectFirst effect;


    public FaceTrackerThread(PlayRTCVideoView localView, PlayRTCVideoView remoteView, Context context, RelativeLayout relativeLayout) {
        this.localView = localView;
        this.remoteView = remoteView;
        this.context = context;
        mLayout = relativeLayout;
        TAG = this.getClass().getName();
        Log.d(TAG, "쓰레드");

        faceDetector = new
                FaceDetector.Builder(context)
                .setTrackingEnabled(true)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();
        safeDetector = new SafeFaceDetector(faceDetector);


        effect = new EffectFirst(context, 0, 0, 150, 100);
        effect.setBackgroundResource(R.drawable.effect1);
        effect.setVisibility(View.INVISIBLE);
        param = new RelativeLayout.LayoutParams(250, 120);
        effect.setLayoutParams(param);
        mLayout.addView(effect);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void run() {
        while (flag) {
            try {
                if (localView != null) {

                    makeSanpshot();
                    detectSnapShot();
                    sleep(10);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public synchronized void makeSanpshot() {
        remoteView.snapshot(new PlayRTCVideoView.SnapshotObserver() {

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onSnapshotImage(Bitmap image) {
                if (image != null) {
                    bitmapQueue.add(image);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public synchronized void detectSnapShot() {

        if (bitmapQueue.size() == 0)
            return;
        Bitmap myBitmap = bitmapQueue.poll();
        if (myBitmap == null)
            return;

        myBitmap = Bitmap.createScaledBitmap(myBitmap, myBitmap.getWidth() / 6, myBitmap.getHeight() / 6, true);
        Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
        SparseArray<Face> faces = safeDetector.detect(frame);
        if (faces.size() > 0) {
            new myAsync().execute(faces.get(0));
        }else{
            new myAsyncInVisible().execute();
        }
    }
    private class myAsyncInVisible extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            effect.setVisibility(View.INVISIBLE);
        }
    }
    private class myAsync extends AsyncTask<Face, Face, Face> {
        @Override
        protected Face doInBackground(Face... faces) {
            return faces[0];
        }

        @Override
        protected void onPostExecute(Face face) {

            if(face!=null)
                if(effect.getVisibility()==View.INVISIBLE)
                    effect.setVisibility(View.VISIBLE);

            effect.setX((int) (face.getLandmarks().get(2).getPosition().x * 6) - 50);
            effect.setY((int) (face.getLandmarks().get(2).getPosition().y) * 6);
            mLayout.updateViewLayout(effect, param);
            Log.d(TAG, "인식 x: " + (int) (face.getLandmarks().get(2).getPosition().x * 6) + " y: " + (int) (face.getLandmarks().get(2).getPosition().y * 6));
        }
    }
}


