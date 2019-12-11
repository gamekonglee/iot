package com.juhao.home.scene;

import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.BaseActivity;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.ilop.demo.DemoApplication;
import com.bean.Scenes;
import com.bean.ScenesBean;
import com.google.gson.Gson;
import com.huxq17.handygridview.HandyGridView;
import com.huxq17.handygridview.listener.IDrawer;
import com.huxq17.handygridview.listener.OnItemCapturedListener;
import com.huxq17.handygridview.scrollrunner.OnItemMovedListener;
import com.juhao.home.R;
import com.juhao.home.UIUtils;
import com.juhao.home.adapter.DragRecyclerAdapter;
import com.util.ApiClientForIot;
import com.util.Constance;
import com.view.MyToast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DragSortSceneActivity extends BaseActivity {

    private HandyGridView dragSortGridView;
    private MyAdapter dragAdapter;
    private boolean isAuto;
    private BookShelfTouchHelper touchHelper;
    private DragRecyclerAdapter myDragAdapter;
    private RecyclerView recycle_view;
    private int[] sorts;
    private TextView tv_title;
//    private String title;

    @Override
    protected void InitDataView() {
            tv_title.setText(isAuto?getString(R.string.str_auto):getString(R.string.str_one_key_do));
    }

    @Override
    protected void initController() {

    }
    JSONArray newOrders=new JSONArray();
    @Override
    protected void initView() {
        setContentView(R.layout.activity_sort_scene);
        dragSortGridView = findViewById(R.id.gv_sort);
        tv_title = findViewById(R.id.tv_title);

        recycle_view = findViewById(R.id.recycle_view);
        // 定义一个线性布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(this);
        // 设置布局管理器
        recycle_view.setLayoutManager(manager);

        recycle_view.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                return false;
            }
        });
        touchHelper = new BookShelfTouchHelper(new TouchCallback(new OnItemTouchCallbackListener() {
            @Override
            public void onSwiped(int position) {
                //处理划动删除操作
                if(scenes != null && scenes.size() >= 0 && position < scenes.size()) {
                    scenes.remove(position);
                    myDragAdapter.notifyItemRemoved(position);
                }
            }

            @Override
            public boolean onMove(int srcPosition, int targetPosition) {
                //处理拖拽事件
                if(scenes == null || scenes.size() == 0) {
                    return false;
                }
                if(srcPosition >= 0 && srcPosition < scenes.size() && targetPosition >= 0 && targetPosition < scenes.size()) {

                    int temp=sorts[srcPosition];
                    sorts[srcPosition]=sorts[targetPosition];
                    sorts[targetPosition]=temp;

                    //交换数据源两个数据的位置
                    Collections.swap(scenes,srcPosition,targetPosition);
                    //更新视图
                    myDragAdapter.notifyItemMoved(srcPosition,targetPosition);
                    //消费事件
                    return true;
                } else {
                    return false;
                }
            }
        }));
        touchHelper.setEnableDrag(true);
        touchHelper.setEnableSwipe(false);
        touchHelper.attachToRecyclerView(recycle_view);

        //长按item响应该item的拖动排序,默认是触摸就开始拖动
        dragSortGridView.setDrawer(new IDrawer() {
            @Override
            public void onDraw(Canvas canvas, int width, int height) {

            }
        },true);
        dragSortGridView.setMode(HandyGridView.MODE.LONG_PRESS);
        dragSortGridView.setOnItemCapturedListener(new OnItemCapturedListener() {
            @Override
            public void onItemCaptured(View v, int position) {

            }

            @Override
            public void onItemReleased(View v, int position) {

            }
        });
        getSceneList();


    }

    @Override
    public void save(View v) {
        super.save(v);
        for(int i=0;i<sorts.length;i++){
            if(sorts[i]!=i){
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("fromOrder",(i+1));
            jsonObject.put("toOrder",(sorts[i]+1));
            jsonObject.put("sceneId",scenes.get(i).getId());
            newOrders.add(jsonObject);
            }
        }


//        JSONObject jsonObject2=new JSONObject();
//        jsonObject2.put("fromOrder",targetPosition);
//        jsonObject2.put("toOrder",srcPosition);
//        jsonObject2.put("sceneId",scenes.get(targetPosition).getId());



//        newOrders.add(jsonObject2);

//        DemoApplication.newOrders=newOrders;
        Map<String,Object>map=new HashMap<>();
        map.put("groupId",isAuto?"1":"0");
        map.put("type","ilop");
        map.put("newOrders",newOrders);
        ApiClientForIot.getIotClient("/scene/list/reorder", "1.0.5", map, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, final IoTResponse ioTResponse) {
            if(ioTResponse.getCode()==200){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                MyToast.show(DragSortSceneActivity.this,getString(R.string.str_excute_success));
                    }
                });
                finish();
            }else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.show(DragSortSceneActivity.this,ioTResponse.getLocalizedMsg());
                    }
                });
            }
            }
        });

    }

    int page=1;
    List<Scenes> scenes=new ArrayList<>();

    private void getSceneList() {
        Map<String, Object> maps = new HashMap<>();
        maps.put("pageNo",page);
        maps.put("pageSize","40");
        maps.put("groupId",!isAuto?"0":"1");

        ApiClientForIot.getIotClient("/scene/list/get", "1.0.5", maps, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse response) {
                Object data = response.getData();
                if(data!=null){
                    ScenesBean scenesBean=new Gson().fromJson(data.toString(),ScenesBean.class);
                    List<Scenes> temp=scenesBean.getScenes();
                    if(temp==null||temp.size()==0){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                        return;
                    }
                    if(scenesBean.getPageNo()==1){
                        scenes =temp;
                    }else {
                        scenes.addAll(temp);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dragAdapter = new MyAdapter();
                            dragSortGridView.setAdapter(dragAdapter);
                            dragSortGridView.setNumColumns(1);
                            myDragAdapter = new DragRecyclerAdapter(DragSortSceneActivity.this,scenes);
                            recycle_view.setAdapter(myDragAdapter);
                            sorts = new int[scenes.size()];
                            for(int i=0;i<scenes.size();i++){
                                sorts[i]=(i);
                            }

                        }
                    });
                }else {
                }
            }
        });
    }

    @Override
    protected void initData() {
        isAuto = getIntent().getBooleanExtra(Constance.is_auto,false);
//        title = getIntent().getStringExtra(Constance.title);
    }


    class MyAdapter extends DragAdapter {
        @Override
        public void onDataModelMove(int from, int to) {
            Scenes s = scenes.remove(from);
            scenes.add(to, s);
        }

        @Override
        public int getCount() {
            return scenes.size();
        }

        @Override
        public Scenes getItem(int position) {
            return scenes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if (convertView == null) {
                convertView=View.inflate(DragSortSceneActivity.this,R.layout.item_sort_scene,null);
//                FrameLayout frameLayout = new FrameLayout(DragSortSceneActivity.this);
                textView = convertView.findViewById(R.id.tv_name);
                convertView.setTag(textView);
            } else {
                textView = (TextView) convertView.getTag();
            }
            convertView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.dip2PX(55)));
            textView.setText(getItem(position).getName());
            return convertView;
        }

        @Override
        public void onItemMoved(int from, int to) {
            Scenes s = scenes.remove(from);
            scenes.add(to, s);
        }

        @Override
        public boolean isFixed(int position) {
            return false;
        }
    }
    public abstract class DragAdapter extends BaseAdapter implements OnItemMovedListener {

        /**
         *
         * @描述:当从from排序被拖到to排序时的处理方式,请对相应的数据做处理。
         *
         * @param from
         * @param to
         * @作者 [pWX273343] 2015年6月24日
         */
        public abstract void onDataModelMove(int from, int to);

        /**
         * 复制View使用的方法,默认直接使用getView方法获取
         * @param position
         * @param convertView
         * @param parent
         * @return
         */
        public View copyView(int position, View convertView, ViewGroup parent) {
            return null;
        }

        /**
         * 是否启用copyView方法
         * @return true 使用copyView复制 false 使用getView直接获取镜像
         */
        public boolean isUseCopyView() {
            return false;
        }
    }
    public class BookShelfTouchHelper extends ItemTouchHelper {


        private TouchCallback callback;

        public BookShelfTouchHelper(TouchCallback callback) {
            super(callback);
            this.callback = callback;
        }

        public void setEnableDrag(boolean enableDrag) {
            callback.setEnableDrag(enableDrag);
        }

        public void setEnableSwipe(boolean enableSwipe) {
            callback.setEnableSwipe(enableSwipe);
        }
    }

    public class TouchCallback extends ItemTouchHelper.Callback {

        private boolean isEnableSwipe;//允许滑动
        private boolean isEnableDrag;//允许拖动
        private OnItemTouchCallbackListener callbackListener;//回调接口

        public TouchCallback(OnItemTouchCallbackListener callbackListener) {
            this.callbackListener = callbackListener;
        }

        /**
         * 滑动或者拖拽的方向，上下左右
         * @param recyclerView 目标RecyclerView
         * @param viewHolder 目标ViewHolder
         * @return 方向
         */
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {// GridLayoutManager
                // flag如果值是0，相当于这个功能被关闭
                int dragFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlag = 0;
                return makeMovementFlags(dragFlag, swipeFlag);
            } else if (layoutManager instanceof LinearLayoutManager) {// linearLayoutManager
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                int orientation = linearLayoutManager.getOrientation();

                int dragFlag = 0;
                int swipeFlag = 0;

                if (orientation == LinearLayoutManager.HORIZONTAL) {//横向布局
                    swipeFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                    dragFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                } else if (orientation == LinearLayoutManager.VERTICAL) {//纵向布局
                    dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                    swipeFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                }
                return makeMovementFlags(dragFlag, swipeFlag);
            }
            return 0;
        }

        /**
         * 拖拽item移动时产生回调
         * @param recyclerView 目标RecyclerView
         * @param viewHolder 拖拽的item对应的viewHolder
         * @param target 拖拽目的地的ViewHOlder
         * @return 是否消费拖拽事件
         */
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            if(this.callbackListener != null) {
                this.callbackListener.onMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());
            }
            return false;
        }

        /**
         * 滑动删除时回调
         * @param viewHolder 当前操作的Item对应的viewHolder
         * @param direction 方向
         */
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            if(this.callbackListener != null) {
                this.callbackListener.onSwiped(viewHolder.getAdapterPosition());
            }
        }

        /**
         * 是否可以长按拖拽
         * @return
         */
        @Override
        public boolean isLongPressDragEnabled() {
            return isEnableDrag;
        }

        /**
         * 是否可以滑动删除
         */
        @Override
        public boolean isItemViewSwipeEnabled() {
            return isEnableSwipe;
        }

        public void setEnableDrag(boolean enableDrag) {
            this.isEnableDrag = enableDrag;
        }

        public void setEnableSwipe(boolean enableSwipe) {
            this.isEnableSwipe = enableSwipe;
        }
    }
    public interface OnItemTouchCallbackListener {
        /**
         * 当某个Item被滑动删除时回调
         */
        void onSwiped(int adapterPosition);

        /**
         * 当两个Item位置互换的时候被回调(拖拽)
         * @param srcPosition    拖拽的item的position
         * @param targetPosition 目的地的Item的position
         * @return 开发者处理了操作应该返回true，开发者没有处理就返回false
         */
        boolean onMove(int srcPosition, int targetPosition);
    }
}
