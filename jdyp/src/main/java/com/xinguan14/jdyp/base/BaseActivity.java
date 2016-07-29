package com.xinguan14.jdyp.base;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.xinguan14.jdyp.MyApplication;
import com.xinguan14.jdyp.bean.User;
import com.xinguan14.jdyp.config.Config;
import com.xinguan14.jdyp.dialog.DialogTips;
import com.xinguan14.jdyp.model.UserModel;
import com.xinguan14.jdyp.ui.activity.LoginActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import cn.bmob.newim.BmobIM;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 基类
 *
 * @author :smile
 * @project:BaseActivity
 * @date :2016-01-15-18:23
 */
public class BaseActivity extends FragmentActivity {

    MyApplication mApplication = MyApplication.INSTANCE();

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Subscribe
    public void onEvent(Boolean empty) {

    }

    protected void initView() {
    }

    protected void runOnMain(Runnable runnable) {
        runOnUiThread(runnable);
    }

    protected final static String NULL = "";
    private Toast toast;

    public void toast(final Object obj) {
        try {
            runOnMain(new Runnable() {

                @Override
                public void run() {
                    if (toast == null)
                        toast = Toast.makeText(BaseActivity.this, NULL, Toast.LENGTH_SHORT);
                    toast.setText(obj.toString());
                    toast.show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startActivity(Class<? extends Activity> target, Bundle bundle, boolean finish) {
        Intent intent = new Intent();
        intent.setClass(this, target);
        if (bundle != null)
            intent.putExtra(getPackageName(), bundle);
        startActivity(intent);
        if (finish)
            finish();
    }

    public Bundle getBundle() {
        if (getIntent() != null && getIntent().hasExtra(getPackageName()))
            return getIntent().getBundleExtra(getPackageName());
        else
            return null;
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 隐藏软键盘-一般是EditText.getWindowToken()
     *
     * @param token
     */
    public void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * Log日志
     *
     * @param msg
     */
    public void log(String msg) {
        if (Config.DEBUG) {
            Logger.i(msg);
        }
    }

    /**
     * 显示下线的对话框
     * showOfflineDialog
     *
     * @return void
     * @throws
     */
    public void showOfflineDialog(final Context context) {
        DialogTips dialog = new DialogTips(this, "您的账号已在其他设备上登录!", "重新登录");
        // 设置成功事件
        dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int userId) {
//                MyApplication.INSTANCE().logout();
                UserModel.getInstance().logout();
                //可断开连接
                BmobIM.getInstance().disConnect();
                startActivity(new Intent(context, LoginActivity.class));
//                startActivity(LoginActivity.class, null);
                finish();
                dialogInterface.dismiss();
            }
        });
        // 显示确认对话框
        dialog.show();
        dialog = null;
    }


    /**
     * 用于登陆或者自动登陆情况下的用户资料及好友资料的检测更新
     *
     * @param
     * @return void
     * @throws
     * @Title: updateUserInfos
     * @Description: TODO
     */
    public void updateUserInfos() {
        //更新地理位置信息
        updateUserLocation();
        //查询该用户的好友列表(这个好友列表是去除黑名单用户的哦),目前支持的查询好友个数为100，如需修改请在调用这个方法前设置BmobConfig.LIMIT_CONTACTS即可。
        //这里默认采取的是登陆成功之后即将好于列表存储到数据库中，并更新到当前内存中,
//        userManager.queryCurrentContactList(new FindListener<BmobChatUser>() {
//
//            @Override
//            public void onError(int arg0, String arg1) {
//                // TODO Auto-generated method stub
//                if(arg0== BmobConstants.CODE_COMMON_NONE){
//                    ShowLog(arg1);
//                }else{
//                    ShowLog("查询好友列表失败："+arg1);
//                }
//            }
//
//            @Override
//            public void onSuccess(List<BmobChatUser> arg0) {
//                // TODO Auto-generated method stub
//                // 保存到application中方便比较
//                MyApplication.getInstance().setContactList(CollectionUtils.list2map(arg0));
//            }
//        });
    }

    /**
     * 更新用户的经纬度信息
     *
     * @param
     * @return void
     * @throws
     * @Title: uploadLocation
     * @Description: TODO
     */
    public void updateUserLocation() {
        if (MyApplication.lastPoint != null) {
            String saveLatitude = mApplication.getLatitude();
            String saveLongtitude = mApplication.getLongtitude();
            String newLat = String.valueOf(MyApplication.lastPoint.getLatitude());
            String newLong = String.valueOf(MyApplication.lastPoint.getLongitude());

            if (!saveLatitude.equals(newLat) || !saveLongtitude.equals(newLong)) {//只有位置有变化就更新当前位置，达到实时更新的目的
                User u = BmobUser.getCurrentUser(this, User.class);
                final User user = new User();
                user.setLocation(MyApplication.lastPoint);
                user.setObjectId(u.getObjectId());
                user.update(this, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        mApplication.setLatitude(String.valueOf(user.getLocation().getLatitude()));
                        mApplication.setLongtitude(String.valueOf(user.getLocation().getLongitude()));
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                    }
                });
            } else {
//				ShowLog("用户位置未发生过变化");
            }
        }
    }

}
