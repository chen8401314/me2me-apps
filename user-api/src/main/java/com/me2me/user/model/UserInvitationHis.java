package com.me2me.user.model;

import java.util.Date;

import com.me2me.common.web.BaseEntity;

public class UserInvitationHis implements BaseEntity {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_invitation_his.id
     *
     * @mbggenerated Tue Sep 19 14:58:42 CST 2017
     */
    private Long id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_invitation_his.uid
     *
     * @mbggenerated Tue Sep 19 14:58:42 CST 2017
     */
    private Long uid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_invitation_his.from_uid
     *
     * @mbggenerated Tue Sep 19 14:58:42 CST 2017
     */
    private Long fromUid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_invitation_his.from_cid
     *
     * @mbggenerated Tue Sep 19 14:58:42 CST 2017
     */
    private Long fromCid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_invitation_his.type
     *
     * @mbggenerated Tue Sep 19 14:58:42 CST 2017
     */
    private Integer type;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_invitation_his.coins
     *
     * @mbggenerated Tue Sep 19 14:58:42 CST 2017
     */
    private Integer coins;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_invitation_his.create_time
     *
     * @mbggenerated Tue Sep 19 14:58:42 CST 2017
     */
    private Date createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_invitation_his.status
     *
     * @mbggenerated Tue Sep 19 14:58:42 CST 2017
     */
    private Integer status;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_invitation_his.receive_time
     *
     * @mbggenerated Tue Sep 19 14:58:42 CST 2017
     */
    private Date receiveTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_invitation_his.id
     *
     * @return the value of user_invitation_his.id
     *
     * @mbggenerated Tue Sep 19 14:58:42 CST 2017
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_invitation_his.id
     *
     * @param id the value for user_invitation_his.id
     *
     * @mbggenerated Tue Sep 19 14:58:42 CST 2017
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_invitation_his.uid
     *
     * @return the value of user_invitation_his.uid
     *
     * @mbggenerated Tue Sep 19 14:58:42 CST 2017
     */
    public Long getUid() {
        return uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_invitation_his.uid
     *
     * @param uid the value for user_invitation_his.uid
     *
     * @mbggenerated Tue Sep 19 14:58:42 CST 2017
     */
    public void setUid(Long uid) {
        this.uid = uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_invitation_his.from_uid
     *
     * @return the value of user_invitation_his.from_uid
     *
     * @mbggenerated Tue Sep 19 14:58:42 CST 2017
     */
    public Long getFromUid() {
        return fromUid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_invitation_his.from_uid
     *
     * @param fromUid the value for user_invitation_his.from_uid
     *
     * @mbggenerated Tue Sep 19 14:58:42 CST 2017
     */
    public void setFromUid(Long fromUid) {
        this.fromUid = fromUid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_invitation_his.from_cid
     *
     * @return the value of user_invitation_his.from_cid
     *
     * @mbggenerated Tue Sep 19 14:58:42 CST 2017
     */
    public Long getFromCid() {
        return fromCid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_invitation_his.from_cid
     *
     * @param fromCid the value for user_invitation_his.from_cid
     *
     * @mbggenerated Tue Sep 19 14:58:42 CST 2017
     */
    public void setFromCid(Long fromCid) {
        this.fromCid = fromCid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_invitation_his.type
     *
     * @return the value of user_invitation_his.type
     *
     * @mbggenerated Tue Sep 19 14:58:42 CST 2017
     */
    public Integer getType() {
        return type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_invitation_his.type
     *
     * @param type the value for user_invitation_his.type
     *
     * @mbggenerated Tue Sep 19 14:58:42 CST 2017
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_invitation_his.coins
     *
     * @return the value of user_invitation_his.coins
     *
     * @mbggenerated Tue Sep 19 14:58:42 CST 2017
     */
    public Integer getCoins() {
        return coins;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_invitation_his.coins
     *
     * @param coins the value for user_invitation_his.coins
     *
     * @mbggenerated Tue Sep 19 14:58:42 CST 2017
     */
    public void setCoins(Integer coins) {
        this.coins = coins;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_invitation_his.create_time
     *
     * @return the value of user_invitation_his.create_time
     *
     * @mbggenerated Tue Sep 19 14:58:42 CST 2017
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_invitation_his.create_time
     *
     * @param createTime the value for user_invitation_his.create_time
     *
     * @mbggenerated Tue Sep 19 14:58:42 CST 2017
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_invitation_his.status
     *
     * @return the value of user_invitation_his.status
     *
     * @mbggenerated Tue Sep 19 14:58:42 CST 2017
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_invitation_his.status
     *
     * @param status the value for user_invitation_his.status
     *
     * @mbggenerated Tue Sep 19 14:58:42 CST 2017
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_invitation_his.receive_time
     *
     * @return the value of user_invitation_his.receive_time
     *
     * @mbggenerated Tue Sep 19 14:58:42 CST 2017
     */
    public Date getReceiveTime() {
        return receiveTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_invitation_his.receive_time
     *
     * @param receiveTime the value for user_invitation_his.receive_time
     *
     * @mbggenerated Tue Sep 19 14:58:42 CST 2017
     */
    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }
}