
package com.xinguan14.jdyp.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.adapter.OnRecyclerViewListener;
import com.xinguan14.jdyp.adapter.base.BaseRecyclerAdapter;
import com.xinguan14.jdyp.adapter.base.BaseRecyclerHolder;
import com.xinguan14.jdyp.adapter.base.IMutlipleItem;
import com.xinguan14.jdyp.base.BaseActivity;
import com.xinguan14.jdyp.bean.User;
import com.xinguan14.jdyp.myVeiw.CircleImageView;
import com.xinguan14.jdyp.util.ImageLoadOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;


public class StepActivity extends BaseActivity {


    @Bind(R.id.rc_view)
    RecyclerView rc_view;
    @Bind(R.id.sw_refresh)
    SwipeRefreshLayout sw_refresh;
    NearPeopleAdapter adapter;
    LinearLayoutManager layoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sport_near);
        IMutlipleItem<User> mutlipleItem = new IMutlipleItem<User>() {

            @Override
            public int getItemLayoutId(int viewtype) {
                return R.layout.item_yuepao;
            }

            @Override
            public int getItemViewType(int postion, User user) {
                return 0;
            }

            @Override
            public int getItemCount(List<User> list) {
                return list.size();
            }
        };
        adapter = new NearPeopleAdapter(this, mutlipleItem, null);
        rc_view.setAdapter(adapter);
        rc_view.setItemAnimator(new DefaultItemAnimator());

        layoutManager = new LinearLayoutManager(this);
        rc_view.setLayoutManager(layoutManager);

        sw_refresh.setEnabled(true);
        setListener();
    }

    private void setListener() {
//        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                sw_refresh.setRefreshing(true);
//                query();
//            }
//        });
        sw_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                query();
            }
        });
        adapter.setOnRecyclerViewListener(new OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {
                User user = adapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("u", user);
                startActivity(UserInfoActivity.class, bundle);
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
        adapter.bindDatas(getNearPeople());
        adapter.notifyDataSetChanged();
        sw_refresh.setRefreshing(false);
    }

    /**
     * 获取会话列表的数据：增加新朋友会话
     *
     * @return
     */
    List<User> userList = new ArrayList<>();

    private List<User> getNearPeople() {

        User me = BmobUser.getCurrentUser(getApplicationContext(), User.class);
        BmobQuery<User> bmobQuery = new BmobQuery<User>();
        bmobQuery.addWhereWithinKilometers("location", me.getLocation(), 1000);
        bmobQuery.findObjects(getApplicationContext(), new FindListener<User>() {
            @Override
            public void onSuccess(List<User> object) {
                userList.clear();
                for (User item : object) {
                    userList.add(item);
                }
            }

            public void onError(int i, String s) {
            }
        });

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

        User me = BmobUser.getCurrentUser(getApplicationContext(), User.class);

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

            if (user != null) {
                //头像
                if (user.getAvatar() != null && !user.getAvatar().equals("")) {
                    ImageLoader.getInstance().displayImage(user.getAvatar(), (CircleImageView) holder.getView(R.id.iv_near_avatar),
                            ImageLoadOptions.getOptions());
                } else {
                    holder.setImageResource(R.id.iv_near_avatar, R.mipmap.head);
                }
//                holder.setImageView(user == null ? null : user.getAvatar(), R.mipmap.head, R.id.iv_near_avatar);
                //名称
                holder.setText(R.id.tv_near_name, user.getNick());

                BmobGeoPoint location = me.getLocation();
                double currentLat = location.getLatitude();
                double currentLong = location.getLongitude();
                double distance = DistanceOfTwoPoints(currentLat, currentLong, user.getLocation().getLatitude(),
                        user.getLocation().getLongitude());
                double distance_km = distance / 1000;//km
                //距离
                holder.setText(R.id.tv_distance, user == null ? "未知1" : String.valueOf(distance_km) + "km");
            }
        }
    }
}
