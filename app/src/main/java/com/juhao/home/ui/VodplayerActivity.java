package com.juhao.home.ui;

import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.BaseActivity;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iotx.linkvisual.media.video.PlayerException;
import com.aliyun.iotx.linkvisual.media.video.listener.OnErrorListener;
import com.aliyun.iotx.linkvisual.media.video.listener.OnPlayerStateChangedListener;
import com.aliyun.iotx.linkvisual.media.video.listener.OnPreparedListener;
import com.aliyun.iotx.linkvisual.media.video.player.VodPlayer;
import com.google.android.exoplayer2.Player;
import com.juhao.home.R;
import com.util.Constance;
import com.util.DateUtils;
import com.util.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class VodplayerActivity extends BaseActivity {

    private GLSurfaceView surface_view;
    private String iotId;
    private VodPlayer player;
    private HorizontalScrollView hsl;
    private TextView tv_head;
    private int[] locationScreen;
    private String currentTime="00:00:00";
    private long startTime;
    private long endTime;
    private String currentEndTime = "00:59:59";
    private float x;


    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initView() {
        setContentView(R.layout.activity_vodplayer);
        surface_view = findViewById(R.id.surface_view);
        hsl = findViewById(R.id.hsl);
        tv_head = findViewById(R.id.tv_head);
        player = new VodPlayer();
        player.setSurfaceView(surface_view);

        // 设置必要的状态监听
        player.setOnPlayerStateChangedListener(new OnPlayerStateChangedListener() {
            @Override
            public void onPlayerStateChange(int playerState) {
                switch (playerState) {
                    case Player.STATE_BUFFERING:
                        break;
                    case Player.STATE_IDLE:
                        break;
                    case Player.STATE_READY:
                        break;
                    case Player.STATE_ENDED:
                        break;
                    default:
                        break;
                }
            }
        });
// 设置错误监听
        player.setOnErrorListener(new OnErrorListener() {
            @Override
            public void onError(PlayerException exception) {
//                makeToast("errorcode: " + exception.getCode() + "\n" + exception.getMessage());
            }
        });
//        player.setDataSourceByIPCRecordFileName(iotId,"L3Byb2dzL3JlYy8wMC8yMDE5MDkyNi9NMDk1OTI1LkgyNjQ=",false,0);
//        player.setDataSourceByIPCRecordTime(iotId,1569463165,1569463408,false,-1);
        startTime = DateUtils.getTimeStamp(year+"-"+month+"-"+day+" "+currentTime,"yyyy-MM-dd hh:mm:ss")/1000L;
        endTime = DateUtils.getTimeStamp(year+"-"+month+"-"+day+" "+currentEndTime,"yyyy-MM-dd hh:mm:ss")/1000L;
//                        getRecordList();
        LogUtils.logE("startTime",startTime+"");
        LogUtils.logE("endTime",endTime+"");
        player.setDataSourceByIPCRecordTime(iotId,(int) startTime,(int) endTime,false,0);
//                        player.reset();
//                        player.prepare();
// 设置数据源就绪监听器
        player.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                // 数据源就绪后开始播放
                player.start();
            }
        });
        player.prepare();

        hsl.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
//                Log.w("x",i+","+i2);
//                Log.w("y",i1+","+i3);
                tv_head.getLocationOnScreen(locationScreen);
//                Log.e("head", locationScreen[0]+"");
                x = locationScreen[0];
                x = (int) (Math.abs(x)*25);
                int h= (int) (x /60/60);
                int m= (int) ((x -h*60*60)/60);
                int s= (int) ((x -m*60)/60/60);
//                currentTime = (h<10?("0"+h):h)+":"+(m<10?("0"+m):m)+":"+(s<10?("0"+s):s);
                if(h<23)h++;
//                currentEndTime = (h<10?("0"+h):h)+":"+(m<10?("0"+m):m)+":"+(s<10?("0"+s):s);
                currentEndTime = "23:59:59";
//                Log.e("time",h+":"+m+":"+s);
                Log.e("time", currentTime);
            }
        });
        hsl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_UP:
                        Log.e("action","up");
                        player.seekTo((long) x);

                        break;
                }
                return false;
            }
        });
        locationScreen = new int[2];
        tv_head.getLocationOnScreen(locationScreen);

    }
    String year="2019";
    String month="09";
    String day="25";
    String day2="26";

    @Override
    protected void onPause() {
        super.onPause();
        surface_view.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        surface_view.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 停止播放
        player.stop();
        player.release();
    }
    private void getRecordList() {
        Map<String,Object> map=new HashMap<>();
        map.put("identifier","QueryRecordList");
        map.put("iotId",iotId);
        JSONObject jsonObject=new JSONObject();
//        String startTime=DateUtils.getTimeStamp("2019-09-26 00:00","yyyy-MM-dd hh:mm")/1000+"";
//        String endTime=DateUtils.getTimeStamp("2019-09-27 00:00","yyyy-MM-dd hh:mm")/1000+"";
//        LogUtils.logE("BeginTime",""+1569463165);
//        LogUtils.logE("EndTime",""+1569463408);
        jsonObject.put("BeginTime",startTime);
        jsonObject.put("EndTime",endTime);
        jsonObject.put("QuerySize",128);
        jsonObject.put("Type",2);
        map.put("args",jsonObject);
        invokeService(map);
    }
    private void invokeService(Map<String,Object> maps) {


        IoTRequestBuilder ioTRequestBuilder = new IoTRequestBuilder()
                .setPath("/thing/service/invoke")
                .setApiVersion("1.0.2")
                .setAuthType("iotAuth")
                .setParams(maps);
        IoTRequest request = ioTRequestBuilder.build();
        IoTAPIClient client = new IoTAPIClientFactory().getClient();
        client.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                LogUtils.logE("onResponse", ioTResponse.getCode() + "," + ioTResponse.getMessage() + "," + ioTResponse.getData());
                if(ioTResponse.getCode()==200){
                    org.json.JSONObject jsonObject= (org.json.JSONObject) ioTResponse.getData();
                    if(jsonObject!=null){
                        try {
                            JSONArray jsonArray=jsonObject.getJSONArray(Constance.RecordList);
                            if(jsonArray!=null&&jsonArray.length()>0){

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        });
    }

    @Override
    protected void initData() {
        iotId = getIntent().getStringExtra(Constance.iotId);
    }
}
