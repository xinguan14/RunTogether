package com.xinguan14.jdyp.ui.fragment.setfragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.base.ParentWithNaviActivity;
import com.xinguan14.jdyp.base.ParentWithNaviFragment;
import com.xinguan14.jdyp.bean.User;
import com.xinguan14.jdyp.ui.fragment.SetFragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by wm on 2016/7/27.
 */
public class ChangeEmailFragment extends ParentWithNaviFragment {
    @Bind(R.id.update_name)
    EditText updateEmail;
    @Bind(R.id.tips)
    TextView tips;
    private  String newEmail;
    private FragmentManager manager;
    private FragmentTransaction ft;

    @Override
    protected String title() {
        return "更改邮箱";
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
//                Fragment changeUserNamefragment = new ChangeMyInfoFragment();
//                ft = manager.beginTransaction();
//                ft.replace(R.id.id_content, changeUserNamefragment);
//                ft.addToBackStack(null);
//                ft.commit();
            }

            @Override
            public void clickRight()
            {
                newEmail = updateEmail.getText().toString();
                if(newEmail.length()!=0) {
                    if(isEmail(newEmail)) {
                        User newUser = new User();
                        newUser.setEmail(newEmail);
                        //获取当前用户
                        User user = BmobUser.getCurrentUser(getActivity(), User.class);
                        newUser.update(getActivity(), user.getObjectId(), new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                toast("更新用户信息成功:");
                                Fragment changeUserNamefragment = new ChangeMyInfoFragment();
                                ft = manager.beginTransaction();
                                ft.replace(R.id.id_content, changeUserNamefragment);
                                ft.addToBackStack(null);
                                ft.commit();
                            }

                            @Override
                            public void onFailure(int code, String msg) {
                                toast("更新用户信息失败:" + msg);
                            }
                        });
                    }else {
                        Toast.makeText(getActivity(), "邮箱格式不正确", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getActivity(), "用户名不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    @Override
    public boolean onBackPressed() {
        return false;
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
        updateEmail.setHint("邮箱");
        tips.setText("请输入正确的邮箱地址");
    }
    /**
     * 判断邮箱是否合法
     * @param email
     * @return
     */
    public static boolean isEmail(String email){
        if (null==email || "".equals(email))
            return false;
        //Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        Pattern p =  Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof SetFragment.HideTab) {
            ((SetFragment.HideTab) getActivity()).hide();
        }
    }
}

