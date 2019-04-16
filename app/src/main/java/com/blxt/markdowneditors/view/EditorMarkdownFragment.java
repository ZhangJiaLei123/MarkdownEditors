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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.bigbai.mfileutils.FileUtils;
import com.bigbai.mfileutils.spControl.FalBoolean;
import com.bigbai.mfileutils.spControl.FalInt;
import com.bigbai.mfileutils.spControl.spBaseControl;
import com.bigbai.mlog.LOG;
import com.blxt.markdowneditors.base.BaseFragment;
import com.blxt.markdowneditors.event.RxEvent;
import com.blxt.markdowneditors.utils.UnzipFromAssets;
import com.blxt.markdowneditors.utils.mPermissionsUnit;
import com.blxt.markdowneditors.widget.MarkdownPreviewView;
import com.md2html.Markdown2Html;

import java.io.IOException;
import java.util.Calendar;

import butterknife.Bind;
import ren.qinc.markdowneditors.R;

import static android.content.Context.MODE_PRIVATE;
import static me.drakeet.library.ui.CrashListAdapter.TAG;

/**
 * 编辑预览界面
 * Created by 沈钦赐 on 16/1/21.
 */
public class EditorMarkdownFragment extends BaseFragment {
    View view;
    @Bind(R.id.markdownView)
    protected MarkdownPreviewView mMarkdownPreviewView;
    @Bind(R.id.title)
    protected TextView mName;
    private String mContent;

    private WebView mainViewWeb;


    public EditorMarkdownFragment() {
    }


    public static EditorMarkdownFragment getInstance() {
        EditorMarkdownFragment editorFragment = new EditorMarkdownFragment();
        return editorFragment;
    }


    @Override
    public boolean hasNeedEvent(int type) {
        //接受刷新数据
        return type == RxEvent.TYPE_REFRESH_DATA;
    }

    boolean isPageFinish = false;

    @Override
    public void onEventMainThread(RxEvent event) {
        if (event.isTypeAndData(RxEvent.TYPE_REFRESH_DATA)) {
            //页面还没有加载完成
            mContent = event.o[1].toString();
            mName.setText(event.o[0].toString());
            if (isPageFinish){
               // mMarkdownPreviewView.parseMarkdown(mContent, true);
                md2htmlString(mContent);
            }

        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_markdown;
    }

    @Override
    public void onCreateAfter(Bundle savedInstanceState) {
        mMarkdownPreviewView.setOnLoadingFinishListener(() -> {
            if (!isPageFinish && mContent != null)//
             {
               //  mMarkdownPreviewView.parseMarkdown(mContent, true);
                 md2htmlString(mContent);
             }


            isPageFinish = true;
        });
    }

    @Override
    public boolean hasMenu() {
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_editor_preview_frag, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }


    String sdCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
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
        mainViewWeb = rootView.findViewById(R.id.mainViewWeb);

        SP = getContext().getSharedPreferences("com.bigbai.mdview.preferences", MODE_PRIVATE);
        spC = new spBaseControl(SP);
        oldDay = new FalInt(SP,"lastDay",20181220);


        //支持javascript
        mainViewWeb.getSettings().setJavaScriptEnabled(true);
        // 设置可以支持缩放
        mainViewWeb.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        mainViewWeb.getSettings().setBuiltInZoomControls(true);
        //扩大比例的缩放
        mainViewWeb.getSettings().setUseWideViewPort(true);
        //自适应屏幕
        mainViewWeb.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mainViewWeb.getSettings().setLoadWithOverviewMode(true);
        initDataM();
    }

    void initDataM() {
        // 权限检查和初始文件解压
        mPermissions.setActivity(getActivity());

        isNewResult = new FalBoolean(SP,"isNewResult",false);
        isNewApp = new FalBoolean(SP,"isNewApp",true);

        // 获取权限
        //verifyStoragePermissions(this);
        mPermissions.setActivity(getActivity())
                .setDefaultDialog(true)
                .setPermissions(PERMISSIONS_STORAGE)
                .setCallback(new mPermissionsUnit.PermissionCheckCallback() {
                    @Override
                    public void onRequest() {
                        LOG.i(TAG, "权限请求...");
                    }

                    @Override
                    public void onGranted() {
                        //  initView();
                        LOG.i(TAG, "权限授予...");
                    }

                    @Override
                    public void onGrantSuccess() {
                        LOG.i(TAG, "获取权限成功");
                        //  initView();
                    }

                    @Override
                    public void onGrantFail() {
                        LOG.i(TAG, "权限获取失败");
                    }
                }).checkPermission();

        Calendar calendar = Calendar.getInstance();
        int timeInt = calendar.get(Calendar.YEAR) * 10000
                + calendar.get(Calendar.DAY_OF_MONTH) * 100
                + calendar.get(Calendar.HOUR_OF_DAY);


        boolean isRes = isNewResult.getValue();

        // 首次运行解压资源
        if ( (!isNewApp.getValue() || spC.getFirstRun()) || !isRes ) {
            LOG.i("解压资源");

            try {
                UnzipFromAssets.unZip(getContext(), "HtmlPlugin.zip", sdCardRoot + "/HtmlPlugin/");
                isReadLockStyle = true;
                isNewApp.setValue(true);// 标更新
                isNewResult.setValue(true);
            } catch (IOException e) {
                e.printStackTrace();
              //  TipToast.showToast(this, "资源解压失败,部分风格可能无法生效");
                isReadLockStyle = false;
            }

        } else {
            LOG.i("已是最新版");
        }

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

    /**
     * md转 html
     *
     * @param strMd md代码
     */
    void md2htmlString(String strMd) {
        Markdown2Html.clear();
        Markdown2Html.setMdText(strMd);
        Markdown2Html.analysis();
        String str = Markdown2Html.getHtmlCode();
        mainViewWeb.freeMemory();
        mainViewWeb.loadDataWithBaseURL(null, str, "text/html", "utf-8", null);
        LOG.i(str);
    }

}
