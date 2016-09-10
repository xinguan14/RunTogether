package com.xinguan14.jdyp.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.orhanobut.logger.Logger;
import com.xinguan14.jdyp.MyVeiw.ATLoginButton;
import com.xinguan14.jdyp.MyVeiw.CircleImageView;
import com.xinguan14.jdyp.MyVeiw.ClearWriteEditText;
import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.base.BaseActivity;
import com.xinguan14.jdyp.bean.User;
import com.xinguan14.jdyp.event.FinishEvent;
import com.xinguan14.jdyp.event.RefreshEvent;
import com.xinguan14.jdyp.model.UserModel;
import com.xinguan14.jdyp.util.ImageLoadOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.Bind;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
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
    ATLoginButton btn_login;
    @Bind(R.id.tv_register)
    TextView tv_register;
    @Bind(R.id.tv_forget)
    TextView tv_forget;
    @Bind(R.id.de_img_backgroud)
    ImageView mImgBackgroud;
    @Bind(R.id.de_login_logo)
    CircleImageView logo;

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
        addEditTextListener();
//        btn_login.setButtonText("登录");

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
                if (et_username.getText().equals("")) {
                    et_username.setShakeAnimation();
                    toast("用户名不能为空");
                    return;
                }
                if (et_password.getText().equals("")) {
                    et_password.setShakeAnimation();
                    toast("密码不能为空");
                    return;
                }

                if (!et_username.getText().toString().equals("") && !et_password.getText().toString().equals("")) {
                    btn_login.buttonLoginAction();
                    toast("登录中，请稍候");

                }
                ((InputMethodManager) LoginActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(LoginActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                UserModel.getInstance().login(et_username.getText().toString(), et_password.getText().toString(), new LogInListener() {

                    @Override
                    public void done(Object o, final BmobException e) {
                        if (e == null) {
                            btn_login.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    btn_login.buttonLoaginResultAciton(true);
                                }
                            }, 100);
                            User user = (User) o;
                            BmobIM.getInstance().updateUserInfo(new BmobIMUserInfo(user.getObjectId(), user.getUsername(), user.getAvatar()));
                            updateUserLocation();
                            //connect server
//                            User user = BmobUser.getCurrentUser(this, User.class);
                            BmobIM.connect(user.getObjectId(), new ConnectListener() {
                                @Override
                                public void done(String uid, BmobException e) {
                                    if (e == null) {
                                        Logger.i("连接成功");
                                        //服务器连接成功就发送一个更新事件，同步更新会话及主页的小红点
                                        EventBus.getDefault().post(new RefreshEvent());
                                        startActivity(MainActivity.class, null, true);
                                    } else {
                                        Logger.e(e.getErrorCode() + "/" + e.getMessage());
                                    }
                                }
                            });
                            //监听连接状态，也可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
                            BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
                                @Override
                                public void onChange(ConnectionStatus status) {
                                    toast("" + status.getMsg());
                                }
                            });
                        } else {
                            btn_login.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    btn_login.buttonLoaginResultAciton(false);
//                                    toast(e.getMessage() + "(" + e.getErrorCode() + ")");
                                    toast("用户名或密码错误");

                                }
                            }, 1000);
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

    private void addEditTextListener() {
        et_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().equals("")) {
                    logo.setImageResource(R.mipmap.ic_launcher);
                }
                if (s.length() == 11) {
                    BmobQuery<User> bmobQuery = new BmobQuery<User>();
                    bmobQuery.addWhereEqualTo("username", s);
                    bmobQuery.findObjects(LoginActivity.this, new FindListener<User>() {
                        @Override
                        public void onSuccess(List<User> list) {
                            if (list.size() == 0) {
                                Toast.makeText(LoginActivity.this, "该手机号尚未注册", Toast.LENGTH_LONG).show();
                            } else {
                                for (User item : list) {
                                    refreshAvatar(item.getAvatar());
                                }
                            }
                        }

                        @Override
                        public void onError(int i, String s) {
                        }
                    });
                }
            }
        });
    }

    /**
     * 更新头像 refreshAvatar
     *
     * @return void
     * @throws
     */
    private void refreshAvatar(String avatar) {
        if (avatar != null && !avatar.equals("")) {
            ImageLoader.getInstance().displayImage(avatar, logo,
                    ImageLoadOptions.getOptions());
        } else {
            logo.setImageResource(R.mipmap.head);
        }
    }
}