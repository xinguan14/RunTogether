package com.xinguan14.jdyp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.adapter.base.BaseListHolder;
import com.xinguan14.jdyp.util.SysUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wm on 2016/7/18.
 * 用来显示动态中的图片
 */
public class GridViewAdapter extends BaseAdapter {

    private int wh;
    protected List<String> mDatas;
    protected Context mContext;
    protected LayoutInflater mInflater;
    protected  int mItemLayoutId;

    //构造GridView时需要传递三个参数，一个是需要使用的Activity，一个是显示的数组数据，一个是item的布局文件
    public GridViewAdapter(Context context, ArrayList<String> list,int itemLayoutId){
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mDatas = list;
        this.mItemLayoutId = itemLayoutId;
        //根据屏幕的大小设置控件的大小
        this.wh=(SysUtils.getScreenWidth(context)- SysUtils.Dp2Px(context, 99))/3;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int i) {
        return mDatas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private BaseListHolder getViewHolder(int position, View convertView,ViewGroup parent)
    {
        return BaseListHolder.get(mContext, convertView, parent, mItemLayoutId, position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // System.out.println("GridView position:" + position);

        final BaseListHolder viewHolder = getViewHolder(position, convertView, parent);
        ModeDto modeDto = new ModeDto();
        if (position == 0 && modeDto.isHasFirstLoaded()) {
            return viewHolder.getConvertView();
        }
        if (position == 0) {
            modeDto.setHasFirstLoaded(true);
        }

            ImageView imageView = viewHolder.getView(R.id.imageView);
            Glide
                    .with(mContext)
                    .load(getItem(position))
                    .dontAnimate()
                    .placeholder(R.drawable.pictures_no)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);

            AbsListView.LayoutParams param = new AbsListView.LayoutParams(wh,wh);
       /* 创建一个布局（LayoutParams）的实例 param。
        AbsListView.LayoutParams(wh,wh) 指定了该布局的宽和高；*/
            viewHolder.getConvertView().setLayoutParams(param);

            return viewHolder.getConvertView();

    }

    class ModeDto {
        private boolean hasFirstLoaded = false;

        public boolean isHasFirstLoaded() {
            return hasFirstLoaded;
        }

        public void setHasFirstLoaded(boolean hasFirstLoaded) {
            this.hasFirstLoaded = hasFirstLoaded;
        }

    }

}
