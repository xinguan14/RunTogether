package com.xinguan14.jdyp.ui.fragment.sportsfragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.adapter.SayListViewAdapter;
import com.xinguan14.jdyp.bean.Friend;
import com.xinguan14.jdyp.bean.Post;
import com.xinguan14.jdyp.bean.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

/**
 * @描述 在Fragment中要使用ListView，必须要用ListFragment
 */
public class SayFragment extends android.support.v4.app.ListFragment {

//    @Bind(R.id.progress_load)
//    ProgressBar progress_load;

    @Bind(R.id.swipe_container)
    SwipeRefreshLayout sps_refresh;

    private View rootView;
    //存放动态的集合
    private List<Post> mPostList;
    //存放评论的集合
    private View mCommentView;
    //适配器
    private SayListViewAdapter mSayListViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sport_main, container, false);
        ButterKnife.bind(this, rootView);
        //mPostList = new ArrayList<Post>();
        //查询数据并绑定数据
        friendQuery();
        //刷新监听
        sps_refresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        sps_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getActivity(), "刷新了",
                        Toast.LENGTH_SHORT).show();
                friendQuery();
            }
        });
        return rootView;
    }


    //获的当前用户朋友的数据
    private void friendQuery() {
        //当前用户的id
        //String currentUId = getCurrentUid();

        //获取当前的用户
        User user = BmobUser.getCurrentUser(getActivity(), User.class);
        BmobQuery<Friend> userFriends = new BmobQuery<Friend>();
        userFriends.addWhereEqualTo("user", user);//查询当前用户的朋友
        userFriends.findObjects(getActivity(), new FindListener<Friend>() {
            @Override
            public void onSuccess(List<Friend> list) {
                int length = list.size();
                User[] userId = new User[length];
                for (int i = 0; i < length; i++) {

                    userId[i] = list.get(i).getFriendUser();
                }
                userQuery(userId);
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(getActivity(), "未查询到数据",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    //获得运动圈要显示的用户动态的数据,包括用户本人和朋友
    private void userQuery(User[] userId) {
        //添加当前用户
        User[] showUser = new User[userId.length + 1];
        showUser[userId.length] = BmobUser.getCurrentUser(getActivity(), User.class);
        for (int i = 0; i < userId.length; i++) {
            showUser[i] = userId[i];
        }
        //获取显示用户的Id
        String[] showUserId = new String[showUser.length];
        for (int i = 0; i < showUserId.length; i++) {
            showUserId[i] = showUser[i].getObjectId();
        }

	/*	String str = "要显示有"+showUserId.length+"个人";
                Toast.makeText(getActivity(), str,
						Toast.LENGTH_SHORT).show();*/
        //查询动态信息
        final BmobQuery<Post> query = new BmobQuery<Post>();
        query.order("-createdAt");// 按照时间降序
        //查询要显示的用户的所有动态
        query.addWhereContainedIn("author", Arrays.asList(showUserId));
        //查询发动态的用户信息
        query.include("author");

        query.findObjects(getActivity(), new FindListener<Post>() {
            @Override
            public void onSuccess(List<Post> list) {
                if (list != null) {
                    int length = list.size();
                    String[] names = new String[length];
                    String[] contents = new String[length];
                    String[] avatar = new String[length];
                    String[] images = new String[length];
                    for (int i = 0; i < list.size(); i++) {
                        Post post = list.get(i);
                        contents[i] = post.getContent();
                        names[i] = post.getAuthor().getUsername();
                        avatar[i] = post.getAuthor().getAvatar();
                        images[i] = post.getImageurl();
                    }

                    initData(names, contents, avatar, images);

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("未查询到数据");
                    builder.setTitle("提示");
                    builder.create().show();
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(getActivity(), "网络未连接",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    //绑定数据
    private void initData(String[] names, String[] contents, String[] avatar, String[] images) {
        List<Post> mPostList = new ArrayList<Post>();
        Post post = null;
        for (int i = 0; i < names.length; i++) {
            post = new Post();
            post.setName(names[i]);
            post.setContent(contents[i]);
            post.setImageurl(images[i]);
            post.setHeadPhoto(avatar[i]);
            mPostList.add(post);
        }
        mSayListViewAdapter = new SayListViewAdapter(getActivity(), mPostList, R.layout.fragment_sport_say_item);
        setListAdapter(mSayListViewAdapter);
        //关闭刷新
        sps_refresh.setRefreshing(false);
    }
}
