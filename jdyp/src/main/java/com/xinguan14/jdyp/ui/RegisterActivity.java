package com.xinguan14.jdyp.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xinguan14.jdyp.myVeiw.ClearWriteEditText;
import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.base.BaseActivity;
import com.xinguan14.jdyp.bean.User;
import com.xinguan14.jdyp.model.UserModel;

import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.RequestSMSCodeListener;

/**
 * 注册界面
 *
 * @author :smile
 * @project:RegisterActivity
 * @date :2016-01-15-18:23
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.reg_nickname)
    ClearWriteEditText reg_nickname;

    @Bind(R.id.reg_phone)
    ClearWriteEditText reg_phone;

    @Bind(R.id.reg_code)
    ClearWriteEditText reg_code;

    @Bind(R.id.reg_password)
    ClearWriteEditText reg_password;

    @Bind(R.id.btn_getcode)
    Button btn_getcode;

    @Bind(R.id.btn_register)
    Button btn_register;

    @Bind(R.id.tv_forget)
    TextView tv_forget;

    @Bind(R.id.tv_login)
    TextView tv_login;

    @Bind(R.id.rg_img_backgroud)
    ImageView mImgBackgroud;

    String nickName, passWord, getCode, tel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //去状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //背景浮动
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.translate_anim);
                mImgBackgroud.startAnimation(animation);
            }
        }, 200);
        btn_getcode.setClickable(false);
        btn_register.setClickable(false);
        addEditTextListener();
        setListener();

    }

    private void setListener() {
        tv_login.setOnClickListener(this);
        tv_forget.setOnClickListener(this);
        btn_getcode.setOnClickListener(this);
        btn_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        nickName = reg_nickname.getText().toString();
        passWord = reg_password.getText().toString();
        getCode = reg_code.getText().toString();
        tel = reg_phone.getText().toString();
        switch (v.getId()) {
            case R.id.tv_login:
                startActivity(LoginActivity.class, null, true);
                break;
            case R.id.tv_forget:
                startActivity(ForgetPasswordActivity.class, null, true);
                break;
            case R.id.btn_getcode:
                //进行获取验证码操作和倒计时1分钟操作

                BmobSMS.requestSMSCode(this, tel, "建大约跑", new RequestSMSCodeListener() {
                    @Override
                    public void done(Integer integer, BmobException e) {
                        if (e == null) {
                            //发送成功时，让获取验证码按钮不可点击，且为灰色
                            btn_getcode.setClickable(false);
                            btn_getcode.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_gray));
                            Toast.makeText(RegisterActivity.this, "验证码发送成功，请尽快使用", Toast.LENGTH_SHORT).show();

                            new CountDownTimer(60000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    btn_getcode.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_gray));
                                    btn_getcode.setText(millisUntilFinished / 1000 + "秒");
                                }

                                @Override
                                public void onFinish() {
                                    btn_getcode.setClickable(true);
                                    btn_getcode.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_blue));
                                    btn_getcode.setText("重新发送");
                                }
                            }.start();
                            Log.e("MESSAGE:", "4");
                        } else {
                            Toast.makeText(RegisterActivity.this, "验证码发送失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case R.id.btn_register:

                if (TextUtils.isEmpty(tel)) {
                    Toast.makeText(RegisterActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
                    reg_phone.setShakeAnimation();
                    return;
                }
                if (TextUtils.isEmpty(passWord)) {
                    Toast.makeText(RegisterActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    reg_password.setShakeAnimation();
                    return;
                }
                if (passWord.contains(" ")) {
                    Toast.makeText(RegisterActivity.this, "密码不能有空格", Toast.LENGTH_SHORT).show();
                    reg_password.setShakeAnimation();
                    return;
                }
                if (TextUtils.isEmpty(getCode)) {
                    Toast.makeText(RegisterActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                    reg_code.setShakeAnimation();
                    return;
                }
                if (TextUtils.isEmpty(nickName)) {
                    Toast.makeText(RegisterActivity.this, "昵称不能为空", Toast.LENGTH_SHORT).show();
                    reg_nickname.setShakeAnimation();
                    return;
                }
                if (nickName.contains(" ")) {
                    Toast.makeText(RegisterActivity.this, "昵称不能有空格", Toast.LENGTH_SHORT).show();
                    reg_nickname.setShakeAnimation();
                    return;
                }

                UserModel.getInstance().register(tel, passWord, getCode, nickName, new LogInListener() {
                    @Override
                    public void done(Object o, BmobException e) {
                        if (e == null) {
                            Toast.makeText(RegisterActivity.this, "注册成功，快去登录吧~.", Toast.LENGTH_SHORT).show();
                            startActivity(LoginActivity.class, null, true);
                        } else {
                            if (e.getErrorCode() == 207) {
                                Toast.makeText(RegisterActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                break;
        }
    }

    private void addEditTextListener() {
        reg_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 11) {
                    BmobQuery<User> bmobQuery = new BmobQuery<User>();
                    bmobQuery.addWhereEqualTo("username", s);
                    bmobQuery.findObjects(RegisterActivity.this, new FindListener<User>() {
                        @Override
                        public void onSuccess(List<User> list) {
                            if (list.size() == 0) {
                                Toast.makeText(RegisterActivity.this, "该手机号可用", Toast.LENGTH_LONG).show();
                                btn_getcode.setClickable(true);
                                btn_getcode.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_blue));
                            } else {
                                Toast.makeText(RegisterActivity.this, "手机号已被注册", Toast.LENGTH_LONG).show();
                                btn_getcode.setClickable(false);
                                btn_getcode.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_gray));
                            }
                        }

                        @Override
                        public void onError(int i, String s) {
                        }
                    });

                } else {
                    btn_getcode.setClickable(false);
                    btn_getcode.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_gray));
                }
            }
        });

        reg_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 6) {
                    btn_register.setClickable(true);
                    btn_register.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_blue));
                } else {
                    btn_register.setClickable(false);
                    btn_register.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_gray));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
