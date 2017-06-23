package pixel.aylson.test;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import pixel.aylson.test.fragment.MainBaseFragment;
import pixel.aylson.test.fragment.MainFoundFragment;
import pixel.aylson.test.fragment.MainHomeFragment;
import pixel.aylson.test.fragment.MainRoomFragment;
import pixel.aylson.test.fragment.MainSetFragment;

public class MainSupportActivity extends AppCompatActivity {
    private MainSupportPresenter presenter;

    public MainSupportPresenter getPresenter() {
        return presenter;
    }

    private final List<MainBaseFragment> fragmentList = new ArrayList<MainBaseFragment>() {{
        add(new MainHomeFragment());
        add(new MainRoomFragment());
        add(new MainFoundFragment());
        add(new MainSetFragment());
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE); // 沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            this.getWindow().setStatusBarColor(Color.TRANSPARENT);  // 状态栏颜色透明

        setContentView(R.layout.activity_main_support);

        presenter = new MainSupportPresenter(this);
        presenter.initTabLayout(findViewById(R.id.tabLayout), new MainSupportPresenter.OnTabSelectListener() {
            @Override
            public void onSelect(int oldSelect, int newSelect, MainSupportPresenter.TabItem oldItem, MainSupportPresenter.TabItem newItem) {
                oldItem.image.setImageResource(R.mipmap.ic_launcher);
                oldItem.text.setTextColor(ContextCompat.getColor(MainSupportActivity.this, R.color.colorPrimary));
                newItem.image.setImageResource(R.mipmap.ic_launcher_round);
                newItem.text.setTextColor(ContextCompat.getColor(MainSupportActivity.this, R.color.colorAccent));

                showFragment(fragmentList, oldSelect - 1, newSelect - 1);
            }
        }, 2);
    }

    public void showFragment(List<MainBaseFragment> fragments, int oldSelect, int newSelect) {
        if (getSupportFragmentManager().findFragmentByTag(fragments.get(newSelect).getClass().getSimpleName()) == null) {
            getSupportFragmentManager().beginTransaction().add(
                    R.id.contentView, fragments.get(newSelect), fragments.get(newSelect).getClass().getSimpleName()).commit();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.hide(fragments.get(oldSelect));
        transaction.show(fragments.get(newSelect));
        transaction.commit();
    }

}
