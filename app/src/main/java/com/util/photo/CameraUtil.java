package com.util.photo;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.BaseActivity;
import com.BaseFragment;
import com.util.Constance;
import com.util.MyShare;
import com.view.MyToast;

import java.io.File;
import java.io.FileOutputStream;



/**
 * 拍照工具类
 * author HeYan
 * time 2015/12/29 16:57
 */
public class CameraUtil {
    private BaseActivity act;
    private BaseFragment fragment;
    private CameraDealListener listener;

    public CameraUtil(BaseActivity act, CameraDealListener listener) {
        this.act = act;
        this.listener = listener;
    }

    public CameraUtil(BaseFragment fragment, CameraDealListener listener) {
        this.fragment = fragment;
        this.listener = listener;
    }

    /**
     * 获取剪切缓存路径
     */
    public static String getCachePathCrop() {
        String path = getCachePath();
        if (path == null)
            return null;
        path = path + File.separator + "CropFile";
        File file = new File(path);
        // 判断文件夹存在与否，否则创建
        if (!file.exists())
            file.mkdirs();
        return path;
    }

    public final static String FILE_CACHE = "juhao";

    /**
     * 获取缓存路径
     */
    public static String getCachePath() {
        // 判断sd卡
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return null;
        }
        String path = Environment.getExternalStorageDirectory().getPath() + File.separator + FILE_CACHE;
        File file = new File(path);
        // 判断文件夹存在与否，否则创建
        if (!file.exists())
            file.mkdirs();
        return path;
    }

    //private Uri imageUri, cacheUri;
    private static final String URI_IMAGE = "CAMERA_URI_IMAGE", URI_CACHE = "CAMERA_URI_CACHE";// URI_CONTENT = "CAMERA_URI_CONTENT";

    private Context getContext() {
        if (act == null) return fragment.getContext();
        return act;
    }

    private boolean initPhotoData() {
        String path = getCachePathCrop();
        // 判断sd卡
        if (path == null) {
            MyToast.show(getContext(), "没有SD卡，不能拍照");
            return false;
        }
        // FileUtil.delAllFile(path);
        long time = System.currentTimeMillis();
        String imagePath = path + File.separator + "pic" + time + ".jpg";
        //String cachePath = path + File.separator + "cache" + time + ".jpg";
        //MyShare.get(getContext()).putString(URI_CACHE, "file://" + cachePath);
        MyShare.get(getContext()).putString(URI_IMAGE, "file://" + imagePath);
        return true;
    }

    public void onDlgCameraClick() {
        if (initPhotoData())
            try {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                ContentValues values = new ContentValues();
                Uri contentUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if (contentUri == null) return;
                MyShare.get(getContext()).putString(URI_CACHE, contentUri.toString());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                startActivityForResult(intent, Constance.FLAG_UPLOAD_TAKE_PICTURE);
            } catch (Exception e) {

            }
    }

    public void onDlgPhotoClick() {
        if (initPhotoData()) {
            try {
                Intent intent;
                if (Build.VERSION.SDK_INT < 19) {//api 19 and later, we can't use this way, demo just select from images
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                } else {
                    intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                }
                startActivityForResult(intent, Constance.FLAG_UPLOAD_CHOOICE_IMAGE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void cropImageUri(int aspectX, int aspectY, int unit) {
        cropImageUri(getCacheUri(), aspectX, aspectY, unit);
    }

    public void cropImageUri(Uri uri, int aspectX, int aspectY, int unit) {
        Uri imageUri = getImageUri();
        if (uri == null || imageUri == null) {
            Log.e(""+getClass(), "地址未初始化");
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("scale", true);// 是否保留比例
        intent.putExtra("scaleUpIfNeeded", true);//黑边
        intent.putExtra("crop", "true");//发送裁剪信号
        intent.putExtra("aspectX", aspectX);// X方向上的比例
        intent.putExtra("aspectY", aspectY);// Y方向上的比例
        intent.putExtra("outputX", aspectX * unit);//裁剪区的宽
        intent.putExtra("outputY", aspectY * unit);//裁剪区的高
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//URI 将URI指向相应的file:///…，
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, Constance.FLAG_UPLOAD_IMAGE_CUT);
    }

    private void startActivityForResult(Intent intent, int requestCode) {
        if (act == null) {
            fragment.startActivityForResult(intent, requestCode);
        } else {
            act.startActivityForResult(intent, requestCode);
        }
    }

    public Uri getCacheUri() {
        String path = MyShare.get(getContext()).getString(URI_CACHE);
        return path == null ? null : Uri.parse(path);
    }

    public Uri getImageUri() {
        String path = MyShare.get(getContext()).getString(URI_IMAGE);
        if(path==null){
            return null;
        }else {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                return FileProvider.getUriForFile(getContext(), "com.juhao.home.fileprovider", new File(path));
            }else {
            return  Uri.parse(path);
        }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            File f;
            Uri cacheUri;
            String path;
            try {
                switch (requestCode) {
                    case Constance.FLAG_UPLOAD_TAKE_PICTURE:
                        if (data != null && data.getData() != null) {
                            path = CameraDocument.getPath(getContext(), data.getData());
                        } else {
                            path = CameraDocument.getPath(getContext(), getCacheUri());
                        }
                        f = new File(path);
                        if (f.exists() && f.length() > 0) {
                            if (listener != null)
                                listener.onCameraTakeSuccess(path);
                        } else {
                            Log.e(getClass().toString(), "拍照存储异常");
                        }
                        break;
                    case Constance.FLAG_UPLOAD_CHOOICE_IMAGE:
                        if (data != null && data.getData() != null) {
                            path = CameraDocument.getPath(getContext(), data.getData());
                            f = new File(path);
                            if (f.exists() && f.length() > 0) {
                                if (listener != null)
                                    listener.onCameraPickSuccess(path);
                            } else {
                                Log.e(getClass().toString(), "选择的图片不存在");
                            }
                        }
                        break;
                    case Constance.FLAG_UPLOAD_IMAGE_CUT:
                        Uri imageUri = getImageUri();
                        f = new File(imageUri.getPath());
                        if (f.exists() && f.length() > 0) {
                            if (listener != null)
                                listener.onCameraCutSuccess(imageUri.getPath());
                        } else if (data != null && data.getData() != null) {
                            Log.e(getClass().toString(), "剪切其他情况");
                            path = CameraDocument.getPath(getContext(), data.getData());
                            if (listener != null)
                                listener.onCameraCutSuccess(path);
                        } else {
                            Log.e(getClass().toString(), "剪切未知情况");
                        }
                        break;
                }
            } catch (Exception e) {

            }
        }
    }

    private boolean saveFile(Uri uri, File f) {
        ContentResolver resolver = getContext().getContentResolver();
        try {
            if (!f.exists())
                f.createNewFile();
            Bitmap bmp;
            String path = CameraDocument.getPath(getContext(), uri);
            bmp = BitmapFactory.decodeFile(path);
            if (bmp == null || bmp.getWidth() < 1) {
                return false;
            }
            FileOutputStream fOut = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            return true;
        } catch (Exception e) {
            Log.e(getClass().toString(), "saveFile Error");
        }
        return false;
    }

    public interface CameraDealListener {
        void onCameraTakeSuccess(String uri);

        void onCameraPickSuccess(String uri);

        void onCameraCutSuccess(String uri);
    }
}
