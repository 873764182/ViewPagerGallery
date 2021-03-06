package com.pixel.viewpagergallery.way3;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pixel.viewpagergallery.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 抽离成控件后测试
 */
@Deprecated
public class Recycler2Activity extends AppCompatActivity {

    private RecyclerGalleryView mRecyclerGalleryView;

    private final List<String> stringList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler2);

        for (int i = 0; i < 5; i++) {
            stringList.add("item " + i);
        }

        mRecyclerGalleryView = (RecyclerGalleryView) findViewById(R.id.recyclerGalleryView);

        mRecyclerGalleryView.setDataSource(false, stringList, new RecyclerGalleryView.OnPagerCallback() {
            @Override
            public View onCreateView(ViewGroup parent, Object dataSource) {
                return getLayoutInflater().inflate(R.layout.recycler_item, parent, false);
            }

            @Override
            public void onItemClick(View item, int position) {
                Toast.makeText(Recycler2Activity.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
