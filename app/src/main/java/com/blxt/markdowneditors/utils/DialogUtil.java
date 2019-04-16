package com.blxt.markdowneditors.utils;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;


/**
 * 使用方式
 *                 final AlertDialog noLoginDialog = DialogUtil.showDialog(
 *                         this,"提示","您正在编辑，是否保存后新建文件？",
 *                         "保存","取消",
 *                         null);
 *                 noLoginDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
 *                     @Override
 *                     public void onClick(View v) {
 *                        // you du
 *                         noLoginDialog.dismiss();
 *                     }
 *                 });
 *                 noLoginDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
 *                     @Override
 *                     public void onClick(View v) {
 *                         noLoginDialog.dismiss();
 *                     }
 *                 });
 */

public class DialogUtil {

	//无标题的进度条对话框
	public static ProgressDialog showProgressDialog(Activity context, String msg) {
		return showProgressDialog(context, null, msg);
	}
	//有标题的进度条对话框
	public static ProgressDialog showProgressDialog(Activity context,
			String title, String msg) {
		ProgressDialog mDialog = new ProgressDialog(context);
		if (title != null) {
			mDialog.setTitle(title);
		}
		mDialog.setMessage(msg);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.show();
		return mDialog;
	}

	//普通对话框（确认，取消）
	public static AlertDialog showDialog(Activity context,
										 String title, String msg ) {

		return showDialog(context, -1, title, msg,null);
	}

	//普通对话框（确认，取消）
	public static AlertDialog showDialog(Activity context, String title,
										 String msg, OnClickListener onClickListener) {

		return showDialog(context, -1, title, msg,onClickListener);
	}
	//普通对话框 2按键
	public static AlertDialog showDialog(Activity context,
										 String title, String msg,
										 String okText,String cancelText,
										 OnClickListener onClickListener) {

		return showDialog(context, title, msg,okText, cancelText, null, onClickListener);
	}
	//普通对话框 3按键
	public static AlertDialog showDialog(Activity context,
										 String title, String msg,
										 String okText,String cancelText, String neutral,
										 OnClickListener onClickListener) {

		return showDialog(context, -1,null, title, msg,okText, cancelText, neutral, onClickListener);
	}

	//带图标的对话框（确认，取消）
	public static AlertDialog showDialog(Activity context, int iconResID,
										String title, String msg,
										 OnClickListener onClickListener) {

		return showDialog(context, iconResID,null, title, msg,"确认","取消", null, onClickListener);
	}

	//自定义视图Dialog
	public static AlertDialog showDialog(Context context,View view,String title, OnClickListener onClickListener){

		return showDialog(context, view, title, "确定", "取消", onClickListener);
	}

	//自定义视图 2按键
	public static AlertDialog showDialog(Context context,View view,String title,
										 String okText,String cancelText,
										 OnClickListener onClickListener){

		return showDialog(context, -1,view, title, null,okText,cancelText, null, onClickListener);
	}


	//自定义视图 2按键,图标
	public static AlertDialog showDialog(Context context,int icoResId, View view,String title,
										 String okText,String cancelText,
										 OnClickListener onClickListener){

		return showDialog(context, icoResId,view, title, null,okText,cancelText, null, onClickListener);
	}

	//自定义视图 3按键
	public static AlertDialog showDialog(Context context,
										 View view, String title,
										 String okText,String cancelText, String neutral,
										 OnClickListener onClickListener) {

		return showDialog(context, -1,view, title, null,okText,cancelText, neutral, onClickListener);
	}

	//自定义视图 3按键 ,自定义图标
	public static AlertDialog showDialogWithIco(Context context,int icoResId,
										 View view, String title,
										 String okText,String cancelText, String neutral,
										 OnClickListener onClickListener) {

		return showDialog(context, icoResId,view, title, null,okText,cancelText, neutral, onClickListener);
	}

	/**
	 * 自定义对话框（确认，取消）
	 *
	 * @param context
	 *            上下文对象
	 * @param iconResID
	 *            图标，无为-1
	 * @param view
	 *            视图
	 * @param title
	 *            标题,没有时置为null
	 * @param msg
	 *            提示内容
	 * @param okText
	 *            Positive提示文本
	 * @param cancelText
	 *            Negative提示文本
	 * @return AlertDialog 对象
	 */

    public static AlertDialog showDialog(Context context,
                                         int iconResID, View view,
                                         String title,String msg,
                                         String okText,String cancelText,String neutral)
    {
        return showDialog(context, iconResID,view, title, msg,okText,cancelText, neutral, null);
    }
    public static AlertDialog showDialog(Context context,
										 int iconResID, View view,
										 String title,String msg,
										 String okText,String cancelText,String neutral,
										 OnClickListener onClickListener) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		if (title != null) {	// 标题
			builder.setTitle(title);
		}
		if (iconResID != -1) {	// ic
			builder.setIcon(iconResID);
		}
		if (view != null )		// 视图
		{
			builder.setView(view);
		}
		if (msg != null)		// 消息
		{
			builder.setMessage(msg);
		}
		if (neutral != null)
		{
			//if( onClickListener != null )
			builder.setNeutralButton(neutral, onClickListener);
		}

		//if( onClickListener != null ) {
			builder.setPositiveButton(okText, onClickListener);
			builder.setNegativeButton(cancelText, onClickListener);
			builder.setCancelable(false);   //设置点击空白区域不消失
		//}else
		//{
		//	builder.setCancelable(true);
		//}

		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		return dialog;
	}

	/**
	 * 列表对话框
	 */
	//        AlertDialog alertDialog = new AlertDialog
//                .Builder(MainActivity.this)
//                .setItems(new String[]{"打开文件管理器", "打开QQ和微信目录"}, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        switch(which)
//                        {
//                            case 0:
//                            {
//                                // 打开文件管理器
//                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                                intent.setType("*/*");//无类型限制
//                                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                                startActivityForResult(intent, 1);
//                                Log.i("测试","进入文件管理器");
//                            }
//                            break;
//                            case 1:
//                            {
//                                Log.i("测试","打开QQ和微信目录");
//                            }
//                        }
//
//                    }
//                }).create();
//        alertDialog.show();


/** 简单输入域对话框*/
//	final EditText et = new EditText(this);
//                new AlertDialog.Builder(this).setTitle("请输入消息")
//                        .setIcon(android.R.drawable.sym_def_app_icon)
//                        .setView(et)
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//		@Override
//		public void onClick(DialogInterface dialogInterface, int i) {
//			//按下确定键后的事件
//			Toast.makeText(getApplicationContext(), et.getText().toString(),Toast.LENGTH_LONG).show();
//		}
//	}).setNegativeButton("取消",null).show();


	//打开app应用程序信息界面
    public static void startSetting(final Activity activity, String content) {
        Dialog deleteDialog = new AlertDialog.Builder(activity)
                .setTitle("提示")
                .setMessage(content)
                .setPositiveButton("打开app应用程序信息界面",
                        new OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                startSetting(activity);
                            }
                        }).create();
        deleteDialog.show();
    }

    /**
     * 启动app设置应用程序信息界面
     */
    public static void startSetting(Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(intent);
    }

}
