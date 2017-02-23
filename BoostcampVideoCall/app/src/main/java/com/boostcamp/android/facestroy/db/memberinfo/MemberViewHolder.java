package com.boostcamp.android.facestroy.db.memberinfo;

import com.boostcamp.android.facestroy.MemberDetailActivity;
import com.boostcamp.android.facestroy.VideoCallActvity;
import com.boostcamp.android.facestroy.db.Member;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.realm.Realm;

/**
 * Created by Jusung on 2017. 2. 17..
 */

public class MemberViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private TextView mPhoneNumber, mName, mVideoCall;
    private Realm realm;
    private List<Member> mList;
    private MemberAdapter.ListItemClickListener mOnClickListener;
    private Context context;


    @Override
    public void onClick(View view) {
        final int clickedPosition = getAdapterPosition();
        mOnClickListener.onListItemClick(clickedPosition);
        mList = realm.where(Member.class).findAll();
        Member member=mList.get(clickedPosition);
        switch (view.getId()){
            case com.boostcamp.android.facestroy.R.id.tv_videocall:
                Intent videoIntent=new Intent(context, VideoCallActvity.class);
                videoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                videoIntent.putExtra("name",member.getName());
                videoIntent.putExtra("phoneNumber",member.getPhoneNumber());
                videoIntent.putExtra("token",member.getToken());
                context.startActivity(videoIntent);
                break;
        }

    }

    public MemberViewHolder(final View itemView,Context context,MemberAdapter.ListItemClickListener mOnClickListener) {
        super(itemView);
        this.context=context;
        this.mOnClickListener = mOnClickListener;
        realm=Realm.getDefaultInstance();
        mList = realm.where(Member.class).findAll();
        mName = (TextView) itemView.findViewById(com.boostcamp.android.facestroy.R.id.tv_name);
        mPhoneNumber = (TextView) itemView.findViewById(com.boostcamp.android.facestroy.R.id.tv_phone);
        mVideoCall=(TextView)itemView.findViewById(com.boostcamp.android.facestroy.R.id.tv_videocall);
        mVideoCall.setOnClickListener(this);
    }

    public void bind(int listIndex) {
        mName.setText(mList.get(listIndex).getName());
        mPhoneNumber.setText(mList.get(listIndex).getPhoneNumber());
    }
}
