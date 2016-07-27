package com.xinguan14.jdyp.StikkyHeader.example;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.base.ParentWithNaviActivity;
import com.xinguan14.jdyp.base.ParentWithNaviFragment;
import com.xinguan14.jdyp.bean.User;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by wm on 2016/7/27.
 */
public class ChangeUserName extends ParentWithNaviFragment {
    @Bind(R.id.update_name)
    EditText updateName;
    private  String newName;
    private FragmentManager manager;
    private FragmentTransaction ft;

    @Override
    protected String title() {
        return "更改名字";
    }
    @Override
    public Object right() {
        return R.drawable.base_action_bar_add_bg_selector;
    }
    @Override
    public Object left() {
        return R.drawable.base_action_bar_back_bg_selector;
    }

    @Override
    public ParentWithNaviActivity.ToolBarListener setToolBarListener() {
        return new ParentWithNaviActivity.ToolBarListener() {
            @Override
            public void clickLeft() {
                Fragment changeUserNamefragment = new ChangeMyInfoFragment();
                ft = manager.beginTransaction();
                ft.replace(R.id.id_content, changeUserNamefragment);
                ft.addToBackStack(null);
                ft.commit();
            }

            @Override
            public void clickRight()
            {
                newName = updateName.getText().toString();
                if(newName.length()!=0) {
                    User newUser = new User();
                    newUser.setUsername(newName);
                    //获取当前用户
                    User user = BmobUser.getCurrentUser(getActivity(), User.class);
                    newUser.update(getActivity(), user.getObjectId(), new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            // TODO Auto-generated method stub
                            toast("更新用户信息成功:");
                            Fragment changeUserNamefragment = new ChangeMyInfoFragment();
                            ft = manager.beginTransaction();
                            ft.replace(R.id.id_content, changeUserNamefragment);
                            ft.addToBackStack(null);
                            ft.commit();
                        }

                        @Override
                        public void onFailure(int code, String msg) {
                            // TODO Auto-generated method stub
                            toast("更新用户信息失败:" + msg);
                        }
                    });
                }else {
                    Toast.makeText(getActivity(), "用户名不能为空", Toast.LENGTH_SHORT).show();
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
        manager = getFragmentManager();



    }
}
