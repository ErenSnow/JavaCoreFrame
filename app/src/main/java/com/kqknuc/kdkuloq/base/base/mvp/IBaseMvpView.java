package com.kqknuc.kdkuloq.base.base.mvp;


/**
 * View基类接口
 */
public interface IBaseMvpView {
    /**
     * 显示加载框
     */
    void showLoading();

    /**
     * 隐藏加载框
     */
    void dismissLoading();

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

    /**
     * 加载错误界面
     *
     * @param code     错误码
     * @param errorMsg 错误信息
     */
    void onError(int code, String errorMsg);

    /**
     * 开启下拉刷新
     */
    void startRefresh();

    /**
     * 关闭下拉刷新
     */
    void stopRefresh();

    /**
     * Toast显示结果
     */
    void showToast(String msg);

    /**
     * 加载失败
     */
    void showLoadMoreError();

    /**
     * 没有更多数据
     */
    void showLoadNoMore();
}
