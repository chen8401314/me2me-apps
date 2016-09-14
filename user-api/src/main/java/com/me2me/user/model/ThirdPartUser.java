package com.me2me.user.model;

import com.me2me.common.web.BaseEntity;

import java.util.Date;

public class ThirdPartUser implements BaseEntity {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column third_part_user.id
     *
     * @mbggenerated Mon Sep 12 18:15:48 CST 2016
     */
    private Long id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column third_part_user.third_part_type
     *
     * @mbggenerated Mon Sep 12 18:15:48 CST 2016
     */
    private Integer thirdPartType;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column third_part_user.uid
     *
     * @mbggenerated Mon Sep 12 18:15:48 CST 2016
     */
    private Long uid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column third_part_user.third_part_open_id
     *
     * @mbggenerated Mon Sep 12 18:15:48 CST 2016
     */
    private String thirdPartOpenId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column third_part_user.third_part_token
     *
     * @mbggenerated Mon Sep 12 18:15:48 CST 2016
     */
    private String thirdPartToken;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column third_part_user.create_time
     *
     * @mbggenerated Mon Sep 12 18:15:48 CST 2016
     */
    private Date createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column third_part_user.update_time
     *
     * @mbggenerated Mon Sep 12 18:15:48 CST 2016
     */
    private Date updateTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column third_part_user.id
     *
     * @return the value of third_part_user.id
     *
     * @mbggenerated Mon Sep 12 18:15:48 CST 2016
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column third_part_user.id
     *
     * @param id the value for third_part_user.id
     *
     * @mbggenerated Mon Sep 12 18:15:48 CST 2016
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column third_part_user.third_part_type
     *
     * @return the value of third_part_user.third_part_type
     *
     * @mbggenerated Mon Sep 12 18:15:48 CST 2016
     */
    public Integer getThirdPartType() {
        return thirdPartType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column third_part_user.third_part_type
     *
     * @param thirdPartType the value for third_part_user.third_part_type
     *
     * @mbggenerated Mon Sep 12 18:15:48 CST 2016
     */
    public void setThirdPartType(Integer thirdPartType) {
        this.thirdPartType = thirdPartType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column third_part_user.uid
     *
     * @return the value of third_part_user.uid
     *
     * @mbggenerated Mon Sep 12 18:15:48 CST 2016
     */
    public Long getUid() {
        return uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column third_part_user.uid
     *
     * @param uid the value for third_part_user.uid
     *
     * @mbggenerated Mon Sep 12 18:15:48 CST 2016
     */
    public void setUid(Long uid) {
        this.uid = uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column third_part_user.third_part_open_id
     *
     * @return the value of third_part_user.third_part_open_id
     *
     * @mbggenerated Mon Sep 12 18:15:48 CST 2016
     */
    public String getThirdPartOpenId() {
        return thirdPartOpenId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column third_part_user.third_part_open_id
     *
     * @param thirdPartOpenId the value for third_part_user.third_part_open_id
     *
     * @mbggenerated Mon Sep 12 18:15:48 CST 2016
     */
    public void setThirdPartOpenId(String thirdPartOpenId) {
        this.thirdPartOpenId = thirdPartOpenId == null ? null : thirdPartOpenId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column third_part_user.third_part_token
     *
     * @return the value of third_part_user.third_part_token
     *
     * @mbggenerated Mon Sep 12 18:15:48 CST 2016
     */
    public String getThirdPartToken() {
        return thirdPartToken;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column third_part_user.third_part_token
     *
     * @param thirdPartToken the value for third_part_user.third_part_token
     *
     * @mbggenerated Mon Sep 12 18:15:48 CST 2016
     */
    public void setThirdPartToken(String thirdPartToken) {
        this.thirdPartToken = thirdPartToken == null ? null : thirdPartToken.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column third_part_user.create_time
     *
     * @return the value of third_part_user.create_time
     *
     * @mbggenerated Mon Sep 12 18:15:48 CST 2016
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column third_part_user.create_time
     *
     * @param createTime the value for third_part_user.create_time
     *
     * @mbggenerated Mon Sep 12 18:15:48 CST 2016
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column third_part_user.update_time
     *
     * @return the value of third_part_user.update_time
     *
     * @mbggenerated Mon Sep 12 18:15:48 CST 2016
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column third_part_user.update_time
     *
     * @param updateTime the value for third_part_user.update_time
     *
     * @mbggenerated Mon Sep 12 18:15:48 CST 2016
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}