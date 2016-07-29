package com.xinguan14.jdyp.StikkyHeader.example;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.base.ParentWithNaviActivity;
import com.xinguan14.jdyp.base.ParentWithNaviFragment;
import com.xinguan14.jdyp.ui.fragment.SetFragment;

import butterknife.ButterKnife;

/**
 * Created by wm on 2016/7/27.
 */
public class ChangePassWord extends ParentWithNaviFragment {
    @Override
    protected String title() {
        return "修改密码";
    }

    @Override
    public Object right() {
        return R.drawable.base_action_bar_add_bg_selector;
    }


    @Override
    public ParentWithNaviActivity.ToolBarListener setToolBarListener() {
        return new ParentWithNaviActivity.ToolBarListener() {
            @Override
            public void clickLeft() {

            }

            @Override
            public void clickRight()
            {

            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.set_update_password, container, false);
        ButterKnife.bind(this, rootView);
        initNaviView();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof SetFragment.HideTab) {
            ((SetFragment.HideTab) getActivity()).hide();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
