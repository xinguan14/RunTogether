package com.xinguan14.jdyp.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by wm on 2016/7/14.
 * 用户动态记录
 */
public class Post extends BmobObject  {
    private String content;//动态内容
    private String name;//发布者的名字
    private String imageurl;//包含的图片
    private String headPhoto;//用户头像


    private BmobFile image;//动态图片
    private BmobRelation likes;//多对多关系：用于存储喜欢该帖子的所有用户
    private User author;//动态的发布者，这里体现的是一对一的关系，该帖子属于某个用户



    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public BmobFile getImage() {
        return image;
    }

    public void setImage(BmobFile image) {
        this.image = image;
    }

    public BmobRelation getLikes() {
        return likes;
    }

    public void setLikes(BmobRelation likes) {
        this.likes = likes;
    }

    public String getHeadPhoto() {
        return headPhoto;
    }

    public void setHeadPhoto(String headPhoto) {
        this.headPhoto = headPhoto;
    }
}
