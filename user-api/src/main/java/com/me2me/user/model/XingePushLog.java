package com.me2me.user.model;

import com.me2me.common.web.BaseEntity;

import java.util.Date;

public class XingePushLog implements BaseEntity {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column xinge_push_log.id
     *
     * @mbggenerated Thu Jun 02 14:36:13 CST 2016
     */
    private Long id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column xinge_push_log.ret_code
     *
     * @mbggenerated Thu Jun 02 14:36:13 CST 2016
     */
    private Integer retCode;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column xinge_push_log.message_type
     *
     * @mbggenerated Thu Jun 02 14:36:13 CST 2016
     */
    private Integer messageType;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column xinge_push_log.content
     *
     * @mbggenerated Thu Jun 02 14:36:13 CST 2016
     */
    private String content;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column xinge_push_log.create_time
     *
     * @mbggenerated Thu Jun 02 14:36:13 CST 2016
     */
    private Date createTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column xinge_push_log.id
     *
     * @return the value of xinge_push_log.id
     *
     * @mbggenerated Thu Jun 02 14:36:13 CST 2016
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column xinge_push_log.id
     *
     * @param id the value for xinge_push_log.id
     *
     * @mbggenerated Thu Jun 02 14:36:13 CST 2016
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column xinge_push_log.ret_code
     *
     * @return the value of xinge_push_log.ret_code
     *
     * @mbggenerated Thu Jun 02 14:36:13 CST 2016
     */
    public Integer getRetCode() {
        return retCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column xinge_push_log.ret_code
     *
     * @param retCode the value for xinge_push_log.ret_code
     *
     * @mbggenerated Thu Jun 02 14:36:13 CST 2016
     */
    public void setRetCode(Integer retCode) {
        this.retCode = retCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column xinge_push_log.message_type
     *
     * @return the value of xinge_push_log.message_type
     *
     * @mbggenerated Thu Jun 02 14:36:13 CST 2016
     */
    public Integer getMessageType() {
        return messageType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column xinge_push_log.message_type
     *
     * @param messageType the value for xinge_push_log.message_type
     *
     * @mbggenerated Thu Jun 02 14:36:13 CST 2016
     */
    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column xinge_push_log.content
     *
     * @return the value of xinge_push_log.content
     *
     * @mbggenerated Thu Jun 02 14:36:13 CST 2016
     */
    public String getContent() {
        return content;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column xinge_push_log.content
     *
     * @param content the value for xinge_push_log.content
     *
     * @mbggenerated Thu Jun 02 14:36:13 CST 2016
     */
    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column xinge_push_log.create_time
     *
     * @return the value of xinge_push_log.create_time
     *
     * @mbggenerated Thu Jun 02 14:36:13 CST 2016
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column xinge_push_log.create_time
     *
     * @param createTime the value for xinge_push_log.create_time
     *
     * @mbggenerated Thu Jun 02 14:36:13 CST 2016
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}