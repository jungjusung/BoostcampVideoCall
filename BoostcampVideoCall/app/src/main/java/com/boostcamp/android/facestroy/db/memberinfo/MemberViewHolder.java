package com.boostcamp.android.facestroy.db.memberinfo;

import com.boostcamp.android.facestroy.activity.MemberDetailActivity;
import com.boostcamp.android.facestroy.R;
import com.boostcamp.android.facestroy.activity.VideoCallActvity;
import com.boostcamp.android.facestroy.db.Member;
import com.boostcamp.android.facestroy.db.MyInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;

/**
 * Created by Jusung on 2017. 2. 17..
 */

public class MemberViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private TextView mPhoneNumber, mName, mVideoCall,mStatus;
    private Realm mRealm;
    private List<Member> mList;
    private MemberAdapter.ListItemClickListener mOnClickListener;
    private Context mContext;
    private CircleImageView mImage;


    @Override
    public void onClick(View view) {
        final int clickedPosition = getAdapterPosition();
        mOnClickListener.onListItemClick(clickedPosition);
        mList = mRealm.where(Member.class).findAll();
        Member member=mList.get(clickedPosition);
        MyInfo myInfo= mRealm.where(MyInfo.class).findFirst();
        switch (view.getId()){
            case R.id.tv_videocall:
                Intent videoIntent=new Intent(mContext, VideoCallActvity.class);
                videoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                videoIntent.putExtra("name",myInfo.getName());
                videoIntent.putExtra("phoneNumber",myInfo.getPhoneNumber());
                videoIntent.putExtra("token",member.getToken());
                mContext.startActivity(videoIntent);
                break;
            case R.id.cv_image:
                Intent detailIntent=new Intent(mContext, MemberDetailActivity.class);
                detailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                detailIntent.putExtra("position",clickedPosition);
                mContext.startActivity(detailIntent);
                break;
        }

    }

    public MemberViewHolder(final View itemView,Context context,MemberAdapter.ListItemClickListener mOnClickListener) {
        super(itemView);
        this.mContext =context;
        this.mOnClickListener = mOnClickListener;
        mRealm =Realm.getDefaultInstance();
        mList = mRealm.where(Member.class).findAll();
        mImage=(CircleImageView)itemView.findViewById(R.id.cv_image);
        mName = (TextView) itemView.findViewById(R.id.tv_name);
        mPhoneNumber = (TextView) itemView.findViewById(R.id.tv_phone);
        mStatus=(TextView)itemView.findViewById(R.id.tv_status);
        mVideoCall=(TextView)itemView.findViewById(R.id.tv_videocall);
        mVideoCall.setOnClickListener(this);
        mImage.setOnClickListener(this);
    }

    public void bind(int listIndex) {
        mName.setText(mList.get(listIndex).getName());
        mPhoneNumber.setText(mList.get(listIndex).getPhoneNumber());
        if(mList.get(listIndex).getStatus()!=null)
            mStatus.setText(mList.get(listIndex).getStatus());
        Glide.with(mContext)
                .load(mList.get(listIndex).getUrl())
                .asBitmap()
                .error(R.drawable.sample)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mImage);
    }
}
