package com.xinguan14.jdyp.ui.fragment.sportsfragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
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


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		friendQuery();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 rootView = inflater.inflate(R.layout.fragment_sport_say_main, container, false);
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
				String[] userId = new String[list.size()];
				for (int i = 0; i < list.size(); i++) {
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
		//userId[userId.length-1]=getCurrentUid();//将当前用户的Id加到数组中
		final BmobQuery<Comments> query = new BmobQuery<Comments>();
		query.order("-createdAt");// 按照时间降序
		query.addWhereContainsAll("userId", Arrays.asList(userId));//查询要显示的用户的数组中的数据
		/*//判断是否有缓存，该方法必须放在查询条件（如果有的话）都设置完之后再来调用才有效，就像这里一样。
		boolean isCache = query.hasCachedResult(getActivity(),Comments.class);
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
						names[i] = comments.getUserId();
					}
					setData(names, contents);
				}else {
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setMessage("没有数据");
					builder.setTitle("提示");
					builder.create().show();
				}

			}

			@Override
			public void onError(int i, String s) {
				Toast.makeText(getActivity(), "未查询到数据",
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
