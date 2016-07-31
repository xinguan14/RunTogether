package com.xinguan14.jdyp.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.xinguan14.jdyp.R;
import com.xinguan14.jdyp.bean.Comment;
import com.xinguan14.jdyp.bean.Post;
import com.xinguan14.jdyp.bean.User;
import com.xinguan14.jdyp.util.SysUtils;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by yyz on 2016/7/28.
 */
public class AddCommentPopupWindow extends PopupWindow {
    private Button submitComment;
    private EditText commentEdit;
    public String comments;
    public  String postId;

    private View mMenuView;
    private int width;
    private int height;
    private Context mContext;

    @SuppressLint("InflateParams")
    public AddCommentPopupWindow(final Context context, final String postId) {
        super(context);
        this.mContext = context;
        this.postId = postId;
        this.width = SysUtils.getScreenWidth(context);
        this.height = SysUtils.getScreenHeight(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.add_comments, null);

        submitComment = (Button) mMenuView.findViewById(R.id.submit_comment);
        commentEdit = (EditText) mMenuView.findViewById(R.id.add_comment);
        // 设置按钮监听
        submitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comments=commentEdit.getText().toString();
                if (comments.length()==0){
                    Toast.makeText(context, "评论不能为空", Toast.LENGTH_SHORT).show();
                }else {
                    User user = BmobUser.getCurrentUser(context, User.class);
                    Post post = new Post();
                    post.setObjectId(postId);
                    final Comment comment = new Comment();
                    comment.setContent(comments);
                    comment.setPost(post);
                    comment.setUser(user);
                    comment.save(context, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            // TODO Auto-generated method stub
                            Toast.makeText(context, "评论成功", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }

                        @Override
                        public void onFailure(int code, String msg) {
                            // TODO Auto-generated method stub
                            Toast.makeText(context, "评论失败", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    });
                }
            }
        });




        //在PopupWindow里面就加上下面代码，让键盘弹出时，不会挡住pop窗口。
        this.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //openKeyboard(new Handler(), 600);
        // 设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);

        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(width);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(height);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.PopupAnimation);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(00000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.comment_view).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }

    private void openKeyboard(Handler mHandler, int s) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, s);
    }

}
