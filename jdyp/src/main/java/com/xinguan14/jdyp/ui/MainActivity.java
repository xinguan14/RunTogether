package com.xinguan14.jdyp.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.xinguan14.jdyp.base.BaseActivity;
import com.xinguan14.jdyp.bean.User;
import com.xinguan14.jdyp.db.NewFriendManager;
import com.xinguan14.jdyp.event.RefreshEvent;
import com.xinguan14.jdyp.ui.fragment.ContactFragment;
import com.xinguan14.jdyp.ui.fragment.ConversationFragment;
import com.xinguan14.jdyp.ui.fragment.FindFragment;
import com.xinguan14.jdyp.ui.fragment.RunFragment;
import com.xinguan14.jdyp.ui.fragment.SetFragment;
import com.xinguan14.jdyp.ui.fragment.SportsFragment;
import com.xinguan14.jdyp.util.IMMLeaks;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;

import com.xinguan14.jdyp.ui.fragment.ConnectFragment;

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
 * @author :smile
 * @project:MainActivity
 * @date :2016-01-15-18:23
 */
public class MainActivity extends BaseActivity implements ObseverListener{

    @Bind(com.xinguan14.jdyp.R.id.btn_sports)
    Button btn_sports;

    @Bind(com.xinguan14.jdyp.R.id.btn_find)
    Button btn_find;

    @Bind(com.xinguan14.jdyp.R.id.btn_run)
    Button btn_run;

    @Bind(com.xinguan14.jdyp.R.id.btn_connect)
    Button btn_connect;

    @Bind(com.xinguan14.jdyp.R.id.btn_set)
    Button btn_set;

    @Bind(com.xinguan14.jdyp.R.id.iv_sports_tips)
    ImageView iv_sports_tips;

    @Bind(com.xinguan14.jdyp.R.id.iv_find_tips)
    ImageView iv_find_tips;

    @Bind(com.xinguan14.jdyp.R.id.iv_run_tips)
    ImageView iv_run_tips;

    @Bind(com.xinguan14.jdyp.R.id.iv_connect_tips)
    ImageView iv_connect_tips;


    private Button[] mTabs;
    private ConversationFragment conversationFragment;
    private SetFragment setFragment;
    private ContactFragment contactFragment;
    private ConnectFragment connectFragment;
    private SportsFragment sportsFragment;
    private RunFragment runFragment;
    private FindFragment findFragment;
    private Fragment[] fragments;
    private int index;
    private int currentTabIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.xinguan14.jdyp.R.layout.activity_main);
        //connect server
        User user = BmobUser.getCurrentUser(this,User.class);
        BmobIM.connect(user.getObjectId(), new ConnectListener() {
            @Override
            public void done(String uid, BmobException e) {
                if (e == null) {
                    Logger.i("connect success");
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
    protected void initView() {
        super.initView();
        mTabs = new Button[5];
        mTabs[0] = btn_sports;
        mTabs[1] = btn_find;
        mTabs[2] = btn_run;
        mTabs[3] = btn_connect;
        mTabs[4] =btn_set;
        mTabs[0].setSelected(true);
        initTab();
    }

    private void initTab(){
        sportsFragment = new SportsFragment();
        findFragment = new FindFragment();
        runFragment = new RunFragment();
        connectFragment = new ConnectFragment();
        setFragment = new SetFragment();
        fragments = new Fragment[] {sportsFragment,findFragment,runFragment,connectFragment,setFragment};
        getSupportFragmentManager().beginTransaction()
                .add(com.xinguan14.jdyp.R.id.fragment_container, sportsFragment).
                add(com.xinguan14.jdyp.R.id.fragment_container, findFragment).
                add(com.xinguan14.jdyp.R.id.fragment_container, runFragment).
                add(com.xinguan14.jdyp.R.id.fragment_container, connectFragment)
                .add(com.xinguan14.jdyp.R.id.fragment_container, setFragment)
                .hide(findFragment).hide(runFragment).hide(connectFragment).hide(setFragment)
                .show(sportsFragment).commit();
    }

    public void onTabSelect(View view) {
        switch (view.getId()) {
            case com.xinguan14.jdyp.R.id.btn_sports:
                index = 0;
                break;
            case com.xinguan14.jdyp.R.id.btn_find:
                index = 1;
                break;
            case com.xinguan14.jdyp.R.id.btn_run:
                index = 2;
                break;
            case com.xinguan14.jdyp.R.id.btn_connect:
                index = 3;
                break;
            case com.xinguan14.jdyp.R.id.btn_set:
                index = 4;
                break;
        }
        onTabIndex(index);
    }

    private void onTabIndex(int index){
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(com.xinguan14.jdyp.R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        mTabs[currentTabIndex].setSelected(false);
        mTabs[index].setSelected(true);
        currentTabIndex = index;
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

    /**注册消息接收事件
     * @param event
     */
    @Subscribe
    public void onEventMainThread(MessageEvent event){
        checkRedPoint();
    }

    /**注册离线消息接收事件
     * @param event
     */
    @Subscribe
    public void onEventMainThread(OfflineMessageEvent event){
        checkRedPoint();
    }

    /**注册自定义消息接收事件
     * @param event
     */
    @Subscribe
    public void onEventMainThread(RefreshEvent event){
        log("---主页接收到自定义消息---");
        checkRedPoint();
    }

    private void checkRedPoint(){
        int count = (int)BmobIM.getInstance().getAllUnReadCount();
        if(count>0){
            iv_sports_tips.setVisibility(View.VISIBLE);
        }else{
            iv_sports_tips.setVisibility(View.GONE);
        }
        //是否有好友添加的请求
        if(NewFriendManager.getInstance(this).hasNewFriendInvitation()){
            iv_connect_tips.setVisibility(View.VISIBLE);
        }else{
            iv_connect_tips.setVisibility(View.GONE);
        }
    }

}
