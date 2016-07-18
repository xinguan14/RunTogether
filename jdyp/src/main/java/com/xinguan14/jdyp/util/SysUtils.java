package com.xinguan14.jdyp.util;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by wm on 2016/7/18.
 */
public class SysUtils {
    public  static int Dp2Px(Context context, float dp){
        final float scale = context.getResources().getDisplayMetrics().density;
       /* android context.getResources().getDisplayMetrics()这是获取手机屏幕参数，
       后面的density就是屏幕的密度，类似分辨率，但不是
       float scale = getResources().getDisplayMetrics().density;
       density值表示每英寸有多少个显示点，与分辨率是两个不同的概念。
       这个得到的不应该叫做密度，应该是密度的一个比例。不是真实的屏幕密度，而是相对于某个值的屏幕密度。
       也可以说是相对密度*/
        return (int) (dp * scale + 0.5f);
    }
    public static int getScreenWidth(Activity activity){
        WindowManager windowManager = activity.getWindowManager();
       /* WindowManager主要用来管理窗口的一些状态、属性、view增加、删除、更新、窗口顺序、消息收集和处理等。*/
        Display display = windowManager.getDefaultDisplay();
      /*  获取默认的显示对象返回值
        默认的Display对象*/
        return display.getWidth();
    }


}
    /*根据手机的分辨率从 dp 的单位 转成为 px(像素)

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

     根据手机的分辨率从 px(像素) 的单位 转成为 dp

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }*/
