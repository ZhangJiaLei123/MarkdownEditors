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

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.blxt.markdowneditors.AppConfig;
import com.blxt.markdowneditors.R;
import com.blxt.markdowneditors.base.BaseFragment;
import com.blxt.markdowneditors.event.RxEvent;
import com.blxt.markdowneditors.utils.CheckNet;
import com.blxt.markdowneditors.utils.FileUtils;
import com.blxt.markdowneditors.utils.MD5Utils;
import com.md2html.CallBack;
import com.md2html.Markdown2Html;

import java.io.File;

import static com.blxt.markdowneditors.view.FolderFragment.file_select;


/**
 * md预览界面
 *
 * @author 沈钦赐
 * @date 16/1/21
 */
public class MdPreviewFragment extends BaseFragment implements CallBack {
    View view;
    final String TAG = "编辑预览界面";
    String title = "MdReader";
    private String mContent;
    /** 用于显示md预览的web视图 */
    private WebView webView;
    static Markdown2Html md;
    static WebView webView_show;
    static boolean isShowToast = false;
    public static final int MSG_START_ANALYSIS = 99; //开始解析
    public static final int MSG_UP_WEB_VIEW = 100; // 更新webview
    public static final int MSG_CLEAR = 101; // 清理缓存

    static private ProgressBar pBarWebPreview;

    private static boolean isShowWeb = false;

    public MdPreviewFragment() {
    }


    public static MdPreviewFragment getInstance() {
        MdPreviewFragment editorFragment = new MdPreviewFragment();
        return editorFragment;
    }

    @Override
    public void resume() {
        Log.i(TAG,"onResume");
    }

    @Override
    public boolean hasNeedEvent(int type) {
        //接受刷新数据
        return type == RxEvent.TYPE_REFRESH_DATA;
    }

    boolean isPageFinish = false;

    static File fileMarkdown;

    /**
     * 事件分发接收
     * @param event
     */
    @Override
    public void onEventMainThread(RxEvent event) {
        if (event.isTypeAndData(RxEvent.TYPE_REFRESH_DATA)
           || event.isTypeAndData(RxEvent.TYPE_REFRESH_NOTIFY)) {
            //页面还没有加载完成
            Log.i("预览", "事件");
            if(!isShowWeb) // 文本改变后,才刷新
            {
                Log.i("预览", "开始解析");
                mContent = event.o[1].toString();
                title = event.o[0].toString();

                String strName = MD5Utils.Str2MD5(file_select.getPath());
                fileMarkdown = new File( getContext().getExternalCacheDir() + "/" + strName + ".html");

                if(fileMarkdown.exists()){
                    Log.i("预览", "加载历史");
                    loadHtmlFile(this.webView, fileMarkdown);
                    isShowWeb = true;
                }
                else{
                    Log.i("预览", "开始解析");
                    if(md2htmlString(this.webView, mContent)){
                        isShowWeb = true;
                    }
                }

            }
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_markdown;
    }

    @Override
    public void onCreateAfter(Bundle savedInstanceState) {
//        if (!isPageFinish && mEtContent != null
//                && isChangeContent || !isShowWeb)//
//         {
//             if(md2htmlString(mEtContent)){
//                isShowWeb = true;
//             }
//             isChangeContent = false;
//
//         }
        isPageFinish = true;
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


    @Override
    public void initData() {

        // 如果有网络，就开启下载
        AppConfig.config.isCheckCache = CheckNet.CheckNetworkState(getActivity());
        // 开启优先使用网络图片
        AppConfig.config.isFirstUrl = CheckNet.CheckNetworkState(getActivity());

        webView = rootView.findViewById(R.id.mainViewWeb);
        //自适应屏幕
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
        enableJavascript();
        // 设置可以支持缩放
        webView.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        webView.getSettings().setBuiltInZoomControls(true);
        //扩大比例的缩放
        webView.getSettings().setUseWideViewPort(true);

        WebSettings webSettings=webView.getSettings();
        //允许webview对文件的操作
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);

        //用于js调用Android
        webSettings.setJavaScriptEnabled(true);
        //设置编码方式
        webSettings.setDefaultTextEncodingName("utf-8");
        webView.setWebChromeClient(new chromClient());


        pBarWebPreview = rootView.findViewById(R.id.pBar_web_preview);

    }
    //支持javascript
    private void enableJavascript() {
        webView.getSettings().setJavaScriptEnabled(true);// 能够执行Javascript脚本
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
    }


    /** 加载html文件 */
    public static boolean loadHtmlFile(WebView webView, File file){
        isShowToast = false;
        Message message_star = new Message();
        message_star.what = MSG_START_ANALYSIS;
        message_star.obj = webView;
        handler.sendMessage(message_star);

        // 读取文件
        String str =  FileUtils.readFileByLines(file);
        Message message = new Message();
        message.what = MSG_UP_WEB_VIEW;
        message.obj = str;
        handler.sendMessage(message);

        return true;
    }



    /**
     * md转 html
     *
     * @param strMd md代码
     */
    public boolean md2htmlString(WebView webView, String strMd) {

        if (strMd == null)
        {
            return false;
        }
        isShowToast = true;

        if(md != null){
            md.clear();
        }

        Message message_star;
        message_star = new Message();
        message_star.what = MSG_START_ANALYSIS;
        message_star.obj = webView;
        handler.sendMessage(message_star);

        // 多线程
        AppConfig.config.multithreading = AppConfig.isMultithreading;
        // 表情
        AppConfig.config.isFace = AppConfig.isFullFace;

        md = new Markdown2Html(AppConfig.config);
        md.setMdText(strMd);
        md.setTitle(title);
        md.setCallBack(this);
        md.analysis();


        return true;
    }

    @Override
    public void analysisOK() {
        Log.i(TAG,"解析完成");
        // 如果是非多线程，就开始组装
        if(!AppConfig.config.multithreading) {
            md.makeHtml();
        }

    }

    @Override
    public void end() {
        if(AppConfig.config.multithreading) {
            md.makeHtml();
        }
        String str = md.getResult();
        Message message = new Message();
        message.what = MSG_UP_WEB_VIEW;
        message.obj = str;
        handler.sendMessage(message);
        if(fileMarkdown != null) {
            FileUtils.writeByte(fileMarkdown, str);
        }

    }
    @Override
    public void add(Object o) {

    }

    @Override
    public void sub(Object o) {

    }



    private class chromClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if(newProgress==100){
                //页面加载完成执行的操作
                Log.i("页面加载完成","");
            }
            super.onProgressChanged(view, newProgress);
        }
    }


    /***
     * Md处理handler
     */
    @SuppressLint("HandlerLeak")
    static Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_START_ANALYSIS: // 开始解析
                    webView_show =  (WebView)msg.obj;
                    webView_show.freeMemory();
                    if(pBarWebPreview != null && isShowToast) {
                        pBarWebPreview.setVisibility(View.VISIBLE);
                    }

                    break;
                case MSG_UP_WEB_VIEW: // 解析完成
                    String str = (String)msg.obj;
                    webView_show.loadDataWithBaseURL(null, str, "text/html", "utf-8", null);

                    if(pBarWebPreview != null) {
                        pBarWebPreview.setVisibility(View.GONE);
                    }

                    isShowWeb = false;
                    break;
                case MSG_CLEAR: // 清理缓存
                    if(md != null){
                        md.clearCache();
                        md.clear();
                    }

                    String strName = MD5Utils.Str2MD5(file_select.getPath());
                    fileMarkdown = new File( EditorActivity.Cachepath + strName + ".html");
                    if(fileMarkdown.exists()) {
                        Log.i("清理缓存","ok");
                        fileMarkdown.delete();
                    }
                    else{
                        Log.i("清理缓存", "no");
                    }

                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

    /***
     * 返回按钮
     * @return
     */
    @Override
    public boolean onBackPressed() {
        super.onBackPressed();
        Log.i(TAG,"预览页返回");
        return true;
    }

}
