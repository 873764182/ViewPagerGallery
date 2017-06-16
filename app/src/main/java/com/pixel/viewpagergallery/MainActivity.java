package com.pixel.viewpagergallery;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private FrameLayout mFrameLayout;
    private ViewPager mViewPager;

    private final List<String> pagerView = new ArrayList<>();
    private final Map<Integer, View> pagerCView = new Hashtable<>();

    // 缩放宽高基准大小(最大值)
    public static final int baseWidthPadding = 80;
    public static final int baseHeightPadding = 160;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for (int i = 0; i < 5; i++) {
            pagerView.add("页面 " + i);
        }
        
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setClipChildren(false);
        mViewPager.setOffscreenPageLimit(3);
//        mViewPager.setPageMargin(100);
        mViewPager.setAdapter(new ViewPagerAdapter());
        mViewPager.addOnPageChangeListener(changeListener);
        int item = (Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2 % pagerView.size())) + pagerView.size() / 2; // 计算让让ViewPager默认显示中间的Item
        mViewPager.setCurrentItem(item, false);

        mFrameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        mFrameLayout.setClipChildren(false);
        mFrameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mViewPager.dispatchTouchEvent(event);    // 将ViewPager上层控件的事件传递给ViewPager,让在父控件滑动时ViewPager也能触发滑动,让滑动事件不仅仅在ViewPager上.
            }
        });
    }

    private final ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            Log.e("ViewPager", "onPageScrolled -> " + position + "\t" + positionOffset + "\t" + positionOffsetPixels);

            if (pagerCView.get(position) != null) {
                int ohp = (int) (positionOffset * baseHeightPadding);
                int owp = (int) (positionOffset * baseWidthPadding);
                pagerCView.get(position).setPadding(owp, ohp, owp, ohp);
            }

            int iwp = (int) ((1 - positionOffset) * baseWidthPadding);
            int ihp = (int) ((1 - positionOffset) * baseHeightPadding);

            if (pagerCView.get(position + 1) != null) {
                pagerCView.get(position + 1).setPadding(iwp, ihp, iwp, ihp);
            }
            if (pagerCView.get(position - 1) != null) {
                pagerCView.get(position - 1).setPadding(iwp, ihp, iwp, ihp);
            }
        }

        @Override
        public void onPageSelected(int position) {
//            Log.e("ViewPager", "onPageSelected -> " + position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {   // 0.状态闲置, 1.正在滑动, 2.滑动完成
//            Log.e("ViewPager", "onPageScrollStateChanged -> " + state);
        }
    };

    private final class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;   // pagerView.size()
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = pagerCView.get(position);
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.pager_view, mViewPager, false);
                TextView textView = (TextView) view.findViewById(R.id.textView);
                final int finalPosition = position;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "position " + finalPosition, Toast.LENGTH_LONG).show();
                    }
                });
                view.setPadding(baseWidthPadding, baseHeightPadding, baseWidthPadding, baseHeightPadding);
                pagerCView.put(position, view);
                if (position >= pagerView.size()) {
                    position = position % pagerView.size();
                }
                textView.setText(pagerView.get(position));
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(pagerCView.get(position));
        }
    }
}
