package com.xinguan14.jdyp.ui.fragment.sportsfragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xinguan14.jdyp.MyVeiw.MyGridView;
import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.adapter.GridViewAdapter;
import com.xinguan14.jdyp.adapter.base.BaseListAdapter;
import com.xinguan14.jdyp.adapter.base.BaseListHolder;
import com.xinguan14.jdyp.bean.Friend;
import com.xinguan14.jdyp.bean.Post;
import com.xinguan14.jdyp.bean.User;
import com.xinguan14.jdyp.ui.AddCommentPopupWindow;
import com.xinguan14.jdyp.util.SysUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @描述 在Fragment中要使用ListView，必须要用ListFragment
 */
public class SayFragment extends android.support.v4.app.ListFragment {

//    @Bind(R.id.progress_load)
//    ProgressBar progress_load;

    @Bind(R.id.swipe_container)
    SwipeRefreshLayout sps_refresh;

    private View rootView;
    //存放动态的集合
    private List<Post> mPostList;
    //存放评论的集合
    private View mCommentView;
    //适配器
    private SayListViewAdapter mSayListViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sport_main, container, false);
        ButterKnife.bind(this, rootView);
        //mPostList = new ArrayList<Post>();
        //查询数据并绑定数据
        friendQuery();
        //刷新监听
        sps_refresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        sps_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getActivity(), "刷新了",
                        Toast.LENGTH_SHORT).show();
                friendQuery();
            }
        });
        return rootView;
    }


    //获的当前用户朋友的数据
    private void friendQuery() {
        //当前用户的id
        //String currentUId = getCurrentUid();

        //获取当前的用户
        User user = BmobUser.getCurrentUser(getActivity(), User.class);
        BmobQuery<Friend> userFriends = new BmobQuery<Friend>();
        userFriends.addWhereEqualTo("user", user);//查询当前用户的朋友
        userFriends.findObjects(getActivity(), new FindListener<Friend>() {
            @Override
            public void onSuccess(List<Friend> list) {
                int length = list.size();
                User[] userId = new User[length];
                for (int i = 0; i < length; i++) {

                    userId[i] = list.get(i).getFriendUser();
                }
                userQuery(userId);
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(getActivity(), "未查询到数据",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    //获得运动圈要显示的用户动态的数据,包括用户本人和朋友
    private void userQuery(User[] userId) {
        //添加当前用户
        User[] showUser = new User[userId.length + 1];
        showUser[userId.length] = BmobUser.getCurrentUser(getActivity(), User.class);
        for (int i = 0; i < userId.length; i++) {
            showUser[i] = userId[i];
        }
        //获取显示用户的Id
        String[] showUserId = new String[showUser.length];
        for (int i = 0; i < showUserId.length; i++) {
            showUserId[i] = showUser[i].getObjectId();
        }

	/*	String str = "要显示有"+showUserId.length+"个人";
                Toast.makeText(getActivity(), str,
						Toast.LENGTH_SHORT).show();*/
        //查询动态信息
        final BmobQuery<Post> query = new BmobQuery<Post>();
        query.order("-createdAt");// 按照时间降序
        //查询要显示的用户的所有动态
        query.addWhereContainedIn("author", Arrays.asList(showUserId));
        //查询发动态的用户信息
        query.include("author");

        query.findObjects(getActivity(), new FindListener<Post>() {
            @Override
            public void onSuccess(List<Post> list) {
                if (list != null) {
                    initData(list);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("未查询到数据");
                    builder.setTitle("提示");
                    builder.create().show();
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(getActivity(), "网络未连接",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    //绑定数据
    private void initData(List<Post> list) {
        mSayListViewAdapter = new SayListViewAdapter(getActivity(), list, R.layout.fragment_sport_say_item);
        setListAdapter(mSayListViewAdapter);
        //关闭刷新
        sps_refresh.setRefreshing(false);
    }

    //动态的适配器
    public class SayListViewAdapter extends BaseListAdapter<Post> implements View.OnClickListener {

        private GridViewAdapter gridViewAdapter;
        //弹出更多
        private PopupWindow mMorePopupWindow;
        private int mShowMorePopupWindowWidth;
        private int mShowMorePopupWindowHeight;
        //弹出评论框
        private AddCommentPopupWindow menuWindow;

        private String postId;
        private BaseListHolder holder;
        private int wh;


        public SayListViewAdapter(Activity context, List<Post> list, int itemLayoutId){
            super(context,list,itemLayoutId);
            //根据屏幕的大小设置控件的大小
            this.wh=(SysUtils.getScreenWidth(context)- SysUtils.Dp2Px(context, 99))/3;

        }

        @Override
        public void convert(final BaseListHolder holder, Post item) {
            //图片布局
            RelativeLayout rL = holder.getView(R.id.rl4);
            MyGridView gv_images = holder.getView(R.id.gv_images);
            postId = item .getObjectId();
            this.holder=holder;
            String name = null,time = null,content = null,headpath = null,contentImageUrl = null;
            if(item !=null){

                name = item.getAuthor().getUsername();
                time = item.getUpdatedAt();
                content = item.getContent();
                headpath = item.getAuthor().getAvatar();
                contentImageUrl = item.getImageurl();
            }
            //昵称
            if (name!=null&&!name.equals("")) {
                holder.setTextView(R.id.user_name,name);
            }
            //是否含有图片，有图片则显示gridview
            if (contentImageUrl!=null&&!contentImageUrl.equals("")) {
                rL.setVisibility(View.VISIBLE);
                initInfoImages(gv_images,contentImageUrl);
            } else {
                rL.setVisibility(View.GONE);
            }
            //点赞的rem ,查询喜欢这个帖子的所有用户，因此查询的是用户表
            BmobQuery<User> query = new BmobQuery<User>();
            Post post = new Post();
            post.setObjectId(postId);
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
                         holder.setTextView(R.id.tv_likes_names,likesUser);
                    }
                    Log.i("info","点赞用户："+likesUser);
                }

                @Override
                public void onError(int i, String s) {

                }
            });
            //显示发布时间
            if (time!=null&&!time.equals("")) {

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    String str = df.format(curDate);
                    Date d1 = df.parse(str);
                    Date d2 = df.parse(item.getUpdatedAt());
                    long diff = d1.getTime() - d2.getTime();//这样得到的差值是微秒级别
                    long days = diff / (1000 * 60 * 60 * 24);
                    long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
                    long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
                    if (days > 0) {
                        holder.setTextView(R.id.add_say_time, days+"天前");

                    } else if (hours > 0) {
                        holder.setTextView(R.id.add_say_time, hours+"小时前");

                    } else if (minutes > 0) {
                        holder.setTextView(R.id.add_say_time, minutes+"分钟前");
                    }
                    else {
                        holder.setTextView(R.id.add_say_time, "刚刚");
                    }
                } catch (Exception e) {
                    //时间
                    holder.setTextView(R.id.add_say_time, "未知" );
                }
            }
            //内容
            if (content!=null&&!content.equals("")) {
                holder.setTextView(R.id.content,content);
            }
            //头像
            if (headpath!=null&&!headpath.equals("")) {
                Glide
                        .with(mContext)
                        .load(headpath)
                        .placeholder(R.drawable.love)
                        .into((ImageView)holder.getView(R.id.user_image));
            } else {
                holder.setImageResource(R.id.user_image,R.drawable.love);
            }
            //点击头像的点击事件
            holder.getView(R.id.user_image).setOnClickListener(this);
            //弹出评论和点赞的按钮
            holder.getView(R.id.more_img).setOnClickListener(this);
        }



        @Override
        public void onClick(View v) {
            switch (v.getId()){
                //如果点击头像
                case  R.id.user_image:
                    Toast.makeText(mContext, "点击了头像", Toast.LENGTH_LONG).show();
                    break;
                //如果点击了评论和点赞图片
                case R.id.more_img:
                    showMore(v);
                    break;
                //点击了评论
                case R.id.comment_img:
                    //弹出评论框
                    menuWindow = new AddCommentPopupWindow(mContext, postId);
                    menuWindow.showAtLocation(holder.getConvertView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    if (mMorePopupWindow != null && mMorePopupWindow.isShowing()) {
                        mMorePopupWindow.dismiss();
                    }

                    break;
                //点击了点赞
                case R.id.good_img:
                    User user = BmobUser.getCurrentUser(mContext, User.class);
                    Post post = new Post();
                    post.setObjectId(postId);
                    //将当前用户添加到Post表中的likes字段值中，表明当前用户喜欢该帖子
                    BmobRelation relation = new BmobRelation();
                    //将当前用户添加到多对多关联中
                    relation.add(user);
                    //多对多关联指向`post`的`likes`字段
                    post.setLikes(relation);
                    post.update(mContext, new UpdateListener() {

                        @Override
                        public void onSuccess() {
                            // TODO Auto-generated method stub
                            Toast.makeText(mContext, "点赞成功", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int arg0, String arg1) {
                            // TODO Auto-generated method stub
                            Toast.makeText(mContext, "点赞失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                    if (mMorePopupWindow != null && mMorePopupWindow.isShowing()) {
                        mMorePopupWindow.dismiss();
                    }
                    break;

            }
        }

        /**
         * 弹出点赞和评论框
         *
         * @param moreBtnView
         */
        private void showMore(View moreBtnView) {
            if (mMorePopupWindow == null) {

                LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View content = li.inflate(R.layout.add_comment_good_layout, null, false);

                mMorePopupWindow = new PopupWindow(content, ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                mMorePopupWindow.setBackgroundDrawable(new BitmapDrawable());
                mMorePopupWindow.setOutsideTouchable(true);
                mMorePopupWindow.setTouchable(true);

                content.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                mShowMorePopupWindowWidth = content.getMeasuredWidth();
                mShowMorePopupWindowHeight = content.getMeasuredHeight();

                View parent = mMorePopupWindow.getContentView();

                ImageView good = (ImageView) parent.findViewById(R.id.good_img);
                ImageView comment = (ImageView) parent.findViewById(R.id.comment_img);

                // 点赞的监听器
                comment.setOnClickListener(this);
                good.setOnClickListener(this);
            }

            if (mMorePopupWindow.isShowing()) {
                mMorePopupWindow.dismiss();
            } else {
                int heightMoreBtnView = moreBtnView.getHeight();

                mMorePopupWindow.showAsDropDown(moreBtnView, -mShowMorePopupWindowWidth,
                        -(mShowMorePopupWindowHeight + heightMoreBtnView) / 2);
            }
        }


        //初始化图片集，设定GridView的列数
        public void initInfoImages(MyGridView gv_images,final String imgUrl){
            if(imgUrl!=null&&!imgUrl.equals("")){
                String[] imgs=imgUrl.split("#");//多张图片的URL一#分开
                ArrayList<String> list=new ArrayList<String>();
                for(int i=0;i<imgs.length;i++){
                    list.add(imgs[i]);
                }
                int w=0;
                switch (imgs.length) {
                    case 1:
                        w=SysUtils.getScreenWidth(mContext)- SysUtils.Dp2Px(mContext, 99);
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
                //传递的数据是当前的Activity，显示图片的集合。显示图片的布局
                gridViewAdapter=new GridViewAdapter(mContext, list,R.layout.fragment_sport_square_item_grid);
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
}
