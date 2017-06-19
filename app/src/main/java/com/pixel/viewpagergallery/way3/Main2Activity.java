package com.pixel.viewpagergallery.way3;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pixel.viewpagergallery.R;
import com.pixel.viewpagergallery.sort.DefaultItemTouchHelpCallback;
import com.pixel.viewpagergallery.sort.DefaultItemTouchHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main2Activity extends AppCompatActivity {
    private RGalleryView mRGalleryView;
    private final List<String> stringList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        for (int i = 0; i < 10; i++) {
            stringList.add("item " + i);
        }

        mRGalleryView = (RGalleryView) findViewById(R.id.rGalleryView);
        mRGalleryView.setDataSource(true, stringList, new RGalleryView.OnPagerCallback() {
            @Override
            public View onCreateView(ViewGroup parent, Object dataSource) {
                return getLayoutInflater().inflate(R.layout.recycler_item, parent, false);
            }

            @Override
            public void onItemClick(View item, int position) {
                Toast.makeText(Main2Activity.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });

//        initSort();
    }

    // 初始化拖动排序 滑动删除
    @Deprecated
    private void initSort() {
        DefaultItemTouchHelper.PackTouchHelper itemTouchHelper = new DefaultItemTouchHelper.PackTouchHelper(listener);
        itemTouchHelper.attachToRecyclerView(mRGalleryView);
        itemTouchHelper.setDragEnable(true);
        itemTouchHelper.setSwipeEnable(true);   // 网格布局时滑动删除无效
    }

    // 拖动/滑动 监听器 更新数据
    private final DefaultItemTouchHelpCallback.OnItemTouchCallbackListener listener = new DefaultItemTouchHelpCallback.OnItemTouchCallbackListener() {
        @Override
        public boolean onMove(int srcPosition, int targetPosition) {
            Collections.swap(stringList, srcPosition, targetPosition);
            mRGalleryView.getAdapter().notifyItemMoved(srcPosition, targetPosition);
            return true;
        }

        @Override
        public void onSwiped(int adapterPosition) {
            stringList.remove(adapterPosition);
            mRGalleryView.getAdapter().notifyItemRemoved(adapterPosition);
        }
    };
}
