package pixel.aylson.test;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pixel.aylson.test.sort.DefaultItemTouchHelpCallback;
import pixel.aylson.test.sort.DefaultItemTouchHelper;

/**
 * Created by pixel on 2017/6/20.
 * <p>
 * MainActivity业务控制器
 */

public class MainPresenter {
    protected MainActivity activity;

    public MainPresenter(MainActivity activity) {
        this.activity = activity;
    }

    public void refreshSceneList(RecyclerView sceneRecyclerView, List<String> sceneList) {
        for (int i = 0; i < 10; i++) {
            sceneList.add("场景 " + i);
        }
        sceneRecyclerView.getAdapter().notifyDataSetChanged();
    }

    public void refreshDeviceList(final RecyclerView deviceRecyclerView, List<String> deviceList) {
        for (int i = 0; i < 10; i++) {
            deviceList.add("房间 " + i);
        }
        deviceRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                deviceRecyclerView.getAdapter().notifyDataSetChanged(); // 嵌套显示时,刷新太快会造成RecyclerView高度计算错误,显示不全.
            }
        }, 1000);
    }

    // 初始化拖动排序 滑动删除
    public DefaultItemTouchHelper.PackTouchHelper initSort(final RecyclerView recyclerView, final List list) {
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
        itemTouchHelper.setSwipeEnable(false);
        return itemTouchHelper;
    }

    public List<String> getChildList(View listView, int position) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            list.add("设备 - " + i + " - " + position);
        }
        return list;
    }
}
