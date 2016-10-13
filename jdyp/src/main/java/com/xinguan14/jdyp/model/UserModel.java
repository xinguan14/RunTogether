package com.xinguan14.jdyp.model;

import com.orhanobut.logger.Logger;
import com.xinguan14.jdyp.bean.Friend;
import com.xinguan14.jdyp.bean.User;
import com.xinguan14.jdyp.model.i.QueryUserListener;
import com.xinguan14.jdyp.model.i.UpdateCacheListener;

import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * @author :smile
 * @project:UserModel
 * @date :2016-01-22-18:09
 */
public class UserModel extends BaseModel {

    private static UserModel ourInstance = new UserModel();

    public static UserModel getInstance() {
        return ourInstance;
    }

    private UserModel() {
    }

    /**
     * 登录
     *
     * @param username
     * @param password
     * @param listener
     */
    public void login(String username, String password, final LogInListener listener) {
//        if (TextUtils.isEmpty(username)) {
//            listener.internalDone(new BmobException(CODE_NULL, "请填写用户名"));
//            return;
//        }
//        if (TextUtils.isEmpty(password)) {
//            listener.internalDone(new BmobException(CODE_NULL, "请填写密码"));
//            return;
//        }
        final User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.login(getContext(), new SaveListener() {
            @Override
            public void onSuccess() {
                listener.done(getCurrentUser(), null);
            }

            @Override
            public void onFailure(int i, String s) {
                listener.done(user, new BmobException(i, s));
            }
        });
    }

    /**
     * 退出登录
     */
    public void logout() {
        BmobUser.logOut(getContext());
    }

    public User getCurrentUser() {
        return BmobUser.getCurrentUser(getContext(), User.class);
    }

    /**
     * 注册时调用
     * @param phone_number  手机号
     * @param password      密码
     * @param security_code 短信验证码
     * @param nick_name     昵称
     * @param listener      回调
     */
    public void register(String phone_number, String password, String security_code, String nick_name, final LogInListener listener) {

        final User user = new User();
        user.setUsername(phone_number);
        user.setPassword(password);
        user.setNick(nick_name);
        user.setMobilePhoneNumber(phone_number);
        user.setSex(true);
        user.setAge(18);
        user.signOrLogin(getContext(), security_code, new SaveListener() {
            @Override
            public void onSuccess() {
                listener.done(null, null);
            }

            @Override
            public void onFailure(int i, String s) {
                listener.done(null, new BmobException(i, s));
            }
        });
    }
//    /**
//     * 修改密码时调用
//     * @param phone_number  手机号
//     * @param password      密码
//     * @param security_code 短信验证码
//     * @param listener      回调
//     */
//    public void register(String phone_number, String password, String security_code, final LogInListener listener) {
//
//        final User user = new User();
//        user.setPassword(password);
//        user.signOrLogin(getContext(), security_code, new SaveListener() {
//            @Override
//            public void onSuccess() {
//                listener.done(null, null);
//            }
//
//            @Override
//            public void onFailure(int i, String s) {
//                listener.done(null, new BmobException(i, s));
//            }
//        });
//    }

    /**
     * 查询用户
     *
     * @param username
     * @param limit
     * @param listener
     */
    public void queryUsers(String username, int limit, final FindListener<User> listener) {
        BmobQuery<User> query = new BmobQuery<>();
        //去掉当前用户
        try {
            BmobUser user = BmobUser.getCurrentUser(getContext());
            query.addWhereNotEqualTo("username", user.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
        }
        query.addWhereContains("username", username);
        query.setLimit(limit);
        query.order("-createdAt");
        query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.findObjects(getContext(), new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                if (list != null && list.size() > 0) {
                    listener.onSuccess(list);
                } else {
                    listener.onError(CODE_NULL, "查无此人");
                }
            }

            @Override
            public void onError(int i, String s) {
                listener.onError(i, s);
            }
        });
    }

    /**
     * 查询用户信息
     *
     * @param objectId
     * @param listener
     */
    public void queryUserInfo(String objectId, final QueryUserListener listener) {
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("objectId", objectId);
        query.findObjects(getContext(), new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                if (list != null && list.size() > 0) {
                    listener.internalDone(list.get(0), null);
                } else {
                    listener.internalDone(new BmobException(000, "查无此人"));
                }
            }

            @Override
            public void onError(int i, String s) {
                listener.internalDone(new BmobException(i, s));
            }
        });
    }


    /**
     * 更新用户资料和会话资料
     *
     * @param event
     * @param listener
     */
    public void updateUserInfo(MessageEvent event, final UpdateCacheListener listener) {
        final BmobIMConversation conversation = event.getConversation();
        final BmobIMUserInfo info = event.getFromUserInfo();
        final BmobIMMessage msg = event.getMessage();
        String username = info.getName();
        String title = conversation.getConversationTitle();
        Logger.i("" + username + "," + title);
        //sdk内部，将新会话的会话标题用objectId表示，因此需要比对用户名和会话标题--单聊，后续会根据会话类型进行判断
        if (!username.equals(title)) {
            UserModel.getInstance().queryUserInfo(info.getUserId(), new QueryUserListener() {
                @Override
                public void done(User s, BmobException e) {
                    if (e == null) {
                        String name = s.getUsername();
                        String avatar = s.getAvatar();
                        Logger.i("query success：" + name + "," + avatar);
                        conversation.setConversationIcon(avatar);
                        conversation.setConversationTitle(name);
                        info.setName(name);
                        info.setAvatar(avatar);
                        //更新用户资料
                        BmobIM.getInstance().updateUserInfo(info);
                        //更新会话资料-如果消息是暂态消息，则不更新会话资料
                        if (!msg.isTransient()) {
                            BmobIM.getInstance().updateConversation(conversation);
                        }
                    } else {
                        Logger.e(e);
                    }
                    listener.done(null);
                }
            });
        } else {
            listener.internalDone(null);
        }
    }

    /**
     * 同意添加好友：1、发送同意添加的请求，2、添加对方到自己的好友列表中
     */
    public void agreeAddFriend(User friend, SaveListener listener) {
        Friend f = new Friend();
        User user = BmobUser.getCurrentUser(getContext(), User.class);
        f.setUser(user);
        f.setFriendUser(friend);
        f.save(getContext(), listener);
    }

    /**
     * 查询好友
     *
     * @param listener
     */
    public void queryFriends(final FindListener<Friend> listener) {
        BmobQuery<Friend> query = new BmobQuery<>();
        User user = BmobUser.getCurrentUser(getContext(), User.class);
        query.addWhereEqualTo("user", user);
        query.include("friendUser");
        query.order("-updatedAt");
        query.findObjects(getContext(), new FindListener<Friend>() {
            @Override
            public void onSuccess(List<Friend> list) {
                if (list != null && list.size() > 0) {
                    listener.onSuccess(list);
                } else {
                    listener.onError(0, "暂无联系人");
                }
            }

            @Override
            public void onError(int i, String s) {
                listener.onError(i, s);
            }
        });
    }

    /**
     * 删除好友
     *
     * @param f
     * @param listener
     */
    public void deleteFriend(Friend f, DeleteListener listener) {
        Friend friend = new Friend();
        friend.delete(getContext(), f.getObjectId(), listener);
    }
}
