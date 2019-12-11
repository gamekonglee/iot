package com.net;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.aliyun.iot.ilop.demo.DemoApplication;
import com.juhao.home.R;
import com.juhao.home.UIUtils;
import com.util.Constance;
import com.util.MyShare;
import com.util.NetWorkConst;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.juhao.home.UIUtils.getString;


/**
 * Created by bocang on 18-2-2.
 */

public class ApiClient {
    /**
     * 检查网络是否联网
     */
    public static boolean hashkNewwork() {

        boolean hasNetwork = NetworkStateManager.instance().isNetworkConnected();
        if (!hasNetwork) {
            Toast.makeText(DemoApplication.getInstance(), "您的网络连接已中断", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    public static void sendPayment(String order, final Callback callback) {
        if(!hashkNewwork()){
            return;
        }

//        responseListener.onStarted();
        Map<String, String> map=new HashMap<>();
        map.put("order",order);
        String url= NetWorkConst.APK_URL;
        String token= MyShare.get(UIUtils.getContext()).getString(Constance.TOKEN);

        OkHttpUtils.post()
                .url(url)
                .addHeader("X-bocang-Authorization",token)
                .addParams("order", order)
                .build()
                .execute(new Callback() {
                    @Override
                    public Object parseNetworkResponse(Response response, int i1) throws Exception {
                            String jsonRes=response.body().string();
//                        callback.onResponse(advertBeanList,i1);
                        return jsonRes;
                    }

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        callback.onError(call,e,i);
                    }

                    @Override
                    public String onResponse(Object o, int i) {
                        callback.onResponse(o,i);

                        return null;
                    }
                });
        //        requestQueue.add(simplePostRequest);

    }
    public static void sendWxPayment(String order, final Callback<String> callback) {
        if(!hashkNewwork()){
            return;
        }

//        responseListener.onStarted();
        Map<String, String> map=new HashMap<>();
        map.put("order",order);
        String url= NetWorkConst.ALIPAY_URL;
        String token= MyShare.get(UIUtils.getContext()).getString(Constance.TOKEN);

        OkHttpUtils.post()
                .url(url)
                .addHeader("X-bocang-Authorization",token)
                .addParams("order", order)
                .addParams("code","wxpay.dmf")
                .build()
                .execute(new Callback() {
                    @Override
                    public Object parseNetworkResponse(Response response, int i1) throws Exception {
                        String jsonRes=response.body().string();
//                        callback.onResponse(advertBeanList,i1);

                        return jsonRes;
                    }

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        callback.onError(call,e,i);
                    }

                    @Override
                    public String onResponse(Object o, int i) {
                        callback.onResponse(String.valueOf(o),i);

                        return null;
                    }
                });
        //        requestQueue.add(simplePostRequest);

    }

    public static void sendUser(String profile, final Callback<String> callback) {
        if(!hashkNewwork()){
            return;
        }

//        responseListener.onStarted();
//        Map<String ,String> map=new HashMap<>();
        String url= profile;
        String token= MyShare.get(UIUtils.getContext()).getString(Constance.TOKEN);
        OkHttpUtils.post()
                .url(url)
                .addHeader("X-bocang-Authorization",token)
//                .addParams("order", order)
                .build()
                .execute(new Callback() {
                    @Override
                    public Object parseNetworkResponse(Response response, int i1) throws Exception {
                        String jsonRes=response.body().string();
//                        LogUtils.logE("okhttp3:",jsonRes);

//                        callback.onResponse(advertBeanList,i1);
                        return jsonRes;
                    }

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        callback.onError(call,e,i);
                    }

                    @Override
                    public String onResponse(Object o, int i) {
                        callback.onResponse((String) o,i);

                        return null;
                    }
                });
    }
    public static void downloadMp4(String url, final FileCallBack callBack){
        if(!hashkNewwork()){
            return;
        }
        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), System.currentTimeMillis()+"juhao.mp4") {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callBack.onError(call,e,id);
                    }

                    @Override
                    public String onResponse(File response, int id) {
                        callBack.onResponse(response,id);
                        return null;
                    }
                });
    }

    public static void getAuthCode(final Callback<String> callback) {
        if(!hashkNewwork()){
            return;
        }

//        responseListener.onStarted();
//        Map<String ,String> map=new HashMap<>();
        String url= NetWorkConst.AUTH_CODE;
        String token= MyShare.get(UIUtils.getContext()).getString(Constance.TOKEN);
        if(TextUtils.isEmpty(token)){return;}
        OkHttpUtils.post()
                .url(url)
                .addHeader("X-bocang-Authorization",token)
//                .addParams("order", order)
                .build()
                .execute(new Callback() {
                    @Override
                    public Object parseNetworkResponse(Response response, int i1) throws Exception {
                        String jsonRes=response.body().string();
//                        LogUtils.logE("okhttp3:",jsonRes);

//                        callback.onResponse(advertBeanList,i1);
                        return jsonRes;
                    }

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        callback.onError(call,e,i);
                    }

                    @Override
                    public String onResponse(Object o, int i) {
                        callback.onResponse((String) o,i);

                        return null;
                    }
                });
    }

    public static void sendBannerIndex(final Callback<String> callback) {


        if(!hashkNewwork()){
            return;
        }

//        responseListener.onStarted();
//        Map<String ,String> map=new HashMap<>();
        String url= NetWorkConst.BANNER_INDEX;
//        String token= MyShare.get(UIUtils.getContext()).getString(Constance.TOKEN);
        OkHttpUtils.post()
                .url(url)
//                .addHeader("X-bocang-Authorization",token)
//                .addParams("order", order)
                .build()
                .execute(new Callback() {
                    @Override
                    public Object parseNetworkResponse(Response response, int i1) throws Exception {
                        String jsonRes=response.body().string();
//                        LogUtils.logE("okhttp3:",jsonRes);

//                        callback.onResponse(advertBeanList,i1);
                        return jsonRes;
                    }

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        callback.onError(call,e,i);
                    }

                    @Override
                    public String onResponse(Object o, int i) {
                        callback.onResponse((String) o,i);

                        return null;
                    }
                });
    }

    public static void getWeather(String district, final Callback<String> callback) {
        if(!hashkNewwork()){
            return;
        }
        String url="http://api.map.baidu.com/telematics/v3/weather?location="+district+"&output=json&ak=waXcx6StTPnQXZqRxG70iSdA";
        OkHttpUtils.get().url(url)
                .build().execute(new Callback() {
            @Override
            public Object parseNetworkResponse(Response response, int id) throws Exception {

                return response.body().string();
            }

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public String onResponse(Object response, int id) {
                callback.onResponse(String.valueOf(response),id);
                return (String) response;
            }
        });
    }

    public static void IotCreatTimer(String iotId, String days, String hour, String minute, int status, String jsonObject, final Callback<String> callback) {
        if(!hashkNewwork()){return;}
        String url=NetWorkConst.IOT_TIMER_CREATE;
//        OkHttpClient okHttpClient=new OkHttpClient().newBuilder()
//                .connectTimeout(10, TimeUnit.SECONDS)
//                .writeTimeout(10,TimeUnit.SECONDS)
//                .readTimeout(10,TimeUnit.SECONDS).build();
//        org.json.JSONObject params=new org.json.JSONObject();
        Map<String, String> params=new HashMap<>();
        params.put("iotid",iotId);
        params.put("items",jsonObject);
        params.put("weeks",days);
        params.put("hour",hour);
        params.put("minute",minute);
        params.put("status",status+"");
//
//        RequestBody requestBody= FormBody.create(MediaType.parse("application/json; charset=utf-8"),jsonObject.toString());
//        Request request=new Request.Builder().build().newBuilder()
//                .post(requestBody)
//                .build();
//        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//
//            }
//        });
        OkHttpUtils.post().url(url)
                .params(params)
                .build().execute(new Callback() {
            @Override
            public Object parseNetworkResponse(Response response, int id) throws Exception {

                return response.body().string();
            }

            @Override
            public void onError(Call call, Exception e, int id) {
//                Log.e("onError",e.getMessage());
            }

            @Override
            public String onResponse(Object response, int id) {
                callback.onResponse(String.valueOf(response),id);
                return String.valueOf(response);
            }
        });

    }

    public static void IotTimerList(String iotid, final Callback<String> callback) {
        if(!hashkNewwork())return;
        String url=NetWorkConst.IOT_TIMER_LIST;
        OkHttpUtils.get().url(url)
                .addParams("iotid",iotid)
                .build().execute(new Callback() {
            @Override
            public Object parseNetworkResponse(Response response, int id) throws Exception {
                return response.body().string();
            }

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public String onResponse(Object response, int id) {
                callback.onResponse(String.valueOf(response),id);
                return null;
            }
        });
    }

    public static void IotTimerUpdate(int pid, String iotId, String jsonObject, String days, String hour, String minute, int status, final Callback<String> callback) {
        if(!hashkNewwork()){
            return;
        }
        String url=NetWorkConst.IOT_TIMER_UPDATE;
        OkHttpClient okHttpClient=new OkHttpClient();
        FormBody formBody=new FormBody.Builder()
                .add("pid",pid+"")
                .add("iotid",iotId)
                .add("items",jsonObject)
                .add("weeks",days)
                .add("hour",hour)
                .add("minute",minute)
                .add("status",status+"")
                .build();

//        RequestBody requestBody= FormBody.create(MediaType.parse("application/json; charset=utf-8"),jsonObject.toString());
        Request request=new Request.Builder()
                .url(url)
                .put(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                    callback.onResponse(response.body().string(),0);
            }
        });
//        OkHttpUtils
//                .put()
//                .requestBody()
//                .url(url)
//
//        .build().execute(new Callback() {
//            @Override
//            public Object parseNetworkResponse(Response response, int id) throws Exception {
//                return response.body().string();
//            }
//
//            @Override
//            public void onError(Call call, Exception e, int id) {
//
//            }
//
//            @Override
//            public String onResponse(Object response, int id) {
//                callback.onResponse(String.valueOf(response),id);
//                return String.valueOf(response);
//            }
//        });

    }

    public static void IotTimerDelete(int pid, String iotid, final Callback<String> callback) {
        if(!hashkNewwork())return;
        OkHttpClient okHttpClient=new OkHttpClient();
        FormBody formBody=new FormBody.Builder()
                .add("pid",pid+"")
                .add("iotid",iotid+"")
                .build();
        String url=NetWorkConst.IOT_TIMER_DELETE;
        Request request=new Request.Builder()
                .url(url)
                .delete(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                    callback.onResponse(response.body().string(),0);
            }
        });
    }

    public static void getScensList(Callback<String> callback) {
        if(!hashkNewwork())return;
        String url=NetWorkConst.CATEGORY_SCENE;
        String tokend=MyShare.get(UIUtils.getContext()).getString(Constance.TOKEN);
        OkHttpUtils.post().url(url)
                .addHeader("X-bocang-Authorization",tokend)
                .build()
                .execute(callback);
    }

    public static void sendShopAddress(String id, Callback<String> callback) {
        if(!hashkNewwork())return;
        String url=NetWorkConst.USER_SHOP_ADDRESS+id;
        Log.e("url",url);
        String tokend=MyShare.get(UIUtils.getContext()).getString(Constance.TOKEN);
        OkHttpUtils.post().url(url)
                .build().execute(callback);

    }

    public static void getDevicesList(int pid, final Callback<String> callback) {
        if(!hashkNewwork())return;
        OkHttpClient okHttpClient=new OkHttpClient();
        FormBody formBody=new FormBody.Builder()
                .add("pid",pid+"")
                .build();
        String laguage=getString(R.string.str_language);
        boolean isEng=true;
        if(laguage.equals("zh-CN")){
            isEng=false;
        }
        String url=NetWorkConst.IOT_DEVICES_LIST+"pid="+pid+(DemoApplication.is_national||isEng?"&locale=en":"");
        Request request=new Request.Builder()
                .url(url)
                .get()
                .build();
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onResponse(response.body().string(),0);
            }
        });
    }

    public static void getDevicesListB(int id, Callback<String> stringCallback) {

    }

    public static void getDevicesDatas( final Callback<String> callback) {
        if(!hashkNewwork())return;
        OkHttpClient okHttpClient=new OkHttpClient();
        FormBody formBody=new FormBody.Builder()
                .build();
        String url=NetWorkConst.IOT_DEVICES_DATAS+(DemoApplication.is_national?"?isGlobal=1":"");
        Request request=new Request.Builder()
                .url(url)
                .get()
                .build();
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onResponse(response.body().string(),0);
            }
        });
    }

    public static void getAreaCode(final okhttp3.Callback callback) {
        if(!hashkNewwork())return;
        OkHttpClient okHttpClient=new OkHttpClient();
        String url="http://smart.bocang.cc/api/area_code";
        Request request=new Request.Builder()
                .url(url)
                .get()
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static String  getSceneImg() {
//        OkHttpClient okHttpClient=new OkHttpClient();
        String url=NetWorkConst.API_SMART+"/api/scene/img";
        try {
            Response response=OkHttpUtils.get().url(url).build().execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
//    public static void SendRequest(String url, final Callback callback){
//        if(!hashkNewwork()){
//            return;
//        }
//
////        responseListener.onStarted();
////        Map<String ,String> map=new HashMap<>();
//        String token= MyShare.get(UIUtils.getContext()).getString(Constance.TOKEN);
//        OkHttpUtils.post()
//                .url(url)
//                .addHeader("X-bocang-Authorization",token)
//                .build()
//                .execute(new Callback() {
//                    @Override
//                    public Object parseNetworkResponse(okhttp3.Response response, int i1) throws Exception {
//                        String jsonRes=response.body().string();
//                        LogUtils.logE("okhttp3:",jsonRes);
//
////                        callback.onResponse(advertBeanList,i1);
//                        return jsonRes;
//                    }
//
//                    @Override
//                    public void onError(okhttp3.Call call, Exception e, int i) {
//                        callback.onError(call,e,i);
//                    }
//
//                    @Override
//                    public void onResponse(Object o, int i) {
//                        callback.onResponse((String) o,i);
//
//                    }
//                });
//    }
}
