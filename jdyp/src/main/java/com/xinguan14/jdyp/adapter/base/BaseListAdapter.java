package com.xinguan14.jdyp.adapter.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class BaseListAdapter<T> extends BaseAdapter
{
	protected LayoutInflater mInflater;
	protected Context mContext;
	protected List<T> mDatas;
	protected final int mItemLayoutId;

	public BaseListAdapter(Context context, List<T> mDatas, int itemLayoutId)
	{
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		this.mDatas = mDatas;
		this.mItemLayoutId = itemLayoutId;
	}

	@Override
	public int getCount()
	{
		return mDatas.size();
	}

	@Override
	public T getItem(int position)
	{
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		final BaseListHolder viewHolder = getViewHolder(position, convertView, parent);

		//子类要重写的方法，给指定控件绑定数据
		convert(viewHolder, getItem(position));

		return viewHolder.getConvertView();

	}

	public abstract void convert(BaseListHolder helper, T item);

	private BaseListHolder getViewHolder(int position, View convertView,ViewGroup parent)
	{
		return BaseListHolder.get(mContext, convertView, parent, mItemLayoutId,
				position);
	}

}
