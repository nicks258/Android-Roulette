package com.zhy.sample_circlemenu.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnItemTouchListener;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerItemClickListener implements OnItemTouchListener {
    @Nullable
    private View childView;
    private int childViewPosition;
    private GestureDetector gestureDetector;
    protected OnItemClickListener listener;

    protected class GestureListener extends SimpleOnGestureListener {
        protected GestureListener() {
        }

        public boolean onSingleTapUp(MotionEvent event) {
            if (RecyclerItemClickListener.this.childView != null) {
                RecyclerItemClickListener.this.listener.onItemClick(RecyclerItemClickListener.this.childView, RecyclerItemClickListener.this.childViewPosition);
            }
            return true;
        }

        public void onLongPress(MotionEvent event) {
            if (RecyclerItemClickListener.this.childView != null) {
                RecyclerItemClickListener.this.listener.onItemLongPress(RecyclerItemClickListener.this.childView, RecyclerItemClickListener.this.childViewPosition);
            }
        }

        public boolean onDown(MotionEvent event) {
            return super.onDown(event);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int i);

        void onItemLongPress(View view, int i);
    }

    public static abstract class SimpleOnItemClickListener implements OnItemClickListener {
        public void onItemClick(View childView, int position) {
        }

        public void onItemLongPress(View childView, int position) {
        }
    }

    public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
        this.gestureDetector = new GestureDetector(context, new GestureListener());
        this.listener = listener;
    }

    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent event) {
        this.childView = view.findChildViewUnder(event.getX(), event.getY());
        this.childViewPosition = view.getChildAdapterPosition(this.childView);
        return this.childView != null && this.gestureDetector.onTouchEvent(event);
    }

    public void onTouchEvent(RecyclerView view, MotionEvent event) {
    }

    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }
}
