package pixel.aylson.test;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

public class HomeActivity extends AppCompatActivity {
    private FrameLayout mContentLayout;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mContentLayout = (FrameLayout) findViewById(R.id.contentLayout);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);

        initTab();
    }

    protected void initTab() {
        TabLayout.Tab tab_1 = mTabLayout.newTab();
        tab_1.setText("主页");
        tab_1.setIcon(R.mipmap.ic_launcher);
        mTabLayout.addTab(tab_1);

        TabLayout.Tab tab_2 = mTabLayout.newTab();
        tab_2.setText("房间");
        tab_2.setIcon(R.mipmap.ic_launcher);
        mTabLayout.addTab(tab_2);

        TabLayout.Tab tab_3 = mTabLayout.newTab();
        tab_3.setText("发现");
        tab_3.setIcon(R.mipmap.ic_launcher);
        mTabLayout.addTab(tab_3);

        TabLayout.Tab tab_4 = mTabLayout.newTab();
        tab_4.setText("设置");
        tab_4.setIcon(R.mipmap.ic_launcher);
        mTabLayout.addTab(tab_4);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.setIcon(R.mipmap.ic_launcher_round);
//                Toast.makeText(HomeActivity.this, "选中 " + tab.getText(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {    // 上次选中的
                tab.setIcon(R.mipmap.ic_launcher);
//                Toast.makeText(HomeActivity.this, "未选 " + tab.getText(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                tab.setIcon(R.mipmap.ic_launcher_round);
//                Toast.makeText(HomeActivity.this, "重选 " + tab.getText(), Toast.LENGTH_SHORT).show();
            }
        });

        mTabLayout.getTabAt(0).select();
    }

}
