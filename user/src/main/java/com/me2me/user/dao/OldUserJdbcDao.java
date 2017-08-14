package com.me2me.user.dao;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.me2me.common.Constant;
import com.me2me.common.security.SecurityUtils;
import com.me2me.common.web.Specification;
import com.me2me.user.dto.UserAccountBindStatusDto;
import com.me2me.user.model.User;
import com.me2me.user.model.UserProfile;
import com.me2me.user.model.UserToken;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/5/10
 * Time :11:51
 */
@Repository
@Slf4j
public class OldUserJdbcDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserMybatisDao userMybatisDao;


    //登录
    public void moveOldUser2Apps(String mobile, String encrypt){
        log.info("moveOldUser2Apps start ...");
        //判断new_user有没有
        User u = userMybatisDao.getUserByUserName(mobile);
        if(u == null){
            String sql = "select Account_UserId,Account_LoginName, user_NickName,user_Gender from old_user_view where Account_LoginName = ? ";
            List<Map<String,Object>> list = jdbcTemplate.queryForList(sql,mobile);
            // 老用户里面有
            log.info( "mobile :" +mobile +" is old user");
            if(list != null &&list.size() > 0) {
                Map<String, Object> map = list.get(0);
                User user = new User();
                user.setUserName(mobile);
                String salt = SecurityUtils.getMask();
                user.setEncrypt(SecurityUtils.md5(encrypt, salt));
                user.setSalt(salt);
                user.setCreateTime(new Date());
                user.setUpdateTime(new Date());
                user.setStatus(Specification.UserStatus.NORMAL.index);
                userMybatisDao.createUser(user);
                log.info("user move success");
                UserProfile userProfile = new UserProfile();
                userProfile.setUid(user.getUid());
                userProfile.setAvatar(Constant.DEFAULT_AVATAR);
                userProfile.setMobile(mobile);
                String nickName = map.get("user_NickName")== null ? "metome" :map.get("user_NickName").toString();
                String user_Gender = map.get("user_Gender")== null ? "10" : map.get("user_Gender").toString();
                List<UserProfile> userProfileList = userMybatisDao.getByNickName(nickName);
                if (userProfileList != null && userProfileList.size() > 0) {
                    userProfile.setNickName(nickName + 1);
                }else {
                    userProfile.setNickName(nickName);
                }
                if (user_Gender.equals("10")) {
                    userProfile.setGender(1);
                } else {
                    userProfile.setGender(0);
                }
                //生日默认给一个不可能的值
                userProfile.setBirthday("1800-1-1");
                // 添加手机绑定
                List<UserAccountBindStatusDto> array = Lists.newArrayList();
                array.add(new UserAccountBindStatusDto(Specification.ThirdPartType.MOBILE.index,Specification.ThirdPartType.MOBILE.name,1));
                String mobileBind = JSON.toJSONString(array);
                userProfile.setThirdPartBind(mobileBind);

                userMybatisDao.createUserProfile(userProfile);
                log.info("userProfile move success");
                // 保存用户token信息
                UserToken userToken = new UserToken();
                userToken.setUid(user.getUid());
                userToken.setToken(SecurityUtils.getToken());
                userMybatisDao.createUserToken(userToken);
                log.info("userToken move success");
            }
        }
        log.info("moveOldUser2Apps end ...");
    }


    public int getUserInternalStatus(long uid, long owner) {
        String caseSql = " select internal_status from sns_circle  where uid = " + uid + " and owner = " + owner;
        List<Map<String,Object>> list = jdbcTemplate.queryForList(caseSql);
        if(list != null &&list.size() > 0) {
            Map<String, Object> map = list.get(0);
            Object result = map.get("internal_status");
            return Integer.parseInt(result == null ? "0" : result.toString());
        }
        return 0;
    }
    
    public List<Long> getAllUids(){
    	String sql = "select DISTINCT t.uid from user_profile t";
    	List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
    	if(null != list && list.size() > 0){
    		List<Long> result = new ArrayList<Long>();
    		for(Map<String,Object> m : list){
    			result.add((Long)m.get("uid"));
    		}
    		return result;
    	}
    	return null;
    }
}
