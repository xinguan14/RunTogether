package com.xinguan14.jdyp.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.adapter.MyAdapter;
import com.xinguan14.jdyp.adapter.base.BaseListAdapter;
import com.xinguan14.jdyp.adapter.base.BaseListHolder;
import com.xinguan14.jdyp.base.ParentWithNaviActivity;
import com.xinguan14.jdyp.bean.Post;
import com.xinguan14.jdyp.bean.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;

public class AddPostActivity extends ParentWithNaviActivity {

	//内容文本框
	@Bind(R.id.edit_content)
	EditText editContent;
	//发布按钮
	@Bind(R.id.submit)
	Button submit;
	//加载动画
	@Bind(R.id.progressBar)
	ProgressBar mProgressBar;
	//显示图片的布局
	@Bind(R.id.show_pic_grid)
	GridView showPicGrid;

	// 上传图片弹出框
	private SelectPicPopupWindow menuWindow;


	//存放图片路径的数组
	private String[] imagePath ;

	//显示图片的集合
	private List<Bitmap> imageItem;


	@Override
	protected String title() {
		return "发布动态";
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_news);
		initNaviView();
		//GridView的初始界面
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_add_pic); //加号
		imageItem = new ArrayList<Bitmap>();
		imageItem.add(bmp);

		AddGridViewAdapter gridViewAdapter=new AddGridViewAdapter(AddPostActivity.this, imageItem,R.layout.add_image_grid_item);
		showPicGrid.setAdapter(gridViewAdapter);
        /*
         * 监听GridView点击事件
         * 报错:该函数必须抽象方法 故需要手动导入import android.view.View;
         */
		showPicGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id)
			{
				 if(position == imageItem.size()-1) { //点击图片位置为+ 0对应0张图片
					 if( imageItem.size() == 7) { //第一张为默认图片
						 Toast.makeText(AddPostActivity.this, "最多添加六张图片", Toast.LENGTH_SHORT).show();
						 return;
					 }else {
						 //弹出选择图片弹出框
						 menuWindow = new SelectPicPopupWindow(AddPostActivity.this, itemsOnClick);
						 menuWindow.showAtLocation(findViewById(R.id.uploadLayout),
								 Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
						 //通过onResume()刷新数据
					 }
				}else {
					 //删除图片
					 dialog(position);
				 }

			}
		});

		//上传动态按钮的监听事件
		submit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				//有图片的动态上传
				if(imageItem.size()>1) {
					//获取发布的动态的内容
					String contents = editContent.getText().toString();
						if (imagePath != null && imagePath.length> 0) {
							new UploadFileTask(contents).execute(imagePath);

					}
				}else {//没有图片的动态上传

					//获取当前用户
					User user = BmobUser.getCurrentUser(AddPostActivity.this, User.class);
					//创建帖子信息
					Post mPost = new Post();
					String contents = editContent.getText().toString();
					//判断内容是否为空
					String length = editContent.getText().toString().trim();
					if(length.length()!=0) {
					mPost.setContent(contents);
						//添加动态和用户之间的一对一关联
						mPost.setAuthor(user);
						mPost.save(AddPostActivity.this, new SaveListener() {
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
					}else {
						Toast.makeText(getApplicationContext(), "内容不能为空!", Toast.LENGTH_SHORT).show();
					}

				}
			}
		});
	}

	/**
	 * 为了得到传回的数据，必须在前面的Activity中（指MainActivity类）重写onActivityResult方法
	 *
	 * requestCode 请求码，即调用startActivityForResult()传递过去的值
	 * resultCode 结果码，结果码用于标识返回数据来自哪个新Activity
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==RESULT_OK) {
			// 取出Intent里的Extras数据
			Bundle bundle = data.getExtras();
			// 取出Bundle中的数据,即传递进来的图片路径数组
			imagePath = bundle.getStringArray("imagePath");

			//显示要上传的图片
			imageItem = new ArrayList<Bitmap>();

			if (imagePath.length != 0) {
				Bitmap add = BitmapFactory.decodeResource(getResources(), R.drawable.ic_add_pic); //加号
				BitmapFactory.Options option = new BitmapFactory.Options();
				// 压缩图片:表示缩略图大小为原始图片大小的几分之一，1为原图
				option.inSampleSize = 2;
				// 根据图片的SDCard路径读出Bitmap,
				Bitmap[] bm = new Bitmap[imagePath.length + 1];
				for (int i = 0; i < imagePath.length + 1; i++) {
					if (i == imagePath.length) {
						bm[i] = add;
					} else {
						bm[i] = BitmapFactory.decodeFile(imagePath[i], option);
						Log.i("info", imagePath[i]);
					}

				}

				for (int i = 0; i < bm.length; i++) {
					imageItem.add(bm[i]);
				}
				AddGridViewAdapter gridViewAdapter = new AddGridViewAdapter(AddPostActivity.this, imageItem, R.layout.add_image_grid_item);
				showPicGrid.setAdapter(gridViewAdapter);
			}
		}
	}


	//为弹出的窗口实现监听类
	private View.OnClickListener itemsOnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// 隐藏弹出窗口
			menuWindow.dismiss();

			switch (v.getId()) {
				/*case R.id.takePhotoBtn:// 拍照
					takePhoto();
					break;*/
				case R.id.pickPhotoBtn:// 相册选择图片
					//得到新打开Activity关闭后返回的数据
					//第二个参数为请求码，可以根据业务需求自己编号
					startActivityForResult(new Intent(AddPostActivity.this, ChooseImageActivity.class), 1);
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
	 *//*
	private void takePhoto() {
		// 执行拍照前，应该先判断SD卡是否存在
		String SDState = Environment.getExternalStorageState();
		if (SDState.equals(Environment.MEDIA_MOUNTED)) {

			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			*//***
			 * 需要说明一下，以下操作使用照相机拍照，拍照后的图片会存放在相册中的
			 * 这里使用的这种方式有一个好处就是获取的图片是拍照后的原图
			 * 如果不使用ContentValues存放照片路径的话，拍照后获取的图片为缩略图不清晰
			 *//*
			ContentValues values = new ContentValues();
			//获取图片路径
			photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
			startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
		} else {
			Toast.makeText(this, "内存卡不存在", Toast.LENGTH_LONG).show();
		}
	}*/

	/*
    * Dialog对话框提示用户删除操作
    * position为删除图片位置
    */
	protected void dialog(final int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(AddPostActivity.this);
		builder.setMessage("确认移除已添加图片吗？");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//从显示的集合中删除
				imageItem.remove(position);
				//从上传的数组中删除

				AddGridViewAdapter gridViewAdapter=new AddGridViewAdapter(AddPostActivity.this, imageItem,R.layout.add_image_grid_item);
				showPicGrid.setAdapter(gridViewAdapter);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	/*
	* 上传动态，放在异步处理中
	* */
	 class UploadFileTask extends AsyncTask<String[],Void,String> {
		private String content;

		public UploadFileTask(String content) {
			this.content = content;
		}

		@Override
		protected String doInBackground(String[]... strings) {
			BmobFile.uploadBatch(AddPostActivity.this,imagePath, new UploadBatchListener() {
				@Override
				public void onSuccess(List<BmobFile> files, List<String> url) {
					//1、files-上传完成后的BmobFile集合，是为了方便大家对其上传后的数据进行操作，例如你可以将该文件保存到表中
					//2、urls-上传文件的完整url地址
					if(url.size()==imagePath.length){//如果数量相等，则代表文件全部上传完成
						//判断内容是否为空
						String length =content.trim();
						if(length.length()!=0) {
							//获取当前用户
							User user = BmobUser.getCurrentUser(AddPostActivity.this, User.class);
							//创建帖子信息
							Post mPost = new Post();
							//获取上传到Bmob后台的图片的路径
							String urlString = "";
							for (int i = 0; i < url.size(); i++) {
								urlString += url.get(i) + "#";
							}
							//创建帖子信息
							mPost = new Post();
							mPost.setImageurl(urlString);
							mPost.setContent(content);
							//添加动态和用户之间的一对一关联
							mPost.setAuthor(user);
							mPost.save(AddPostActivity.this, new SaveListener() {
								@Override
								public void onSuccess() {
									Toast.makeText(getApplicationContext(), "发布成功!", Toast.LENGTH_SHORT).show();
									setResult(RESULT_OK);
									//发布之后清空选中图片
									MyAdapter.mSelectedImage.clear();
									finish();
								}

								@Override
								public void onFailure(int code, String arg0) {
									Toast.makeText(getApplicationContext(), "发布失败!", Toast.LENGTH_SHORT).show();
								}
							});
						}else {
							Toast.makeText(getApplicationContext(), "内容不能为空!", Toast.LENGTH_SHORT).show();
						}

					}
				}

				@Override
				public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
					//1、curIndex--表示当前第几个文件正在上传
					//2、curPercent--表示当前上传文件的进度值（百分比）
					//3、total--表示总的上传文件数
					//4、totalPercent--表示总的上传进度（百分比）
				}

				@Override
				public void onError(int i, String s) {
					Log.i("info","错误码"+i +",错误描述："+s);
				}
			});
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressBar.setVisibility(View.VISIBLE);
		}
	}


	 class AddGridViewAdapter extends BaseListAdapter<Bitmap> {

		 private AddGridViewAdapter(Context context,List<Bitmap> bm,int itemLayoutId){
			 super(context,bm,itemLayoutId);

		 }
		 @Override
		 public void convert(BaseListHolder holder, Bitmap item) {
			 ImageView imageView = holder.getView(R.id.imageView);
			 imageView.setImageBitmap(item);
		 }
	 }
}
