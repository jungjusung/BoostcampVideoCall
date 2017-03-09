package com.boostcamp.android.facestroy.effect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
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

public class RabbitEffectForMeThread extends Thread {

    private static final String TAG = "RabbitEffect";
    private PlayRTCVideoView mLocalView;
    private PlayRTCVideoView mRemoteView;
    private Bitmap mBitmap;
    private Context mContext;
    private boolean mThreadFlag = true;
    private boolean mRestartFlag = true;
    private FaceDetector mFaceDetector;
    private Detector<Face> mSafeDetector;
    private Queue<Bitmap> mBitmapQueue = new LinkedList<>();
    private PlayRTCVideoView.SnapshotObserver mSnapShot;

    private RelativeLayout mLayout;
    private RelativeLayout.LayoutParams mParam;
    private Effect mEffect;
    private Point mPoint;

    private int mLocation[] = new int[2];

    public RabbitEffectForMeThread(PlayRTCVideoView localView, PlayRTCVideoView remoteView, Context context, RelativeLayout relativeLayout) {
        this.mLocalView = localView;
        this.mRemoteView = remoteView;
        this.mContext = context;
        mLayout = relativeLayout;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        mPoint = new Point();
        display.getSize(mPoint);

        localView.getLocationOnScreen(mLocation);
        mEffect = new Effect(context, 0, 0, 150, 100);
        mEffect.setVisibility(View.GONE);
        mSnapShot = new PlayRTCVideoView.SnapshotObserver() {

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onSnapshotImage(Bitmap image) {
                if (image != null && mRemoteView != null && mThreadFlag&&mLayout!=null) {
                    mBitmapQueue.add(image);
                }
            }
        };

        mFaceDetector = new
                FaceDetector.Builder(context)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setTrackingEnabled(true)
                .build();
        mSafeDetector = new SafeFaceDetector(mFaceDetector);




        Glide.with(context)
                .load(R.drawable.rabbit)
                .asGif()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(mEffect);


        mParam = new RelativeLayout.LayoutParams(500, 530);


        mEffect.setLayoutParams(mParam);
        mLayout.addView(mEffect);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void run() {
        while (mThreadFlag) {
            if (mRestartFlag) {
                try {
                    if (mLocalView != null && mThreadFlag) {
                        makeSanpshot();
                        detectSnapShot();
                        sleep(50);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                Thread.yield();
            }
        }
    }

    private void makeSanpshot() {
        if (mLocalView != null && mSnapShot != null && mThreadFlag) {
            mLocalView.snapshot(mSnapShot);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void detectSnapShot() {

        if (mBitmapQueue.size() == 0)
            return;

        Bitmap myBitmap = mBitmapQueue.poll();
        if (myBitmap == null)
            return;


        Log.d(TAG, myBitmap.getWidth() + " " + myBitmap.getHeight());
        myBitmap = Bitmap.createScaledBitmap(myBitmap, myBitmap.getWidth() / 2, myBitmap.getHeight() / 2, true);
        Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
        SparseArray<Face> faces = mSafeDetector.detect(frame);
        myBitmap.recycle();

        if (faces.size() == 0) {
            if (mEffect!=null&&mEffect.getVisibility() == View.VISIBLE)
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
            if (face != null && mThreadFlag) {
                mEffect.setVisibility(View.VISIBLE);
                for (Landmark landmark : face.getLandmarks()) {

                    if (landmark.getType() == Landmark.NOSE_BASE) {
                        int x = mPoint.x - (int) (mLayout.getWidth() * 0.3);
                        int y = mPoint.y - (int) (mLayout.getHeight() * 0.3) - 50;

                        mEffect.setX((int) (x + landmark.getPosition().x * 2 - 280));
                        mEffect.setY((int) (landmark.getPosition().y * 2) - 550);
                        mLayout.updateViewLayout(mEffect, mParam);

                        //    Log.d(TAG, "인식 x: " + (int) (face.getLandmarks().get(2).getPosition().x * 5) + " y: " + (int) (face.getLandmarks().get(2).getPosition().y * 5));
                    }
                }
            }
        }
    }

    public void stopThread() {
        mRestartFlag = false;
        interrupt();
    }
    public void restartThread() {
        mRestartFlag = true;
        interrupt();
    }

    public void effectOff() {
        mEffect.setVisibility(View.GONE);
    }

    public boolean isRunning() {
        return mRestartFlag;
    }

    public void effectOn() {
        mEffect.setVisibility(View.VISIBLE);
    }


}


