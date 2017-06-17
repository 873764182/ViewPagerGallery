package com.pixel.viewpagergallery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 通过RecyclerView实现画廊效果
 * <p>
 * http://www.jianshu.com/p/85bf072bfeed 参考
 */
public class RecyclerActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;

    private final List<String> stringList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        for (int i = 0; i < 10; i++) {
            stringList.add("item " + i);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(new RecyclerAdapter());
        final LinearSnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) { // 小于0代表从第10 item 划向第一 item, 反之亦然.
                View snapView = helper.findSnapView(linearLayoutManager);
                Log.e("mRecyclerView", "dx " + dx + "\t====\tdy " + dy + "\t" + snapView.getTag());
            }
        });
    }

    private final class RecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            textView = (TextView) itemView.findViewById(R.id.textView);
        }
    }

    private final class RecyclerAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
        private int pw, ph;

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (pw == 0) pw = mRecyclerView.getMeasuredWidth();
            if (ph == 0) ph = mRecyclerView.getMeasuredHeight();

            RecyclerViewHolder viewHolder = new RecyclerViewHolder(getLayoutInflater().inflate(R.layout.recycler_item, null));
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(pw / 2, ph / 2);
            layoutParams.setMargins(16, 16, 16, 16);
            viewHolder.itemView.setLayoutParams(layoutParams);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolder holder, int position) {
            holder.textView.setText(stringList.get(position));
            holder.itemView.setTag(position);   // 保存下标
        }

        @Override
        public int getItemCount() {
            return stringList.size();
        }
    }

}
