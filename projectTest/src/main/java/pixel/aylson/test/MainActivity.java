package pixel.aylson.test;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试即将在项目中使用的UI布局可行性
 */
public class MainActivity extends AppCompatActivity {
    private AppBarLayout mAppBarLayout;
    private RecyclerView mSceneRecyclerView;
    private RecyclerView mDeviceRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private MainPresenter mPresenter;
    private final List<String> sceneList = new ArrayList<>();
    private final List<String> deviceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAppBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        mSceneRecyclerView = (RecyclerView) findViewById(R.id.sceneRecyclerView);
        mDeviceRecyclerView = (RecyclerView) findViewById(R.id.deviceRecyclerView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setProgressViewOffset(false, mAppBarLayout.getMeasuredHeight(), (int) (mAppBarLayout.getMeasuredHeight() * 1.2f));
            }
        }, 1000);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {  // 标题栏展开了
                    swipeRefreshLayout.setEnabled(true);    // 展开时说明列表滑动到顶部了,避免出现滑动异常.理论上应该再判断RecyclerView是否到顶了,但是RecyclerView没有被回收难以判断.
                } else {
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });

        mPresenter = new MainPresenter(this);   // 创建业务控制器

        SceneAdapter sceneAdapter = new SceneAdapter();
        LinearLayoutManager sceneLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mSceneRecyclerView.setLayoutManager(sceneLayoutManager);
        mSceneRecyclerView.setAdapter(sceneAdapter);
        mPresenter.initSort(mSceneRecyclerView, sceneList);
        mPresenter.refreshSceneList(mSceneRecyclerView, sceneList);

        DeviceAdapter deviceAdapter = new DeviceAdapter();
        LinearLayoutManager deviceLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        deviceLayoutManager.setSmoothScrollbarEnabled(true);
//        deviceLayoutManager.setAutoMeasureEnabled(true);
//        mDeviceRecyclerView.setHasFixedSize(true);
        mDeviceRecyclerView.setNestedScrollingEnabled(false);
        mDeviceRecyclerView.setLayoutManager(deviceLayoutManager);
        mDeviceRecyclerView.setAdapter(deviceAdapter);
        mPresenter.initSort(mDeviceRecyclerView, deviceList);
        mPresenter.refreshDeviceList(mDeviceRecyclerView, deviceList);

    }

    // 场景 ViewHolder
    public class SceneViewHolder extends RecyclerView.ViewHolder {
        ImageView mImage;
        TextView mText;

        public SceneViewHolder(View itemView) {
            super(itemView);
            mImage = (ImageView) itemView.findViewById(R.id.image);
            mText = (TextView) itemView.findViewById(R.id.text);
        }
    }

    // 场景 Adapter
    public class SceneAdapter extends RecyclerView.Adapter<SceneViewHolder> {

        @Override
        public SceneViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int itemWidth = parent.getMeasuredWidth() / 4;
            int itemHeight = itemWidth;
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(itemWidth, itemHeight);
            View item = getLayoutInflater().inflate(R.layout.item_scene, parent, false);
            item.setLayoutParams(layoutParams);
            return new SceneViewHolder(item);
        }

        @Override
        public void onBindViewHolder(SceneViewHolder holder, int position) {
            holder.mText.setText(sceneList.get(position));
        }

        @Override
        public int getItemCount() {
            return sceneList.size();
        }
    }

    // 设备 ViewHolder
    public class DeviceViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView text;
        Switch switchView;

        public DeviceViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            text = (TextView) itemView.findViewById(R.id.text);
            switchView = (Switch) itemView.findViewById(R.id.switchView);
        }
    }

    // 设备 Adapter
    public class DeviceAdapter extends RecyclerView.Adapter<DeviceViewHolder> {

        @Override
        public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new DeviceViewHolder(getLayoutInflater().inflate(R.layout.item_device, parent, false));
        }

        @Override
        public void onBindViewHolder(DeviceViewHolder holder, int position) {
            holder.text.setText(deviceList.get(position));
        }

        @Override
        public int getItemCount() {
            return deviceList.size();
        }
    }
}
