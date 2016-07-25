package com.xinguan14.jdyp.bean;

import com.xinguan14.jdyp.db.NewFriend;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * @author :smile
 * @project:User
 * @date :2016-01-22-18:11
 */
public class User extends BmobUser {

    private String avatar;
    private Boolean sex;//性别-true-男
    private BmobGeoPoint location;//地理坐标
    //private String userName;

    public User() {}

    public User(NewFriend friend) {
        setObjectId(friend.getUid());
        setUsername(friend.getName());
        setAvatar(friend.getAvatar());
    }

    public Boolean getSex() { return sex; }
    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) {this.avatar = avatar;}

    public BmobGeoPoint getLocation() {
        return location;
    }
    public void setLocation(BmobGeoPoint location) {
        this.location = location;
    }




    /*public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }*/


}