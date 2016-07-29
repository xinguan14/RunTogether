package com.xinguan14.jdyp.ui.fragment.setfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.base.ParentWithNaviActivity;
import com.xinguan14.jdyp.base.ParentWithNaviFragment;
import com.xinguan14.jdyp.ui.fragment.SetFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by wm on 2016/7/27.
 */
public class ChangeUserNameFragment extends ParentWithNaviFragment {

    @Bind(R.id.update_name)
    EditText updateName;
    private String newName;
    @Override
    protected String title() {
        return "更改名字";
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
            public void clickRight() {
                newName = updateName.getText().toString();
                if (newName.length() != 0) {
                    BmobUser newUser = new BmobUser();
                    newUser.setUsername(newName);
                    BmobUser bmobUser = BmobUser.getCurrentUser(getActivity());
                    newUser.update(getActivity(),bmobUser.getObjectId(),new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            // TODO Auto-generated method stub
                            toast("更新用户信息成功:");
                        }
                        @Override
                        public void onFailure(int code, String msg) {
                            // TODO Auto-generated method stub
                            toast("更新用户信息失败:" + msg);
                        }
                    });
                }else {
                    toast("用户名不能为空");
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.set_changes_username, container, false);
        ButterKnife.bind(this, rootView);
        initNaviView();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof SetFragment.HideTab) {
            ((SetFragment.HideTab) getActivity()).hide();
        }
    }
}
