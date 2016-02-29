package com.me2me.user.dao;

import com.me2me.common.web.Specification;
import com.me2me.user.mapper.UserMapper;
import com.me2me.user.model.User;
import com.me2me.user.model.UserExample;
import com.me2me.user.model.UserHobby;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/26.
 */
@Repository
public class UserMybatisDao {

    @Autowired
    private UserMapper userMapper;


    /**
     * 保存用户注册信息
     * @param user
     */
    public void createUser(User user){
        userMapper.insertSelective(user);
    }

    /**
     * 根据用户账号获取用户信息
     * @param username
     */
    public User getUserByUserName(String username){
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andUserNameEqualTo(username);
        criteria.andStatusEqualTo(Specification.UserStatus.NORMAL.index);
        List<User> users = userMapper.selectByExample(example);
        return (users!=null&&users.size()>0)? users.get(0):null;
    }

    public void modifyUser(User user){
        userMapper.updateByPrimaryKey(user);

    }


    public void modifyUserHobby(UserHobby userHobby){

    }
}
