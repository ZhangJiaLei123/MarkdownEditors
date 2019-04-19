package com.blxt.markdowneditors.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;

import com.blxt.markdowneditors.AppConfig;
import com.blxt.markdowneditors.R;
import com.blxt.markdowneditors.base.BaseViewHolder;
import com.blxt.markdowneditors.utils.Toast;
import com.blxt.markdowneditors.widget.MySwitch;

/**
 * @author Zhang
 */
public class SetActivity extends AppCompatActivity {

    ViewHolder viewHolder;
    Context context;
    class ViewHolder extends BaseViewHolder implements CompoundButton.OnCheckedChangeListener {

        private MySwitch swIsOnlyMd;
        private MySwitch swIsShowMoreMkdir;
        private MySwitch swIsHideSystemMkdir;
        private MySwitch swIsShowHideMkdir;
        private MySwitch swIsUseTtf;





        public ViewHolder(View viewRoot) {
            super(viewRoot);
        }

        @Override
        protected void findViewById() {
            swIsOnlyMd = viewRoot.findViewById(R.id.sw_is_only_md);
            swIsShowMoreMkdir = viewRoot.findViewById(R.id.sw_is_show_more_mkdir);
            swIsHideSystemMkdir = viewRoot.findViewById(R.id.sw_is_hide_system_mkdir);
            swIsShowHideMkdir = viewRoot.findViewById(R.id.sw_is_show_hide_mkdir);
            swIsUseTtf = viewRoot.findViewById(R.id.sw_is_use_ttf);
        }

        @Override
        protected void initUI() {
            swIsOnlyMd.setOnCheckedChangeListener(this);
            swIsShowMoreMkdir.setOnCheckedChangeListener(this);
            swIsHideSystemMkdir.setOnCheckedChangeListener(this);
            swIsShowHideMkdir.setOnCheckedChangeListener(this);
            swIsUseTtf.setOnCheckedChangeListener(this);
        }

        @Override
        protected void initData() {
            AppConfig.isOnlyShowMd = swIsOnlyMd.getFalData().getValue();
            AppConfig.isOnlyShowMoreDir = swIsShowMoreMkdir.getFalData().getValue();
            AppConfig.isHideSystemMkdir = swIsHideSystemMkdir.getFalData().getValue();
            AppConfig.isShowHideMkdir = swIsShowHideMkdir.getFalData().getValue();
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int id = buttonView.getId();

            switch (id){
                case R.id.sw_is_only_md:
                    AppConfig.isOnlyShowMd = swIsOnlyMd.getFalData().getValue();
                    break;
                case R.id.sw_is_show_more_mkdir:
                    AppConfig.isOnlyShowMoreDir = swIsShowMoreMkdir.getFalData().getValue();
                    break;
                case R.id.sw_is_hide_system_mkdir:
                    AppConfig.isHideSystemMkdir = swIsHideSystemMkdir.getFalData().getValue();
                    break;
                case R.id.sw_is_show_hide_mkdir:
                    AppConfig.isShowHideMkdir = swIsShowHideMkdir.getFalData().getValue();
                    break;
                case R.id.sw_is_use_ttf:
                    Toast.showShort(context,"暂未开发,敬请期待");
                    break;
                    default:
            }
        }
    }

    public static void startSetActivity(Context context) {
        Intent intent = new Intent(context, SetActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        context = this;
        viewHolder = new ViewHolder(getWindow().getDecorView().getRootView());
    }
}
