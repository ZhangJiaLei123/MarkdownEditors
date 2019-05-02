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
package com.blxt.markdowneditors;

import android.content.Context;
import android.content.SharedPreferences;

import com.bigbai.mfileutils.spControl.FalBoolean;
import com.m2h.model.Config;


/**
 * 相关配置(后续用到)
 *
 * @author Zhang
 */
public class AppConfig {
    public static Config config = new Config();
    /** 使用全面屏 */
    public static Boolean swIsFullScreen = true;
    /** 只显示md文件 */
    public static boolean isOnlyShowMd = true;
    /** 显示更多文件夹,如QQ文件夹,微信文件夹等 */
    public static boolean isShowMoreDir = true;
    /** 显示隐藏文件夹 */
    public static boolean isShowHideMkdir = true;
    /** 隐藏系统文件夹 */
    public static boolean isHideSystemMkdir = true;
    /** 文件夹屏蔽列表 */
    public static String[] hideFileRm = {"360","10086","DCIM","MIUI","Movies","Music","Ccb","MIUI"};
    /** 多线程解析 */
    public static boolean isMultithreading = false;
    /** 支持全部表情 */
    public static boolean isFullFace = false;

    /** 是否有网络 */
    public static Boolean isNetwork = false;

    public static void initAppConfig(SharedPreferences SP, Context context){

        FalBoolean f1 =  new FalBoolean(SP, context.getResources().getString( R.string.sw_is_only_md),true );
        FalBoolean f2 =  new FalBoolean(SP, context.getResources().getString( R.string.sw_is_show_more_mkdir),true );
        FalBoolean f3 =  new FalBoolean(SP, context.getResources().getString( R.string.sw_is_hide_system_mkdir),true );
        FalBoolean f4 =  new FalBoolean(SP, context.getResources().getString( R.string.sw_is_show_hide_mkdir),false );

        AppConfig.isOnlyShowMd = f1.getValue();
        AppConfig.isShowMoreDir = f2.getValue();
        AppConfig.isHideSystemMkdir = f3.getValue();
        AppConfig.isShowHideMkdir = f4.getValue();
//        AppConfig.isShowMoreDir = swIsShowMoreMkdir.getFalData().getValue();
//        AppConfig.isHideSystemMkdir = swIsHideSystemMkdir.getFalData().getValue();
//        AppConfig.isShowHideMkdir = swIsShowHideMkdir.getFalData().getValue();
    }
//    //==================缓存文件相关==================
//    // 设置文件sharedPreference的文件名字
//    public static String SETTING_FILE_NAME = "setting_file";
//
//    //全局变量缓存目录,一般不删除
//    public static String GLOBA_CACHE_NAME = "globa_file";
//
//    //文件缓存(程序缓存数据保存在这里,可以删除)
//    public static String FILE_CACHE = "cacle_file";
//
//    //数据库保存的文件
//    public static String DB_NAME = "blxt.db";
//
//    //null:全部打印,不然{1,2}就打印1和2为flag的api请求
//    public static final int[] SHOW_LOG = null;
}
