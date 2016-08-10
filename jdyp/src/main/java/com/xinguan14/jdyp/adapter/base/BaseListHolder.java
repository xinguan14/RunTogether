package com.xinguan14.jdyp.adapter.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xinguan14.jdyp.util.ImageLoader;

/**
 * Created by wm on 2016/7/21.
 */
public class BaseListHolder {
    private SparseArray<View> mSparseArray;
    private int mPosition;
    private View mConvertView;

    public BaseListHolder(Context context, ViewGroup parent, int layoutId,
                          int position){
        this.mPosition =position;
        Log.i("info","创建holder，当前holder的位置"+position);
        this.mSparseArray = new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId,parent,false);
        mConvertView.setTag(this);

    }

    //初始化holder
    public static BaseListHolder get(Context context,View convertView,
              ViewGroup parent,int layoutId,int position) {
        if (convertView == null) {
            return new BaseListHolder(context, parent, layoutId, position);
        } else {
            BaseListHolder holder = (BaseListHolder)convertView.getTag();
            holder.mPosition =position;
            Log.i("info","已有holder,当前holder的位置"+position);
            return holder;
        }
    }

    public View getConvertView() {

        return mConvertView;
    }

    public  int getPosition(){
        return mPosition;
    }
//给TextView设置值
    public BaseListHolder setTextView(int viewId,String text){

        //通过viewId在容器mSparseArray中寻找控件
        TextView tv =getView(viewId);
        tv.setText(text);
        return  this;
    }

    public BaseListHolder setImageResource(int viewId,int reId){

        ImageView iv =getView(viewId);
        iv.setImageResource(reId);
        return  this;
    }

//通过Bitmap给Imageview设置图片
    public BaseListHolder setImageBitmap(int viewId,Bitmap bitmap){

        ImageView iv =getView(viewId);
        iv.setImageBitmap(bitmap);
        return  this;
    }

//通过URL给给Imageview设置图片
public BaseListHolder setImageByUrl(int viewId, String url)
{
    ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(url, (ImageView) getView(viewId));
    return this;
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
