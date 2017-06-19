package com.pixel.viewpagergallery.way3;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pixel.viewpagergallery.R;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {
    private RGalleryView mRGalleryView;
    private final List<String> stringList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        for (int i = 0; i < 10; i++) {
            stringList.add("item " + i);
        }

        mRGalleryView = (RGalleryView) findViewById(R.id.rGalleryView);
        mRGalleryView.setDataSource(true, stringList, new RGalleryView.OnPagerCallback() {
            @Override
            public View onCreateView(ViewGroup parent, Object dataSource) {
                return getLayoutInflater().inflate(R.layout.recycler_item, parent, false);
            }

            @Override
            public void onItemClick(View item, int position) {
                Toast.makeText(Main2Activity.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
