package com.me2me.activity.model;

import com.me2me.common.web.BaseEntity;

public class ArecommendUserDesc implements BaseEntity {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column a_recommend_user_desc.id
     *
     * @mbggenerated Wed Dec 14 15:08:28 CST 2016
     */
    private Long id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column a_recommend_user_desc.auid
     *
     * @mbggenerated Wed Dec 14 15:08:28 CST 2016
     */
    private Long auid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column a_recommend_user_desc.uid
     *
     * @mbggenerated Wed Dec 14 15:08:28 CST 2016
     */
    private Long uid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column a_recommend_user_desc.rec_uid
     *
     * @mbggenerated Wed Dec 14 15:08:28 CST 2016
     */
    private Long recUid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column a_recommend_user_desc.rec_time_key
     *
     * @mbggenerated Wed Dec 14 15:08:28 CST 2016
     */
    private String recTimeKey;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column a_recommend_user_desc.id
     *
     * @return the value of a_recommend_user_desc.id
     *
     * @mbggenerated Wed Dec 14 15:08:28 CST 2016
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column a_recommend_user_desc.id
     *
     * @param id the value for a_recommend_user_desc.id
     *
     * @mbggenerated Wed Dec 14 15:08:28 CST 2016
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column a_recommend_user_desc.auid
     *
     * @return the value of a_recommend_user_desc.auid
     *
     * @mbggenerated Wed Dec 14 15:08:28 CST 2016
     */
    public Long getAuid() {
        return auid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column a_recommend_user_desc.auid
     *
     * @param auid the value for a_recommend_user_desc.auid
     *
     * @mbggenerated Wed Dec 14 15:08:28 CST 2016
     */
    public void setAuid(Long auid) {
        this.auid = auid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column a_recommend_user_desc.uid
     *
     * @return the value of a_recommend_user_desc.uid
     *
     * @mbggenerated Wed Dec 14 15:08:28 CST 2016
     */
    public Long getUid() {
        return uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column a_recommend_user_desc.uid
     *
     * @param uid the value for a_recommend_user_desc.uid
     *
     * @mbggenerated Wed Dec 14 15:08:28 CST 2016
     */
    public void setUid(Long uid) {
        this.uid = uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column a_recommend_user_desc.rec_uid
     *
     * @return the value of a_recommend_user_desc.rec_uid
     *
     * @mbggenerated Wed Dec 14 15:08:28 CST 2016
     */
    public Long getRecUid() {
        return recUid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column a_recommend_user_desc.rec_uid
     *
     * @param recUid the value for a_recommend_user_desc.rec_uid
     *
     * @mbggenerated Wed Dec 14 15:08:28 CST 2016
     */
    public void setRecUid(Long recUid) {
        this.recUid = recUid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column a_recommend_user_desc.rec_time_key
     *
     * @return the value of a_recommend_user_desc.rec_time_key
     *
     * @mbggenerated Wed Dec 14 15:08:28 CST 2016
     */
    public String getRecTimeKey() {
        return recTimeKey;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column a_recommend_user_desc.rec_time_key
     *
     * @param recTimeKey the value for a_recommend_user_desc.rec_time_key
     *
     * @mbggenerated Wed Dec 14 15:08:28 CST 2016
     */
    public void setRecTimeKey(String recTimeKey) {
        this.recTimeKey = recTimeKey == null ? null : recTimeKey.trim();
    }
}