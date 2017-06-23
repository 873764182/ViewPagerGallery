package pixel.aylson.test;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by pixel on 2017/6/23.
 */

public class MainSupportPresenter {

    private MainSupportActivity activity;

    public MainSupportPresenter(MainSupportActivity mainSupportActivity) {
        this.activity = mainSupportActivity;
    }

    public interface OnTabSelectListener {
        void onSelect(int oldSelect, int newSelect, TabItem oldItem, TabItem newItem);
    }

    static class TabLayout {
        LinearLayout layout1;
        ImageView image1;
        TextView text1;
        LinearLayout layout2;
        ImageView image2;
        TextView text2;
        LinearLayout layout3;
        ImageView image3;
        TextView text3;
        LinearLayout layout4;
        ImageView image4;
        TextView text4;


        public TabLayout(View tabView) {
            layout1 = (LinearLayout) tabView.findViewById(R.id.layout_1);
            image1 = (ImageView) tabView.findViewById(R.id.image_1);
            text1 = (TextView) tabView.findViewById(R.id.text_1);
            layout2 = (LinearLayout) tabView.findViewById(R.id.layout_2);
            image2 = (ImageView) tabView.findViewById(R.id.image_2);
            text2 = (TextView) tabView.findViewById(R.id.text_2);
            layout3 = (LinearLayout) tabView.findViewById(R.id.layout_3);
            image3 = (ImageView) tabView.findViewById(R.id.image_3);
            text3 = (TextView) tabView.findViewById(R.id.text_3);
            layout4 = (LinearLayout) tabView.findViewById(R.id.layout_4);
            image4 = (ImageView) tabView.findViewById(R.id.image_4);
            text4 = (TextView) tabView.findViewById(R.id.text_4);
        }
    }

    static class TabItem {
        LinearLayout layout;
        ImageView image;
        TextView text;

        public TabItem(LinearLayout layout, ImageView image, TextView text) {
            this.layout = layout;
            this.image = image;
            this.text = text;
        }
    }

    public TabLayout initTabLayout(View tabView, final OnTabSelectListener listener, int select) {
        final TabLayout holder = new TabLayout(tabView);
        View.OnClickListener clickListener = new View.OnClickListener() {
            int previousSelect = 1;

            public TabItem getTabItem(int select) {
                TabItem tabItem = null;
                if (select == 1) {
                    tabItem = new TabItem(holder.layout1, holder.image1, holder.text1);
                } else if (select == 2) {
                    tabItem = new TabItem(holder.layout2, holder.image2, holder.text2);
                } else if (select == 3) {
                    tabItem = new TabItem(holder.layout3, holder.image3, holder.text3);
                } else if (select == 4) {
                    tabItem = new TabItem(holder.layout4, holder.image4, holder.text4);
                }
                return tabItem;
            }

            @Override
            public void onClick(View v) {
                if (v == holder.layout1) {
                    listener.onSelect(previousSelect, 1, getTabItem(previousSelect), getTabItem(1));
                    previousSelect = 1;
                } else if (v == holder.layout2) {
                    listener.onSelect(previousSelect, 2, getTabItem(previousSelect), getTabItem(2));
                    previousSelect = 2;
                } else if (v == holder.layout3) {
                    listener.onSelect(previousSelect, 3, getTabItem(previousSelect), getTabItem(3));
                    previousSelect = 3;
                } else if (v == holder.layout4) {
                    listener.onSelect(previousSelect, 4, getTabItem(previousSelect), getTabItem(4));
                    previousSelect = 4;
                }
            }
        };
        holder.layout1.setOnClickListener(clickListener);
        holder.layout2.setOnClickListener(clickListener);
        holder.layout3.setOnClickListener(clickListener);
        holder.layout4.setOnClickListener(clickListener);

        switch (select) {
            case 1:
                holder.layout1.callOnClick();
                break;
            case 2:
                holder.layout2.callOnClick();
                break;
            case 3:
                holder.layout3.callOnClick();
                break;
            case 4:
                holder.layout4.callOnClick();
                break;
            default:
                holder.layout1.callOnClick();
        }

        return holder;
    }

}
