package com.xinguan14.jdyp.MyVeiw;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.xinguan14.jdyp.R;

import java.util.List;

/**
 * 描述：
 * 作者：HMY
 * 时间：2016/5/12
 */
public class NineGridTestLayout extends NineGridLayout {

    protected static final int MAX_W_H_RATIO = 3;

    public NineGridTestLayout(Context context) {
        super(context);
    }

    public NineGridTestLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean displayOneImage(final RatioImageView imageView, String url, final int parentWidth) {
        Glide.with(mContext)
                .load(url)
                .asBitmap()
                .placeholder(R.drawable.pictures_no)
                .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {

                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                        // Do something with bitmap here.
                        int h=bitmap.getHeight(); //获取bitmap信息，可赋值给外部变量操作，也可在此时行操作。
                        int w=bitmap.getWidth();
                        int newW;
                        int newH;
                        if (h > w * MAX_W_H_RATIO) {//h:w = 5:3
                            newW = parentWidth / 2;
                            newH = newW * 5 / 3;
                        } else if (h < w) {//h:w = 2:3
                            newW = parentWidth * 2 / 3;
                            newH = newW * 2 / 3;
                        } else {//newH:h = newW :w
                            newW = parentWidth / 2;
                            newH = h * newW / w;
                        }

                        setOneImageLayoutParams(imageView, newW, newH);
                        imageView.setImageBitmap(bitmap);
                    }

                });

        return false;// true 代表按照九宫格默认大小显示(此时不要调用setOneImageLayoutParams)；false 代表按照自定义宽高显示。
    }
    @Override
    protected void displayImage(RatioImageView imageView, String url) {
        Glide.with(mContext)
                .load(url)
                .dontAnimate()
                .placeholder(R.drawable.pictures_no)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }
    @Override
    protected void onClickImage(int i, String url, List<String> urlList) {
        Toast.makeText(mContext, "点击了图片" + url, Toast.LENGTH_SHORT).show();
    }
}
