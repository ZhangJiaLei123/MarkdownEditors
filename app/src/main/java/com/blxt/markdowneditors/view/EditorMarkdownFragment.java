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

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.blxt.markdowneditors.R;
import com.blxt.markdowneditors.base.BaseFragment;
import com.blxt.markdowneditors.event.RxEvent;
import com.md2html.Markdown2Html;

import java.io.File;

import butterknife.Bind;

import static com.blxt.markdowneditors.view.EditorFragment.isChangeContent;


/**
 * 编辑预览界面
 * Created by 沈钦赐 on 16/1/21.
 */
public class EditorMarkdownFragment extends BaseFragment {
    View view;
    @Bind(R.id.title)
    protected TextView mName;
    private String mContent;
    /** 用于显示md预览的web视图 */
    private WebView webView;

    private boolean isShowWeb = false;

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
            if(isChangeContent || !isShowWeb) // 文本改变后,才刷新
            {
                mContent = event.o[1].toString();
                mName.setText(event.o[0].toString());
                if (isPageFinish){
                    // mMarkdownPreviewView.parseMarkdown(mEtContent, true);
                    if(md2htmlString(mContent)){
                        isShowWeb = true;
                    }
                }
                isChangeContent = false;
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
        webView = rootView.findViewById(R.id.mainViewWeb);

        // 设置可以支持缩放
        webView.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        webView.getSettings().setBuiltInZoomControls(true);
        //扩大比例的缩放
        webView.getSettings().setUseWideViewPort(true);
        //自适应屏幕
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
        enableJavascript();

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

    }
    //支持javascript
    private void enableJavascript() {
        webView.getSettings().setJavaScriptEnabled(true);// 能够执行Javascript脚本
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
    }

    /**
     * md转 html
     *
     * @param strMd md代码
     */
    boolean md2htmlString(String strMd) {
        if (strMd == null)
        {
            return false;
        }
        Markdown2Html.clear();
        Markdown2Html.setMdText(strMd);
        Markdown2Html.analysis();
        String str = Markdown2Html.getHtmlCode();
        webView.freeMemory();
        webView.loadDataWithBaseURL(null, str, "text/html", "utf-8", null);

        return true;
    }

    private class chromClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if(newProgress==100){
                //页面加载完成执行的操作
                String path= "file://"+ Environment.getExternalStorageDirectory()+ File.separator+"123.jpg";
                String action="javascript:aa('"+path+"')";
                Log.i("页面加载完成",action);
              //  runWebView(action);
            }
            super.onProgressChanged(view, newProgress);
        }
    }


}
