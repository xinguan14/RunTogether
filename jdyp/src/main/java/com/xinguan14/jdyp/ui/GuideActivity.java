package com.xinguan14.jdyp.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.adapter.ViewPagerAdapter;
import com.xinguan14.jdyp.ui.fragment.GuideFragment1;
import com.xinguan14.jdyp.ui.fragment.GuideFragment2;
import com.xinguan14.jdyp.ui.fragment.GuideFragment3;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuling on 2016/9/9.
 */
public class GuideActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {
    private ViewPager mActivity;
    private GuideFragment1 mGuideFragment1;
    private GuideFragment2 mGuideFragment2;
    private GuideFragment3 mGuideFragment3;
    private List<Fragment> mListFragment = new ArrayList<Fragment>();
    private PagerAdapter mPgAdapter;
    private ImageView[] dots;
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //设置无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initView();
        initDots();
    }

    private void initView() {
        mActivity = (ViewPager) findViewById(R.id.viewPager);
        mGuideFragment1 = new GuideFragment1();
        mGuideFragment2 = new GuideFragment2();
        mGuideFragment3 = new GuideFragment3();
        mListFragment.add(mGuideFragment1);
        mListFragment.add(mGuideFragment2);
        mListFragment.add(mGuideFragment3);
        mPgAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mListFragment);
        mActivity.setAdapter(mPgAdapter);
        mActivity.addOnPageChangeListener(this);
    }

    private void initDots() {
        LinearLayout ly = (LinearLayout) findViewById(R.id.ly);
        dots = new ImageView[mListFragment.size()];
        for (int i = 0; i < mListFragment.size(); i++) {
            dots[i] = (ImageView) ly.getChildAt(i);
            dots[i].setEnabled(true);
        }
        currentIndex = 0;
        dots[currentIndex].setEnabled(false);
    }

    private void setCurrentDot(int position) {
        if (position < 0 || position > mListFragment.size() - 1
                || currentIndex == position) {
            return;
        }
        dots[position].setEnabled(false);
        dots[currentIndex].setEnabled(true);
        currentIndex = position;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setCurrentDot(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {


    }
}


