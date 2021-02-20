package com.kqknuc.kdkuloq.base.base.bean;

/**
 * 错误信息，需要使用时，继承即可
 */
public class ErrorBean {

    private String errors;

    public String getErrors() {
        return errors == null ? "" : errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }
}
