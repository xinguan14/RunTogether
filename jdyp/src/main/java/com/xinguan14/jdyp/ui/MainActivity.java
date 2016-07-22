package com.xinguan14.jdyp.ui;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.xinguan14.jdyp.GooeyMenu;
import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.SwipMenu.SwipeMenu;
import com.xinguan14.jdyp.SwipMenu.SwipeMenuBuilder;
import com.xinguan14.jdyp.SwipMenu.SwipeMenuItem;
import com.xinguan14.jdyp.SwipMenu.SwipeMenuView;
import com.xinguan14.jdyp.base.BaseActivity;
import com.xinguan14.jdyp.bean.User;
import com.xinguan14.jdyp.db.NewFriendManager;
import com.xinguan14.jdyp.event.RefreshEvent;
import com.xinguan14.jdyp.ui.fragment.ContactFragment;
import com.xinguan14.jdyp.ui.fragment.MessageFragment;
import com.xinguan14.jdyp.ui.fragment.SetFragment;
import com.xinguan14.jdyp.ui.fragment.SportsFragment;
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
public class MainActivity extends BaseActivity implements ObseverListener,GooeyMenu.GooeyMenuInterface, SwipeMenuBuilder, MessageFragment.Check {

    @Bind(R.id.btn_message)
    Button btn_message;

    @Bind(R.id.tv_recent_unread)
    TextView tv_recent_unread;

    @Bind(R.id.btn_connect)
    Button btn_connect;

    @Bind(R.id.iv_connect_tips)
    ImageView iv_connect_tips;

    @Bind(R.id.btn_sports)
    Button btn_sports;

    @Bind(R.id.iv_sports_tips)
    ImageView iv_sports_tips;

    @Bind(R.id.btn_set)
    Button btn_set;

    @Bind(R.id.gooey_menu)//环形菜单
            GooeyMenu gooeyMenu;

    private Button[] mTabs;
    private SetFragment setFragment;
    private ContactFragment contactFragment;
    private SportsFragment sportsFragment;
    private MessageFragment messageFragment;
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
                    transaction.add(R.id.id_content, setFragment);
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
//        int count = messageFragment.count;
        if (count > 0) {
            tv_recent_unread.setVisibility(View.VISIBLE);
            tv_recent_unread.setText(String.valueOf(count));
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

    @Override
    public SwipeMenuView create() {

        SwipeMenu menu = new SwipeMenu(this);

        SwipeMenuItem item = new SwipeMenuItem(this);


        item.setTitle("删除")
                .setTitleColor(Color.WHITE)
                .setBackground(new ColorDrawable(Color.RED));
        menu.addMenuItem(item);

        SwipeMenuView menuView = new SwipeMenuView(menu);

        menuView.setOnMenuItemClickListener(mOnSwipeItemClickListener);

        return menuView;

    }

    private SwipeMenuView.OnMenuItemClickListener mOnSwipeItemClickListener = new SwipeMenuView.OnMenuItemClickListener() {

        @Override
        public void onMenuItemClick(int pos, SwipeMenu menu, int index) {
            Toast.makeText(MainActivity.this, menu.getMenuItem(index).getTitle(), Toast.LENGTH_LONG).show();
            messageFragment.close(pos);

            if (index == 1) {
                messageFragment.close(pos);
//                messageFragment.rc_view.smoothCloseMenu(pos);
////                recyclerView.smoothCloseMenu(pos);
////                list.remove(pos);
//                messageFragment.adapter.remove(pos);
            }
        }
    };

    /**
     * 为了调用activity里的方法创建的
     */
    @Override
    public void logout() {
        checkRedPoint();
    }
}