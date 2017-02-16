package android.boostcamp.com.boostcampvideocall;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by Jusung on 2017. 1. 23..
 */

public class RealmInit extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }

}
