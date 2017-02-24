package com.boostcamp.android.facestroy.effect;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.SparseArray;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.boostcamp.android.facestroy.R;
import com.boostcamp.android.facestroy.SafeFaceDetector;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;
import com.sktelecom.playrtc.util.ui.PlayRTCVideoView;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Jusung on 2017. 2. 14..
 */

public class HeartTreeEffectForOtherThread extends Thread {

    private PlayRTCVideoView localView;
    private static final String TAG = "HeartTreeEffectForOtherThread";
    private PlayRTCVideoView remoteView;
    private Bitmap mBitmap;
    private Context context;
    private boolean flag = true;
    FaceDetector faceDetector;
    Detector<Face> safeDetector;
    Queue<Bitmap> bitmapQueue = new LinkedList<>();
    Queue<Face> faceQueue = new LinkedList<>();

    private RelativeLayout mLayout;
    RelativeLayout.LayoutParams param;
    private Effect effect;

    public HeartTreeEffectForOtherThread(PlayRTCVideoView localView, PlayRTCVideoView remoteView, Context context, RelativeLayout relativeLayout) {
        this.localView = localView;
        this.remoteView = remoteView;
        this.context = context;
        mLayout = relativeLayout;
        WindowManager wm = (WindowManager)    context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        faceDetector = new
                FaceDetector.Builder(context)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setTrackingEnabled(true)
                .build();
        safeDetector = new SafeFaceDetector(faceDetector);




        effect = new Effect(context,0,0, 150, 100);

        Glide.with(context)
                .load(R.drawable.heart_tree)
                .asGif()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(effect);

        effect.setVisibility(View.INVISIBLE);
        param = new RelativeLayout.LayoutParams(500, 520);


        effect.setLayoutParams(param);
        mLayout.addView(effect);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void run() {
        while (flag) {
            try {
                if (remoteView != null) {
                    makeSanpshot();
                    detectSnapShot();
                    sleep(30);
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


        myBitmap = Bitmap.createScaledBitmap(myBitmap, myBitmap.getWidth() / 10, myBitmap.getHeight() / 10, true);
        Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
        SparseArray<Face> faces = safeDetector.detect(frame);
        myBitmap.recycle();

        if (faces.size() == 0) {
            if (effect.getVisibility() == View.VISIBLE)
                new myAsyncInVisible().execute();
            else
                return;
        }

        for (int i = 0; i < faces.size(); i++) {
            Face face = faces.valueAt(i);
            new myAsync().execute(face);
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
            //Log.d(TAG, "인식!!" + faces[0] + "");
            return faces[0];
        }

        @Override
        protected void onPostExecute(Face face) {

            super.onPostExecute(face);
            if (face != null) {
                effect.setVisibility(View.VISIBLE);
                int x1=0;
                int x2=0;
                for (Landmark landmark : face.getLandmarks()) {

                    if(x1!=0&&x2!=0){
                        param.width=x2-x1;
                        mLayout.updateViewLayout(effect, param);
                    }
                    if (landmark.getType() == Landmark.LEFT_EYE)
                        x1 = ((int) (landmark.getPosition().x * 10));

                    if (landmark.getType() == Landmark.RIGHT_EYE)
                        x2 = ((int) (landmark.getPosition().x * 10));

                    if (landmark.getType() == Landmark.NOSE_BASE) {

                        effect.setX((int) (landmark.getPosition().x * 10-700));
                        effect.setY((int) (landmark.getPosition().y * 10-900));
                        mLayout.updateViewLayout(effect, param);

                        //    Log.d(TAG, "인식 x: " + (int) (face.getLandmarks().get(2).getPosition().x * 5) + " y: " + (int) (face.getLandmarks().get(2).getPosition().y * 5));
                    }
                }
            }
        }
    }
    public void stopThread(){
        flag=false;
        interrupt();
    }
    public void effectOff(){
        effect.setVisibility(View.GONE);
    }

    public boolean isRunning(){
        return flag;
    }

    public void effectOn(){
        effect.setVisibility(View.VISIBLE);
    }
    public void restartThread(){
        flag=true;
        interrupt();
    }
}


