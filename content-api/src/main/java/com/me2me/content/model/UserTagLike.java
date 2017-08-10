package com.me2me.content.model;

import java.io.Serializable;
import java.util.Date;

public class UserTagLike implements Serializable{
    /**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_tag_like.id
     *
     * @mbggenerated Tue Aug 08 16:48:46 CST 2017
     */
    private Long id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_tag_like.uid
     *
     * @mbggenerated Tue Aug 08 16:48:46 CST 2017
     */
    private Long uid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_tag_like.tag
     *
     * @mbggenerated Tue Aug 08 16:48:46 CST 2017
     */
    private String tag;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_tag_like.score
     *
     * @mbggenerated Tue Aug 08 16:48:46 CST 2017
     */
    private Integer score;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_tag_like.last_update_time
     *
     * @mbggenerated Tue Aug 08 16:48:46 CST 2017
     */
    private Date lastUpdateTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_tag_like.id
     *
     * @return the value of user_tag_like.id
     *
     * @mbggenerated Tue Aug 08 16:48:46 CST 2017
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_tag_like.id
     *
     * @param id the value for user_tag_like.id
     *
     * @mbggenerated Tue Aug 08 16:48:46 CST 2017
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_tag_like.uid
     *
     * @return the value of user_tag_like.uid
     *
     * @mbggenerated Tue Aug 08 16:48:46 CST 2017
     */
    public Long getUid() {
        return uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_tag_like.uid
     *
     * @param uid the value for user_tag_like.uid
     *
     * @mbggenerated Tue Aug 08 16:48:46 CST 2017
     */
    public void setUid(Long uid) {
        this.uid = uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_tag_like.tag
     *
     * @return the value of user_tag_like.tag
     *
     * @mbggenerated Tue Aug 08 16:48:46 CST 2017
     */
    public String getTag() {
        return tag;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_tag_like.tag
     *
     * @param tag the value for user_tag_like.tag
     *
     * @mbggenerated Tue Aug 08 16:48:46 CST 2017
     */
    public void setTag(String tag) {
        this.tag = tag == null ? null : tag.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_tag_like.score
     *
     * @return the value of user_tag_like.score
     *
     * @mbggenerated Tue Aug 08 16:48:46 CST 2017
     */
    public Integer getScore() {
        return score;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_tag_like.score
     *
     * @param score the value for user_tag_like.score
     *
     * @mbggenerated Tue Aug 08 16:48:46 CST 2017
     */
    public void setScore(Integer score) {
        this.score = score;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_tag_like.last_update_time
     *
     * @return the value of user_tag_like.last_update_time
     *
     * @mbggenerated Tue Aug 08 16:48:46 CST 2017
     */
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_tag_like.last_update_time
     *
     * @param lastUpdateTime the value for user_tag_like.last_update_time
     *
     * @mbggenerated Tue Aug 08 16:48:46 CST 2017
     */
    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}