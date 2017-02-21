package com.me2me.user.dao;

import com.me2me.core.dao.BaseJdbcDao;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/5/10.
 */
@Repository
public class UserInitJdbcDao extends BaseJdbcDao {

    public void batchInsertMeNumber(List<Integer> values) {
        String sql = "insert into user_no (me_number) values ";
        StringBuilder sb = new StringBuilder();
        for (Integer value : values) {
            if (value != values.get(values.size() - 1)) {
                sb.append("(").append(value).append(")").append(",");
            } else {
                sb.append("(").append(value).append(")");
            }
        }
        this.execute(sql + sb.toString());
    }

    public List<Map<String, Object>> getRobots(int limit) {
        String sql = "select * from user where user_name BETWEEN '18900000200' and '18900000384' order by RAND() limit " + limit;
        return super.query(sql);
    }


    public List<Map<String, Object>> getUserNoticeCounter(String value) {
        String sql = "SELECT to_uid as uid,count(to_uid) as counter from user_notice where push_status = 0 and notice_type in (" + value + ") group by to_uid";
        return super.query(sql);
    }

    public List<Map<String, Object>> getUserNoticeList(String value) {
        String sql = "SELECT id from user_notice where push_status = 0 and notice_type in (" + value + ")";
        return super.query(sql);
    }

    public int getContentCount(long uid) {
        String sql = "select count(*) as count from content where uid = ?";
        return super.count(sql,uid);
    }

    /**
     * 获取直播数量
     * @param uid
     * @return
     */
    public int getLiveCount(long uid) {
        String sql = "select count(*) as count from topic where (uid = ? or FIND_IN_SET(?,SUBSTR(core_circle FROM 2 FOR LENGTH(core_circle)-2))) and status <> 2 " ;
        return super.count(sql,uid,uid);
    }

    public int getLiveAcCount(long uid) {
        String sql = "select count(*) as count from topic where (uid = ? or FIND_IN_SET(?,SUBSTR(core_circle FROM 2 FOR LENGTH(core_circle)-2))) and status <> 2 and type = 1000 " ;
        return super.count(sql,uid,uid);
    }

    public int getUGCount(long uid, int vFlag){
    	String sql = null;
    	if(vFlag == 0){
    		sql = "select count(*) as count from content where uid = ? and status <> 1 and type in (0,1,8,9)" ;
    	}else{
    		sql = "select count(*) as count from content where uid = ? and status <> 1 and type in (0,1,6,8,9)" ;
    	}
    	
        return super.count(sql,uid);
    }

    public List<Map<String, Object>> getPhoto(long sinceId) {
        String sql = "select id , image as imageUrl,cid as title from content_image where id < "+ sinceId +" order by id desc limit 100 ";
        return super.query(sql);
    }

    public List<Map<String, Object>> getLuckStatusOperateMobile() {
        String sql = "select operate_mobile from luck_status ";
        return super.query(sql);
    }

    public List<Map<String, Object>> searchUserProfilesByPage(String nickName, String mobile, int vLv, int status, String startTime, String endTime, int start, int pageSize){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select p.avatar,u.create_time,p.gender,p.mobile,p.nick_name,p.third_part_bind,p.uid,p.v_lv,p.birthday,u.disable_user ");
    	sb.append("from user u,user_profile p ");
    	sb.append("where u.uid=p.uid ");
    	if(null != nickName && !"".equals(nickName)){
    		sb.append("and p.nick_name like '%").append(nickName).append("%' ");
    	}
    	if(null != mobile && !"".equals(mobile)){
    		sb.append("and p.mobile like '%").append(mobile).append("%' ");
    	}
    	if(vLv >= 0){
    		sb.append("and p.v_lv=").append(vLv).append(" ");
    	}
    	if(status >= 0){
    		sb.append("and u.disable_user=").append(status).append(" ");
    	}
    	if(null != startTime && !"".equals(startTime)){
    		sb.append("and u.create_time>='").append(startTime).append("' ");
    	}
    	if(null != endTime && !"".equals(endTime)){
    		sb.append("and u.create_time<='").append(endTime).append("' ");
    	}
    	sb.append("order by u.create_time desc limit ").append(start).append(",").append(pageSize);
    	String sql = sb.toString();
    	return super.query(sql);
    }
    
    public int countUserProfilesByPage(String nickName, String mobile, int vLv, int status, String startTime, String endTime){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select count(1) as count ");
    	sb.append("from user u,user_profile p ");
    	sb.append("where u.uid=p.uid ");
    	if(null != nickName && !"".equals(nickName)){
    		sb.append("and p.nick_name like '%").append(nickName).append("%' ");
    	}
    	if(null != mobile && !"".equals(mobile)){
    		sb.append("and p.mobile like '%").append(mobile).append("%' ");
    	}
    	if(vLv >= 0){
    		sb.append("and p.v_lv=").append(vLv).append(" ");
    	}
    	if(status >= 0){
    		sb.append("and u.disable_user=").append(status).append(" ");
    	}
    	if(null != startTime && !"".equals(startTime)){
    		sb.append("and u.create_time>='").append(startTime).append("' ");
    	}
    	if(null != endTime && !"".equals(endTime)){
    		sb.append("and u.create_time<='").append(endTime).append("' ");
    	}
    	String sql = sb.toString();
    	return super.count(sql);
    }
}
