package com.me2me.user.dao;

import com.me2me.common.web.Response;
import com.me2me.common.web.Specification;
import com.me2me.sms.dto.PushLogDto;
import com.me2me.user.dto.*;
import com.me2me.user.mapper.*;
import com.me2me.user.model.*;
import com.me2me.user.model.ApplicationSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    private DictionaryTypeMapper dictionaryTypeMapper;

    @Autowired
    private UserProfileMapper userProfileMapper;

    @Autowired
    private UserTokenMapper userTokenMapper;

    @Autowired
    private UserTagsMapper userTagsMapper;

    @Autowired
    private UserTagsRecordMapper userTagsRecordMapper;

    @Autowired
    private UserTagsDetailsMapper userTagsDetailsMapper;

    @Autowired
    private UserNoticeMapper userNoticeMapper;

    @Autowired
    private UserTipsMapper userTipsMapper;

    @Autowired
    private UserReportMapper userReportMapper;

    @Autowired
    private UserFollowMapper userFollowMapper;

    @Autowired
    private ApplicationSecurityMapper applicationSecurityMapper;

    @Autowired
    private UserNoMapper userNoMapper;

    @Autowired
    private VersionControlMapper controlMapper;

    @Autowired
    private UserDeviceMapper userDeviceMapper;

    @Autowired
    private XingePushLogMapper xingePushLogMapper;

    @Autowired
    private JpushTokenMapper jpushTokenMapper;

    @Autowired
    private ThirdPartUserMapper thirdPartUserMapper;

    @Autowired
    private OpenDeviceCountMapper openDeviceCountMapper;

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    @Autowired
    private UserGagMapper userGagMapper;

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

    public UserToken getUserTokenByUid(long uid ,String token){
        UserTokenExample example = new UserTokenExample();
        UserTokenExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andTokenEqualTo(token);
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


    public User getUserByUid(long uid){
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andStatusEqualTo(Specification.UserStatus.NORMAL.index);
        List<User> users = userMapper.selectByExample(example);
        return (users!=null&&users.size()>0)? users.get(0):null;
    }

    public User getUserByUidAndTime(long uid , Date startDate , Date endDate){
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andCreateTimeGreaterThan(startDate);
        criteria.andCreateTimeLessThan(endDate);
        criteria.andStatusEqualTo(Specification.UserStatus.NORMAL.index);
        List<User> users = userMapper.selectByExample(example);
        return (users!=null&&users.size()>0)? users.get(0):null;
    }

    public void modifyUser(User user){
        userMapper.updateByPrimaryKeySelective(user);

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
        example.setOrderByClause(" sort asc ");
        return dictionaryMapper.selectByExample(example);
    }

    public DictionaryType getDictionaryType(BasicDataDto basicDataDto) {
        DictionaryTypeExample example = new DictionaryTypeExample();
        DictionaryTypeExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(basicDataDto.getType());
        List<DictionaryType> list = dictionaryTypeMapper.selectByExample(example);
        return (list != null && list.size() > 0) ? list.get(0) : null;
    }

    public void modifyUserProfile(UserProfile userProfile){
        userProfileMapper.updateByPrimaryKeySelective(userProfile);
    }

    public UserProfile getUserProfileByUid(long uid) {
        UserProfileExample example = new UserProfileExample();
        UserProfileExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        List<UserProfile> lists = userProfileMapper.selectByExample(example);
        return (lists != null && lists.size() > 0) ? lists.get(0) : null;
    }
    public List<UserProfile> getUserProfilesByUids(List<Long> uids) {
        UserProfileExample example = new UserProfileExample();
        UserProfileExample.Criteria criteria = example.createCriteria();
        criteria.andUidIn(uids);
        return userProfileMapper.selectByExample(example);
    }

    /**
     * 根据标签内容查找相应的标签
     * @param tag 标签内容
     * @return 标签
     */
    public UserTags getUserTag(String tag){
        UserTagsExample userTagsExample = new UserTagsExample();
        UserTagsExample.Criteria criteria = userTagsExample.createCriteria();
        criteria.andTagEqualTo(tag);
        List<UserTags> tagsList = userTagsMapper.selectByExample(userTagsExample);
        return (tagsList !=null &&tagsList.size() > 0) ? tagsList.get(0) : null;
    }

    /**
     * 更新用户标签详情
     */
    public void updateUserTagDetail(UserTagsDetails userTagsDetails){
        userTagsDetailsMapper.updateByPrimaryKeySelective(userTagsDetails);
    }

    public UserTagsDetails getUserTagByTidAndUid(long tid,long uid){
        UserTagsDetailsExample userTagsDetailsExample = new UserTagsDetailsExample();
        UserTagsDetailsExample.Criteria criteria1 = userTagsDetailsExample.createCriteria();
        criteria1.andUidEqualTo(uid).andTidEqualTo(tid);
        List<UserTagsDetails> lists = userTagsDetailsMapper.selectByExample(userTagsDetailsExample);
        return (lists != null && lists.size() > 0) ? lists.get(0) : null;
    }

    /**
     * 贴标签
     * @param tag
     */
    public long saveUserTag(String tag){
        UserTags userTags = new UserTags();
        userTags.setTag(tag);
        return userTagsMapper.insertSelective(userTags);
    }

    /**
     * 保存标签详情
      * @param tagId
     * @param pasteTagDto
     */
    public void saveUserTagDetail(Long tagId,PasteTagDto pasteTagDto){
        UserTagsDetails details = new UserTagsDetails();
        details.setTid(tagId);
        details.setUid(pasteTagDto.getFromUid());
        details.setFrequency(1L);
        userTagsDetailsMapper.insertSelective(details);
    }

    /**
     * 保存用户标签记录
     * @param fromUserId 贴标签的用户Id
     * @param toUserId 被贴标签的用户Id
     */
    public void saveUserTagRecord(Long fromUserId,Long toUserId){
        UserTagsRecord record = new UserTagsRecord();
        record.setFromUid(fromUserId);
        record.setToUid(toUserId);
        userTagsRecordMapper.insertSelective(record);
    }

    public List<UserNotice> userNotice(UserNoticeDto userNoticeDto){
        UserNoticeExample example = new UserNoticeExample();
        UserNoticeExample.Criteria criteria = example.createCriteria();
        criteria.andToUidEqualTo(userNoticeDto.getUid());
        criteria.andIdLessThan(userNoticeDto.getSinceId());
        example.setOrderByClause("id desc limit 10 ");
        return  userNoticeMapper.selectByExample(example);
    }
    public void createUserNotice(UserNotice userNotice){
        userNoticeMapper.insertSelective(userNotice);
    }

    public List<UserTips> getUserTips(long uid){
        UserTipsExample example = new UserTipsExample();
        UserTipsExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        return userTipsMapper.selectByExample(example);
    }

    public void modifyUserTips(UserTips userTips){
        userTipsMapper.updateByPrimaryKeySelective(userTips);
    }

    public UserTips getUserTips(UserTips userTips){
        UserTipsExample example = new UserTipsExample();
        UserTipsExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(userTips.getUid());
        criteria.andTypeEqualTo(userTips.getType());
        List<UserTips>  list = userTipsMapper.selectByExample(example);
        return  (list != null && list.size() >0) ? list.get(0) : null;
    }

    public void createUserTips(UserTips userTips){
        userTipsMapper.insertSelective(userTips);
    }

    public void createUserReport(UserReport userReport){
        userReportMapper.insertSelective(userReport);
    }

    public List<UserTagsDetails> getUserTags(long uid){
        UserTagsDetailsExample example = new UserTagsDetailsExample();
        UserTagsDetailsExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        return userTagsDetailsMapper.selectByExample(example);
    }

    public UserTags getUserTagsById(long id){
        return userTagsMapper.selectByPrimaryKey(id);
    }

    public void createUserTagsRecord(UserTagsRecord userTagsRecord){
        userTagsRecordMapper.insertSelective(userTagsRecord);
    }
    public UserTagsRecord getUserTagsRecord(UserTagsRecord userTagsRecord){
        UserTagsRecordExample example = new UserTagsRecordExample();
        UserTagsRecordExample.Criteria criteria = example.createCriteria();
        criteria.andFromUidEqualTo(userTagsRecord.getFromUid());
        criteria.andToUidEqualTo(userTagsRecord.getToUid());
        criteria.andTagIdEqualTo(userTagsRecord.getTagId());
        List<UserTagsRecord> userTagsRecordList = userTagsRecordMapper.selectByExample(example);
        return  (userTagsRecordList != null && userTagsRecordList.size() > 0) ?userTagsRecordList.get(0) : null;
    }

    public void deleteUserTagsRecord(UserTagsRecord userTagsRecord){
        userTagsRecordMapper.deleteByPrimaryKey(userTagsRecord.getId());
    }

    public void createFollow(UserFollow userFollow){
        userFollowMapper.insertSelective(userFollow);
    }

    public void deleteFollow(long id){
        userFollowMapper.deleteByPrimaryKey(id);
    }

    public UserFollow getUserFollow(long sourceUid,long targetUid){
        UserFollowExample example = new UserFollowExample();
        UserFollowExample.Criteria criteria =  example.createCriteria();
        criteria.andSourceUidEqualTo(sourceUid);
        criteria.andTargetUidEqualTo(targetUid);
        List<UserFollow> list = userFollowMapper.selectByExample(example);
        return list!=null&&list.size()>0 ? list.get(0) : null;
    }

    public List<UserFansDto> getFans(FansParamsDto fansParamsDto){
        return userFollowMapper.getFans(fansParamsDto);
    }

    public List<UserFollowDto> getFollows(FollowParamsDto followParamsDto){
        return userFollowMapper.getFollows(followParamsDto);
    }

    public List<UserProfile> search(String keyword,int page,int pageSize){
        UserProfileExample example = new UserProfileExample();
        UserProfileExample.Criteria criteria =  example.createCriteria();
        criteria.andNickNameLike("%"+keyword+"%");
        example.setOrderByClause("nick_name limit "+((page-1)*pageSize)+", " + pageSize);
        return userProfileMapper.selectByExample(example);
    }

    public int total(String keyword){
        UserProfileExample example = new UserProfileExample();
        UserProfileExample.Criteria criteria =  example.createCriteria();
        criteria.andNickNameLike("%"+keyword+"%");
        return userProfileMapper.countByExample(example);
    }

    public List<UserProfile> assistant(String keyword){
        UserProfileExample example = new UserProfileExample();
        UserProfileExample.Criteria criteria =  example.createCriteria();
        criteria.andNickNameLike(keyword+"%");
        example.setOrderByClause("nick_name limit 10");
        return userProfileMapper.selectByExample(example);
    }

    public List<UserProfile> getByNickName(String nickName){
        UserProfileExample example = new UserProfileExample();
        UserProfileExample.Criteria criteria =  example.createCriteria();
        criteria.andNickNameEqualTo(nickName);
        return userProfileMapper.selectByExample(example);
    }

    public List<UserFollow> getUserFollow(long uid){
        UserFollowExample example = new UserFollowExample();
        UserFollowExample.Criteria criteria =  example.createCriteria();
        criteria.andSourceUidEqualTo(uid);
        return userFollowMapper.selectByExample(example);
    }

    public List<UserFollow> getUserFans(long uid){
        UserFollowExample example = new UserFollowExample();
        UserFollowExample.Criteria criteria =  example.createCriteria();
        criteria.andTargetUidEqualTo(uid);
        return userFollowMapper.selectByExample(example);
    }

    public int getUserFollowCount(long uid){
        UserFollowExample example = new UserFollowExample();
        UserFollowExample.Criteria criteria =  example.createCriteria();
        criteria.andSourceUidEqualTo(uid);
        return userFollowMapper.countByExample(example);
    }

    public int getUserFansCount(long uid){
        UserFollowExample example = new UserFollowExample();
        UserFollowExample.Criteria criteria =  example.createCriteria();
        criteria.andTargetUidEqualTo(uid);
        return userFollowMapper.countByExample(example);
    }

    public List<UserHobby> getHobby(long uid){
        UserHobbyExample example = new UserHobbyExample();
        UserHobbyExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        return userHobbyMapper.selectByExample(example);
    }

    public ApplicationSecurity getApplicationSecurityByAppId(String appId){
        ApplicationSecurityExample example = new ApplicationSecurityExample();
        ApplicationSecurityExample.Criteria criteria = example.createCriteria();
        criteria.andAppIdEqualTo(appId);
        List<ApplicationSecurity> list = applicationSecurityMapper.selectByExample(example);
        return list!=null&&list.size()>0?list.get(0):null;
    }

    public UserNo getUserNoByUid(long uid){
        return userNoMapper.selectByPrimaryKey(uid);
    }

    public VersionControl getNewestVersion(int platform){
        VersionControlExample example = new VersionControlExample();
        VersionControlExample.Criteria criteria = example.createCriteria();
        criteria.andPlatformEqualTo(platform);
        example.setOrderByClause(" update_time desc ");
        List<VersionControl> list =  controlMapper.selectByExample(example);
        return  (list != null &&list.size() > 0) ? list.get(0) : null;
    }

    public VersionControl getVersion(String version,int platform){
        VersionControlExample example = new VersionControlExample();
        VersionControlExample.Criteria criteria = example.createCriteria();
        criteria.andVersionEqualTo(version);
        criteria.andPlatformEqualTo(platform);
        List<VersionControl> list =  controlMapper.selectByExample(example);
        return  (list != null &&list.size() > 0) ? list.get(0) : null;

    }

    public void updateVersion(VersionDto versionDto){
        VersionControl versionControl = new VersionControl();
        versionControl.setUpdateDescription(versionDto.getUpdateDescription());
        versionControl.setPlatform(versionDto.getPlatform());
        versionControl.setUpdateUrl(versionDto.getUpdateUrl());
        versionControl.setVersion(versionDto.getVersion());
        controlMapper.insertSelective(versionControl);

    }

    public Dictionary getDictionaryById(long id){
        return dictionaryMapper.selectByPrimaryKey(id);
    }

    public UserNotice getUserNotice(UserNotice userNotice){
        UserNoticeExample example = new UserNoticeExample();
        UserNoticeExample.Criteria criteria = example.createCriteria();
        criteria.andNoticeTypeEqualTo(userNotice.getNoticeType());
        criteria.andFromUidEqualTo(userNotice.getFromUid());
        criteria.andToUidEqualTo(userNotice.getToUid());
        criteria.andCidEqualTo(userNotice.getCid());
        List<UserNotice> userNotices = userNoticeMapper.selectByExample(example);
        return (userNotices != null && userNotices.size() > 0) ? userNotices.get(0) : null;
    }

    public UserNotice getUserNoticeById(long noticeId){
        return userNoticeMapper.selectByPrimaryKey(noticeId);
    }

    public void updateUserNoticePushStatus(UserNotice userNotice){
        userNoticeMapper.updateByPrimaryKeySelective(userNotice);
    }

    public UserDevice getUserDevice(long uid){
        UserDeviceExample example = new UserDeviceExample();
        UserDeviceExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        List<UserDevice> list = userDeviceMapper.selectByExample(example);
        return (list != null && list.size() > 0) ? list.get(0) : null;
    }
    public void updateUserDevice(UserDevice userDevice){
        UserDevice device = getUserDevice(userDevice.getUid());
       if(device != null){
           device.setDeviceNo(userDevice.getDeviceNo());
           device.setOs(userDevice.getOs());
           device.setPlatform(userDevice.getPlatform());
           userDeviceMapper.updateByPrimaryKeySelective(device);
       }else {
           if(!StringUtils.isEmpty(userDevice.getDeviceNo())) {
               userDeviceMapper.insertSelective(userDevice);
           }
       }
    }

    public void createPushLog(PushLogDto pushLogDto){
        XingePushLog pushLog = new XingePushLog();
        pushLog.setContent(pushLogDto.getContent());
        pushLog.setMessageType(pushLogDto.getMessageType());
        pushLog.setRetCode(pushLogDto.getRetCode());
        xingePushLogMapper.insertSelective(pushLog);
    }

    public void logout(long uid){
        UserDeviceExample example = new UserDeviceExample();
        UserDeviceExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        userDeviceMapper.deleteByExample(example);
    }

    public List<User> getRobots(int limit){
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andUserNameLike("18900000%");
        example.setOrderByClause(" uid desc rand() limit "+limit);
        return userMapper.selectByExample(example);
    }

    public int getRefereeCount(long uid){
        UserProfileExample example = new UserProfileExample();
        UserProfileExample.Criteria criteria = example.createCriteria();
        criteria.andRefereeUidEqualTo(uid);
        criteria.andIsActivateEqualTo(Specification.UserActivate.ACTIVATED.index);
        return userProfileMapper.countByExample(example);
    }

    public int getFansCount(long uid){
        UserFollowExample example = new UserFollowExample();
        UserFollowExample.Criteria criteria = example.createCriteria();
        criteria.andTargetUidEqualTo(uid);
        return userFollowMapper.countByExample(example);
    }

    public List<UserNotice> getUserNotice(long uid){
        UserNoticeExample example = new UserNoticeExample();
        UserNoticeExample.Criteria criteria = example.createCriteria();
        criteria.andToUidEqualTo(uid);
        criteria.andPushStatusEqualTo(Specification.PushStatus.UN_PUSHED.index);
        return  userNoticeMapper.selectByExample(example);
    }

    public List<UserFansDto> getFansOrderByNickName(FansParamsDto fansParamsDto){
        return userFollowMapper.getFansOrderByNickName(fansParamsDto);
    }

    public List<UserFollowDto> getFollowsOrderByNickName(FollowParamsDto followParamsDto){
        return userFollowMapper.getFollowsOrderByNickName(followParamsDto);
    }

    public List<UserFansDto> getFansOrderByTime(FansParamsDto fansParamsDto){
        return userFollowMapper.getFansOrderByTime(fansParamsDto);
    }

    public List<UserFollowDto> getFollowsOrderByTime(FollowParamsDto followParamsDto){
        return userFollowMapper.getFollowsOrderByTime(followParamsDto);
    }

    public List<UserProfile> getPromoter(String nickName){
        UserProfileExample example = new UserProfileExample();
        UserProfileExample.Criteria criteria = example.createCriteria();
        criteria.andIsPromoterEqualTo(1);
        if(!StringUtils.isEmpty(nickName)) {
            criteria.andNickNameEqualTo(nickName);
        }
        return userProfileMapper.selectByExample(example);
    }

    public int getUnactivatedCount(long uid,String startDate,String endDate){
        UserProfileExample example = new UserProfileExample();
        UserProfileExample.Criteria criteria = example.createCriteria();
        criteria.andRefereeUidEqualTo(uid);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(!StringUtils.isEmpty(startDate)) {
            try {
                criteria.andCreateTimeGreaterThan(sdf.parse(startDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if(!StringUtils.isEmpty(endDate)) {
            try {
                criteria.andCreateTimeLessThan(sdf.parse(startDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        criteria.andIsActivateEqualTo(Specification.UserActivate.UN_ACTIVATED.index);
        return userProfileMapper.countByExample(example);
    }

    public int getRefereeCount(long uid,String startDate,String endDate){
        UserProfileExample example = new UserProfileExample();
        UserProfileExample.Criteria criteria = example.createCriteria();
        criteria.andRefereeUidEqualTo(uid);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(!StringUtils.isEmpty(startDate)) {
            try {
                criteria.andCreateTimeGreaterThan(sdf.parse(startDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if(!StringUtils.isEmpty(endDate)) {
            try {
                criteria.andCreateTimeLessThan(sdf.parse(startDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        criteria.andIsActivateEqualTo(Specification.UserActivate.ACTIVATED.index);
        return userProfileMapper.countByExample(example);
    }

    public void createJpushToken(JpushToken jpushToken){
        jpushTokenMapper.insertSelective(jpushToken);
    }

    public List<JpushToken> getJpushToken(long uid){
        JpushTokenExample example = new JpushTokenExample();
        JpushTokenExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        return jpushTokenMapper.selectByExample(example);
    }

    public void refreshJpushToken(JpushToken jpushToken){
        jpushTokenMapper.updateByPrimaryKeySelective(jpushToken);
    }

    public List<UserProfile> searchFans(SearchFansDto searchFansDto){
        return userProfileMapper.searchFans(searchFansDto);
    }

    public int totalFans(SearchFansDto searchFansDto){
        return userProfileMapper.countFans(searchFansDto);
    }

    //新增第三方登录数据
    public void creatThirdPartUser(ThirdPartUser thirdPartUser){
        thirdPartUserMapper.insertSelective(thirdPartUser);
    }

    public ThirdPartUser getThirdPartUser(String openId ,int type){
        ThirdPartUserExample example = new ThirdPartUserExample();
        ThirdPartUserExample.Criteria criteria = example.createCriteria();
        criteria.andThirdPartOpenIdEqualTo(openId);
        criteria.andStatusEqualTo(1);
        criteria.andThirdPartTypeEqualTo(type);
        List<ThirdPartUser> thirdPartUsers = thirdPartUserMapper.selectByExample(example);
        return thirdPartUsers.size()>0 && thirdPartUsers !=null ?thirdPartUsers.get(0):null;
    }

    public List<ThirdPartUser> getThirdPartUserByUnionId(String unionId ,int type){
        ThirdPartUserExample example = new ThirdPartUserExample();
        ThirdPartUserExample.Criteria criteria = example.createCriteria();
        criteria.andThirdPartUnionidEqualTo(unionId);
        criteria.andStatusEqualTo(1);
        criteria.andThirdPartTypeEqualTo(type);
        List<ThirdPartUser> thirdPartUsers = thirdPartUserMapper.selectByExample(example);
        return thirdPartUsers;
    }

    public void updateThirdPartUser(ThirdPartUser thirdPartUser){
        thirdPartUserMapper.updateByPrimaryKeySelective(thirdPartUser);
    }

    public List<UserProfile> checkUserNickName(String nickName){
        UserProfileExample example = new UserProfileExample();
        UserProfileExample.Criteria criteria = example.createCriteria();
        criteria.andNickNameEqualTo(nickName);
        return userProfileMapper.selectByExample(example);
    }

    public ThirdPartUser checkOpenId(String openId){
        ThirdPartUserExample example = new ThirdPartUserExample();
        ThirdPartUserExample.Criteria criteria = example.createCriteria();
        criteria.andThirdPartOpenIdEqualTo(openId);
        criteria.andStatusEqualTo(1);
        List<ThirdPartUser> thirdPartUsers = thirdPartUserMapper.selectByExample(example);
        return thirdPartUsers!=null&&thirdPartUsers.size()>0?thirdPartUsers.get(0):null;
    }

    public ThirdPartUser checkUnionId(String unionId){
        ThirdPartUserExample example = new ThirdPartUserExample();
        ThirdPartUserExample.Criteria criteria = example.createCriteria();
        criteria.andThirdPartUnionidEqualTo(unionId);
        criteria.andStatusEqualTo(1);
        List<ThirdPartUser> thirdPartUsers = thirdPartUserMapper.selectByExample(example);
        return thirdPartUsers!=null&&thirdPartUsers.size()>0?thirdPartUsers.get(0):null;
    }

    //检测第三方账号是否存在、根据openId
    public ThirdPartUser thirdPartIsExist(String openId ,int type){
        ThirdPartUserExample example = new ThirdPartUserExample();
        ThirdPartUserExample.Criteria criteria = example.createCriteria();
        criteria.andThirdPartOpenIdEqualTo(openId);
        criteria.andThirdPartTypeEqualTo(type);
        List<ThirdPartUser> thirdPartUsers = thirdPartUserMapper.selectByExample(example);
        return thirdPartUsers!=null&&thirdPartUsers.size()>0?thirdPartUsers.get(0):null;
    }

    public void createOpenCount(OpenDeviceCount openDeviceCount){
        openDeviceCountMapper.insertSelective(openDeviceCount);
    }

    public SystemConfig getSystemConfig() {
        SystemConfigExample example = new SystemConfigExample();

        List<SystemConfig> systemConfigs =systemConfigMapper.selectByExample(example);
        return systemConfigs.size()>0?systemConfigs.get(0):null;
    }

    public List<UserProfile> searchByNickNameAndvLv(String nickName, String mobile, int vLv, int page, int pageSize){
    	UserProfileExample example = new UserProfileExample();
        UserProfileExample.Criteria criteria =  example.createCriteria();
        if(null != nickName && !"".equals(nickName)){
        	criteria.andNickNameLike("%"+nickName+"%");
        }
        if(null != mobile && !"".equals(mobile)){
        	criteria.andMobileLike("%"+mobile+"%");
        }
        if(vLv >= 0){
        	criteria.andVLvEqualTo(vLv);
        }
        example.setOrderByClause("create_time desc limit "+((page-1)*pageSize)+", " + pageSize);
        return userProfileMapper.selectByExample(example);
    }
    
    public int totalByNickNameAndvLv(String nickName, String mobile, int vLv){
    	UserProfileExample example = new UserProfileExample();
        UserProfileExample.Criteria criteria =  example.createCriteria();
        if(null != nickName && !"".equals(nickName)){
        	criteria.andNickNameLike("%"+nickName+"%");
        }
        if(null != mobile && !"".equals(mobile)){
        	criteria.andMobileLike("%"+mobile+"%");
        }
        if(vLv >= 0){
        	criteria.andVLvEqualTo(vLv);
        }
        return userProfileMapper.countByExample(example);
    }

    public void createGag(UserGag gag) {
        userGagMapper.insert(gag);
    }

    public boolean checkGag(UserGag gag) {

        return getUserGag(gag)!=null;
    }

    public void deleteGag(UserGag gag) {
        UserGagExample example = new UserGagExample();
        UserGagExample.Criteria criteria = example.createCriteria();
        criteria.andTargetUidEqualTo(gag.getTargetUid());
        criteria.andTypeEqualTo(gag.getType());
        criteria.andCidEqualTo(gag.getCid());
        criteria.andGagLevelEqualTo(gag.getGagLevel());

        userGagMapper.deleteByExample(example);
    }

    public UserGag getUserGag(UserGag gag) {
        UserGagExample example = new UserGagExample();
        UserGagExample.Criteria criteria = example.createCriteria();
        criteria.andTargetUidEqualTo(gag.getTargetUid());
        List<Integer> intList = new ArrayList<>();
        intList.add(gag.getType());
        if(gag.getType()>0){
            intList.add(0);
        }
        criteria.andTypeIn(intList);
        List<Long> longList = new ArrayList<>();
        longList.add(gag.getCid());
        if(gag.getCid()>0){
            longList.add(0L);
        }
        criteria.andCidIn(longList);
        intList = new ArrayList<>();
        intList.add(gag.getGagLevel());
        if(gag.getGagLevel()>0){
            intList.add(0);
        }
        criteria.andGagLevelIn(intList);

        List<UserGag> list = userGagMapper.selectByExample(example);
        return list!=null&&list.size()>0?list.get(0):null;
    }
}
