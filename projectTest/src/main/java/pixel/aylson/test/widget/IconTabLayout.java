package pixel.aylson.test.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.TintTypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by pixel on 2017/6/22.
 * <p>
 * 尝试让TabItem能修改图标
 */

@Deprecated
public class IconTabLayout extends TabLayout {

    public IconTabLayout(Context context) {
        super(context);
    }

    public IconTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IconTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addTabFromItemView(@NonNull IconTabItem item) {
        final Tab tab = newTab();
        if (item.mText != null) {
            tab.setText(item.mText);
        }
        if (item.mIcon != null) {
            tab.setIcon(item.mIcon);
        }
        if (item.mCustomLayout != 0) {
            tab.setCustomView(item.mCustomLayout);
        }
        if (!TextUtils.isEmpty(item.getContentDescription())) {
            tab.setContentDescription(item.getContentDescription());
        }
        addTab(tab);
    }

    public void addViewInternal(final View child) {
        if (child instanceof TabItem) {
            addTabFromItemView((IconTabItem) child);
        } else {
            throw new IllegalArgumentException("Only TabItem instances can be added to TabLayout");
        }
    }

    public static class IconTabItem extends View {
        final CharSequence mText;
        final Drawable mIcon;
        final int mCustomLayout;

        public IconTabItem(Context context) {
            this(context, null);
        }

        public IconTabItem(Context context, AttributeSet attrs) {
            super(context, attrs);

            final TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs,
                    android.support.design.R.styleable.TabItem);
            mText = a.getText(android.support.design.R.styleable.TabItem_android_text);
            mIcon = a.getDrawable(android.support.design.R.styleable.TabItem_android_icon);
            mCustomLayout = a.getResourceId(android.support.design.R.styleable.TabItem_android_layout, 0);
            a.recycle();
        }
    }
}
