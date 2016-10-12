package com.me2me.user.model;

import com.me2me.common.web.BaseEntity;

import java.util.Date;

public class UserProfile implements BaseEntity{
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_profile.id
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    private Long id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_profile.uid
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    private Long uid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_profile.mobile
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    private String mobile;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_profile.nick_name
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    private String nickName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_profile.gender
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    private Integer gender;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_profile.birthday
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    private String birthday;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_profile.avatar
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    private String avatar;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_profile.years_id
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    private Long yearsId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_profile.star_id
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    private Long starId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_profile.social_class
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    private Long socialClass;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_profile.industry
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    private Long industry;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_profile.marriage_status
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    private Long marriageStatus;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_profile.bear_status
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    private Long bearStatus;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_profile.excellent
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    private Integer excellent;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_profile.introduced
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    private String introduced;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_profile.create_time
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    private Date createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_profile.update_time
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    private Date updateTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_profile.is_promoter
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    private Integer isPromoter;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_profile.is_activate
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    private Integer isActivate;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_profile.referee_uid
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    private Long refereeUid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_profile.third_part_bind
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    private String thirdPartBind;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_profile.qrcode
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    private String qrcode;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_profile.v_lv
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    private Integer vLv;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_profile.id
     *
     * @return the value of user_profile.id
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_profile.id
     *
     * @param id the value for user_profile.id
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_profile.uid
     *
     * @return the value of user_profile.uid
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public Long getUid() {
        return uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_profile.uid
     *
     * @param uid the value for user_profile.uid
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public void setUid(Long uid) {
        this.uid = uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_profile.mobile
     *
     * @return the value of user_profile.mobile
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_profile.mobile
     *
     * @param mobile the value for user_profile.mobile
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_profile.nick_name
     *
     * @return the value of user_profile.nick_name
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_profile.nick_name
     *
     * @param nickName the value for user_profile.nick_name
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public void setNickName(String nickName) {
        this.nickName = nickName == null ? null : nickName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_profile.gender
     *
     * @return the value of user_profile.gender
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public Integer getGender() {
        return gender;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_profile.gender
     *
     * @param gender the value for user_profile.gender
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public void setGender(Integer gender) {
        this.gender = gender;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_profile.birthday
     *
     * @return the value of user_profile.birthday
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public String getBirthday() {
        return birthday;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_profile.birthday
     *
     * @param birthday the value for user_profile.birthday
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public void setBirthday(String birthday) {
        this.birthday = birthday == null ? null : birthday.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_profile.avatar
     *
     * @return the value of user_profile.avatar
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_profile.avatar
     *
     * @param avatar the value for user_profile.avatar
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar == null ? null : avatar.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_profile.years_id
     *
     * @return the value of user_profile.years_id
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public Long getYearsId() {
        return yearsId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_profile.years_id
     *
     * @param yearsId the value for user_profile.years_id
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public void setYearsId(Long yearsId) {
        this.yearsId = yearsId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_profile.star_id
     *
     * @return the value of user_profile.star_id
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public Long getStarId() {
        return starId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_profile.star_id
     *
     * @param starId the value for user_profile.star_id
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public void setStarId(Long starId) {
        this.starId = starId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_profile.social_class
     *
     * @return the value of user_profile.social_class
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public Long getSocialClass() {
        return socialClass;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_profile.social_class
     *
     * @param socialClass the value for user_profile.social_class
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public void setSocialClass(Long socialClass) {
        this.socialClass = socialClass;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_profile.industry
     *
     * @return the value of user_profile.industry
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public Long getIndustry() {
        return industry;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_profile.industry
     *
     * @param industry the value for user_profile.industry
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public void setIndustry(Long industry) {
        this.industry = industry;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_profile.marriage_status
     *
     * @return the value of user_profile.marriage_status
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public Long getMarriageStatus() {
        return marriageStatus;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_profile.marriage_status
     *
     * @param marriageStatus the value for user_profile.marriage_status
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public void setMarriageStatus(Long marriageStatus) {
        this.marriageStatus = marriageStatus;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_profile.bear_status
     *
     * @return the value of user_profile.bear_status
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public Long getBearStatus() {
        return bearStatus;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_profile.bear_status
     *
     * @param bearStatus the value for user_profile.bear_status
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public void setBearStatus(Long bearStatus) {
        this.bearStatus = bearStatus;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_profile.excellent
     *
     * @return the value of user_profile.excellent
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public Integer getExcellent() {
        return excellent;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_profile.excellent
     *
     * @param excellent the value for user_profile.excellent
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public void setExcellent(Integer excellent) {
        this.excellent = excellent;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_profile.introduced
     *
     * @return the value of user_profile.introduced
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public String getIntroduced() {
        return introduced;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_profile.introduced
     *
     * @param introduced the value for user_profile.introduced
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public void setIntroduced(String introduced) {
        this.introduced = introduced == null ? null : introduced.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_profile.create_time
     *
     * @return the value of user_profile.create_time
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_profile.create_time
     *
     * @param createTime the value for user_profile.create_time
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_profile.update_time
     *
     * @return the value of user_profile.update_time
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_profile.update_time
     *
     * @param updateTime the value for user_profile.update_time
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_profile.is_promoter
     *
     * @return the value of user_profile.is_promoter
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public Integer getIsPromoter() {
        return isPromoter;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_profile.is_promoter
     *
     * @param isPromoter the value for user_profile.is_promoter
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public void setIsPromoter(Integer isPromoter) {
        this.isPromoter = isPromoter;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_profile.is_activate
     *
     * @return the value of user_profile.is_activate
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public Integer getIsActivate() {
        return isActivate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_profile.is_activate
     *
     * @param isActivate the value for user_profile.is_activate
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public void setIsActivate(Integer isActivate) {
        this.isActivate = isActivate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_profile.referee_uid
     *
     * @return the value of user_profile.referee_uid
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public Long getRefereeUid() {
        return refereeUid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_profile.referee_uid
     *
     * @param refereeUid the value for user_profile.referee_uid
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public void setRefereeUid(Long refereeUid) {
        this.refereeUid = refereeUid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_profile.third_part_bind
     *
     * @return the value of user_profile.third_part_bind
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public String getThirdPartBind() {
        return thirdPartBind;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_profile.third_part_bind
     *
     * @param thirdPartBind the value for user_profile.third_part_bind
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public void setThirdPartBind(String thirdPartBind) {
        this.thirdPartBind = thirdPartBind == null ? null : thirdPartBind.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_profile.qrcode
     *
     * @return the value of user_profile.qrcode
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public String getQrcode() {
        return qrcode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_profile.qrcode
     *
     * @param qrcode the value for user_profile.qrcode
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public void setQrcode(String qrcode) {
        this.qrcode = qrcode == null ? null : qrcode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_profile.v_lv
     *
     * @return the value of user_profile.v_lv
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public Integer getvLv() {
        return vLv;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_profile.v_lv
     *
     * @param vLv the value for user_profile.v_lv
     *
     * @mbggenerated Tue Sep 27 15:33:51 CST 2016
     */
    public void setvLv(Integer vLv) {
        this.vLv = vLv;
    }
}