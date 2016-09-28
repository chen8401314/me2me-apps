package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * Created by pc41 on 2016/9/27.
 */
@Data
public class UserVDto implements BaseEntity {

    /**
     * 需要上V的用户ID
     */
    private long customerId;

}
