package com.xinguan14.jdyp.ui.fragment.sportsfragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.adapter.SquareListViewAdapter;
import com.xinguan14.jdyp.base.BaseFragment;
import com.xinguan14.jdyp.bean.Post;
import com.xinguan14.jdyp.floatingactionbar.FloatingActionButton;
import com.xinguan14.jdyp.floatingactionbar.ScrollDirectionListener;
import com.xinguan14.jdyp.ui.AddPostActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class SquareFragment extends BaseFragment {

    private View rootView;
    //private  List<Post> mPostList;
    private SquareListViewAdapter mSquareListViewAdapter;

    @Bind(R.id.swipe_refredsh)
    SwipeRefreshLayout sps_refresh;
    @Bind(R.id.squareList)
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sport_aquare_main, container, false);
        ButterKnife.bind(this, rootView);
        query();
        sps_refresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        sps_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getActivity(), "刷新了",
                        Toast.LENGTH_SHORT).show();
                query();
            }
        });
        return rootView;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void query() {
        BmobQuery<Post> commentsBmobQuery = new BmobQuery<Post>();
        commentsBmobQuery.order("-createdAt");// 按照时间降序
        //查询发动态的用户信息
        commentsBmobQuery.include("author");
        commentsBmobQuery.findObjects(getActivity(), new FindListener<Post>() {
            @Override
            public void onSuccess(List<Post> list) {
                if (list != null) {
                    initData(list);
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(getActivity(), "网络未连接",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    //获得数据
    private void initData(List<Post> list) {

        mSquareListViewAdapter = new SquareListViewAdapter(getActivity(), list, R.layout.fragment_sport_square_item);
        listView.setAdapter(mSquareListViewAdapter);
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab_square);
        fab.attachToListView(listView, new ScrollDirectionListener() {
            @Override
            public void onScrollDown() {
            }

            @Override
            public void onScrollUp() {
            }
        }, new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        //浮动按钮点击事件
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AddPostActivity.class, null);

            }
        });

        sps_refresh.setRefreshing(false);
    }
}

