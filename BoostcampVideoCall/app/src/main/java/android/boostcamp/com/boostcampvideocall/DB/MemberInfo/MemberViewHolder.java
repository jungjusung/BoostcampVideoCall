package android.boostcamp.com.boostcampvideocall.DB.MemberInfo;

import android.boostcamp.com.boostcampvideocall.DB.Member;
import android.boostcamp.com.boostcampvideocall.R;
import android.content.Context;
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

    private TextView mPhoneNumber, mName, mInfo, mVideoCall;
    private ImageView mStartBackground,mEndBackground;
    private Realm realm;
    private List<Member> mList;
    private MemberAdapter.ListItemClickListener mOnClickListener;
    private Context context;
    private int[] backColors = {R.drawable.image_back1, R.drawable.image_back2, R.drawable.image_back3, R.drawable.image_back4, R.drawable.image_back5};

    @Override
    public void onClick(View view) {
        final int clickedPosition = getAdapterPosition();
        mOnClickListener.onListItemClick(clickedPosition);
        switch (view.getId()){
            case R.id.tv_info:
                Toast.makeText(context,clickedPosition+"인포", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_videocall:
                Toast.makeText(context,clickedPosition+"영상통화전화", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    public MemberViewHolder(final View itemView,Context context,MemberAdapter.ListItemClickListener mOnClickListener) {
        super(itemView);
        this.context=context;
        this.mOnClickListener = mOnClickListener;
        realm=Realm.getDefaultInstance();
        mList = realm.where(Member.class).findAll();
        mName = (TextView) itemView.findViewById(R.id.tv_name);
        mPhoneNumber = (TextView) itemView.findViewById(R.id.tv_phone);
        mInfo=(TextView)itemView.findViewById(R.id.tv_info);
        mVideoCall=(TextView)itemView.findViewById(R.id.tv_videocall);
        mStartBackground = (ImageView) itemView.findViewById(R.id.iv_back_start);
        mEndBackground = (ImageView) itemView.findViewById(R.id.iv_back_end);

        mInfo.setOnClickListener(this);
        mVideoCall.setOnClickListener(this);
    }

    void bind(int listIndex) {
        mName.setText(mList.get(listIndex).getName());
        mPhoneNumber.setText(mList.get(listIndex).getPhoneNumber());
        mStartBackground.setBackgroundResource(backColors[listIndex % 5]);
        mEndBackground.setBackgroundResource(backColors[listIndex % 5]);
    }
}
