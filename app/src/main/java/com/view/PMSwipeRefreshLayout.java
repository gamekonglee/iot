package com.view;

import android.content.Context;
import androidx.core.view.ViewCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;


public class PMSwipeRefreshLayout extends SwipeRefreshLayout {

    //实际需要滑动的child view
    private View mScrollUpChild;

    public PMSwipeRefreshLayout(Context context) {
        super(context);
    }

    public PMSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isEnabled()) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        if (!isEnabled()) {
            return false;
        }
        return super.onTouchEvent(arg0);
    }

    public void setScrollUpChild(View view) {
        mScrollUpChild = view;
    }

    @Override
    public boolean canChildScrollUp() {
        if (mScrollUpChild != null) {
            if (android.os.Build.VERSION.SDK_INT < 14) {
                if (mScrollUpChild instanceof AbsListView) {
                    final AbsListView absListView = (AbsListView) mScrollUpChild;
                    return absListView.getChildCount() > 0
                            && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                            .getTop() < absListView.getPaddingTop());
                } else {
                    return ViewCompat.canScrollVertically(mScrollUpChild, -1) || mScrollUpChild.getScrollY() > 0;
                }
            } else {
                return ViewCompat.canScrollVertically(mScrollUpChild, -1);
            }
        }
        return super.canChildScrollUp();
    }


}