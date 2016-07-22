package com.xinguan14.jdyp;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * 自定义dialog
 * 
 * @author xwj
 * @date 2014/9/25
 */
public class CustomDialog extends Dialog {

	private Context mContext;
	private String title; // dialog的标题
	private List<String> list; // 此处为dilog内容，暂定为string类型
	private ListView mListView;

	public CustomDialog(Context context, String title, List<String> list) {
		super(context, R.style.customDialog); // 取得dillog自定义的样式
		this.mContext = context;
		this.title = title;
		this.list = list;
		initComponent();

	}

	private void initComponent() {
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.custom_dialog, null);
		((TextView) view.findViewById(R.id.item_dialog_title_tv))
				.setText(title);
		mListView = (ListView) view.findViewById(R.id.item_dialog_list_lv);
		mListView.setAdapter(new MyAdapter());
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				itemOnClickListener.itemOnClick(position);
			}

		});
		this.setContentView(view);
	}

	/**
	 * mListView的内容适配器
	 * 
	 * @author TY
	 * 
	 */
	public class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			return list.size();
		}

		@Override
		public Object getItem(int position) {

			return list.get(position);
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder viewHolder = null;

			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = View.inflate(mContext,
						R.layout.custom_dialog_item, null);
				viewHolder.textView = (TextView) convertView
						.findViewById(R.id.item);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.textView.setText(list.get(position));

			return convertView;
		}

		private class ViewHolder {
			private TextView textView;
		}

	}

	private MyItemOnClickListener itemOnClickListener;

	
	public interface MyItemOnClickListener {
		public void itemOnClick(int itemPosition);
	}
	
	public void setItemOnClickListener(MyItemOnClickListener myItemOnClickListener){
		this.itemOnClickListener = myItemOnClickListener;
	}
	
	

}
