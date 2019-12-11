package com.juhao.home.suggestion;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.BaseActivity;
import com.alibaba.sdk.android.openaccount.OpenAccountSDK;
import com.alibaba.sdk.android.openaccount.callback.LoginCallback;
import com.alibaba.sdk.android.openaccount.model.OpenAccountSession;
import com.alibaba.sdk.android.openaccount.ui.OpenAccountUIService;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.ilop.demo.DemoApplication;
import com.bean.AccountDevDTO;
import com.bean.PostImageVideoBean;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.donkingliang.imageselector.utils.ImageSelectorUtils;
import com.facebook.soloader.SysUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.juhao.home.R;
import com.juhao.home.UIUtils;
import com.juhao.home.adapter.BaseAdapterHelper;
import com.juhao.home.adapter.QuickAdapter;
import com.juhao.home.ui.UserInfoActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.util.ApiClientForIot;
import com.util.Constance;
import com.util.FileUtil;
import com.util.ImageUtil;
import com.util.LogUtils;
import com.util.SystemUtil;
import com.util.photo.CameraUtil;
import com.view.MyToast;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.util.Constance.PHOTO_WITH_CAMERA;


public class SuggestDeviceActivity extends BaseActivity implements View.OnClickListener {

    private EditText et_suggestion;
    private TextView tv_device_select;
    private TextView tv_input_count;
    private TextView tv_add_img;
    private EditText et_mobile;
    private Button btn_submit;
    private LinearLayout ll_img;
    private QuickAdapter<AccountDevDTO> adapter;
    private List<AccountDevDTO> accountDevDTOS;
    private AccountDevDTO currentDev;
    private int type=1;
    private String path;

    @Override
    protected void InitDataView() {

    }

    @Override
    protected void initController() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_suggest_device);
        et_suggestion = findViewById(R.id.et_suggestion);
        tv_device_select = findViewById(R.id.tv_device_select);
        tv_input_count = findViewById(R.id.tv_input_count);
        tv_add_img = findViewById(R.id.tv_add_img);
        et_mobile = findViewById(R.id.et_mobile);
        btn_submit = findViewById(R.id.btn_submit);
        ll_img = findViewById(R.id.ll_img);
        tv_add_img.setOnClickListener(this);
        tv_device_select.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        et_mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkBtnEnable();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        et_suggestion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkBtnEnable();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void checkBtnEnable() {
        btn_submit.setEnabled(false);
        if(currentDev==null||currentDev.getProductKey()==null||currentDev.getIotId()==null){
            return;
        }
        String content=et_suggestion.getText().toString();
        if(TextUtils.isEmpty(content)){
            return;
        }
        String  contact=et_mobile.getText().toString();
        if(TextUtils.isEmpty(contact)){
            return;
        }
        btn_submit.setEnabled(true);
    }

    @Override
    protected void initData() {
        type=getIntent().getIntExtra(Constance.type,1);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_add_img:
                addImg();
                break;
            case R.id.tv_device_select:
                final Dialog dialog=UIUtils.showBottomInDialog(this,R.layout.dialog_device_select,UIUtils.dip2PX(375));
                GridView gv_device=dialog.findViewById(R.id.gv_device);
                adapter = new QuickAdapter<AccountDevDTO>(this,R.layout.item_dev_suggest) {
                    @Override
                    protected void convert(BaseAdapterHelper helper, AccountDevDTO item) {
                        String productName=item.getNickName();
                        if(productName==null)productName=item.getProductName();
                        if(productName==null)productName=item.getName();
                        helper.setText(R.id.tv_name,productName);
                        ImageView iv_img=helper.getView(R.id.iv_img);
                        ImageLoader.getInstance().displayImage(item.getCategoryImage(),iv_img);
                    }
                };
                gv_device.setAdapter(adapter);
                Map<String, Object> maps = new HashMap<>();
                maps.put("pageSize","20");
                maps.put("pageNo", 1);

                ApiClientForIot.getIotClient("/uc/listBindingByAccount", "1.0.2", maps, new IoTCallback() {
                    @Override
                    public void onFailure(IoTRequest ioTRequest, Exception e) {

                    }

                    @Override
                    public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                        Object data = ioTResponse.getData();
                        if (null != data) {
                            if(data instanceof JSONObject){
                                JSONObject result= (JSONObject) data;
                                JSONArray listData= null;
                                try {
                                    listData = result.getJSONArray(Constance.data);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
//                        mDeviceList = parseDeviceListFromSever((JSONArray) data);
                                accountDevDTOS = new Gson().fromJson(((JSONArray)listData).toString(),new TypeToken<List<AccountDevDTO>>(){}.getType());
                                if(accountDevDTOS ==null|| accountDevDTOS.size()==0){

//                                    mHandler.sendEmptyMessage(1);
                                    return;
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.replaceAll(accountDevDTOS);
                                    }
                                });
                            }
                        }
                    }
                });
                gv_device.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        currentDev = accountDevDTOS.get(i);
                        String productName=currentDev.getNickName();
                        if(productName==null)productName=currentDev.getProductName();
                        if(productName==null)productName=currentDev.getName();
                        currentDev.setName(productName);
                        tv_device_select.setText(productName);
                        checkBtnEnable();
                        dialog.dismiss();
                    }
                });
                break;
            case R.id.btn_submit:
                if(currentDev==null||currentDev.getProductKey()==null||currentDev.getIotId()==null){
                    MyToast.show(this,getString(R.string.str_atleastone));
                    return;
                }
                String content=et_suggestion.getText().toString();
                if(TextUtils.isEmpty(content)){
                    MyToast.show(this,getString(R.string.str_input_suggest_info));
                    return;
                }
                String  contact=et_mobile.getText().toString();
                if(TextUtils.isEmpty(contact)){
                    MyToast.show(this,getString(R.string.str_input_contact_info));
                    return;
                }
                String mobileSystem= SystemUtil.getSystemVersion();
                String appVersion=UIUtils.getVerName(this);
                String productKey=currentDev.getProductKey();
                String iotId=currentDev.getIotId();
                String mobileModel= SystemUtil.getSystemModel();
                String topic=getString(R.string.str_device_suggest);
                String devicename=currentDev.getDeviceName();
                Map<String,Object> map=new HashMap<>();
                map.put("content",content);
                map.put("contact",contact);
                map.put("mobileSystem",mobileSystem);
                map.put("appVersion",appVersion);
                map.put("productKey",productKey);
                map.put("iotId",iotId);
                map.put("mobileModel",mobileModel);
                map.put("topic",topic);
                map.put("type",type);
                map.put("devicename",devicename);
                ApiClientForIot.getIotClient("/feedback/add", "1.0.1", map, new IoTCallback() {
                    @Override
                    public void onFailure(IoTRequest ioTRequest, Exception e) {

                    }

                    @Override
                    public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                        if(ioTResponse.getCode()==200){
                            MyToast.show(SuggestDeviceActivity.this,getString(R.string.str_excute_success));
                            finish();
                        }
                    }
                });
                break;

        }
    }

    /**
     * 头像
     */
    private CameraUtil camera;

    public void addImg() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
        //图片剪裁的一些设置
        UCrop.Options options = new UCrop.Options();
        //图片生成格式
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        //图片压缩比
        options.setCompressionQuality(80);
//
//            new PickConfig.Builder(PostedImageActivity.this)
//                    .maxPickSize(9)//最多选择几张
//                    .isneedcamera(true)//是否需要第一项是相机
//                    .spanCount(4)//一行显示几张照片
//                    .actionBarcolor(Color.parseColor("#EE7600"))//设置toolbar的颜色
//                    .statusBarcolor(Color.parseColor("#EE7600")) //设置状态栏的颜色(5.0以上)
//                    .isneedcrop(false)//受否需要剪裁
//                    .setUropOptions(options) //设置剪裁参数
//                    .isSqureCrop(true) //是否是正方形格式剪裁
//                    .pickMode(PickConfig.MODE_MULTIP_PICK)//单选还是多选
//                    .build();
        //限数量的多选(比喻最多9张)

        FileUtil.openSunImage(this);

//        FileUtil.openImage(this);
//
//        if (camera == null) {
//            camera = new CameraUtil(this, new CameraUtil.CameraDealListener() {
//                @Override
//                public void onCameraTakeSuccess(String path) {
//                    camera.cropImageUri(1, 1, 256);
//                }
//                @Override
//                public void onCameraPickSuccess(String path) {
//                    Uri uri ;
//                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
//                        uri= FileProvider.getUriForFile(SuggestDeviceActivity.this, "com.juhao.home.fileprovider", new File(path));
//                    }else {
//                        uri = Uri.parse("file://" + path);
//                    }
//                    camera.cropImageUri(uri, 1, 1, 256);
//                }
//
//                @Override
//                public void onCameraCutSuccess(final String uri) {
//                    File file = new File(uri);
//                    Uri uriTemp;
//                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
//                        uriTemp= FileProvider.getUriForFile(SuggestDeviceActivity.this, "com.juhao.home.fileprovider", new File(uri));
//                    }else {
//                        uriTemp = Uri.parse("file://" + uri);
//                    }
//
//                    iv_head.setImageURI(uriTemp);
//                    upLoad(uri.toString());
//                }
//            });
//        }

//        mHeadView.show();
    }



    @Override
    protected void onDestroy() {
        //删除文件夹及文件
        FileUtil.deleteDir();
        super.onDestroy();
    }
    // 用于保存图片资源文件
    public List<PostImageVideoBean> lists = new ArrayList<PostImageVideoBean>();
    private ArrayList<String> images=new ArrayList<>();
    public List<File> files=new ArrayList<>();
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constance.REQUEST_CODE && data != null) {
            ArrayList<String> temps=data.getStringArrayListExtra(
                    ImageSelectorUtils.SELECT_RESULT);
            if(lists!=null&&temps.size()+lists.size()>3){
                MyToast.show(this,"上传图片最多3张");
                return;
            }
            images.addAll(temps);
            //获取选择器返回的数据
            for (int i = 0; i < temps.size(); i++) {
                PostImageVideoBean postImageVideoBean=new PostImageVideoBean();
                postImageVideoBean.isVideo=false;
                postImageVideoBean.bitmap= ImageUtil.adjustImage(this,temps.get(i));
                lists.add(postImageVideoBean);
            }
            // 更新GrideView
//            gvAdapter.setList();
            setImageGallery(lists);
        }else if(requestCode==PHOTO_WITH_CAMERA){
            String status = Environment.getExternalStorageState();
            if (status.equals(Environment.MEDIA_MOUNTED)) { // 是否有SD卡
                File imageFile = new File(DemoApplication.cameraPath, DemoApplication.imagePath + ".jpg");
                if (imageFile.exists()) {
//                    String imageURL = "file://" + imageFile.toString();
                    String imageURL =  imageFile.toString();
                    if(images!=null&&images.size()>=3){
                        MyToast.show(this,"上传图片最多3张");
                        return;
                    }
                    images.add(imageURL);
                    PostImageVideoBean postImageVideoBean=new PostImageVideoBean();
                    postImageVideoBean.isVideo=false;
                    postImageVideoBean.bitmap= BitmapFactory.decodeFile(imageURL);
                    lists.add(postImageVideoBean);
                    setImageGallery(lists);
                    DemoApplication.imagePath = null;
                    DemoApplication.cameraPath = null;
                } else {
                }
            } else {
            }
        }else if(requestCode==300&&resultCode==300){
            if(images!=null&&images.size()>=3){
                MyToast.show(this,"上传图片最多3张");
                return;
            }
            path = data.getStringExtra(Constance.path);
            files.add(new File(path));
            LogUtils.logE("path", path);
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            media.setDataSource(path);
//            View view=View.inflate(this,R.layout.video_start,null);
//            View imageView=view.findViewById(R.id.iv_img);
//            imageView.setBackground(new BitmapDrawable(bitmap));
//            Bitmap temp=ImageUtil.loadBitmapFromView(view);
            PostImageVideoBean postImageVideoBean=new PostImageVideoBean();
            postImageVideoBean.isVideo=true;
            postImageVideoBean.bitmap=media.getFrameAtTime();
            postImageVideoBean.path= path;
            lists.add(postImageVideoBean);
            DemoApplication.imagePath = null;
            DemoApplication.cameraPath = null;
            setImageGallery(lists);
        }
    }

    private void setImageGallery(List<PostImageVideoBean> lists) {
        ll_img.removeAllViews();
        for(int i=0;i<lists.size();i++){
            ImageView imageView=new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(UIUtils.dip2PX(85),UIUtils.dip2PX(85));
            layoutParams.setMargins(0,0,UIUtils.dip2PX(15),0);
            imageView.setLayoutParams(layoutParams);
            imageView.setImageBitmap(lists.get(i).bitmap);
            ll_img.addView(imageView);
        }
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (camera != null)
//            camera.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == -1) {
//            switch (requestCode) {
//                case Constance.PHOTO_WITH_CAMERA: {// 拍照获取图片
//                    String status = Environment.getExternalStorageState();
//                    if (status.equals(Environment.MEDIA_MOUNTED)) { // 是否有SD卡
//                        File imageFile = new File(DemoApplication.cameraPath, DemoApplication.imagePath + ".jpg");
//                        imageURL = "file://" + imageFile;
//                        final Uri uri = Uri.parse("file://" + imageFile);
//                        iv_head.setImageURI(uri);
//                        upLoad(uri.toString());
//                    }
//                }
//                break;
//                case Constance.PHOTO_WITH_DATA: // 从图库中选择图片
//                    // 照片的原始资源地址
//                    imageURL = data.getData().toString();
//                    iv_head.setImageURI(data.getData());
//                    upLoad(imageURL.toString());
//                    break;
//                case Constance.FLAG_UPLOAD_IMAGE_CUT:
//                    final Uri uri=data.getData();
//                    iv_head.setImageURI(uri);
//                    upLoad(uri.toString());
//                    break;
//            }
//        }else if(requestCode== Constance.FLAG_UPLOAD_IMAGE_CUT){
//            final Uri uri=data.getData();
//            iv_head.setImageURI(uri);
//            upLoad(uri.toString());
//        }
//    }

}
