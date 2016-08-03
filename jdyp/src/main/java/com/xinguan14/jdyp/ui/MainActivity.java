package com.xinguan14.jdyp.ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.xinguan14.jdyp.MyVeiw.GooeyMenu;
import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.base.BackHandledInterface;
import com.xinguan14.jdyp.base.BaseActivity;
import com.xinguan14.jdyp.base.ParentWithNaviFragment;
import com.xinguan14.jdyp.bean.User;
import com.xinguan14.jdyp.db.NewFriendManager;
import com.xinguan14.jdyp.event.RefreshEvent;
import com.xinguan14.jdyp.ui.fragment.ContactFragment;
import com.xinguan14.jdyp.ui.fragment.MessageFragment;
import com.xinguan14.jdyp.ui.fragment.SetFragment;
import com.xinguan14.jdyp.ui.fragment.SportsFragment;
import com.xinguan14.jdyp.ui.fragment.setfragment.ChangeMyInfoFragment;
import com.xinguan14.jdyp.ui.fragment.setfragment.ChangePassWordFragment;
import com.xinguan14.jdyp.util.IMMLeaks;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.newim.listener.ObseverListener;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

/**
 * 四个tab加一个环形菜单
 */
public class MainActivity extends BaseActivity implements ObseverListener, GooeyMenu.GooeyMenuInterface,
        MessageFragment.Check, SetFragment.HideTab, SetFragment.AddMenu, BackHandledInterface {

    @Bind(R.id.btn_message)
    Button btn_message;

    @Bind(R.id.tv_recent_unread)
    TextView tv_recent_unread;

    @Bind(R.id.btn_connect)
    Button btn_connect;

    @Bind(R.id.iv_connect_tips)
    TextView iv_connect_tips;

    @Bind(R.id.btn_sports)
    Button btn_sports;

    @Bind(R.id.iv_sports_tips)
    ImageView iv_sports_tips;

    @Bind(R.id.btn_set)
    Button btn_set;

    @Bind(R.id.gooey_menu)//环形菜单
            GooeyMenu gooeyMenu;

    @Bind(R.id.bottom)
    LinearLayout bottom;

    @Bind(R.id.gooey)
    LinearLayout gooey;

    @Bind(R.id.id_content)
    FrameLayout content;

    private ParentWithNaviFragment parentWithNaviFragment;
    private Button[] mTabs;
    private SetFragment setFragment;
    private ContactFragment contactFragment;
    private SportsFragment sportsFragment;
    private MessageFragment messageFragment;
    ChangeMyInfoFragment changeMyInfoFragment;
    ChangePassWordFragment changePassWordFragment;
    private int index;
    private int mScreenWidth;
    private int mScreenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //connect server
        User user = BmobUser.getCurrentUser(this, User.class);
        gooeyMenu.setOnMenuListener(this);
        BmobIM.connect(user.getObjectId(), new ConnectListener() {
            @Override
            public void done(String uid, BmobException e) {
                if (e == null) {
                    Logger.i("连接成功");
                    //服务器连接成功就发送一个更新事件，同步更新会话及主页的小红点
                    EventBus.getDefault().post(new RefreshEvent());
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
        //解决leancanary提示InputMethodManager内存泄露的问题
        IMMLeaks.fixFocusedViewLeak(getApplication());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

    }

    @Override
    protected void initView() {
        super.initView();
        mTabs = new Button[4];
        mTabs[0] = btn_message;
        mTabs[1] = btn_connect;
        mTabs[2] = btn_sports;
        mTabs[3] = btn_set;
        onTabSelect(mTabs[0]);
    }

    public void onTabSelect(View view) {
        switch (view.getId()) {
            case R.id.btn_message:
                index = 0;
                break;
            case R.id.btn_connect:
                index = 1;
                break;
            case R.id.btn_sports:
                index = 2;
                break;
            case R.id.btn_set:
                index = 3;

                break;
        }
        setSelect(index);
    }

    private void setSelect(int index) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        hideFragment(transaction);
        switch (index) {
            case 0:
                mTabs[0].setSelected(true);
                mTabs[1].setSelected(false);
                mTabs[2].setSelected(false);
                mTabs[3].setSelected(false);

                if (messageFragment == null) {
                    messageFragment = new MessageFragment();
                    transaction.add(R.id.id_content, messageFragment);
                } else {
                    transaction.show(messageFragment);
                }
                break;
            case 1:
                mTabs[1].setSelected(true);
                mTabs[0].setSelected(false);
                mTabs[2].setSelected(false);
                mTabs[3].setSelected(false);
                if (contactFragment == null) {
                    contactFragment = new ContactFragment();
                    transaction.add(R.id.id_content, contactFragment);
                } else {
                    transaction.show(contactFragment);
                }
                break;
            case 2:
                mTabs[2].setSelected(true);
                mTabs[1].setSelected(false);
                mTabs[0].setSelected(false);
                mTabs[3].setSelected(false);
                if (sportsFragment == null) {
                    sportsFragment = new SportsFragment();
                    transaction.add(R.id.id_content, sportsFragment);
                } else {
                    transaction.show(sportsFragment);
                }
                break;
            case 3:
                mTabs[3].setSelected(true);
                mTabs[1].setSelected(false);
                mTabs[2].setSelected(false);
                mTabs[0].setSelected(false);
                if (setFragment == null) {
                    setFragment = new SetFragment();
                    transaction.add(R.id.id_content, setFragment, "setFragment");
                } else {
                    transaction.show(setFragment);
                }
                break;
            default:
                break;
        }
        transaction.commit();
    }

    private void hideFragment(FragmentTransaction transaction) {
        if (messageFragment != null) {
            transaction.hide(messageFragment);
        }
        if (contactFragment != null) {
            transaction.hide(contactFragment);
        }
        if (sportsFragment != null) {
            transaction.hide(sportsFragment);
        }
        if (setFragment != null) {
            transaction.hide(setFragment);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //显示小红点
        checkRedPoint();
        //进入应用后，通知栏应取消
        BmobNotificationManager.getInstance(this).cancelNotification();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清理导致内存泄露的资源
        BmobIM.getInstance().clear();
    }

    @Override
    public void onBackPressed() {
        if (parentWithNaviFragment == null || !parentWithNaviFragment.onBackPressed()) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();
            } else {
                int a = getSupportFragmentManager().getBackStackEntryCount();
                getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(a - 1).getName(), getFragmentManager().POP_BACK_STACK_INCLUSIVE);
                if (a==1){
                    showTab();
                }else {
                    hideTab();
                }
                System.out.println("返回 " + a);
            }
        }
    }

    /**
     * 注册消息接收事件
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(MessageEvent event) {
        checkRedPoint();
    }

    /**
     * 注册离线消息接收事件
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(OfflineMessageEvent event) {
        checkRedPoint();
    }

    /**
     * 注册自定义消息接收事件
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(RefreshEvent event) {
        log("---主页接收到自定义消息---");
        checkRedPoint();
    }

    public void checkRedPoint() {
        int count = (int) BmobIM.getInstance().getAllUnReadCount();
        if (count > 0) {
            tv_recent_unread.setVisibility(View.VISIBLE);
            if (count > 99) {
                tv_recent_unread.setText(String.valueOf(count) + "+");
            } else {
                tv_recent_unread.setText(String.valueOf(count));
            }
        } else {
            tv_recent_unread.setVisibility(View.GONE);
        }
        //是否有好友添加的请求
        if (NewFriendManager.getInstance(this).hasNewFriendInvitation()) {
            iv_connect_tips.setVisibility(View.VISIBLE);
        } else {
            iv_connect_tips.setVisibility(View.GONE);
        }
    }

    /**
     * 隐藏底部tab
     */
    public void hideTab() {
        bottom.setVisibility(View.GONE);
        gooey.setVisibility(View.GONE);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) content.getLayoutParams();
        layoutParams.bottomMargin = 0;
        content.setLayoutParams(layoutParams);

    }

    /**
     * 显示底部tab
     */
    public void showTab() {
        bottom.setVisibility(View.VISIBLE);
        gooey.setVisibility(View.VISIBLE);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) content.getLayoutParams();
        layoutParams.bottomMargin = 60;
        content.setLayoutParams(layoutParams);

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void menuOpen() {

        //获取当前屏幕宽高
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;

        View view = getLayoutInflater().inflate(R.layout.activity_gooey_menu, null);
        PopupWindow popupWindow = new PopupWindow(view, mScreenWidth, mScreenHeight);
        popupWindow.setAnimationStyle(R.style.AppTheme_PopupOverlay);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 0.5f;
        getWindow().setAttributes(params);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
                gooeyMenu.close();
            }
        });

        popupWindow.showAsDropDown(gooeyMenu, Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void menuClose() {

    }

    @Override
    public void menuItemClicked(int menuNumber) {

    }

    /**
     * 为了调用activity里的方法创建的
     */
    @Override
    public void logout() {
        checkRedPoint();
    }

    @Override
    public void hide() {
        hideTab();
    }

    @Override
    public void showMenu() {
        showTab();
    }

    @Override
    public void setSelectedFragment(ParentWithNaviFragment selectedFragment) {
        this.parentWithNaviFragment = selectedFragment;
    }
}