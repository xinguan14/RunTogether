package com.xinguan14.jdyp.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xinguan14.jdyp.MyVeiw.ChangeAvatarPopupWindow;
import com.xinguan14.jdyp.MyVeiw.CircleImageView;
import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.StikkyHeader.example.AchievementsFragment;
import com.xinguan14.jdyp.StikkyHeader.example.ChangeMyInfoFragment;
import com.xinguan14.jdyp.StikkyHeader.example.DynamicFragment;
import com.xinguan14.jdyp.base.ParentWithNaviFragment;
import com.xinguan14.jdyp.bean.User;
import com.xinguan14.jdyp.config.BmobConstants;
import com.xinguan14.jdyp.model.UserModel;
import com.xinguan14.jdyp.util.ImageLoadOptions;
import com.xinguan14.jdyp.util.PhotoUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * 设置
 *
 * @author :smile
 * @project:SetFragment
 * @date :2016-01-25-18:23
 */
//
public class SetFragment extends ParentWithNaviFragment {


    @Bind(R.id.tv_user_name)
    TextView tv_user_name;
    //
//    @Bind(R.id.layout_info)
//    RelativeLayout layout_info;
    @Bind(R.id.iv_avatar)
    CircleImageView circleImageView;

    @Bind(R.id.set)
    LinearLayout layout_all;


    private FragmentManager manager;
    private FragmentTransaction ft;
    // 上传图片弹出框
    private ChangeAvatarPopupWindow menuWindow;


    @Override
    protected String title() {
        return "我的";
    }

    public static SetFragment newInstance() {
        SetFragment fragment = new SetFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SetFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_set, container, false);
        initNaviView();
        ButterKnife.bind(this, rootView);
        String username = UserModel.getInstance().getCurrentUser().getUsername();
        tv_user_name.setText(TextUtils.isEmpty(username) ? "" : username);

        //更新头像
        User user = BmobUser.getCurrentUser(getActivity(), User.class);
        refreshAvatar(user.getAvatar());

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //弹出选择图片弹出框
                menuWindow = new ChangeAvatarPopupWindow(getActivity(), itemsOnClick);
                menuWindow.showAtLocation(getActivity().findViewById(R.id.set),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView listView = (ListView) getActivity().findViewById(R.id.listview);
        manager = getFragmentManager();
        String[] mFrags = {"最新成就", "我的动态", "个人资料"};

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mFrags);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = null;
                switch (position) {
                    case 0:
                        fragment = new AchievementsFragment();
                        break;
                    case 1:
                        fragment = new DynamicFragment();
                        break;
                    case 2:
                        fragment = new ChangeMyInfoFragment();
                        break;
                }
                ft = manager.beginTransaction();
                ft.replace(R.id.id_content, fragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof AddMenu)
            ((AddMenu) getActivity()).showMenu();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() instanceof HideTab) {
            ((HideTab) getActivity()).hide();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }




    public String filePath = "";


    //为弹出的窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 隐藏弹出窗口
            menuWindow.dismiss();

            switch (v.getId()) {
				case R.id.takePhotoBtn:// 拍照
                    File dir = new File(BmobConstants.MyAvatarDir);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    // 原图
                    File file = new File(dir, new SimpleDateFormat("yyMMddHHmmss")
                            .format(new Date()));
                    filePath = file.getAbsolutePath();// 获取相片的保存路径
                    Uri imageUri = Uri.fromFile(file);

                    Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent2.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent2,
                            BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA);

					break;
                case R.id.pickPhotoBtn:// 相册选择图片
                    Intent intent = new Intent(Intent.ACTION_PICK, null);
                    intent.setDataAndType(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(intent,
                            BmobConstants.REQUESTCODE_UPLOADAVATAR_LOCATION);

                    break;
                case R.id.cancelBtn:// 取消

                    break;
                default:
                    break;
            }
        }
    };

    /**
     * @return void
     * @throws
     * @Title: startImageAction
     */
    private void startImageAction(Uri uri, int outputX, int outputY,
                                  int requestCode, boolean isCrop) {
        Intent intent = null;
        if (isCrop) {
            intent = new Intent("com.android.camera.action.CROP");
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        }
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, requestCode);
    }

    Bitmap newBitmap;
    boolean isFromCamera = false;// 区分拍照旋转
    int degree = 0;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA:// 拍照修改头像
//                if (resultCode == 1) {
                if (!Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    Toast.makeText(getActivity(), "SD不可用", Toast.LENGTH_SHORT).show();
//                        ShowToast("SD不可用");

                    return;
                }
                isFromCamera = true;
                File file = new File(filePath);
                degree = PhotoUtil.readPictureDegree(file.getAbsolutePath());
                Log.i("life", "拍照后的角度：" + degree);
                startImageAction(Uri.fromFile(file), 200, 200,
                        BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
//                }
                break;
            case BmobConstants.REQUESTCODE_UPLOADAVATAR_LOCATION:// 本地修改头像
                Uri uri = null;
                if (data == null) {
                    return;
                }
//                if (resultCode == 1) {
                if (!Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
//                        ShowToast("SD不可用");
                    Toast.makeText(getActivity(), "SD不可用", Toast.LENGTH_SHORT).show();
                    return;
                }
                isFromCamera = false;
                uri = data.getData();
                startImageAction(uri, 200, 200,
                        BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
//                } else {
//                    Toast.makeText(getActivity(), "照片获取失败", Toast.LENGTH_SHORT).show();

//                    ShowToast("照片获取失败");
//                }

                break;
            case BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP:// 裁剪头像返回
                // TODO sent to crop
                if (data == null) {
                    // Toast.makeText(this, "取消选择", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    saveCropAvator(data);
                }
                // 初始化文件路径
                filePath = "";
                // 上传头像
                uploadAvatar();
                break;
            default:
                break;

        }
    }

    private void uploadAvatar() {
//        BmobLog.i("头像地址：" + path);
        final BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.upload(getActivity(), new UploadFileListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                String url = bmobFile.getFileUrl(getActivity());
                // 更新BmobUser对象
                updateUserAvatar(url);
            }

            @Override
            public void onProgress(Integer arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFailure(int arg0, String msg) {
                // TODO Auto-generated method stub
//                ShowToast("头像上传失败：" + msg);
                Toast.makeText(getActivity(), "头像上传失败" + msg, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void updateUserAvatar(final String url) {
        User u = new User();
        u.setAvatar(url);
        updateUserData(u, new UpdateListener() {
            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
//                ShowToast("头像更新成功！");
                Toast.makeText(getActivity(), "头像更新成功", Toast.LENGTH_SHORT).show();

                // 更新头像
                refreshAvatar(url);
            }

            @Override
            public void onFailure(int code, String msg) {
                // TODO Auto-generated method stub
//                ShowToast("头像更新失败：" + msg);
                Toast.makeText(getActivity(), "头像上传失败" + msg, Toast.LENGTH_SHORT).show();

            }
        });
    }

    String path;

    /**
     * 保存裁剪的头像
     *
     * @param data
     */
    private void saveCropAvator(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap bitmap = extras.getParcelable("data");
            Log.i("life", "avatar - bitmap = " + bitmap);
            if (bitmap != null) {
                bitmap = PhotoUtil.toRoundCorner(bitmap, 10);
                if (isFromCamera && degree != 0) {
                    bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
                }
                circleImageView.setImageBitmap(bitmap);
                // 保存图片
                String filename = new SimpleDateFormat("yyMMddHHmmss")
                        .format(new Date()) + ".png";
                path = BmobConstants.MyAvatarDir + filename;
                PhotoUtil.saveBitmap(BmobConstants.MyAvatarDir, filename,
                        bitmap, true);
                // 上传头像
                if (bitmap != null && bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
        }
    }

    private void updateUserData(User user, UpdateListener listener) {
        User current = BmobUser.getCurrentUser(getActivity(), User.class);
        user.setObjectId(current.getObjectId());
        user.update(getActivity(), listener);
    }

    /**
     * 更新头像 refreshAvatar
     *
     * @return void
     * @throws
     */
    private void refreshAvatar(String avatar) {
        if (avatar != null && !avatar.equals("")) {
            ImageLoader.getInstance().displayImage(avatar, circleImageView,
                    ImageLoadOptions.getOptions());
        } else {
            circleImageView.setImageResource(R.mipmap.head);


        }
    }

    /**
     * fragment调用HideTab
     */
    public interface HideTab {
        void hide();
    }

    public interface AddMenu {
        void showMenu();
    }
}
