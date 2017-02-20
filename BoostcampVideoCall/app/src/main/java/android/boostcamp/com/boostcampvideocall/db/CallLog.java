package android.boostcamp.com.boostcampvideocall.db;


import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by Jusung on 2017. 2. 20..
 */

public class CallLog extends RealmObject{

    private int id;
    private String to;
    private String from;
    private Date date;
    private long time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
