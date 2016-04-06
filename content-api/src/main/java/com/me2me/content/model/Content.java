package com.me2me.content.model;

import java.util.Date;

public class Content {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column content.id
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    private Long id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column content.uid
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    private Long uid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column content.forward_cid
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    private Long forwardCid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column content.title
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    private String title;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column content.feeling
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    private String feeling;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column content.type
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    private Integer type;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column content.conver_image
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    private String converImage;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column content.forward_title
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    private String forwardTitle;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column content.forward_url
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    private String forwardUrl;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column content.content_type
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    private Integer contentType;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column content.thumbnail
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    private String thumbnail;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column content.hot_value
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    private Integer hotValue;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column content.like_count
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    private Integer likeCount;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column content.authorization
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    private Integer authorization;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column content.create_time
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    private Date createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column content.status
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    private Integer status;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column content.content
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    private String content;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column content.id
     *
     * @return the value of content.id
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column content.id
     *
     * @param id the value for content.id
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column content.uid
     *
     * @return the value of content.uid
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public Long getUid() {
        return uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column content.uid
     *
     * @param uid the value for content.uid
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public void setUid(Long uid) {
        this.uid = uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column content.forward_cid
     *
     * @return the value of content.forward_cid
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public Long getForwardCid() {
        return forwardCid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column content.forward_cid
     *
     * @param forwardCid the value for content.forward_cid
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public void setForwardCid(Long forwardCid) {
        this.forwardCid = forwardCid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column content.title
     *
     * @return the value of content.title
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public String getTitle() {
        return title;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column content.title
     *
     * @param title the value for content.title
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column content.feeling
     *
     * @return the value of content.feeling
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public String getFeeling() {
        return feeling;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column content.feeling
     *
     * @param feeling the value for content.feeling
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public void setFeeling(String feeling) {
        this.feeling = feeling == null ? null : feeling.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column content.type
     *
     * @return the value of content.type
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public Integer getType() {
        return type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column content.type
     *
     * @param type the value for content.type
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column content.conver_image
     *
     * @return the value of content.conver_image
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public String getConverImage() {
        return converImage;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column content.conver_image
     *
     * @param converImage the value for content.conver_image
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public void setConverImage(String converImage) {
        this.converImage = converImage == null ? null : converImage.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column content.forward_title
     *
     * @return the value of content.forward_title
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public String getForwardTitle() {
        return forwardTitle;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column content.forward_title
     *
     * @param forwardTitle the value for content.forward_title
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public void setForwardTitle(String forwardTitle) {
        this.forwardTitle = forwardTitle == null ? null : forwardTitle.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column content.forward_url
     *
     * @return the value of content.forward_url
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public String getForwardUrl() {
        return forwardUrl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column content.forward_url
     *
     * @param forwardUrl the value for content.forward_url
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public void setForwardUrl(String forwardUrl) {
        this.forwardUrl = forwardUrl == null ? null : forwardUrl.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column content.content_type
     *
     * @return the value of content.content_type
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public Integer getContentType() {
        return contentType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column content.content_type
     *
     * @param contentType the value for content.content_type
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public void setContentType(Integer contentType) {
        this.contentType = contentType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column content.thumbnail
     *
     * @return the value of content.thumbnail
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public String getThumbnail() {
        return thumbnail;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column content.thumbnail
     *
     * @param thumbnail the value for content.thumbnail
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail == null ? null : thumbnail.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column content.hot_value
     *
     * @return the value of content.hot_value
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public Integer getHotValue() {
        return hotValue;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column content.hot_value
     *
     * @param hotValue the value for content.hot_value
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public void setHotValue(Integer hotValue) {
        this.hotValue = hotValue;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column content.like_count
     *
     * @return the value of content.like_count
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public Integer getLikeCount() {
        return likeCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column content.like_count
     *
     * @param likeCount the value for content.like_count
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column content.authorization
     *
     * @return the value of content.authorization
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public Integer getAuthorization() {
        return authorization;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column content.authorization
     *
     * @param authorization the value for content.authorization
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public void setAuthorization(Integer authorization) {
        this.authorization = authorization;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column content.create_time
     *
     * @return the value of content.create_time
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column content.create_time
     *
     * @param createTime the value for content.create_time
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column content.status
     *
     * @return the value of content.status
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column content.status
     *
     * @param status the value for content.status
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column content.content
     *
     * @return the value of content.content
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public String getContent() {
        return content;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column content.content
     *
     * @param content the value for content.content
     *
     * @mbggenerated Wed Apr 06 16:27:35 CST 2016
     */
    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }
}