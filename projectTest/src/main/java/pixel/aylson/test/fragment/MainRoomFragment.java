package pixel.aylson.test.fragment;


import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pixel.aylson.test.R;
import pixel.aylson.test.widget.PagerGalleryView;

/**
 * 房间
 */
public class MainRoomFragment extends MainBaseFragment {
    private View mSwitchView;
    private LinearLayout mPagerLayout;
    private PagerGalleryView mGalleryView;
    private LinearLayout mGradLayout;
    private RecyclerView mRecyclerView;
    private LinearLayout mEditLayout;
    private Button mEditBtn1;
    private Button mEditBtn2;

    private boolean isEdit = false;
    private final List<String> roomList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_room, container, false);
        mSwitchView = (View) view.findViewById(R.id.switchView);
        mPagerLayout = (LinearLayout) view.findViewById(R.id.pagerLayout);
        mGalleryView = (PagerGalleryView) view.findViewById(R.id.galleryView);
        mGradLayout = (LinearLayout) view.findViewById(R.id.gradLayout);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mEditLayout = (LinearLayout) view.findViewById(R.id.editLayout);
        mEditBtn1 = (Button) view.findViewById(R.id.editBtn_1);
        mEditBtn2 = (Button) view.findViewById(R.id.editBtn_2);

        for (int i = 0; i < 20; i++) {
            roomList.add("item " + i);
        }

        initGallery();
        initGrad();

        mSwitchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPagerLayout.getVisibility() == View.GONE) {
                    mPagerLayout.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.popup_dialog_enter));
                    mPagerLayout.setVisibility(View.VISIBLE);
                    mGradLayout.clearAnimation();
                    mGradLayout.setVisibility(View.GONE);
                } else {
                    mGradLayout.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.inputodown));
                    mGradLayout.setVisibility(View.VISIBLE);
                    mPagerLayout.clearAnimation();
                    mPagerLayout.setVisibility(View.GONE);
                }
            }
        });

        mEditBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEdit = false;
                mEditLayout.setVisibility(View.GONE);
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }
        });
        mEditBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEdit = false;
                mEditLayout.setVisibility(View.GONE);
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }
        });

        return view;
    }

    public void initGallery() {
        mGalleryView.setDataSource(true, roomList, new PagerGalleryView.OnPagerCallback() {

            @Override
            public View onCreateView(ViewGroup parent, Object dataSource) {
                View view = getActivity().getLayoutInflater().inflate(R.layout.pager_view, parent, false);
                TextView textView = (TextView) view.findViewById(R.id.textView);
                textView.setText(dataSource.toString());
                return view;
            }

            @Override
            public void onItemClick(View item, int position) {
                Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initGrad() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mRecyclerView.setAdapter(new RecyclerAdapter());
    }

    private class RecyclerHolder extends RecyclerView.ViewHolder {
        TextView textView;
        CheckBox checkBox;

        public RecyclerHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
        }
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerHolder> {

        @Override
        public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerHolder(getActivity().getLayoutInflater().inflate(R.layout.pager_view, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerHolder holder, int position) {
            holder.textView.setText(roomList.get(position));
            if (isEdit) {
                holder.checkBox.setVisibility(View.VISIBLE);
            } else {
                holder.checkBox.setVisibility(View.GONE);
            }
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    isEdit = true;
                    mEditLayout.setVisibility(View.VISIBLE);
                    RecyclerAdapter.this.notifyDataSetChanged();
                    Toast.makeText(getActivity(), v.toString(), Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return roomList.size();
        }
    }

}
