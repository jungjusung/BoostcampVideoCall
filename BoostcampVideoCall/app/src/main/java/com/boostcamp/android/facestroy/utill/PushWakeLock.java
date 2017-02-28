package com.boostcamp.android.facestroy.utill;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;

/**
 * Created by Jusung on 2017. 1. 26..
 */

public class PushWakeLock {
    private static PowerManager.WakeLock mCpuWakeLock;

    public static void acquireCpuWakeLock(Context context) {
        //화면이 off 상태일때 깨워준다.
        if (mCpuWakeLock != null) {
            return;
        }
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mCpuWakeLock = pm.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.ON_AFTER_RELEASE, "hello");
        mCpuWakeLock.acquire();
    }

    public static void releaseCpuLock() {
        if (mCpuWakeLock != null) {
            mCpuWakeLock.release();
            mCpuWakeLock = null;
        }
    }
}