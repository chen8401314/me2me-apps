package com.me2me.content.model;

import java.util.Date;

public class ContentReview {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column content_review.id
     *
     * @mbggenerated Tue May 03 16:59:57 CST 2016
     */
    private Long id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column content_review.uid
     *
     * @mbggenerated Tue May 03 16:59:57 CST 2016
     */
    private Long uid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column content_review.review
     *
     * @mbggenerated Tue May 03 16:59:57 CST 2016
     */
    private String review;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column content_review.cid
     *
     * @mbggenerated Tue May 03 16:59:57 CST 2016
     */
    private Long cid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column content_review.create_time
     *
     * @mbggenerated Tue May 03 16:59:57 CST 2016
     */
    private Date createTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column content_review.id
     *
     * @return the value of content_review.id
     *
     * @mbggenerated Tue May 03 16:59:57 CST 2016
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column content_review.id
     *
     * @param id the value for content_review.id
     *
     * @mbggenerated Tue May 03 16:59:57 CST 2016
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column content_review.uid
     *
     * @return the value of content_review.uid
     *
     * @mbggenerated Tue May 03 16:59:57 CST 2016
     */
    public Long getUid() {
        return uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column content_review.uid
     *
     * @param uid the value for content_review.uid
     *
     * @mbggenerated Tue May 03 16:59:57 CST 2016
     */
    public void setUid(Long uid) {
        this.uid = uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column content_review.review
     *
     * @return the value of content_review.review
     *
     * @mbggenerated Tue May 03 16:59:57 CST 2016
     */
    public String getReview() {
        return review;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column content_review.review
     *
     * @param review the value for content_review.review
     *
     * @mbggenerated Tue May 03 16:59:57 CST 2016
     */
    public void setReview(String review) {
        this.review = review == null ? null : review.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column content_review.cid
     *
     * @return the value of content_review.cid
     *
     * @mbggenerated Tue May 03 16:59:57 CST 2016
     */
    public Long getCid() {
        return cid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column content_review.cid
     *
     * @param cid the value for content_review.cid
     *
     * @mbggenerated Tue May 03 16:59:57 CST 2016
     */
    public void setCid(Long cid) {
        this.cid = cid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column content_review.create_time
     *
     * @return the value of content_review.create_time
     *
     * @mbggenerated Tue May 03 16:59:57 CST 2016
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column content_review.create_time
     *
     * @param createTime the value for content_review.create_time
     *
     * @mbggenerated Tue May 03 16:59:57 CST 2016
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}