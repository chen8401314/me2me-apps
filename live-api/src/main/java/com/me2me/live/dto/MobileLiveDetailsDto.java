package com.me2me.live.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * Created by pc329 on 2017/4/5.
 */
@Data
public class MobileLiveDetailsDto implements BaseEntity {

    // 王国基础数据
    private ShowLiveDto liveBasicData;

    // 王国分页数据
    private LiveUpdateDto livePaginationData;

    // 王国具体数据

    private LiveDetailDto liveDetailData;

}
