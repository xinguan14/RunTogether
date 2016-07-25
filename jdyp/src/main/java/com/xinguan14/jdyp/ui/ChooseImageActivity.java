package com.xinguan14.jdyp.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.adapter.MyAdapter;
import com.xinguan14.jdyp.bean.ImageFloder;
import com.xinguan14.jdyp.ui.fragment.AddPostActivity;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import butterknife.Bind;

/**
 * Created by yyz on 2016/7/23.
 */
public class ChooseImageActivity extends Activity {

    private ProgressDialog mProgressDialog;
    //存储文件夹中的图片数量
    private int mMaxCount;
    //图片显示的文件夹，默认是图片最多的文件夹
    private File mCurrentDir;
    //临时的辅助类，用于防止同一个文件夹的多次扫描
    private HashSet<String> mDirPaths = new HashSet<String>();
    //扫描拿到所有的图片文件夹
    private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();

    private  static final int DATA_LOADED=0X110;
    //所有的图片
    private List<String> mImgs;
    private MyAdapter mAdapter;
    //选中的图片集合
    private List<String> mSelectedImage=new ArrayList<String>();

   /* private GridView mGirdView;

    private RelativeLayout mBottomLy;
    private TextView mDirName;
    private TextView mDirCount;*/
    @Bind(R.id.id_gridView)
   GridView mGirdView;
    @Bind(R.id.id_choose_dir)
    TextView mDirName;
    @Bind(R.id.id_total_count)
    TextView mDirCount;
    @Bind(R.id.id_bottom_ly)
    RelativeLayout mBottomLy;
    /*@Bind(R.id.id_select)
     Button select;*/
    /*@Bind(R.id.id_back)
    ImageButton back;
*/
    //int totalCount = 0;

    private int mScreenHeight;

    private ListImageDirPopupWindow mListImageDirPopupWindow;

    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            if(msg.what==DATA_LOADED) {
                mProgressDialog.dismiss();
                // 为View绑定数据
                data2View();
                // 初始化展示文件夹的popupWindw
                initListDirPopupWindw();
            }
        }
    };

    /**
     * 为View绑定数据
     */
    private void data2View()
    {
        if (mCurrentDir == null)
        {
            Toast.makeText(getApplicationContext(), "未扫描到图片",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mImgs = Arrays.asList(mCurrentDir.list());
        /**
         * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
         */
        mAdapter = new MyAdapter(getApplicationContext(), mImgs,
                R.layout.grid_item, mCurrentDir.getAbsolutePath());
        mGirdView.setAdapter(mAdapter);
        mDirCount.setText(mMaxCount + "张");
        mDirName.setText(mCurrentDir.getName());

        //获取选中的照片
        mSelectedImage=mAdapter.getmSelectedImage();
    }

    /**
     * 初始化展示文件夹的popupWindw
     */
    private void initListDirPopupWindw()
    {
        mListImageDirPopupWindow = new ListImageDirPopupWindow(
                ViewGroup.LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
                mImageFloders, LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.list_dir, null));

        mListImageDirPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
        {

            @Override
            public void onDismiss()
            {
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        // 设置选择文件夹的回调
       mListImageDirPopupWindow.setOnImageDirSelected(new ListImageDirPopupWindow.OnImageDirSelected() {
           @Override
           public void selected(ImageFloder floder) {
               mCurrentDir = new File(floder.getDir());
               mImgs = Arrays.asList(mCurrentDir.list(new FilenameFilter()
               {
                   @Override
                   public boolean accept(File dir, String filename)
                   {
                       if (filename.endsWith(".jpg") || filename.endsWith(".png")
                               || filename.endsWith(".jpeg"))
                           return true;
                       return false;
                   }
               }));
               /**
               * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
               */
               mAdapter = new MyAdapter(getApplicationContext(), mImgs,
                       R.layout.grid_item, mCurrentDir.getAbsolutePath());
               mGirdView.setAdapter(mAdapter);
               // mAdapter.notifyDataSetChanged();
               mDirCount.setText(floder.getCount() + "张");
               mDirName.setText(floder.getName());
               mListImageDirPopupWindow.dismiss();

               //获取选中的照片
               mSelectedImage=mAdapter.getmSelectedImage();
           }
       });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_image_main);
        ImageView back = (ImageView)findViewById(R.id.id_back);
        Button select = (Button)findViewById(R.id.id_select);

        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        mScreenHeight = outMetrics.heightPixels;

        initView();
        initDatas();
        //弹出widow
        initEvent();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChooseImageActivity.this,AddPostActivity.class));
                finish();
            }
        });
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] imagePath = new String[mSelectedImage.size()];
                for (int i=0;i<mSelectedImage.size();i++) {
                    imagePath[i] = mSelectedImage.get(i);
                }
                //数据是使用Intent返回
                Intent intent = new Intent();
                //把返回数据存入Intent
                intent.putExtra("imagePath",imagePath);
                //设置该SelectActivity的结果码和返回数据
                ChooseImageActivity.this.setResult(RESULT_OK, intent);
                //关闭Activity
                ChooseImageActivity.this.finish();
                /*AlertDialog.Builder builder = new AlertDialog.Builder(ChooseImageActivity.this);
                String str="选中图片路径";
                for(int i=0;i<imagePath.length;i++){
                    str +=imagePath[i]+"下一张";
                }
                builder.setMessage(str);
                builder.setTitle("提示");
                builder.create().show();*/

            }
        });

    }

    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
     */
    private void initDatas()
    {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED))
        {
            Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }
        // 显示进度条
        mProgressDialog = ProgressDialog.show(this, null, "正在加载...");
        //启动线程扫描图片
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //
                String firstImage = null;
                //获得手机中的所有图片
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = ChooseImageActivity.this
                        .getContentResolver();

                // 只查询jpeg和png的图片
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[] { "image/jpeg", "image/png" },
                        MediaStore.Images.Media.DATE_MODIFIED);

                Log.e("TAG", mCursor.getCount() + "");
                while (mCursor.moveToNext())
                {
                    // 获取当前图片的路径
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));

                    Log.e("TAG", path);
                    // 拿到第一张图片的路径
                    if (firstImage == null)
                        firstImage = path;
                    // 获取该图片的父路径名
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null)
                        continue;
                    String dirPath = parentFile.getAbsolutePath();
                    ImageFloder imageFloder = null;

                    // 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
                    if (mDirPaths.contains(dirPath))
                    {
                        continue;
                    } else
                    {
                        mDirPaths.add(dirPath);
                        // 初始化imageFloder
                        imageFloder = new ImageFloder();
                        imageFloder.setDir(dirPath);
                        imageFloder.setFirstImagePath(path);
                    }
                    if (parentFile.list()==null){
                        continue;
                    }
                    int picSize = parentFile.list(new FilenameFilter()
                    {
                        @Override
                        public boolean accept(File dir, String filename)
                        {
                            if (filename.endsWith(".jpg")
                                    || filename.endsWith(".png")
                                    || filename.endsWith(".jpeg"))
                                return true;
                            return false;
                        }
                    }).length;
                    //mMaxCount += picSize;

                    imageFloder.setCount(picSize);
                    mImageFloders.add(imageFloder);

                    if (picSize > mMaxCount)
                    {
                        mMaxCount = picSize;
                        mCurrentDir = parentFile;
                    }
                }
                mCursor.close();

                // 扫描完成，辅助的HashSet也就可以释放内存了
                mDirPaths = null;

                // 通知Handler扫描图片完成
                mHandler.sendEmptyMessage(DATA_LOADED);

            }
        }).start();

    }

    /**
     * 初始化View
     */
    private void initView()
    {
        mGirdView = (GridView) findViewById(R.id.id_gridView);
        mDirName = (TextView) findViewById(R.id.id_choose_dir);
        mDirCount = (TextView) findViewById(R.id.id_total_count);

        mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);

    }

    /**
     * 为底部的布局设置点击事件，弹出popupWindow
     */
    private void initEvent()
    {

        mBottomLy.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mListImageDirPopupWindow
                        .setAnimationStyle(R.style.PopupAnimation);
                mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = .3f;
                getWindow().setAttributes(lp);
            }
        });
    }
}
