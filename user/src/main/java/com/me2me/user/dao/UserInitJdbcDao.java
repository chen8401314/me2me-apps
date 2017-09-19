package com.me2me.user.dao;

import com.me2me.common.utils.DateUtil;
import com.me2me.common.utils.Lists;
import com.me2me.core.dao.BaseJdbcDao;
import com.me2me.user.model.UserHobby;
import com.me2me.user.rule.CoinRule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/5/10.
 */
@Repository
public class UserInitJdbcDao extends BaseJdbcDao {
	  @Autowired
	    private JdbcTemplate jdbc;

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
    	String sql = "select uid from user where user_name BETWEEN '18900000200' and '18900000384'";
    	List<Map<String, Object>> uidList = super.query(sql);
    	List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
    	if(null != uidList && uidList.size() > 0){
    		Random random = new Random();
    		for(int i=0;i<limit;i++){
    			if(uidList.size() < 1){
    				break;
    			}
    			int idx = random.nextInt(uidList.size());
    			result.add(uidList.get(idx));
    			uidList.remove(idx);
    		}
    	}
    	return result;
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

    public List<Map<String, Object>> searchUserProfilesByPage(String nickName, String mobile, int vLv, int status, String startTime, String endTime, long meCode, int start, int pageSize){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select p.avatar,u.create_time,p.gender,p.mobile,p.nick_name,p.third_part_bind,p.uid,p.v_lv,p.birthday,u.disable_user,n.me_number,p.level ");
    	sb.append("from user u,user_profile p,user_no n ");
    	sb.append("where u.uid=p.uid and u.uid=n.uid ");
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
    	if(meCode > 0){
    		sb.append("and n.me_number=").append(meCode).append(" ");
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
    
    public List<Map<String, Object>> getUserFollowInfoPage(String nickName, long uid, int start, int pageSize){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select p.*,case p.name_group when '#' then 'Z#' else p.name_group end as ng");
    	sb.append(" from user_follow f, user_profile p where f.target_uid=p.uid");
    	sb.append(" and f.target_uid>0 and f.source_uid=").append(uid);
    	if(!StringUtils.isEmpty(nickName)){
    		sb.append(" and p.nick_name like '%").append(nickName).append("%'");
    	}
    	sb.append(" order by ng asc,p.nick_name ASC");
    	sb.append(" limit ").append(start).append(",").append(pageSize);
    	
    	return super.query(sb.toString());
    }
    
    public int countUserFollowInfo(String nickName, long uid){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select count(1) as count from user_follow f,user_profile p");
    	sb.append(" where f.target_uid>0 and f.target_uid=p.uid and f.source_uid=").append(uid);
    	if(!StringUtils.isEmpty(nickName)){
    		sb.append(" and p.nick_name like '%").append(nickName).append("%'");
    	}
    	return super.count(sb.toString());
    }
    
    public void batchUserFollowInsertIntoDB(long sourceUid, List<Long> targetUid){
    	if(null == targetUid || targetUid.size() == 0){
    		return;
    	}
    	StringBuilder sb = new StringBuilder();
    	sb.append("insert into user_follow(source_uid,target_uid) values ");
    	for(int i=0;i<targetUid.size();i++){
    		if(i > 0){
    			sb.append(",");
    		}
    		sb.append("(").append(sourceUid).append(",").append(targetUid.get(i)).append(")");
    	}
    	super.execute(sb.toString());
    }
    
    public List<String> getAllUserMobilesInApp(){
		String sql = "select DISTINCT t.mobile from user_profile t where t.third_part_bind like '%mobile%' order by t.mobile";
		List<Map<String,Object>> list = super.query(sql);
		if(null != list && list.size() > 0){
			List<String> result = new ArrayList<String>();
			String m = null;
			for(Map<String,Object> map : list){
				m = (String)map.get("mobile");
				if(!StringUtils.isEmpty(m)){
					result.add(m);
				}
			}
			return result;
		}
		return null;
	}
    
    public void updateUserProfileParam4Number(long uid, int intParam, String paramName){
    	StringBuilder sb = new StringBuilder();
    	sb.append("update user_profile set ").append(paramName);
    	sb.append("=").append(intParam).append(",update_time=now() where uid=");
    	sb.append(uid);
    	
    	super.execute(sb.toString());
    }
    public void updateUserProfileParam4String(long uid, String strParam, String paramName){
    	String sql ="update user_profile set "+paramName+"=? ,update_time=now() where uid=?";
    	
    	jdbc.update(sql, new Object[]{strParam,uid});
    }
    
    public void batchInsertIntoUserMobileList(long uid, List<String> mobileList){
    	if(null == mobileList || mobileList.size() == 0){
    		return;
    	}
    	StringBuilder sb = new StringBuilder();
    	sb.append("insert into user_mobile_list(uid, mobile) values ");
    	for(int i=0;i<mobileList.size();i++){
    		if(i>0){
    			sb.append(",");
    		}
    		sb.append("(").append(uid).append(",'").append(mobileList.get(i));
    		sb.append("')");
    	}
    	super.execute(sb.toString());
    }
    /**
     * 保存用户
     * @author zhangjiwei
     * @date May 23, 2017
     * @param uid
     */
	public void saveMBTIShareResult(long uid) {
		String sql = "update user_mbti_history set shared=1 where uid=? order by create_time desc limit 1";
		jdbc.update(sql, uid);
	}
	
    public List<Map<String, Object>> getSummaryEmotionInfo(long uid,String dateStart,String dateEnd) {
        String sql = "SELECT emotionId,COUNT(1) AS countNum,ROUND(AVG(happyValue),0) AS happyValue,ROUND(AVG(freeValue),0) AS freeValue FROM emotion_record "
        		+ "   where create_time >= '"+dateStart+"' and create_time <= '"+dateEnd+"' and uid = "+uid 
        		+ "   GROUP BY emotionId";
        return super.query(sql);
    }
    public List<Map<String, Object>> getEmotionRecordByStartAndEnd(String dateStart,String dateEnd) {
        String sql = "SELECT uid FROM emotion_record WHERE create_time >='"+dateStart+"' AND create_time<='"+dateEnd+"' GROUP BY uid ";
        return super.query(sql);
    }

	public void modifyUserCoin(long uid , int coin) {
		String sql = "UPDATE user_profile SET available_coin = ? WHERE uid = ? ";
		jdbc.update(sql,coin,uid);
	}
	
	public void incrUserCoin(long uid, int coin){
		if(coin <= 0){
			return;
		}
		String sql = "update user_profile set available_coin=available_coin+? where uid=?";
		jdbc.update(sql, coin, uid);
	}

	public void modifyUserLevel(long uid , int level) {
		String sql = "UPDATE user_profile SET `level` = ? WHERE uid = ? ";
		jdbc.update(sql,level,uid);
	}

	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


	// 获取今日米汤币累计值
	public int getDayCoins(long uid) {
		String sql = "select IFNULL(sum(coin),0) as count from rule_log where uid = ? and DATE_FORMAT(create_time,'%Y-%m-%d') = ?";
		String day = sdf.format(new Date());
		List list = super.query(sql,uid,day);
		if(list==null||list.isEmpty()){
			return 0;
		}else {
			return Integer.valueOf(Lists.getSingle(super.query(sql, uid, day)).get("count").toString());
		}
	}
	public int getDayCoins2(long uid) {
		String sql = "select IFNULL(sum(stealed_coins),0) as count from user_steal_log where uid = ? and DATE_FORMAT(create_time,'%Y-%m-%d') = ?";
		String day = sdf.format(new Date());
		List list = super.query(sql,uid,day);
		if(list==null||list.isEmpty()){
			return 0;
		}else {
			return Integer.valueOf(Lists.getSingle(super.query(sql, uid, day)).get("count").toString());
		}
	}

	public boolean isNotExistsRuleLogByDay(int code, long uid) {
		String sql = "SELECT * from rule_log where rule_code = ? and uid = ? and DATE_FORMAT(create_time,'%Y-%m-%d') = ?";
		String day = sdf.format(new Date());
		List<Map<String,Object>> list = super.query(sql,code,uid,day);
		return  (list!=null&&list.size()>0) ? Boolean.FALSE : Boolean.TRUE;
	}

	public boolean isNotExistsRuleLogByDay(int code, long uid, long ext) {
		String sql = "SELECT * from rule_log where rule_code = ? and uid = ? and ext = ?";
		List<Map<String,Object>> list = super.query(sql,code,uid,ext);
		return  (list!=null&&list.size()>0) ? Boolean.FALSE : Boolean.TRUE;
	}

	public void writeRuleLog(long uid, CoinRule rule){
		String sql = "insert into rule_log (uid,rule_code,rule_name,coin,ext) values(?,?,?,?,?)";
		jdbc.update(sql,uid,rule.getCode(),rule.getName(),rule.getPoint(),rule.getExt());
	}

	public List<Map<String, Object>> getCanStealTopicId(long uid) {
		String dateStr = DateUtil.date2string(new Date(), "yyyy-MM-dd") + " 00:00:00";
		StringBuilder sb = new StringBuilder();
		sb.append("select t.id as topic_id from topic t,topic_data d where t.id=d.topic_id");
		sb.append(" and t.uid!=").append(uid).append(" and d.steal_price>0");
		sb.append(" and t.id not in (SELECT topic_id FROM user_steal_log s WHERE s.uid=").append(uid);
		sb.append(" and s.create_time>='").append(dateStr).append("')");
		
		return super.query(sb.toString());
	}

	public List<Map<String, Object>> getCanSpeakTopicId(long uid) {
		String sql = "SELECT id FROM topic WHERE uid != ?  ORDER BY update_time DESC LIMIT 500";
		return super.query(sql,uid);
	}


	public void redBagInsert(long uid , int coin){
			String sql = "insert into rule_log (uid,rule_code,rule_name,coin,ext) values(?,?,?,?,?)";
			jdbc.update(sql,uid,-1,"红包",coin,uid);
	}

	public List<Map<String, Object>> getRedBag(long uid) {
		String sql = "SELECT * from rule_log where uid = ?";
		return super.query(sql,uid);
	}


	public void batchInsertUserLikeTags(Long uid, Set<Long> userHobbyTags) {
		if(null == userHobbyTags || userHobbyTags.size() == 0){
			return;
		}
		StringBuilder delSql = new StringBuilder();
		delSql.append("delete from user_dislike where uid=? and is_like=1 and type=2 and data in (");
		int i=0;
		for(Long tag:userHobbyTags){
			if(i>0){
				delSql.append(",");
			}
			delSql.append(tag.toString());
			i++;
		}
		delSql.append(")");
		jdbc.update(delSql.toString(),uid);
		String sql ="insert into user_dislike (uid,data,is_like,type,create_time) values(?,?,1,2,now())";
		List<Object[]> batchArgs = new ArrayList<>();
		for(Long tagid:userHobbyTags){
			batchArgs.add(new Object[]{uid,tagid});
		}
		jdbc.batchUpdate(sql, batchArgs );
	}
	public List<Long> getTagFromUserHobby(long userHobby) {
		List<Map<String,Object>> dataList =jdbc.queryForList("select id from topic_tag where find_in_set(?,user_hobby_ids)",userHobby);
		List<Long> ret = new ArrayList<>();
		for(Map<String,Object> data:dataList){
			ret.add(((Number) data.get("id")).longValue());
		}
		return ret;
	}


}
