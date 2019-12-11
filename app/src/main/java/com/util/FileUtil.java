package com.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.aliyun.iot.ilop.demo.DemoApplication;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.donkingliang.imageselector.utils.ImageSelectorUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.MEDIA_MOUNTED;

public class FileUtil {

    /**
     * 选择图片
     *
     * @param context
     */
    public static void openImage(final Activity context) {
        new AlertView(null, null, "取消", null,
                new String[]{"拍照", "从相册中选择"},
                context, AlertView.Style.ActionSheet, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                switch (position) {
                    case 0:
                        PermissionUtils.requestPermission(context, PermissionUtils.CODE_CAMERA, new PermissionUtils.PermissionGrant() {
                            @Override
                            public void onPermissionGranted(int requestCode) {
                                takePhoto(context);
                            }
                        });
                        break;
                    case 1:
                        PermissionUtils.requestPermission(context, PermissionUtils.CODE_READ_EXTERNAL_STORAGE, new PermissionUtils.PermissionGrant() {
                            @Override
                            public void onPermissionGranted(int requestCode) {
                                pickPhoto(context);
                            }
                        });

                        break;
                }
            }
        }
       ).show();
    }

    /**
     * 拍照获取相片
     **/
    public static void takePhoto(final Activity context) {
        ActivityCompat.requestPermissions(context,
                new String[]{Manifest.permission.CAMERA},
                1);
        PackageManager packageManager = context.getPackageManager();
        int permission = packageManager.checkPermission("android.permission.CAMERA", "com.juhao.home");
        if (PackageManager.PERMISSION_GRANTED != permission) {
            return;
        } else {
            // 图片名称 时间命名
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = new Date(System.currentTimeMillis());
            DemoApplication.imagePath = format.format(date);
            DemoApplication.cameraPath = FileUtil.getOwnFilesDir(context, Constance.CAMERA_PATH);
//            Uri imageUri = Uri.fromFile(new File(IssueApplication.cameraPath, IssueApplication.imagePath + ".jpg"));
            Uri apkUri =
                    FileProvider.getUriForFile(context, "com.juhao.home.hms.update.provider", new File(DemoApplication.cameraPath, DemoApplication.imagePath + ".jpg"));
            System.out.println("imageUri" + apkUri.toString());
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // 调用系统相机
            // 指定照片保存路径（SD卡）
            intent.putExtra(MediaStore.EXTRA_OUTPUT, apkUri);
            context.startActivityForResult(intent, Constance.PHOTO_WITH_CAMERA); // 用户点击了从相机获取
        }


    }


    /**
     * 从相册获取图片
     **/
    public static void pickPhoto(Activity context) {
        Intent intent = new Intent();
        intent.setType("image/*"); // 开启Pictures画面Type设定为image
        intent.setAction(Intent.ACTION_GET_CONTENT); // 使用Intent.ACTION_PICK这个Action则是直接打开系统图库
        context.startActivityForResult(intent, Constance.PHOTO_WITH_DATA); // 取得相片后返回到本画面
    }

    private static final  int REQUEST_CODE = 400;

    /**
     * 获取自定义sd卡上的文件目录
     *
     * @param context
     * @param cacheDir
     * @return 文件夹创建成功返回File or 文件夹创建失败返回null
     */
    public static File getOwnFilesDir(Context context, String cacheDir) {
        File appCacheDir = null;
        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                && context
                .checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED) {
            appCacheDir = new File(Environment.getExternalStorageDirectory(),
                    cacheDir);
        }
        if (appCacheDir == null
                || (!appCacheDir.exists() && !appCacheDir.mkdirs())) {
            appCacheDir = context
                    .getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }
        return appCacheDir;
    }
    /**
     * 选择图片
     *
     * @param context
     */
    public static void openSunImage(final Activity context) {
        new AlertView(null, null, "取消", null,
                new String[]{"拍照", "从相册中选择","小视频"},
                context, AlertView.Style.ActionSheet, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                switch (position) {
                    case 0:
                        PermissionUtils.requestPermission(context, PermissionUtils.CODE_CAMERA, new PermissionUtils.PermissionGrant() {
                            @Override
                            public void onPermissionGranted(int requestCode) {
                                takePhoto(context);
                            }
                        });
                        break;
                    case 1:
                        PermissionUtils.requestPermission(context, PermissionUtils.CODE_READ_EXTERNAL_STORAGE, new PermissionUtils.PermissionGrant() {
                            @Override
                            public void onPermissionGranted(int requestCode) {
                                ImageSelectorUtils.openPhoto(context, REQUEST_CODE, false, 3);
                            }
                        });
                        break;
//                    case 2:
//                        PermissionUtils.requestPermission(context, PermissionUtils.CODE_CAMERA, new PermissionUtils.PermissionGrant() {
//                            @Override
//                            public void onPermissionGranted(int requestCode) {
//                                context.startActivityForResult(new Intent(context, VideoShotActivity.class),300);
//                            }
//                        });
                }
            }
        }).show();
    }

    /**
     * 生成文件夹路径
     */
    public static String SDPATH = Environment.getExternalStorageDirectory()
            + "/TEST_PY/";
    /**
     * 删除指定文件夹中的所有文件
     */
    public static void deleteDir() {
        File dir = new File(SDPATH);
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;

        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete();
            else if (file.isDirectory())
                deleteDir();
        }
        dir.delete();
    }


}
