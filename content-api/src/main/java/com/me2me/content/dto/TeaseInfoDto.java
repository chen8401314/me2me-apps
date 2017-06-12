package com.me2me.content.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * 
 * Author: chenxiang
 * Date: 2017-05-08
 */
@Data
public class TeaseInfoDto implements BaseEntity {

    private List<TeaseElement> teaseData = Lists.newArrayList();

    public TeaseElement createTeaseElement(){
        return new TeaseElement();
    }

    @Data
    public static class TeaseElement implements BaseEntity{
        private long id;

        private String name;

        private String image;

        private String audio;
        
        private String extra;
    }

}
