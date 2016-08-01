package com.xinguan14.jdyp.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.adapter.base.BaseViewHolder;
import com.xinguan14.jdyp.base.ImageLoaderFactory;
import com.xinguan14.jdyp.bean.User;
import com.xinguan14.jdyp.ui.CheckUserInfo;

import butterknife.Bind;

public class SearchUserHolder extends BaseViewHolder {

  @Bind(R.id.avatar)
  public ImageView avatar;
  @Bind(R.id.name)
  public TextView name;
  @Bind(R.id.btn_add)
  public Button btn_add;

  public SearchUserHolder(Context context, ViewGroup root,OnRecyclerViewListener onRecyclerViewListener) {
    super(context, root, R.layout.item_search_user,onRecyclerViewListener);
  }

  @Override
  public void bindData(Object o) {
    final User user =(User)o;
    ImageLoaderFactory.getLoader().loadAvator(avatar,user.getAvatar(), R.mipmap.head);
    name.setText(user.getUsername());
    btn_add.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {//查看个人详情
          Bundle bundle = new Bundle();
          bundle.putSerializable("u", user);
          startActivity(CheckUserInfo.class,bundle);
        }
    });
  }
}