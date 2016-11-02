package com.me2me.live.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * Created by pc188 on 2016/10/31.
 */
@Data
public class LiveUpdateDto implements BaseEntity{

    private int totalRecords;

    private int updateRecords;

    private int totalPages;

    private int startPageNo;
}
