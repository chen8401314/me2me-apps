package com.me2me.live.model;

import java.util.Date;

import com.me2me.common.web.BaseEntity;

public class RobotInfo  implements BaseEntity{
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column robot_info.id
     *
     * @mbggenerated Thu Jul 20 13:53:53 CST 2017
     */
    private Long id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column robot_info.uid
     *
     * @mbggenerated Thu Jul 20 13:53:53 CST 2017
     */
    private Long uid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column robot_info.create_time
     *
     * @mbggenerated Thu Jul 20 13:53:53 CST 2017
     */
    private Date createTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column robot_info.id
     *
     * @return the value of robot_info.id
     *
     * @mbggenerated Thu Jul 20 13:53:53 CST 2017
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column robot_info.id
     *
     * @param id the value for robot_info.id
     *
     * @mbggenerated Thu Jul 20 13:53:53 CST 2017
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column robot_info.uid
     *
     * @return the value of robot_info.uid
     *
     * @mbggenerated Thu Jul 20 13:53:53 CST 2017
     */
    public Long getUid() {
        return uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column robot_info.uid
     *
     * @param uid the value for robot_info.uid
     *
     * @mbggenerated Thu Jul 20 13:53:53 CST 2017
     */
    public void setUid(Long uid) {
        this.uid = uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column robot_info.create_time
     *
     * @return the value of robot_info.create_time
     *
     * @mbggenerated Thu Jul 20 13:53:53 CST 2017
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column robot_info.create_time
     *
     * @param createTime the value for robot_info.create_time
     *
     * @mbggenerated Thu Jul 20 13:53:53 CST 2017
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}