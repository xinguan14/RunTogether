package com.xinguan14.jdyp.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.base.ParentWithNaviActivity;
import com.xinguan14.jdyp.bean.Comments;
import com.xinguan14.jdyp.bean.User;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;

public class AddNewsActivity extends ParentWithNaviActivity  {

	@Bind(R.id.content)
	EditText content;
	@Bind(R.id.submit)
	Button submit;
	String userId;
	private  String userName;
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
		final BmobQuery<User> userNameQuery = new BmobQuery<User>();
		userNameQuery.getObject(this, userId, new GetListener<User>() {
			@Override
			public void onSuccess(User user) {
				 userName=user.getUsername();
			}

			@Override
			public void onFailure(int i, String s) {

			}
		});

		submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String contents = content.getText().toString();
				Comments comments = new Comments();

				comments.setContent(contents);
				comments.setUserId(userId);

				comments.setUserName(userName);
				comments.save(AddNewsActivity.this,new SaveListener() {
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
		});
	}

}
