package com.pixel.viewpagergallery.way3;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
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
 * Created by pixel on 2017/6/17.
 * <p>
 * 基于自定义控件
 */

public class RecyclerGalleryView extends FrameLayout {
    // 上下文（Activity）
    private Context mContext;
    // 容器与RecyclerView的宽高
    private int mLayoutWidth, mLayoutHeight, mRecyclerWidth, mRecyclerHeight;
    // 对应的RecyclerView控件
    private RecyclerView mRecyclerView;
    // 数据源
    private List<Object> mDataSourceList = new ArrayList<>();
    // 列表适配器
    private RecyclerAdapter mAdapter;
    // ViewPager是否可以循环滚动
    private boolean mCycleRolling = false;
    // 默认显示的item是总大小的宽高比例（居中显示的item不受该限制）
    private float itemZoomWidth = 0.5f, itemZoomHeight = 1.0f;
    // RecyclerView滚动的全部距离
    private int mRecyclerScrollSize;
    // 数据源与创建item回调借口
    private OnPagerCallback mOnPagerCallback;
    // RecyclerView滚动状态
    private int mRecyclerScrollState = 0;
    // 延时操作
    private final Handler mHandler = new Handler();
    // 默认显示的item
    private int mShowPosition = 0;

    public RecyclerGalleryView(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    public RecyclerGalleryView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initialize();
            }
        }, 500);
    }

    protected void initialize() {
        if (mDataSourceList == null || mOnPagerCallback == null) {
            throw new NullPointerException("数据源与回调接口不能为空,请调用 setDataSourceAndCallback() 设置数据.");
        }

        mLayoutWidth = this.getMeasuredWidth();
        mLayoutHeight = this.getMeasuredHeight();
        mRecyclerWidth = mLayoutWidth;
        mRecyclerHeight = mLayoutHeight;

        mRecyclerView = new RecyclerView(mContext);
        LayoutParams layoutParams = new LayoutParams(mRecyclerWidth, mRecyclerHeight);
        layoutParams.gravity = Gravity.CENTER;
        mRecyclerView.setLayoutParams(layoutParams);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mLinearSnapHelper.attachToRecyclerView(mRecyclerView);

        mAdapter = new RecyclerAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(mScrollListener);

        this.addView(mRecyclerView);

//        if (mCycleRolling) {
//            mShowPosition = mDataSourceList.size() / 2 +
//                    (Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2 % mDataSourceList.size())); // 计算让让ViewPager默认显示中间的Item
//        } else {
//            mShowPosition = mDataSourceList.size() / 2;
//        }
//
//        layoutManager.scrollToPositionWithOffset(mShowPosition, 0); // 如果是循环滚动模式则定位到中间位置
//        layoutManager.setStackFromEnd(true);
//
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                ViewGroup itemView = (ViewGroup) mRecyclerView
//                        .getLayoutManager().findViewByPosition(mShowPosition);
//                if (itemView != null) {
//                    View child = itemView.getChildAt(0);
//                    if (child != null) {
//                        child.setLayoutParams(new FrameLayout.LayoutParams(mRecyclerWidth / 2, mRecyclerHeight));
//                    }
//                }
//            }
//        }, 100);
    }

    // 执行item缩放处理
    protected void performZoomItem() {

        // 距离下一个item显示的百分比 向右滑（10向1）百分比变小，向左滑（1向10）百分比变大
        float percent = (float) mAdapter.getItemOffset() / (float) mAdapter.getItemWidth();
        // 获取最左边显示的item下标
        int position = mAdapter.getCenterPosition() - 1;

//        float scale = 0.9f;
//
//        if (position > 0) {
//            ViewGroup liftView = (ViewGroup) mRecyclerView.getLayoutManager().findViewByPosition(position);
//            if (liftView != null && liftView.getChildAt(0) != null) {
//                liftView.getChildAt(0).setScaleX((1 - scale) * percent + scale);
//                liftView.getChildAt(0).setScaleY((1 - scale) * percent + scale);
//            }
//        }
//
//        ViewGroup centerView = (ViewGroup) mRecyclerView.getLayoutManager().findViewByPosition(position + 1);
//        if (centerView != null && centerView.getChildAt(0) != null) {
//            centerView.getChildAt(0).setScaleX((scale - 1) * percent + 1);
//            centerView.getChildAt(0).setScaleY((scale - 1) * percent + 1);
//        }
//
//
//        if (position < mAdapter.getItemCount() - 1) {
//            ViewGroup rightView = (ViewGroup) mRecyclerView.getLayoutManager().findViewByPosition(position + 2);
//            if (rightView != null && rightView.getChildAt(0) != null) {
//                rightView.getChildAt(0).setScaleX((1 - scale) * percent + scale);
//                rightView.getChildAt(0).setScaleY((1 - scale) * percent + scale);
//            }
//        }

        if (position > 0 && percent <= 0.5f) {
            ViewGroup liftView = (ViewGroup) mRecyclerView.getLayoutManager().findViewByPosition(position);
            if (liftView != null && liftView.getChildAt(0) != null) {
                float scale = 1 - (percent);
                liftView.getChildAt(0).setScaleX(scale);
                liftView.getChildAt(0).setScaleY(scale);
            }
        }

        ViewGroup centerView = (ViewGroup) mRecyclerView.getLayoutManager().findViewByPosition(position + 1);
        if (centerView != null && centerView.getChildAt(0) != null) {
            float scale = Math.abs(percent - 1);
            centerView.getChildAt(0).setScaleX(scale);
            centerView.getChildAt(0).setScaleY(scale);
        }


        if (position < mAdapter.getItemCount() - 1 && percent >= 0.5f) {
            ViewGroup rightView = (ViewGroup) mRecyclerView.getLayoutManager().findViewByPosition(position + 2);
            if (rightView != null && rightView.getChildAt(0) != null) {
                float scale = 1 - (percent);
                rightView.getChildAt(0).setScaleX(scale);
                rightView.getChildAt(0).setScaleY(scale);
            }
        }
    }

    // RecyclerView滚动监听
    private final RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {

        @Override   // newState: 0.闲置， 1.用户正在拖动， 2.用户放手停止拖动
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            mRecyclerScrollState = newState;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            mRecyclerScrollSize += dx;  // RecyclerView只会水平滚动,大小等于从RecyclerView最左边开始的可见部分计算。

            switch (mRecyclerScrollState) {
                case RecyclerView.SCROLL_STATE_IDLE:    // 0
                    break;
                case RecyclerView.SCROLL_STATE_DRAGGING:    // 1
                    performZoomItem();
                    break;
                case RecyclerView.SCROLL_STATE_SETTLING:    // 2
                    performZoomItem();    // 用户停止拖动后在执行一次，避免出现效果异常。
                    break;
            }
        }
    };

    // 控制RecyclerView控件item居中显示
    private final LinearSnapHelper mLinearSnapHelper = new LinearSnapHelper() {
        @Override
        public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
            if (mRecyclerScrollState == RecyclerView.SCROLL_STATE_IDLE &&   // 在RecyclerView停止滚动且滚动到第一个或者最后一个时不执行居中操作
                    (mRecyclerScrollSize <= 0 || mRecyclerScrollSize >= mAdapter.getItemCount() * mAdapter.getItemWidth())) {
                return new int[]{0, 0}; // 数组第一第二位分别别代表X，Y上是否需要居中操作.
            } else {
                return super.calculateDistanceToFinalSnap(layoutManager, targetView);
            }
        }
    };

    // RecyclerView适配器
    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private int itemWidth, itemHeight;

        public RecyclerAdapter() {
            this.itemWidth = (int) (mRecyclerWidth * itemZoomWidth);
            this.itemHeight = (int) (mRecyclerHeight * itemZoomHeight);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Item对外直接就是一个容器布局，item内控件让调用者控制。
            ViewGroup viewGroup = new FrameLayout(mContext);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(itemWidth, itemHeight); // 设置默认显示大小
            viewGroup.setLayoutParams(layoutParams);
            return new RecyclerView.ViewHolder(viewGroup) {
            };
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            View item = ((ViewGroup) holder.itemView).getChildAt(0);
            if (item == null) {
                int tempPosition = position;
                if (mCycleRolling && position >= mDataSourceList.size()) {  // 如果ViewPager是循环滚动的则要计算下标位置指定数据源,避免下标越界.
                    tempPosition = position % mDataSourceList.size();
                }
                item = mOnPagerCallback.onCreateView((ViewGroup) holder.itemView, mDataSourceList.get(tempPosition));
                // 这是设置不居中的View显示大小为item大小的0.8倍
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) (itemWidth * 0.8), (int) (itemHeight * 0.8));
                layoutParams.gravity = Gravity.CENTER;
                item.setLayoutParams(layoutParams);

                item.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int p = position;    // RecyclerView有自己的回收策略，点击事件只会设置一次，要重新计算。
                        if (mCycleRolling && position >= mDataSourceList.size()) {  // 如果ViewPager是循环滚动的则要计算下标位置指定数据源,避免下标越界.
                            p = position % mDataSourceList.size();
                        }
                        mOnPagerCallback.onItemClick(v, p);
                    }
                });

                ((ViewGroup) holder.itemView).addView(item, 0);
            }
        }

        @Override
        public int getItemCount() {
            return mCycleRolling ? Integer.MAX_VALUE : (mDataSourceList != null ? mDataSourceList.size() : 0);
        }

        // 获取item的宽度
        public int getItemWidth() {
            return itemWidth;
        }

        // 获取当前滑动时item水平偏移量（滑动总距离与页面数量的差）
        public int getItemOffset() {
            return mRecyclerScrollSize - getItemWidth() * (getCenterPosition() - 1);
        }

        // 获取在中间显示的item下标
        public int getCenterPosition() {
            return (mRecyclerScrollSize / getItemWidth()) + 1;  // 直接计算拿到的是左边的item +1则获取到中间的item 一个页面最多只能显示3个item
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


}
