package com.boostcamp.android.facestroy.db.memberinfo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.boostcamp.android.facestroy.db.Member;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by Jusung on 2017. 2. 17..
 */

public class MemberAdapter extends RealmRecyclerViewAdapter<Member,MemberViewHolder> {

    private int mNumberItems;
    private Context context;
    final private ListItemClickListener mOnClickListener;
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public MemberAdapter(Context context,ListItemClickListener mOnClickListener, @Nullable OrderedRealmCollection<Member> data, boolean autoUpdate) {
        super(context, data, autoUpdate);
        this.mOnClickListener = mOnClickListener;
        this.context = context;
        if(data==null)
            mNumberItems=0;
        else
            this.mNumberItems = data.size();
    }

    @Override
    public MemberViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int viewType) {
        final View view = LayoutInflater.from(context).inflate(com.boostcamp.android.facestroy.R.layout.item_rv_content, viewGroup, false);
        MemberViewHolder holder = new MemberViewHolder(view,context,mOnClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MemberViewHolder holder, final int position) {
        holder.bind(position);
        
    }

    @Override
    public int getItemCount() {
        return mNumberItems;
    }
}
