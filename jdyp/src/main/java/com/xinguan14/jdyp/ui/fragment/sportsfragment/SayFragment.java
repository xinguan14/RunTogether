package com.xinguan14.jdyp.ui.fragment.sportsfragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.bean.Comments;
import com.xinguan14.jdyp.bean.User;

import java.util.ArrayList;
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
		query();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 rootView = inflater.inflate(R.layout.fragment_sport_say_main, container, false);
		//mListView = (ListView) rootView.findViewById(R.id.sport_say_list);
		return rootView;

	}
	//获得数据
	//private String[] names;
	//private  String[] contents;
	private void query(){
		final BmobQuery<Comments> query = new BmobQuery<Comments>();
		//获取userID是当前用户ID的动态
		query.addWhereEqualTo("userId",getCurrentUid());
		query.findObjects(getActivity(), new FindListener<Comments>() {
			@Override
			public void onSuccess(List<Comments> list) {
				/*AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("query");
				String str = "";
				for(Comments comments:list){
					str +=comments.getUserId()+":"+comments.getContent();
				}
				builder.setMessage(str);
				builder.create().show();*/
				int length = list.size();
				String[] names = new String[length];
				String[] contents = new String[length];
				for(int i=0;i<list.size();i++)
				{
					Comments comments=list.get(i);
					contents[i]=comments.getContent();
					names[i]=comments.getUserId();
				}
				setData(names,contents);
				/*//测试数组中的数据
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("query");
				String str = "";
				for(int i=0;i<names.length;i++)
				{
					str =names[i]+":"+contents[i];
				}
				builder.setMessage(str);
				builder.create().show();*/
			}

			@Override
			public void onError(int i, String s) {

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
