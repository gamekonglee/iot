package com.view;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class EndOfListView extends ListView {
    private OnEndOfListListener onEndOfListListener;

    private boolean hasWarned = false;
    private OnCanRefreshListener onCanRefreshListener;

    public EndOfListView(Context context) {
        super(context);
        init();
    }

    public EndOfListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EndOfListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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

    public void setOnEndOfListListener(OnEndOfListListener onEndOfListListener) {
        this.onEndOfListListener = onEndOfListListener;
    }
    public void setOnCanRefreshListener(OnCanRefreshListener onCanRefreshListener){
    this.onCanRefreshListener=onCanRefreshListener;
    }
    public static interface OnEndOfListListener<T> {
        void onEndOfList(T lastItem);
    }
    public static interface  OnCanRefreshListener<T>{
        void canRefresh(boolean refesh);
    }
}
