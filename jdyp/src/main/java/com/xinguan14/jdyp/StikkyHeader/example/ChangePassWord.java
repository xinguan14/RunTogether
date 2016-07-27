package com.xinguan14.jdyp.StikkyHeader.example;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.base.ParentWithNaviActivity;
import com.xinguan14.jdyp.base.ParentWithNaviFragment;
import com.xinguan14.jdyp.ui.LoginActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by wm on 2016/7/27.
 */
public class ChangePassWord extends ParentWithNaviFragment {

    @Bind(R.id.old_password)
    EditText old_PassWord;

    @Bind(R.id.new_password)
    EditText new_password;

    @Bind(R.id.new_password2)
    EditText new_password2;

    @Bind(R.id.update_pswd_confirm)
    Button update_pswd_confirm;

    private String oldPassWord;
    private  String newPassWord;
    private  String confirmNewPassWord;

    @Override
    protected String title() {
        return "修改密码";
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
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        update_pswd_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changPassWord();
            }
        });
    }

    private  void changPassWord(){
        oldPassWord =old_PassWord.getText().toString();
        newPassWord = new_password.getText().toString();
        confirmNewPassWord = new_password2.getText().toString();

        if (oldPassWord.length()==0){
            Toast.makeText(getActivity(), "旧密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }else  if(newPassWord.length()==0){
            Toast.makeText(getActivity(), "新密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }else if(confirmNewPassWord.length()==0){
            Toast.makeText(getActivity(), "确认密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }else if (oldPassWord.length()!=0||newPassWord.length()!=0||confirmNewPassWord.length()!=0){

            if(newPassWord.equals(confirmNewPassWord)){
                BmobUser.updateCurrentUserPassword(getActivity(), oldPassWord, newPassWord, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        Log.i("smile", "密码修改成功，可以用新密码进行登录啦");
                        Toast.makeText(getActivity(), "密码修改成功，请重新登录", Toast.LENGTH_SHORT).show();
                        startActivity(LoginActivity.class,null);
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        // TODO Auto-generated method stub
                        Log.i("smile", "密码修改失败："+msg+"("+code+")");
                        Toast.makeText(getActivity(), "密码修改失败，旧密码不正确", Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                Log.i("info",oldPassWord);
                Log.i("info",newPassWord);
                Toast.makeText(getActivity(), "两次密码不一致", Toast.LENGTH_SHORT).show();
            }
        }


    }
}
