package com.kqknuc.kdkuloq.utils.help;

import com.kqknuc.kdkuloq.base.base.mvp.IBaseMvpView;
import com.kqknuc.kdkuloq.utils.lifecycle.RxLifecycleUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * RxJava 帮助类
 */
public class RxHelper {

    private static Disposable mSubscribeThread;
    private static Disposable mSubscribeUI;

    /**
     * P 层网络请求操作封装
     *
     * @param view          V层引用
     * @param isShowLoading 是否显示进度条
     */
    public static <T> ObservableTransformer<T, T> applySchedulers(final IBaseMvpView view, boolean isShowLoading) {
        return observable ->
                observable
                        // 请求数据的事件发生在io线程
                        .subscribeOn(Schedulers.io())
                        // 调用之前
                        .doOnSubscribe(disposable -> {
                            // 显示进度条
                            if (isShowLoading && view != null) {
                                view.showLoading();
                            }
                        })
                        // 请求完成后在主线程更显UI
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        // 最终一定会调用，隐藏进度条
                        .doFinally(() -> {
                            //隐藏进度条
                            if (view != null) {
                                if (isShowLoading) {
                                    view.dismissLoading();
                                }
                                view.stopRefresh();
                            }
                        })
                        // 使用 RxLifecycle,使 Disposable 和 Activity 一起销毁
                        .compose(RxLifecycleUtils.bindToLifecycle(view));
    }

    /**
     * 执行先后顺序
     */
    public static void doNext(First first, Second second) {
        Observable
                .create((ObservableOnSubscribe<String>) emitter -> {
                    first.doFirst();
                    emitter.onComplete();
                })
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String s) {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        second.doSecond();
                    }
                });
    }

    /**
     * 统一线程处理
     * <p>
     * 发布事件io线程，接收事件主线程
     */
    public static <T> ObservableTransformer<T, T> rxSchedulerHelper() {//compose处理线程
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 主线程做操作
     */
    public static void doOnUIThread(UITask uiTask) {
        mSubscribeUI = Observable.just(uiTask)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(uiTask1 -> uiTask1.doOnUI());
    }

    /**
     * io线程做操作
     */
    public static void doOnThread(ThreadTask threadTask) {
        mSubscribeThread = Observable.just(threadTask)
                .observeOn(Schedulers.io())
                .subscribe(threadTask1 -> threadTask1.doOnThread());
    }

    /**
     * 释放RxJava
     */
    public static void closeRxJava() {
        if (mSubscribeUI != null && !mSubscribeUI.isDisposed()) {
            mSubscribeUI.dispose();
            mSubscribeUI = null;
        }
        if (mSubscribeThread != null && !mSubscribeThread.isDisposed()) {
            mSubscribeThread.dispose();
            mSubscribeThread = null;
        }
    }

    /**
     * 延迟操作刷新数据
     *
     * @param time 时间毫秒
     */
    public static void doDelay(long time, Delay delay) {
        Observable.timer(time, TimeUnit.MILLISECONDS)
                .just(delay)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(delay1 -> delay1.doDelay(time));
    }

    public interface ThreadTask {
        void doOnThread();
    }

    public interface UITask {
        void doOnUI();
    }

    public interface Delay {
        void doDelay(long time);
    }

    public interface First {
        void doFirst();
    }

    public interface Second {
        void doSecond();
    }
}
