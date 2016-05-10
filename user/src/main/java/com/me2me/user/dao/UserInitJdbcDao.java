package com.me2me.user.dao;

import com.me2me.core.dao.BaseJdbcDao;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/5/10.
 */
@Repository
public class UserInitJdbcDao extends BaseJdbcDao {

    public void batchInsertMeNumber(List<Integer> values){
        String sql = "insert into user_no (me_number) values ";
        StringBuilder sb = new StringBuilder();
        for(Integer value : values){
            if(value!=values.get(values.size()-1)) {
                sb.append("(").append(value).append(")").append(",");
            }else{
                sb.append("(").append(value).append(")");
            }
        }
        this.execute(sql+sb.toString());
    }

}
