package pixel.aylson.test;

import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.MenuItem;
import com.yydcdut.sdlv.SlideAndDragListView;

import java.util.List;

public class SdlvActivity extends AppCompatActivity {

    //每一行的隐藏菜单
    private Menu nMenu;
    //每一行的填充数据源
    private List<ApplicationInfo> mListApp;
    //滑动和拖动控件
    private SlideAndDragListView<ApplicationInfo> mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdlv);

        //初始化数据
        initData();
        //初始化list中的隐藏菜单项
        initMenu();


        //初始化控件
        mListView = (SlideAndDragListView<ApplicationInfo>) findViewById(R.id.slideAndDragListView);
        //为mListView设置隐藏菜单
        mListView.setMenu(nMenu);
        //设置适配器
        mListView.setAdapter(mAdapter);

        /**
         * 设置监听器
         */

        //设置长按监听
//        mListView.setOnListItemLongClickListener(new SlideAndDragListView.OnListItemLongClickListener() {
//            @Override
//            public void onListItemLongClick(View view, int position) {
//                Toast.makeText(SdlvActivity.this, "长按list" + position, Toast.LENGTH_SHORT).show();
//            }
//        });

        // 设置移动控件监听
        mListView.setOnDragListener(new SlideAndDragListView.OnDragListener() {
            @Override
            //移动控件开始
            public void onDragViewStart(int position) {
                Toast.makeText(SdlvActivity.this, "移动控件开始" + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            //移动控件进行中
            public void onDragViewMoving(int position) {
                Toast.makeText(SdlvActivity.this, "移动控件进行中" + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            //移动控件移动停止
            public void onDragViewDown(int position) {
                Toast.makeText(SdlvActivity.this, "移动控件移动停止" + position, Toast.LENGTH_SHORT).show();
            }
            //这里还有一个参数
        }, mListApp);


        //点击监听
//        mListView.setOnListItemClickListener(new SlideAndDragListView.OnListItemClickListener() {
//            @Override
//            public void onListItemClick(View v, int position) {
//                Toast.makeText(SdlvActivity.this, "点击" + position, Toast.LENGTH_SHORT).show();
//            }
//        });


        //滑动监听
        mListView.setOnSlideListener(new SlideAndDragListView.OnSlideListener() {
            @Override
            public void onSlideOpen(View view, View parentView, int position, int direction) {
                Toast.makeText(SdlvActivity.this, "滑动打开监听" + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            //滑动关闭监听
            public void onSlideClose(View view, View parentView, int position, int direction) {
                Toast.makeText(SdlvActivity.this, "滑动关闭监听" + position, Toast.LENGTH_SHORT).show();
            }
        });

        //隐藏菜单子项点击监听
        mListView.setOnMenuItemClickListener(new SlideAndDragListView.OnMenuItemClickListener() {
            @Override
            public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {

                switch (direction) {
                    //如果是打开左边的item
                    case MenuItem.DIRECTION_LEFT:
                        switch (buttonPosition) {
                            case 0:
                                //无效果
                                return Menu.ITEM_NOTHING;
                            case 1:
                                //滑动关闭listView的子项
                                return Menu.ITEM_SCROLL_BACK;

                        }
                        break;

                    //如果是打开右边的item
                    case MenuItem.DIRECTION_RIGHT:
                        switch (buttonPosition) {
                            case 0:
                                //滑动关闭listView的子项
                                return Menu.ITEM_SCROLL_BACK;
                            case 1:
                                //从底部到顶部删除listView的子项，选择这个会调用
                                // setOnItemDeleteListener的onItemDelete方法
                                return Menu.ITEM_DELETE_FROM_BOTTOM_TO_TOP;

                        }
                        break;
                }
                return Menu.ITEM_NOTHING;
            }
        });

        //删除Item监听
        mListView.setOnItemDeleteListener(new SlideAndDragListView.OnItemDeleteListener() {
            @Override
            public void onItemDeleteAnimationFinished(View view, int position) {
                Toast.makeText(SdlvActivity.this, "删除" + position, Toast.LENGTH_SHORT).show();
                mListApp.remove(position - mListView.getHeaderViewsCount());
                mAdapter.notifyDataSetChanged();
            }
        });


        //滚动监听
//        mListView.setOnListScrollListener(new SlideAndDragListView.OnListScrollListener() {
//            @Override
//            //滑动状态改变监听
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//                switch (scrollState) {
//                    case SlideAndDragListView.OnListScrollListener.SCROLL_STATE_IDLE:
//                        break;
//                    case SlideAndDragListView.OnListScrollListener.SCROLL_STATE_TOUCH_SCROLL:
//                        break;
//                    case SlideAndDragListView.OnListScrollListener.SCROLL_STATE_FLING:
//                        break;
//                }
//
//            }
//
//            @Override
//            //当滚动的时候触发
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                //会一直调用
//                //Toast.makeText(SdlvActivity.this, "正在滚动" , Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    //初始化数据方法
    public void initData() {
        //得到手机上App的相关信息
        mListApp = getPackageManager().getInstalledApplications(0);
    }

    //初始化list中的菜单项
    public void initMenu() {
        //第一个参数表示是否设置背景透明而拖,第二个参数表示是否可以滑过。
        nMenu = new Menu(true, 0);

        //设置各种属性
        nMenu.addItem(new MenuItem.Builder().setWidth(150)//设置宽度
                //设置背景
                //.setBackground(getResources().getDrawable(R.drawable.index, getApplicationContext().getTheme()))
                .setText("无效果")
                .setTextColor(Color.GRAY)
                .setTextSize(14)
                .build());

        nMenu.addItem(new MenuItem.Builder().setWidth(150)//设置宽度
                //设置背景
                //.setBackground(getResources().getDrawable(R.drawable.index, getApplicationContext().getTheme()))
                .setText("关闭")
                .setTextColor(Color.BLACK)
                .setTextSize(14)
                .build());

        nMenu.addItem(new MenuItem.Builder().setWidth(200)//设置宽度
                //设置背景
                //.setBackground(getResources().getDrawable(R.drawable.index, getApplicationContext().getTheme()))
                .setText("关闭")
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .setTextColor(Color.BLACK)
                .setTextSize(14)
                .build());

        nMenu.addItem(new MenuItem.Builder().setWidth(200)//设置宽度
                //设置背景
                //.setBackground(getResources().getDrawable(R.drawable.index, getApplicationContext().getTheme()))
                .setText("删除")
                .setTextColor(Color.BLUE)
                .setDirection(MenuItem.DIRECTION_RIGHT)
                //.setIcon(getResources().getDrawable(R.mipmap.ic_launcher, getApplicationContext().getTheme()))
                .setTextSize(14)
                .build());

    }

    //适配器

    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mListApp.size();


        }

        @Override
        public Object getItem(int position) {
            return mListApp.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;

            if (convertView == null) {
                vh = new ViewHolder();
                //加载布局
                convertView = LayoutInflater.from(SdlvActivity.this).inflate(R.layout.item_custom_btn, null);
                //初始化控件
                vh.imgLogo = (ImageView) convertView.findViewById(R.id.img_item_edit);
                vh.txtName = (TextView) convertView.findViewById(R.id.txt_item_edit);
                vh.btnClick = (Button) convertView.findViewById(R.id.btn_item_click);

                //设置按钮点击监听
                vh.btnClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Object o = v.getTag();

                        if (o instanceof Integer) {
                            Toast.makeText(SdlvActivity.this, "点击按钮" + ((Integer) o), Toast.LENGTH_LONG).show();
                        }
                    }
                });

                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            //得到每一行的app相关信息
            ApplicationInfo item = (ApplicationInfo) this.getItem(position);

            //设置控件
            vh.txtName.setText(item.loadLabel(getPackageManager()));
            vh.imgLogo.setImageDrawable(item.loadIcon(getPackageManager()));
            vh.btnClick.setText(position + "");
            vh.btnClick.setTag(position);
            return convertView;
        }
    };

    private class ViewHolder {

        public ImageView imgLogo;
        public TextView txtName;
        public Button btnClick;
    }
}
