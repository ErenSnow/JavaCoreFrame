package com.kqknuc.kdkuloq.utils;


import com.kqknuc.kdkuloq.base.base.bean.EventBean;

import org.greenrobot.eventbus.EventBus;

/**
 * @author Eren
 * <p>
 * 事件工具类
 */
public class EventBusUtils {

    /**
     * 注册事件
     */
    public static void register(Object subscriber) {
        if (!EventBus.getDefault().isRegistered(subscriber)) {
            EventBus.getDefault().register(subscriber);
        }
    }

    /**
     * 解除事件
     */
    public static void unregister(Object subscriber) {
        if (EventBus.getDefault().isRegistered(subscriber)) {
            EventBus.getDefault().unregister(subscriber);
        }
    }

    /**
     * 发送普通事件
     */
    public static void sendEvent(EventBean event) {
        EventBus.getDefault().post(event);
    }

    /**
     * 发送粘性事件
     */
    public static void sendStickyEvent(EventBean event) {
        EventBus.getDefault().postSticky(event);
    }

    /**
     * 移除指定的粘性订阅事件
     */
    public static <T> void removeStickyEvent(Class<T> eventType) {
        T stickyEvent = EventBus.getDefault().getStickyEvent(eventType);
        if (stickyEvent != null) {
            EventBus.getDefault().removeStickyEvent(stickyEvent);
        }
    }

    /**
     * 取消事件传送
     */
    public static void cancelEventDelivery(Object event) {
        EventBus.getDefault().cancelEventDelivery(event);
    }

    /**
     * 移除所有的粘性订阅事件
     */
    public static void removeAllStickyEvents() {
        EventBus.getDefault().removeAllStickyEvents();
    }
}
