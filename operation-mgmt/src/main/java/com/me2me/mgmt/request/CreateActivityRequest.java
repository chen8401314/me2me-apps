package com.me2me.mgmt.request;

import lombok.Getter;
import lombok.Setter;

import org.springframework.web.multipart.MultipartFile;

public class CreateActivityRequest{


    // 活动期次
    @Getter
    @Setter
    private String txtIssue;

    // 活动标题
    @Getter
    @Setter
    private String txtTitle;

    // 活动标识
    @Getter
    @Setter
    private String txtHashTitle;

    // 活动内容
    @Getter
    @Setter
    private String txtContent;

    // 上传的图片
    @Getter
    @Setter
    private MultipartFile FUCoverImg;

    // 活动开始时间
    @Getter
    @Setter
    private String txtStarTime;

    // 活动结束时间
    @Getter
    @Setter
    private String txtEndTime;





}
