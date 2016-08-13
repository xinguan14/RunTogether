package com.xinguan14.jdyp.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.base.ParentWithNaviActivity;
import com.xinguan14.jdyp.bean.AddFriendMessage;
import com.xinguan14.jdyp.bean.Friend;
import com.xinguan14.jdyp.bean.User;
import com.xinguan14.jdyp.util.ImageLoadOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by wm on 2016/7/31.
 */
public class CheckUserInfoByUser extends ParentWithNaviActivity implements View.OnClickListener{

    @Bind(R.id.img_my_avatar)
    ImageView image_my_avatar;
    @Bind(R.id.tv_my_userName)
    TextView userName;
    @Bind(R.id.tv_my_nikeName)
    TextView nikeName;
    @Bind(R.id.tv_my_sex)
    TextView userSex;
    @Bind(R.id.tv_my_userEmail)
    TextView userEmail;
    @Bind(R.id.tv_my_phone)
    TextView userPhone;
    @Bind(R.id.rl_addFriends)
    RelativeLayout addFriends;
    @Bind(R.id.rl_talk)
    RelativeLayout talk;

    User user;
    BmobIMUserInfo info;

    @Override
    protected String title() {
        return "详细资料";
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_info_layout);
        initNaviView();
        //接收传递的user信息
        user = (User) getBundle().getSerializable("u");
        //如果是自己的朋友则不显示显示添加好友的按钮
        if (user.getObjectId().equals(getCurrentUid())) {
            addFriends.setVisibility(View.GONE);
        }else {
            //如果不是自己就判断是不是好友
            friendQuery();
        }

        //构造聊天方的用户信息:传入用户id、用户名和用户头像三个参数
        info = new BmobIMUserInfo(user.getObjectId(), user.getUsername(), user.getAvatar());
        dataBind();
    }
    //给控件绑定数据
    public void dataBind(){
       //如果头像不为空则使用bmob的头像，为空则使用默认的头像
        if (user.getAvatar()!=null) {
            ImageLoader.getInstance()
                    .displayImage(user.getAvatar(), image_my_avatar, ImageLoadOptions.getOptions());
        }else {
            image_my_avatar.setImageResource(R.drawable.love);
        }
        userName.setText(user.getUsername());
        nikeName.setText(user.getNick());
        if (user.getSex()) {
            userSex.setText("男");
        } else {
            userSex.setText("女");
        }
        userEmail.setText(user.getEmail());
        userPhone.setText(user.getMobilePhoneNumber());
        //给按钮设置监听事件
        addFriends.setOnClickListener(this);
        talk.setOnClickListener(this);
        userName.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_addFriends:
                //添加好友
                sendAddFriendMessage();
                break;
            case R.id.rl_talk:
                //发起聊天
                onChatClick(view);
                break;
            case R.id.tv_my_userName:
                //呼叫手机号
                break;
        }

    }
    /**
     * 发送添加好友的请求
     */
    private void sendAddFriendMessage() {
        //启动一个会话，如果isTransient设置为true,则不会创建在本地会话表中创建记录，
        //设置isTransient设置为false,则会在本地数据库的会话列表中先创建（如果没有）与该用户的会话信息，且将用户信息存储到本地的用户表中
        BmobIMConversation c = BmobIM.getInstance().startPrivateConversation(info, true, null);
        //这个obtain方法才是真正创建一个管理消息发送的会话
        BmobIMConversation conversation = BmobIMConversation.obtain(BmobIMClient.getInstance(), c);
        AddFriendMessage msg = new AddFriendMessage();
        User currentUser = BmobUser.getCurrentUser(this, User.class);
        msg.setContent("很高兴认识你，可以加个好友吗?");//给对方的一个留言信息
        Map<String, Object> map = new HashMap<>();
        map.put("name", currentUser.getUsername());//发送者姓名，这里只是举个例子，其实可以不需要传发送者的信息过去
        map.put("avatar", currentUser.getAvatar());//发送者的头像
        map.put("uid", currentUser.getObjectId());//发送者的uid
        msg.setExtraMap(map);
        conversation.sendMessage(msg, new MessageSendListener() {
            @Override
            public void done(BmobIMMessage msg, BmobException e) {
                if (e == null) {//发送成功
                    toast("好友请求发送成功，等待验证");
                } else {//发送失败
                    toast("发送失败:" + e.getMessage());
                }
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

    //获的当前用户朋友的数据
    private void friendQuery() {

        //需要显示信息的用户
        String userId = ((User) getBundle().getSerializable("u")).getObjectId();
        BmobQuery<Friend> query1 = new BmobQuery<Friend>();
        query1.addWhereEqualTo("user", getCurrentUid());//当前的用户
        query1.addWhereEqualTo("friendUser", userId);//查询当前显示的用户
        query1.findObjects(this, new FindListener<Friend>() {
            @Override
            public void onSuccess(List<Friend> list) {
                if (list.size() == 0) {
                    addFriends.setVisibility(View.VISIBLE);
                    //Log.i("info","不是好友"+list.size());

                } else {
                    addFriends.setVisibility(View.GONE);
                    //Log.i("info","是好友"+list.size());
                }
            }
            @Override
            public void onError(int i, String s) {
               // Log.i("info","出错了");
            }
        });
    }
}
