package android.boostcamp.com.boostcampvideocall.db.calllog;

import android.boostcamp.com.boostcampvideocall.R;
import android.boostcamp.com.boostcampvideocall.db.memberinfo.MemberAdapter;
import android.boostcamp.com.boostcampvideocall.db.memberinfo.MemberViewHolder;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Jusung on 2017. 2. 20..
 */

public class CallLogAdapter extends RecyclerView.Adapter<CallLogViewHolder>{

    private static final String TAG="CallLog";
    private int mLogItems;
    private Context mContext;

    final private CallLogAdapter.ListItemClickListener mOnClickListener;
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public CallLogAdapter(int mLogItems,Context mContext,CallLogAdapter.ListItemClickListener mOnClickListener) {
        this.mLogItems = mLogItems;
        this.mContext=mContext;
        this.mOnClickListener=mOnClickListener;
    }

    @Override
    public CallLogViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.item_rv_log_content, viewGroup, false);
        CallLogViewHolder holder = new CallLogViewHolder(view,mContext,mOnClickListener);

        return holder;
    }

    @Override
    public void onBindViewHolder(CallLogViewHolder holder, int position) {
        holder.bind(position);
        Log.d(TAG,"#"+position);
    }

    @Override
    public int getItemCount() {
        return mLogItems;
    }
}
