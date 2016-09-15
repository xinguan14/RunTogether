package com.xinguan14.jdyp.myVeiw;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.PopupWindow;

import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.util.SysUtils;

/**
 * Created by wm on 2016/7/17.
 *
 */
public class SelectPicPopupWindow extends PopupWindow {
    private Button  pickPhotoBtn, cancelBtn;
    private View mMenuView;
    private int width;
    private int height;

    @SuppressLint("InflateParams")
    public SelectPicPopupWindow(Context context, OnClickListener itemsOnClick) {
        super(context);
        this.width= SysUtils.getScreenWidth(context);
        this.height=SysUtils.getScreenHeight(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.dialod_choose_picture, null);

        pickPhotoBtn = (Button) mMenuView.findViewById(R.id.pickPhotoBtn);
        cancelBtn = (Button) mMenuView.findViewById(R.id.cancelBtn);
        // 设置按钮监听
        cancelBtn.setOnClickListener(itemsOnClick);
        pickPhotoBtn.setOnClickListener(itemsOnClick);

        // 设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(width);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(height);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.PopupAnimation);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x80000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new OnTouchListener() {

            @Override
            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

    }
}
