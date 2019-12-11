package com.aliyun.iot.demo.ipcview.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.iot.demo.ipcview.R;
import com.aliyun.iot.demo.ipcview.activity.PictureTestActivity;
import com.aliyun.iot.demo.ipcview.beans.PicInfo;
import com.aliyun.iot.demo.ipcview.utils.ImageCache;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

/**
 * 图片适配器
 *
 * ！！！注意！！！
 * 注意本示例代码主要用于演示部分视频业务接口以及对应的效果
 * 代码中涉及的交互，UI以及代码框架请自行设计，示例代码仅供参考，稳定性请客户自行保证。
 *
 *
 * @author azad
 */
public class PicTestAdapter extends BaseAdapter implements PictureTestActivity.onViewScrollListener {
    private List<PicInfo> data;
    private LayoutInflater inflater;
    private boolean isState = false;
    private List<Boolean> selectedLst = Collections.synchronizedList(new ArrayList<Boolean>());
    private int ImgStart, ImgEnd;
    private boolean isFirstIn;
    private GridView gridView;

    public PicTestAdapter(Context context, List<PicInfo> data, GridView view) {
        inflater = LayoutInflater.from(context);
        this.data = data;
        isFirstIn = true;
        gridView = view;
    }


    public void setSelectedItems(ArrayList<Boolean> selectedLst) {
        this.selectedLst = selectedLst;
    }

    public void setData(List<PicInfo> data) {
        this.data = data;
    }

    public void setIsState(boolean isState) {
        this.isState = isState;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.pic_picture_item, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.pic_iv);
            holder.textView = (TextView) convertView.findViewById(R.id.title_tv);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.choose_cb);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.imageView.setTag(data.get(position).getThumbUrl());
        holder.checkBox.setVisibility(isState ? View.VISIBLE : View.GONE);
        if (selectedLst.size() != 0) {
            holder.checkBox.setChecked(selectedLst.get(position));
        }
        holder.textView.setText(data.get(position).getPictureTime());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
        showImage(position,holder.imageView, data.get(position).getThumbUrl());
        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;
        CheckBox checkBox;
        TextView textView;
    }

    private void showImage(int position, ImageView imageView, String url) {
        Bitmap bitmap = ImageCache.getInstance().getImage(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }else{
            imageView.setImageResource(R.drawable.errors_no_pic);
            ImgAsyncTask task = new ImgAsyncTask(url, position);
            task.execute(url);
            taskSet.add(task);
        }
    }

    private String changeTime(long timestamp) {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA).format(new Date(timestamp));
        return date;
    }


    private Set<ImgAsyncTask> taskSet = new HashSet<>();

    public void LoadImageByAsyncTask(int ImgStart, int ImgEnd) {
        String url;
        ImgAsyncTask task;
        for (int i = ImgStart; i < ImgEnd; i++) {
            url = data.get(i).getThumbUrl();
            Bitmap bitmap = ImageCache.getInstance().getImage(url);
            if (bitmap != null) {
                ImageView imageView = (ImageView) gridView
                        .findViewWithTag(url);
                imageView.setImageBitmap(bitmap);
            } else {
                task = new ImgAsyncTask(url, i);
                task.execute(url);
                taskSet.add(task);
            }

        }
    }

    private class ImgAsyncTask extends AsyncTask<String, Void, Bitmap> {
        int position;
        String url;

        public ImgAsyncTask(String url, int position) {
            this.url = url;
            this.position = position;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = downloadImage(url);
            if (bitmap != null) {
                ImageCache.getInstance().addImgIntoCache(url, bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView imageView = (ImageView) gridView.findViewWithTag(url);
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
            taskSet.remove(this);
        }
    }

    private Bitmap downloadImage(String url) {
        HttpURLConnection con = null;
        Bitmap bitmap = null;
        try {
            URL imgUrl = new URL(url);
            con = (HttpURLConnection) imgUrl.openConnection();
            // 设置请求方法，注意大写
            con.setRequestMethod("GET");
            // 设置连接超时
            con.setConnectTimeout(5000);
            // 设置读取超时
            con.setReadTimeout(5000);
            // 5、发送请求，与服务器建立连接
            con.connect();
            // 如果响应码为200
            if (con.getResponseCode() == 200) {
                bitmap = BitmapFactory.decodeStream(con.getInputStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return bitmap;
    }

    public void cancelAllTask() {
        if (taskSet != null) {
            for (ImgAsyncTask task : taskSet) {
                task.cancel(false);
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            LoadImageByAsyncTask(ImgStart, ImgEnd);
        } else {
            cancelAllTask();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        ImgStart = firstVisibleItem;
        ImgEnd = ImgStart + visibleItemCount;
        if (isFirstIn && visibleItemCount > 0) {
            LoadImageByAsyncTask(ImgStart, ImgEnd);
            isFirstIn = false;
        }
    }
}
