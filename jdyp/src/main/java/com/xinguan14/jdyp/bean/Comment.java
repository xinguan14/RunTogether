package com.xinguan14.jdyp.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by wm on 2016/7/21.
 * 用户的评论记录
 */
public class Comment extends BmobObject {

    private String content;//评论内容

    private User user;//评论的用户，Pointer类型，一对一关系

    private Post post; //所评论的动态，这里体现的是一对多的关系，一个评论只能属于一个动态

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
