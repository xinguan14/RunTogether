package com.xinguan14.jdyp.ui.fragment.sportsfragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.base.ParentWithNaviActivity;
import com.xinguan14.jdyp.bean.Comment;
import com.xinguan14.jdyp.bean.Post;
import com.xinguan14.jdyp.bean.User;
import com.xinguan14.jdyp.myVeiw.NineGridTestLayout;
import com.xinguan14.jdyp.ui.ChatActivity;
import com.xinguan14.jdyp.ui.CheckUserInfoByUser;
import com.xinguan14.jdyp.util.ImageLoadOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by wm on 2016/9/10.
 */
public class ItemDetailsActivity extends ParentWithNaviActivity implements View.OnClickListener {

    @Bind(R.id.user_logo)
    ImageView userImage;
    @Bind(R.id.user_name)
    TextView userName;
    @Bind(R.id.item_public_time)
    TextView time;
    @Bind(R.id.content_text)
    TextView content;
    @Bind(R.id.content_image)
    NineGridTestLayout content_image;
    @Bind(R.id.item_action_chat)
    TextView chat;
    @Bind(R.id.item_action_love)
    TextView likes;
    @Bind(R.id.item_action_comment)
    TextView comment;
    @Bind(R.id.comment_list)
    LinearLayout commentList;
    @Bind(R.id.loadmore)
    TextView loadmore;
    @Bind(R.id.comment_content)
    EditText commentEdit;
    @Bind(R.id.comment_commit)
    Button commentCommit;
    @Bind(R.id.area_commit)
     LinearLayout area_commit;



    User user;
    Post post;
    String comments;
    int goodState;
    private int pageNum;
    BmobIMUserInfo info;
    public static final int NUMBERS_PER_PAGE = 15;// 每次请求返回评论条数

    @Override
    protected String title() {
        return "动态信息";
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detail);
        initNaviView();
        //接收传递的user和post信息
        user = (User) getBundle().getSerializable("u");
        post=(Post)getBundle().getSerializable("p");
        goodState =getBundle().getInt("zan");
        //构造聊天方的用户信息:传入用户id、用户名和用户头像三个参数
        info = new BmobIMUserInfo(user.getObjectId(), user.getUsername(), user.getAvatar());
        dataBind();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
    //给控件绑定数据
    public void dataBind(){
        //如果头像不为空则使用bmob的头像，为空则使用默认的头像
        if (user.getAvatar()!=null) {
            ImageLoader.getInstance()
                    .displayImage(user.getAvatar(), userImage, ImageLoadOptions.getOptions());
        }else {
            userImage.setImageResource(R.drawable.love);
        }
        userName.setText(user.getNick());//用户名
        time.setText(post.getCreatedAt());//发布动态的时间
        content.setText(post.getContent());//动态内容
        if (post.getZan()!=null) {
            likes.setText(post.getZan().intValue() + "赞");//点赞人数
        }

        //动态图片加载
        if(post.getImageurl()!=null&&!post.getImageurl().equals("")) {
            String[] imgs = post.getImageurl().split("#");//多张图片的URL一#分开

            List<String> list = new ArrayList<>();//图片url
            for (int i = 0; i < imgs.length; i++) {
                list.add(imgs[i]);
            }
            content_image.setIsShowAll(false); //当传入的图片数超过9张时，是否全部显示
            content_image.setSpacing(5); //动态设置图片之间的间隔
            content_image.setUrlList(list); //最后再设置图片url
        }
        //显示评论
        showComments(post);

        //给按钮设置监听事件
        userImage.setOnClickListener(this);
        chat.setOnClickListener(this);
        likes.setOnClickListener(this);
        comment.setOnClickListener(this);
        loadmore.setOnClickListener(this);
        commentCommit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //跳转到个人信息界面
            case R.id.user_logo:
                Bundle bundle = new Bundle();
                bundle.putSerializable("u", user);
                Intent intent = new Intent(this,CheckUserInfoByUser.class);
                if (bundle != null)
                    intent.putExtra(this.getPackageName(), bundle);
                this.startActivity(intent);
                break;
            case R.id.item_action_chat:
                //聊天
                onChatClick(view);
                break;
            case R.id.item_action_love:
                //点赞
                setGoodState(post);
                break;
            case R.id.item_action_comment:
                //评论
                area_commit.setVisibility(View.VISIBLE);
                break;
            case R.id.loadmore:
                //加载更多
                showComments(post);
                break;
            case R.id.comment_commit:
                //提交评论
                submitComment(post);
                break;
        }
    }

    private void showComments(Post item){
        BmobQuery<Comment> commentBmobQuery = new BmobQuery<Comment>();
        Post post = new Post();
        //用此方式可以构造一个BmobPointer对象。只需要设置objectId就行
        post.setObjectId(item.getObjectId());
        commentBmobQuery.addWhereEqualTo("post",new BmobPointer(post));
        //希望同时查询该评论的发布者的信息，以及该帖子的作者的信息，这里用到上面`include`的并列对象查询和内嵌对象的查询
        commentBmobQuery.include("user,post.author");

        commentBmobQuery.setLimit(NUMBERS_PER_PAGE);
        commentBmobQuery.setSkip(NUMBERS_PER_PAGE * (pageNum++));

        commentBmobQuery.findObjects(this, new FindListener<Comment>() {
            @Override
            public void onSuccess(List<Comment> list) {
                comment.setText(list.size()+"评论");
                if (list.size()!=0 ) {
                    if (list.size() < NUMBERS_PER_PAGE) {
                        loadmore.setText("暂无更多评论~");
                        loadmore.setEnabled(false);
                    }

                    commentList.removeAllViews();
                    commentList.setVisibility(View.VISIBLE);

                    for (int i= 0;i<list.size();i++) {
                        TextView t = new TextView(ItemDetailsActivity.this);
                        t.setLayoutParams(new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)));
                        t.setTextSize(16);
                        t.setPadding(5, 2, 0, 3);
                        t.setLineSpacing(3, (float) 1.5);
                        t.setText(Html.fromHtml("<font color='#4A766E'>"+list.get(i).getUser().getNick()+"</font>:"+list.get(i).getContent()));
                        commentList.addView(t);
                    }

                } else {
                    commentList.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(int code, String msg) {


            }
        });
    }

    //启动会话
    public void onChatClick(View view) {
        //启动一个会话，设置isTransient设置为false,则会在本地数据库的会话列表中先创建（如果没有）与该用户的会话信息，且将用户信息存储到本地的用户表中
        BmobIMConversation c = BmobIM.getInstance().startPrivateConversation(info, false, null);
        Bundle bundle = new Bundle();
        bundle.putSerializable("c", c);
        startActivity(ChatActivity.class, bundle, false);
    }

    public void submitComment(final Post mPost){
        comments=commentEdit.getText().toString();
        if (comments.length()==0){
            Toast.makeText(this, "评论不能为空", Toast.LENGTH_SHORT).show();
        }else {
            User user = BmobUser.getCurrentUser(this, User.class);
            Post post = new Post();
            post.setObjectId(mPost.getObjectId());
            Log.i("info","postId1:"+mPost.getObjectId());
            final Comment comment = new Comment();
            comment.setContent(comments);
            comment.setPost(post);
            comment.setUser(user);
            comment.save(this, new SaveListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(ItemDetailsActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    commentList.setVisibility(View.VISIBLE);
                    TextView t = new TextView(ItemDetailsActivity.this);
                    t.setLayoutParams(new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)));
                    t.setTextSize(16);
                    t.setPadding(5, 2, 0, 3);
                    t.setLineSpacing(3, (float) 1.5);
                    t.setText(Html.fromHtml("<font color='#4A766E'>"
                            +BmobUser.getCurrentUser(ItemDetailsActivity.this, User.class).getNick()
                            +"</font>:"+comments));
                    commentList.addView(t);
                    area_commit.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(int code, String msg) {
                    Toast.makeText(ItemDetailsActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void setGoodState(final Post mPost){
        if(goodState==1) {
            Toast.makeText(this, "您已经点赞过啦", Toast.LENGTH_SHORT).show();
        }
        if (goodState==0) {
            User user = BmobUser.getCurrentUser(this, User.class);
            Post post = new Post();
            post.setObjectId(mPost.getObjectId());
            //将当前用户添加到Post表中的likes字段值中，表明当前用户喜欢该帖子
            BmobRelation relation = new BmobRelation();
            //将当前用户添加到多对多关联中
            relation.add(user);
            //多对多关联指向`post`的`likes`字段
            post.setLikes(relation);
            post.increment("zan", 1); // 点赞的数量加1
            post.update(this, new UpdateListener() {

                @Override
                public void onSuccess() {
                    //跟新点赞人数
                    likes.setText( mPost.getZan().intValue()+"赞");
                    Toast.makeText(ItemDetailsActivity.this, "点赞成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int arg0, String arg1) {
                    Toast.makeText(ItemDetailsActivity.this, "点赞失败", Toast.LENGTH_SHORT).show();
                }
            });
            goodState=1;
        }
    }

}
