package com.xinguan14.jdyp.ui.fragment;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.base.ParentWithNaviActivity;
import com.xinguan14.jdyp.bean.Comments;
import com.xinguan14.jdyp.bean.User;
import com.xinguan14.jdyp.ui.fragment.sportsfragment.SelectPicPopupWindow;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class AddNewsActivity extends ParentWithNaviActivity {

	@Bind(R.id.content)
	EditText content;
	@Bind(R.id.submit)
	Button submit;
	@Bind(R.id.add_pic)
	ImageView add_pic;

	String userId;
	private String userName;
	private BmobFile image;

	private Context mContext;
	private SelectPicPopupWindow menuWindow; // 上传图片弹出框
	/**
	 * 使用照相机拍照获取图片
	 */
	public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
	/**
	 * 使用相册中的图片
	 */
	public static final int SELECT_PIC_BY_PICK_PHOTO = 2;

	/**
	 * 获取到的图片路径
	 */
	private String picPath = "";
	/*
	* 图片的uri
	* */
	private Uri photoUri;


	@Override
	protected String title() {
		return "发布动态";
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.add_news);

		mContext = AddNewsActivity.this;
		initNaviView();
		qury();

		//上传动态按钮的监听事件
		submit.setOnClickListener(new View.OnClickListener() {
			Comments comments = new Comments();
			String contents = content.getText().toString();
			@Override
			public void onClick(View view) {
				//上传图片
				final BmobFile file = new BmobFile(new File(picPath));

				file.upload(AddNewsActivity.this, new UploadFileListener() {
					@Override
					public void onSuccess() {
						comments.setImage(file);
						comments.save(AddNewsActivity.this);
					}
					@Override
					public void onProgress(Integer arg0) {
						// TODO Auto-generated method stub
					}
					@Override
					public void onFailure(int i, String s) {
						Toast.makeText(getApplicationContext(), "上传失败!", Toast.LENGTH_SHORT).show();
					}
				});

				//上传文字
				comments.setContent(contents);
				comments.setUserId(userId);
				comments.setUserName(userName);
				comments.save(AddNewsActivity.this, new SaveListener() {
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


		//弹出选择图片的窗口
		add_pic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				menuWindow = new SelectPicPopupWindow(mContext, itemsOnClick);
				menuWindow.showAtLocation(findViewById(R.id.uploadLayout),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			}
		});

	}

	//查询数据，得到当前用户的用户名
	private void qury() {
		userId = getCurrentUid();
		final BmobQuery<User> userNameQuery = new BmobQuery<User>();
		userNameQuery.getObject(this, userId, new GetListener<User>() {
			@Override
			public void onSuccess(User user) {
				userName = user.getUsername();
			}

			@Override
			public void onFailure(int i, String s) {

			}
		});
	}


	//为弹出窗口实现监听类
	private View.OnClickListener itemsOnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// 隐藏弹出窗口
			menuWindow.dismiss();

			switch (v.getId()) {
				case R.id.takePhotoBtn:// 拍照
					takePhoto();
					break;
				case R.id.pickPhotoBtn:// 相册选择图片
					pickPhoto();
					break;
				case R.id.cancelBtn:// 取消
					break;
				default:
					break;
			}
		}
	};

	/**
	 * 拍照获取图片
	 */
	private void takePhoto() {
		// 执行拍照前，应该先判断SD卡是否存在
		String SDState = Environment.getExternalStorageState();
		if (SDState.equals(Environment.MEDIA_MOUNTED)) {

			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			/***
			 * 需要说明一下，以下操作使用照相机拍照，拍照后的图片会存放在相册中的
			 * 这里使用的这种方式有一个好处就是获取的图片是拍照后的原图
			 * 如果不使用ContentValues存放照片路径的话，拍照后获取的图片为缩略图不清晰
			 */
			ContentValues values = new ContentValues();
			//获取图片路径
			photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
			startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
		} else {
			Toast.makeText(this, "内存卡不存在", Toast.LENGTH_LONG).show();
		}
	}

	/***
	 * 从相册中取图片
	 */
	private void pickPhoto() {
		//使用intent调用系统提供的相册功能，使用startActivityForResult是为了获取用户选择的图片
		Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
		// 如果要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
		getAlbum.setType("image/*");
		startActivityForResult(getAlbum, SELECT_PIC_BY_PICK_PHOTO);
	}

	//重写startActivityForResult方法
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 点击取消按钮
		if (resultCode != RESULT_OK) {
			Log.e("TAG->onresult","ActivityResult resultCode error");
			return;
		}
		// 可以使用同一个方法，这里分开写为了防止以后扩展不同的需求
		switch (requestCode) {
			case SELECT_PIC_BY_PICK_PHOTO:// 如果是直接从相册获取
				doPhoto(requestCode, data);
				break;
			case SELECT_PIC_BY_TACK_PHOTO:// 如果是调用相机拍照时
				doPhoto(requestCode, data);
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 选择图片后，获取图片的路径
	 *
	 * @param requestCode
	 * @param data
	 */
	private void doPhoto(int requestCode, Intent data) {

		// 从相册取图片，有些手机有异常情况，请注意
		if (requestCode == SELECT_PIC_BY_PICK_PHOTO) {
			if (data == null) {
				Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
				return;
			}
			Bitmap bm=null;

			 photoUri = data.getData();

			AlertDialog.Builder builder = new AlertDialog.Builder(AddNewsActivity.this);
			builder.setMessage("图片的uri:"+photoUri);
			builder.setTitle("提示");
			builder.create().show();

			if (photoUri == null) {
				Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
				return;
			}
			//外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
			ContentResolver resolver = getContentResolver();
			try {

				bm = MediaStore.Images.Media.getBitmap(resolver, photoUri);
				// 显示在图片控件上
				add_pic.setImageBitmap(bm);

				String[] pojo = {MediaStore.MediaColumns.DATA};
				Cursor cursor = managedQuery(photoUri, pojo, null, null, null);
				//按我个人理解 这个是获得用户选择的图片的索引值
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				//将光标移至开头 ，这个很重要，不小心很容易引起越界
				cursor.moveToFirst();
				//最后根据索引值获取图片路径
				picPath = cursor.getString(column_index);

				AlertDialog.Builder builder2 = new AlertDialog.Builder(AddNewsActivity.this);
				builder2.setMessage("图片的路径:"+picPath);
				builder2.setTitle("提示");
				builder2.create().show();

			}catch (IOException e){
				Log.e("TAG-->Error",e.toString());
			}


		}

	}
}
