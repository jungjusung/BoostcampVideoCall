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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;

/**
 * Created by Jusung on 2017. 2. 20..
 */

public class CallLogViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "CallLogViewHolder";
    private static final String[] DAY_OF_WEEK = {"", "일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"};
    private TextView mFromName, mToName, mDate;
    private CircleImageView mFromImage, mToImage;
    private Realm mRealm;
    private Context mContext;
    private List<CallLog> mList;

    public CallLogViewHolder(View itemView, Context mContext) {
        super(itemView);
        this.mContext = mContext;

        mToName = (TextView) itemView.findViewById(R.id.tv_to_name);
        mFromName = (TextView) itemView.findViewById(R.id.tv_from_name);
        mToImage = (CircleImageView) itemView.findViewById(R.id.cv_image_to);
        mFromImage = (CircleImageView) itemView.findViewById(R.id.cv_image_from);
        mDate = (TextView) itemView.findViewById(R.id.tv_time);

        mToName.bringToFront();
        mRealm = Realm.getDefaultInstance();
        mList = mRealm.where(CallLog.class).findAll();
    }

    public void bind(int listIndex) {
        Member memberFrom = mRealm.where(Member.class).equalTo("token", mList.get(listIndex).getFrom()).findFirst();
        Member memberTo = mRealm.where(Member.class).equalTo("token", mList.get(listIndex).getTo()).findFirst();
        if (memberTo != null) {
            mToName.setText(memberTo.getName());
            Glide.with(mContext)
                    .load(memberTo.getUrl())
                    .error(R.drawable.sample)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mToImage);
        } else {
            MyInfo myInfo = mRealm.where(MyInfo.class).equalTo("token", mList.get(listIndex).getTo()).findFirst();
            if (myInfo != null) {
                mToName.setText(myInfo.getName());
                Glide.with(mContext)
                        .load(myInfo.getUrl())
                        .error(R.drawable.sample)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mToImage);
            }
        }
        if (memberFrom != null) {
            mFromName.setText(memberFrom.getName());
            Glide.with(mContext)
                    .load(memberFrom.getUrl())
                    .error(R.drawable.sample)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mFromImage);
        } else {
            MyInfo myInfo = mRealm.where(MyInfo.class).equalTo("token", mList.get(listIndex).getFrom()).findFirst();
            if (myInfo != null) {
                mFromName.setText(myInfo.getName());
                Glide.with(mContext)
                        .load(myInfo.getUrl())
                        .error(R.drawable.sample)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mFromImage);
            }
        }
        String day = getStringDate(mList.get(listIndex).getDate());
        mDate.setText(day);
    }

    public String getStringDate(Date date) {
        //수정 필요
        Calendar today = Calendar.getInstance();
        Calendar before = Calendar.getInstance();
        before.setTime(date);

        long diffDays = (today.getTimeInMillis() - before.getTimeInMillis()) / (24 * 60 * 60 * 1000);

        if (diffDays == 0) {
            String flag;
            String timeFlag = mContext.getResources().getString(R.string.time_flag);
            if (before.get(Calendar.HOUR) > 12)
                flag = mContext.getResources().getString(R.string.pm);
            else
                flag = mContext.getResources().getString(R.string.am);
            return flag + before.get(Calendar.HOUR) + timeFlag + before.get(Calendar.HOUR);
        }

        // 하루전
        if (diffDays == 1)
            return mContext.getResources().getString(R.string.yesterday);

        // 일주일 사이
        if(diffDays<=7) {
            switch (before.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.SUNDAY:
                    return DAY_OF_WEEK[Calendar.SUNDAY];
                case Calendar.MONDAY:
                    return DAY_OF_WEEK[Calendar.MONDAY];
                case Calendar.TUESDAY:
                    return DAY_OF_WEEK[Calendar.TUESDAY];
                case Calendar.WEDNESDAY:
                    return DAY_OF_WEEK[Calendar.WEDNESDAY];
                case Calendar.THURSDAY:
                    return DAY_OF_WEEK[Calendar.THURSDAY];
                case Calendar.FRIDAY:
                    return DAY_OF_WEEK[Calendar.FRIDAY];
                case Calendar.SATURDAY:
                    return DAY_OF_WEEK[Calendar.SATURDAY];
            }
        }
        // 그후
        String dot=mContext.getResources().getString(R.string.dot);
        return before.get(Calendar.YEAR)+dot+(before.get(Calendar.MONTH)+1)+dot+before.get(Calendar.DATE);
    }
}