package pixel.aylson.test.fragment;


import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
import java.util.Collections;
import java.util.List;

import pixel.aylson.test.R;
import pixel.aylson.test.sort.DefaultItemTouchHelpCallback;
import pixel.aylson.test.sort.DefaultItemTouchHelper;
import pixel.aylson.test.widget.SdListView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainHomeFragment extends MainBaseFragment {
    private AppBarLayout mAppBarLayout;
    private RecyclerView mSceneRecyclerView;
    private RecyclerView mDeviceRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private final List<String> sceneList = new ArrayList<>();
    private final List<String> deviceList = new ArrayList<>();

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_home, container, false);

        mAppBarLayout = (AppBarLayout) view.findViewById(R.id.appBarLayout);
        mSceneRecyclerView = (RecyclerView) view.findViewById(R.id.sceneRecyclerView);
        mDeviceRecyclerView = (RecyclerView) view.findViewById(R.id.deviceRecyclerView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);

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

        SceneAdapter sceneAdapter = new SceneAdapter();
        LinearLayoutManager sceneLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mSceneRecyclerView.setLayoutManager(sceneLayoutManager);
        mSceneRecyclerView.setAdapter(sceneAdapter);
        initSort(mSceneRecyclerView, sceneList);
        refreshSceneList(mSceneRecyclerView, sceneList);

        DeviceAdapter deviceAdapter = new DeviceAdapter();
        LinearLayoutManager deviceLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mDeviceRecyclerView.setNestedScrollingEnabled(false);
        mDeviceRecyclerView.setLayoutManager(deviceLayoutManager);
        mDeviceRecyclerView.setAdapter(deviceAdapter);
        initSort(mDeviceRecyclerView, deviceList);
        refreshDeviceList(mDeviceRecyclerView, deviceList);

        return view;
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
            View item = getActivity().getLayoutInflater().inflate(R.layout.item_scene, parent, false);
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
            return new DeviceViewHolder(getActivity().getLayoutInflater().inflate(R.layout.item_device, parent, false));
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
            final List<String> list = getChildList(holder.listView, position);
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
                convertView = getActivity().getLayoutInflater().inflate(R.layout.child_view, parent, false);
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
            Toast.makeText(getActivity(), itemPosition + " --- " + buttonPosition + " --- " + direction, Toast.LENGTH_SHORT).show();
            if (buttonPosition == 1) {
                showSortView(this, list);
            }
            return Menu.ITEM_SCROLL_BACK;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getActivity(), "点击 " + position, Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            showSortView(this, list);
            return true;
        }
    }

    public void showSortView(final BaseAdapter adapter, List list) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.sirt_view, null);
        Button completeButton = (Button) view.findViewById(R.id.completeButton);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        final Dialog dialog = new Dialog(getActivity(), R.style.Dialog_Fullscreen);
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new SortAdapter(list));
        initSort(recyclerView, list);
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
            return new SortViewHolder(getActivity().getLayoutInflater().inflate(R.layout.sort_item, parent, false));
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
