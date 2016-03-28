package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * Created by pc62 on 2016/3/25.
 */
@Data
public class PasteTagDto implements BaseEntity {

    private String tag; //��ǩ����

    private Long targetUid;  //������ǩ���û�Id

    private Long fromUid;  //����ǩ���û�Id

}
