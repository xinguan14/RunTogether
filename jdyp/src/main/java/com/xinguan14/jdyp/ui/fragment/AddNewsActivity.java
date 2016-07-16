package com.xinguan14.jdyp.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.base.ParentWithNaviActivity;
import com.xinguan14.jdyp.bean.Comments;
import com.xinguan14.jdyp.bean.User;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.listener.SaveListener;

public class AddNewsActivity extends ParentWithNaviActivity  {

	@Bind(R.id.content)
	EditText content;
	String userId;
	User user;
	BmobIMUserInfo info;

	@Override
	protected String title() {
		return "发布动态";
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_news);
		initNaviView();
		userId = getCurrentUid();
		
	}


	@OnClick(R.id.submit)
	public void submitNewsClick(View view){

		String contents = content.getText().toString();
		Comments comments = new Comments();

		comments.setContent(contents);
		comments.setUserId(userId);
		comments.save(this,new SaveListener() {
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "发布成功!", Toast.LENGTH_SHORT).show();
				setResult(RESULT_OK);
				finish();
			}

			@Override
			public void onFailure(int code, String arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "发布失败!", Toast.LENGTH_SHORT).show();
			}
		});
	}

}
