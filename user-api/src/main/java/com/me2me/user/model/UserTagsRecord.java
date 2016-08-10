package com.me2me.user.model;

import com.me2me.common.web.BaseEntity;

import java.util.Date;

public class UserTagsRecord implements BaseEntity {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_tags_record.id
     *
     * @mbggenerated Tue Apr 12 19:33:04 CST 2016
     */
    private Long id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_tags_record.tag_id
     *
     * @mbggenerated Tue Apr 12 19:33:04 CST 2016
     */
    private Long tagId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_tags_record.from_uid
     *
     * @mbggenerated Tue Apr 12 19:33:04 CST 2016
     */
    private Long fromUid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_tags_record.to_uid
     *
     * @mbggenerated Tue Apr 12 19:33:04 CST 2016
     */
    private Long toUid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_tags_record.create_time
     *
     * @mbggenerated Tue Apr 12 19:33:04 CST 2016
     */
    private Date createTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_tags_record.id
     *
     * @return the value of user_tags_record.id
     *
     * @mbggenerated Tue Apr 12 19:33:04 CST 2016
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_tags_record.id
     *
     * @param id the value for user_tags_record.id
     *
     * @mbggenerated Tue Apr 12 19:33:04 CST 2016
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_tags_record.tag_id
     *
     * @return the value of user_tags_record.tag_id
     *
     * @mbggenerated Tue Apr 12 19:33:04 CST 2016
     */
    public Long getTagId() {
        return tagId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_tags_record.tag_id
     *
     * @param tagId the value for user_tags_record.tag_id
     *
     * @mbggenerated Tue Apr 12 19:33:04 CST 2016
     */
    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_tags_record.from_uid
     *
     * @return the value of user_tags_record.from_uid
     *
     * @mbggenerated Tue Apr 12 19:33:04 CST 2016
     */
    public Long getFromUid() {
        return fromUid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_tags_record.from_uid
     *
     * @param fromUid the value for user_tags_record.from_uid
     *
     * @mbggenerated Tue Apr 12 19:33:04 CST 2016
     */
    public void setFromUid(Long fromUid) {
        this.fromUid = fromUid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_tags_record.to_uid
     *
     * @return the value of user_tags_record.to_uid
     *
     * @mbggenerated Tue Apr 12 19:33:04 CST 2016
     */
    public Long getToUid() {
        return toUid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_tags_record.to_uid
     *
     * @param toUid the value for user_tags_record.to_uid
     *
     * @mbggenerated Tue Apr 12 19:33:04 CST 2016
     */
    public void setToUid(Long toUid) {
        this.toUid = toUid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_tags_record.create_time
     *
     * @return the value of user_tags_record.create_time
     *
     * @mbggenerated Tue Apr 12 19:33:04 CST 2016
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_tags_record.create_time
     *
     * @param createTime the value for user_tags_record.create_time
     *
     * @mbggenerated Tue Apr 12 19:33:04 CST 2016
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}