package com.xinguan14.jdyp.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by wm on 2016/7/14.
 */
public class Comments extends BmobObject {
    private String content;
    //private User user;
    private String userId;

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
   /* public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }*/
   public String getUserId() {
       return userId;
   }
    public void setUserId(String userId) {
        this.userId = userId;
    }

}
