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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.blxt.markdowneditors.AppContext;
import com.blxt.markdowneditors.R;
import com.blxt.markdowneditors.base.BaseApplication;
import com.blxt.markdowneditors.base.BaseFragment;
import com.blxt.markdowneditors.base.mvp.IMvpView;
import com.blxt.markdowneditors.engine.PerformEditable;
import com.blxt.markdowneditors.engine.PerformInputAfter;
import com.blxt.markdowneditors.event.RxEvent;
import com.blxt.markdowneditors.event.RxEventBus;
import com.blxt.markdowneditors.presenter.EditorFragmentPresenter;
import com.blxt.markdowneditors.presenter.IEditorFragmentView;
import com.blxt.markdowneditors.utils.MD5Utils;
import com.blxt.markdowneditors.utils.SystemUtils;
import com.blxt.markdowneditors.widget.MyEditText;

import java.io.File;

import butterknife.Bind;
import de.mrapp.android.bottomsheet.BottomSheet;
import ren.qinc.edit.PerformEdit;

import static com.blxt.markdowneditors.view.FolderFragment.file_select;


/**
 * 编辑界面
 * @change blxt
 * @author 沈钦赐
 * @date 19/04/17
 */
public class EditorFragment extends BaseFragment implements IEditorFragmentView, View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    public static final String TAG = "EditorFragment";
    public static final String FILE_PATH_KEY = "FILE_PATH_KEY";
    /** 编辑内容改变 */
    public static boolean isChangeContent = true;
    @Bind(R.id.title)
    protected EditText mName;
    @Bind(R.id.content)
    protected MyEditText mEtContent;
    /** 只读 */
    private CheckBox cbIsOnlyRead;


    private EditorFragmentPresenter mPresenter;

    private PerformEditable mPerformEditable;
    private PerformEdit mPerformEdit;
    private PerformEdit mPerformNameEdit;

    public EditorFragment() {
        isChangeContent = true;
    }

    public static EditorFragment getInstance(String filePath) {
        EditorFragment editorFragment = new EditorFragment();
        Bundle bundle = new Bundle();
        bundle.putString(FILE_PATH_KEY, filePath);
        editorFragment.setArguments(bundle);
        return editorFragment;
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_editor;
    }

    /***
     * 设置文本监听等
     * @param savedInstanceState
     */
    @Override
    public void onCreateAfter(Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        String fileTemp = arguments.getString(FILE_PATH_KEY);
        if (fileTemp == null) {
            Toast.makeText(AppContext.context(), "路径参数有误！", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(fileTemp);
        //创建新文章
        mPresenter = new EditorFragmentPresenter(file);
        mPresenter.attachView(this);

        //代码格式化或者插入操作
        mPerformEditable = new PerformEditable(mEtContent);

        //撤销和恢复初始化
        mPerformEdit = new PerformEdit(mEtContent) {
            @Override
            protected void onTextChanged(Editable s) {
                //文本改变
                mPresenter.textChange();
                deleteTmp();
                isChangeContent = true;
                Log.i("文本改变", "");
            }
        };

        mPerformNameEdit = new PerformEdit(mName) {
            @Override
            protected void onTextChanged(Editable s) {
                //文本改变
                mPresenter.textChange();
                // 内容改变后，就删除缓存
                deleteTmp();
                Log.i("文本改变", "");
                isChangeContent = true;
            }
        };

        //文本输入监听(用于自动输入)
        PerformInputAfter.start(mEtContent);

        //装置数据
        if (file.isFile()) {
            mPresenter.loadFile();
        }
    }

    /***
     * 删除缓存
     */
    public void deleteTmp(){
        if(file_select != null && file_select.getPath() != null){
            String strName = MD5Utils.Str2MD5(file_select.getPath());
            File f = new File( getContext().getExternalCacheDir() + "/" + strName + ".html");
            if(f.exists()){
                Log.i("删除",f.getPath());
                f.delete();
            }
        }
    }

    /***
     * 刷新
     */
    @Override
    public void resume() {
        Log.i(TAG,"onResume");
    }

    @Override
    public void initData() {

        cbIsOnlyRead = rootView.findViewById(R.id.cb_is_only_read);
        cbIsOnlyRead.setOnCheckedChangeListener(this);

        int hight =SystemUtils.getWindowsH(getContext());
        mEtContent.setMinHeight(hight);
        mEtContent.setDrawLine(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPresenter != null) {
            mPresenter.detachView();//VP分离
        }
        mPresenter = null;
    }

    @Override
    public void otherSuccess(int flag) {
        switch (flag) {
            case CALL_EXIT:
                getActivity().finish();
                break;
            case CALL_NO_SAVE:
                noSave();
                break;
            case CALL_SAVE:
                saved();
                break;
                default:
        }
    }

    @Override
    public void onFailure(int errorCode, String message, int flag) {
        switch (flag) {
            case CALL_SAVE:
            case CALL_LOAOD_FILE:
                BaseApplication.showSnackbar(mEtContent, message);
                break;
            default:
                BaseApplication.showSnackbar(mEtContent, message);
                break;
        }
    }

    @Override
    public void showWait(String message, boolean canBack, int flag) {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (!(activity instanceof IMvpView)) {
            return;
        }
        IMvpView iMvpView = (IMvpView) activity;
        iMvpView.showWait(message, canBack, flag);

    }

    @Override
    public void hideWait(int flag) {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (!(activity instanceof IMvpView)) {
            return;
        }
        IMvpView iMvpView = (IMvpView) activity;
        iMvpView.hideWait(flag);
    }


    public PerformEditable getPerformEditable() {
        return mPerformEditable;
    }

    @Override
    public boolean hasMenu() {
        return true;
    }

    //菜单
    private MenuItem mActionSave;


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_editor_frag, menu);
        mActionSave = menu.findItem(R.id.action_save);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_share://分享
                shareMenu();
                return true;
            case R.id.action_undo://撤销
                mPerformEdit.undo();
                return true;
            case R.id.action_redo://重做
                mPerformEdit.redo();
                return true;
            case R.id.action_save://保存
                mPresenter.save(mName.getText().toString().trim(), mEtContent.getText().toString().trim());
                return true;
                default:
        }

        return super.onOptionsItemSelected(item);
    }

    private void shareMenu() {
        SystemUtils.hideSoftKeyboard(mEtContent);
        if (mName.getText().toString().isEmpty()) {
            AppContext.showSnackbar(mName, "当前标题为空");
            return;
        }
        if (mEtContent.getText().toString().isEmpty()) {
            AppContext.showSnackbar(mEtContent, "当前内容为空");
            return;
        }

        mPresenter.save(mName.getText().toString(), mEtContent.getText().toString());

        BottomSheet.Builder builder = new BottomSheet.Builder(getActivity());
//        builder.setTitle(R.string.bottom_sheet_title);
        builder.addItem(0, R.string.share_copy_text);
        builder.addItem(1, R.string.share_text);
        builder.addItem(2, R.string.share_md);
//        builder.addDivider();
//        builder.addItem(3, R.string.share_html);
//        builder.addItem(4, R.string.share_png);
        builder.setOnItemClickListener((parent, view, position, id) -> {
            switch ((int) id) {
                case 0://复制
                    shareCopyText();
                    break;
                case 1://分享文本
                    shareText();
                    break;
                case 2://分享md文件
                    shareMD();
                    break;
                    default:
            }

        });
        BottomSheet bottomSheet = builder.create();
        bottomSheet.show();
    }

    private void shareCopyText() {
        SystemUtils.copyToClipBoard(getActivity(), mEtContent.getText().toString());
    }

    private void shareText() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, mEtContent.getText().toString());
        shareIntent.setType("text/plain");

        BottomSheet.Builder builder = new BottomSheet.Builder(getActivity(), R.style.AppTheme);
        builder.setIntent(getActivity(), shareIntent);
        BottomSheet bottomSheet = builder.create();
        bottomSheet.show();
    }

    private void shareMD() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(mPresenter.getMDFile()));
        shareIntent.setType("*/*");

//        startActivity(Intent.createChooser(share,"Share Image"));
        BottomSheet.Builder builder = new BottomSheet.Builder(getActivity());
        builder.setIntent(getActivity(), shareIntent);
        BottomSheet bottomSheet = builder.create();
        bottomSheet.show();

//        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//        Uri photoUri = FileProvider.getUriForFile(
//                getContext(),
//                "com.blxt.markdowneditors.view",
//                mPresenter.getMDFile());
//        shareIntent.putExtra(Intent.EXTRA_STREAM, photoUri);
//        shareIntent.setType("*/*");
//
//        BottomSheet.Builder builder = new BottomSheet.Builder(getActivity());
//        builder.setIntent(getActivity(), shareIntent);
//        BottomSheet bottomSheet = builder.create();
//        bottomSheet.show();
    }

    @Override
    public void onReadSuccess(@NonNull String name, @NonNull String content) {
        mPerformNameEdit.setDefaultText(name.substring(0, name.lastIndexOf(".")));
        mPerformEdit.setDefaultText(content);
        if (content.length() > 0) {
            //切换到预览界面
//            RxEventBus.getInstance().send(new RxEvent(RxEvent.TYPE_SHOW_PREVIEW, mName.getText().toString(), mEtContent.getText().toString()));
        }
    }

    public void noSave() {
        if (mActionSave == null) {
            return;
        }
        mActionSave.setIcon(R.drawable.ic_action_unsave);
    }

    public void saved() {
        if (mActionSave == null) {
            return;
        }
        mActionSave.setIcon(R.drawable.ic_action_save);
    }


    @Override
    public boolean hasNeedEvent(int type) {
        //接受刷新数据
        return type == RxEvent.TYPE_REFRESH_NOTIFY;
    }

    @Override
    public void onEventMainThread(RxEvent event) {
        if (event.isType(RxEvent.TYPE_REFRESH_NOTIFY)) {
            //刷新markdown渲染
            RxEventBus.getInstance().send(new RxEvent(RxEvent.TYPE_REFRESH_DATA, mName.getText().toString(), mEtContent.getText().toString()));
        }
    }

    @Override
    public boolean onBackPressed() {
        // Log.i("返回按钮","EditorFragment");
        if (mPresenter.isSave()) {
            return false;
        }
        onNoSave();
        super.onBackPressed();
        return true;
    }


    private void onNoSave() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        builder.setMessage("当前文件未保存，是否退出?");
        builder.setNegativeButton("不保存", (dialog, which) -> {
            getActivity().finish();

        }).setNeutralButton("取消", (dialog, which) -> {
            dialog.dismiss();

        }).setPositiveButton("保存", (dialog, which) -> {
            mPresenter.saveForExit(mName.getText().toString().trim(), mEtContent.getText().toString().trim(), true);

        }).show();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();

        switch (id){
            case R.id.cb_is_only_read:
                mEtContent.setEnabled(!isChecked);
                mName.setEnabled(!isChecked);
                break;
            default:
        }

    }
}
