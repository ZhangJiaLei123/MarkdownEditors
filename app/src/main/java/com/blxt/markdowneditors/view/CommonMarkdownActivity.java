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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.blxt.markdowneditors.utils.Check;
import com.blxt.markdowneditors.utils.SystemUtils;

import java.io.InputStream;

import butterknife.Bind;
import ren.qinc.markdowneditors.R;
import com.blxt.markdowneditors.base.BaseToolbarActivity;
import com.blxt.markdowneditors.widget.MarkdownPreviewView;


/**
 * 通用MarkdownEditor查看器
 *
 * @author 沈钦赐
 * @date 16/6/30
 */
public class CommonMarkdownActivity extends BaseToolbarActivity implements MarkdownPreviewView.OnLoadingFinishListener {
    private static final String CONTENT = "CONTENT";
    private static final String TITLE = "TITLE";
    @Bind(R.id.markdownView)
    protected MarkdownPreviewView mMarkdownPreviewView;
    private String mTitle;
    private String mContent;

    public static void startMarkdownActivity(Context context, String title, String content) {
        Intent intent = new Intent(context, CommonMarkdownActivity.class);
        intent.putExtra(CONTENT, content);
        intent.putExtra(TITLE, title);
        context.startActivity(intent);
    }

    public static void startHelper(Context context) {
        InputStream inputStream = context.getResources().openRawResource(R.raw.syntax_hepler);
        startMarkdownActivity(context, context.getString(R.string.action_helper), new String(SystemUtils.readInputStream(inputStream)));
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_common_markdown;
    }

    @Override
    public void onCreateAfter(Bundle savedInstanceState) {
        mTitle = getIntent().getStringExtra(TITLE);
        mContent = getIntent().getStringExtra(CONTENT);
        Check.CheckNull(mTitle, "标题不能为空");
        Check.CheckNull(mContent, "内容不能为空");
        mMarkdownPreviewView.setOnLoadingFinishListener(this);

    }

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    protected String getTitleString() {
        return getIntent().getStringExtra(TITLE);
    }

    boolean flag = true;

    @Override
    public void onLoadingFinish() {
        if (flag) {
            flag = false;
            mMarkdownPreviewView.parseMarkdown(mContent, true);
        }
    }

}
