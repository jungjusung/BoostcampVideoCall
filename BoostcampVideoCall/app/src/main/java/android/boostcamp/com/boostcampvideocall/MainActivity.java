package android.boostcamp.com.boostcampvideocall;

import android.boostcamp.com.boostcampvideocall.db.CallLog;
import android.boostcamp.com.boostcampvideocall.db.Member;
import android.boostcamp.com.boostcampvideocall.db.calllog.CallLogAdapter;
import android.boostcamp.com.boostcampvideocall.db.memberinfo.MemberAdapter;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import devlight.io.library.ntb.NavigationTabBar;
import io.realm.Realm;
import io.realm.RealmResults;


public class MainActivity extends AppCompatActivity implements MemberAdapter.ListItemClickListener,CallLogAdapter.ListItemClickListener{

    String TAG;
    private ViewPager mViewPager;
    private static final int VIEW_PAGER_SIZE = 3;

    private NavigationTabBar navigationTabBar;
    private Realm realm;
    private int[] icons = {R.drawable.ic_contacts, R.drawable.ic_video_call, R.drawable.ic_theaters};
    private String[] colors = {"#f9bb72", "#dd6495", "#72d3b4"};
    private String[] titles = {"연락처", "통화기록", "저장소"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.myNoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        realm = Realm.getDefaultInstance();
        initUI();
    }

    public void initUI() {
        mViewPager = (ViewPager) findViewById(R.id.vp_list);
        navigationTabBar = (NavigationTabBar) findViewById(R.id.nav_facestory);
        TAG = this.getClass().getName();

        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return VIEW_PAGER_SIZE;
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

                final View viewCallLog=LayoutInflater.from(getBaseContext()).inflate(R.layout.item_rv_log,null,false);

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
                    List<Member> list = realm.where(Member.class).findAll();
                    for(Member call:list) {
                        Log.d(TAG, call.getTime() + "");
                        Log.d(TAG, call.getCount() + "");
                    }
                    recyclerViewMember.setAdapter(new MemberAdapter(list.size(),getApplicationContext(),MainActivity.this));
                    container.addView(viewMember);
                    return viewMember;
                }else if(position==1){
                    RealmResults<CallLog> list=realm.where(CallLog.class).findAll();

                    recyclerCallLog.setAdapter(new CallLogAdapter(list.size(),getApplicationContext(),MainActivity.this));
                    container.addView(viewCallLog);
                    return viewCallLog;
                }else{
                    return null;
                }
            }
        });


        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        for (int i = 0; i < VIEW_PAGER_SIZE; i++) {

            if (android.os.Build.VERSION.SDK_INT >= 21) {
                models.add(new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(icons[i], null),
                        Color.parseColor(colors[i]))
                        .title(titles[i])
                        .build());
            } else {
                models.add(new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(icons[i]),
                        Color.parseColor(colors[i]))
                        .title(titles[i])
                        .build());
            }
        }
        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(mViewPager, 0);
        navigationTabBar.setBehaviorEnabled(true);
        navigationTabBar.setOnTabBarSelectedIndexListener(new NavigationTabBar.OnTabBarSelectedIndexListener() {
            @Override
            public void onStartTabSelected(final NavigationTabBar.Model model, final int index) {
            }

            @Override
            public void onEndTabSelected(final NavigationTabBar.Model model, final int index) {
                model.hideBadge();
            }
        });
        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
}
