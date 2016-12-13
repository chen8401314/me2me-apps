package com.me2me.activity.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * Created by 马秀成 on 2016/12/5.
 */
@Data
public class QiActivityInfoDto implements BaseEntity {

    private String name;
    
    //以下都是0否  1是
    private int isInActivity;
    private int isSignUpStage;
    private int isSingleStage;
    private int isDoubleStage;

    public void init(){
    	this.isInActivity = 0;
    	this.isSignUpStage = 0;
    	this.isSingleStage = 0;
    	this.isDoubleStage = 0;
    }
}
