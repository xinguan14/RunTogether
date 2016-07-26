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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;

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
            public int getItemLayoutId(int viewtype) {
                return R.layout.item_near;
            }

            @Override
            public int getItemViewType(int postion, User user) {
                return 0;
            }

            @Override
            public int getItemCount(List<User> list) {
                return list.size() + 1;
            }

        };

        layoutManager = new LinearLayoutManager(getActivity());
        rc_view.setLayoutManager(layoutManager);
        adapter = new NearPeopleAdapter(getActivity(), mutlipleItem, null);
        rc_view.setAdapter(adapter);

        sw_refresh.setEnabled(true);
        setListener();
        return rootView;
    }

    private void setListener() {
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                sw_refresh.setRefreshing(true);
                query();
            }
        });
        sw_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                query();
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

    @Override
    public void onResume() {
        super.onResume();
        sw_refresh.setRefreshing(true);
        query();
    }

    /**
     * 查询人
     */
    public void query() {
        User me = BmobUser.getCurrentUser(getActivity(), User.class);
        BmobQuery<User> bmobQuery = new BmobQuery<User>();
        bmobQuery.addWhereWithinKilometers("location", me.getLocation(), 1000);
        bmobQuery.findObjects(getActivity(), new FindListener<User>() {
            @Override
            public void onSuccess(final List<User> object) {
                adapter.bindDatas(object);

            }

            public void onError(int i, String s) {
            }
        });
        adapter.notifyDataSetChanged();
        sw_refresh.setRefreshing(false);
    }

    /**
     * 获取会话列表的数据：增加新朋友会话
     *
     * @return
     */
    private List<User> getNearPeople() {
        User me = BmobUser.getCurrentUser(getActivity(), User.class);

        final User user = new User();
        //BmobIMUserInfo
        final List<User> userList = new ArrayList<>();
//		userBmobQuery.addWhereWithinKilometers("location",me.getLocation(),1000);
        BmobQuery<User> bmobQuery = new BmobQuery<User>();
        bmobQuery.addWhereWithinKilometers("location", me.getLocation(), 1000);
        bmobQuery.findObjects(getActivity(), new FindListener<User>() {
            @Override
            public void onSuccess(final List<User> object) {

//						for (int i = 0; i<object.size();i++){
//							userList[i] = object[i];
//						}
                for (User item : object) {
                    userList.add(item);
                    System.out.println("内部" + userList);
                    System.out.println("内部object" + object.size());
                }
            }

            public void onError(int i, String s) {
            }
        });
        System.out.println("外部" + userList.size());
        userList.clear();


//		if (list != null && list.size() > 0) {
//			for (BmobIMConversation item : list) {
//				switch (item.getConversationType()) {
//					case 1://私聊
//						userList.add(new PrivateConversation(item));
//						break;
//					default:
//						break;
//				}
//			}
        return userList;
    }

    /**
     * 附近的人
     *
     * @author smile
     * @ClassName: BlackListAdapter
     * @Description: TODO
     * @date 2014-6-24 下午5:27:14
     */
    class NearPeopleAdapter extends BaseRecyclerAdapter<User> {

        User me = BmobUser.getCurrentUser(getActivity(), User.class);

        private static final double EARTH_RADIUS = 6378137;

        private double rad(double d) {
            return d * Math.PI / 180.0;
        }

        public NearPeopleAdapter(Context context, IMutlipleItem<User> items, Collection<User> datas) {
            super(context, items, datas);
        }

        /**
         * 根据两点间经纬度坐标（double值），计算两点间距离，
         *
         * @param lat1
         * @param lng1
         * @param lat2
         * @param lng2
         * @return 距离：单位为米
         */
        public double DistanceOfTwoPoints(double lat1, double lng1, double lat2, double lng2) {
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
        public void bindView(BaseRecyclerHolder holder, User user, int position) {

            //好友头像
            holder.setImageView(user == null ? null : user.getAvatar(), R.mipmap.head, R.id.iv_near_avatar);
            //好友名称
            holder.setText(R.id.tv_near_name, user == null ? "未知" : user.getUsername());
            holder.setText(R.id.tv_logintime, user == null ? "未知" : user.getUpdatedAt());

            if (user != null) {

                BmobGeoPoint location = me.getLocation();
                double currentLat = location.getLatitude();
                double currentLong = location.getLongitude();
                double distance = DistanceOfTwoPoints(currentLat, currentLong, user.getLocation().getLatitude(),
                        user.getLocation().getLongitude());
                holder.setText(R.id.tv_distance, user == null ? "未知1" : String.valueOf(distance) + "米");
            }else {
                holder.setText(R.id.tv_distance, "未知");
            }

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
