package com.blxt.markdowneditors.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

/**
 * 动态权限 申请模块
 */
@TargetApi(Build.VERSION_CODES.M)
public class mPermissionsUnit {

    private Activity mActivity;
    private String[] mPermissions;
    private PermissionCheckCallback mCallback;
    private static final String TAG = "mPermissionsUnit测试";
    private static final int REQUST_CODE = 0;
    private boolean isDefaultDialog = false;

    public mPermissionsUnit setActivity(Activity activity) {
        this.mActivity = activity;
        return this;
    }

    public mPermissionsUnit setPermissions(String[] permissions) {
        this.mPermissions = permissions;
        return this;
    }

    public mPermissionsUnit setCallback(PermissionCheckCallback callback) {
        this.mCallback = callback;
        return this;
    }

    public mPermissionsUnit setDefaultDialog(boolean isDefaultDialog) {
        this.isDefaultDialog = isDefaultDialog;
        return this;
    }

    /**
     * 检查 是否 有 xx权限
     * @param permission
     * @return
     */
    public boolean isPermissionGrant(String permission) {
        return mActivity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * 发起请求权限
     * @return
     */
    public boolean checkPermission() {


        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }

        if (mPermissions == null || mActivity == null || mPermissions == null) {
            return false;
        }

        List<String> permissionToRequestList = new LinkedList<String>();
        for (String permission : mPermissions) {
            if(mActivity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionToRequestList.add(permission);
            }
        }
        String[] permissionToRequest = (String[])permissionToRequestList.toArray(new String[permissionToRequestList.size()]);
        if(permissionToRequest.length > 0){
            mActivity.requestPermissions(permissionToRequest, REQUST_CODE);
            if (mCallback != null) {
                mCallback.onRequest();
            }
        } else {
            if (mCallback != null) {
                mCallback.onGranted();
            }
        }

        return false;
    }

    /**
     * 结果回调接口
     */
    public interface PermissionCheckCallback {
        void onRequest();
        void onGranted();
        void onGrantSuccess();
        void onGrantFail();
    }


    /**
     * 结果回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUST_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mCallback != null) {
                        mCallback.onGrantSuccess();
                    }
                } else {
                    Log.v(TAG, "未检测到权限");
                    if (isDefaultDialog) {
                        popupWarningDialog();
                        return;
                    }
                    if (mCallback != null) {
                        mCallback.onGrantFail();
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 权限请求 提示 对话框
     */
    private void popupWarningDialog(){

        DialogInterface.OnClickListener dialogOnclicListener=new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case Dialog.BUTTON_POSITIVE:
                        if (mCallback != null) {
                            mCallback.onGranted();
                        }
                        break;
                    case Dialog.BUTTON_NEGATIVE:
                        mActivity.finish();
                        break;
                    default:
                        break;
                }
            }
        };

        int checkSelfPermission;
        try {
            checkSelfPermission = ActivityCompat.checkSelfPermission(mActivity, mPermissions[0]);
        } catch (RuntimeException e) {
            Toast.makeText(mActivity, "请打开必要权限", Toast.LENGTH_SHORT)
                    .show();
            Log.e(TAG, "RuntimeException:" + e.getMessage());
            return;
        }

        AlertDialog.Builder builder=new AlertDialog.Builder(mActivity);
        builder.setTitle("警告");
        builder.setMessage("请允许读写权限,以提供基础服务");
        builder.setPositiveButton("确认",dialogOnclicListener);
        builder.setNegativeButton("取消", dialogOnclicListener);
        builder.create().show();

    }
}
