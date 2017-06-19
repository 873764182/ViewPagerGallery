package com.pixel.viewpagergallery.way3;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pixel on 2017/6/19.
 * <p>
 * 画廊控件 参考: http://www.jianshu.com/p/85bf072bfeed
 */

public class RGalleryView extends RecyclerView {
    // 最大顺时滑动速度
    private static final int FLING_MAX_VELOCITY = 8000;
    // 不需要滚动 (到列表顶部与底部时)
    boolean mNoNeedToScroll = false;
    // 滑动一页的距离
    private int mOnePageWidth;
    // 卡片宽度
    private int mCardWidth;
    //
    private int mCardGalleryWidth;
    //
    private int mCurrentItemPos;
    //  页面内边距
    int mPagePadding = 5;
    //
    int mShowLeftCardWidth = 50;
    // 两边视图scale
    private float mScale = 0.8f;
    //
    private Context mContext;
    // 数据源
    private List<Object> mDataSourceList = new ArrayList<>();
    // 数据源与创建item回调借口
    private OnPagerCallback mOnPagerCallback;
    // 是否循环滚动
    private boolean mCycleRolling = false;

    public RGalleryView(Context context) {
        super(context);
        this.mContext = context;
    }

    public RGalleryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public RGalleryView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                initialize();
            }
        }, 500);
    }

    // 初始化
    protected void initialize() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        this.setLayoutManager(linearLayoutManager);
        this.setAdapter(new GalleryAdapter());
        new ScaleHelper(this);
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {    // 计算滚动速度
        velocityX = velocityX > 0 ? Math.min(velocityX, FLING_MAX_VELOCITY) : Math.min(velocityX, -FLING_MAX_VELOCITY);
        velocityY = velocityY > 0 ? Math.min(velocityY, FLING_MAX_VELOCITY) : Math.min(velocityY, -FLING_MAX_VELOCITY);
        return super.fling(velocityX, velocityY);
    }

    private class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewGroup itemView = new FrameLayout(mContext);
            int itemWidth = parent.getMeasuredWidth() - dip2px(mContext, 2 * (mPagePadding + mShowLeftCardWidth));
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(itemWidth, parent.getMeasuredHeight());
            itemView.setLayoutParams(layoutParams);
            return new RecyclerView.ViewHolder(itemView) {
            };
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            int relativePosition = position;
            if (mCycleRolling && relativePosition >= mDataSourceList.size()) {  // 如果ViewPager是循环滚动的则要计算下标位置指定数据源,避免下标越界.
                relativePosition = position % mDataSourceList.size();
            }

            View content = ((ViewGroup) holder.itemView).getChildAt(0);
            if (content == null) {
                content = mOnPagerCallback.onCreateView(((ViewGroup) holder.itemView), mDataSourceList.get(relativePosition));
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -1, Gravity.CENTER);
                content.setLayoutParams(layoutParams);
                ((ViewGroup) holder.itemView).addView(content, 0);
            }

            final int finalRelativePosition = relativePosition;
            content.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnPagerCallback.onItemClick(v, finalRelativePosition);
                }
            });

            int padding = dip2px(mContext, mPagePadding);
            holder.itemView.setPadding(padding, 0, padding, 0);
            int leftMarin = (relativePosition == 0 ? padding + dip2px(mContext, mShowLeftCardWidth) : 0);
            int rightMarin = (relativePosition == getItemCount() - 1 ? padding + dip2px(mContext, mShowLeftCardWidth) : 0);

            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
            if (lp.leftMargin != leftMarin || lp.topMargin != 0 || lp.rightMargin != rightMarin || lp.bottomMargin != 0) {
                lp.setMargins(leftMarin, 0, rightMarin, 0);
                holder.itemView.setLayoutParams(lp);
            }
        }

        @Override
        public int getItemCount() {
            return mCycleRolling ? Integer.MAX_VALUE : mDataSourceList.size();
        }
    }

    private class ScaleHelper extends LinearSnapHelper {
        RecyclerView recyclerView;
        int mCurrentItemOffset;

        public ScaleHelper(final RecyclerView recyclerView) {
            this.recyclerView = recyclerView;

            this.attachToRecyclerView(recyclerView);

            this.recyclerView.addOnScrollListener(new OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        mNoNeedToScroll = mCurrentItemOffset == 0 || mCurrentItemOffset == getDestItemOffset(recyclerView.getAdapter().getItemCount() - 1);
                    } else {
                        mNoNeedToScroll = false;
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    mCurrentItemOffset += dx;   // dx>0则表示右滑, dx<0表示左滑, dy<0表示上滑, dy>0表示下滑
                    computeCurrentItemPos();
                    onScrolledChangedCallback();
                }
            });

            if (mCycleRolling) {    // 如果是循环滚动 则定位到中间显示
                mCurrentItemPos = mDataSourceList.size() / 2 +
                        (Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2 % mDataSourceList.size())); // 计算让让ViewPager默认显示中间的Item
            } else {
                mCurrentItemPos = mDataSourceList.size() / 2;
            }

            this.recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    mCardGalleryWidth = recyclerView.getWidth();
                    mCardWidth = mCardGalleryWidth - dip2px(mContext, 2 * (mPagePadding + mShowLeftCardWidth));
                    mOnePageWidth = mCardWidth;
                    recyclerView.smoothScrollToPosition(mCurrentItemPos);   // scrollToPosition 会失去缩放效果
                    onScrolledChangedCallback();
                }
            });
        }

        @Override
        public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
            if (mNoNeedToScroll) {
                return new int[]{0, 0};
            } else {
                return super.calculateDistanceToFinalSnap(layoutManager, targetView);
            }
        }


        private int getDestItemOffset(int destPos) {
            return mOnePageWidth * destPos;
        }

        // 计算mCurrentItemOffset
        private void computeCurrentItemPos() {
            if (mOnePageWidth <= 0) return;
            boolean pageChanged = false;
            if (Math.abs(mCurrentItemOffset - mCurrentItemPos * mOnePageWidth) >= mOnePageWidth) {
                pageChanged = true; // 滑动超过一页说明已翻页
            }
            if (pageChanged) {
                mCurrentItemPos = mCurrentItemOffset / mOnePageWidth;
            }
        }

        // RecyclerView位移事件监听, view大小随位移事件变化
        private void onScrolledChangedCallback() {
            int offset = mCurrentItemOffset - mCurrentItemPos * mOnePageWidth;
            float percent = (float) Math.max(Math.abs(offset) * 1.0 / mOnePageWidth, 0.0001);

            View leftView = null;
            View currentView;
            View rightView = null;
            if (mCurrentItemPos > 0) {
                leftView = recyclerView.getLayoutManager().findViewByPosition(mCurrentItemPos - 1);
            }
            currentView = recyclerView.getLayoutManager().findViewByPosition(mCurrentItemPos);
            if (mCurrentItemPos < recyclerView.getAdapter().getItemCount() - 1) {
                rightView = recyclerView.getLayoutManager().findViewByPosition(mCurrentItemPos + 1);
            }
            if (leftView != null) {
                leftView.setScaleY((1 - mScale) * percent + mScale);// y = (1 - mScale)x + mScale
            }
            if (currentView != null) {
                currentView.setScaleY((mScale - 1) * percent + 1);// y = (mScale - 1)x + 1
            }
            if (rightView != null) {
                rightView.setScaleY((1 - mScale) * percent + mScale);// y = (1 - mScale)x + mScale
            }
        }
    }

    protected int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
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
//        this.mCycleRolling = cycleRolling;    // TODO 暂时不支持无限滚动
        this.mOnPagerCallback = callback;
        if (dataSourceList instanceof List) {
            this.mDataSourceList = (List<Object>) dataSourceList;
        } else {
            throw new ClassCastException("dataSourceList 必须是列表类型 List");
        }
    }
}
