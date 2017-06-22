package pixel.aylson.test;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import pixel.aylson.test.fragment.MainFoundFragment;
import pixel.aylson.test.fragment.MainHomeFragment;
import pixel.aylson.test.fragment.MainRoomFragment;
import pixel.aylson.test.fragment.MainSetFragment;

public class HomeActivity extends AppCompatActivity {
    private FrameLayout mContentLayout;
    private TabLayout mTabLayout;

    private final MainHomeFragment homeFragment = new MainHomeFragment();
    private final MainRoomFragment roomFragment = new MainRoomFragment();
    private final MainFoundFragment foundFragment = new MainFoundFragment();
    private final MainSetFragment setFragment = new MainSetFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mContentLayout = (FrameLayout) findViewById(R.id.contentLayout);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(R.id.contentLayout, homeFragment, "HOME").hide(homeFragment);
        transaction.add(R.id.contentLayout, roomFragment, "ROOM").hide(roomFragment);
        transaction.add(R.id.contentLayout, foundFragment, "FOUND").hide(foundFragment);
        transaction.add(R.id.contentLayout, setFragment, "SET").hide(setFragment);
        transaction.commit();

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//                tab.setIcon(R.mipmap.ic_launcher_round);
                if ("主页".equals(tab.getText())) {
                    getSupportFragmentManager().beginTransaction().show(homeFragment).commit();
                }
                if ("房间".equals(tab.getText())) {
                    getSupportFragmentManager().beginTransaction().show(roomFragment).commit();
                }
                if ("发现".equals(tab.getText())) {
                    getSupportFragmentManager().beginTransaction().show(foundFragment).commit();
                }
                if ("设置".equals(tab.getText())) {
                    getSupportFragmentManager().beginTransaction().show(setFragment).commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {    // 上次选中的
//                tab.setIcon(R.mipmap.ic_launcher);
                if ("主页".equals(tab.getText())) {
                    getSupportFragmentManager().beginTransaction().hide(homeFragment).commit();
                }
                if ("房间".equals(tab.getText())) {
                    getSupportFragmentManager().beginTransaction().hide(roomFragment).commit();
                }
                if ("发现".equals(tab.getText())) {
                    getSupportFragmentManager().beginTransaction().hide(foundFragment).commit();
                }
                if ("设置".equals(tab.getText())) {
                    getSupportFragmentManager().beginTransaction().hide(setFragment).commit();
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
//                tab.setIcon(R.mipmap.ic_launcher_round);
                if ("主页".equals(tab.getText())) {
                    getSupportFragmentManager().beginTransaction().show(homeFragment).commit();
                }
                if ("房间".equals(tab.getText())) {
                    getSupportFragmentManager().beginTransaction().show(roomFragment).commit();
                }
                if ("发现".equals(tab.getText())) {
                    getSupportFragmentManager().beginTransaction().show(foundFragment).commit();
                }
                if ("设置".equals(tab.getText())) {
                    getSupportFragmentManager().beginTransaction().show(setFragment).commit();
                }
            }
        });
    }

}
