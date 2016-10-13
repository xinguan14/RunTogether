package com.xinguan14.jdyp.ui.fragment.setfragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.base.ParentWithNaviActivity;
import com.xinguan14.jdyp.base.ParentWithNaviFragment;
import com.xinguan14.jdyp.ui.LoginActivity;
import com.xinguan14.jdyp.ui.fragment.SetFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by wm on 2016/7/27.
 */
public class ChangePassWordFragment extends ParentWithNaviFragment {

    @Bind(R.id.old_password)
    EditText old_password;
    @Bind(R.id.new_password)
    EditText new_password;
    @Bind(R.id.new_password2)
    EditText cfm_password;
    @Bind(R.id.update_pswd_confirm)
    Button changPassWord;
    private String oldPassWord;
    private String newPassWord;
    private String cfmPassWord;
    @Override
    protected String title() {
        return "修改密码";
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
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.set_update_password, container, false);
        ButterKnife.bind(this, rootView);
        initNaviView();
        changPassWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldPassWord = old_password.getText().toString();
                newPassWord = new_password.getText().toString();
                cfmPassWord = cfm_password.getText().toString();
                if (oldPassWord.length()==0){
                    toast("旧密码不能为空");
                }
                if (newPassWord.length()==0){
                    toast("新密码不能为空");
                }
                if (cfmPassWord.length()==0){
                    toast("确认密码不能为空");
                }
                if(oldPassWord.length()!=0||newPassWord.length()!=0||cfmPassWord.length()!=0){
                    if (newPassWord.equals(cfmPassWord)) {
                        BmobUser.updateCurrentUserPassword(getActivity(), oldPassWord, newPassWord, new UpdateListener() {

                            @Override
                            public void onSuccess() {
                                // TODO Auto-generated method stub
                               toast("密码修改成功，可以用新密码进行登录啦");
                               startActivity( LoginActivity.class,null);
                            }

                            @Override
                            public void onFailure(int code, String msg) {
                                // TODO Auto-generated method stub
                                Log.i("smile", "密码修改失败："+msg+"("+code+")");
                            }
                        });

                    }else {
                        toast("新密码和确认密码不相等");
                    }
                }
            }
        });
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
