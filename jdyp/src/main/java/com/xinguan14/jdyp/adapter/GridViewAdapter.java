package com.xinguan14.jdyp.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.adapter.base.BaseListAdapter;
import com.xinguan14.jdyp.adapter.base.BaseListHolder;
import com.xinguan14.jdyp.util.SysUtils;

import net.tsz.afinal.FinalBitmap;

import java.util.ArrayList;

/**
 * Created by wm on 2016/7/18.
 * 用来显示动态中的图片
 */
public class GridViewAdapter extends BaseListAdapter<String> {


    public Bitmap bitmaps[];
    private FinalBitmap finaImageLoader;
    private int wh;

    //构造GridView时需要传递两个参数，一个是需要使用的Activity，一个是显示的数组数据
    public GridViewAdapter(Activity context,ArrayList<String> list){
        super(context,list);
        //根据屏幕的宽度设定显示的图片的宽度
        this.wh=(SysUtils.getScreenWidth(context)-SysUtils.Dp2Px(context, 99))/3;

        this.finaImageLoader = FinalBitmap.create(context);/*获取一个FinalBitmap对象*/

        this.finaImageLoader.configLoadfailImage(R.drawable.love);/*图片加载完成前显示的图片*/
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (list.size()==0){
            return null;
        }

        final BaseListHolder holder = BaseListHolder.get(mContext,convertView,
                parent, R.layout.fragment_sport_square_item_grid,position);
        ImageView imageView = holder.getView(R.id.imageView);


        finaImageLoader.display(imageView, list.get(position));
        AbsListView.LayoutParams param = new AbsListView.LayoutParams(wh,wh);
       /* 创建一个布局（LayoutParams）的实例 param。
        AbsListView.LayoutParams(wh,wh) 指定了该布局的宽和高；*/
        convertView.setLayoutParams(param);

        return holder.getConvertView();

    }

}
