package com.me2me.content.model;

import java.util.Date;

public class ArticleReview {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column article_review.id
     *
     * @mbggenerated Tue Jun 07 17:02:42 CST 2016
     */
    private Long id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column article_review.article_id
     *
     * @mbggenerated Tue Jun 07 17:02:42 CST 2016
     */
    private Long articleId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column article_review.uid
     *
     * @mbggenerated Tue Jun 07 17:02:42 CST 2016
     */
    private Long uid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column article_review.review
     *
     * @mbggenerated Tue Jun 07 17:02:42 CST 2016
     */
    private String review;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column article_review.create_time
     *
     * @mbggenerated Tue Jun 07 17:02:42 CST 2016
     */
    private Date createTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column article_review.id
     *
     * @return the value of article_review.id
     *
     * @mbggenerated Tue Jun 07 17:02:42 CST 2016
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column article_review.id
     *
     * @param id the value for article_review.id
     *
     * @mbggenerated Tue Jun 07 17:02:42 CST 2016
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column article_review.article_id
     *
     * @return the value of article_review.article_id
     *
     * @mbggenerated Tue Jun 07 17:02:42 CST 2016
     */
    public Long getArticleId() {
        return articleId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column article_review.article_id
     *
     * @param articleId the value for article_review.article_id
     *
     * @mbggenerated Tue Jun 07 17:02:42 CST 2016
     */
    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column article_review.uid
     *
     * @return the value of article_review.uid
     *
     * @mbggenerated Tue Jun 07 17:02:42 CST 2016
     */
    public Long getUid() {
        return uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column article_review.uid
     *
     * @param uid the value for article_review.uid
     *
     * @mbggenerated Tue Jun 07 17:02:42 CST 2016
     */
    public void setUid(Long uid) {
        this.uid = uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column article_review.review
     *
     * @return the value of article_review.review
     *
     * @mbggenerated Tue Jun 07 17:02:42 CST 2016
     */
    public String getReview() {
        return review;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column article_review.review
     *
     * @param review the value for article_review.review
     *
     * @mbggenerated Tue Jun 07 17:02:42 CST 2016
     */
    public void setReview(String review) {
        this.review = review == null ? null : review.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column article_review.create_time
     *
     * @return the value of article_review.create_time
     *
     * @mbggenerated Tue Jun 07 17:02:42 CST 2016
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column article_review.create_time
     *
     * @param createTime the value for article_review.create_time
     *
     * @mbggenerated Tue Jun 07 17:02:42 CST 2016
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}