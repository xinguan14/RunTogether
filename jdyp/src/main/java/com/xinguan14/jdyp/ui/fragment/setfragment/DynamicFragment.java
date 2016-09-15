package com.xinguan14.jdyp.ui.fragment.setfragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.stikkyHeader.DynamicAnimator;
import com.xinguan14.jdyp.stikkyHeader.Utils;
import com.xinguan14.jdyp.stikkyHeader.core.StikkyHeaderBuilder;
import com.xinguan14.jdyp.base.ParentWithNaviFragment;
import com.xinguan14.jdyp.ui.fragment.SetFragment;

/**
 * 我的动态
 * @author 徐玲
 */
public class DynamicFragment extends ParentWithNaviFragment {

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


    @Override
    public void onPause() {
        super.onPause();
        if (getActivity()instanceof SetFragment.AddMenu){
            ((SetFragment.AddMenu) getActivity()).showMenu();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof SetFragment.HideTab) {
            ((SetFragment.HideTab) getActivity()).hide();
        }
    }

    @Override
    protected String title() {
        return null;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}