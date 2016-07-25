package com.xinguan14.jdyp.ui.fragment.sportsfragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.adapter.OnRecyclerViewListener;
import com.xinguan14.jdyp.adapter.base.BaseRecyclerAdapter;
import com.xinguan14.jdyp.adapter.base.BaseRecyclerHolder;
import com.xinguan14.jdyp.adapter.base.IMutlipleItem;
import com.xinguan14.jdyp.bean.User;

import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NearFragment extends Fragment {

	private View rootView;

	@Bind(R.id.rc_view)
	RecyclerView rc_view;
	@Bind(R.id.sw_refresh)
	SwipeRefreshLayout sw_refresh;
	NearPeopleAdapter adapter;
	LinearLayoutManager layoutManager;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_sport_near, container, false);
		ButterKnife.bind(this, rootView);
		IMutlipleItem<User> mutlipleItem = new IMutlipleItem<User>() {

			@Override
			public int getItemLayoutId(int viewtype) {return R.layout.item_contact;}
			@Override
			public int getItemViewType(int postion, User user) {return 0;}
			@Override
			public int getItemCount(List<User> list) {return list.size() + 1;}

		};

		layoutManager = new LinearLayoutManager(getActivity());
		rc_view.setLayoutManager(layoutManager);
		adapter = new NearPeopleAdapter(getActivity(), mutlipleItem, null);
		rc_view.setAdapter(adapter);

//		sw_refresh.setEnabled(true);
		setListener();
		return rootView;
	}
	private void setListener() {
		rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				sw_refresh.setRefreshing(true);
			}
		});
		sw_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
			}
		});
		adapter.setOnRecyclerViewListener(new OnRecyclerViewListener() {
			@Override
			public void onItemClick(int position) {

			}

			@Override
			public boolean onItemLongClick(final int position) {

				return true;
			}
		});
	}
	/**
	 * 附近的人
	 *
	 * @ClassName: BlackListAdapter
	 * @Description: TODO
	 * @author smile
	 * @date 2014-6-24 下午5:27:14
	 */
	 class NearPeopleAdapter extends BaseRecyclerAdapter<User> {

		private static final double EARTH_RADIUS = 6378137;
		private  double rad(double d) {
			return d * Math.PI / 180.0;
		}
		User user;

		public NearPeopleAdapter(Context context, IMutlipleItem<User> items, Collection<User> datas) {
			super(context, items,datas);
		}
		/**
		 * 根据两点间经纬度坐标（double值），计算两点间距离，
		 * @param lat1
		 * @param lng1
		 * @param lat2
		 * @param lng2
		 * @return 距离：单位为米
		 */
		public  double DistanceOfTwoPoints(double lat1, double lng1,double lat2, double lng2) {
			double radLat1 = rad(lat1);
			double radLat2 = rad(lat2);
			double a = radLat1 - radLat2;
			double b = rad(lng1) - rad(lng2);
			double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
					+ Math.cos(radLat1) * Math.cos(radLat2)
					* Math.pow(Math.sin(b / 2), 2)));
			s = s * EARTH_RADIUS;
			s = Math.round(s * 10000) / 10000;
			return s;
		}

		@Override
		public void bindView(BaseRecyclerHolder holder, User item, int position) {

			//好友头像
			holder.setImageView(user == null ? null : user.getAvatar(), R.mipmap.head, R.id.iv_recent_avatar);
			//好友名称
			holder.setText(R.id.tv_recent_name, user == null ? "未知" : user.getUsername());

//			TextView tv_name = ViewHolder.get(convertView, R.id.tv_name);
//			TextView tv_distance = ViewHolder.get(convertView, R.id.tv_distance);
//			TextView tv_logintime = ViewHolder.get(convertView, R.id.tv_logintime);
//			ImageView iv_avatar = ViewHolder.get(convertView, R.id.iv_avatar);
//			String avatar = contract.getAvatar();
//			if (avatar != null && !avatar.equals("")) {
//				ImageLoader.getInstance().displayImage(avatar, iv_avatar,
//						ImageLoadOptions.getOptions());
//			} else {
//				iv_avatar.setImageResource(R.drawable.default_head);
//			}
//			BmobGeoPoint location = contract.getLocation();
//			String currentLat = CustomApplcation.getInstance().getLatitude();
//			String currentLong = CustomApplcation.getInstance().getLongtitude();
//			if(location!=null && !currentLat.equals("") && !currentLong.equals("")){
//				double distance = DistanceOfTwoPoints(Double.parseDouble(currentLat),Double.parseDouble(currentLong),contract.getLocation().getLatitude(),
//						contract.getLocation().getLongitude());
//				tv_distance.setText(String.valueOf(distance)+"米");
//			}else{
//				tv_distance.setText("未知");
//			}
		}
	}

}
