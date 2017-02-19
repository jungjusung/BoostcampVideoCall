package android.boostcamp.com.boostcampvideocall.db;

import io.realm.RealmObject;

/**
 * Created by Jusung on 2017. 2. 16..
 */

public class MyInfo extends RealmObject {

    private String name;
    private String phoneNumber;
    private String token;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}
