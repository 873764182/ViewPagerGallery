package com.pixel.viewpagergallery.nest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pixel.viewpagergallery.R;
import com.pixel.viewpagergallery.sort.DefaultItemTouchHelpCallback;
import com.pixel.viewpagergallery.sort.DefaultItemTouchHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * 竖直方向RecyclerView嵌套
 */
public class NestActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private TextView mTitleText;

    private static final List<String> groupList = new ArrayList<>();
    private static final Map<String, List<String>> groupData = new Hashtable<>();

    static {    // 模拟测试数据
        for (int i = 0; i < 10; i++) {
            List<String> childList = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                childList.add("child -> " + j);
            }
            String key = "group -> " + i;
            groupData.put(key, childList);
            groupList.add(key);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nest);

        mTitleText = (TextView) findViewById(R.id.titleText);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new RecyclerAdapter(mRecyclerView, groupList, groupData));

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = ((LinearLayoutManager) mRecyclerView.getLayoutManager());
                int f1 = layoutManager.findFirstCompletelyVisibleItemPosition();    // 第一个完全可见的item(-1时代表屏幕上没有完全可见的item,可能item过大造成.)
                int f2 = layoutManager.findFirstVisibleItemPosition();  // 第一个可见的item(可见一部分也算)
                int l1 = layoutManager.findLastCompletelyVisibleItemPosition(); // 最后一个完全可见的item(-1时代表屏幕上没有完全可见的item,可能item过大造成.)
                int l2 = layoutManager.findLastVisibleItemPosition();   // 最后一个可见的item(可见一部分也算)

                mTitleText.setText(groupList.get(f2));  // 设置列表头标题

                View item = layoutManager.findViewByPosition(f2);

                Integer cY = dyMap.get(f2);
                if (cY == null) {
                    cY = 0;
                }
                dyMap.put(f2, cY + dy);

                TextView cTv = (TextView) item.findViewById(R.id.titleView);
                cTv.setText(groupList.get(f2));
                if (item.getMeasuredHeight() - dyMap.get(f2) <= mTitleText.getMeasuredHeight()) {
                    mTitleText.setVisibility(View.GONE);
                    cTv.setVisibility(View.VISIBLE);
                } else {
                    mTitleText.setVisibility(View.VISIBLE);
                    cTv.setVisibility(View.GONE);
                }

                Log.e("onScrolled", f1 + " === " + f2 + " === " + l1 + " === " + l2 + " ---> " + dy + " -- " + item.getMeasuredHeight() + " -- " + mTitleText.getMeasuredHeight());
            }
        });

        initSort(mRecyclerView, groupList);
    }

    Map<Integer, Integer> dyMap = new Hashtable<>();

    // 初始化拖动排序 滑动删除
    private void initSort(final RecyclerView recyclerView, final List<String> list) {
        DefaultItemTouchHelper.PackTouchHelper itemTouchHelper = new DefaultItemTouchHelper.PackTouchHelper(
                new DefaultItemTouchHelpCallback.OnItemTouchCallbackListener() {
                    @Override
                    public boolean onMove(int srcPosition, int targetPosition) {
                        Collections.swap(list, srcPosition, targetPosition);
                        recyclerView.getAdapter().notifyItemMoved(srcPosition, targetPosition);
                        return true;
                    }

                    @Override
                    public void onSwiped(int adapterPosition) {
                        list.remove(adapterPosition);
                        recyclerView.getAdapter().notifyItemRemoved(adapterPosition);
                    }
                });
        itemTouchHelper.attachToRecyclerView(recyclerView);
        itemTouchHelper.setDragEnable(true);
        itemTouchHelper.setSwipeEnable(false);   // 网格布局时滑动删除无效
    }

    /*外层RecyclerView ViewHolder*/
    private class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        RecyclerView recyclerView;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.textView);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView);
        }
    }

    /*外层RecyclerView Adapter*/
    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
        RecyclerView recyclerView;
        List<String> list;
        Map<String, List<String>> groupData;    // 所有的数据

        public RecyclerAdapter(RecyclerView recyclerView, List<String> list, Map<String, List<String>> groupData) {
            this.recyclerView = recyclerView;
            this.list = list;
            this.groupData = groupData;
        }

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerViewHolder(getLayoutInflater().inflate(R.layout.nest_recycler_view, parent, false));
        }

        @Override
        public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
            holder.textView.setText(list.get(position));
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(NestActivity.this, LinearLayoutManager.VERTICAL, false));
            holder.recyclerView.setItemAnimator(new DefaultItemAnimator());

            final List<String> childList = new ArrayList<>();
            childList.addAll(groupData.get(list.get(position)));    // 如果想要默认组是展开的 请保留这行
            holder.recyclerView.setAdapter(new ChildRecyclerAdapter(childList));

            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (childList.size() > 0) {
                        childList.clear();
//                        if (position > 0) {
//                            ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(position - 1, 0);    // 滚动到当前手动展开的item
//                        }
                    } else {
                        childList.addAll(groupData.get(list.get(position)));
//                        ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(position, 0);    // 滚动到当前手动展开的item
                    }
                    holder.recyclerView.getAdapter().notifyDataSetChanged();
                }
            });

            initSort(holder.recyclerView, list);
        }

        @Override
        public int getItemCount() {
            return list != null ? list.size() : 0;
        }
    }

    /*嵌套的RecyclerView ViewHolder*/
    private class ChildRecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ChildRecyclerViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.textView);
        }
    }

    /*嵌套的RecyclerView Adapter*/
    private class ChildRecyclerAdapter extends RecyclerView.Adapter<ChildRecyclerViewHolder> {
        List<String> list;

        public ChildRecyclerAdapter(List<String> list) {
            this.list = list;
        }

        @Override
        public ChildRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ChildRecyclerViewHolder(getLayoutInflater().inflate(R.layout.nest_recycler_child_view, parent, false));
        }

        @Override
        public void onBindViewHolder(ChildRecyclerViewHolder holder, int position) {
            holder.textView.setText(list.get(position));
            holder.itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return list != null ? list.size() : 0;
        }
    }
}
