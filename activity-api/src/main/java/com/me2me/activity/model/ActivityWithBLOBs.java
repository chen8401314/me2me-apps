package com.me2me.activity.model;

public class ActivityWithBLOBs extends Activity {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column activity.activity_content
     *
     * @mbggenerated Tue Apr 26 20:29:20 CST 2016
     */
    private String activityContent;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column activity.ext1
     *
     * @mbggenerated Tue Apr 26 20:29:20 CST 2016
     */
    private String ext1;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column activity.ext2
     *
     * @mbggenerated Tue Apr 26 20:29:20 CST 2016
     */
    private String ext2;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column activity.activity_content
     *
     * @return the value of activity.activity_content
     *
     * @mbggenerated Tue Apr 26 20:29:20 CST 2016
     */
    public String getActivityContent() {
        return activityContent;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column activity.activity_content
     *
     * @param activityContent the value for activity.activity_content
     *
     * @mbggenerated Tue Apr 26 20:29:20 CST 2016
     */
    public void setActivityContent(String activityContent) {
        this.activityContent = activityContent == null ? null : activityContent.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column activity.ext1
     *
     * @return the value of activity.ext1
     *
     * @mbggenerated Tue Apr 26 20:29:20 CST 2016
     */
    public String getExt1() {
        return ext1;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column activity.ext1
     *
     * @param ext1 the value for activity.ext1
     *
     * @mbggenerated Tue Apr 26 20:29:20 CST 2016
     */
    public void setExt1(String ext1) {
        this.ext1 = ext1 == null ? null : ext1.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column activity.ext2
     *
     * @return the value of activity.ext2
     *
     * @mbggenerated Tue Apr 26 20:29:20 CST 2016
     */
    public String getExt2() {
        return ext2;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column activity.ext2
     *
     * @param ext2 the value for activity.ext2
     *
     * @mbggenerated Tue Apr 26 20:29:20 CST 2016
     */
    public void setExt2(String ext2) {
        this.ext2 = ext2 == null ? null : ext2.trim();
    }
}