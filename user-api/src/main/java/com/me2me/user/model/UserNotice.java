package com.me2me.user.model;

import com.me2me.common.web.BaseEntity;

import java.util.Date;

public class UserNotice implements BaseEntity {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_notice.id
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    private Long id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_notice.notice_type
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    private Integer noticeType;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_notice.from_uid
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    private Long fromUid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_notice.to_uid
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    private Long toUid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_notice.from_nick_name
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    private String fromNickName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_notice.to_nick_name
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    private String toNickName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_notice.review
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    private String review;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_notice.tag
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    private String tag;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_notice.cover_image
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    private String coverImage;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_notice.summary
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    private String summary;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_notice.like_count
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    private Integer likeCount;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_notice.from_avatar
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    private String fromAvatar;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_notice.push_status
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    private Integer pushStatus;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_notice.read_status
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    private Integer readStatus;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_notice.cid
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    private Long cid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_notice.create_time
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    private Date createTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_notice.id
     *
     * @return the value of user_notice.id
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_notice.id
     *
     * @param id the value for user_notice.id
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_notice.notice_type
     *
     * @return the value of user_notice.notice_type
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    public Integer getNoticeType() {
        return noticeType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_notice.notice_type
     *
     * @param noticeType the value for user_notice.notice_type
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    public void setNoticeType(Integer noticeType) {
        this.noticeType = noticeType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_notice.from_uid
     *
     * @return the value of user_notice.from_uid
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    public Long getFromUid() {
        return fromUid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_notice.from_uid
     *
     * @param fromUid the value for user_notice.from_uid
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    public void setFromUid(Long fromUid) {
        this.fromUid = fromUid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_notice.to_uid
     *
     * @return the value of user_notice.to_uid
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    public Long getToUid() {
        return toUid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_notice.to_uid
     *
     * @param toUid the value for user_notice.to_uid
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    public void setToUid(Long toUid) {
        this.toUid = toUid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_notice.from_nick_name
     *
     * @return the value of user_notice.from_nick_name
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    public String getFromNickName() {
        return fromNickName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_notice.from_nick_name
     *
     * @param fromNickName the value for user_notice.from_nick_name
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    public void setFromNickName(String fromNickName) {
        this.fromNickName = fromNickName == null ? null : fromNickName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_notice.to_nick_name
     *
     * @return the value of user_notice.to_nick_name
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    public String getToNickName() {
        return toNickName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_notice.to_nick_name
     *
     * @param toNickName the value for user_notice.to_nick_name
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    public void setToNickName(String toNickName) {
        this.toNickName = toNickName == null ? null : toNickName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_notice.review
     *
     * @return the value of user_notice.review
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    public String getReview() {
        return review;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_notice.review
     *
     * @param review the value for user_notice.review
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    public void setReview(String review) {
        this.review = review == null ? null : review.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_notice.tag
     *
     * @return the value of user_notice.tag
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    public String getTag() {
        return tag;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_notice.tag
     *
     * @param tag the value for user_notice.tag
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    public void setTag(String tag) {
        this.tag = tag == null ? null : tag.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_notice.cover_image
     *
     * @return the value of user_notice.cover_image
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    public String getCoverImage() {
        return coverImage;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_notice.cover_image
     *
     * @param coverImage the value for user_notice.cover_image
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage == null ? null : coverImage.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_notice.summary
     *
     * @return the value of user_notice.summary
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    public String getSummary() {
        return summary;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_notice.summary
     *
     * @param summary the value for user_notice.summary
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    public void setSummary(String summary) {
        this.summary = summary == null ? null : summary.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_notice.like_count
     *
     * @return the value of user_notice.like_count
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    public Integer getLikeCount() {
        return likeCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_notice.like_count
     *
     * @param likeCount the value for user_notice.like_count
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_notice.from_avatar
     *
     * @return the value of user_notice.from_avatar
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    public String getFromAvatar() {
        return fromAvatar;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_notice.from_avatar
     *
     * @param fromAvatar the value for user_notice.from_avatar
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    public void setFromAvatar(String fromAvatar) {
        this.fromAvatar = fromAvatar == null ? null : fromAvatar.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_notice.push_status
     *
     * @return the value of user_notice.push_status
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    public Integer getPushStatus() {
        return pushStatus;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_notice.push_status
     *
     * @param pushStatus the value for user_notice.push_status
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    public void setPushStatus(Integer pushStatus) {
        this.pushStatus = pushStatus;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_notice.read_status
     *
     * @return the value of user_notice.read_status
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    public Integer getReadStatus() {
        return readStatus;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_notice.read_status
     *
     * @param readStatus the value for user_notice.read_status
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    public void setReadStatus(Integer readStatus) {
        this.readStatus = readStatus;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_notice.cid
     *
     * @return the value of user_notice.cid
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    public Long getCid() {
        return cid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_notice.cid
     *
     * @param cid the value for user_notice.cid
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    public void setCid(Long cid) {
        this.cid = cid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_notice.create_time
     *
     * @return the value of user_notice.create_time
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_notice.create_time
     *
     * @param createTime the value for user_notice.create_time
     *
     * @mbggenerated Wed Jun 29 13:28:20 CST 2016
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}