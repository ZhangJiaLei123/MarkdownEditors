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

package com.blxt.markdowneditors.model;

import android.os.Environment;
import android.support.annotation.NonNull;

import com.bigbai.mlog.LOG;
import com.blxt.markdowneditors.AppConfig;
import com.blxt.markdowneditors.entity.FileBean;
import com.blxt.markdowneditors.utils.Check;
import com.blxt.markdowneditors.utils.FileUtils;
import com.blxt.markdowneditors.utils.RxUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Model统一数据管理
 * Created by 沈钦赐 on 16/1/26.
 */
public class DataManager {



    private IFileModel mFileModel;

    public static DataManager getInstance() {
        return DataManagerInstance.getManager();
    }

    private DataManager() {
        mFileModel = FileModel.getInstance();
    }

    /**
     * 读取文件
     *
     * @param file the file path
     * @return the observable
     */
    public Observable<String> readFile(@NonNull File file) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                if (file == null) {
                    subscriber.onError(new IllegalStateException("文件获取失败：路径错误"));
                    return;
                }
                if (file.isDirectory()) {
                    subscriber.onError(new IllegalStateException("文件获取失败：不是文件"));
                    return;
                }
                if (!file.exists()) {
                    subscriber.onError(new IllegalStateException("文件获取失败：文件不存在"));
                    return;
                }

                subscriber.onNext(FileUtils.readFile(file));
            }
        })
                .compose(RxUtils.applySchedulersIoAndMainThread());
    }

    /**
     * 保存文件
     *
     * @param file    the file path
     * @param content the content
     * @return the observable
     */
    public Observable<Boolean> saveFile(@NonNull File file, @NonNull String content) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                if (file == null) {
                    subscriber.onError(new IllegalStateException("文件保存失败：路径错误"));
                    return;
                }

                boolean b = FileUtils.writeByte(file, content);
                subscriber.onNext(b);
            }
        }).compose(RxUtils.applySchedulersIoAndMainThread());

    }


    /***
     * 获取默认文件夹
     * @return
     */
    public List<FileBean> getDefaultPath(){
        List<FileBean> fileList = new ArrayList<>();
        String SdPath = Environment.getExternalStorageDirectory().getPath();

        // Log.i("获取默认文件夹" , SdPath);
        String[] defaultPath = new String[]{ "","tencent/QQfile_recv", "tencent/TIMfile_recv", "tencent/MicroMsg/Download", "DingTalk"};
        String[] defaultPathName = new String[]{ "根目录","QQ", "TIM", "微信", "钉钉"};

        // 添加默认文件到集合
        for (int i= 0; i < defaultPath.length; i++){
            FileBean fileBean = createFileBean(SdPath + "/" + defaultPath[i],
                    defaultPathName[i],true);
            if(fileBean != null) {
                fileList.add(fileBean);
            }
        }

        return fileList;
    }

    /**
     * 创建一个 FileBean
     * @param path
     * @param name
     * @param isDirectory
     * @return
     */
    public static FileBean createFileBean(String path,String name,boolean isDirectory){
        boolean isCreate = false;
        File file = new File(path.trim());
        if(!file.exists()){ // 跳过不存在的路径
            if(!isCreate){ // 不存在,是否创建
                return null;
            }
            else if(isDirectory) { // 创建文件夹
                file.mkdir();
            }else{ // 创建文件
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }

        FileBean fileBean = new FileBean();
        fileBean.absPath = path;
        fileBean.name = name;
        fileBean.isDirectory = isDirectory;
        fileBean.lastTime = new Date(file.lastModified()); // 最后修改时间

        return  fileBean;
    }

    /**
     * 获取文件列表，（md文件和文件夹）并转化为FileBean
     * Gets file list data.
     *
     * @param currentFolder the current folder
     * @return the file list data
     */
    public Observable<List<FileBean>> getFileListData(File currentFolder, String key) {
        File[] files = null;

        if (Check.isEmpty(key))//默认，文件夹和文件
        {
            if(AppConfig.isOnlyShowMd){
                files = currentFolder
                        .listFiles(file -> file.isDirectory() ||
                                file.getName().endsWith(".md") ||
                                file.getName().endsWith(".markdown") ||
                                file.getName().endsWith(".mdown"));
            }
            else{
                files = currentFolder
                        .listFiles(file -> file.isDirectory() ||
                                file.getName().endsWith(".h") ||
                                file.getName().endsWith(".c") ||
                                file.getName().endsWith(".java") ||
                                file.getName().endsWith(".txt") ||
                                file.getName().endsWith(".md") ||
                                file.getName().endsWith(".markdown") ||
                                file.getName().endsWith(".mdown"));
            }

        } else //搜索
        {
            if(AppConfig.isOnlyShowMd){
                files = currentFolder
                        .listFiles(file -> file.getName().contains(key) &&
                                (
                                file.getName().endsWith(".md") ||
                                file.getName().endsWith(".markdown") ||
                                file.getName().endsWith(".mdown")));//只显示md和文件夹
            }else{
                files = currentFolder
                        .listFiles(file -> file.getName().contains(key) &&
                                (
                                file.getName().endsWith(".h") ||
                                file.getName().endsWith(".c") ||
                                file.getName().endsWith(".java") ||
                                file.getName().endsWith(".txt") ||
                                file.getName().endsWith(".md") ||
                                file.getName().endsWith(".markdown") ||
                                file.getName().endsWith(".mdown")));//只显示md和文件夹
            }

        }

        if (files == null) {
            return getCommonObservable();
        }else{
            for(int i = 0; i < files.length ; i++){
                if(!AppConfig.isShowHideMkdir){ // 屏蔽隐藏文件/夹
                    if( files[i].getName().startsWith(".")){
                        files[i] = null;
                        continue;
                    }
                }
                if(AppConfig.isHideSystemMkdir){ // 屏蔽系统文件夹
                    if(files[i].isDirectory()){
                        if(compareFileByName(files[i])){
                            files[i] = null;
                        }
                    }
                }
            }

        }

        return Observable
                .from(files)
                .filter(file -> file != null)
//                .filter(file -> file.isDirectory() || file.getName().endsWith(".md"))
                .flatMap(file -> mFileModel.getFileBeanObservable(file)
                        .filter(bean -> bean != null))
//                .toList()
                .toSortedList(this::fileSort)
                .compose(RxUtils.applySchedulersIoAndMainThread());
    }

    /**
     * 文件复制
     * Copy file observable.
     *
     * @param beans      the beans 待复制的文件或者文件夹集合
     * @param targetPath the targetPath 目标目录
     * @return the observable
     */
    public Observable<FileBean> copyFile(List<FileBean> beans, String targetPath) {
        return Observable
                .from(beans)
//                .flatMap(bean -> mFileModel.getFileObservable(bean))
                .map(bean ->
                        FileUtils.copyFolder(bean.absPath, targetPath) ? bean : null
                )
                .map(bean -> {
                    if (bean == null) {
                        throw new IllegalStateException("复制失败了");
                    } else {
                        return bean;
                    }
                })
                .map(bean -> {//新路径改变
                    if (targetPath.endsWith(File.separator)) {
                        bean.absPath = targetPath + bean.name;
                    } else {
                        bean.absPath = targetPath + File.separator + bean.name;
                    }
                    return bean;
                })
                .compose(RxUtils.applySchedulersIoAndMainThread());
    }

    public Observable<FileBean> cutFile(List<FileBean> beans, String target) {
        return Observable
                .from(beans)
//                .flatMap(bean -> mFileModel.getFileObservable(bean))
                .map(bean ->
                        FileUtils.moveFolder(bean.absPath, target) ? bean : null
                )
                .map(bean -> {
                    if (bean == null) {
                        throw new IllegalStateException("剪切失败了");
                    } else {
                        return bean;
                    }
                })
                .map(bean -> {//新路径改变
                    if (target.endsWith(File.separator)) {
                        bean.absPath = target + bean.name;
                    } else {
                        bean.absPath = target + File.separator + bean.name;
                    }
                    return bean;
                })
                .compose(RxUtils.applySchedulersIoAndMainThread());
    }

    /**
     * 过去一个数据空回调
     * Gets common observable.
     *
     * @param <T> the type parameter
     * @return the common observable
     */
    @SuppressWarnings("unchecked")
    private <T> Observable<T> getCommonObservable() {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                subscriber.onNext((T) getNullList());
                subscriber.onCompleted();
            }
        });
    }

    /**
     * 获取一个空的List
     * Gets null list.
     *
     * @param <T> the type parameter
     * @return the null list
     */
    @SuppressWarnings("unchecked")
    private <T> T getNullList() {
        return (T) new ArrayList<>();
    }

    /**
     * 文件排序
     * File sort int.
     *
     * @param file1 the file 1
     * @param file2 the file 2
     * @return the int
     */
    private int fileSort(FileBean file1, FileBean file2) {
        //大体按照时间排序
        if ((file1.isDirectory && file2.isDirectory) || (!file1.isDirectory && !file2.isDirectory)) {
            return file1.name.compareTo(file2.name);
//            return -1 * file1.lastTime.compareTo(file2.lastTime);
        }
        //如果是文件和文件夹，则文件拍在前面
        if (file1.isDirectory && !file2.isDirectory) {
            return 1;
        } else {
            return -1;
        }
    }

    private static class DataManagerInstance {
        public static DataManager manager = new DataManager();

        public static DataManager getManager() {
            return manager;
        }
    }

    /**
     * 判断文件是否符合类型
     * @return true 符合比较类型 false 不符合比较类型
     */
    private boolean compareFileByName(File file){

        for(String s : AppConfig.hideFileRm){
            if(file.getName().equals(s)){
                return true;
            }
        }
        return false;
    }
}
