package com.boostcamp.android.facestroy.effect;

import com.boostcamp.android.facestroy.R;
import com.boostcamp.android.facestroy.SafeFaceDetector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.SparseArray;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

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

public class MustacheEffectForMeThread extends Thread {

    private PlayRTCVideoView localView;
    private static final int MARGIN=30;
    private static final String TAG = "MustacheEffectForMeThread";
    private PlayRTCVideoView remoteView;
    private Bitmap mBitmap;
    private Context context;
    private boolean flag = true;
    FaceDetector faceDetector;
    Detector<Face> safeDetector;
    Queue<Bitmap> bitmapQueue = new LinkedList<>();
    Queue<Face> faceQueue = new LinkedList<>();
    private WindowManager windowManager;
    private RelativeLayout mLayout;
    RelativeLayout.LayoutParams param;
    private Effect effect;
    private int mLocation[]=new int[2];
    private Point mPoint;
    public MustacheEffectForMeThread(PlayRTCVideoView localView, PlayRTCVideoView remoteView, Context context, RelativeLayout relativeLayout) {
        this.localView = localView;
        this.remoteView = remoteView;
        this.context = context;
        mLayout = relativeLayout;
        WindowManager wm = (WindowManager)    context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        mPoint=new Point();
        display.getSize(mPoint);

        localView.getLocationOnScreen(mLocation);

        faceDetector = new
                FaceDetector.Builder(context)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setTrackingEnabled(true)
                .build();
        safeDetector = new SafeFaceDetector(faceDetector);

        effect = new Effect(context, mLocation[0], mLocation[1], 150, 100);
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
                    sleep(50);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public synchronized void makeSanpshot() {
        localView.snapshot(new PlayRTCVideoView.SnapshotObserver() {

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
                for (Landmark landmark : face.getLandmarks()) {
                    if (landmark.getType() == Landmark.NOSE_BASE) {
                        int x=mPoint.x-(int)(mLayout.getWidth()*0.3)-MARGIN;
                        int y=mPoint.y-(int)(mLayout.getHeight()*0.3)-MARGIN;
                        effect.setX((int) (x+landmark.getPosition().x * 10));
                        effect.setY((int) (landmark.getPosition().y * 10));
                        mLayout.updateViewLayout(effect, param);
                        break;
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
        effect.setVisibility(View.INVISIBLE);
    }

}


