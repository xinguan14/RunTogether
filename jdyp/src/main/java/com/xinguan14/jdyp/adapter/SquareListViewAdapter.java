package com.xinguan14.jdyp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.adapter.base.BaseListAdapter;
import com.xinguan14.jdyp.adapter.base.BaseListHolder;
import com.xinguan14.jdyp.bean.Comment;
import com.xinguan14.jdyp.bean.Post;
import com.xinguan14.jdyp.bean.User;
import com.xinguan14.jdyp.myVeiw.AddCommentPopupWindow;
import com.xinguan14.jdyp.myVeiw.NineGridTestLayout;
import com.xinguan14.jdyp.ui.CheckUserInfoByUser;
import com.xinguan14.jdyp.ui.fragment.sportsfragment.ItemDetailsActivity;
import com.xinguan14.jdyp.util.ImageLoadOptions;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by wm on 2016/7/18.
 * 用来显示动态的数据
 */
public class SquareListViewAdapter extends BaseListAdapter<Post> {


    //要传入的参数有当前的Activity，数据集。item的布局文件
    public SquareListViewAdapter(Context context, List<Post> list,int itemLayoutId){

        super(context,list,itemLayoutId);

    }

    //给控件绑定数据
    @Override
    public void convert(final BaseListHolder holder, final Post item) {

        RelativeLayout rL = holder.getView(R.id.rl4);
        NineGridTestLayout gv_images = holder.getView(R.id.gv_images);

        String name = null,time = null,content = null,headpath = null,contentImageUrl = null;
        Number zan = null;
        if(item !=null){
            name = item.getAuthor().getNick();
            time = item.getCreatedAt();
            content = item.getContent();
            headpath = item.getAuthor().getAvatar();
            contentImageUrl = item.getImageurl();
            zan =item.getZan();
        }
        //昵称
        if (name!=null&&!name.equals("")) {
            holder.setTextView(R.id.info_tv_name,name);
        }
        //是否含有图片，有图片则显示gridview
        if (contentImageUrl!=null&&!contentImageUrl.equals("")) {
            rL.setVisibility(View.VISIBLE);
            initInfoImages(gv_images,contentImageUrl);
        } else {
            rL.setVisibility(View.GONE);
        }
        //点赞的rem ,查询喜欢这个帖子的所有用户，因此查询的是用户表
        if (zan!=null&&zan.toString().trim().length()!=0) {
            holder.setTextView(  R.id.tv_likes_number, "(" +zan.intValue() + ")");
        }else {
            holder.setTextView(R.id.tv_likes_number,"");
        }
        showZan(holder,item);

        //发布时间
        if (time!=null&&!time.equals("")) {
            holder.setTextView(R.id.info_tv_time,time);
        }
        //内容
        if (content!=null&&!content.equals("")) {
            holder.setTextView(R.id.info_tv_content,content);
        }
        //头像
        if (headpath!=null&&!headpath.equals("")) {
            ImageLoader.getInstance().
                    displayImage(headpath,(ImageView)holder.getView(R.id.info_iv_head),
                    ImageLoadOptions.getOptions());
        } else {
            holder.setImageResource(R.id.info_iv_head,R.drawable.love);
        }

        BmobQuery<Comment> commentBmobQuery = new BmobQuery<Comment>();
        Post post = new Post();
        //用此方式可以构造一个BmobPointer对象。只需要设置objectId就行
        post.setObjectId(item.getObjectId());
        commentBmobQuery.addWhereEqualTo("post", new BmobPointer(post));
        commentBmobQuery.findObjects(mContext, new FindListener<Comment>() {
            @Override
            public void onSuccess(List<Comment> list) {
                holder.setTextView(R.id.comment_numbers,"("+list.size()+")");
            }
            @Override
            public void onError(int code, String msg) {

            }
        });

        holder.getView(R.id.item_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                User userInfo = item.getAuthor();
                bundle.putSerializable("u", userInfo);
                bundle.putSerializable("p", item);
                Intent intent = new Intent(mContext,ItemDetailsActivity.class);
                if (bundle != null)
                    intent.putExtra(mContext.getPackageName(), bundle);
                mContext.startActivity(intent);
            }
        });


        //点击头像的点击事件
        holder.getView(R.id.info_iv_head).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Bundle bundle = new Bundle();
                User userInfo = item.getAuthor();
                bundle.putSerializable("u", userInfo);
               Intent intent = new Intent(mContext,CheckUserInfoByUser.class);
                if (bundle != null)
                    intent.putExtra(mContext.getPackageName(), bundle);
                mContext.startActivity(intent);
            }
        });


        //点赞
        holder.getView(R.id.iv_share_heart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.getView(R.id.iv_share_heart).isEnabled()==false) {
                    Toast.makeText(mContext, "您已经点赞过啦", Toast.LENGTH_SHORT).show();
                }
                if (holder.getView(R.id.iv_share_heart).isEnabled()) {
                    User user = BmobUser.getCurrentUser(mContext, User.class);
                    Post post = new Post();
                    post.setObjectId(item.getObjectId());
                    //将当前用户添加到Post表中的likes字段值中，表明当前用户喜欢该帖子
                    BmobRelation relation = new BmobRelation();
                    //将当前用户添加到多对多关联中
                    relation.add(user);
                    //多对多关联指向`post`的`likes`字段
                    post.setLikes(relation);
                    post.increment("zan", 1); // 点赞的数量加1
                    post.update(mContext, new UpdateListener() {

                        @Override
                        public void onSuccess() {
                            Toast.makeText(mContext, "点赞成功", Toast.LENGTH_SHORT).show();
                            //跟新点赞人数
                            if (item.getZan()!=null) {
                                int zan =item.getZan().intValue()+1;
                                holder.setTextView(R.id.tv_likes_number, "(" + zan + ")");
                            }else {
                                holder.setTextView(R.id.tv_likes_number, "");
                            }
                        }

                        @Override
                        public void onFailure(int arg0, String arg1) {
                            // TODO Auto-generated method stub
                            Toast.makeText(mContext, "点赞失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                    holder.getView(R.id.iv_share_heart).setEnabled(false);
                }
            }
        });
        holder.getView(R.id.comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //弹出评论框
                AddCommentPopupWindow menuWindow = new AddCommentPopupWindow(mContext, item.getObjectId());
                Log.i("info","postId2:"+item.getObjectId());
                menuWindow.showAtLocation(holder.getConvertView(), Gravity.BOTTOM , 0, 0);

            }
        });
    }
//显示点赞的人
    private void showZan(final BaseListHolder holder, final Post item){
        BmobQuery<User> query = new BmobQuery<User>();
        Post post = new Post();
        post.setObjectId(item.getObjectId());
        //likes是Post表中的字段，用来存储所有喜欢该帖子的用户
        query.addWhereRelatedTo("likes", new BmobPointer(post));
        query.findObjects(mContext, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                String likesUser ="";
                for (int i= 0;i<list.size();i++){
                    likesUser +=list.get(i).getUsername()+",";

                }
                if (likesUser.length()!=0) {
                   // holder.setTextView(R.id.tv_likes_names,likesUser);
                }else {
                   // holder.setTextView(R.id.tv_likes_names,"");
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }


    //初始化图片集，设定GridView的列数
    public void initInfoImages(NineGridTestLayout gv_images,final String imgUrl){
        if(imgUrl!=null&&!imgUrl.equals("")) {
            String[] imgs = imgUrl.split("#");//多张图片的URL一#分开

            List<String> list = new ArrayList<>();//图片url
            for (int i = 0; i < imgs.length; i++) {
                list.add(imgs[i]);
            }
            gv_images.setIsShowAll(false); //当传入的图片数超过9张时，是否全部显示
            gv_images.setSpacing(5); //动态设置图片之间的间隔
            gv_images.setUrlList(list); //最后再设置图片url


        }

    }

}
