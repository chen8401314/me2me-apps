package com.me2me.content.dto;

import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import com.me2me.content.dto.ShowContentListDto.ContentDataElement.TagElement;

import lombok.Data;

/**
 * @author pc339
 *
 */
@Data
public class UserGroupDto implements BaseEntity {

	private List<UserElement> userGroup=Lists.newArrayList(); 

    @Data
    public static class UserElement implements BaseEntity{


        private long uid;

        private String nickName;

        private String avatar;

        private int gender;

        private int vip;

        private int level;

        private String introduce;
        
        private String avatarFrame;

    }
}
