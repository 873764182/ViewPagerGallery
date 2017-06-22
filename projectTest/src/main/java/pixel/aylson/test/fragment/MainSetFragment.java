package pixel.aylson.test.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pixel.aylson.test.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainSetFragment extends MainBaseFragment {


    public MainSetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_set, container, false);
    }

}
