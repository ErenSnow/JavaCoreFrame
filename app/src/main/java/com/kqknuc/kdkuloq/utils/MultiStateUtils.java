package com.kqknuc.kdkuloq.utils;

import android.view.View;

import com.kennyc.view.MultiStateView;
import com.kqknuc.kdkuloq.utils.multi.MultiStateListener;
import com.kqknuc.kdkuloq.utils.multi.OnClickListenerContinuous;

/**
 * MultiState多种界面状态工具栏
 */
public class MultiStateUtils {

    /**
     * 展示进度
     */
    public static void showLoading(MultiStateView view) {
        view.setViewState(MultiStateView.VIEW_STATE_LOADING);
    }

    /**
     * 展示空布局
     */
    public static void showEmpty(MultiStateView view) {
//        if (view.getViewState() == MultiStateView.VIEW_STATE_CONTENT) {
//            return;
//        }
        view.setViewState(MultiStateView.VIEW_STATE_EMPTY);
    }

    /**
     * 展示错误布局
     */
    public static void showError(MultiStateView view) {
//        if (view.getViewState() == MultiStateView.VIEW_STATE_CONTENT) {
//            return;
//        }
        view.setViewState(MultiStateView.VIEW_STATE_ERROR);
    }

    /**
     * 展示内容布局
     */
    public static void showContent(MultiStateView view) {
        view.setViewState(MultiStateView.VIEW_STATE_CONTENT);
    }

    /**
     * 设置空布局和错误布局的点击事件
     */
    public static void setEmptyAndErrorClick(MultiStateView view, MultiStateListener listener) {
        setEmptyClick(view, listener);
        setErrorClick(view, listener);
    }

    public static void setEmptyClick(MultiStateView view, MultiStateListener listener) {
        View empty = view.getView(MultiStateView.VIEW_STATE_EMPTY);
        if (empty != null) {
            empty.setOnClickListener(new OnClickListenerContinuous() {
                @Override
                public void onClickContinuous(View v) {
                    listener.onResult();
                }
            });
        }
    }

    public static void setErrorClick(MultiStateView view, MultiStateListener listener) {
        View error = view.getView(MultiStateView.VIEW_STATE_ERROR);
        if (error != null) {
            error.setOnClickListener(new OnClickListenerContinuous() {
                @Override
                public void onClickContinuous(View v) {
                    listener.onResult();
                }
            });
        }
    }
}
