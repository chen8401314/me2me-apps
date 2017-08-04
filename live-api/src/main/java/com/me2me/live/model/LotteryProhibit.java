package com.me2me.live.model;

import java.util.Date;

import com.me2me.common.web.BaseEntity;

public class LotteryProhibit  implements BaseEntity{
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column lottery_prohibit.id
     *
     * @mbggenerated Fri Aug 04 15:07:45 CST 2017
     */
    private Long id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column lottery_prohibit.lottery_id
     *
     * @mbggenerated Fri Aug 04 15:07:45 CST 2017
     */
    private Long lotteryId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column lottery_prohibit.uid
     *
     * @mbggenerated Fri Aug 04 15:07:45 CST 2017
     */
    private Long uid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column lottery_prohibit.create_time
     *
     * @mbggenerated Fri Aug 04 15:07:45 CST 2017
     */
    private Date createTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column lottery_prohibit.id
     *
     * @return the value of lottery_prohibit.id
     *
     * @mbggenerated Fri Aug 04 15:07:45 CST 2017
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column lottery_prohibit.id
     *
     * @param id the value for lottery_prohibit.id
     *
     * @mbggenerated Fri Aug 04 15:07:45 CST 2017
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column lottery_prohibit.lottery_id
     *
     * @return the value of lottery_prohibit.lottery_id
     *
     * @mbggenerated Fri Aug 04 15:07:45 CST 2017
     */
    public Long getLotteryId() {
        return lotteryId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column lottery_prohibit.lottery_id
     *
     * @param lotteryId the value for lottery_prohibit.lottery_id
     *
     * @mbggenerated Fri Aug 04 15:07:45 CST 2017
     */
    public void setLotteryId(Long lotteryId) {
        this.lotteryId = lotteryId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column lottery_prohibit.uid
     *
     * @return the value of lottery_prohibit.uid
     *
     * @mbggenerated Fri Aug 04 15:07:45 CST 2017
     */
    public Long getUid() {
        return uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column lottery_prohibit.uid
     *
     * @param uid the value for lottery_prohibit.uid
     *
     * @mbggenerated Fri Aug 04 15:07:45 CST 2017
     */
    public void setUid(Long uid) {
        this.uid = uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column lottery_prohibit.create_time
     *
     * @return the value of lottery_prohibit.create_time
     *
     * @mbggenerated Fri Aug 04 15:07:45 CST 2017
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column lottery_prohibit.create_time
     *
     * @param createTime the value for lottery_prohibit.create_time
     *
     * @mbggenerated Fri Aug 04 15:07:45 CST 2017
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}