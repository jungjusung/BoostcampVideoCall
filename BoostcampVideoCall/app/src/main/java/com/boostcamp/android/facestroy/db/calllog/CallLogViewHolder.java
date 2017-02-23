package com.boostcamp.android.facestroy.db.calllog;

import com.boostcamp.android.facestroy.db.CallLog;
import com.boostcamp.android.facestroy.db.Member;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import io.realm.Realm;

/**
 * Created by Jusung on 2017. 2. 20..
 */

public class CallLogViewHolder extends RecyclerView.ViewHolder{

    private static final String TAG="CallLogViewHolder";
    private ImageView mStartBackground,mEndBackground;
    private TextView mName,mPhoneNumber,mDate;
    private int[] backColors = {com.boostcamp.android.facestroy.R.drawable.image_back1, com.boostcamp.android.facestroy.R.drawable.image_back2, com.boostcamp.android.facestroy.R.drawable.image_back3, com.boostcamp.android.facestroy.R.drawable.image_back4, com.boostcamp.android.facestroy.R.drawable.image_back5};
    private Context mContext;
    private CallLogAdapter.ListItemClickListener mOnClickListener;
    private Realm mRealm;
    private List<CallLog> mList;
    public CallLogViewHolder(View itemView,Context mContext, CallLogAdapter.ListItemClickListener mOnClickListener) {
        super(itemView);
        this.mContext=mContext;
        this.mOnClickListener=mOnClickListener;
        mStartBackground=(ImageView)itemView.findViewById(com.boostcamp.android.facestroy.R.id.iv_back_start);
        mEndBackground=(ImageView)itemView.findViewById(com.boostcamp.android.facestroy.R.id.iv_back_end);
        mName=(TextView)itemView.findViewById(com.boostcamp.android.facestroy.R.id.tv_name);
        mPhoneNumber=(TextView)itemView.findViewById(com.boostcamp.android.facestroy.R.id.tv_phone);
        mDate=(TextView)itemView.findViewById(com.boostcamp.android.facestroy.R.id.tv_date);
        mRealm=Realm.getDefaultInstance();
        mList = mRealm.where(CallLog.class).findAll();

    }
    public void bind(int listIndex){
        Member member=mRealm.where(Member.class).equalTo("token",mList.get(listIndex).getFrom()).findFirst();
        if(member!=null) {
            mName.setText(member.getName());
            String day=computeDate(mList.get(listIndex).getDate());
            mDate.setText(day);
            mPhoneNumber.setText(member.getPhoneNumber());
            mStartBackground.setBackgroundResource(backColors[listIndex % 5]);
            mEndBackground.setBackgroundResource(backColors[listIndex % 5]);
        }
    }
    public String computeDate(Date before){
        Date date=new Date(System.currentTimeMillis());
        long diff = date.getTime() - before.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000);

        if(diffDays==0){
            String flag;
            if(before.getHours()>12)
                flag="오후 ";
            else
                flag="오전 ";
            return flag+before.getHours()+" : "+before.getMinutes();
        }else if(diffDays==1){
            return "어제";
        }else {
            switch (before.getDay()){
                case 1:
                    return "일요일";
                case 2:
                    return "월요일";
                case 3:
                    return "화요일";
                case 4:
                    return "수요일";
                case 5:
                    return "목요일";
                case 6:
                    return "금요일";
                case 7:
                    return "토요일";
            }
        }

        return "";
    }
}
