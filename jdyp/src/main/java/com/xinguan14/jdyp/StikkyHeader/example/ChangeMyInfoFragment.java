package com.xinguan14.jdyp.StikkyHeader.example;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xinguan14.jdyp.CircleImageView;
import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.bean.User;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.GetListener;

/**
 * Created by yyz on 2016/7/26.
 */
public class ChangeMyInfoFragment extends Fragment {

   @Bind(R.id.img_my_avatar)
    CircleImageView img_my_avatar;
    @Bind(R.id.rl_my_avatar)
    RelativeLayout  rl_my_avatar;

    @Bind(R.id.rl_my_username)
    RelativeLayout  rl_my_username;
    @Bind(R.id.tv_my_username)
    TextView tv_my_username;

    @Bind(R.id.rl_my_userSex)
    RelativeLayout  rl_my_userSex;
    @Bind(R.id.tv_my_userSex)
    TextView tv_my_userSex;

   @Bind(R.id.rl_my_userEmail)
   RelativeLayout  rl_my_userEmail;
   @Bind(R.id.tv_my_userEmail)
   TextView tv_my_userEmail;

    @Bind(R.id.rl_my_phone)
    RelativeLayout  rl_my_phone;
    @Bind(R.id.tv_my_phone)
    TextView tv_my_phone;

    @Bind(R.id.ac_set_change_pswd)
    RelativeLayout  rl_set_change_pswd;

    @Bind(R.id.ac_set_exit)
    RelativeLayout  rl_set_exit;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         View rootView = inflater.inflate(R.layout.set_change_info, container, false);
        ButterKnife.bind(this,rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       dataBind();

    }

 private  String userAvatar;
 private  String userName;
 private  String userSex;
 private  String userPhone;
 private  String password;
 private  String userEmail;

    private void  dataBind(){
     //获取当前的用户
     User user  = BmobUser.getCurrentUser(getActivity(),User.class);
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
      }

      @Override
      public void onFailure(int i, String s) {

      }
     });
      tv_my_username.setText(userName);
      tv_my_phone.setText(userPhone);
      tv_my_userEmail.setText(userEmail);
    // img_my_avatar.setImageURI(userAvatar);
    }
}
