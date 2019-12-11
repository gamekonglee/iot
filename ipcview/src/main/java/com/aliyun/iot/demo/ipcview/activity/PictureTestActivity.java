package com.aliyun.iot.demo.ipcview.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.demo.ipcview.R;
import com.aliyun.iot.demo.ipcview.adapter.PicTestAdapter;
import com.aliyun.iot.demo.ipcview.beans.PicInfo;
import com.aliyun.iot.demo.ipcview.dialog.PictureDialog;
import com.aliyun.iot.demo.ipcview.enums.PicRequestTypeEnums;
import com.aliyun.iotx.linkvisual.IPCManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * 查看图片的界面
 *
 *
 *
 *
 * ！！！注意！！！
 * 注意本示例代码主要用于演示部分视频业务接口以及对应的效果
 * 代码中涉及的交互，UI以及代码框架请自行设计，示例代码仅供参考，稳定性请客户自行保证。
 *
 * @author azad
 */
public class PictureTestActivity extends AppCompatActivity {
    private String TAG = this.getClass().getSimpleName();

    private String iotId;

    private Button cancelBtn, delBtn, selectAllBtn, clearBtn, refreshBtn;
    private RelativeLayout headerRl;
    private TextView titleTv;
    private GridView picGv;
    /**
     * 每一个item
     */
    private ArrayList<PicInfo> picInfoLst;
    /**
     * 用于存储已选中项目的位置
     */
    private ArrayList<Boolean> selectItems;
    private PicTestAdapter picTestAdapter;
    private boolean isChooseState;
    private int index = 0;
    private int requestNum = 9;
    private boolean haveMoreData = true;
    private long endTime = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_test);
        picInfoLst = new ArrayList<>();
        selectItems = new ArrayList<>();
        initData();
        initView();
    }

    private void initData() {
        iotId = getIntent().getStringExtra("iotId");
        endTime = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPictureLst(0, endTime, index, requestNum, 0);
    }

    private void refreshGV() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                picTestAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initView() {

        titleTv = (TextView)findViewById(R.id.title_tv);

        cancelBtn = (Button)findViewById(R.id.bt_cancel);
        delBtn = (Button)findViewById(R.id.bt_del);
        selectAllBtn = (Button)findViewById(R.id.bt_select_all);
        clearBtn = (Button)findViewById(R.id.bt_clear);
        refreshBtn = (Button)findViewById(R.id.bt_refresh);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quitChooseState();
            }
        });
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delSelections();
            }
        });
        selectAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectAll(true);
            }
        });
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectAll(false);
            }
        });
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshPicData();
            }
        });

        headerRl = (RelativeLayout)findViewById(R.id.rl_pic_test_header);
        picGv = (GridView)findViewById(R.id.gv_pic);
        picTestAdapter = new PicTestAdapter(this, picInfoLst, picGv);
        picGv.setAdapter(picTestAdapter);
        picGv.setOnItemClickListener(itemClickListener);
        picGv.setOnItemLongClickListener(itemLongClickListener);
        picGv.setOnScrollListener(scrollListener);
        registerViewScrollListener(picTestAdapter);
    }

    private void quitChooseState() {
        selectItems.clear();
        picTestAdapter.setIsState(false);
        setChooseState(false);
    }

    private void refreshPicData() {
        if (picInfoLst == null) {
            picInfoLst = new ArrayList<>();
        }
        picInfoLst.clear();
        index = 0;
        haveMoreData = true;
        getPictureLst(0, endTime, 0, requestNum, 0);
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (isChooseState) {
                CheckBox checkBox = (CheckBox)view.findViewById(R.id.choose_cb);
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                    selectItems.set(position, false);
                } else {
                    checkBox.setChecked(true);
                    selectItems.set(position, true);
                }
                picTestAdapter.setSelectedItems(selectItems);
                picTestAdapter.notifyDataSetChanged();
                setSelectNum();
            } else {
                //通过弹窗展示照片原图
                String url = picInfoLst.get(position).getPictureUrl();
                PictureDialog.getInstance().openDialog(PictureTestActivity.this, url);
            }
        }
    };

    private AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            //如果不是选择模式则进图选择模式
            if (!isChooseState) {
                selectItems = new ArrayList<>();
                for (int i = 0; i < picInfoLst.size(); i++) {
                    selectItems.add(false);
                }
                CheckBox box = (CheckBox)view.findViewById(R.id.choose_cb);
                box.setChecked(true);
                selectItems.set(position, true);
                setChooseState(true);
                picTestAdapter.setIsState(true);
                setSelectNum();
                picTestAdapter.setSelectedItems(selectItems);
                refreshGV();
            }
            return true;
        }
    };

    public interface onViewScrollListener {
        void onScrollStateChanged(AbsListView view, int scrollState);

        void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
    }

    private List<onViewScrollListener> listenerList = new LinkedList<>();

    public void registerViewScrollListener(onViewScrollListener listener) {
        if (listenerList == null) {
            listenerList = new LinkedList<>();
        }
        if (!listenerList.contains(listener)) {
            listenerList.add(listener);
        }
    }

    public void unregisterViewScrollListener(onViewScrollListener listener) {
        if (listenerList == null) {
            listenerList = new LinkedList<>();
            return;
        }
        if (listenerList.contains(listener)) {
            listenerList.remove(listener);
        }
    }

    private boolean canLoadData = false;
    private AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (listenerList != null && listenerList.size() > 0) {
                for (onViewScrollListener listener : listenerList) {
                    listener.onScrollStateChanged(view, scrollState);
                }
            }
            //处于选择模式不会进行分页加载
            if (!isChooseState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    View v = (View)view.getChildAt(view.getChildCount() - 1);
                    if (!view.canScrollVertically(1)) {
                        if (canLoadData) {
                            if (haveMoreData) {
                                synchronized (PictureTestActivity.class) {
                                    getPictureLst(0, endTime, index, requestNum, 0);
                                    canLoadData = false;
                                }
                            } else {
                                showToast(R.string.ipc_album_no_more);
                            }
                        } else {
                            if (haveMoreData) {
                                showToast(R.string.ipc_album_swip_load_next);
                                canLoadData = true;
                            } else {
                                showToast(R.string.ipc_album_no_more);
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (listenerList != null && listenerList.size() > 0) {
                for (onViewScrollListener listener : listenerList) {
                    listener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }
            }
        }
    };

    /**
     * 设置当前状态 是否在多选模式
     * @param isChooseState 是否是选择模式
     */
    private void setChooseState(boolean isChooseState) {
        this.isChooseState = isChooseState;
        if (isChooseState) {
            refreshBtn.setVisibility(View.GONE);
            selectAllBtn.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.VISIBLE);
            setSelectNum();
            delBtn.setVisibility(View.VISIBLE);
        } else {
            titleTv.setText("查看图片");
            refreshBtn.setVisibility(View.VISIBLE);
            selectAllBtn.setVisibility(View.GONE);
            clearBtn.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.GONE);
            delBtn.setVisibility(View.GONE);
        }
    }

    /**
     * 显示已选项数目
     */
    private void setSelectNum() {
        int num = 0;
        for (Boolean isSelected : selectItems) {
            if (isSelected) {
                num++;
            }
        }
        titleTv.setText(String.format(Locale.getDefault(), getString(R.string.ipc_reg_album_select_count), num));
    }

    private void setSelectAll(boolean isSelected) {
        for (int i = 0; i < selectItems.size(); i++) {
            selectItems.set(i, isSelected);
        }
        picTestAdapter.setSelectedItems(selectItems);
        refreshGV();
        setSelectNum();
        selectAllBtn.setVisibility(isSelected ? View.GONE : View.VISIBLE);
        clearBtn.setVisibility(isSelected ? View.VISIBLE : View.GONE);
    }


    private void delSelections() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (!selectItems.contains(true)) {
            builder.setTitle(R.string.ipc_album_del_dialog_title).setMessage(R.string.ipc_album_del_dialog_msg_no_select).setPositiveButton(R.string.ipc_confirm, null).create().show();
        } else {
            builder.setTitle(R.string.ipc_album_del_dialog_title);
            builder.setMessage(R.string.ipc_album_del_dialog_msg_reconfirm);
            builder.setPositiveButton(R.string.ipc_confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    List<PicInfo> deleteLst = new LinkedList<>();
                    for (int i = 0; i < picInfoLst.size(); i++) {
                        if (selectItems.get(i)) {
                            if (!hasSamePic(deleteLst, picInfoLst.get(i))) {
                                deleteLst.add(picInfoLst.get(i));
                            }
                        }
                    }
                    if (deleteLst.size() < 1) {
                        return;
                    }
                    deletePicBatch(deleteLst);


                }
            });
            builder.setNegativeButton(R.string.ipc_cancle, null);
            builder.create().show();
        }
    }

    private boolean hasSamePic(List<PicInfo> lst, PicInfo picInfo) {
        return lst.contains(picInfo);
    }

    private void onDeletePicSuccess() {
        for (int i = 0; i < picInfoLst.size(); i++) {
            if (selectItems.get(i)) {
                picInfoLst.set(i, null);
            }
        }
        if (index < 0) {
            index = 0;
        }
        while (picInfoLst.contains(null)) {
            picInfoLst.remove(null);
        }
        selectItems = new ArrayList<>();
        for (int i = 0; i < picInfoLst.size(); i++) {
            selectItems.add(false);
        }
        Log.e(TAG, "onDeletePicSuccess    picInfoLst.size():" + picInfoLst.size());
        picTestAdapter.setSelectedItems(selectItems);
        picTestAdapter.setData(picInfoLst);
        refreshGV();
        if (picInfoLst.size() == 0) {
            picTestAdapter.setIsState(false);
            setChooseState(false);
            if (haveMoreData) {
                refreshPicData();
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setSelectNum();
                }
            });
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isChooseState) {
                titleTv.setText(R.string.ipc_album_select_one);
                quitChooseState();
                return true;
            } else {
                finish();
                return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    private void showToast(String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(PictureTestActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showToast(int s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(PictureTestActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isLoading = false;

    /**
     * 获取照片列表
     * @param startTime 查询的开始时间
     * @param endTime 查询的结束时间
     * @param pageStart 分页查找的第几页
     * @param pageSize 分页查找一页的大小
     * @param source 图片来源 0全部 1报警 2主动抓图
     */
    private void getPictureLst(long startTime, long endTime, int pageStart, int pageSize, int source) {
        Log.d(TAG, "getPictureLst  startTime:" + startTime + "  endTime:" + endTime + "  pageStart:" + pageStart
            + "  pageSize:" + pageSize);
        //一次请求还没有回复的时候不再进行请求
        if (isLoading) {
            return;
        }
        isLoading = true;
        IPCManager.getInstance().getDevice(iotId).queryDevPictureFileList(startTime, endTime, pageStart, pageSize,
            PicRequestTypeEnums.ALL.getCode(), source, new IoTCallback() {
                @Override
                public void onFailure(IoTRequest ioTRequest, Exception e) {
                    isLoading = false;
                    Log.d(TAG, "queryDevPictureFileList ToC onFailure:" + e.toString());
                    showToast(String.format(getString(R.string.ipc_reg_album_query_list_fail), e.toString()));
                }

                @Override
                public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                    isLoading = false;
                    Log.d(TAG, "queryDevPictureFileList ToC onResponse:" + ioTResponse.getCode() + "  ID:" + ioTResponse
                        .getId() + "   Data:" + ioTResponse.getData());
                    if (ioTResponse.getCode() == 200) {
                        if (ioTResponse.getData() != null && !"".equals(ioTResponse.getData().toString().trim())) {
                            JSONObject data = JSONObject.parseObject(String.valueOf(ioTResponse.getData()));
                            List<PicInfo> picLst = data.getJSONArray("pictureList").toJavaList(PicInfo.class);
                            if (picLst != null && picLst.size() > 0) {
                                if (picInfoLst == null) {
                                    picInfoLst = new ArrayList<>();
                                }
                                for (int j = 0; j < picLst.size(); j++) {
                                    if (hasSamePic(picInfoLst, picLst.get(j))) {
                                        continue;
                                    }
                                    picInfoLst.add(picLst.get(j));
                                }
                                //更新页数
                                index += 1;
                                //简单的一个判断，如果获取的图片数小于请求数时认为没有图片了
                                if (picLst.size() < pageSize) {
                                    haveMoreData = false;
                                }
                                refreshGV();
                            }
                        } else {
                            showToast(R.string.ipc_album_query_list_null);
                        }
                    } else {
                        showToast(String.format(getString(R.string.ipc_album_reg_query_list_err_info), ioTResponse.getCode(), ioTResponse.getData()));
                    }
                }
            });

    }

    /**
     * 批量删除图片
     * @param infoLst
     */
    private void deletePicBatch(List<PicInfo> infoLst) {

        List<String> lst = new LinkedList<>();
        for (PicInfo pic : infoLst) {
            lst.add(pic.getPictureId());
        }
        IPCManager.getInstance().batchDeleteDevPictureFile(iotId, lst, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                Log.e(TAG, "batchDeleteDevPictureFile ToC onFailure:" + e.toString());
                showToast(String.format(getString(R.string.ipc_reg_album_delete_err_info), e.toString()));
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                Log.d(TAG,
                    "batchDeleteDevPictureFile ToC onResponse:" + ioTResponse.getCode() + "  ID:" + ioTResponse
                        .getId() + "   msg:" + ioTResponse.getLocalizedMsg());
                if (ioTResponse.getCode() == 200) {
                    if (ioTResponse.getData() != null && !"".equals(ioTResponse.getData().toString().trim())) {
                        JSONObject json = JSON.parseObject(ioTResponse.getData().toString());
                        if(json != null){
                            if(json.containsKey("deleteCount")){
                                int deleteCount = json.getIntValue("deleteCount");
                                if(deleteCount == lst.size()){
                                    showToast(R.string.ipc_album_delete_success);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            onDeletePicSuccess();
                                        }
                                    });
                                }else if(deleteCount <= lst.size() && deleteCount > 0){
                                    showToast(R.string.ipc_album_delete_part_success);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            onDeletePicSuccess();
                                        }
                                    });
                                }else{
                                    showToast(R.string.ipc_album_delete_fail);
                                }
                            }
                        }
                    } else {
                        showToast(R.string.ipc_album_delete_fail_null);
                    }
                } else {
                    String localizedMsg = ioTResponse.getLocalizedMsg();
                    String msg = ioTResponse.getMessage();

                    showToast(String.format(getString(R.string.ipc_album_reg_delete_fail_code), ioTResponse.getCode(), localizedMsg, msg));
                }
            }
        });
    }

}
