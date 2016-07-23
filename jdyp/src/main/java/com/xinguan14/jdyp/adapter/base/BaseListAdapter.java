package com.xinguan14.jdyp.adapter.base;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by wm on 2016/7/21.
 */
public abstract class BaseListAdapter<T> extends BaseAdapter {

    protected LayoutInflater mInflater;
    protected Activity mContext;
    //显示的动态的数据的集合
    protected List<T> list;

    public BaseListAdapter(Activity context, List<T> list){
        //获取布局文件
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.list = list;

    }

    public List<T> getlist(){
        return  list;
    }

    @Override
    public int getCount() {
        //如何list不为空则返回list的长度
        return list == null ? 0 : list.size();
    }

    @Override
    public T getItem(int position) {

        return list == null ? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list == null ? null : position;
    }

    @Override
    public  abstract View getView(int position, View convertView, ViewGroup parent) ;

}
