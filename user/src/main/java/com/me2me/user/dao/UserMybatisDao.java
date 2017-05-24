package com.me2me.user.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.me2me.common.page.PageBean;
import com.me2me.common.utils.FirstCharUtils;
import com.me2me.common.web.Specification;
import com.me2me.sms.dto.PushLogDto;
import com.me2me.user.dto.BasicDataDto;
import com.me2me.user.dto.EntryPageDto;
import com.me2me.user.dto.FansParamsDto;
import com.me2me.user.dto.FollowParamsDto;
import com.me2me.user.dto.PasteTagDto;
import com.me2me.user.dto.SearchFansDto;
import com.me2me.user.dto.SearchUserDto;
import com.me2me.user.dto.UserFansDto;
import com.me2me.user.dto.UserFollowDto;
import com.me2me.user.dto.UserNoticeDto;
import com.me2me.user.dto.VersionDto;
import com.me2me.user.mapper.ApplicationSecurityMapper;
import com.me2me.user.mapper.DictionaryMapper;
import com.me2me.user.mapper.DictionaryTypeMapper;
import com.me2me.user.mapper.EmotionInfoMapper;
import com.me2me.user.mapper.EmotionRecordMapper;
import com.me2me.user.mapper.EntryPageConfigMapper;
import com.me2me.user.mapper.ImConfigMapper;
import com.me2me.user.mapper.IosWapxMapper;
import com.me2me.user.mapper.JpushTokenMapper;
import com.me2me.user.mapper.MbtiMappingMapper;
import com.me2me.user.mapper.OpenDeviceCountMapper;
import com.me2me.user.mapper.SystemConfigMapper;
import com.me2me.user.mapper.ThirdPartUserMapper;
import com.me2me.user.mapper.UserDeviceMapper;
import com.me2me.user.mapper.UserFamousMapper;
import com.me2me.user.mapper.UserFollowMapper;
import com.me2me.user.mapper.UserGagMapper;
import com.me2me.user.mapper.UserHobbyMapper;
import com.me2me.user.mapper.UserMapper;
import com.me2me.user.mapper.UserMbtiHistoryMapper;
import com.me2me.user.mapper.UserMobileListMapper;
import com.me2me.user.mapper.UserNoMapper;
import com.me2me.user.mapper.UserNoticeMapper;
import com.me2me.user.mapper.UserNoticeUnreadMapper;
import com.me2me.user.mapper.UserProfileMapper;
import com.me2me.user.mapper.UserReportMapper;
import com.me2me.user.mapper.UserSeekFollowMapper;
import com.me2me.user.mapper.UserTagsDetailsMapper;
import com.me2me.user.mapper.UserTagsMapper;
import com.me2me.user.mapper.UserTagsRecordMapper;
import com.me2me.user.mapper.UserTipsMapper;
import com.me2me.user.mapper.UserTokenMapper;
import com.me2me.user.mapper.VersionChannelDownloadMapper;
import com.me2me.user.mapper.VersionControlMapper;
import com.me2me.user.mapper.XingePushLogMapper;
import com.me2me.user.model.ApplicationSecurity;
import com.me2me.user.model.ApplicationSecurityExample;
import com.me2me.user.model.Dictionary;
import com.me2me.user.model.DictionaryExample;
import com.me2me.user.model.DictionaryType;
import com.me2me.user.model.DictionaryTypeExample;
import com.me2me.user.model.EmotionInfo;
import com.me2me.user.model.EmotionInfoExample;
import com.me2me.user.model.EmotionRecord;
import com.me2me.user.model.EntryPageConfig;
import com.me2me.user.model.EntryPageConfigExample;
import com.me2me.user.model.ImConfig;
import com.me2me.user.model.ImConfigExample;
import com.me2me.user.model.IosWapx;
import com.me2me.user.model.IosWapxExample;
import com.me2me.user.model.JpushToken;
import com.me2me.user.model.JpushTokenExample;
import com.me2me.user.model.MbtiMapping;
import com.me2me.user.model.MbtiMappingExample;
import com.me2me.user.model.OpenDeviceCount;
import com.me2me.user.model.SystemConfig;
import com.me2me.user.model.SystemConfigExample;
import com.me2me.user.model.ThirdPartUser;
import com.me2me.user.model.ThirdPartUserExample;
import com.me2me.user.model.User;
import com.me2me.user.model.UserDevice;
import com.me2me.user.model.UserDeviceExample;
import com.me2me.user.model.UserExample;
import com.me2me.user.model.UserFamous;
import com.me2me.user.model.UserFamousExample;
import com.me2me.user.model.UserFollow;
import com.me2me.user.model.UserFollowExample;
import com.me2me.user.model.UserGag;
import com.me2me.user.model.UserGagExample;
import com.me2me.user.model.UserHobby;
import com.me2me.user.model.UserHobbyExample;
import com.me2me.user.model.UserMbtiHistory;
import com.me2me.user.model.UserMbtiHistoryExample;
import com.me2me.user.model.UserMobileList;
import com.me2me.user.model.UserMobileListExample;
import com.me2me.user.model.UserNo;
import com.me2me.user.model.UserNotice;
import com.me2me.user.model.UserNoticeExample;
import com.me2me.user.model.UserNoticeUnread;
import com.me2me.user.model.UserNoticeUnreadExample;
import com.me2me.user.model.UserProfile;
import com.me2me.user.model.UserProfileExample;
import com.me2me.user.model.UserReport;
import com.me2me.user.model.UserSeekFollow;
import com.me2me.user.model.UserSeekFollowExample;
import com.me2me.user.model.UserTags;
import com.me2me.user.model.UserTagsDetails;
import com.me2me.user.model.UserTagsDetailsExample;
import com.me2me.user.model.UserTagsExample;
import com.me2me.user.model.UserTagsRecord;
import com.me2me.user.model.UserTagsRecordExample;
import com.me2me.user.model.UserTips;
import com.me2me.user.model.UserTipsExample;
import com.me2me.user.model.UserToken;
import com.me2me.user.model.UserTokenExample;
import com.me2me.user.model.VersionChannelDownload;
import com.me2me.user.model.VersionChannelDownloadExample;
import com.me2me.user.model.VersionControl;
import com.me2me.user.model.VersionControlExample;
import com.me2me.user.model.XingePushLog;

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

    @Autowired
    private  EntryPageConfigMapper entryPageConfigMapper;

    @Autowired
    private UserFamousMapper userFamousMapper;

    @Autowired
    private IosWapxMapper iosWapxMapper;

    @Autowired
    private ImConfigMapper imConfigMapper;
    
    @Autowired
    private VersionChannelDownloadMapper versionChannelDownloadMapper;
    
    @Autowired
    private UserSeekFollowMapper userSeekFollowMapper;
    
    @Autowired
    private UserNoticeUnreadMapper userNoticeUnreadMapper;
    
    @Autowired
    private UserMobileListMapper userMobileListMapper;

    @Autowired
    private UserMbtiHistoryMapper mbtiHistoryMapper;
    
    @Autowired
    private EmotionInfoMapper emotionInfoMapper;
    
    @Autowired
    private MbtiMappingMapper mbtiMappingMapper;
    
    @Autowired
    private EmotionRecordMapper emotionRecordMapper;
    
    /**
     * 保存用户注册信息
     * @param user
     */
    public void createUser(User user){
        userMapper.insertSelective(user);
    }

    public void createUserProfile(UserProfile userProfile){
    	userProfile.setNameGroup(FirstCharUtils.getFirstChar(userProfile.getNickName()));
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
    
    public List<UserToken> getUserTokensByUids(List<Long> uids){
    	UserTokenExample example = new UserTokenExample();
        UserTokenExample.Criteria criteria = example.createCriteria();
        criteria.andUidIn(uids);
        return userTokenMapper.selectByExample(example);
    }

    public UserToken getUserTokenByUid(long uid ,String token){
        UserTokenExample example = new UserTokenExample();
        UserTokenExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andTokenEqualTo(token);
        List<UserToken> lists = userTokenMapper.selectByExample(example);
        return (lists != null && lists.size() > 0) ? lists.get(0) : null;
    }
    
    public void updateUserToken(UserToken userToken){
    	userTokenMapper.updateByPrimaryKeySelective(userToken);
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

    public List<User> getAllUser(){
        return userMapper.selectByExample(null);
    }
    
    public User getUserByUidPrimaryKey(long uid){
    	return userMapper.selectByPrimaryKey(uid);
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
    	if(null != userProfile.getNickName()){
    		userProfile.setNameGroup(FirstCharUtils.getFirstChar(userProfile.getNickName()));
    	}
        userProfileMapper.updateByPrimaryKeySelective(userProfile);
    }

    public UserProfile getUserProfileByUid(long uid) {
        UserProfileExample example = new UserProfileExample();
        UserProfileExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        List<UserProfile> lists = userProfileMapper.selectByExample(example);
        return (lists != null && lists.size() > 0) ? lists.get(0) : null;
    }
    
    public int getNoNameGroupUserProfileCount(){
    	UserProfileExample example = new UserProfileExample();
        UserProfileExample.Criteria criteria = example.createCriteria();
        criteria.andNameGroupEqualTo("");
        return userProfileMapper.countByExample(example);
    }
    
    public List<UserProfile> getNoNameGroupUserProfiles(int pageSize){
    	UserProfileExample example = new UserProfileExample();
        UserProfileExample.Criteria criteria = example.createCriteria();
        criteria.andNameGroupEqualTo("");
        example.setOrderByClause(" id asc limit " + pageSize);
        return userProfileMapper.selectByExample(example);
    }
    
    public void updateUserProfile(UserProfile userProfile){
    	if(null != userProfile.getNickName()){
    		userProfile.setNameGroup(FirstCharUtils.getFirstChar(userProfile.getNickName()));
    	}
    	userProfileMapper.updateByPrimaryKeySelective(userProfile);
    }

    public UserProfile getUserProfileByMobile(String mobile) {
        UserProfileExample example = new UserProfileExample();
        UserProfileExample.Criteria criteria = example.createCriteria();
        criteria.andMobileEqualTo(mobile);
        List<UserProfile> lists = userProfileMapper.selectByExample(example);
        return (lists != null && lists.size() > 0) ? lists.get(0) : null;
    }

    public List<UserProfile> getUserProfilesByUids(List<Long> uids) {
        UserProfileExample example = new UserProfileExample();
        UserProfileExample.Criteria criteria = example.createCriteria();
        criteria.andUidIn(uids);
        example.setOrderByClause(" id desc ");
        return userProfileMapper.selectByExample(example);
    }
    
    public List<UserProfile> getUserProfilesByMobiles(List<String> mobile){
    	UserProfileExample example = new UserProfileExample();
        UserProfileExample.Criteria criteria = example.createCriteria();
        criteria.andMobileIn(mobile);
        example.setOrderByClause(" id desc ");
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
        if(userNoticeDto.getLevel() == 1){//一级目录，除核心圈和聚合相关的信息
        	criteria.andNoticeTypeLessThanOrEqualTo(5);
        }else if(userNoticeDto.getLevel() == 2){//二级目录，系统消息，核心圈和聚合相关的信息
        	criteria.andNoticeTypeGreaterThanOrEqualTo(6);
        }else{//老版本的，则只需要查询2.2.0版本前的消息即可，主要是为了兼容老版本
        	criteria.andNoticeTypeLessThanOrEqualTo(7);//新的类型不展示。。防止前台不兼容
        }
        example.setOrderByClause("id desc limit 10 ");
        return  userNoticeMapper.selectByExampleWithBLOBs(example);
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
    
    public List<UserFollow> getAllFollows(long uid, List<Long> uids){
    	UserFollowExample example = new UserFollowExample();
        UserFollowExample.Criteria criteria =  example.createCriteria();
        criteria.andSourceUidEqualTo(uid);
        criteria.andTargetUidIn(uids);
        UserFollowExample.Criteria criteria2 =  example.createCriteria();
        criteria2.andSourceUidIn(uids);
        criteria2.andTargetUidEqualTo(uid);
        example.or(criteria2);
        return userFollowMapper.selectByExample(example);
    }
    
    public List<UserFollow> getUserFollowsBySourceUidAndTargetUids(long sourceUid, List<Long> targetUids){
    	UserFollowExample example = new UserFollowExample();
        UserFollowExample.Criteria criteria =  example.createCriteria();
        criteria.andSourceUidEqualTo(sourceUid);
        criteria.andTargetUidIn(targetUids);
        return userFollowMapper.selectByExample(example);
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
        criteria.andTargetUidGreaterThan(Long.valueOf(0));
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
    
    public List<VersionControl> getVersionListByVersionAndPlatform(String version,int platform){
    	VersionControlExample example = new VersionControlExample();
        VersionControlExample.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(version)){
        	criteria.andVersionLike("%"+version+"%");
        }
        if(platform > 0){
        	criteria.andPlatformEqualTo(platform);
        }
        example.setOrderByClause(" update_time desc ");
        List<VersionControl> list =  controlMapper.selectByExample(example);
        return list;
    }
    
    public VersionControl getVersionById(long id){
    	return controlMapper.selectByPrimaryKey(id);
    }
    
    public void saveOrUpdateVersion(VersionControl vc){
    	if(null == vc){
    		return;
    	}
    	if(null != vc.getId() && vc.getId() > 0){
    		controlMapper.updateByPrimaryKeySelective(vc);
    	}else{
    		controlMapper.insertSelectiveNotId(vc);
    	}
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
        List<UserNotice> userNotices = userNoticeMapper.selectByExampleWithBLOBs(example);
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
        return  userNoticeMapper.selectByExampleWithBLOBs(example);
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
        example.setOrderByClause(" id desc");
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
        userGagMapper.insertSelective(gag);
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
    
    public List<UserGag> getGagUserPageByTargetUid(long targetUid, int page, int pageSize){
    	 UserGagExample example = new UserGagExample();
         UserGagExample.Criteria criteria = example.createCriteria();
         if(targetUid > 0){
        	 criteria.andTargetUidEqualTo(targetUid);
         }
         example.setOrderByClause("id desc limit "+((page-1)*pageSize)+", " + pageSize);
         return userGagMapper.selectByExample(example);
    }
    
    public int countGagUserPageByTargetUid(long targetUid){
    	UserGagExample example = new UserGagExample();
        UserGagExample.Criteria criteria = example.createCriteria();
        if(targetUid > 0){
        	criteria.andTargetUidEqualTo(targetUid);
        }
        return userGagMapper.countByExample(example);
    }
    
    public void deleteGagUserById(long id){
    	userGagMapper.deleteByPrimaryKey(id);
    }

    public List<EntryPageConfig> getEntryPageConfig(EntryPageDto dto) {
        EntryPageConfigExample example = new EntryPageConfigExample();
        EntryPageConfigExample.Criteria criteria = example.createCriteria();
        criteria.andCversionGreaterThan(dto.getCversion());
        if(dto.getType()>0){
            criteria.andTypeEqualTo(dto.getType());
        }
        if(dto.getCversion()==0){
            //默认情况下只获取有效的数据
            criteria.andStatusEqualTo(0);
        }
        return entryPageConfigMapper.selectByExample(example);
    }
    
    public void updateSystemConfig(SystemConfig config){
    	systemConfigMapper.updateByPrimaryKeySelective(config);
    }

    public List<UserFamous> getUserFamousList(int start, int pageSize){
    	UserFamousExample example = new UserFamousExample();
    	example.setOrderByClause(" update_time desc limit "+start+","+pageSize);
    	return userFamousMapper.selectByExample(example);
    }
    
    public UserFamous getUserFamousByUid(long uid){
        UserFamousExample example = new UserFamousExample();
        example.createCriteria().andUidEqualTo(uid);
        List<UserFamous> list = userFamousMapper.selectByExample(example);
        return list.size()>0&&list!=null?list.get(0):null;
    }

    public void updateUserFamous(UserFamous userFamous){
         userFamousMapper.updateByPrimaryKeySelective(userFamous);
    }

    public void createUserFamous(UserFamous userFamous){
        userFamousMapper.insert(userFamous);
    }

    public void deleteUserFamous(long uid){
        UserFamousExample example = new UserFamousExample();
        example.createCriteria().andUidEqualTo(uid);
        userFamousMapper.deleteByExample(example);
    }

    public void createWapx(IosWapx iosWapx){
        iosWapxMapper.insertSelective(iosWapx);
    }

//    public IosWapx getWapxByIdfa(String idfa){
//        IosWapxExample example = new IosWapxExample();
//        example.createCriteria().andIdfaEqualTo(idfa);
//        List<IosWapx> list = iosWapxMapper.selectByExample(example);
//        return list.size()>0&&list!=null?list.get(0):null;
//    }

    public IosWapx getWapxByIdfa(String idfa ,int type){
        IosWapxExample example = new IosWapxExample();
        example.createCriteria().andIdfaEqualTo(idfa).andChannelTypEqualTo(type);
        List<IosWapx> list = iosWapxMapper.selectByExample(example);
        return list.size()>0&&list!=null?list.get(0):null;
    }

    public List<IosWapx> getWapxByIdfaList(String idfa){
        IosWapxExample example = new IosWapxExample();
        example.createCriteria().andIdfaEqualTo(idfa);
        return iosWapxMapper.selectByExample(example);
    }

    public void updateWapx(IosWapx iosWapx){
        iosWapxMapper.updateByPrimaryKeySelective(iosWapx);
    }

    public List<VersionChannelDownload> queryVersionChannelDownloads(String channel){
    	VersionChannelDownloadExample example = new VersionChannelDownloadExample();
    	VersionChannelDownloadExample.Criteria criteria = example.createCriteria();
    	if(!StringUtils.isEmpty(channel)){
    		criteria.andChannelLike("%"+channel+"%");
    	}
    	return versionChannelDownloadMapper.selectByExample(example);
    }
    
    public VersionChannelDownload getVersionChannelDownloadByChannel(String channel){
    	VersionChannelDownloadExample example = new VersionChannelDownloadExample();
    	VersionChannelDownloadExample.Criteria criteria = example.createCriteria();
    	criteria.andChannelEqualTo(channel);
    	List<VersionChannelDownload> list = versionChannelDownloadMapper.selectByExample(example);
    	if(null != list && list.size() > 0){
    		return list.get(0);
    	}
    	return null;
    }
    
    public void saveVersionChannelDownload(VersionChannelDownload vcd){
    	versionChannelDownloadMapper.insertSelective(vcd);
    }
    
    public VersionChannelDownload getVersionChannelDownloadById(long id){
    	return versionChannelDownloadMapper.selectByPrimaryKey(id);
    }
    
    public void updateVersionChannelDownload(VersionChannelDownload vcd){
    	versionChannelDownloadMapper.updateByPrimaryKeySelective(vcd);
    }
    
    public void deleteVersionChannelDownload(long id){
    	versionChannelDownloadMapper.deleteByPrimaryKey(id);
    }

	public PageBean<SearchUserDto> searchUserPage(PageBean page, Map<String, Object> queries) {
		queries.put("skip", (page.getCurrentPage()-1)*page.getPageSize());
		queries.put("limit", page.getPageSize());
		String order = (String) queries.get("order");
		String orderBy = (String) queries.get("orderBy");
		if(order!=null && orderBy!=null){
			queries.put("order", orderBy+" "+order);
		}
		List<SearchUserDto> list = userProfileMapper.searchUserPage(queries);
		int totalRecords=userProfileMapper.countSearchUserForPage(queries);
		page.setTotalRecords(totalRecords);
		page.setDataList(list);
		return page;
	}

    public boolean spreadIdfaExists(int spreadChannel, String idfa) {
        IosWapxExample example = new IosWapxExample();
        IosWapxExample.Criteria criteria = example.createCriteria();
        criteria.andChannelTypEqualTo(spreadChannel);
        criteria.andIdfaEqualTo(idfa);
        List<IosWapx> list = iosWapxMapper.selectByExample(example);
        return (list!=null&&list.size()>0)?Boolean.TRUE :Boolean.FALSE;
    }

    public void createImConfig(ImConfig imConfig){
        imConfigMapper.insertSelective(imConfig);
    }

    public ImConfig getImConfig(long customerId){
        ImConfigExample example = new ImConfigExample();
        example.createCriteria().andUidEqualTo(customerId);
        List<ImConfig> list = imConfigMapper.selectByExample(example);
        return list.size()>0&&list!=null?list.get(0):null;
    }
    
    public void countUserByDay(){
    	userProfileMapper.delUserCountDay();
    	userProfileMapper.countUserByDay();
    }

    public List<UserSeekFollow> getUserSeekFollows(long uid, long sinceId, int pageSize){
    	return userSeekFollowMapper.getUserSeekFollowWithUid(uid, sinceId, pageSize);
    }
    
    public UserSeekFollow getUserSeekFollowByUid(long uid){
    	UserSeekFollowExample example = new UserSeekFollowExample();
    	UserSeekFollowExample.Criteria criteria = example.createCriteria();
    	criteria.andUidEqualTo(uid);
    	List<UserSeekFollow> list = userSeekFollowMapper.selectByExample(example);
    	if(null != list && list.size() > 0){
    		return list.get(0);
    	}
    	return null;
    }
    
    public void saveUserSeekFollow(UserSeekFollow usf){
    	userSeekFollowMapper.insertSelective(usf);
    }
    
    public void deleteOvertimeSeek(Date lastDate){
    	UserSeekFollowExample example = new UserSeekFollowExample();
    	UserSeekFollowExample.Criteria criteria = example.createCriteria();
    	criteria.andCreateTimeLessThan(lastDate);
    	userSeekFollowMapper.deleteByExample(example);
    }
    
    public void deleteUserProfile(long id){
    	userProfileMapper.deleteByPrimaryKey(id);
    }
    
    public int countUnreadNotice(long uid){
    	UserNoticeUnreadExample example = new UserNoticeUnreadExample();
    	UserNoticeUnreadExample.Criteria criteria = example.createCriteria();
    	criteria.andUidEqualTo(uid);
    	return userNoticeUnreadMapper.countByExample(example);
    }
    
    public void createUserNoticeUnread(UserNoticeUnread userNoticeUnread){
    	userNoticeUnreadMapper.insertSelective(userNoticeUnread);
    }
    
    public void clearUserNoticeUnreadByLevel(long uid, int level){
    	UserNoticeUnreadExample example = new UserNoticeUnreadExample();
    	UserNoticeUnreadExample.Criteria criteria = example.createCriteria();
    	criteria.andUidEqualTo(uid);
    	criteria.andLevelEqualTo(level);
    	userNoticeUnreadMapper.deleteByExample(example);
    }
    
    public void clearUserNoticeUnreadByCid(long uid, int contentType, long cid){
    	UserNoticeUnreadExample example = new UserNoticeUnreadExample();
    	UserNoticeUnreadExample.Criteria criteria = example.createCriteria();
    	criteria.andUidEqualTo(uid);
    	criteria.andContentTypeEqualTo(contentType);
    	criteria.andCidEqualTo(cid);
    	userNoticeUnreadMapper.deleteByExample(example);
    }
    
    public List<UserMobileList> getUserMobileListByMobile(String mobile, long exceptUid){
    	UserMobileListExample example = new UserMobileListExample();
    	UserMobileListExample.Criteria criteria = example.createCriteria();
    	criteria.andMobileEqualTo(mobile);
    	if(exceptUid > 0){
    		criteria.andUidNotEqualTo(exceptUid);
    	}
    	return userMobileListMapper.selectByExample(example);
    }
    
    public void deleteUserMobileListByUid(long uid){
    	UserMobileListExample example = new UserMobileListExample();
    	UserMobileListExample.Criteria criteria = example.createCriteria();
    	criteria.andUidEqualTo(uid);
    	userMobileListMapper.deleteByExample(example);
    }
    
    public void saveMBTIResult(long uid,String mbti){
    	UserMbtiHistory history = new UserMbtiHistory();
    	history.setCreateTime(new Date());
    	history.setMbti(mbti);
    	history.setShared(0);
    	history.setUid(uid);
    	
    	mbtiHistoryMapper.insert(history);
    }
    public void addMBTIMapping(MbtiMapping mapping) {
		mbtiMappingMapper.insertSelective(mapping);
	}

	public void deleteMBTIMappingById(long mappingId) {
		mbtiMappingMapper.deleteByPrimaryKey((int)mappingId);
	}

	public void updateMBTIMapping(MbtiMapping mapping) {
		mbtiMappingMapper.updateByPrimaryKeySelective(mapping);
	}

	public MbtiMapping getMBTIMappingById(long id) {
		return mbtiMappingMapper.selectByPrimaryKey((int)id);
	}

	public List<MbtiMapping> getAllMBTIMapping() {
		MbtiMappingExample example = new MbtiMappingExample();
		example.setOrderByClause("createTime asc");
		return mbtiMappingMapper.selectByExample(example);
	}

	public List<UserMbtiHistory> getMBTIHistoryByUid(long uid) {
		UserMbtiHistoryExample example = new UserMbtiHistoryExample();
		example.createCriteria().andUidEqualTo(uid);
		example.setOrderByClause("create_time desc");
		return mbtiHistoryMapper.selectByExample(example);
	}
	
	public boolean isMBTIShared(long uid) {
		UserMbtiHistoryExample example = new UserMbtiHistoryExample();
		example.createCriteria().andUidEqualTo(uid).andSharedEqualTo(1);
		return mbtiHistoryMapper.countByExample(example)>0;
	}

	public Long getKingdomIdByMBTI(String mbti) {
		MbtiMappingExample example = new MbtiMappingExample();
		example.createCriteria().andNameEqualTo(mbti);
		List<MbtiMapping> mappings=  mbtiMappingMapper.selectByExample(example);
		if(mappings.size()>0){
			return mappings.get(0).getKingdomid();
		}else{
			return null;
		}
	}
    
	public List<EmotionInfo> getEmotionInfoList() {
		EmotionInfoExample example = new EmotionInfoExample();
		example.setOrderByClause("id desc");
		List<Integer> vList = new ArrayList<Integer>();
		vList.add(1);
		vList.add(2);
		example.createCriteria().andStatusIn(vList);
		List<EmotionInfo> list=  emotionInfoMapper.selectByExample(example);
		return list;
	}
	
	public EmotionInfo getEmotionInfoByKey(Long id) {
		return emotionInfoMapper.selectByPrimaryKey(id);
	}
	
	public void updateEmotionInfoByKey(EmotionInfo emotionInfo) {
		emotionInfoMapper.updateByPrimaryKeySelective(emotionInfo);
	}
	public Integer addEmotionInfo(EmotionInfo emotionInfo) {
		return emotionInfoMapper.insertSelective(emotionInfo);
	}
	
	public EmotionInfo getEmotionInfoByValue(int happyValue,int freeValue) {
		EmotionInfoExample example = new EmotionInfoExample();
		EmotionInfoExample.Criteria criteria  = example.createCriteria();
		criteria.andHappymaxGreaterThanOrEqualTo(happyValue);
		criteria.andHappyminLessThanOrEqualTo(happyValue);
		criteria.andFreemaxGreaterThanOrEqualTo(freeValue);
		criteria.andFreeminLessThanOrEqualTo(freeValue);
		List<EmotionInfo> list=  emotionInfoMapper.selectByExample(example);
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
	public Integer addEmotionRecord(EmotionRecord emotionRecord) {
		return emotionRecordMapper.insertSelective(emotionRecord);
	}
	
}
