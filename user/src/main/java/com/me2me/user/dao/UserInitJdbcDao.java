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

    public int getUGCount(long uid){
        String sql = "select count(*) as count from content where uid = ? and status <> 1 and type in (0,1,8,9)" ;
        return super.count(sql,uid);
    }

    public List<Map<String, Object>> getPhoto(long sinceId) {
        String sql = "select id , image as imageUrl,cid as title from content_image where id < "+ sinceId +" order by id desc limit 100 ";
        return super.query(sql);
    }

}
