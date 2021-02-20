package com.kqknuc.kdkuloq.base.base.mvp;


/**
 * View基类接口
 */
public interface IBaseView {
    /**
     * 显示Toast
     *
     * @param msg Toast信息
     */
    void showToast(String msg);

    /**
     * 显示加载框
     */
    void showLoading();

    /**
     * 隐藏加载框
     */
    void dismissLoading();

    /**
     * 开启下拉刷新
     */
    void startRefresh();

    /**
     * 关闭下拉刷新
     */
    void stopRefresh();

    /**
     * 加载错误界面
     */
    void showError();

    /**
     * 空界面
     */
    void showEmpty();

    /**
     * 显示界面
     */
    void showContent();
}
