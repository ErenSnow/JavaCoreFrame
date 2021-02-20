package com.kqknuc.kdkuloq.base.base.activity;

import android.content.IntentFilter;
import android.os.Bundle;

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
 * Activity MVP基类
 */
public abstract class BaseMvpActivity<P extends BasePresenter, M extends IBaseModel> extends BaseActivity implements IBaseMvpView {

    public P mPresenter;
    public M mModel;

    private NetworkConnectChangedReceiver mNetworkConnectChangedReceiver;

    @Override
    protected boolean regEvent() {
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册广播用于监听网络状态改变
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
        registerReceiver(mNetworkConnectChangedReceiver, filter);
        mNetworkConnectChangedReceiver.networkConnectChangedListenerListener(b -> {
            if (b) {
                initMvp();
                initData();
            }
        });
    }

    @Override
    protected void onErrorClick() {
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放RxJava
        RxHelper.closeRxJava();
        // 注销MVP
        detachMvp();
        // 注销断网
        if (mNetworkConnectChangedReceiver != null) {
            unregisterReceiver(mNetworkConnectChangedReceiver);
            mNetworkConnectChangedReceiver = null;
        }
    }

    @Override
    public void showToast(String msg) {
        super.showToast(msg);
    }

    @Override
    public void showLoading() {
        if (aviBaseActivity != null) {
            aviBaseActivity.smoothToShow();
        }
    }

    @Override
    public void dismissLoading() {
        if (aviBaseActivity != null) {
            aviBaseActivity.smoothToHide();
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
     * 注销MVP
     */
    private void detachMvp() {
        if (mPresenter != null) {
            mPresenter.onDetached();
        }
    }

    /**
     * 初始化网络加载
     */
    protected abstract void initData();

    /**
     * 初始化MVP
     */
    private void initMvp() {
        if (this instanceof IBaseView &&
                this.getClass().getGenericSuperclass() instanceof ParameterizedType &&
                ((ParameterizedType) (this.getClass().getGenericSuperclass())).getActualTypeArguments().length > 0) {
            mPresenter = InstanceUtil.getInstance(this, 0);
            mModel = InstanceUtil.getInstance(this, 1);
        }
        if (mPresenter != null) {
            mPresenter.setContext(this);
        }
        // 初始化Presenter
        if (mPresenter != null) {
            mPresenter.setMV(mModel, this);
        }
    }

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
