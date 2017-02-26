package com.boostcamp.android.facestroy.effect;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.SparseArray;
import android.view.View;
import android.widget.RelativeLayout;

import com.boostcamp.android.facestroy.R;
import com.boostcamp.android.facestroy.utill.SafeFaceDetector;
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


    private static final String TAG = "HeartTreeEffectForOtherThread";
    private PlayRTCVideoView mLocalView;
    private PlayRTCVideoView mRemoteView;
    private Bitmap mBitmap;
    private Context mContext;
    private boolean mThreadFlag = true;
    private FaceDetector mFaceDetector;
    private Detector<Face> mSafeDetector;
    private Queue<Bitmap> mBitmapQueue = new LinkedList<>();

    private RelativeLayout mLayout;
    private RelativeLayout.LayoutParams mParam;
    private Effect mEffect;

    public HeartTreeEffectForOtherThread(PlayRTCVideoView localView, PlayRTCVideoView remoteView, Context context, RelativeLayout relativeLayout) {
        this.mLocalView = localView;
        this.mRemoteView = remoteView;
        this.mContext = context;
        mLayout = relativeLayout;

        mFaceDetector = new
                FaceDetector.Builder(context)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setTrackingEnabled(true)
                .build();
        mSafeDetector = new SafeFaceDetector(mFaceDetector);

        mEffect = new Effect(context,0,0, 150, 100);

        Glide.with(context)
                .load(R.drawable.heart_tree)
                .asGif()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(mEffect);

        mEffect.setVisibility(View.INVISIBLE);
        mParam = new RelativeLayout.LayoutParams(500, 520);


        mEffect.setLayoutParams(mParam);
        mLayout.addView(mEffect);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void run() {
        while (mThreadFlag) {
            try {
                if (mRemoteView != null) {
                    makeSanpshot();
                    detectSnapShot();
                    sleep(30);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private synchronized void makeSanpshot() {
        mRemoteView.snapshot(new PlayRTCVideoView.SnapshotObserver() {

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onSnapshotImage(Bitmap image) {
                if (image != null) {
                    mBitmapQueue.add(image);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private synchronized void detectSnapShot() {

        if (mBitmapQueue.size() == 0)
            return;

        Bitmap myBitmap = mBitmapQueue.poll();
        if (myBitmap == null)
            return;


        myBitmap = Bitmap.createScaledBitmap(myBitmap, myBitmap.getWidth() / 10, myBitmap.getHeight() / 10, true);
        Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
        SparseArray<Face> faces = mSafeDetector.detect(frame);
        myBitmap.recycle();

        if (faces.size() == 0) {
            if (mEffect.getVisibility() == View.VISIBLE)
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
            mEffect.setVisibility(View.INVISIBLE);
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
                mEffect.setVisibility(View.VISIBLE);
                int x1=0;
                int x2=0;
                for (Landmark landmark : face.getLandmarks()) {

                    if(x1!=0&&x2!=0){
                        mParam.width=x2-x1;
                        mLayout.updateViewLayout(mEffect, mParam);
                    }
                    if (landmark.getType() == Landmark.LEFT_EYE)
                        x1 = ((int) (landmark.getPosition().x * 10));

                    if (landmark.getType() == Landmark.RIGHT_EYE)
                        x2 = ((int) (landmark.getPosition().x * 10));

                    if (landmark.getType() == Landmark.NOSE_BASE) {

                        mEffect.setX((int) (landmark.getPosition().x * 10-700));
                        mEffect.setY((int) (landmark.getPosition().y * 10-900));
                        mLayout.updateViewLayout(mEffect, mParam);

                        //    Log.d(TAG, "인식 x: " + (int) (face.getLandmarks().get(2).getPosition().x * 5) + " y: " + (int) (face.getLandmarks().get(2).getPosition().y * 5));
                    }
                }
            }
        }
    }
    public void stopThread(){
        mThreadFlag =false;
        interrupt();
    }
    public void effectOff(){
        mEffect.setVisibility(View.INVISIBLE);
    }


    public boolean isRunning(){
        return mThreadFlag;
    }

    public void effectOn(){
        mEffect.setVisibility(View.VISIBLE);
    }
    public void restartThread(){
        mThreadFlag =true;
        interrupt();
    }
}


