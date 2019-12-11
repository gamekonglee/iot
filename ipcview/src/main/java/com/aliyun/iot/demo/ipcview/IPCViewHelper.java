package com.aliyun.iot.demo.ipcview;

import android.content.Context;

import com.aliyun.iot.demo.ipcview.manager.SharePreferenceManager;
import com.aliyun.iot.demo.ipcview.utils.ImageCache;
import com.aliyun.iotx.linkvisual.IPCManager;

/**
 * 初始化类
 *
 *
 * ！！！注意！！！
 * 注意本示例代码主要用于演示部分视频业务接口以及对应的效果
 * 代码中涉及的交互，UI以及代码框架请自行设计，示例代码仅供参考，稳定性请客户自行保证。
 *
 * @author azad
 */
public class IPCViewHelper {
    private IPCViewHelper(){}

    private static class IPCViewHelperHolder {
        private static final IPCViewHelper IPC_VIEW_HELPER = new IPCViewHelper();
    }

    public static IPCViewHelper getInstance(){
        return IPCViewHelperHolder.IPC_VIEW_HELPER;
    };

    public void init(Context context,String version){
        ImageCache.getInstance().init();
        SharePreferenceManager.getInstance().init(context);
        IPCManager.getInstance().init(context,version);

    }
}
