package com.pangbai.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

import com.pangbai.dowork.tool.util;

public class CustomDialog extends Dialog {
    private Window mWindow;
    public  int progress;
    private Context ct;
    public CustomDialog(Context context) {
      //  super(context,R.style.customDialog);
	  super(context);
      this.ct=context;
		
    }

    public CustomDialog(Context context, int themeResId) {
        super(context, themeResId);
    }
     
    
    protected CustomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
	public void setView(int v) {
      //  setContentView(LayoutInflater.from(ct).inflate(R.layout.linux_list, null));
	  setContentView(v);
        
    }

	@Override
	public void show() {
		super.show();
        setCancelable(false);
		//setProperty();
	}

	 
    public void setProperty(int height) {
	
        mWindow = getWindow();
        WindowManager.LayoutParams params = mWindow.getAttributes();
      /*  params.x = x;  //设置对话框的位置，0为中间
        params.y = y;
        params.width = w;
        params.height = h;*/
        
        params.height = util.Dp2Px(ct,height);
		params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.alpha = 1.0f; //设置对话框的透明度,1f不透明
        mWindow.setAttributes(params);
        
	//mWindow.clearFlags( WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }
	}
