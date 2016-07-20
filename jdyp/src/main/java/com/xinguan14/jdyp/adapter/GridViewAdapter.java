package com.xinguan14.jdyp.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.util.SysUtils;
import net.tsz.afinal.FinalBitmap;

import java.util.ArrayList;

/**
 * Created by wm on 2016/7/18.
 */
public class GridViewAdapter extends BaseAdapter {

    Activity context;
    ArrayList<String> list;
    public Bitmap bitmaps[];
    private FinalBitmap finaImageLoader;
    private int wh;
    public GridViewAdapter(Activity context,ArrayList<String> list){
        this.context = context;
        this.list = list;
        this.wh=(SysUtils.getScreenWidth(context)-SysUtils.Dp2Px(context, 99))/3;
        this.finaImageLoader = FinalBitmap.create(context);/*获取一个FinalBitmap对象*/
        this.finaImageLoader.configLoadfailImage(R.drawable.love);/*图片加载完成前显示的图片*/
    }

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
    public View getView(int position, View view, ViewGroup parent) {
        Holder holder;//java内部类
        if(view==null){
            view = LayoutInflater.from(context).inflate(R.layout.fragment_sport_square_item_grid,null);
            holder = new Holder();
            holder.imageView = (ImageView)view.findViewById(R.id.imageView);
            view.setTag(holder);
            /*首先我们要知道setTag方法是干什么的，他是给View对象的一个标签，标签可以是任何内容，
            我们这里把他设置成了一个对象，因为我们是把item_gridview.xml的元素抽象出来成为一个类ViewHolder，
            用了setTag，这个标签就是ViewHolder实例化后对象的一个属性。我们之后对于ViewHolder实例化的对象holder的操作，
            都会因为Java的引用机制而一直存活并改变convertView的内容，而不是每次都是去new一个。我们就这样达到的重用*/
        }
        else{
            holder = (Holder)view.getTag();
        }

        finaImageLoader.display(holder.imageView, list.get(position));
        AbsListView.LayoutParams param = new AbsListView.LayoutParams(wh,wh);
       /* 创建一个布局（LayoutParams）的实例 param。
        AbsListView.LayoutParams(wh,wh) 指定了该布局的宽和高；*/
        view.setLayoutParams(param);
        return view;

    }
    class Holder{
        ImageView imageView;
    }

}
