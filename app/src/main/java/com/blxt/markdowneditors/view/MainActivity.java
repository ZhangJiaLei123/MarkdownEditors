/*
 * Copyright 2016. SHENQINCI(沈钦赐)<dev@blxt.me>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blxt.markdowneditors.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.IdRes;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;

import com.bigbai.mfileutils.FileUtils;
import com.bigbai.mfileutils.spControl.FalBoolean;
import com.bigbai.mfileutils.spControl.FalInt;
import com.bigbai.mfileutils.spControl.spBaseControl;
import com.bigbai.mlog.LOG;
import com.blxt.markdowneditors.utils.UnzipFromAssets;
import com.blxt.markdowneditors.utils.mPermissionsUnit;
import com.md2html.Markdown2Html;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.blxt.markdowneditors.utils.Toast;

import com.blxt.markdowneditors.AppContext;
import com.blxt.markdowneditors.R;
import com.blxt.markdowneditors.base.BaseDrawerLayoutActivity;
import com.blxt.markdowneditors.base.BaseFragment;

import java.io.IOException;

import static com.blxt.markdowneditors.utils.Toast.LENGTH_SHORT;

/**
 * The type Main activity.
 */
public class MainActivity extends BaseDrawerLayoutActivity {
    private BaseFragment mCurrentFragment;


    private int currentMenuId;


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    /**
     *  阴影的高度
     */
    @Override
    protected float getElevation() {
        return 0;
    }

    @Override
    public void onCreateAfter(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            setDefaultFragment(R.id.content_fragment_container);
        }

        initUpdate(false);
    }

    private void setDefaultFragment(@IdRes int fragmentId) {
        mCurrentFragment = new FolderManagerFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(fragmentId, mCurrentFragment)
                .commit();
    }


    public static String sdCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath();

    static boolean isPermissions = false;
    boolean isReadLockStyle;
    mPermissionsUnit mPermissions = new mPermissionsUnit();
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    spBaseControl spC;
    SharedPreferences SP; // 管理类
    FalInt oldDay ;
    FalBoolean isNewApp; // 是否最新app
    FalBoolean isNewResult; // 是否解压资源

    @Override
    public void initData() {

        SP = getSharedPreferences("com.bigbai.mdview.preferences", MODE_PRIVATE);
        spC = new spBaseControl(SP);
        oldDay = new FalInt(SP,"lastDay",20181220);

        // 权限检查和初始文件解压
        mPermissions.setActivity(this);

        isNewResult = new FalBoolean(SP,"isNewResult",false);
        isNewApp = new FalBoolean(SP,"isNewApp",true);

        // 获取权限
        //verifyStoragePermissions(this);
        mPermissions.setActivity(this)
                .setDefaultDialog(true)
                .setPermissions(PERMISSIONS_STORAGE)
                .setCallback(new mPermissionsUnit.PermissionCheckCallback() {
                    @Override
                    public void onRequest() {
                        LOG.i( "权限请求...");
                    }

                    @Override
                    public void onGranted() {
                        //  initView();
                        LOG.i("权限授予...");
                    }

                    @Override
                    public void onGrantSuccess() {
                        LOG.i("获取权限成功");
                        //  initView();
                    }

                    @Override
                    public void onGrantFail() {
                        LOG.i( "权限获取失败");
                    }
                }).checkPermission();


        boolean isRes = isNewResult.getValue();

        // 首次运行解压资源
        if ( (!isNewApp.getValue() || spC.getFirstRun()) || !isRes ) {
            LOG.i("解压资源");

            try {
                UnzipFromAssets.unZip(this, "HtmlPlugin.zip", sdCardRoot + "/HtmlPlugin/");
                isReadLockStyle = true;
                isNewApp.setValue(true);// 标更新
                isNewResult.setValue(true);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.showToast(this, "资源解压失败,部分风格可能无法生效",LENGTH_SHORT);
                isReadLockStyle = false;
            }

        } else {
            LOG.i("已是最新版");
        }
        initDataM();
    }

    void initDataM() {
        // md2html 初始化
        Markdown2Html.mdEndtitys.clear();

        Markdown2Html.init("file://" + sdCardRoot + "/HtmlPlugin/");
        String styleStr = FileUtils.ReadTxtFile(sdCardRoot + "/HtmlPlugin/style/style.style");
        //  MdBaseConfig.htmlStytle.addData(styleStr);

        Markdown2Html.isUseFlash = true;
        Markdown2Html.isActionMenu = true;
        Markdown2Html.isTocTop = true;

        Markdown2Html.initHighlightSty();
        Markdown2Html.loadMdFormat();
        Markdown2Html.loadFont();
        Markdown2Html.loadHeader();
        Markdown2Html.loadStyle();
        Markdown2Html.loadActionFrame();
        Markdown2Html.loadActionMenuBtn(30,30);
        Markdown2Html.loadTableList();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.localhost) {//|| id == R.id.other
            if (id == currentMenuId) {
                return false;
            }
            currentMenuId = id;
            getDrawerLayout().closeDrawer(GravityCompat.START);
            return true;
        }
        else if(id == R.id.menu_setting){
            SetActivity.startSetActivity(this);
            return true;
        }
        else if (onOptionsItemSelected(item)) {
            getDrawerLayout().closeDrawer(GravityCompat.START);
        }
        return false;
    }

    @Override
    protected int getDefaultMenuItemId() {
        currentMenuId = R.id.localhost;
        return currentMenuId;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_helper:
                CommonMarkdownActivity.startHelper(this);
                return true;
            case R.id.menu_about:
                AboutActivity.startAboutActivity(this);
                return true;
            case R.id.menu_setting:
                SettingsActivity.startSettingsActivity(this);
                return true;
            case R.id.menu_update:
                initUpdate(true);
                return true;
            case R.id.other:
                AppContext.showSnackbar(getWindow().getDecorView(), "敬请期待");
                return true;
                default:break;
        }
        return super.onOptionsItemSelected(item);// || mCurrentFragment.onOptionsItemSelected(item);
    }

    private long customTime = 0;

    /**
     * 返回按钮
     */
    @Override
    public void onBackPressed() {
        if (getDrawerLayout().isDrawerOpen(GravityCompat.START)) {//侧滑菜单打开，关闭菜单
            getDrawerLayout().closeDrawer(GravityCompat.START);
            return;
        }

        if (mCurrentFragment != null && mCurrentFragment.onBackPressed()) {//如果Fragment有处理，则不据需执行
            return;
        }

        //没有东西可以返回了，剩下软件退出逻辑
        if (Math.abs(customTime - System.currentTimeMillis()) < 2000) {
            finish();
        } else {// 提示用户退出
            customTime = System.currentTimeMillis();
            Toast.showShort(mContext, "再按一次退出软件");
        }
    }


    /**
     * 更新检查
     * @param isShow
     */
    private void initUpdate(boolean isShow) {
        PgyUpdateManager.register(MainActivity.this,
                new UpdateManagerListener() {
                    @Override
                    public void onUpdateAvailable(final String result) {
                        final AppBean appBean = getAppBeanFromString(result);
                        if (appBean.getReleaseNote().startsWith("####")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.DialogTheme);
                            builder
                                    .setTitle("当前版本已经停用了")
                                    .setCancelable(false)
                                    .setMessage("更新到最新版?")
                                    .setNegativeButton("取消", (dialog, which) -> {
                                       finish();
                                    })
                                    .setPositiveButton("确定", (dialog1, which) -> {
                                        startDownloadTask(
                                                MainActivity.this,
                                                appBean.getDownloadURL());
                                        dialog1.dismiss();
                                    }).show();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.DialogTheme);
                            builder
                                    .setTitle("更新")
                                    .setMessage(appBean.getReleaseNote() + "")
                                    .setNegativeButton("先不更新", (dialog, which) -> {
                                        dialog.dismiss();
                                    })
                                    .setPositiveButton("更新", (dialog1, which) -> {
                                        startDownloadTask(
                                                MainActivity.this,
                                                appBean.getDownloadURL());
                                        dialog1.dismiss();
                                    }).show();

                        }
                    }

                    @Override
                    public void onNoUpdateAvailable() {
                        if (isShow) {
                            android.widget.Toast.makeText(application, "已经是最新版", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
