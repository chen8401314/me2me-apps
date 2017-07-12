package com.me2me.content.dao;

import com.me2me.common.utils.Lists;
import com.me2me.core.dao.BaseJdbcDao;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by pc329 on 2017/3/20.
 */
@Repository
public class BillBoardJdbcDao extends BaseJdbcDao {


    public List<Map<String,Object>> loadBillBoard(){
        String sql = "SELECT * from billboard INNER JOIN billboard_details on billboard.id = billboard_details.bid";
        return super.query(sql);
    }

    public List<Map<String,Object>> loadBillBoardBySourceId(long sourceId){
        String sql = "select * from billboard_relation where source_id = ?";
        return super.query(sql,sourceId);
    }

    /**
     * 查询王国
     * @param id
     * @return
     */
    public Map<String,Object> getTopicById(long id){
        String sql = "select * from topic where id = ?";
        return Lists.getSingle(super.query(sql,id));
    }

    public boolean isSubscribe(long topicId,long uid){
        String sql = "select * from live_favorite where topic_id = ? and uid = ?";
        List<Map<String,Object>> data =  super.query(sql,topicId,uid);
        if(null!=data&&data.size()>0){
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }


    public List<Map<String,Object>> getAllContent(){
        String sql = "select * from content order by update_time";
        List<Map<String,Object>> list = super.query(sql);
        return list;
    }

    public void setUpdateId(long updateId,long id){
        String updateSQL = "update content set update_id = ? where id = ?";
        super.update(updateSQL,updateId,id);
    }
}
