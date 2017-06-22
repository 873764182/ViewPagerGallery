package pixel.aylson.test;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.MenuItem;
import com.yydcdut.sdlv.SlideAndDragListView;

import java.util.ArrayList;
import java.util.List;

import pixel.aylson.test.sort.DefaultItemTouchHelper;
import pixel.aylson.test.widget.SdListView;

/**
 * 测试即将在项目中使用的UI布局可行性
 * <p>
 * //        deviceLayoutManager.setSmoothScrollbarEnabled(true);
 * //        deviceLayoutManager.setAutoMeasureEnabled(true);
 * //        mDeviceRecyclerView.setHasFixedSize(true);
 * //        mDeviceRecyclerView.setNestedScrollingEnabled(false);
 */
public class MainActivity extends AppCompatActivity {
    private AppBarLayout mAppBarLayout;
    private RecyclerView mSceneRecyclerView;
    private RecyclerView mDeviceRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NestedScrollView mNestedScrollView;

    private MainPresenter mPresenter;
    private final List<String> sceneList = new ArrayList<>();
    private final List<String> deviceList = new ArrayList<>();

    private DefaultItemTouchHelper.PackTouchHelper packTouchHelper;
    private boolean canScrollVertically = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAppBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        mSceneRecyclerView = (RecyclerView) findViewById(R.id.sceneRecyclerView);
        mDeviceRecyclerView = (RecyclerView) findViewById(R.id.deviceRecyclerView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mNestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

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
        mDeviceRecyclerView.setNestedScrollingEnabled(false);
        mDeviceRecyclerView.setLayoutManager(deviceLayoutManager);
        mDeviceRecyclerView.setAdapter(deviceAdapter);
        packTouchHelper = mPresenter.initSort(mDeviceRecyclerView, deviceList);
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

    // 房间设备 ViewHolder
    public class DeviceViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView text;
        TextView text2;
        SdListView listView;

        public DeviceViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            text = (TextView) itemView.findViewById(R.id.text);
            text2 = (TextView) itemView.findViewById(R.id.text2);
            listView = (SdListView) itemView.findViewById(R.id.nestedListView);
        }
    }

    // 房间设备 Adapter
    public class DeviceAdapter extends RecyclerView.Adapter<DeviceViewHolder> {
        private boolean open = true;

        @Override
        public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new DeviceViewHolder(getLayoutInflater().inflate(R.layout.item_device, parent, false));
        }

        @Override
        public void onBindViewHolder(final DeviceViewHolder holder, final int position) {
            holder.text.setText(deviceList.get(position));

            if (!open) {
                holder.listView.setVisibility(View.GONE);
                holder.image.setRotation(0);
            } else {
                holder.listView.setVisibility(View.VISIBLE);
                holder.image.setRotation(90);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    open = true;

                    if (holder.listView.getVisibility() == View.GONE) {
                        holder.listView.setVisibility(View.VISIBLE);
                        holder.image.setRotation(90);
                    } else {
                        holder.listView.setVisibility(View.GONE);
                        holder.image.setRotation(0);
                    }
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    open = false;
                    DeviceAdapter.this.notifyDataSetChanged();
                    return true;
                }
            });
            final List<String> list = mPresenter.getChildList(holder.listView, position);
            final ChildListAdapter adapter = new ChildListAdapter(list);
            holder.listView.setMenu(adapter.getManu());
            holder.listView.setAdapter(adapter);
            holder.listView.setOnMenuItemClickListener(adapter);
            holder.listView.setOnItemClickListener(adapter);
            holder.listView.setOnItemLongClickListener(adapter);

            holder.text2.setText(list.size() + "");
        }

        @Override
        public int getItemCount() {
            return deviceList.size();
        }
    }

    // 子项 ViewHolder
    private class ChildViewHolder {
        TextView textView;
    }

    // 子项 Adapter
    private class ChildListAdapter extends BaseAdapter implements
            SlideAndDragListView.OnMenuItemClickListener,
            AdapterView.OnItemClickListener,
            AdapterView.OnItemLongClickListener {

        List<String> list;

        public ChildListAdapter(List<String> childList) {
            this.list = childList;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ChildViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.child_view, parent, false);
                viewHolder = new ChildViewHolder();
                viewHolder.textView = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ChildViewHolder) convertView.getTag();
            }
            viewHolder.textView.setText(list.get(position));
            return convertView;
        }

        public Menu getManu() {
            Menu menu = new Menu(true, 0);
            menu.addItem(new MenuItem.Builder().setWidth(200)//设置宽度
                    .setBackground(new ColorDrawable(Color.RED))
                    .setText("隐藏")
                    .setDirection(MenuItem.DIRECTION_RIGHT)
                    .setTextColor(Color.WHITE)
                    .setTextSize(14)
                    .build());
            menu.addItem(new MenuItem.Builder().setWidth(200)//设置宽度
                    .setBackground(new ColorDrawable(Color.YELLOW))
                    .setText("编辑")
                    .setTextColor(Color.GRAY)
                    .setDirection(MenuItem.DIRECTION_RIGHT)
                    //.setIcon(getResources().getDrawable(R.mipmap.ic_launcher, getApplicationContext().getTheme()))
                    .setTextSize(14)
                    .build());
            return menu;
        }

        @Override
        public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
            Toast.makeText(MainActivity.this, itemPosition + " --- " + buttonPosition + " --- " + direction, Toast.LENGTH_SHORT).show();
            if (buttonPosition == 1) {
                showSortView(this, list);
            }
            return Menu.ITEM_SCROLL_BACK;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(MainActivity.this, "点击 " + position, Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            showSortView(this, list);
            return true;
        }
    }

    public void showSortView(final BaseAdapter adapter, List list) {
        View view = getLayoutInflater().inflate(R.layout.sirt_view, null);
        Button completeButton = (Button) view.findViewById(R.id.completeButton);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        final Dialog dialog = new Dialog(this, R.style.Dialog_Fullscreen);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(view);
        dialog.show();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                adapter.notifyDataSetChanged(); // 刷新主列表
            }
        });

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new SortAdapter(list));
        mPresenter.initSort(recyclerView, list);
    }

    private class SortViewHolder extends RecyclerView.ViewHolder {
        private TextView text;

        public SortViewHolder(View itemView) {
            super(itemView);

            text = (TextView) itemView.findViewById(R.id.text);
        }
    }

    private class SortAdapter extends RecyclerView.Adapter<SortViewHolder> {
        List<String> list;

        public SortAdapter(List<String> childList) {
            this.list = childList;
        }

        @Override
        public SortViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SortViewHolder(getLayoutInflater().inflate(R.layout.sort_item, parent, false));
        }

        @Override
        public void onBindViewHolder(SortViewHolder holder, int position) {
            holder.text.setText(list.get(position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

}
