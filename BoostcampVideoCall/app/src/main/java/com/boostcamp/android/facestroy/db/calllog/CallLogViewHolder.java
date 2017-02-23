package com.boostcamp.android.facestroy.db.calllog;

import com.boostcamp.android.facestroy.R;
import com.boostcamp.android.facestroy.db.CallLog;
import com.boostcamp.android.facestroy.db.Member;
import com.boostcamp.android.facestroy.db.MyInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;

/**
 * Created by Jusung on 2017. 2. 20..
 */

public class CallLogViewHolder extends RecyclerView.ViewHolder{

    private static final String TAG="CallLogViewHolder";

    private TextView mFromName,mToName,mDate;
    private Context mContext;
    private CircleImageView mFromImage,mToImage;
    private CallLogAdapter.ListItemClickListener mOnClickListener;
    private Realm mRealm;
    private List<CallLog> mList;
    public CallLogViewHolder(View itemView,Context mContext, CallLogAdapter.ListItemClickListener mOnClickListener) {
        super(itemView);
        this.mContext=mContext;
        this.mOnClickListener=mOnClickListener;

        mToName=(TextView)itemView.findViewById(R.id.tv_to_name);
        mToName.bringToFront();
        mFromName=(TextView)itemView.findViewById(R.id.tv_from_name);
        mToImage=(CircleImageView)itemView.findViewById(R.id.cv_image_to);
        mFromImage=(CircleImageView)itemView.findViewById(R.id.cv_image_from);
        mDate=(TextView)itemView.findViewById(R.id.tv_time);

        mRealm=Realm.getDefaultInstance();
        mList = mRealm.where(CallLog.class).findAll();

    }
    public void bind(int listIndex){
        Member memberFrom=mRealm.where(Member.class).equalTo("token",mList.get(listIndex).getFrom()).findFirst();
//        Log.d(TAG,"from_name"+memberFrom.getName());
//        Log.d(TAG,"from_url"+memberFrom.getUrl());
        Member memberTo=mRealm.where(Member.class).equalTo("token",mList.get(listIndex).getTo()).findFirst();
//        Log.d(TAG,"to_name"+memberTo.getName());
//        Log.d(TAG,"to_url"+memberTo.getUrl());
        if(memberTo!=null) {
            mToName.setText(memberTo.getName());
            Glide.with(mContext)
                    .load(memberTo.getUrl())
                    .error(R.drawable.sample)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mToImage);
        }else{
            MyInfo myInfo=mRealm.where(MyInfo.class).equalTo("token",mList.get(listIndex).getTo()).findFirst();
            if(myInfo!=null) {
                mToName.setText(myInfo.getName());
                Glide.with(mContext)
                        .load(myInfo.getUrl())
                        .error(R.drawable.sample)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mToImage);
            }
        }
        if(memberFrom!=null){
            mFromName.setText(memberFrom.getName());
            Glide.with(mContext)
                    .load(memberFrom.getUrl())
                    .error(R.drawable.sample)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mFromImage);
        }else{
            MyInfo myInfo=mRealm.where(MyInfo.class).equalTo("token",mList.get(listIndex).getFrom()).findFirst();
            if(myInfo!=null) {
                mFromName.setText(myInfo.getName());
                Glide.with(mContext)
                        .load(myInfo.getUrl())
                        .error(R.drawable.sample)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mFromImage);
            }
        }
        String day=computeDate(mList.get(listIndex).getDate());
        Log.d(TAG,"시간"+day);
        mDate.setText(day);
    }
    public String computeDate(Date before){
        //수정 필요
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
