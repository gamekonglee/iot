package com.aliyun.iot.demo.ipcview.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aliyun.iot.demo.ipcview.R;
import com.aliyun.iot.demo.ipcview.beans.VideoInfo;
import com.aliyun.iot.demo.ipcview.constants.Constants;
import com.aliyun.iot.demo.ipcview.utils.TimeUtil;

import java.util.List;

/**
 * Created by guoweisong on 2018/8/16.
 *
 *
 *
 * ！！！注意！！！
 * 注意本示例代码主要用于演示部分视频业务接口以及对应的效果
 * 代码中涉及的交互，UI以及代码框架请自行设计，示例代码仅供参考，稳定性请客户自行保证。
 */

public class IpcHistoryListRecycleAdapter extends RecyclerView.Adapter<IpcHistoryListRecycleAdapter.ViewHolder> {

    private final String TAG = this.getClass().getSimpleName();
    private List<VideoInfo> videoList;
    private Context context;
    private OnHistoryRecordItemClickedListener onHistoryRecordItemClickedListener;
    private View footerView;
    private TextView footerTextView;

    /**
     * 说明是带有Footer的
     */
    public static final int TYPE_FOOTER = 1;

    /**
     * 说明是不带有header和footer的
     */
    public static final int TYPE_NORMAL = 2;

    public IpcHistoryListRecycleAdapter(List<VideoInfo> list) {
        this.videoList = list;
    }

    @Override
    public IpcHistoryListRecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        IpcHistoryListRecycleAdapter.ViewHolder viewHolder = null;
        if (footerView != null && viewType == TYPE_FOOTER) {
            viewHolder = new IpcHistoryListRecycleAdapter.ViewHolder(footerView, TYPE_FOOTER);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.ipc_history_video_item, parent, false);
            viewHolder = new IpcHistoryListRecycleAdapter.ViewHolder(view, TYPE_NORMAL);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(IpcHistoryListRecycleAdapter.ViewHolder holder, int position) {
        if (!isFooterView(position)) {
            VideoInfo videoInfo = videoList.get(position);
            if (videoInfo.type == Constants.EVENT_VIDEO) {
                holder.iotId = videoInfo.iotId;
                holder.fileName = videoInfo.fileName;
                holder.startTime.setText(videoInfo.beginTime);
                holder.endTime.setText(videoInfo.endTime);
                holder.startTime.setText(videoInfo.beginTime);
                holder.endTime.setText(videoInfo.endTime);
                holder.endTime.setVisibility(View.VISIBLE);
                holder.desc.setVisibility(View.GONE);

            } else if (videoInfo.type == Constants.CARD_VIDEO) {
                holder.fileName = videoInfo.fileName;
                holder.iotId = videoInfo.iotId;
                holder.type = videoInfo.type;
                holder.startTime.setText(TimeUtil.TimeStamp2Date(videoInfo.beginTime, "yyyy-MM-dd HH:mm:ss"));
                holder.endTime.setText(TimeUtil.TimeStamp2Date(videoInfo.endTime, "yyyy-MM-dd HH:mm:ss"));
                holder.endTime.setVisibility(View.VISIBLE);
                holder.desc.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (footerView == null) {
            return TYPE_NORMAL;
        }
        if (position == getItemCount() - 1) {
            //最后一个,应该加载Footer
            return TYPE_FOOTER;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        int count = (videoList == null ? 0 : videoList.size());
        if (footerView != null) {
            count++;
        }
        return count;
    }

    private boolean isFooterView(int position) {
        if (footerView != null && position == (getItemCount() - 1)) {
            return true;
        }
        return false;
    }

    public void revertFooterText() {
        if (footerTextView != null) {
            footerTextView.setText(R.string.btn_footer_more);
        }
    }

    public void setVideoList(List<VideoInfo> updateVideoList) {
        if (updateVideoList != null && updateVideoList.size() > 0) {
            for (VideoInfo videoInfo : updateVideoList) {
                if (!updateVideoList.contains(videoInfo)) {
                    videoList.add(videoInfo);
                }
            }
        }
    }

    public void setVideoChangeListener(OnHistoryRecordItemClickedListener videoChangeListener) {
        this.onHistoryRecordItemClickedListener = videoChangeListener;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public View getFooterView() {
        return footerView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
        notifyItemInserted(getItemCount() - 1);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView startTime;
        TextView endTime;
        TextView footerText;
        TextView desc;
        String iotId;
        String fileName;
        int type;

        ViewHolder(View itemView, int viewType) {
            super(itemView);
            if (TYPE_NORMAL == viewType) {
                startTime = itemView.findViewById(R.id.list_item_video_start_tv);
                endTime = itemView.findViewById(R.id.list_item_video_end_tv);
                desc = itemView.findViewById(R.id.list_item_event_desc_tv);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onHistoryRecordItemClickedListener != null) {
                            onHistoryRecordItemClickedListener.onItemClick(fileName);
                        }
                    }
                });
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (onHistoryRecordItemClickedListener != null) {
                            onHistoryRecordItemClickedListener.onItemLongClick(fileName);
                        }
                        return true;
                    }
                });
            } else {
                footerTextView = itemView.findViewById(R.id.list_item_video_footer);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        footerTextView.setText(R.string.btn_footer_loading);
                        if (onHistoryRecordItemClickedListener != null) {
                            onHistoryRecordItemClickedListener.scrollBottom();
                        }
                    }
                });
            }
        }
    }


    public interface OnHistoryRecordItemClickedListener {

        void scrollBottom();

        void onItemClick(String fileName);

        void onItemLongClick(String fileName);
    }
}
