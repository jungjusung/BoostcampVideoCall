package android.boostcamp.com.boostcampvideocall;

import android.boostcamp.com.boostcampvideocall.db.Member;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Jusung on 2017. 2. 20..
 */

public class MemberDetailActivity extends AppCompatActivity implements View.OnClickListener{


    private ViewPager mDetailViewPager;
    private Realm mRealm;
    private CircleImageView mBtnPrevious,mBtnNext;
    private ImageView mBtnBackspace;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.myNoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_detail);
        mRealm=Realm.getDefaultInstance();
        mDetailViewPager=(ViewPager)findViewById(R.id.vp_detail);
        mBtnPrevious=(CircleImageView)findViewById(R.id.bt_previous);
        mBtnNext=(CircleImageView)findViewById(R.id.bt_next);
        mBtnBackspace=(ImageView)findViewById(R.id.bt_backspace);
        mBtnPrevious.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
        mBtnBackspace.setOnClickListener(this);
        mBtnBackspace.bringToFront();

        DetailAdapter detailAdapter=new DetailAdapter(getLayoutInflater());
        mDetailViewPager.setAdapter(detailAdapter);

    }

    @Override
    public void onClick(View view) {

        int position;
        switch (view.getId()){
            case R.id.bt_next:
                position=mDetailViewPager.getCurrentItem();
                mDetailViewPager.setCurrentItem(position+1,true);
                break;
            case R.id.bt_previous:
                position=mDetailViewPager.getCurrentItem();
                mDetailViewPager.setCurrentItem(position-1,true);
                break;
            case R.id.bt_backspace:
                onBackPressed();
                break;
        }
    }

    public class DetailAdapter extends PagerAdapter {

        private LayoutInflater mInflater;


        public DetailAdapter(LayoutInflater mInflater) {
            this.mInflater = mInflater;

        }

        @Override
        public int getCount() {
            return mRealm.where(Member.class).findAll().size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view=null;
            view= mInflater.inflate(R.layout.item_detail, null);
            CircleImageView circleImageView=(CircleImageView)view.findViewById(R.id.profile_image);
            TextView name=(TextView)view.findViewById(R.id.tv_name);
            TextView phoneNumber=(TextView)view.findViewById(R.id.tv_phone);
            TextView count=(TextView)view.findViewById(R.id.tv_count);
            TextView time=(TextView)view.findViewById(R.id.tv_total_time);

            RealmResults<Member> list=mRealm.where(Member.class).findAll();
            Member member=list.get(position);
            name.setText(member.getName());
            phoneNumber.setText(member.getPhoneNumber());
            count.setText(member.getCount()+"회");
            time.setText(member.getTime()+"");

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
