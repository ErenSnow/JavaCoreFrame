package com.kqknuc.kdkuloq.base.base.bean;

/**
 * @author Eren
 * <p>
 * 备用response bean
 */
public class StandbyBean {

    /**
     * protocol : https
     * domain : wjshl.com
     */

    private String protocol;
    private String domain;

    public String getProtocol() {
        return protocol == null ? "" : protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getDomain() {
        return domain == null ? "" : domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
