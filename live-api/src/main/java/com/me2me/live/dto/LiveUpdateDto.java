package com.me2me.live.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * Created by pc188 on 2016/10/31.
 */
@Data
public class LiveUpdateDto implements BaseEntity{
	private static final long serialVersionUID = 5208813120140471997L;

	private int totalRecords;

    private int updateRecords;

    private int totalPages;

    private int startPageNo;
    
    private long lastFragmentId;
    
    private int firstPage;
}
