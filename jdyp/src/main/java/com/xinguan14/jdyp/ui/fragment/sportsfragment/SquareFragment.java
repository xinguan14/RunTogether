package com.xinguan14.jdyp.ui.fragment.sportsfragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.adapter.SquareListViewAdapter;
import com.xinguan14.jdyp.bean.Post;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class SquareFragment extends android.support.v4.app.ListFragment {

	private View rootView;
	//private  List<Post> mPostList;
	private SquareListViewAdapter mSquareListViewAdapter;
	private SwipeRefreshLayout sps_refresh;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_sport_aquare_main, container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//mPostList = new ArrayList<Post>();
		query();
		sps_refresh = (SwipeRefreshLayout)getActivity().findViewById(R.id.swipe_refredsh) ;
		sps_refresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
		sps_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				Toast.makeText(getActivity(), "刷新了",
						Toast.LENGTH_SHORT).show();
				query();
			}
		});
	}

	private void query() {
		BmobQuery<Post> commentsBmobQuery = new BmobQuery<Post>();
		commentsBmobQuery.order("-createdAt");// 按照时间降序
		//查询发动态的用户信息
		commentsBmobQuery.include("author");
		commentsBmobQuery.findObjects(getActivity(), new FindListener<Post>() {
			@Override
			public void onSuccess(List<Post> list) {
				if(list!=null) {
					int length = list.size();
					String[] names = new String[length];
					String[] contents = new String[length];
					String[] avatar = new String[length];
					String[] images = new String[length];
					for (int i = 0; i < list.size(); i++) {
						Post post = list.get(i);
						contents[i] = post.getContent();
						names[i] = post.getAuthor().getUsername();
						avatar[i]= post.getAuthor().getAvatar();
						images[i]= post.getImageurl();
					}
					initData(names,contents,avatar,images);
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
	private void initData(String[] names,String[] contents,String[] avatar,String[] images){
		List<Post> mPostList = new ArrayList<Post>();
		Post post =null;
		for(int i = 0;i<names.length;i++){
			post = new Post();
			post.setName(names[i]);
			post.setContent(contents[i]);
			post.setImageurl(images[i]);
			post.setHeadPhoto(avatar[i]);
			mPostList.add(post);
		}
		mSquareListViewAdapter = new SquareListViewAdapter(getActivity(), mPostList,R.layout.fragment_sport_square_item);
		setListAdapter(mSquareListViewAdapter);
		sps_refresh.setRefreshing(false);
	}
}

