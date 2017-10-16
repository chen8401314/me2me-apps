package com.me2me.activity.model;

import java.util.Date;

import com.me2me.common.web.BaseEntity;

public class GameLookHis implements BaseEntity {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column game_look_his.id
     *
     * @mbggenerated Mon Oct 16 13:57:57 CST 2017
     */
    private Long id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column game_look_his.game_uid
     *
     * @mbggenerated Mon Oct 16 13:57:57 CST 2017
     */
    private Long gameUid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column game_look_his.uid
     *
     * @mbggenerated Mon Oct 16 13:57:57 CST 2017
     */
    private Long uid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column game_look_his.create_time
     *
     * @mbggenerated Mon Oct 16 13:57:57 CST 2017
     */
    private Date createTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column game_look_his.id
     *
     * @return the value of game_look_his.id
     *
     * @mbggenerated Mon Oct 16 13:57:57 CST 2017
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column game_look_his.id
     *
     * @param id the value for game_look_his.id
     *
     * @mbggenerated Mon Oct 16 13:57:57 CST 2017
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column game_look_his.game_uid
     *
     * @return the value of game_look_his.game_uid
     *
     * @mbggenerated Mon Oct 16 13:57:57 CST 2017
     */
    public Long getGameUid() {
        return gameUid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column game_look_his.game_uid
     *
     * @param gameUid the value for game_look_his.game_uid
     *
     * @mbggenerated Mon Oct 16 13:57:57 CST 2017
     */
    public void setGameUid(Long gameUid) {
        this.gameUid = gameUid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column game_look_his.uid
     *
     * @return the value of game_look_his.uid
     *
     * @mbggenerated Mon Oct 16 13:57:57 CST 2017
     */
    public Long getUid() {
        return uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column game_look_his.uid
     *
     * @param uid the value for game_look_his.uid
     *
     * @mbggenerated Mon Oct 16 13:57:57 CST 2017
     */
    public void setUid(Long uid) {
        this.uid = uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column game_look_his.create_time
     *
     * @return the value of game_look_his.create_time
     *
     * @mbggenerated Mon Oct 16 13:57:57 CST 2017
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column game_look_his.create_time
     *
     * @param createTime the value for game_look_his.create_time
     *
     * @mbggenerated Mon Oct 16 13:57:57 CST 2017
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}