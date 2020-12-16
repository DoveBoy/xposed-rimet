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

package com.sky.xposed.rimet;

import android.app.Application;
import android.content.Context;

import com.sky.xposed.common.util.ToastUtil;
import com.sky.xposed.core.XStore;
import com.sky.xposed.core.adapter.CoreListenerAdapter;
import com.sky.xposed.core.adapter.ThrowableAdapter;
import com.sky.xposed.core.component.ComponentFactory;
import com.sky.xposed.core.interfaces.XConfig;
import com.sky.xposed.core.interfaces.XCoreManager;
import com.sky.xposed.core.interfaces.XPlugin;
import com.sky.xposed.core.internal.CoreManager;
import com.sky.xposed.javax.XposedPlus;
import com.sky.xposed.javax.XposedUtil;
import com.sky.xposed.rimet.util.FileUtil;
import com.sky.xposed.ui.util.CoreUtil;
import com.sky.xposed.ui.util.DisplayUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by sky on 2019/3/14.
 */
public class Main implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpParam) throws Throwable {

        if (!XConstant.Rimet.PACKAGE_NAME.equals(lpParam.packageName)) return;

        // 初始化XposedPlus
        XposedPlus.setDefaultInstance(new XposedPlus.Builder(lpParam)
                .throwableCallback(new ThrowableAdapter())
                .build());

        XposedUtil.findMethod(
                "com.alibaba.android.dingtalkbase.multidexsupport.DDApplication", "onCreate")
                .before(param -> handleLoadPackage(param, lpParam));
    }

    /**
     * 处理加载的包
     * @param param
     * @param lpParam
     * @throws Throwable
     */
    private void handleLoadPackage(
            XC_MethodHook.MethodHookParam param,
            XC_LoadPackage.LoadPackageParam lpParam) throws Throwable {

        Application application = (Application) param.thisObject;
        Context context = application.getApplicationContext();

        final String className = application.getClass().getName();

        if (!"com.alibaba.android.rimet.LauncherApplication".equals(className)) {
            // 不需要处理
            return;
        }

        XCoreManager coreManager = new CoreManager.Build(context)
                .setPluginPackageName(BuildConfig.APPLICATION_ID)
                .setProcessName(lpParam.processName)
                .setClassLoader(lpParam.classLoader)
                .setComponentFactory(new ComponentFactory() {
                    @Override
                    protected List<Class<? extends XConfig>> getVersionData() {
                        return XStore.getConfigClass();
                    }

                    @Override
                    protected List<Class<? extends XPlugin>> getPluginData() {
                        return XStore.getPluginClass();
                    }
                })
                .setCoreListener(new CoreListenerAdapter() {

                    @Override
                    public void onInitComplete(XCoreManager coreManager) {
                        super.onInitComplete(coreManager);

                        final Context context = coreManager.getLoadPackage().getContext();

                        // 保存当前版本MD5信息
                        String md5 = FileUtil.getFileMD5(new File(context.getApplicationInfo().sourceDir));
                        coreManager.getDefaultPreferences().putString(XConstant.Key.PACKAGE_MD5, md5);

                        // 初始化
                        CoreUtil.init(coreManager);
                        DisplayUtil.init(context);
                        ToastUtil.getInstance().init(context);
                        Picasso.setSingletonInstance(new Picasso.Builder(context).build());
                    }
                })
                .build();

        // 开始处理加载的包
        coreManager.loadPlugins();
    }
}
