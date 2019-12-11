package com.juhao.home.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.BaseActivity;
import com.bean.ImageBean;
import com.facebook.imageutils.BitmapUtil;
import com.facebook.react.views.imagehelper.ImageSource;
import com.juhao.home.R;
import com.juhao.home.UIUtils;
import com.juhao.home.adapter.BaseAdapterHelper;
import com.juhao.home.adapter.QuickAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.view.EndOfGridView;
import com.view.MyToast;
import com.view.PMSwipeRefreshLayout;
import com.zhy.http.okhttp.utils.ImageUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ImageStoreActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private EndOfGridView gv_image;
    private PMSwipeRefreshLayout pullToRefresh;
    private List<ImageBean> imageBeanList;

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_image_store);
        gv_image = findViewById(R.id.gv_image);
        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(this);
        final int width=(UIUtils.getScreenWidth(this)-UIUtils.dip2PX(20))/3;
        QuickAdapter<ImageBean> adapter=new QuickAdapter<ImageBean>(this,R.layout.item_image) {
            @Override
            protected void convert(BaseAdapterHelper helper, final ImageBean item) {
                ImageView iv_img=helper.getView(R.id.iv_img);
                iv_img.setLayoutParams(new LinearLayout.LayoutParams(width,width));
//                helper.getView().setLayoutParams(new ViewGroup.LayoutParams(width,width));
//                FileInputStream fs = null;
//                try {
//                    fs = new FileInputStream(item.name);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
                ImageLoader.getInstance().displayImage("file://"+item.name,iv_img);
//                final Bitmap bitmap  =getLocalBitmap(item.name);
//                iv_img.setImageBitmap(bitmap);
                iv_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Intent intent=new Intent(ImageStoreActivity.this,ImageShowActivity.class);
//                        startActivity(intent);
                        final Dialog dialog=new Dialog(ImageStoreActivity.this,R.style.customDialog);
                        dialog.setContentView(R.layout.dialog_image_detail);
                        ImageView iv_img=dialog.findViewById(R.id.iv_img);
                        View ll_bg=dialog.findViewById(R.id.ll_bg);
                        ll_bg.setLayoutParams(new FrameLayout.LayoutParams(UIUtils.getScreenWidth(ImageStoreActivity.this),UIUtils.getScreenHeight(ImageStoreActivity.this)));
                        final Bitmap bitmap  =getLocalBitmap(item.name);
                        iv_img.setImageBitmap(bitmap);
                        ll_bg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();


                    }
                });
            }
        };
        gv_image.setAdapter(adapter);
        imageBeanList = new ArrayList<>();
        String SCAN_PATH= Environment.getExternalStorageDirectory().getAbsolutePath()+"/iotjuhaoImage";
        File folder = new File(SCAN_PATH);
        String[] allFiles=folder.list();
//        List<ImageBean> imageBeanList=new ArrayList<>();
        if(allFiles==null){
            MyToast.show(this,"暂无截图！");
            return;
        }
        for(int i=0;i<allFiles.length;i++)
        {
            ImageBean imageBean=new ImageBean();
            imageBean.name=SCAN_PATH+"/"+allFiles[i];
            imageBeanList.add(imageBean);
        }
        adapter.replaceAll(imageBeanList);

    }

    @Override
    protected void initData() {

    }
    public static Bitmap getLocalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = 10;
            Bitmap btp =BitmapFactory.decodeStream(fis,null,options);
            return btp;  ///把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }
    @Override
    public void onRefresh() {
    pullToRefresh.setRefreshing(false);
    }
}
