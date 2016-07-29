package com.xinguan14.jdyp.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.xinguan14.jdyp.MyVeiw.CustomDialog;
import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.adapter.OnRecyclerViewListener;
import com.xinguan14.jdyp.adapter.base.BaseRecyclerAdapter;
import com.xinguan14.jdyp.adapter.base.BaseRecyclerHolder;
import com.xinguan14.jdyp.adapter.base.IMutlipleItem;
import com.xinguan14.jdyp.base.ParentWithNaviActivity;
import com.xinguan14.jdyp.base.ParentWithNaviFragment;
import com.xinguan14.jdyp.bean.Friend;
import com.xinguan14.jdyp.bean.User;
import com.xinguan14.jdyp.db.NewFriendManager;
import com.xinguan14.jdyp.event.RefreshEvent;
import com.xinguan14.jdyp.model.UserModel;
import com.xinguan14.jdyp.ui.activity.ChatActivity;
import com.xinguan14.jdyp.ui.activity.NewFriendActivity;
import com.xinguan14.jdyp.ui.activity.SearchUserActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;

/**
 * 联系人界面
 *
 * @author :smile
 * @project:ContactFragment
 * @date :2016-04-27-14:23
 */
public class ContactFragment extends ParentWithNaviFragment {

    @Bind(R.id.rc_view)
    RecyclerView rc_view;
    @Bind(R.id.sw_refresh)
    SwipeRefreshLayout sw_refresh;
    ContactAdapter adapter;
    LinearLayoutManager layoutManager;
    private List<String> mDilogList;  // 删除

    @Override
    protected String title() {
        return "联系人";
    }

    @Override
    public Object right() {
        return R.drawable.base_action_bar_add_bg_selector;
    }

    @Override
    public ParentWithNaviActivity.ToolBarListener setToolBarListener() {
        return new ParentWithNaviActivity.ToolBarListener() {
            @Override
            public void clickLeft() {

            }

            @Override
            public void clickRight() {
                startActivity(SearchUserActivity.class, null);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_contact, container, false);
        initNaviView();
        ButterKnife.bind(this, rootView);
        IMutlipleItem<Friend> mutlipleItem = new IMutlipleItem<Friend>() {

            @Override
            public int getItemViewType(int postion, Friend friend) {
                if (postion == 0) {
                    return ContactAdapter.TYPE_NEW_FRIEND;
                } else {
                    return ContactAdapter.TYPE_ITEM;
                }
            }

            @Override
            public int getItemLayoutId(int viewtype) {
                if (viewtype == ContactAdapter.TYPE_NEW_FRIEND) {
                    return R.layout.header_new_friend;
                } else {
                    return R.layout.item_contact;
                }
            }

            @Override
            public int getItemCount(List<Friend> list) {
                return list.size() + 1;
            }
        };

        mDilogList = new ArrayList<String>();
        mDilogList.add("删除");

        layoutManager = new LinearLayoutManager(getActivity());
        rc_view.setLayoutManager(layoutManager);
        adapter = new ContactAdapter(getActivity(), mutlipleItem, null);
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
                if (position == 0) {//跳转到新朋友页面
                    startActivity(NewFriendActivity.class, null);
                } else {
                    Friend friend = adapter.getItem(position);
                    User user = friend.getFriendUser();
                    BmobIMUserInfo info = new BmobIMUserInfo(user.getObjectId(), user.getUsername(), user.getAvatar());
                    //启动一个会话，实际上就是在本地数据库的会话列表中先创建（如果没有）与该用户的会话信息，且将用户信息存储到本地的用户表中
                    BmobIMConversation c = BmobIM.getInstance().startPrivateConversation(info, null);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("c", c);
                    startActivity(ChatActivity.class, bundle);
                }
            }

            @Override
            public boolean onItemLongClick(final int position) {

                Log.i("info", "长按" + position);
                if (position == 0) {
                    return true;
                }
                final CustomDialog customDialog = new CustomDialog(getActivity(), "提示", mDilogList);
                customDialog.show();
                customDialog.setItemOnClickListener(new CustomDialog.MyItemOnClickListener() {

                    @Override
                    public void itemOnClick(int itemPosition) {

                        UserModel.getInstance().deleteFriend(adapter.getItem(position), new DeleteListener() {
                            @Override
                            public void onSuccess() {
                                toast("好友删除成功");
                                adapter.remove(position);
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                toast("好友删除失败：" + i + ",s =" + s);
                            }
                        });
                        adapter.notifyDataSetChanged();
                        customDialog.cancel();
                    }
                });

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
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    /**
     * 注册自定义消息接收事件
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(RefreshEvent event) {
        //重新刷新列表
        log("---联系人界面接收到自定义消息---");
        adapter.notifyDataSetChanged();
    }

    /**
     * 查询本地会话
     */
    public void query() {
        UserModel.getInstance().queryFriends(new FindListener<Friend>() {
            @Override
            public void onSuccess(List<Friend> list) {
                adapter.bindDatas(list);
                adapter.notifyDataSetChanged();
                sw_refresh.setRefreshing(false);
            }

            @Override
            public void onError(int i, String s) {
                adapter.bindDatas(null);
                adapter.notifyDataSetChanged();
                sw_refresh.setRefreshing(false);
            }
        });
    }

    /**
     * 联系人
     * 一种简洁的Adapter实现方式，可用于多种Item布局的recycleView实现，不用再写ViewHolder啦
     *
     * @author :smile
     * @project:ContactNewAdapter
     * @date :2016-04-27-14:18
     */
    class ContactAdapter extends BaseRecyclerAdapter<Friend> {

        public static final int TYPE_NEW_FRIEND = 0;
        public static final int TYPE_ITEM = 1;

        public ContactAdapter(Context context, IMutlipleItem<Friend> items, Collection<Friend> datas) {
            super(context, items, datas);
        }

        @Override
        public void bindView(BaseRecyclerHolder holder, Friend friend, int position) {
            if (holder.layoutId == R.layout.item_contact) {
                User user = friend.getFriendUser();
                //好友头像
                holder.setImageView(user == null ? null : user.getAvatar(), R.mipmap.head, R.id.iv_recent_avatar);
                //好友名称
                holder.setText(R.id.tv_recent_name, user == null ? "未知" : user.getUsername());
            } else if (holder.layoutId == R.layout.header_new_friend) {
                if (NewFriendManager.getInstance(context).hasNewFriendInvitation()) {
                    holder.setVisible(R.id.iv_msg_tips, View.VISIBLE);
                } else {
                    holder.setVisible(R.id.iv_msg_tips, View.GONE);
                }
            }
        }

    }

}
