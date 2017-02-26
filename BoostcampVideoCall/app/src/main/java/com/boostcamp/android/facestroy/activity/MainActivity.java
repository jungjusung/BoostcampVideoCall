package com.boostcamp.android.facestroy.activity;

import com.boostcamp.android.facestroy.R;
import com.boostcamp.android.facestroy.db.CallLog;
import com.boostcamp.android.facestroy.db.Member;
import com.boostcamp.android.facestroy.db.MyInfo;
import com.boostcamp.android.facestroy.db.calllog.CallLogAdapter;
import com.boostcamp.android.facestroy.db.memberinfo.MemberAdapter;
import com.boostcamp.android.facestroy.utill.Constant;
import com.boostcamp.android.facestroy.utill.Utill;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import devlight.io.library.ntb.NavigationTabBar;
import io.realm.Realm;
import io.realm.RealmResults;


public class MainActivity extends AppCompatActivity implements MemberAdapter.ListItemClickListener, CallLogAdapter.ListItemClickListener, View.OnClickListener {


    private static final String TAG = "MainActivity";

    private ViewPager mViewPager;

    private NavigationTabBar mNavigationTabBar;
    private AppBarLayout mAppbar;
    private CollapsingToolbarLayout mToolbar;
    private CircleImageView mMyImage;
    private TextView mMyName,mMyStatus;


    private int[] mIcon = {R.drawable.ic_contacts, R.drawable.ic_video_call, R.drawable.ic_theaters};
    private String[] mColor = {getString(R.string.first_color),getString(R.string.second_color),getString(R.string.third_color)};
    private String[] mTitles = {getString(R.string.phone),getString(R.string.history), getString(R.string.record)};

    private Realm mRealm;
    private MyInfo mMyInformtaion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.myNoActionBar);
        setContentView(R.layout.activity_main);
        mRealm = Realm.getDefaultInstance();

    }

    public void initUI() {
        mViewPager = (ViewPager) findViewById(R.id.vp_list);
        mNavigationTabBar = (NavigationTabBar) findViewById(R.id.nav_facestory);
        mToolbar=(CollapsingToolbarLayout)findViewById(R.id.toolbar);
        mAppbar=(AppBarLayout)findViewById(R.id.appbar_facestory);
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)mAppbar.getLayoutParams();
        if(mMyInformtaion.getStatus().equals("")) {
            //대화명 없을시
            float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
            lp.height=(int)pixels;
        }else{
            //대화명 있을시
            float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 215, getResources().getDisplayMetrics());
            lp.height=(int)pixels;
        }
        mMyImage=(CircleImageView)findViewById(R.id.cv_my_image);
        mMyImage.setOnClickListener(this);
        mMyName=(TextView)findViewById(R.id.tv_my_name);
        mMyStatus=(TextView)findViewById(R.id.tv_my_status);
        Glide.with(getApplicationContext())
                .load(mMyInformtaion.getUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.sample)
                .into(mMyImage);
        mMyName.setText(mMyInformtaion.getName());
        mMyStatus.setText(mMyInformtaion.getStatus());
        mMyStatus.setMaxLines(1);
        mMyStatus.setEllipsize(TextUtils.TruncateAt.END);


        mAppbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (Math.abs(verticalOffset)-appBarLayout.getTotalScrollRange() == 0)
                    mToolbar.setTitle(getString(R.string.app_title));
                else
                    mToolbar.setTitle("");


            }
        });

        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return Constant.VIEW_PAGER_SIZE;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view.equals(object);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                ((ViewPager) container).removeView((View) object);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                final View viewMember = LayoutInflater.from(
                        getBaseContext()).inflate(R.layout.item_rv_list, null, false);

                final View viewCallLog = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_rv_log, null, false);

                final RecyclerView recyclerViewMember = (RecyclerView) viewMember.findViewById(R.id.rv_facestory);
                final RecyclerView recyclerCallLog = (RecyclerView) viewCallLog.findViewById(R.id.rv_call_log);

                recyclerViewMember.setHasFixedSize(true);
                recyclerViewMember.setLayoutManager(new LinearLayoutManager(
                        getBaseContext(), LinearLayoutManager.VERTICAL, false)
                );

                recyclerCallLog.setHasFixedSize(true);
                recyclerCallLog.setLayoutManager(new LinearLayoutManager(
                        getBaseContext(), LinearLayoutManager.VERTICAL, false)
                );

                if (position == 0) {
                    final RealmResults<Member> list = mRealm.where(Member.class).findAll();
                    recyclerViewMember.setAdapter(new MemberAdapter(getApplicationContext(), MainActivity.this, list, true));

                    container.addView(viewMember);
                    return viewMember;
                } else if (position == 1) {
                    RealmResults<CallLog> list = mRealm.where(CallLog.class).findAll();

                    recyclerCallLog.setAdapter(new CallLogAdapter(getApplicationContext(), MainActivity.this, list, true));
                    container.addView(viewCallLog);
                    return viewCallLog;
                } else {
                    return null;
                }
            }
        });


        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        for (int i = 0; i < Constant.VIEW_PAGER_SIZE; i++) {

            if (android.os.Build.VERSION.SDK_INT >= 21) {
                models.add(new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(mIcon[i], null),
                        Color.parseColor(mColor[i]))
                        .title(mTitles[i])
                        .build());
            } else {
                models.add(new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(mIcon[i]),
                        Color.parseColor(mColor[i]))
                        .title(mTitles[i])
                        .build());
            }
        }
        mNavigationTabBar.setModels(models);
        mNavigationTabBar.setViewPager(mViewPager, 0);
        mNavigationTabBar.setBehaviorEnabled(true);
        mNavigationTabBar.setOnTabBarSelectedIndexListener(new NavigationTabBar.OnTabBarSelectedIndexListener() {
            @Override
            public void onStartTabSelected(final NavigationTabBar.Model model, final int index) {
            }

            @Override
            public void onEndTabSelected(final NavigationTabBar.Model model, final int index) {
                model.hideBadge();
            }
        });
        mNavigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(final int position) {

            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });

    }

    @Override
    public void onListItemClick(int clickedItemIndex) {

    }



    @Override
    protected void onResume() {
        super.onResume();
        mMyInformtaion=mRealm.where(MyInfo.class).findFirst();
//
        Utill.updateMemberToRealm(getApplicationContext());
        initUI();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cv_my_image:
                Intent intent=new Intent(this,ModMyInfoActivity.class);
                startActivity(intent);
                break;
        }
    }
}
