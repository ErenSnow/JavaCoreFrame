package com.kqknuc.kdkuloq.base.base.fragment;

import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.kqknuc.kdkuloq.base.base.mvp.BasePresenter;
import com.kqknuc.kdkuloq.base.base.mvp.IBaseModel;
import com.kqknuc.kdkuloq.base.base.mvp.IBaseMvpView;
import com.kqknuc.kdkuloq.base.base.mvp.IBaseView;
import com.kqknuc.kdkuloq.receiver.NetworkConnectChangedReceiver;
import com.kqknuc.kdkuloq.utils.InstanceUtil;
import com.kqknuc.kdkuloq.utils.help.RxHelper;
import com.kqknuc.kdkuloq.utils.multi.MultiStateUtils;
import com.orhanobut.logger.Logger;

import java.lang.reflect.ParameterizedType;

/**
 * @author Eren
 * <p>
 * Fragment MVP基类
 */
public abstract class BaseMvpFragment<P extends BasePresenter, M extends IBaseModel> extends BaseFragment implements IBaseMvpView {

    public P mPresenter;
    public M mModel;

    private NetworkConnectChangedReceiver mNetworkConnectChangedReceiver;

    @Override
    protected boolean regEvent() {
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注册广播用于监听网络状态改变
        detectNetWork();
    }

    /**
     * 注册广播用于监听网络状态改变
     */
    private void detectNetWork() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        mNetworkConnectChangedReceiver = new NetworkConnectChangedReceiver();
        mContext.registerReceiver(mNetworkConnectChangedReceiver, filter);
        mNetworkConnectChangedReceiver.networkConnectChangedListenerListener(b -> {
            if (b) {
                initMvp();
                initData();
            }
        });
    }

    @Override
    public void onErrorClick() {
        initData();
    }

    /**
     * 初始化网络加载
     */
    protected abstract void initData();

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 释放RxJava
        RxHelper.closeRxJava();
        // 注销MVP
        detachMvp();
        // 注销断网
        if (mNetworkConnectChangedReceiver != null) {
            mContext.unregisterReceiver(mNetworkConnectChangedReceiver);
            mNetworkConnectChangedReceiver = null;
        }
    }

    /**
     * 注销MVP
     */
    private void detachMvp() {
        if (mPresenter != null) {
            mPresenter.onDetached();
        }
    }

    @Override
    public void showToast(String msg) {
        super.showToast(msg);
    }

    @Override
    public void showLoading() {
        if (aviBaseFragment != null) {
            aviBaseFragment.smoothToShow();
        }
    }

    @Override
    public void dismissLoading() {
        if (aviBaseFragment != null) {
            aviBaseFragment.smoothToHide();
        }
    }

    @Override
    public void startRefresh() {

    }

    @Override
    public void stopRefresh() {

    }

    @Override
    public void showError() {
        MultiStateUtils.showError(msv);
    }

    @Override
    public void showEmpty() {
        MultiStateUtils.showEmpty(msv);
    }

    @Override
    public void showContent() {
        MultiStateUtils.showContent(msv);
    }

    /**
     * 初始化P、M
     */
    private void initMvp() {
        if (this instanceof IBaseView &&
                this.getClass().getGenericSuperclass() instanceof ParameterizedType &&
                ((ParameterizedType) (this.getClass().getGenericSuperclass())).getActualTypeArguments().length > 0) {
            mPresenter = InstanceUtil.getInstance(this, 0);
            mModel = InstanceUtil.getInstance(this, 1);
        }
        if (mPresenter != null) {
            mPresenter.setContext(mContext);
        }
        // 初始化Presenter
        initPresenter();
    }

    /**
     * 初始化Presenter
     */
    protected abstract void initPresenter();

    /**
     * 报错上传友盟
     *
     * @param code     错误码
     * @param errorMsg 错误信息
     */
    @Override
    public void onError(int code, String errorMsg) {
        Logger.d("网络错误:" + code + ", 错误信息:" + errorMsg);
    }

    @Override
    public void showLoadMoreError() {

    }

    @Override
    public void showLoadNoMore() {

    }
}
