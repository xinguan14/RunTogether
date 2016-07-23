package com.xinguan14.jdyp.adapter.base;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by wm on 2016/7/21.
 */
public class BaseListHolder {
    private SparseArray<View> mSparseArray;
    private int mPosition;
    private View mConvertView;

    public BaseListHolder(Activity context, ViewGroup parent, int layoutId,
                          int position){
        this.mPosition =position;
        this.mSparseArray = new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId,parent,false);
        mConvertView.setTag(this);

    }

    public static BaseListHolder get(Activity context,View convertView,
              ViewGroup parent,int layoutId,int position) {
        if (convertView == null) {
            return new BaseListHolder(context, parent, layoutId, position);
        } else {
            BaseListHolder holder = (BaseListHolder)convertView.getTag();
            holder.mPosition =position;
            return holder;
        }
    }

    public View getConvertView() {

        return mConvertView;
    }

//给TextView设置值
    public BaseListHolder setTextView(int viewId,String text){

        TextView tv =getView(viewId);
        tv.setText(text);
        return  this;
    }

    public BaseListHolder setImageResource(int viewId,int reId){

        ImageView iv =getView(viewId);
        iv.setImageResource(reId);
        return  this;
    }

    public BaseListHolder setImageBitmap(int viewId,Bitmap bitmap){

        ImageView iv =getView(viewId);
        iv.setImageBitmap(bitmap);
        return  this;
    }
//加载网络图片
    public BaseListHolder setImageURL(int viewId,String url){

        ImageView iv =getView(viewId);
        //ImageLoader.getInstance().loadImg(iv,url);
        //iv.setImageBitmap(bitmap);
        return  this;
    }
    /*
    * 使用泛型T表示类型为View的子类
    * 通过viewId获取控件
    * */
    public <T extends View> T getView(int viewId){
        View view = mSparseArray.get(viewId);
        if (view==null){
            view  = mConvertView.findViewById(viewId);
            mSparseArray.put(viewId,view);
        }
        return (T)view;
    }

}
