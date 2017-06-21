package pixel.aylson.test.widget;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by pixel on 2017/6/21.
 */

public class SwipeItemLayout extends FrameLayout {

    private FrameLayout.LayoutParams rootParams = null;
    private Context context;
    private View itemView;
    private String[] menus;

    public SwipeItemLayout(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public SwipeItemLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public SwipeItemLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public SwipeItemLayout(Context context, View itemView, String... menus) {
        super(context);
        this.context = context;
        this.itemView = itemView;
        this.menus = menus;

//        postDelayed(new Runnable() {
//            @Override
//            public void run() {
        init();
//            }
//        }, 100);
    }

    protected void init() {

//        ListView.LayoutParams listParams = new ListView.LayoutParams(-1, -1);
//        this.setLayoutParams(listParams);

        FrameLayout rootLayout = new FrameLayout(context);
        rootParams = new FrameLayout.LayoutParams(-1, -1);

        LinearLayout menuLayout = new LinearLayout(context);
        menuLayout.setOrientation(LinearLayout.HORIZONTAL);
        FrameLayout.LayoutParams menuParams = new FrameLayout.LayoutParams(-2, -1, Gravity.RIGHT);
        menuLayout.setLayoutParams(menuParams);
        for (String menu : menus) {
            TextView textView = new TextView(context);
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(-2, -1, Gravity.CENTER);
            textView.setLayoutParams(textParams);
            textView.setText(menu);
            menuLayout.addView(textView);
        }
        rootLayout.addView(menuLayout, 0);

        FrameLayout.LayoutParams itemParams = new FrameLayout.LayoutParams(-1, -1);
        itemView.setLayoutParams(itemParams);
        rootLayout.addView(itemView, 1);

        this.addView(rootLayout);
    }

    private float downX;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            return true;
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if (downX == 0) downX = ev.getX();
            rootParams.rightMargin = (int) Math.abs(ev.getX() - downX);
            this.setLayoutParams(rootParams);
            return true;
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            rootParams.rightMargin = (int) (downX = 0);
            this.setLayoutParams(rootParams);
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }
}
