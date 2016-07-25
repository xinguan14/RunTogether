package com.xinguan14.jdyp.adapter;

import android.content.Context;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.adapter.base.BaseListHolder;
import com.xinguan14.jdyp.adapter.base.CommonAdapter;
import com.xinguan14.jdyp.util.SysUtils;

import java.util.ArrayList;

/**
 * Created by wm on 2016/7/18.
 * 用来显示动态中的图片
 */
public class GridViewAdapter extends CommonAdapter<String> {

    private int wh;

    //构造GridView时需要传递三个参数，一个是需要使用的Activity，一个是显示的数组数据，一个是item的布局文件
    public GridViewAdapter(Context context, ArrayList<String> list,int itemLayoutId){
        super(context,list,itemLayoutId);
        //根据屏幕的大小设置控件的大小
        this.wh=(SysUtils.getScreenWidth(context)- SysUtils.Dp2Px(context, 99))/3;
    }

    @Override
    public void convert(BaseListHolder holder, String item) {

        ImageView imageView = holder.getView(R.id.imageView);
        Glide
                .with(mContext)
                .load(item)
                .placeholder(R.drawable.pictures_no)
                .into(imageView);

        AbsListView.LayoutParams param = new AbsListView.LayoutParams(wh,wh);
       /* 创建一个布局（LayoutParams）的实例 param。
        AbsListView.LayoutParams(wh,wh) 指定了该布局的宽和高；*/
        holder.getConvertView().setLayoutParams(param);


    }

}
