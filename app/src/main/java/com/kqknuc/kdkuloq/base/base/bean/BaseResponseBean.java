package com.kqknuc.kdkuloq.base.base.bean;

/**
 * @author Eren
 * <p>
 * 基础response bean
 */
public class BaseResponseBean<T> {

    /**
     * 返回数据
     */
    private T data;
    /**
     * 200 代表执行成功  -1001 代表登录失效，需要重新登录
     */
    private int code;
    /**
     * 返回信息
     */
    private String message;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message == null ? "" : message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 请求是否成功
     */
    public boolean isOk() {
        // 成功返回码
        int requestSuccess = 200;
        return code == requestSuccess;
    }
}
