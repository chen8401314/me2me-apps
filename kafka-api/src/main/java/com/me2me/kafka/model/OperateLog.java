package com.me2me.kafka.model;

import java.io.Serializable;

public class OperateLog implements Serializable {

    private static final long serialVersionUID = -1036844736487022006L;
    
    public OperateLog() {
        this.logTime = System.currentTimeMillis();
    }

    private String action;
    
    private String userId;
    
    private String cid;
    
    private long id;
    
    private String ext;
    
    private long logTime;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public long getLogTime() {
        return logTime;
    }

    public void setLogTime(long logTime) {
        this.logTime = logTime;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }
    
    
}
