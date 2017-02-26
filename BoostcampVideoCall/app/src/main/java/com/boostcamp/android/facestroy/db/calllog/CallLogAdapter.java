package com.boostcamp.android.facestroy.db.calllog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.boostcamp.android.facestroy.db.CallLog;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by Jusung on 2017. 2. 20..
 */

public class CallLogAdapter extends RealmRecyclerViewAdapter<CallLog, CallLogViewHolder> {

    private static final String TAG = "CallLog";
    private int mLogItems;
    private Context mContext;

    final private CallLogAdapter.ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }


    public CallLogAdapter(@NonNull Context mContext, CallLogAdapter.ListItemClickListener mOnClickListener, @Nullable OrderedRealmCollection<CallLog> data, boolean autoUpdate) {
        super(mContext, data, autoUpdate);
        this.mContext = mContext;
        this.mOnClickListener = mOnClickListener;
        if (data == null)
            this.mLogItems = 0;
        else
            this.mLogItems = data.size();
    }

    @Override
    public CallLogViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(com.boostcamp.android.facestroy.R.layout.item_rv_log_content, viewGroup, false);
        CallLogViewHolder holder = new CallLogViewHolder(view, mContext);

        return holder;
    }

    @Override
    public void onBindViewHolder(CallLogViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mLogItems;
    }
}
