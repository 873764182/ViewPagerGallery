package com.pixel.viewpagergallery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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
        mPagerGalleryView.setDataSource(false, stringList, new PagerGalleryView.OnPagerCallback() {

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
