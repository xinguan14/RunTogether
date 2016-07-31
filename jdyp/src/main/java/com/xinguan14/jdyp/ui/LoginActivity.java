package com.xinguan14.jdyp.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xinguan14.jdyp.MyVeiw.ClearWriteEditText;
import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.base.BaseActivity;
import com.xinguan14.jdyp.bean.User;
import com.xinguan14.jdyp.event.FinishEvent;
import com.xinguan14.jdyp.model.UserModel;

import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

/**
 * 登陆界面
 *
 * @author :smile
 * @project:LoginActivity
 * @date :2016-01-15-18:23
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.et_username)
    ClearWriteEditText et_username;
    @Bind(R.id.et_password)
    ClearWriteEditText et_password;
    @Bind(R.id.btn_login)
    Button btn_login;
    @Bind(R.id.tv_register)
    TextView tv_register;
    @Bind(R.id.tv_forget)
    TextView tv_forget;
    @Bind(R.id.de_img_backgroud)
    ImageView mImgBackgroud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //去状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setListener();

        //背景浮动
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.translate_anim);
                mImgBackgroud.startAnimation(animation);
            }
        }, 200);
    }

    public void setListener() {
        btn_login.setOnClickListener(this);
        tv_register.setOnClickListener(this);
        tv_forget.setOnClickListener(this);
    }

    @Subscribe
    public void onEventMainThread(FinishEvent event) {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                UserModel.getInstance().login(et_username.getText().toString(), et_password.getText().toString(), new LogInListener() {

                    @Override
                    public void done(Object o, BmobException e) {
                        if (e == null) {
                            User user = (User) o;
                            BmobIM.getInstance().updateUserInfo(new BmobIMUserInfo(user.getObjectId(), user.getUsername(), user.getAvatar()));
                            updateUserLocation();
                            startActivity(MainActivity.class, null, true);
                        } else {
                            toast(e.getMessage() + "(" + e.getErrorCode() + ")");
                        }
                    }
                });
                break;
            case R.id.tv_register:
                startActivity(RegisterActivity.class, null, false);
                break;
            case R.id.tv_forget:
                startActivity(ForgetPasswordActivity.class, null, false);
                break;
        }
    }
}
