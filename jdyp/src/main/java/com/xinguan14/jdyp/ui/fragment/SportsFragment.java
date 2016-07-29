package com.xinguan14.jdyp.ui.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xinguan14.jdyp.MyVeiw.PagerSlidingTabStrip;
import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.base.ParentWithNaviActivity;
import com.xinguan14.jdyp.base.ParentWithNaviFragment;
import com.xinguan14.jdyp.ui.activity.AddPostActivity;
import com.xinguan14.jdyp.ui.fragment.sportsfragment.NearFragment;
import com.xinguan14.jdyp.ui.fragment.sportsfragment.SayFragment;
import com.xinguan14.jdyp.ui.fragment.sportsfragment.SquareFragment;

import java.lang.reflect.Field;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 运动圈
 */
public class SportsFragment extends ParentWithNaviFragment {


    @Bind(R.id.pager)
    ViewPager pager;

    @Bind(R.id.tabs)
    PagerSlidingTabStrip tabs;

    private SayFragment sayFragment;
    private SquareFragment squareFragment;
    private NearFragment nearFragment;

    @Override
    protected String title() {
        return "运动圈";
    }

    //设置导航栏右边
    @Override
    public Object right() {
        return R.drawable.base_action_bar_add_bg_selector;
    }

    /*
    * 给导航栏设置监听事件
    * */
    @Override
    public ParentWithNaviActivity.ToolBarListener setToolBarListener() {
        return new ParentWithNaviActivity.ToolBarListener() {
            @Override
            public void clickLeft() {

            }
            @Override
            public void clickRight() {
                startActivity(AddPostActivity.class, null);

            }
        };
    }


    /**
     * 获取当前屏幕的密度
     */
    private DisplayMetrics dm;


    @Override
    public void onCreate(Bundle savedInstanceState) {// 在前面执行

        super.onCreate(savedInstanceState);
        // 获取参数
        Bundle bundle = getArguments();
        if (null != bundle) {
            //
        }
    }

    private EdgeEffectCompat leftEdge;
    private EdgeEffectCompat rightEdge;
    private void initViewPager() {
        try {
            Field leftEdgeField = pager.getClass().getDeclaredField("mLeftEdge");
            Field rightEdgeField = pager.getClass().getDeclaredField("mRightEdge");
            if (leftEdgeField != null && rightEdgeField != null) {
                leftEdgeField.setAccessible(true);
                rightEdgeField.setAccessible(true);
                leftEdge = (EdgeEffectCompat) leftEdgeField.get(pager);
                rightEdge = (EdgeEffectCompat) rightEdgeField.get(pager);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(com.xinguan14.jdyp.R.layout.fragment_message, null);
        ButterKnife.bind(this,rootView);

        initNaviView();
        initView();
        initViewPager();
        return rootView;
    }

    private void initView() {

        dm = getResources().getDisplayMetrics();
        pager.setAdapter(new MyPagerAdapter(getChildFragmentManager()));
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (leftEdge != null && rightEdge != null) {
                    leftEdge.finish();
                    rightEdge.finish();
                    leftEdge.setSize(0, 0);
                    rightEdge.setSize(0, 0);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabs.setViewPager(pager);
        setTabsValue();

    }

    /**
     * 顶部滑动区域
     * 对PagerSlidingTabStrip的各项属性进行赋值。
     */
    private void setTabsValue() {
        // 设置Tab是自动填充满屏幕的
        tabs.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        tabs.setDividerColor(Color.TRANSPARENT);
        // tabs.setDividerColor(Color.BLACK);
        // 设置Tab底部线的高度
        tabs.setUnderlineHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1, dm));
        // 设置Tab Indicator的高度
        tabs.setIndicatorHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4, dm));// 4
        // 设置Tab标题文字的大小
        tabs.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 16, dm)); // 16
        // 设置Tab Indicator的颜色
        tabs.setIndicatorColor(Color.parseColor("#45c01a"));// #45c01a
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        tabs.setSelectedTextColor(Color.parseColor("#45c01a"));// #45c01a
        // 取消点击Tab时的背景色
        tabs.setTabBackground(0);
    }

    // FragmentPagerAdapter FragmentStatePagerAdapter //不能用FragmentPagerAdapter


    public class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = {"动态", "广场", "附近的人"};

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:

                    if (null == sayFragment) {
                        sayFragment = new SayFragment();
                    }
                    return sayFragment;

                case 1:
                    if (null == squareFragment) {
                        squareFragment = new SquareFragment();
                    }
                    return squareFragment;

                case 2:
                    if (null == nearFragment) {
                        nearFragment = new NearFragment();
                    }
                    return nearFragment;
                default:
                    return null;
            }
        }

    }
}
