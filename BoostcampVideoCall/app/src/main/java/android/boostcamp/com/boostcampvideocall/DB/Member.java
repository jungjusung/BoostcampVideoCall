package android.boostcamp.com.boostcampvideocall.DB;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Jusung on 2017. 2. 16..
 */

public class Member extends RealmObject{

    @PrimaryKey
    private int id;
    private String name;
    private String phoneNumber;
    private String token;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
