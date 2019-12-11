package com.juhao.home.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.sdk.android.openaccount.ConfigManager;
import com.alibaba.sdk.android.openaccount.Environment;
import com.alibaba.sdk.android.openaccount.OpenAccountSDK;
import com.alibaba.sdk.android.openaccount.OpenAccountService;
import com.alibaba.sdk.android.openaccount.OpenAccountSessionService;
import com.alibaba.sdk.android.openaccount.callback.InitResultCallback;
import com.alibaba.sdk.android.openaccount.callback.LoginCallback;
import com.alibaba.sdk.android.openaccount.callback.LogoutCallback;
import com.alibaba.sdk.android.openaccount.model.OpenAccountSession;
import com.alibaba.sdk.android.openaccount.model.User;
import com.alibaba.sdk.android.openaccount.session.SessionListener;
import com.alibaba.sdk.android.openaccount.session.SessionManagerService;
import com.alibaba.sdk.android.openaccount.ui.OpenAccountUIService;
import com.aliyun.iot.aep.sdk.login.ILoginAdapter;
import com.aliyun.iot.aep.sdk.login.ILoginCallback;
import com.aliyun.iot.aep.sdk.login.ILoginStatusChangeListener;
import com.aliyun.iot.aep.sdk.login.ILogoutCallback;
import com.aliyun.iot.aep.sdk.login.IRefreshSessionCallback;
import com.aliyun.iot.aep.sdk.login.data.SessionInfo;
import com.aliyun.iot.aep.sdk.login.data.UserInfo;
import com.aliyun.iot.aep.sdk.threadpool.ThreadPool;
import com.aliyun.iot.ilop.demo.DemoApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by feijie.xfj on 17/10/30.
 * 默认实现一个OALoginAdapter
 */

public class OAMyLoginAdapter implements ILoginAdapter {
    private final String TAG = "OAMyLoginAdapter";

    private Context context;

    private boolean isDebug = false;

    private List<ILoginStatusChangeListener> mLoginStatusChangeListenerList = new ArrayList<>();

    //当业务方调用 refreshSession时，刷新成功的回调，只保证一次有效
    private volatile List<IRefreshSessionCallback> mRefreshCallbacks = new ArrayList<>();

    //登出之前需要同步做的清理工作
    private List<OnBeforeLogoutListener> mBeforeLogoutListener = new ArrayList<>();

    private volatile boolean isRefreshing = false;

    private SessionListener defaultOASessionListener;

    private SessionInfo sessionInfo = new SessionInfo();


    /* --         Method         --*/
    public OAMyLoginAdapter(Context context) {
        this(context, false);
    }

    public OAMyLoginAdapter(Context context, boolean debug) {
        this.context = context;
        this.isDebug = debug;
    }

    public void setDefaultOAHost(String host) {
        if (!TextUtils.isEmpty(host)) {
            ConfigManager.getInstance().setApiGatewayHost(host);
        }
    }

    @Override
    public void init(String env) {
        log("init() OAMyLoginAdapter , env is:" + env);
        /**
         * 设置当前的环境信息
         * Environment.TEST 日常
         * Environment.PRE 预发
         * Environment.ONLINE 线上
         * */
        if ("TEST".equalsIgnoreCase(env)) {
            ConfigManager.getInstance().setEnvironment(Environment.TEST);
        } else if ("PRE".equalsIgnoreCase(env)) {
            ConfigManager.getInstance().setEnvironment(Environment.PRE);
        } else {
            ConfigManager.getInstance().setEnvironment(Environment.ONLINE);
        }
        if(DemoApplication.is_national){
            ConfigManager.getInstance().setSecGuardImagePostfix("develop_oversea");
        }else {
        ConfigManager.getInstance().setSecGuardImagePostfix("114d");
        }
        ConfigManager.getInstance().setUseSingleImage(true);
        ConfigManager.getInstance().setAPIGateway(true);
//
//        if (isDebug) {
//            OpenAccountSDK.turnOnDebug();
//        }

        OpenAccountSDK.asyncInit(context, new InitResultCallback() {
            @Override
            public void onSuccess() {
                log("OpenAccountSDK init success");
                //增加默认OA Session 监听,当有Session变化时，必须通过此回调返回给调用者
                defaultOASessionListener = new OASessionListener();
                OpenAccountService openAccountService = OpenAccountSDK.getService(OpenAccountService.class);
                openAccountService.addSessionListener(defaultOASessionListener);
                updateSession();
            }

            @Override
            public void onFailure(int i, String s) {
                log("OpenAccountSDK init failed:" + s);
            }
        });
    }

    /**
     * Account模块内部的日志调试开关
     */
    @Override
    public void setIsDebuggable(boolean debuggable) {
        if (debuggable) {
            OpenAccountSDK.turnOnDebug();
        }
    }

    /*
    * 实际上调用refresh ,底层会先判断是否失效然后再决定要不要刷新
    * */
    @Override
    public void refreshSession(final boolean force, IRefreshSessionCallback sessionListener) {
        if (sessionListener != null) {
            mRefreshCallbacks.add(sessionListener);
        }
        if (isRefreshing) {
            return;
        }
        ThreadPool.DefaultThreadPool.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                log("refreshSession() is force:" + force);
                OpenAccountSessionService openAccountSessionService = OpenAccountSDK.getService(OpenAccountSessionService.class);
                openAccountSessionService.refreshSession(force);
            }
        });
    }

    @Override
    public void registerLoginListener(ILoginStatusChangeListener listener) {
        mLoginStatusChangeListenerList.add(listener);
    }

    @Override
    public void unRegisterLoginListener(ILoginStatusChangeListener listener) {
        mLoginStatusChangeListenerList.remove(listener);
    }

    @Override
    public void login(ILoginCallback callback) {
        final OpenAccountUIService openAccountService = OpenAccountSDK.getService(OpenAccountUIService.class);
        try {
            openAccountService.showLogin(context, new OALoginCallback(callback));
        } catch (Exception e) {
            log("login failed:" + e.toString());
        }
    }

    @Override
    public void logout(ILogoutCallback callback) {
        OpenAccountService openAccountService = OpenAccountSDK.getService(OpenAccountService.class);
        try {
            openAccountService.logout(context, new OALogoutCallback(callback));
        } catch (Exception e) {
            log("logout failure" + e.toString());
        }
    }

    @Override
    public boolean isLogin() {
        OpenAccountService openAccountService = OpenAccountSDK.getService(OpenAccountService.class);
        OpenAccountSession session = openAccountService.getSession();
        return session != null && session.isLogin();
    }

    @Override
    public UserInfo getUserData() {
        OpenAccountService openAccountService = OpenAccountSDK.getService(OpenAccountService.class);
        OpenAccountSession session = openAccountService.getSession();
        User user = session == null ? null : session.getUser();
        UserInfo userInfo = new UserInfo();
        if (user != null) {
            if (!TextUtils.isEmpty(user.openId)) {//auth登录，有openId,用户信息需要从这个字段里取出来
                if (session.getOtherInfo() != null) {
                    try {
                        Map<String, Object> otherInfo = (Map<String, Object>) session.getOtherInfo().get("openaccount_other_info");
                        userInfo.userAvatarUrl = (String) otherInfo.get("avatarUrl");
                        userInfo.userId = (Long) otherInfo.get("id") + "";
                        userInfo.userNick = (String) otherInfo.get("displayName");
                    } catch (Exception e) {
                        return null;
                    }
                    return userInfo;
                }
            }
            userInfo.userAvatarUrl = user.avatarUrl;
            userInfo.userId = user.id;
            userInfo.userNick = user.nick;
            userInfo.userPhone = user.mobile;
            return userInfo;
        }
        return null;
    }

    @Override
    public Object getSessionData() {
        return sessionInfo;
    }

    @Override
    public String getSessionId() {
        return sessionInfo.sessionId;
    }


    private boolean isSessionExpired() {
        SessionManagerService sessionManagerService = OpenAccountSDK.getService(SessionManagerService.class);
        return sessionManagerService.isSessionExpired();
    }

    private void updateSession() {
        SessionManagerService sessionManagerService = OpenAccountSDK.getService(SessionManagerService.class);
        if (sessionManagerService == null) {
            return;
        }
        if (sessionInfo == null) {
            sessionInfo = new SessionInfo();
        }
        sessionInfo.sessionCreateTime = sessionManagerService.getSessionCreationTime();
        sessionInfo.sessionId = sessionManagerService.getSessionId();
        sessionInfo.sessionExpire = sessionManagerService.getSessionExpiredIn();
        log("updateSession() sessionInfo:" + sessionInfo.toString());
    }


    public void registerBeforeLogoutListener(OnBeforeLogoutListener listener) {
        if (listener != null) {
            mBeforeLogoutListener.add(listener);
        }
    }

    public void unRegisterBeforeLogoutListener(OnBeforeLogoutListener listener) {
        if (listener == null) {
            return;
        }
        try {//万一外部传入异常的东西
            mBeforeLogoutListener.remove(listener);
        } catch (Exception e) {
        }
    }

    private class OASessionListener implements SessionListener {

        @Override
        public void onStateChanged(OpenAccountSession openAccountSession) {
            log("onStateChanged() refreshCacheList size:" + mRefreshCallbacks.size());
            updateSession();
            if (!mRefreshCallbacks.isEmpty()) {
                dealCacheRefreshListeners();
            }
            isRefreshing = false;
        }
    }

    /**
     * 处理缓存的callback
     */
    private synchronized void dealCacheRefreshListeners() {
        log("dealCacheRefreshListeners() Deal cache listener size:" + mRefreshCallbacks.size());
        for (IRefreshSessionCallback callback : mRefreshCallbacks) {
            if (callback != null) {
                callback.onRefreshSuccess();
            }
        }
        mRefreshCallbacks.clear();
    }

    private class OALoginCallback implements LoginCallback {
        private ILoginCallback loginCallback;

        public OALoginCallback(ILoginCallback loginCallback) {
            this.loginCallback = loginCallback;
        }

        @Override
        public void onSuccess(OpenAccountSession openAccountSession) {
            log("login Success" + getSessionString(openAccountSession));
            for (ILoginStatusChangeListener listener : mLoginStatusChangeListenerList) {
                listener.onLoginStatusChange();
            }
            if (loginCallback != null) {
                loginCallback.onLoginSuccess();
            }
        }

        @Override
        public void onFailure(int i, String s) {
            log("login failed  code:" + i + " msg:" + s);
            if (loginCallback != null) {
                loginCallback.onLoginFailed(i, s);
            }
        }
    }

    private class OALogoutCallback implements LogoutCallback {

        private ILogoutCallback logoutCallback;

        public OALogoutCallback(ILogoutCallback logoutCallback) {
            this.logoutCallback = logoutCallback;
        }

        @Override
        public void onFailure(int code, String msg) {
            log("logout failure" + "code:" + code + " msg:" + msg);
            if (logoutCallback != null) {
                logoutCallback.onLogoutFailed(code, msg);
            }
        }

        @Override
        public void onSuccess() {
            log("logout Success");
            for (ILoginStatusChangeListener listener : mLoginStatusChangeListenerList) {
                listener.onLoginStatusChange();
            }

            if (logoutCallback != null) {
                logoutCallback.onLogoutSuccess();
            }
        }
    }

    public interface OnBeforeLogoutListener {
        void doAction();
    }

    private String getSessionString(OpenAccountSession session) {
        if (session == null) {
            return "";
        }
        return "userid:" + session.getUserId() + " authorizationCode:" + session.getAuthorizationCode() + " loginTime:" +
                session.getLoginTime() + " user:" + (session.getUser() == null ? "" : session.getUser().toString());
    }

    private void log(String str) {
        Log.i(TAG, str);
    }
}
