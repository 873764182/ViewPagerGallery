package pixel.aylson.test.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.yydcdut.sdlv.SlideAndDragListView;

/**
 * Created by pixel on 2017/6/21.
 */

public class SdListView extends SlideAndDragListView {

    private OnInterceptTouchEvent onInterceptTouchEvent;

    public void setOnInterceptTouchEvent(OnInterceptTouchEvent onInterceptTouchEvent) {
        this.onInterceptTouchEvent = onInterceptTouchEvent;
    }

    public SdListView(Context context) {
        super(context);
    }

    public SdListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SdListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (onInterceptTouchEvent != null) {
            return onInterceptTouchEvent.onInterceptTouchEvent(this, ev);
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    public boolean superOnInterceptTouchEvent(MotionEvent ev){
        return super.onInterceptTouchEvent(ev);
    }

    public interface OnInterceptTouchEvent {
        boolean onInterceptTouchEvent(SdListView sdListView, MotionEvent ev);
    }

}
