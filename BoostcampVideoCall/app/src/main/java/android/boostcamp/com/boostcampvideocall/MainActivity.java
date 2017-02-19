package android.boostcamp.com.boostcampvideocall;

import android.boostcamp.com.boostcampvideocall.db.Member;
import android.boostcamp.com.boostcampvideocall.db.memberinfo.MemberAdapter;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import devlight.io.library.ntb.NavigationTabBar;
import io.realm.Realm;



public class MainActivity extends AppCompatActivity implements MemberAdapter.ListItemClickListener{

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
                final View view = LayoutInflater.from(
                        getBaseContext()).inflate(R.layout.item_rv_list, null, false);

                final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv_facestory);

                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(
                        getBaseContext(), LinearLayoutManager.VERTICAL, false)
                );

                if (position == 0) {
                    List<Member> list = realm.where(Member.class).findAll();
                    recyclerView.setAdapter(new MemberAdapter(list.size(),getApplicationContext(),MainActivity.this));
                }

                container.addView(view);
                return view;
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
