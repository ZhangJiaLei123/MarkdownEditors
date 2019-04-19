package com.blxt.markdowneditors.base;

import android.view.View;

/**
 * 基础ViewHolder
 * @author Zhang
 */
public abstract class BaseViewHolder {
    public View viewRoot;

    public BaseViewHolder(View viewRoot) {
        this.viewRoot = viewRoot;

        findViewById();
        initUI();
        initData();
    }

    /**
     * findViewById
     *
     */
    protected abstract void findViewById();

    /**
     * initUI
     * 初始化UI,在findViewById()后执行,
     * 一般用于添加监听,和填充初始UI数据
     */
    protected abstract void initUI();

    /**
     * initData
     * 初始化数据,在initUI()后执行
     */
    protected abstract void initData();

}
