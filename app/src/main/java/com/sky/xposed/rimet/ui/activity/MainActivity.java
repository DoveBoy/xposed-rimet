/*
 * Copyright (c) 2020 The sky Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sky.xposed.rimet.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.sky.xposed.common.util.PackageUtil;
import com.sky.xposed.rimet.BuildConfig;
import com.sky.xposed.rimet.R;
import com.sky.xposed.rimet.XConstant;
import com.sky.xposed.rimet.ui.dialog.LoveDialog;
import com.sky.xposed.rimet.ui.dialog.SettingsDialog;
import com.sky.xposed.rimet.ui.util.ActivityUtil;
import com.sky.xposed.rimet.ui.util.DialogUtil;
import com.sky.xposed.ui.view.ItemMenu;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ItemMenu imVersion = findViewById(R.id.im_version);
        ItemMenu imDingVersion = findViewById(R.id.im_ding_version);
        TextView tvSupportVersion = findViewById(R.id.tv_support_version);

        imVersion.setDesc("v" + BuildConfig.VERSION_NAME);
        imDingVersion.setDesc(getVersionName(XConstant.Rimet.PACKAGE_NAME));

//        XVersionManager versionManager = CoreUtil.getCoreManager().getVersionManager();

        StringBuilder builder = new StringBuilder();
        builder.append("配置入口: 钉钉->我的->设置->钉钉助手");
        builder.append("\n注: 只有Xposed功能生效,才会在设置中显示钉钉助手");
        builder.append("\n\n适配的版本: \n当出现版本不适配时,根据助手中的提示可以自动适配\n");
//        builder.append(versionManager.getSupportVersion());

        tvSupportVersion.setText(builder.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (BuildConfig.DEBUG) {
            getMenuInflater().inflate(R.menu.activity_main_menu, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_settings) {
            // 显示
            SettingsDialog dialog = new SettingsDialog();
            dialog.show(getFragmentManager(), "setting");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.im_download:
                // 下载
                ActivityUtil.openUrl(this, "http://repo.xposed.info/module/com.sky.xposed.rimet");
                break;
            case R.id.im_source:
                // 源地址
                ActivityUtil.openUrl(this, "https://github.com/sky-wei/xposed-rimet");
                break;
            case R.id.im_document:
                // 文档地址
                ActivityUtil.openUrl(this, "http://blog.skywei.info/2019-04-18/xposed_rimet");
                break;
            case R.id.im_love:
                // 公益
                LoveDialog loveDialog = new LoveDialog();
                loveDialog.show(getFragmentManager(), "love");
                break;
            case R.id.im_about:
                // 关于
                DialogUtil.showAboutDialog(this);
                break;
        }
    }

    private String getVersionName(String packageName) {

        // 获取版本名
        PackageUtil.SimplePackageInfo info = PackageUtil
                .getSimplePackageInfo(this, packageName);

        return info == null ? "Unknown" : "v" + info.getVersionName();
    }
}
