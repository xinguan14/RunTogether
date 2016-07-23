package com.xinguan14.jdyp.adapter;

/**
 * Created by yyz on 2016/7/19.
 */

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.xinguan14.jdyp.MyGridView;
import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.adapter.base.BaseListAdapter;
import com.xinguan14.jdyp.adapter.base.BaseListHolder;
import com.xinguan14.jdyp.bean.Post;
import com.xinguan14.jdyp.util.SysUtils;

import net.tsz.afinal.FinalBitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wm on 2016/7/18.
 * 用来显示动态的数据
 */
public class SayListViewAdapter extends BaseListAdapter<Post> {

    private FinalBitmap finalBitmap;
    private GridViewAdapter gridViewAdapter;
    private int wh;

    public SayListViewAdapter(Activity context, List<Post> list){
        super(context,list);
        //根据屏幕的大小设置控件的大小
        this.wh=(SysUtils.getScreenWidth(context)- SysUtils.Dp2Px(context, 99))/3;
        //传递的动态数据
        this.list = list;
        this.finalBitmap = FinalBitmap.create(context);
        //图片未加载成功显示的数据
        this.finalBitmap.configLoadfailImage(R.drawable.love);
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        if (list.size()==0){
            return null;
        }

        final BaseListHolder holder = BaseListHolder.get(mContext,convertView,
                parent, R.layout.fragment_sport_say_item,position);

        ImageView headPhoto = holder.getView(R.id.user_image);
        RelativeLayout rL = holder.getView(R.id.rl4);
        MyGridView gv_images = holder.getView(R.id.gv_images);
        ImageView commentView = holder.getView(R.id.comment_view) ;//显示评论和点赞的按钮

       final  RelativeLayout rLCommentView = holder.getView(R.id.rl_good_comment);
        final Post post = list.get(position);

        String name = null,time = null,content = null,headpath = null,contentimage = null;
        if(post !=null){
            name = post.getName();
            //time = post.getTime();
            content = post.getContent();
            headpath = post.getHeadPhoto();
            //contentimage = post.getImage();
        }
        //昵称
        if (name!=null&&!name.equals("")) {
           // holder.disName.setText(name);
            holder.setTextView(R.id.user_name,name);
        }
        //是否含有图片，有图片则显示gridview
        if (contentimage!=null&&!contentimage.equals("")) {
            rL.setVisibility(View.VISIBLE);
            initInfoImages(gv_images,contentimage);
        } else {
            rL.setVisibility(View.GONE);
        }
        //发布时间
        if (time!=null&&!time.equals("")) {
            holder.setTextView(R.id.info_tv_time,time);
        }
        //内容
        if (content!=null&&!content.equals("")) {
            holder.setTextView(R.id.content,content);
            //Linkify.addLinks(holder.content, Linkify.WEB_URLS);
        }
        //头像
        if (headpath!=null&&!headpath.equals("")) {
            finalBitmap.display(headPhoto,headpath);
        } else {
            headPhoto.setImageResource(R.drawable.love);
        }
        //点击头像的点击事件
        headPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Toast.makeText(mContext, "点击了头像", Toast.LENGTH_LONG).show();
            }
        });
        commentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //设置评论弹出框的显示
                rLCommentView.setVisibility(View.VISIBLE);
                rLCommentView.findViewById(R.id.good_img).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(mContext, "点了赞", Toast.LENGTH_LONG).show();
                    }
                });
                rLCommentView.findViewById(R.id.comment_img).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(mContext, "点了评论", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        return holder.getConvertView();
    }


    //初始化图片集，设定GridView的列数
    public void initInfoImages(MyGridView gv_images,final String imgspath){
        if(imgspath!=null&&!imgspath.equals("")){
            String[] imgs=imgspath.split("#");
            ArrayList<String> list=new ArrayList<String>();
            for(int i=0;i<imgs.length;i++){
                list.add(imgs[i]);
            }
            int w=0;
            switch (imgs.length) {
                case 1:
                    w=wh;
                    gv_images.setNumColumns(1);
                    break;
                case 2:
                case 4:
                    w=2*wh+SysUtils.Dp2Px(mContext, 2);
                    gv_images.setNumColumns(2);
                    break;
                case 3:
                case 5:
                case 6:
                    w=wh*3+ SysUtils.Dp2Px(mContext, 2)*2;
                    gv_images.setNumColumns(3);
                    break;
            }
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(w, RelativeLayout.LayoutParams.WRAP_CONTENT);
            gv_images.setLayoutParams(lp);
            /*第一个参数为宽的设置，第二个参数为高的设置。
            如果将一个View添加到一个Layout中，最好告诉Layout用户期望的布局方式，也就是将一个认可的layoutParams传递进去
            但LayoutParams类也只是简单的描述了宽高，宽和高都可以设置成三种值：
            1，一个确定的值；
            2，FILL_PARENT，即填满（和父容器一样大小）；
            3，WRAP_CONTENT，即包裹住组件就好。。*/

            gridViewAdapter=new GridViewAdapter(mContext, list);
            gv_images.setAdapter(gridViewAdapter);
            //点击图片的点击事件
            gv_images.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    Toast.makeText(mContext, "点击了第"+(arg2+1)+"张图片", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

}
