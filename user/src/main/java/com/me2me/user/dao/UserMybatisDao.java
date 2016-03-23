package com.me2me.user.dao;

import com.me2me.common.web.Specification;
import com.me2me.user.dto.BasicDataDto;
import com.me2me.user.dto.ModifyUserProfileDto;
import com.me2me.user.mapper.*;
import com.me2me.user.model.*;
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

    @Autowired
    private UserHobbyMapper userHobbyMapper;

    @Autowired
    private DictionaryMapper dictionaryMapper;

    @Autowired
    private UserProfileMapper userProfileMapper;

    @Autowired
    private UserTokenMapper userTokenMapper;

    /**
     * 保存用户注册信息
     * @param user
     */
    public void createUser(User user){
        userMapper.insertSelective(user);
    }

    public void createUserProfile(UserProfile userProfile){
        userProfileMapper.insertSelective(userProfile);
    }

    public void createUserToken(UserToken userToken){
        userTokenMapper.insertSelective(userToken);
    }

    public UserToken getUserTokenByUid(long uid){
        UserTokenExample example = new UserTokenExample();
        UserTokenExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        List<UserToken> lists = userTokenMapper.selectByExample(example);
        return (lists != null && lists.size() > 0) ? lists.get(0) : null;
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


    public void deleteUserHobby(UserHobby userHobby){
        UserHobbyExample example = new UserHobbyExample();
        UserHobbyExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(userHobby.getUid());
        userHobbyMapper.deleteByExample(example);
    }

    public void createUserHobby(UserHobby userHobby){
        userHobbyMapper.insertSelective(userHobby);
    }

    public List<Dictionary> getDictionary(BasicDataDto basicDataDto){
        DictionaryExample example = new DictionaryExample();
        DictionaryExample.Criteria criteria = example.createCriteria();
        criteria.andTidEqualTo(basicDataDto.getType());
        return dictionaryMapper.selectByExample(example);
    }
    public void modifyUserAvatar(ModifyUserProfileDto modifyUserProfileDto){
        UserProfileExample example = new UserProfileExample();
        UserProfileExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(modifyUserProfileDto.getUid());
        List<UserProfile> userProfileList = userProfileMapper.selectByExample(example);
        if(userProfileList != null && userProfileList.size() > 0){
            UserProfile userProfile = userProfileList.get(0);
            userProfile.setAvatar(modifyUserProfileDto.getAvatar());
            userProfileMapper.updateByPrimaryKey(userProfile);
        }
    }

    public void modifyNickName(ModifyUserProfileDto modifyUserProfileDto){
        UserProfileExample example = new UserProfileExample();
        UserProfileExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(modifyUserProfileDto.getUid());
        List<UserProfile> userProfileList = userProfileMapper.selectByExample(example);
        if(userProfileList != null && userProfileList.size() > 0){
            UserProfile userProfile = userProfileList.get(0);
            userProfile.setAvatar(modifyUserProfileDto.getNickName());
            userProfileMapper.updateByPrimaryKey(userProfile);
        }
    }

    public void modifyUserProfile(ModifyUserProfileDto modifyUserProfileDto){
        UserProfileExample example = new UserProfileExample();
        UserProfileExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(modifyUserProfileDto.getUid());
        List<UserProfile> userProfileList = userProfileMapper.selectByExample(example);
        if(userProfileList != null && userProfileList.size() > 0){
            UserProfile userProfile = userProfileList.get(0);
            userProfile.setMobile(modifyUserProfileDto.getUserName());
            userProfile.setBearStatus(modifyUserProfileDto.getBearStatus());
            userProfile.setMarriageStatus(modifyUserProfileDto.getMarriageStatus());
            userProfile.setGender(modifyUserProfileDto.getGender());
            userProfile.setIndustry(modifyUserProfileDto.getIndustry());
            userProfile.setSocialClass(modifyUserProfileDto.getSocialClass());
            userProfile.setYearsId(modifyUserProfileDto.getYearsId());
            userProfile.setStarId(modifyUserProfileDto.getStartId());
            userProfileMapper.updateByPrimaryKey(userProfile);
        }
    }


    public UserProfile getUserProfileByUid(long uid) {
        UserProfileExample example = new UserProfileExample();
        UserProfileExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        List<UserProfile> lists = userProfileMapper.selectByExample(example);
        return (lists != null && lists.size() > 0) ? lists.get(0) : null;
    }
}
