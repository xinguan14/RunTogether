
package com.xinguan14.jdyp.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.adapter.OnRecyclerViewListener;
import com.xinguan14.jdyp.adapter.base.BaseRecyclerAdapter;
import com.xinguan14.jdyp.adapter.base.BaseRecyclerHolder;
import com.xinguan14.jdyp.adapter.base.IMutlipleItem;
import com.xinguan14.jdyp.base.BaseActivity;
import com.xinguan14.jdyp.bean.AddFriendMessage;
import com.xinguan14.jdyp.bean.User;
import com.xinguan14.jdyp.myVeiw.CircleImageView;
import com.xinguan14.jdyp.util.ImageLoadOptions;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


public class YuepaoActivity extends BaseActivity {


    @Bind(R.id.rc_view)
    RecyclerView rc_view;
    @Bind(R.id.sw_refresh)

    SwipeRefreshLayout sw_refresh;
    private NearPeopleAdapter adapter;
    private LinearLayoutManager layoutManager;

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 查询人
     */
    public void query() {
        queryNear();
        adapter.notifyDataSetChanged();
        sw_refresh.setRefreshing(false);
    }

    /**
     * 获取附近的列表的数据
     *
     */
    private void queryNear() {
        User me = BmobUser.getCurrentUser(getApplicationContext(), User.class);
        BmobQuery<User> bmobQuery = new BmobQuery<User>();
        bmobQuery.addWhereWithinKilometers("location", me.getLocation(), 1000);
        bmobQuery.findObjects(getApplicationContext(), new FindListener<User>() {
            @Override
            public void onSuccess(List<User> object) {
                if (object != null) {
                    adapter.bindDatas(object);
                }
            }

            public void onError(int i, String s) {
                Toast.makeText(getApplicationContext(), "网络未连接",
                        Toast.LENGTH_SHORT).show();
            }
        });
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

        BmobIMUserInfo info;
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

            //构造聊天方的用户信息:传入用户id、用户名和用户头像三个参数
            info = new BmobIMUserInfo(user.getObjectId(), user.getNick(), user.getAvatar());

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
                if (!user.getSex()) {
                    holder.setImageResource(R.id.iv_sex, R.mipmap.sex_woman);

                }
                BmobGeoPoint location = me.getLocation();
                double currentLat = location.getLatitude();
                double currentLong = location.getLongitude();
                double distance = DistanceOfTwoPoints(currentLat, currentLong, user.getLocation().getLatitude(),
                        user.getLocation().getLongitude());
                double distance_km = distance / 1000;//km
                //距离
                holder.setText(R.id.tv_distance, user == null ? "未知1" : String.valueOf(distance_km) + "km");
                holder.getView(R.id.bt_yuepao).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //启动一个会话，如果isTransient设置为true,则不会创建在本地会话表中创建记录，
                        //设置isTransient设置为false,则会在本地数据库的会话列表中先创建（如果没有）与该用户的会话信息，且将用户信息存储到本地的用户表中
                        BmobIMConversation c = BmobIM.getInstance().startPrivateConversation(info, true, null);
                        //这个obtain方法才是真正创建一个管理消息发送的会话
                        BmobIMConversation conversation = BmobIMConversation.obtain(BmobIMClient.getInstance(), c);
                        AddFriendMessage msg = new AddFriendMessage();
                        User currentUser = BmobUser.getCurrentUser(YuepaoActivity.this, User.class);
                        msg.setContent("可以一起约跑吗");//给对方的一个留言信息
                        Map<String, Object> map = new HashMap<>();
                        map.put("name", currentUser.getUsername());//发送者姓名，这里只是举个例子，其实可以不需要传发送者的信息过去
                        map.put("avatar", currentUser.getAvatar());//发送者的头像
                        map.put("uid", currentUser.getObjectId());//发送者的uid
                        msg.setExtraMap(map);
                        conversation.sendMessage(msg, new MessageSendListener() {
                            @Override
                            public void done(BmobIMMessage msg, BmobException e) {
                                if (e == null) {//发送成功
                                    toast("约跑请求发送成功，等待验证");
                                } else {//发送失败
                                    toast("发送失败:" + e.getMessage());
                                }
                            }
                        });
                    }
                });
            }
        }
    }
}
