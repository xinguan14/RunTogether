package com.xinguan14.jdyp.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by wm on 2016/7/14.
 */
public class Comments extends BmobObject {
    private String content;
    private BmobFile image;
    private String userId;
    private String userName;

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getUserName(){
      return userName;
   }
    public void setUserName(String userName){
        this.userName=userName;
    }
    public String getUserId() {
       return userId;
   }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public BmobFile getImage() {
        return image;
    }
    public void setFile(BmobFile image) {
        this.image = image;
    }

}
