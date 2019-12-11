package com.aliyun.iot.demo.ipcview.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.aliyun.iot.demo.ipcview.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 *
 * ！！！注意！！！
 * 注意本示例代码主要用于演示部分视频业务接口以及对应的效果
 * 代码中涉及的交互，UI以及代码框架请自行设计，示例代码仅供参考，稳定性请客户自行保证。
 *
 *
 *
 * @Author: EverettLi.ll
 * @Date: 11/7/18
 * @Description: 视频截图预览窗口
 **/
public class SnapshotPreviewDialog extends Dialog implements View.OnClickListener {

    Bitmap bitmap;

    PhotoView photoView;
    Button closeBtn;
    Button saveBtn;

    public SnapshotPreviewDialog(@NonNull Context context) {
        super(context);
    }

    public SnapshotPreviewDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }



    protected SnapshotPreviewDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.snapshot_preview_dialog);
        photoView = findViewById(R.id.photo_view);
        closeBtn =findViewById(R.id.close_btn);
        saveBtn = findViewById(R.id.save_btn);
        closeBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
    }

    public void setImageBitmap(Bitmap bitmap){
        this.bitmap = bitmap;
        photoView.setImageBitmap(bitmap);
    }


    @Override
    public void onClick(View v) {
        if(v == closeBtn) {
            if (isShowing()) {
                dismiss();
            }
        }else if(v == saveBtn){
            SnapshotPreviewDialog.saveImageToGallery(getContext(), bitmap);
            Toast.makeText(getContext(), v.getContext().getString(R.string.ipc_dialog_saved), Toast.LENGTH_SHORT).show();
            dismiss();
        }
    }

    public static void saveImageToGallery(Context context, Bitmap bmp) {
        File appDir = new File(Environment.getExternalStorageDirectory(), "linkvision");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
    }
}
