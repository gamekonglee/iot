package com.aliyun.iot.demo.ipcview.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.iot.demo.ipcview.R;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 展示照片的弹窗
 *
 *
 * ！！！注意！！！
 * 注意本示例代码主要用于演示部分视频业务接口以及对应的效果
 * 代码中涉及的交互，UI以及代码框架请自行设计，示例代码仅供参考，稳定性请客户自行保证。
 *
 *
 * @author azad
 */
public class PictureDialog {
    private String TAG = this.getClass().getSimpleName();

    private PictureDialog() {}

    private static class PictureDialogHolder {
        private static final PictureDialog dialog = new PictureDialog();
    }

    public static PictureDialog getInstance() {
        return PictureDialogHolder.dialog;
    }

    private Handler handler;

    private ImageView picIv;
    private TextView infoTv;

    public void openDialog(Context context, String url) {
        handler = new Handler(context.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        picIv.setVisibility(View.INVISIBLE);
                        infoTv.setVisibility(View.VISIBLE);
                        infoTv.setText(context.getString(R.string.ipc_dialog_url_request_err));
                        break;
                    case 1:
                        picIv.setVisibility(View.VISIBLE);
                        picIv.setImageBitmap((Bitmap)msg.obj);
                        break;
                    default:
                        break;
                }
            }
        };
        View view = LayoutInflater.from(context).inflate(R.layout.picture_dialog_layout, null);
        picIv = view.findViewById(R.id.iv_picture);
        infoTv = view.findViewById(R.id.tv_info);
        AlertDialog.Builder builder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle(context.getString(R.string.ipc_dialog_title_pic));
        builder.setView(view);
        builder.setNegativeButton(R.string.ipc_close, null);
        AlertDialog dialog = builder.create();
        dialog.show();
        getPic(url);

    }

    private void getPic(String path) {
        Thread t = new Thread() {
            @Override
            public void run() {

                try {
                    URL url = new URL(path);
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(5000);
                    con.setReadTimeout(5000);
                    con.connect();
                    if (con.getResponseCode() == 200) {
                        Bitmap bm = BitmapFactory.decodeStream(con.getInputStream());

                        Message msg = new Message();
                        msg.obj = bm;
                        msg.what = 1;
                        handler.sendMessage(msg);
                    } else {
                        Message msg = handler.obtainMessage();
                        msg.what = 0;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }
            }
        };
        t.start();
    }

}
