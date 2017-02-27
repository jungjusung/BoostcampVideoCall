package com.boostcamp.android.facestroy.effect;

import com.boostcamp.android.facestroy.R;
import com.boostcamp.android.facestroy.utill.SafeFaceDetector;

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

    private static final int MARGIN = 30;
    private static final String TAG = "MustacheEffectForMeThread";
    private PlayRTCVideoView mLocalView;
    private PlayRTCVideoView mRemoteView;
    private Bitmap mBitmap;
    private Context mContext;
    private boolean mThreadFlag = true;
    private FaceDetector mFaceDetector;
    private Detector<Face> mSafeDetector;
    private Queue<Bitmap> mBitmapQueue = new LinkedList<>();
    private PlayRTCVideoView.SnapshotObserver mSnapShot;
    private RelativeLayout mLayout;
    private RelativeLayout.LayoutParams mParam;
    private Effect mEffect;
    private Point mPoint;
    private int mLocation[] = new int[2];

    public MustacheEffectForMeThread(PlayRTCVideoView localView, PlayRTCVideoView remoteView, Context context, RelativeLayout relativeLayout) {
        this.mLocalView = localView;
        this.mRemoteView = remoteView;
        this.mContext = context;
        mLayout = relativeLayout;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        mPoint = new Point();
        display.getSize(mPoint);

        localView.getLocationOnScreen(mLocation);
        mEffect = new Effect(context, mLocation[0], mLocation[1], 150, 100);
        mEffect.setVisibility(View.GONE);
        mFaceDetector = new
                FaceDetector.Builder(context)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setTrackingEnabled(true)
                .build();
        mSafeDetector = new SafeFaceDetector(mFaceDetector);

        mSnapShot = new PlayRTCVideoView.SnapshotObserver() {

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onSnapshotImage(Bitmap image) {
                if (image != null) {
                    mBitmapQueue.add(image);
                }
            }
        };


        mEffect.setBackgroundResource(R.drawable.effect1);

        mParam = new RelativeLayout.LayoutParams(125, 80);

        mEffect.setLayoutParams(mParam);
        mLayout.addView(mEffect);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void run() {
        while (mThreadFlag) {
            try {
                if (mLocalView != null) {

                    makeSanpshot();
                    detectSnapShot();
                    sleep(50);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private synchronized void makeSanpshot() {
        if (mLocalView != null && mSnapShot != null && mThreadFlag) {
            mLocalView.snapshot(mSnapShot);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public synchronized void detectSnapShot() {

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
            mEffect.setVisibility(View.GONE);
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
                        int x = mPoint.x - (int) (mLayout.getWidth() * 0.3) - MARGIN;
                        int y = mPoint.y - (int) (mLayout.getHeight() * 0.3) - MARGIN;
                        mEffect.setX((int) (x + landmark.getPosition().x * 10));
                        mEffect.setY((int) (landmark.getPosition().y * 10));
                        mLayout.updateViewLayout(mEffect, mParam);
                        break;
                        //    Log.d(TAG, "인식 x: " + (int) (face.getLandmarks().get(2).getPosition().x * 5) + " y: " + (int) (face.getLandmarks().get(2).getPosition().y * 5));
                    }
                }
            }
        }
    }

    public void stopThread() {
        mBitmapQueue.clear();
        mThreadFlag = false;
        interrupt();

    }

    public void effectOff() {
        mEffect.setVisibility(View.GONE);
        mEffect = null;
    }

    public boolean isRunning() {
        return mThreadFlag;
    }

    public void effectOn() {
        mEffect.setVisibility(View.VISIBLE);
    }

    public void restartThread() {
        mThreadFlag = true;
        interrupt();
    }
}


