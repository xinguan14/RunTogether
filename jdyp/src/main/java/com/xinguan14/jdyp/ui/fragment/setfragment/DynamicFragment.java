package com.xinguan14.jdyp.ui.fragment.setfragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.base.ParentWithNaviFragment;
import com.xinguan14.jdyp.stikkyHeader.DynamicAnimator;
import com.xinguan14.jdyp.stikkyHeader.core.StikkyHeaderBuilder;
import com.xinguan14.jdyp.ui.fragment.SetFragment;

/**
 * 关于软件
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

        String[] mFrags = {"版本号：1.0.0",
                "微博：建大约跑",
                "微信：建大约跑",
                "地址：济南市历城区凤鸣路1000号山东建筑大学",
                "山东建筑大学信管14开发团队",
                "客服电话：17865165522",
                "客服QQ:1472610316"};

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mFrags);
        mListView.setAdapter(arrayAdapter);

        DynamicAnimator animator = new DynamicAnimator(getActivity());
        StikkyHeaderBuilder.stickTo(mListView)
                .setHeader(R.id.header, (ViewGroup) getView())
                .minHeightHeaderDim(R.dimen.min_height_header_materiallike)
                .animator(animator)
                .build();
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