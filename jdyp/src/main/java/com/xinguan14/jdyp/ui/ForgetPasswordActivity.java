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
import com.xinguan14.jdyp.MyVeiw.ClearWriteEditText;
import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.base.BaseActivity;
import com.xinguan14.jdyp.bean.User;
import java.util.List;
import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.ResetPasswordByCodeListener;

/**
 * 找回密码
 *
 * @author :YYZ
 */
public class ForgetPasswordActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.forget_phone)
    ClearWriteEditText forget_phone;

    @Bind(R.id.forget_code)
    ClearWriteEditText forget_code;

    @Bind(R.id.forget_password)
    ClearWriteEditText forget_password;

    @Bind(R.id.forget_password_again)
    ClearWriteEditText forget_password_again;

    @Bind(R.id.btn_forget_getcode)
    Button btn_forget_getcode;

    @Bind(R.id.btn_forget_confirm)
    Button btn_forget_confirm;

    @Bind(R.id.tv_register)
    TextView tv_register;
    @Bind(R.id.tv_login)
    TextView tv_login;

    @Bind(R.id.de_img_backgroud)
    ImageView mImgBackgroud;

    String passWord, PassWordAgain, getCode, tel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //背景浮动
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(ForgetPasswordActivity.this, R.anim.translate_anim);
                mImgBackgroud.startAnimation(animation);
            }
        }, 200);
        addEditTextListener();
        setListener();

    }

    private void setListener() {
        btn_forget_getcode.setOnClickListener(this);
        btn_forget_confirm.setOnClickListener(this);
        tv_register.setOnClickListener(this);
        tv_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        passWord = forget_password.getText().toString();
        PassWordAgain = forget_password_again.getText().toString();
        getCode = forget_code.getText().toString();
        tel = forget_phone.getText().toString();
        switch (v.getId()) {
            case R.id.tv_register:
                startActivity(RegisterActivity.class, null, false);
                break;
            case R.id.tv_login:
                startActivity(LoginActivity.class, null, false);

                break;
            case R.id.btn_forget_getcode:
                //进行获取验证码操作和倒计时1分钟操作
                BmobSMS.requestSMSCode(this, tel, "找回密码", new RequestSMSCodeListener() {
                    @Override
                    public void done(Integer integer, BmobException e) {
                        if (e == null) {
                            //发送成功时，让获取验证码按钮不可点击，且为灰色
                            btn_forget_getcode.setClickable(false);
                            btn_forget_getcode.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_gray));
                            Toast.makeText(ForgetPasswordActivity.this, "验证码发送成功，请尽快使用", Toast.LENGTH_SHORT).show();

                            new CountDownTimer(60000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    btn_forget_getcode.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_gray));
                                    btn_forget_getcode.setText(millisUntilFinished / 1000 + "秒");
                                }

                                @Override
                                public void onFinish() {
                                    btn_forget_getcode.setClickable(true);
                                    btn_forget_getcode.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_blue));
                                    btn_forget_getcode.setText("重新发送");
                                }
                            }.start();
                            Log.e("MESSAGE:", "4");
                        } else {
                            Toast.makeText(ForgetPasswordActivity.this, "验证码发送失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                break;
            case R.id.btn_forget_confirm:

                if (TextUtils.isEmpty(tel)) {
                    Toast.makeText(ForgetPasswordActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
                    forget_phone.setShakeAnimation();
                    return;
                }
                if (TextUtils.isEmpty(passWord)) {
                    Toast.makeText(ForgetPasswordActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    forget_password.setShakeAnimation();
                    return;
                }
                if (!(passWord.equals(PassWordAgain))) {
                    Toast.makeText(ForgetPasswordActivity.this, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
                    forget_password.setShakeAnimation();
                    forget_password_again.setShakeAnimation();
                    return;
                }
                if (passWord.contains(" ")) {
                    Toast.makeText(ForgetPasswordActivity.this, "密码不能有空格", Toast.LENGTH_SHORT).show();
                    forget_password.setShakeAnimation();
                    return;
                }
                if (TextUtils.isEmpty(getCode)) {
                    Toast.makeText(ForgetPasswordActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                    forget_code.setShakeAnimation();
                    return;
                }

                BmobQuery<User> bmobQuery = new BmobQuery<User>();
                bmobQuery.addWhereEqualTo("username", tel);
                Toast.makeText(ForgetPasswordActivity.this, "正在修改，请稍候", Toast.LENGTH_LONG).show();
                bmobQuery.findObjects(ForgetPasswordActivity.this, new FindListener<User>() {
                    @Override
                    public void onSuccess(List<User> list) {
                        System.out.println("···············································" + list.size());
                        if (list.size() == 0) {
                            Toast.makeText(ForgetPasswordActivity.this, "该手机号尚未注册", Toast.LENGTH_LONG).show();
                        } else {

                            for (User item : list) {
                                item.resetPasswordBySMSCode(ForgetPasswordActivity.this, getCode, passWord, new ResetPasswordByCodeListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            Toast.makeText(ForgetPasswordActivity.this, "密码修改成功，去登录吧~", Toast.LENGTH_SHORT).show();
                                            startActivity(LoginActivity.class, null, true);
                                        } else {
                                            Toast.makeText(ForgetPasswordActivity.this, e + "密码修改失败" + e.getErrorCode(), Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                    }
                });
                break;
        }
    }


    private void addEditTextListener() {
        forget_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 11) {//手机号长度为11
                    BmobQuery<User> bmobQuery = new BmobQuery<User>();
                    bmobQuery.addWhereEqualTo("username", s);
                    Toast.makeText(ForgetPasswordActivity.this, "正在检查手机号，请稍候，检查成功后按钮会变成蓝色", Toast.LENGTH_LONG).show();
                    bmobQuery.findObjects(ForgetPasswordActivity.this, new FindListener<User>() {
                        @Override
                        public void onSuccess(List<User> list) {
                            if (list.size() == 0) {
                                Toast.makeText(ForgetPasswordActivity.this, "该手机号尚未注册", Toast.LENGTH_LONG).show();
                                btn_forget_getcode.setClickable(false);
                                btn_forget_getcode.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_gray));
                            } else {

                                btn_forget_getcode.setClickable(true);
                                btn_forget_getcode.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_blue));
                            }
                        }

                        @Override
                        public void onError(int i, String s) {
                        }
                    });
                } else {
                    btn_forget_getcode.setClickable(false);
                    btn_forget_getcode.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_gray));
                }
                tel = s.toString();
            }
        });

        forget_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                passWord = s.toString();
            }
        });
        forget_password_again.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (passWord.length() < 6) {
                    Toast.makeText(ForgetPasswordActivity.this, "密码至少6位", Toast.LENGTH_LONG).show();
                    forget_password.setShakeAnimation();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 5) {
                    btn_forget_confirm.setClickable(true);
                    btn_forget_confirm.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_blue));
                } else {
                    btn_forget_confirm.setClickable(false);
                    btn_forget_confirm.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_gray));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
