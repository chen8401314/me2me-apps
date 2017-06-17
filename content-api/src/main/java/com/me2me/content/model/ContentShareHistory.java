package com.me2me.content.model;

import java.util.Date;

import com.me2me.common.web.BaseEntity;

public class ContentShareHistory implements BaseEntity {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column content_share_history.id
     *
     * @mbggenerated Mon Jun 12 22:36:33 CST 2017
     */
    private Long id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column content_share_history.uid
     *
     * @mbggenerated Mon Jun 12 22:36:33 CST 2017
     */
    private Long uid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column content_share_history.create_time
     *
     * @mbggenerated Mon Jun 12 22:36:33 CST 2017
     */
    private Date createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column content_share_history.type
     *
     * @mbggenerated Mon Jun 12 22:36:33 CST 2017
     */
    private Integer type;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column content_share_history.cid
     *
     * @mbggenerated Mon Jun 12 22:36:33 CST 2017
     */
    private Long cid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column content_share_history.share_addr
     *
     * @mbggenerated Mon Jun 12 22:36:33 CST 2017
     */
    private String shareAddr;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column content_share_history.id
     *
     * @return the value of content_share_history.id
     *
     * @mbggenerated Mon Jun 12 22:36:33 CST 2017
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column content_share_history.id
     *
     * @param id the value for content_share_history.id
     *
     * @mbggenerated Mon Jun 12 22:36:33 CST 2017
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column content_share_history.uid
     *
     * @return the value of content_share_history.uid
     *
     * @mbggenerated Mon Jun 12 22:36:33 CST 2017
     */
    public Long getUid() {
        return uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column content_share_history.uid
     *
     * @param uid the value for content_share_history.uid
     *
     * @mbggenerated Mon Jun 12 22:36:33 CST 2017
     */
    public void setUid(Long uid) {
        this.uid = uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column content_share_history.create_time
     *
     * @return the value of content_share_history.create_time
     *
     * @mbggenerated Mon Jun 12 22:36:33 CST 2017
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column content_share_history.create_time
     *
     * @param createTime the value for content_share_history.create_time
     *
     * @mbggenerated Mon Jun 12 22:36:33 CST 2017
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column content_share_history.type
     *
     * @return the value of content_share_history.type
     *
     * @mbggenerated Mon Jun 12 22:36:33 CST 2017
     */
    public Integer getType() {
        return type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column content_share_history.type
     *
     * @param type the value for content_share_history.type
     *
     * @mbggenerated Mon Jun 12 22:36:33 CST 2017
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column content_share_history.cid
     *
     * @return the value of content_share_history.cid
     *
     * @mbggenerated Mon Jun 12 22:36:33 CST 2017
     */
    public Long getCid() {
        return cid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column content_share_history.cid
     *
     * @param cid the value for content_share_history.cid
     *
     * @mbggenerated Mon Jun 12 22:36:33 CST 2017
     */
    public void setCid(Long cid) {
        this.cid = cid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column content_share_history.share_addr
     *
     * @return the value of content_share_history.share_addr
     *
     * @mbggenerated Mon Jun 12 22:36:33 CST 2017
     */
    public String getShareAddr() {
        return shareAddr;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column content_share_history.share_addr
     *
     * @param shareAddr the value for content_share_history.share_addr
     *
     * @mbggenerated Mon Jun 12 22:36:33 CST 2017
     */
    public void setShareAddr(String shareAddr) {
        this.shareAddr = shareAddr == null ? null : shareAddr.trim();
    }
}