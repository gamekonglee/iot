package com;

import android.content.Context;

import com.aliyun.iot.aep.sdk.framework.AApplication;


/**
 * Copyright (C) 2016
 * This file is part of the Epiphyllum B7 System.
 * <p>
 * filename :
 * action : 基本Application
 *
 * @author : adam
 * @version : 7.1
 * @date : 2016-09-01
 * modify :
 */
public class BaseApplication extends AApplication {

    protected static BaseApplication mInstance = null;
    protected static Context mContext = null;

    public static BaseApplication getInstance() {
        return mInstance;
    }

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance=this;
        mContext=this;
    }

}
