package com.xinguan14.jdyp.ui.fragment.sportsfragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.bean.Comments;
import com.xinguan14.jdyp.bean.Friend;
import com.xinguan14.jdyp.bean.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

/**
 * @描述 在Fragment中要使用ListView，必须要用ListFragment
 * */
public class SayFragment extends ListFragment {

	private View rootView;
	private SimpleAdapter simpleAdapter;
	//@Bind(R.id.sw_refresh)
	//SwipeRefreshLayout sw_refresh;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		friendQuery();
		refresh();

	}
	//下拉刷新
	private  void refresh(){
		final SwipeRefreshLayout sps_refresh = (SwipeRefreshLayout)getActivity().findViewById(R.id.swipe_container) ;
		sps_refresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
		sps_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				friendQuery();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						sps_refresh.setRefreshing(false);
					}
				}, 1000);
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 rootView = inflater.inflate(R.layout.fragment_sport_main, container, false);
		return rootView;

	}
	//获的当前用户朋友的数据
	private void friendQuery() {
		String currentUId = getCurrentUid();
		BmobQuery<Friend> user = new BmobQuery<Friend>();
		user.addWhereEqualTo("user", currentUId);//查询当前用户的朋友
		user.findObjects(getActivity(), new FindListener<Friend>() {
			@Override
			public void onSuccess(List<Friend> list) {
				int length = list.size();
				String[] userId = new String[length];
				for (int i = 0; i < length; i++) {
					Friend friend = list.get(i);
					userId[i] = friend.getFriendUser().getObjectId();
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
//获得运动圈要显示的用户动态的数据
	private void userQuery( String[] userId) {
		String[] showUserId = new String[userId.length+1];
		showUserId[userId.length]=getCurrentUid();
		for (int i=0;i<userId.length;i++){
				showUserId[i] = userId[i];
		}

		final BmobQuery<Comments> query = new BmobQuery<Comments>();
		query.order("-createdAt");// 按照时间降序
	   //查询要显示的用户的数组中的数据
		query.addWhereContainedIn("userId", Arrays.asList(showUserId));
		//判断是否有缓存，该方法必须放在查询条件（如果有的话）都设置完之后再来调用才有效，就像这里一样。
		/*boolean isCache = query.hasCachedResult(getActivity(),Comments.class);
		if(isCache){
			// 如果有缓存的话，先从缓存读取数据，如果没有，再从网络获取。
			query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
		}else{
			// 如果没有缓存的话，先从网络读取数据，如果没有，再从缓存中获取。
			query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
		}*/
		query.findObjects(getActivity(), new FindListener<Comments>() {
			@Override
			public void onSuccess(List<Comments> list) {
				if(list!=null) {

					int length = list.size();
					String[] names = new String[length];
					String[] contents = new String[length];
					for (int i = 0; i < list.size(); i++) {
						Comments comments = list.get(i);
						contents[i] = comments.getContent();
						names[i] = comments.getUserName();
					}
					setData(names, contents);
				}else {
					/*AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setMessage("没有数据");
					builder.setTitle("提示");
					builder.create().show();*/
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
	private void setData(String[] names,String[] contents) {
		//创建一个List集合，List集合的元素是Map
		List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();
		for (int i=0;i<names.length;i++){
			Map<String,Object> listItem = new HashMap<String,Object>();
			listItem.put("userImage",R.drawable.default_qq_avatar);
			listItem.put("userName",names[i]);
			listItem.put("content",contents[i]);
			listItems.add(listItem);
		}
		simpleAdapter = new SimpleAdapter( getActivity(),listItems,
				R.layout.fragment_sport_say_item,
				new String[]{"userImage","userName","content"},
				new int[]{R.id.user_image,R.id.user_name,R.id.content});

		setListAdapter(simpleAdapter);
	}



	public String getCurrentUid(){
		return BmobUser.getCurrentUser(getActivity(),User.class).getObjectId();
	}
}
