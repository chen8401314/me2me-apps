package com.me2me.user.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * Created by pc188 on 2016/11/3.
 */
@Data
public class EntryPageReturnDto implements BaseEntity{

    private int cversion;

    private List<EntryPageElement> entryPageElements = Lists.newArrayList();

    public EntryPageElement createElement(){
        return new EntryPageElement();
    }

    @Data
    public static class EntryPageElement implements  BaseEntity{
        private long id;

        private int type;

        private String text;

        private int cversion;

        private int status;
    }
}
