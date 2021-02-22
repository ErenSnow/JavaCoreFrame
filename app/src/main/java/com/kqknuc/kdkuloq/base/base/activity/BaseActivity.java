package com.kqknuc.kdkuloq.base.base.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;

import com.kennyc.view.MultiStateView;
import com.kqknuc.kdkuloq.R;
import com.kqknuc.kdkuloq.base.base.bean.EventBean;
import com.kqknuc.kdkuloq.base.base.mvp.IBaseView;
import com.kqknuc.kdkuloq.receiver.NetworkConnectChangedReceiver;
import com.kqknuc.kdkuloq.utils.ActivityCollector;
import com.kqknuc.kdkuloq.utils.EventBusUtils;
import com.kqknuc.kdkuloq.utils.PermissionUtils;
import com.kqknuc.kdkuloq.utils.ToastUtils;
import com.kqknuc.kdkuloq.utils.lifecycle.ActivityLifecycleAble;
import com.kqknuc.kdkuloq.utils.multi.MultiStateUtils;
import com.kqknuc.kdkuloq.view.listener.SpecialEditText;
import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import me.yokeyword.fragmentation.SupportActivity;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

/**
 * @author Eren
 * <p>
 * Activity基类
 */
public abstract class BaseActivity extends SupportActivity implements IBaseView, ActivityLifecycleAble {
    /**
     * RxJava内存优化
     */
    private final BehaviorSubject<ActivityEvent> mLifecycleSubject = BehaviorSubject.create();

    @BindView(R.id.tool_bar)
    public Toolbar toolBar;
    @BindView(R.id.tv_toolbar)
    TextView tvToolbar;
    @BindView(R.id.avi_base_activity)
    AVLoadingIndicatorView aviBaseActivity;
    @BindView(R.id.msv)
    MultiStateView msv;
    @BindView(R.id.ll_wifi)
    LinearLayout mLlWifi;
    @BindView(R.id.network_warning)
    TextView mNetworkWarning;

    /**
     * 数据绑定
     */
    private Unbinder unbinder;
    private ActionBar mActionBar;
    private NetworkConnectChangedReceiver mNetworkConnectChangedReceiver;

    @NonNull
    @Override
    public final Subject<ActivityEvent> provideLifecycleSubject() {
        return mLifecycleSubject;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 获取传递参数
        getParams(getIntent().getExtras());
        // 注册EventBus
        if (regEvent()) {
            EventBusUtils.register(this);
        }
        // 当前所在类
        Logger.i("当前Activity:" + getClass().getSimpleName());
        // 将当前正在创建的活动添加到活动管理期里
        ActivityCollector.addActivity(this);
        // 初始化布局
        initUI();
        initView();
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
        registerReceiver(mNetworkConnectChangedReceiver, filter);
        mNetworkConnectChangedReceiver.networkConnectChangedListenerListener(b -> {
            if (b) {
                mLlWifi.setVisibility(View.GONE);
            } else {
                mLlWifi.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 获取传递参数
     *
     * @param extras 传递参数
     */
    protected void getParams(Bundle extras) {
    }

    /**
     * 需要接收事件 重写该方法 并返回true
     */
    protected boolean regEvent() {
        return false;
    }

    /**
     * 初始化布局
     */
    private void initUI() {
        setContentView(R.layout.activity_base);
        // 显示具体的布局界面，由子类显示
        FrameLayout mContainer = findViewById(R.id.container);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.setLayoutParams(params);
        mContainer.addView(View.inflate(this, setLayoutId(), null));
        // 初始化ButterKnife
        unbinder = ButterKnife.bind(this);
        // 点击空布局和错误布局
        MultiStateUtils.setEmptyAndErrorClick(msv, this::onErrorClick);
    }

    /**
     * 初始化布局
     */
    protected abstract void initView();

    /**
     * 具体的布局
     *
     * @return 布局ID
     */
    protected abstract int setLayoutId();

    /**
     * 空白或错误后点击刷新
     */
    protected void onErrorClick() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销ButterKnife
        if (unbinder != null) {
            unbinder.unbind();
        }
        // 注销断网
        if (mNetworkConnectChangedReceiver != null) {
            unregisterReceiver(mNetworkConnectChangedReceiver);
            mNetworkConnectChangedReceiver = null;
        }
        // 注销EventBus
        if (regEvent()) {
            EventBusUtils.unregister(this);
        }
        // 将一个马上要销毁的活动从管理器里移除
        ActivityCollector.removeActivity(this);
    }

    /**
     * 对Toolbar进行设置
     */
    protected void setToolbar(Toolbar toolbar) {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            mActionBar = getSupportActionBar();
            if (mActionBar != null) {
                // 去除默认Title显示
                mActionBar.setDisplayShowTitleEnabled(false);
                mActionBar.setDisplayHomeAsUpEnabled(true);
                // 设置返回键图片
                mActionBar.setHomeAsUpIndicator(R.drawable.ic_back);
            }
            // 返回键的点击事件
            toolbar.setNavigationOnClickListener(v -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else {
                    onBackPressedSupport();
                }
            });
        }
    }

    /**
     * 去掉Toolbar返回键
     */
    protected void setToolBarGone() {
        if (mActionBar != null) {
            // 去除默认Title显示
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setDisplayHomeAsUpEnabled(false);
            // 设置返回键图片
            mActionBar.setHomeAsUpIndicator(null);
        }
    }

    /**
     * 隐藏ToolBar
     */
    protected void hideToolBar() {
        toolBar.setVisibility(View.GONE);
    }

    @Override
    public void setTitle(CharSequence title) {
        if (tvToolbar != null) {
            tvToolbar.setText(title);
        }
        // 初始化Toolbar
        setToolbar();
    }

    /**
     * 默认Toolbar设置
     */
    protected void setToolbar() {
        if (toolBar != null) {
            setSupportActionBar(toolBar);
            mActionBar = getSupportActionBar();
            if (mActionBar != null) {
                // 去除默认Title显示
                mActionBar.setDisplayShowTitleEnabled(false);
                mActionBar.setDisplayHomeAsUpEnabled(true);
                // 设置返回键图片
                mActionBar.setHomeAsUpIndicator(R.drawable.ic_back);
            }
            // 返回键的点击事件
            toolBar.setNavigationOnClickListener(v -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else {
                    onBackPressedSupport();
                }
            });
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 页面跳转
     *
     * @param clz 要跳转的Activity
     */
    public void startActivity(Class<?> clz) {
        startActivity(new Intent(this, clz));
    }

    /**
     * 页面跳转
     *
     * @param clz    要跳转的Activity
     * @param intent intent
     */
    public void startActivity(Class<?> clz, Intent intent) {
        intent.setClass(this, clz);
        startActivity(intent);
    }

    /**
     * 携带数据的页面跳转
     *
     * @param clz    要跳转的Activity
     * @param bundle bundle数据
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 页面跳转并返回
     *
     * @param clz         要跳转的Activity
     * @param requestCode requestCode
     */
    public void startActivityForResult(Class<?> clz, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        startActivityForResult(intent, requestCode);
    }

    /**
     * 含有Bundle通过Class打开编辑界面
     *
     * @param clz         要跳转的Activity
     * @param bundle      bundle数据
     * @param requestCode requestCode
     */
    public void startActivityForResult(Class<?> clz, Bundle bundle,
                                       int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
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
        ToastUtils.showToast(msg);
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
     * 点击EditText之外的部分关闭软键盘
     */
    private boolean flagMove = false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case ACTION_DOWN:
                flagMove = false;
                break;
            case ACTION_MOVE:
                flagMove = true;
                break;
            case ACTION_UP:
                if (!flagMove) {
                    View currentFocus = getCurrentFocus();
                    if (isShouldHideInput(currentFocus, ev)) {
                        clearEditTextFocus(currentFocus);
                        hideInputMethod(currentFocus);
                    }
                    return super.dispatchTouchEvent(ev);
                }
                break;
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        } else {
            onTouchEvent(ev);
        }
        return false;
    }

    private void hideInputMethod(View view) {
        InputMethodManager systemService = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (systemService != null) {
            systemService.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 设置EditText失去焦点
     */
    private void clearEditTextFocus(View view) {
        if (view instanceof EditText) {
            view.clearFocus();
        }
    }

    /**
     * 判断是否应该隐藏软键盘
     */
    private boolean isShouldHideInput(View view, MotionEvent event) {
        if (view instanceof EditText || view instanceof AppCompatEditText || view instanceof SpecialEditText) {
            int[] leftTop = {0, 0};
            int width;
            int height;
            //获取输入框当前的location位置
            View parent = findSpecialEditTextParent(view, 5);
            if (parent != null) {
                parent.getLocationInWindow(leftTop);
                height = parent.getHeight();
                width = parent.getWidth();
            } else {
                view.getLocationInWindow(leftTop);
                height = view.getHeight();
                width = view.getWidth();
            }
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + height;
            int right = left + width;
            // 点击的是输入框区域，保留点击EditText的事件
            return !(event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }

    private View findSpecialEditTextParent(View view, int maxTimes) {
        for (int i = 0; i <= maxTimes; i++) {
            if (view instanceof SpecialEditText) {
                return view;
            }
            try {
                view = (View) view.getParent();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
