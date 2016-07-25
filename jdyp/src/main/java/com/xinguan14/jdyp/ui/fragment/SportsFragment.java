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
import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.base.ParentWithNaviActivity;
import com.xinguan14.jdyp.base.ParentWithNaviFragment;
import com.xinguan14.jdyp.ui.AddPostActivity;
import com.xinguan14.jdyp.ui.fragment.sportsfragment.NearFragment;
import com.xinguan14.jdyp.ui.fragment.sportsfragment.SayFragment;
import com.xinguan14.jdyp.ui.fragment.sportsfragment.SquareFragment;

import butterknife.ButterKnife;
/*
* 运动圈界面
* */
public class SportsFragment extends ParentWithNaviFragment {

	@Override
	protected String title() {
		return "运动圈";
	}
	//设置导航栏右边
	@Override
	public Object right() {
		return R.drawable.base_action_bar_add_bg_selector;
	}
	/*@Override
	public Object left(){
		return null;
	}*/

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
			public void clickRight()
			{
				startActivity(AddPostActivity.class,null);
				/*Fragment newFragment = new AddPostActivity();
				FragmentManager fragmentManager =getFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//替换该界面中的Fragment
				fragmentTransaction.replace(R.id.id_content, newFragment);
//将事务调价到Back栈中，允许用户按返回键返回到替换前的界面
				fragmentTransaction.addToBackStack(null);
				fragmentTransaction.commit();*/

			}
		};
	}

	private SayFragment sayFragment;
	private SquareFragment squareFragment;
	private NearFragment nearFragment;
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
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

		private final String[] titles = { "动态","附近的人", "广场" };

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

					if (null == nearFragment) {
						nearFragment = new NearFragment();
					}

					return nearFragment;

				case 2:

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
