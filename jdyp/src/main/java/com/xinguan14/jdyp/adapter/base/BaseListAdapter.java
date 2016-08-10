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
	//每个item的数据
	protected List<T> mDatas;
	//item的布局文件
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
