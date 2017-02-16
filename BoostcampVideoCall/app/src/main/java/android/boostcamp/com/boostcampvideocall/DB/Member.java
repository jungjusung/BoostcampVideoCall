package android.boostcamp.com.boostcampvideocall.DB;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Jusung on 2017. 2. 16..
 */

public class Member extends RealmObject{

    @PrimaryKey
    private int member_id;
    private String member_name;
    private String member_phoneNumber;
    private String member_token;

    public int getMember_id() {
        return member_id;
    }

    public void setMember_id(int member_id) {
        this.member_id = member_id;
    }

    public String getMember_name() {
        return member_name;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }

    public String getMember_phoneNumber() {
        return member_phoneNumber;
    }

    public void setMember_phoneNumber(String member_phoneNumber) {
        this.member_phoneNumber = member_phoneNumber;
    }

    public String getMember_token() {
        return member_token;
    }

    public void setMember_token(String member_token) {
        this.member_token = member_token;
    }
}
