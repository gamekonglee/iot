package com.juhao.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bean.Scenes;
import com.juhao.home.R;

import java.util.List;

public class DragRecyclerAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<Scenes> mEntityList;

    public DragRecyclerAdapter (Context context, List<Scenes> entityList){
        this.mContext = context;
        this.mEntityList = entityList;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_sort_scene, parent, false);
        return new DemoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Scenes entity = mEntityList.get(position);
        ((DemoViewHolder)holder).mText.setText(entity.getName());
    }

    @Override
    public int getItemCount() {
        return mEntityList.size();
    }
    private class DemoViewHolder extends RecyclerView.ViewHolder{

        private TextView mText;

        public DemoViewHolder(View itemView) {
            super(itemView);
            mText = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }
}
