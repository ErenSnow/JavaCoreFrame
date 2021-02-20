package com.kqknuc.kdkuloq.base.base.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kennyc.view.MultiStateView;
import com.kqknuc.kdkuloq.R;
import com.kqknuc.kdkuloq.base.base.bean.EventBean;
import com.kqknuc.kdkuloq.base.base.mvp.IBaseView;
import com.kqknuc.kdkuloq.receiver.NetworkConnectChangedReceiver;
import com.kqknuc.kdkuloq.utils.EventBusUtils;
import com.kqknuc.kdkuloq.utils.ToastUtils;
import com.kqknuc.kdkuloq.utils.lifecycle.FragmentLifecycleAble;
import com.kqknuc.kdkuloq.utils.multi.MultiStateUtils;
import com.trello.rxlifecycle3.android.FragmentEvent;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * @author Eren
 * <p>
 * Fragment基类
 */
public abstract class BaseFragment extends SupportFragment implements IBaseView, FragmentLifecycleAble {
    private final BehaviorSubject<FragmentEvent> mLifecycleSubject = BehaviorSubject.create();
    /**
     * 上下文
     */
    protected Context mContext;
    @BindView(R.id.avi_base_fragment)
    AVLoadingIndicatorView aviBaseFragment;
    @BindView(R.id.msv)
    MultiStateView msv;
    /**
     * 数据绑定
     */
    private Unbinder unbinder;

    private NetworkConnectChangedReceiver mNetworkConnectChangedReceiver;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 初始化布局
        View rootView = inflater.inflate(R.layout.fragment_base, container, false);
        FrameLayout mContainer = rootView.findViewById(R.id.container);
        mContainer.addView(getLayoutInflater().inflate(setLayoutId(), null));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.setLayoutParams(params);
        // 初始化ButterKnife
        unbinder = ButterKnife.bind(this, rootView);
        // 注册EventBus
        if (regEvent()) {
            EventBusUtils.register(this);
        }
        // 点击加载失败布局
        MultiStateUtils.setErrorClick(msv, this::onErrorClick);
        // 子类自定义布局
        initView(savedInstanceState);
        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            //注册广播用于监听网络状态改变
            detectNetWork();
        }
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
    }

    /**
     * 具体的布局
     *
     * @return 布局ID
     */
    protected abstract int setLayoutId();

    /**
     * 需要接收事件 重写该方法 并返回true
     */
    protected boolean regEvent() {
        return false;
    }

    /**
     * 空布局或加载错误后点击刷新
     */
    protected void onErrorClick() {
    }

    /**
     * 初始化布局
     *
     * @param savedInstanceState 异常关闭数据保存
     */
    protected abstract void initView(Bundle savedInstanceState);

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 注销ButterKnife
        if (unbinder != null) {
            unbinder.unbind();
        }
        // 注销EventBus
        if (regEvent()) {
            EventBusUtils.unregister(this);
        }
        // 注销断网
        if (mNetworkConnectChangedReceiver != null) {
            mContext.unregisterReceiver(mNetworkConnectChangedReceiver);
            mNetworkConnectChangedReceiver = null;
        }
    }

    protected View getView(int id) {
        if (getView() != null) {
            return getView().findViewById(id);
        }
        return null;
    }

    /**
     * 子类接受事件 重写该方法
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(EventBean event) {
        if (event != null) {
            receiveEvent(event);
        }
    }

    /**
     * 接收到分发到事件
     *
     * @param event 事件
     */
    protected void receiveEvent(EventBean event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onStickyEventBusCome(EventBean event) {
        if (event != null) {
            receiveStickyEvent(event);
        }
    }

    /**
     * 接受到分发的粘性事件
     *
     * @param event 粘性事件
     */
    protected void receiveStickyEvent(EventBean event) {

    }

    /**
     * 跳转到Fragment
     *
     * @param supportFragment 要跳转的Fragment
     */
    public void startFragment(@NonNull SupportFragment supportFragment) {
        start(supportFragment);
    }

    /**
     * 跳转到Fragment返回数据
     *
     * @param supportFragment 要跳转的Fragment
     * @param requestCode     请求码
     */
    public void startFragmentForResult(@NonNull SupportFragment supportFragment, int
            requestCode) {
        startForResult(supportFragment, requestCode);
    }

    /**
     * 跳转到Activity
     *
     * @param clz 要跳转的Activity
     */
    public void startActivity(@NonNull Class<?> clz) {
        startActivity(new Intent(mContext, clz));
    }

    /**
     * 跳转到Activity并携带参数
     *
     * @param clz    要跳转的Activity
     * @param bundle bundle数据
     */
    public void startActivity(@NonNull Class<?> clz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(mContext, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 跳转到Activity返回数据
     *
     * @param clz         要跳转的Activity
     * @param requestCode 请求码
     */
    public void startActivityForResult(@NonNull Class<?> clz, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(mContext, clz);
        startActivityForResult(intent, requestCode);
    }

    /**
     * 跳转到Activity返回数据
     *
     * @param clz         要跳转的Activity
     * @param bundle      bundle数据
     * @param requestCode 请求码
     */
    public void startActivityForResult(@NonNull Class<?> clz, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(mContext, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * 信息提示
     */
    @Override
    public void showToast(String msg) {
        ToastUtils.showToast(mContext, msg, 1000);
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
    public void showContent() {
        MultiStateUtils.showContent(msv);
    }

    @Override
    public void showEmpty() {
        MultiStateUtils.showEmpty(msv);
    }

    @NonNull
    @Override
    public final Subject<FragmentEvent> provideLifecycleSubject() {
        return mLifecycleSubject;
    }

}