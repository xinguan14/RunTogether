package com.xinguan14.jdyp.MyVeiw;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;

import com.xinguan14.jdyp.R;


/**
 * @author wqn
 * 
 * ���ڼ�������
 */
public class LoadingDialog extends Dialog {
	@SuppressWarnings("unused")
	private Context context = null;
	private static LoadingDialog customProgressDialog = null;
	
	public LoadingDialog(Context context){
		super(context);
		this.context = context;
	}
	
	public LoadingDialog(Context context, int theme) {
        super(context, theme);
    }
	
	public static LoadingDialog createDialog(Context context){
		customProgressDialog = new LoadingDialog(context, R.style.LoadingDialog);
		customProgressDialog.setContentView(R.layout.loading_dialog);
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		
		return customProgressDialog;
	}
 
    public void onWindowFocusChanged(boolean hasFocus){
    	
    	if (customProgressDialog == null){
    		return;
    	}
    	
        ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();
    }
    
    /**
     *  ��ʾ����
     */
    public LoadingDialog setMessage(String strMessage){
    	return customProgressDialog;
    }
}
