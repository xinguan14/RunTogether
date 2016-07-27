package com.xinguan14.jdyp.StikkyHeader.example;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xinguan14.jdyp.CircleImageView;
import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.base.ParentWithNaviActivity;
import com.xinguan14.jdyp.base.ParentWithNaviFragment;
import com.xinguan14.jdyp.bean.User;
import com.xinguan14.jdyp.model.UserModel;
import com.xinguan14.jdyp.ui.LoginActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.newim.BmobIM;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.GetListener;

/**
 * Created by wm on 2016/7/26.
 */
public class ChangeMyInfoFragment extends ParentWithNaviFragment implements View.OnClickListener {

    @Bind(R.id.img_my_avatar)
    CircleImageView img_my_avatar;
    @Bind(R.id.rl_my_avatar)
    RelativeLayout rl_my_avatar;

    @Bind(R.id.rl_my_username)
    RelativeLayout rl_my_username;
    @Bind(R.id.tv_my_username)
    TextView tv_my_username;

    @Bind(R.id.rl_my_sex)
    RelativeLayout rl_my_userSex;
    @Bind(R.id.tv_my_sex)
    TextView tv_my_userSex;

    @Bind(R.id.rl_my_userEmail)
    RelativeLayout rl_my_userEmail;
    @Bind(R.id.tv_my_userEmail)
    TextView tv_my_userEmail;

    @Bind(R.id.rl_my_phone)
    RelativeLayout rl_my_phone;
    @Bind(R.id.tv_my_phone)
    TextView tv_my_phone;

    @Bind(R.id.ac_set_change_pswd)
    RelativeLayout rl_set_change_pswd;

    @Bind(R.id.ac_set_exit)
    RelativeLayout rl_set_exit;


    @Override
    protected String title() {
        return "个人信息";
    }

    private FragmentManager manager;
    private FragmentTransaction ft;

    @Override
    public Object left() {
        return R.drawable.base_action_bar_back_bg_selector;
    }

    public  interface Return{
        void returnSetFragment();
    }

    @Override
    public ParentWithNaviActivity.ToolBarListener setToolBarListener() {
        return new ParentWithNaviActivity.ToolBarListener() {
            @Override
            public void clickLeft() {
                //返回设置界面
                if (getActivity() instanceof Return){
                    ((Return) getActivity()).returnSetFragment();
                }
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
         rootView = inflater.inflate(R.layout.set_change_info, container, false);
        ButterKnife.bind(this, rootView);
        initNaviView();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        manager = getFragmentManager();


        dataBind();
        rl_my_avatar.setOnClickListener(this);
        rl_my_phone.setOnClickListener(this);
        rl_my_userEmail.setOnClickListener(this);
        rl_my_username.setOnClickListener(this);
        rl_my_userSex.setOnClickListener(this);
        rl_set_change_pswd.setOnClickListener(this);
        rl_set_exit.setOnClickListener(this);


    }

    private String userAvatar;
    private String userName;
    private Boolean userSex;
    private String userPhone;
    private String userEmail;

    private void dataBind() {
        //获取当前的用户
        User user = BmobUser.getCurrentUser(getActivity(), User.class);
        String id = user.getObjectId();
        BmobQuery<User> userBmobQuery = new BmobQuery<User>();
        userBmobQuery.getObject(getActivity(), id, new GetListener<User>() {
            @Override
            public void onSuccess(User user) {
                userAvatar = user.getAvatar();
                userName = user.getUsername();
                //userSex = user.getSex();
                userPhone = user.getMobilePhoneNumber();
                userEmail = user.getEmail();

                //给控件绑定数据
                tv_my_username.setText(userName);
                tv_my_phone.setText(userPhone);
                tv_my_userEmail.setText(userEmail);
                /*if (userSex) {
                    tv_my_userSex.setText("男");
                } else {
                    tv_my_userSex.setText("女");
                }*/

                Glide
                        .with(getActivity())
                        .load(userAvatar)
                        .placeholder(R.drawable.love)
                        .into(img_my_avatar);
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_my_avatar:
                break;
            case R.id.rl_my_userEmail:
                break;
            case R.id.rl_my_username:
                Fragment changeUserNamefragment = new ChangeUserName();
                ft = manager.beginTransaction();
                ft.replace(R.id.id_content, changeUserNamefragment);
                ft.addToBackStack(null);
                ft.commit();
                break;
            case R.id.rl_my_sex:
                break;
            case R.id.rl_my_phone:
                break;
            case R.id.ac_set_change_pswd:
                Fragment changePassWordfragment = new ChangePassWord();
                ft = manager.beginTransaction();
                ft.replace(R.id.id_content, changePassWordfragment);
                ft.addToBackStack(null);
                ft.commit();
                break;
            case R.id.ac_set_exit:
                UserModel.getInstance().logout();
                //可断开连接
                BmobIM.getInstance().disConnect();
                getActivity().finish();
                startActivity(LoginActivity.class,null);
                break;
        }

    }
}
