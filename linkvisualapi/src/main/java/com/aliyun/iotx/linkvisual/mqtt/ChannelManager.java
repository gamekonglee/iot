package com.aliyun.iotx.linkvisual.mqtt;

import android.content.Context;
import android.util.Log;

import com.aliyun.alink.linksdk.channel.core.base.AError;
import com.aliyun.alink.linksdk.channel.mobile.api.IMobileConnectListener;
import com.aliyun.alink.linksdk.channel.mobile.api.IMobileDownstreamListener;
import com.aliyun.alink.linksdk.channel.mobile.api.IMobileRequestListener;
import com.aliyun.alink.linksdk.channel.mobile.api.IMobileSubscrbieListener;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileChannel;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileConnectConfig;
import com.aliyun.alink.linksdk.channel.mobile.api.MobileConnectState;

import java.util.LinkedList;
import java.util.List;

public class ChannelManager {

    private String TAG = "ChannelManager";
    private String allTopicWildcard = "#";
    /**
     * 当前建联状态
     */
    private MobileConnectState mMobileConnectState;
    private Context context;
    private String appKey;
    private List<IMobileMsgListener> listenerList;

    private ChannelManager() {
    }

    private static class ChannelManagerHolder {
        private final static ChannelManager channelManager = new ChannelManager();
    }

    public static ChannelManager getInstance() {
        return ChannelManagerHolder.channelManager;
    }

    public void init(Context context, String appKey) {
        this.context = context;
        this.appKey = appKey;
        connect();
    }

    private IMobileDownstreamListener downstreamListener = new IMobileDownstreamListener() {

        @Override
        public void onCommand(String topic, String msg) {
            processMsg(topic, msg);
        }


        @Override
        public boolean shouldHandle(String s) {
            return true;
        }

    };
    private IMobileConnectListener connectListener = new IMobileConnectListener() {

        @Override
        public void onConnectStateChange(MobileConnectState mobileConnectState) {
            mMobileConnectState = mobileConnectState;
            if (MobileConnectState.CONNECTED == mobileConnectState) {
                //已连接
                Log.e(TAG,"通道已连接");
                ChannelManager.this.notify("dev/state","CONNECTED");
                subscrbie(allTopicWildcard, subscrbieListener);
            } else if (MobileConnectState.DISCONNECTED == mobileConnectState) {
                //已断开
                Log.e(TAG,"通道已断开");
                ChannelManager.this.notify("dev/state","DISCONNECTED");
                connect();
            } else if (MobileConnectState.CONNECTING == mobileConnectState) {
                //连接中
                Log.e(TAG,"通道连接中");
                ChannelManager.this.notify("dev/state","CONNECTING");
            } else if (MobileConnectState.CONNECTFAIL == mobileConnectState) {
                //连接失败
                Log.e(TAG,"通道连接失败");
                ChannelManager.this.notify("dev/state","CONNECT_FAIL");
                connect();
            }
        }
    };

    private IMobileSubscrbieListener subscrbieListener = new IMobileSubscrbieListener() {
        @Override
        public void onSuccess(String s) {
            Log.e(TAG,"订阅成功");
        }

        @Override
        public void onFailed(String s, AError aError) {
            Log.e(TAG,"订阅失败");
            if(MobileConnectState.CONNECTED == mMobileConnectState){
                subscrbie(allTopicWildcard, subscrbieListener);
            }else{
                connect();
            }
        }

        @Override
        public boolean needUISafety() {
            return false;
        }
    };

    /**
     * 发布
     */
    public void publish(String topic, String msg, IMobileRequestListener listener) {
        MobileChannel.getInstance().ayncSendPublishRequest(topic, msg, listener);
    }

    /**
     * 订阅
     */
    public void subscrbie(String topic, IMobileSubscrbieListener listener) {
        MobileChannel.getInstance().subscrbie(topic, listener);
    }

    /**
     * 取消订阅
     */
    public void unSubscrbie(String topic, IMobileSubscrbieListener listener) {
        MobileChannel.getInstance().unSubscrbie(topic, listener);
    }

    /**
     * 建联
     */
    private void connect() {
        MobileConnectConfig config = new MobileConnectConfig();
        config.appkey = appKey;
        MobileChannel.getInstance().startConnect(context, config, connectListener);
        MobileChannel.getInstance().registerDownstreamListener(true, downstreamListener);
    }

    public void disconnect(){
        MobileChannel.getInstance().unRegisterConnectListener(connectListener);
        MobileChannel.getInstance().unRegisterDownstreamListener(downstreamListener);
    }

    private void processMsg(String topic, String msg){
        Log.d(TAG,"收到消息    topic"+topic+"      msg:"+msg);
        notify(topic, msg);
    }

    public interface IMobileMsgListener{
        void onCommand(String topic, String msg);
    }

    public void registerListener(IMobileMsgListener listener){
        if(listener == null){
            return;
        }
        if(listenerList == null){
            listenerList = new LinkedList<>();
        }
        if(!listenerList.contains(listener)){
            listenerList.add(listener);
        }
    }

    public void unRegisterListener(IMobileMsgListener listener){
        if(listener == null){
            return;
        }
        if(listenerList == null){
            listenerList = new LinkedList<>();
            return;
        }
        if(listenerList.contains(listener)){
            listenerList.remove(listener);
        }
    }

    private void notify(String topic, String msg){
        if(topic == null || msg == null){
            return;
        }
        if(listenerList!= null && listenerList.size() >0){
            for(IMobileMsgListener listener:listenerList){
                listener.onCommand(topic,msg);
            }
        }
    }
}
