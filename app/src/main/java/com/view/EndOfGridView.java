package com.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ListAdapter;

/**
 * Created by gklee on 2016/12/22.
 */

public class EndOfGridView extends GridView {
    private EndOfListView.OnEndOfListListener onEndOfListListener;

    private boolean hasWarned = false;
    private OnCanRefreshListener onCanRefreshListener;
    public EndOfGridView(Context context) {
        super(context);
        init();
    }

    public EndOfGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EndOfGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(onCanRefreshListener!=null){
                    if(firstVisibleItem==0){
                        onCanRefreshListener.canRefresh(true);
                    }else {
                        onCanRefreshListener.canRefresh(false);
                    }
                }
                if (hasWarned
                        || view.getLastVisiblePosition() != totalItemCount - 1
                        || onEndOfListListener == null)
                    return;

                hasWarned = true;
                Object lastItem = view.getItemAtPosition(totalItemCount - 1);
                if (lastItem != null || totalItemCount == 0)
                    onEndOfListListener.onEndOfList(lastItem);
            }
        });
    }

    @Override
    protected void handleDataChanged() {
        super.handleDataChanged();
        hasWarned = false;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        hasWarned = false;
    }

    public void setOnEndOfListListener(EndOfListView.OnEndOfListListener onEndOfListListener) {
        this.onEndOfListListener = onEndOfListListener;
    }

    public static interface OnEndOfListListener<T> {
        void onEndOfList(T lastItem);
    }
    public void setOnCanRefreshListener(OnCanRefreshListener onCanRefreshListener){
        this.onCanRefreshListener=onCanRefreshListener;
    }
    public static interface  OnCanRefreshListener<T>{
        void canRefresh(boolean refesh);
    }
}
