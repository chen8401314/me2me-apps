package com.me2me.kafka.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class ClientLog implements Serializable {

    private static final long serialVersionUID = -5138160880508253992L;

    public ClientLog() {
        this.logTime = System.currentTimeMillis();
    }

    @Setter
    @Getter
    private int action;

    @Setter
    @Getter
    private long userId;

    @Setter
    @Getter
    private String cid;

    @Setter
    @Getter
    private long id;

    @Setter
    @Getter
    private String ext;

    @Setter
    @Getter
    private long logTime;

    @Setter
    @Getter
    private String userAgent;

    @Setter
    @Getter
    private String version;

    @Setter
    @Getter
    private String channel;
    
    
}
