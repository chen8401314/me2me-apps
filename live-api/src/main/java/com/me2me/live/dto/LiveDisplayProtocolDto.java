package com.me2me.live.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class LiveDisplayProtocolDto implements BaseEntity{

    private Long id;


    private Integer vLv;


    private Integer coreType;


    private String coreAlign;


    private Integer atCoreType;


    private String atCoreAlign;


    private Integer inType;


    private String inAlign;


    private Integer inRange;


    private Integer outType;


    private String outAlign;


    private Integer outRange;


    private Long uid;


    private Date createTime;

}