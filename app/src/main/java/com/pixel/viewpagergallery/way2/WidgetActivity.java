package com.pixel.viewpagergallery.way2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.pixel.viewpagergallery.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现方式二：使用自定义控件,抽离业务无关代码.
 */
public class WidgetActivity extends AppCompatActivity {

    private PagerGalleryView mPagerGalleryView;

    private final List<String> stringList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget);

        for (int i = 0; i < 10; i++) {
            stringList.add("item " + i);
        }

        mPagerGalleryView = (PagerGalleryView) findViewById(R.id.pagerGalleryView);
        mPagerGalleryView.setDataSource(true, stringList, new PagerGalleryView.OnPagerCallback() {

            @Override
            public View onCreateView(ViewGroup parent, Object dataSource) {
                View view = getLayoutInflater().inflate(R.layout.pager_view, null);
                TextView textView = (TextView) view.findViewById(R.id.textView);
                textView.setText(dataSource.toString());
                return view;
            }

            @Override
            public void onItemClick(View item, int position) {
                Toast.makeText(WidgetActivity.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
