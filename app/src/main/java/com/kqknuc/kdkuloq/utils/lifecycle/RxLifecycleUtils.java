package com.kqknuc.kdkuloq.utils.lifecycle;

import com.kqknuc.kdkuloq.base.base.mvp.IBaseMvpView;
import com.trello.rxlifecycle3.LifecycleTransformer;
import com.trello.rxlifecycle3.RxLifecycle;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.trello.rxlifecycle3.android.FragmentEvent;
import com.trello.rxlifecycle3.android.RxLifecycleAndroid;

import io.reactivex.annotations.NonNull;

/**
 * ================================================
 * 使用此类操作 RxLifecycle 的特性
 * <p>
 * Created by JessYan on 26/08/2017 17:52
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */

public class RxLifecycleUtils {

    private RxLifecycleUtils() {
        throw new IllegalStateException("you can't instantiate me!");
    }

    /**
     * 绑定 Activity 的指定生命周期
     *
     * @param view
     * @param event
     * @param <T>
     * @return
     */
    public static <T> LifecycleTransformer<T> bindUntilEvent(@NonNull final IBaseMvpView view,
                                                             final ActivityEvent event) {
        Preconditions.checkNotNull(view, "view == null");
        if (view instanceof ActivityLifecycleAble) {
            return bindUntilEvent((ActivityLifecycleAble) view, event);
        } else {
            throw new IllegalArgumentException("view isn't ActivityLifecycleAble");
        }
    }

    public static <T, R> LifecycleTransformer<T> bindUntilEvent(@NonNull final LifecycleAble<R> lifecycleable,
                                                                final R event) {
        Preconditions.checkNotNull(lifecycleable, "lifecycleable == null");
        return RxLifecycle.bindUntilEvent(lifecycleable.provideLifecycleSubject(), event);
    }

    /**
     * 绑定 Fragment 的指定生命周期
     *
     * @param view
     * @param event
     * @param <T>
     * @return
     */
    public static <T> LifecycleTransformer<T> bindUntilEvent(@NonNull final IBaseMvpView view,
                                                             final FragmentEvent event) {
        Preconditions.checkNotNull(view, "view == null");
        if (view instanceof FragmentLifecycleAble) {
            return bindUntilEvent((FragmentLifecycleAble) view, event);
        } else {
            throw new IllegalArgumentException("view isn't FragmentLifecycleAble");
        }
    }

    /**
     * 绑定 Activity/Fragment 的生命周期
     *
     * @param view
     * @param <T>
     * @return
     */
    public static <T> LifecycleTransformer<T> bindToLifecycle(@NonNull IBaseMvpView view) {
        Preconditions.checkNotNull(view, "view == null");
        if (view instanceof LifecycleAble) {
            return bindToLifecycle((LifecycleAble) view);
        } else {
            throw new IllegalArgumentException("view isn't LifecycleAble");
        }
    }

    public static <T> LifecycleTransformer<T> bindToLifecycle(@NonNull LifecycleAble lifecycleable) {
        Preconditions.checkNotNull(lifecycleable, "lifecycleable == null");
        if (lifecycleable instanceof ActivityLifecycleAble) {
            return RxLifecycleAndroid.bindActivity(((ActivityLifecycleAble) lifecycleable).provideLifecycleSubject());
        } else if (lifecycleable instanceof FragmentLifecycleAble) {
            return RxLifecycleAndroid.bindFragment(((FragmentLifecycleAble) lifecycleable).provideLifecycleSubject());
        } else {
            throw new IllegalArgumentException("LifecycleAble not match");
        }
    }
}
