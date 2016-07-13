package com.xinguan14.jdyp.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.bean.User;
import com.xinguan14.jdyp.base.BaseActivity;
import com.xinguan14.jdyp.model.UserModel;

/**启动界面
 * @author :smile
 * @project:SplashActivity
 * @date :2016-01-15-18:23
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Handler handler =new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                User user = UserModel.getInstance().getCurrentUser();
                if (user == null) {
                    startActivity(LoginActivity.class,null,true);
                }else{
                    startActivity(MainActivity.class,null,true);
                }
            }
        },1000);

    }
}
