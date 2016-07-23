package com.xinguan14.jdyp.StikkyHeader.example;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.StikkyHeader.DynamicAnimator;
import com.xinguan14.jdyp.StikkyHeader.Utils;
import com.xinguan14.jdyp.StikkyHeader.core.StikkyHeaderBuilder;


public class DynamicFragment extends Fragment {

    private ListView mListView;

    public DynamicFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dynamic, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView = (ListView) getView().findViewById(R.id.listview);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        DynamicAnimator animator = new DynamicAnimator(getActivity());

        StikkyHeaderBuilder.stickTo(mListView)
                .setHeader(R.id.header, (ViewGroup) getView())
                .minHeightHeaderDim(R.dimen.min_height_header_materiallike)
                .animator(animator)
                .build();

        Utils.populateListView(mListView);
    }

}
