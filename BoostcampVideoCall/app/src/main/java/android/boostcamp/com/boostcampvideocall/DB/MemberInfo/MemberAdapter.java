package android.boostcamp.com.boostcampvideocall.DB.MemberInfo;

import android.boostcamp.com.boostcampvideocall.MainActivity;
import android.boostcamp.com.boostcampvideocall.R;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Jusung on 2017. 2. 17..
 */

public class MemberAdapter extends RecyclerView.Adapter<MemberViewHolder> {

    private int viewHolderCount;
    private int mNumberItems;
    private Context context;
    final private ListItemClickListener mOnClickListener;
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public MemberAdapter(int numberOfItems,Context context,ListItemClickListener mOnClickListener) {
        mNumberItems = numberOfItems;
        this.mOnClickListener=mOnClickListener;
        this.context=context;
        viewHolderCount = 0;
    }

    @Override
    public MemberViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_rv_content, viewGroup, false);
        MemberViewHolder holder = new MemberViewHolder(view,context,mOnClickListener);
        viewHolderCount++;
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
