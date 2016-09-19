package com.xinguan14.jdyp.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.base.BackHandledInterface;
import com.xinguan14.jdyp.base.BaseActivity;
import com.xinguan14.jdyp.base.ParentWithNaviFragment;
import com.xinguan14.jdyp.bean.User;
import com.xinguan14.jdyp.db.NewFriendManager;
import com.xinguan14.jdyp.event.RefreshEvent;
import com.xinguan14.jdyp.guideView.Guide;
import com.xinguan14.jdyp.guideView.GuideBuilder;
import com.xinguan14.jdyp.guideView.SimpleComponent;
import com.xinguan14.jdyp.music.ui.MusicPlayerActivity;
import com.xinguan14.jdyp.myVeiw.DensityUtil;
import com.xinguan14.jdyp.step.StepCounterActivity;
import com.xinguan14.jdyp.trackshow.BaiduActivity;
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
public class MainActivity extends BaseActivity implements ObseverListener,
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

    @Bind(R.id.btn_set)
    Button btn_set;

    @Bind(R.id.bt_add_main)
    ImageView bt_add_main;

    @Bind(R.id.bottom)
    LinearLayout bottom;

    @Bind(R.id.id_content)
    FrameLayout content;

    private ParentWithNaviFragment parentWithNaviFragment;
    private Button[] mTabs;
    private SetFragment setFragment;
    private ContactFragment contactFragment;
    private SportsFragment sportsFragment;
    private MessageFragment messageFragment;
    private int index;
    private SoundPool sp;// 声明一个SoundPool
    private int music;// 定义一个整型用load（）；来设置suondIDf
    private ImageView run, iv_yuepao,
            step, iv_music, iv_add_center;
    private LinearLayout ll_createtask_center, ll_createproject_center,
            ll_registration_center, ll_oa_center;
    private PopupWindow menu;
    private int y1, y2;// y1:新建弹出框中新建任务/新建项目的高度 y2:新建弹出框中签到/OA的高度

    private Guide guide;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //connect server
        User user = BmobUser.getCurrentUser(this, User.class);
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

        run = (ImageView) findViewById(R.id.iv_createtask_center);
        iv_yuepao = (ImageView) findViewById(R.id.iv_createproject_center);
        step = (ImageView) findViewById(R.id.iv_registration_center);
        iv_music = (ImageView) findViewById(R.id.iv_oa_center);

        sp = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);// 第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
        music = sp.load(this, R.raw.key_sound, 1); // 把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级

        inipopmenu();
        setListener();
    }

    private void setListener() {
        bt_add_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 启动动画
                sp.play(music, 1, 1, 0, 0, 1);// 播放音效
                // 获取弹出框中两排按钮在屏幕上的高度
                int[] locations1 = new int[2];
                ll_createtask_center.getLocationOnScreen(locations1);
                y1 = locations1[1];
                int[] locations2 = new int[2];
                ll_registration_center.getLocationOnScreen(locations2);
                y2 = locations2[1];

                // 显示新建弹出框
                menu.showAtLocation(
                        MainActivity.this.findViewById(R.id.mainLayout),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                rotate(iv_add_center);// 弹出框中最下方按钮添加旋转动画

                // 弹出框中按钮弹出动画
                tran(ll_createtask_center, y1, 0, true);
                tran(ll_createproject_center, y1, 50, true);
                tran(ll_registration_center, y2, 100, true);
                tran(ll_oa_center, y2, 150, true);
            }
        });
        bt_add_main.post(new Runnable() {
            @Override
            public void run() {
                showGuideView();
            }
        });
        run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(BaiduActivity.class, null, false);
            }
        });
        iv_yuepao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(YuepaoActivity.class, null, false);
            }
        });
        step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(StepCounterActivity.class, null, false);
            }
        });
        iv_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MusicPlayerActivity.class, null, false);
            }
        });
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
                if (a == 1) {
                    showTab();
                } else {
                    hideTab();
                }
                System.out.println("返回 " + a);
            }
        }
    }

    public void showGuideView() {
        GuideBuilder builder = new GuideBuilder();
        builder.setTargetView(bt_add_main)
                .setAlpha(150)
                .setHighTargetCorner(20)
                .setHighTargetPadding(10)
                .setOverlayTarget(false)
                .setOutsideTouchable(false);
        builder.setOnVisibilityChangedListener(new GuideBuilder.OnVisibilityChangedListener() {
            @Override
            public void onShown() {
            }

            @Override
            public void onDismiss() {
            }
        });

        builder.addComponent(new SimpleComponent());
        guide = builder.createGuide();
        guide.setShouldCheckLocInWindow(true);
        guide.show(this);
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
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) content.getLayoutParams();
        layoutParams.bottomMargin = 0;
        content.setLayoutParams(layoutParams);

    }

    /**
     * 显示底部tab
     */
    public void showTab() {
        bottom.setVisibility(View.VISIBLE);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) content.getLayoutParams();
        layoutParams.bottomMargin = 52;
        content.setLayoutParams(layoutParams);

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

    /**
     * @param view 要添加动画的View
     * @方法名称:rotate
     * @描述: 旋转动画
     * @返回类型：void
     */
    public void rotate(View view) {
        // 从0开始旋转360度，以图片中心为圆心旋转(0.5f,0.5f表示图片中心 1.0f,1.0f表示右下角0.0f,o.0f表示左上角)
        RotateAnimation ra = new RotateAnimation(0, 135,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        ra.setDuration(500);
        // ra.setRepeatCount(1);
        // ra.setRepeatMode(Animation.REVERSE);
        ra.setFillAfter(true);
        view.startAnimation(ra);
    }

    /**
     * @方法名称:inipopmenu
     * @描述: 初始化新建弹出框
     * @返回类型：void
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("NewApi")
    private void inipopmenu() {
        // 初始化新建弹出框
        View contentView = View.inflate(MainActivity.this,
                R.layout.create_pop_menu, null);

        /********************************************************************/
        // 避免运行在Android 4.0上程序报空指针异常，原因是跟硬盘加速有关（？）
        // contentView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        /********************************************************************/
        ll_createtask_center = (LinearLayout) contentView
                .findViewById(R.id.ll_createtask_center);
        ll_createproject_center = (LinearLayout) contentView
                .findViewById(R.id.ll_createproject_center);
        ll_registration_center = (LinearLayout) contentView
                .findViewById(R.id.ll_registration_center);
        ll_oa_center = (LinearLayout) contentView
                .findViewById(R.id.ll_oa_center);
        run = (ImageView) contentView
                .findViewById(R.id.iv_createtask_center);
        iv_yuepao = (ImageView) contentView
                .findViewById(R.id.iv_createproject_center);
        step = (ImageView) contentView
                .findViewById(R.id.iv_registration_center);
        iv_music = (ImageView) contentView.findViewById(R.id.iv_oa_center);
        iv_add_center = (ImageView) contentView
                .findViewById(R.id.iv_add_center);

        MyOnClickListener listener = new MyOnClickListener();
        // 点击四个按钮其他位置隐藏弹出框
        contentView.findViewById(R.id.pop_layout).setOnClickListener(listener);

        // 弹出框最下方关闭按钮添加点击事件监听
        iv_add_center.setOnClickListener(listener);

        menu = new PopupWindow(contentView);
        // PopUpWindow必须设置宽高，否则调用showAtLocation方法无法显示
        // 设置SelectPicPopupWindow弹出窗体的宽
        menu.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        menu.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        menu.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x80000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        menu.setBackgroundDrawable(dw);
    }

    /**
     * @类名称: MyOnClickListener
     * @类描述: 自定义点击事件监听
     */
    class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            sp.play(music, 1, 1, 0, 0, 1);// 播放音效
            reRotate(iv_add_center);// 按钮反向旋转动画

            // 弹出框隐藏动画
            retran(ll_oa_center, y2, 0, false);
            retran(ll_registration_center, y2, 50, false);
            retran(ll_createproject_center, y1, 100, false);
            retran(ll_createtask_center, y1, 150, false);
        }
    }


    /**
     * @param view 要添加反方向旋转动画的View
     * @方法名称:reRotate
     * @描述: 反方向旋转动画
     * @返回类型：void
     */
    public void reRotate(View view) {
        // 从0开始旋转360度，以图片中心为圆心旋转(0.5f,0.5f表示图片中心 1.0f,1.0f表示右下角0.0f,o.0f表示左上角)
        RotateAnimation ra = new RotateAnimation(135, 0,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        ra.setDuration(500);
        // ra.setRepeatCount(1);//重复次数
        // ra.setRepeatMode(Animation.REVERSE);//是否反方向执行
        ra.setFillAfter(true);
        ra.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 最后一个执行完动画之后执行的代码
                if (menu != null && menu.isShowing()) {
                    menu.dismiss();
                }
            }
        });
        view.startAnimation(ra);
    }


    /**
     * @param view  执行动画的控件
     * @param y     移动的距离
     * @param start 开始执行动画的时间
     * @param flag  标识执行的是弹出的动画还是隐藏的动画 true标识弹出动画
     * @方法名称:retran
     * @描述: 回弹动画
     * @返回类型：void
     */
    private void retran(final View view, final int y, final int start,
                        final boolean flag) {
        TranslateAnimation animation;
        if (flag) {
            animation = new TranslateAnimation(0, 0, -DensityUtil.dip2px(
                    getApplicationContext(), 50), 0);
        } else {
            animation = new TranslateAnimation(0, 0, 0, -DensityUtil.dip2px(
                    getApplicationContext(), 50));
        }
        animation.setDuration(150);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (flag) {
                    view.setVisibility(View.VISIBLE);
                    scaleAndAlpha(iv_yuepao);
                    scaleAndAlpha(run);
                    scaleAndAlpha(iv_music);
                    scaleAndAlpha(step);
                } else {
                    tran(view, y, start, flag);
                }
            }
        });
        view.startAnimation(animation);
    }


    private void scaleAndAlpha(final View view) {

        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1.0f);
        alphaAnimation.setDuration(100);
        alphaAnimation.setFillAfter(true);

        ScaleAnimation animation = new ScaleAnimation(0f, 1.1f, 0f, 1.1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        animation.setDuration(200);
        animation.setFillAfter(true);

        AnimationSet set = new AnimationSet(false);
        set.addAnimation(alphaAnimation);
        set.addAnimation(animation);

        set.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                reScale(view);
            }
        });
        view.startAnimation(set);
    }

    private void reScale(View view) {
        ScaleAnimation animation = new ScaleAnimation(1.1f, 1.0f, 1.1f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        animation.setDuration(100);
        animation.setFillAfter(true);
        view.startAnimation(animation);
    }


    /**
     * @param view  要执行动画的View
     * @param y     执行动画的View的高
     * @param start 执行动画的开始时间
     * @方法名称:tran
     * @描述: 显示弹出框时执行的动画
     * @返回类型：void
     */
    private void tran(final View view, final int y, final int start,
                      final boolean flag) {
        int heightPixels = getResources().getDisplayMetrics().heightPixels;
        TranslateAnimation animation;
        if (flag) {
            animation = new TranslateAnimation(0, 0, heightPixels - y,
                    -DensityUtil.dip2px(getApplicationContext(), 50));
        } else {
            animation = new TranslateAnimation(0, 0, -DensityUtil.dip2px(
                    getApplicationContext(), 50), heightPixels - y);
        }
        animation.setStartOffset(start);
        animation.setDuration(150);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (flag) {
                    retran(view, y, start, flag);
                }
            }
        });
        view.startAnimation(animation);
    }
}