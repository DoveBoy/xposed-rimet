/*
 * Copyright (c) 2019 The sky Authors.
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

package com.sky.xposed.rimet.data.source;

import com.sky.xposed.rimet.data.cache.ICacheManager;
import com.sky.xposed.rimet.data.cache.IRimetCache;
import com.sky.xposed.rimet.data.cache.RimetCache;
import com.sky.xposed.rimet.data.service.IServiceFactory;
import com.sky.xposed.rimet.data.service.ServiceFactory;
import com.sky.xposed.rimet.data.source.local.RimetLocalSource;
import com.sky.xposed.rimet.data.source.remote.RimetRemoteSource;
import com.sky.xposed.rimet.plugin.interfaces.XConfigManager;

/**
 * Created by sky on 2019-05-27.
 */
public class RepositoryFactory implements IRepositoryFactory {

    private IServiceFactory mServiceFactory;
    private IRimetCache mRimetCache;

    public RepositoryFactory(XConfigManager xConfigManager, ICacheManager iCacheManager) {
        mServiceFactory = new ServiceFactory();
        mRimetCache = new RimetCache(xConfigManager, iCacheManager);
    }

    @Override
    public IRimetSource createRimetSource() {
        return new RimetRepository(
                new RimetLocalSource(mRimetCache),
                new RimetRemoteSource(mServiceFactory, mRimetCache));
    }
}
