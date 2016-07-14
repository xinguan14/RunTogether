package com.xinguan14.jdyp.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xinguan14.jdyp.PagerSlidingTabStrip;
import com.xinguan14.jdyp.base.ParentWithNaviFragment;
import com.xinguan14.jdyp.ui.fragment.sportsfragment.ShuoFragment;
import com.xinguan14.jdyp.ui.fragment.sportsfragment.SquareFragment;

import butterknife.ButterKnife;

public class SportsFragment extends ParentWithNaviFragment {

	@Override
	protected String title() {
		return "运动圈";
	}

	private ShuoFragment shuoFragment;
	private SquareFragment squareFragment;
	//private SubFragment3 subFragment3;
	/**
	 * PagerSlidingTabStrip的实例
	 */
	private PagerSlidingTabStrip tabs;

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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		rootView = inflater.inflate(com.xinguan14.jdyp.R.layout.fragment_message, null);
		initNaviView();

		initView(rootView);
		ButterKnife.bind(this, rootView);

		return rootView;
	}

	private void initView(View view) {

		dm = getResources().getDisplayMetrics();
		ViewPager pager = (ViewPager) view.findViewById(com.xinguan14.jdyp.R.id.pager);
		tabs = (PagerSlidingTabStrip) view.findViewById(com.xinguan14.jdyp.R.id.tabs);
		pager.setAdapter(new MyPagerAdapter(getChildFragmentManager()));
		tabs.setViewPager(pager);
		setTabsValue();

	}

	/**
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
			// TODO Auto-generated constructor stub
		}

		private final String[] titles = { "动态", "广场" };

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

					if (null == shuoFragment) {
						shuoFragment = new ShuoFragment();
					}

					return shuoFragment;

				case 1:

					if (null == squareFragment) {
						squareFragment = new SquareFragment();
					}

					return squareFragment;

				default:
					return null;
			}
		}

	}
}
