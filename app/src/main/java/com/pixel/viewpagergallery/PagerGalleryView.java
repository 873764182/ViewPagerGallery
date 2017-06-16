package com.pixel.viewpagergallery;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by pixel on 2017/6/16.
 * <p>
 * 基于ViewPager实现画廊效果
 */

public class PagerGalleryView extends FrameLayout {
    // 缩放宽高基准大小 水平 缩放为正常宽度的85%
    public float baseZoomWidth = 0.15f;
    // 缩放宽高基准大小 竖直 缩放为正常高度的85%
    public float baseZoomHeight = 0.15f;
    // 缓存ViewPager的子View
    private final Map<Integer, View> mViewCache = new Hashtable<>();
    // ViewPager子View创建接口
    private OnPagerCallback mOnPagerCallback;
    // ViewPager数据源列表 设置默认值 避免空指针
    private List<Object> mDataSourceList = new ArrayList<>();
    // ViewPager是否可以循环滚动
    private boolean mCycleRolling = false;

    private Context mContext;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private int frameWidth, frameHeight;
    private int pagerWidth, pagerHeight;

    public PagerGalleryView(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    public PagerGalleryView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initialize();
            }
        }, 100);
    }

    protected void initialize() {
        if (mDataSourceList == null || mDataSourceList.size() <= 0 || mOnPagerCallback == null) {
            throw new NullPointerException("数据源与回调接口不能为空,请调用 setDataSourceAndCallback() 设置数据.");
        }

        this.setClipChildren(false);

        frameWidth = getMeasuredWidth();
        frameHeight = getMeasuredHeight();
        pagerWidth = (int) (frameWidth * (2f / 3f));    // ViewPager宽度是父控件的2/3
        pagerHeight = frameHeight;

        mViewPager = new ViewPager(mContext);
        mViewPager.setClipChildren(false);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(pagerChangeListener);
        mViewPager.setOverScrollMode(OVER_SCROLL_NEVER);
        LayoutParams layoutParams = new LayoutParams(pagerWidth, pagerHeight);
        layoutParams.gravity = Gravity.CENTER;
        mViewPager.setLayoutParams(layoutParams);
        mPagerAdapter = new ViewPagerAdapter();
        mViewPager.setAdapter(mPagerAdapter);

        if (mCycleRolling) {    // 如果是循环滚动 则定位到中间显示
            int showPosition = mDataSourceList.size() / 2 +
                    (Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2 % mDataSourceList.size())); // 计算让让ViewPager默认显示中间的Item
            mViewPager.setCurrentItem(showPosition, false);
        }

        this.addView(mViewPager);
        this.setOnTouchListener(new OnTouchListener() {
            float downX, downY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) { // 触碰边缘时让ViewPager滚动到指定Item
                    downX = event.getX();
                    downY = event.getY();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (Math.abs(event.getX() - downX) < 50 && Math.abs(event.getY() - downY) < 50) {
                        if (downX < pagerWidth && mViewPager.getCurrentItem() > 0) {
                            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
                        } else if (downX > pagerWidth && mViewPager.getCurrentItem() < Integer.MAX_VALUE) {
                            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
                        }
                        return true;    // 避免触发点击事件
                    }
                }
                return mViewPager.dispatchTouchEvent(event);    // 将父控件的触摸事件直接转给ViewPager
            }
        });
    }

    private final ViewPager.OnPageChangeListener pagerChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mViewCache.get(position) != null) {
                int ohp = (int) (positionOffset * baseZoomHeight * pagerWidth);
                int owp = (int) (positionOffset * baseZoomWidth * pagerHeight);
                mViewCache.get(position).setPadding(owp, ohp, owp, ohp);
            }
            int iwp = (int) ((1 - positionOffset) * baseZoomWidth * pagerWidth);
            int ihp = (int) ((1 - positionOffset) * baseZoomHeight * pagerHeight);
            if (mViewCache.get(position + 1) != null) {
                mViewCache.get(position + 1).setPadding(iwp, ihp, iwp, ihp);
            }
            if (mViewCache.get(position - 1) != null) {
                mViewCache.get(position - 1).setPadding(iwp, ihp, iwp, ihp);
            }
        }

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {   // 0.状态闲置, 1.正在滑动, 2.滑动完成
        }
    };

    private class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mCycleRolling ? Integer.MAX_VALUE : (mDataSourceList != null ? mDataSourceList.size() : 0);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View item = mViewCache.get(position);
            if (item == null && mOnPagerCallback != null) {
                int tempPosition = position;
                if (mCycleRolling && mDataSourceList.size() > 0 && position >= mDataSourceList.size()) {  // 如果ViewPager是循环滚动的则要计算下标位置指定数据源,避免下标越界.
                    tempPosition = position % mDataSourceList.size();
                }
                item = mOnPagerCallback.onCreateView(mViewPager, mDataSourceList.get(tempPosition));
                int w = (int) (baseZoomWidth * pagerWidth);
                int h = (int) (baseZoomHeight * pagerHeight);
                item.setPadding(w, h, w, h);
                final int finalTempPosition = tempPosition;
                item.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnPagerCallback.onItemClick(v, finalTempPosition);
                    }
                });
                mViewCache.put(position, item);
            }
            container.addView(item);
            return item;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewCache.get(position));
        }
    }

    public interface OnPagerCallback {
        View onCreateView(ViewGroup parent, Object dataSource);

        void onItemClick(View item, int position);
    }

    public void setDataSource(@NonNull Object dataSourceList, @NonNull OnPagerCallback callback) {
        setDataSource(false, dataSourceList, callback);
    }

    /**
     * 设置数据源与回调函数
     *
     * @param cycleRolling   是否循环滚动
     * @param dataSourceList 数据源
     * @param callback       回调函数 创建item与item点击
     */
    public void setDataSource(boolean cycleRolling, @NonNull Object dataSourceList, @NonNull OnPagerCallback callback) {
        this.mCycleRolling = cycleRolling;
        this.mOnPagerCallback = callback;
        if (dataSourceList instanceof List) {
            this.mDataSourceList = (List<Object>) dataSourceList;
        } else {
            throw new ClassCastException("dataSourceList 必须是列表类型 List");
        }
    }

    /**
     * 设置缩放的等级 设置区间: 0.0 - 1.0
     *
     * @param baseZoomWidth  在宽度上的缩放等级
     * @param baseZoomHeight 在高度上的缩放等级
     */
    public void setBaseZoom(float baseZoomWidth, float baseZoomHeight) {
        if (baseZoomWidth > 0.0f && baseZoomWidth < 1.0f) {
            this.baseZoomWidth = 1.0f - baseZoomWidth;
        }
        if (baseZoomHeight > 0.0f && baseZoomHeight < 1.0f) {
            this.baseZoomHeight = 1.0f - baseZoomHeight;
        }
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    public PagerAdapter getPagerAdapter() {
        return mPagerAdapter;
    }
}
