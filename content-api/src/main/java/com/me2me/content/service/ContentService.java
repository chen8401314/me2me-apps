package com.me2me.content.service;

import java.util.List;
import java.util.Map;

import com.me2me.common.enums.USER_OPRATE_TYPE;
import com.me2me.common.page.PageBean;
import com.me2me.common.web.Response;
import com.me2me.content.dto.*;
import com.me2me.content.model.*;


/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/22.
 */
public interface ContentService{

    Response recommend(long uid,String emotion);

    /**
     * 精选接口
     * @return
     */
    Response highQuality(int sinceId,long uid);

    /**
     * 广场列表
     * @param sinceId
     * @return
     */
    Response square(int sinceId,long uid);

    /**
     * 发布接口
     * @param contentDto
     * @return
     */
    Response publish(ContentDto contentDto);

    /**
     * 点赞接口
     * @return
     */
    Response like(LikeDto likeDto);

    /**
     * 打标签接口
     * @return
     */
    Response writeTag(WriteTagDto writeTagDto);

    /**
     * 用户发布内容删除
     * @param id
     * @param uid
     * @return
     */
    Response deleteContent(long id, long uid, boolean isSys);

    /**
     * 获取内容详情
     * @param id
     * @return
     */
    Response contentDetail(long id,long uid);

    /**
     * 我发布的内容列表
     * @param uid
     * @param updateTime
     * @return
     */
    Response myPublish(long uid ,long updateTime ,int type ,int sinceId ,int newType,int vFlag);

    /**
     * 内容所有感受列表
     * @param cid
     * @return
     */
   // Response getContentFeeling(long cid, int sinceId);

    /**
     * 根据内容id，返回内容信息给H5
     * @param id
     */
    ContentH5Dto contentH5(long id);

    /**
     *
     * @param targetUid
     * @param sourceUid
     * @return
     */
    Response UserData(long targetUid ,long sourceUid);

    Response UserData2(long targetUid ,long sourceUid,int vFlag);

    /**
     * 小编发布接口
     * @param contentDto
     * @return
     */
    Response editorPublish(ContentDto contentDto);

    /**
     * 小编精选
     * @param sinceId
     * @return
     */
    Response SelectedData(int sinceId,long uid);

    /**
     * 精选首页
     * @return
     */
    Response highQualityIndex(int sinceId,long uid);

    /**
     * 修改内容权限
     * @param rights
     * @param cid
     * @param uid
     * @return
     */
    Response modifyRights(int rights,long cid,long uid);

    Response showContents(EditorContentDto editorContentDto);

    Response Activities(int sinceId,long uid);

    Response getHottest(int sinceId,long uid);

    Response Newest(long sinceId,long uid, int vFlag);

    Response Attention(int sinceId,long uid, int vFlag);

    List<Content> getAttention(long sinceId , long uid, int vFlag);

    Response createReview(ReviewDto reviewDto);

    Response option(long id, int optionAction, int action);

    Content getContentByTopicId(long topicId);
    
    List<Content> getContentsByTopicIds(List<Long> topicIds);

    Response showUGCDetails(long id);

    Response reviewList(long cid,long sinceId,int type);
    
    void addContentLikeByCid(long cid, long addNum);

    int isLike(long cid,long uid);

    int countFragment(long topicId,long uid);

    Response publish2(ContentDto contentDto);

    void createTag(ContentDto contentDto, Content content);

    void createContent(Content content);

    void createContentImage(ContentImage contentImage);

    Content getContentById(long id);

    ContentLikesDetails getContentLikesDetails(ContentLikesDetails contentLikesDetails);

    void createContentLikesDetails(ContentLikesDetails contentLikesDetails);

    void remind(Content content ,long uid ,int type,String arg);

    void remind(Content content ,long uid ,int type,String arg,long atUid);

    void deleteContentLikesDetails(ContentLikesDetails contentLikesDetails);

    Response like2(LikeDto likeDto);

    void createArticleLike(LikeDto likeDto);

    void createArticleReview(ReviewDto reviewDto);

    void createReview2(ReviewDto review);

    Response getArticleComments(long uid,long id);

    Response getArticleReview(long id, long sinceId);

    void createTag(ContentTags contentTags);

    void createContentTagsDetails(ContentTagsDetails contentTagsDetails);

    void createContentArticleDetails(ArticleTagsDetails articleTagsDetails);

    Response writeTag2(WriteTagDto writeTagDto);

    Response modifyPGC(ContentDto contentDto);

    void robotLikes(LikeDto likeDto);

    Response kingTopic(KingTopicDto kingTopic);

    Response myPublishByType(long uid ,int sinceId ,int type,long updateTime,long currentUid,int vFlag);

    void clearData();

    Response Hottest2(int sinceId,long uid, int flag);

    int getUgcCount(long uid);

    int getLiveCount(long uid);
    
    Response deleteReview(ReviewDelDTO delDTO);
    
    Response delArticleReview(ReviewDelDTO delDTO, boolean isSys);

    Response delContentReview(ReviewDelDTO delDTO, boolean isSys);
    
    Response searchUserContent(UserContentSearchDTO searchDTO);
    
    Response delUserContent(int type, long id);
    
    Response hotList(long sinceId, long uid, int vflag);
    
    Response ceKingdomHotList(long sinceId, long uid, int vflag);

    /**
     * 榜单列表
     * @return
     */
    Response showBangDanList(long sinceId, int type,long currentUid, int vflag);

    /**
     * 给IMS系统开的后门，直接通过sql查询结果
     * 其他地方不建议调用本方法
     * @param sql
     * @return
     */
    List<Map<String, Object>> queryEvery(String sql);
    
    /**
     * 给IMS系统开的后门，直接运行sql
     * @param sql
     */
    void executeSql(String sql);
    /**
     * for IMS
     */
    List<Map<String,Object>> queryForList(String sql,Object ...params);
    /**
     * for IMS
     */
    Map<String,Object> queryForMap(String sql,Object ...args);
    /**
     * for IMS
     */
    <T> T queryForObject(String sql,Class<T> cls,Object ...args);
    /**
     * for IMS
     * @author zhangjiwei
     * @date Jul 20, 2017
     * @param sql
     * @param args
     */
    void update(String sql,Object ...args);
    /**
     * 插入并返回ID，for ims
     * @author zhangjiwei
     * @date Jul 20, 2017
     * @param sql
     * @param args
     * @return
     */
    int insert(String sql,Object ...args);
    /**
     * 自动榜单列表插入方法(供IMS系统调用)
     * @param insertList
     * @param key
     */
    void insertBillboardList(List<BillBoardList> insertList, String key);

    /**
     * 榜单详情接口
     * @param currentUid
     * @param bid
     * @return
     */
     Response showListDetail(long currentUid, long bid,long sinceId, int vflag);
    /**
     * 获取所有的榜单
     * @author zhangjiwei
     * @date Mar 21, 2017
     * @return
     */
    List<BillBoard> getAllBillBoard();
    /**
     * 修改一个榜单
     * @author zhangjiwei
     * @date Mar 21, 2017
     * @param bb
     */
    void updateBillBoard(BillBoard bb);
    /**
     * 按ID删除榜单
     * @author zhangjiwei
     * @date Mar 21, 2017
     * @param id
     */
    void deleteBillBoardById(long id);
    /**
     * 按ID取一个榜单
     * @author zhangjiwei
     * @date Mar 21, 2017
     * @param id
     * @return
     */
    BillBoard getBillBoardById(long id);
    /**
     * 添加榜单
     * @author zhangjiwei
     * @date Mar 21, 2017
     * @param bb
     */
    void addBillBoard(BillBoard bb);
    /**
     * 根据榜单ID取榜单下的排名数据。（王国、人、子榜单）
     * @author zhangjiwei
     * @date Mar 21, 2017
     * @param id 榜单ID。
     * @return
     */
    List<BillBoardRelationDto> getRelationsByBillBoardId(long id);
    
    List<BillBoardRelation> getBillBoardRelationByBid(long bid);
    
   /**
    * 添加一个榜单排名数据。
    * @author zhangjiwei
    * @date Mar 21, 2017
    * @param br
    */
    void addRelationToBillBoard(BillBoardRelation br);
    /**
     * 删除一个排名数据
     * @author zhangjiwei
     * @date Mar 21, 2017
     * @param rid
     */
    void delBillBoardRelationById(long rid);
    /**
     * 修改一个排名数据。
     * @author zhangjiwei
     * @date Mar 21, 2017
     * @param br
     */
    void updateBillBoardRelation(BillBoardRelation br);
    
   //---上线榜单管理------------
    
    /**
     * 找人、找组织 上线榜单列表
     * @author zhangjiwei
     * @date Mar 21, 2017
     * @param id 榜单ID。
     * @return
     */
    List<OnlineBillBoardDto> getOnlineBillBoardListByType(int type);
   /**
    * 添加一个上线榜单。
    * @author zhangjiwei
    * @date Mar 21, 2017
    * @param br
    */
    void addOnlineBillBoard(BillBoardDetails br);
    /**
     * 删除一个上线榜单
     * @author zhangjiwei
     * @date Mar 21, 2017
     * @param rid
     */
    void delOnlineBillBoardById(long rid);
    /**
     * 修改一个上线榜单。
     * @author zhangjiwei
     * @date Mar 21, 2017
     * @param br
     */
    void updateOnlineBillBoard(BillBoardDetails br);
    
    public Integer addEmotionPack(EmotionPack pack);
	
	public void deleteEmotionPackByKey(Integer id);
	
	public void updateEmotionPackByKey(EmotionPack pack);
	
	public EmotionPack getEmotionPackByKey(Integer id);
	
	public PageBean<EmotionPack> getEmotionPackPage(PageBean<EmotionPack> page,Map<String,Object> conditions);
	
	public Integer addEmotionPackDetail(EmotionPackDetail detail);
	
	public void deleteEmotionPackDetailByKey(Integer id);
	
	public void updateEmotionPackDetailByKey(EmotionPackDetail detail);
	
	public EmotionPackDetail getEmotionPackDetailByKey(Integer id);
	
	
	public PageBean<EmotionPackDetail> getEmotionPackDetailPage(PageBean<EmotionPackDetail> page,Map<String,Object> conditions);
	/**
	 * 查询表情包
	 * @author zhangjiwei
	 * @date Apr 22, 2017
	 * @return
	 */
	Response emojiPackageQuery();
	
	Response emojiPackageDetail(int packageId);
	
	List<Long> getBillboardTopicIds4kingdomPushTask();
	
	List<BillBoard> getBillBoardList4kingdomPushTask();
	
	Response getEmotionInfoByValue(int happyValue,int freeValue) ;
	
	Response getLastEmotionInfo(long uid);
	
	List<EmotionPackDetail> getEmotionPackDetailBig();
	/**
	 * 返回价值王国列表。
	 * @author zhangjiwei
	 * @date Jun 9, 2017
	 * @param page 当前页
	 * @param pageSize 页大小
	 * @return
	 */
	Response<PricedKingdomDto> getPricedKingdomList(int page,int pageSize,long currentUid);
	
	Response shareRecord(long uid, int type, long cid, String shareAddr);
	
	public int getTopicMembersCount(long topicId);
	
	public int getTopicShareCount(long topicId);
	
	public void updateContentUid(long newUid,long topicId);
	/**
	 * 标签主页，取标签下面的王国列表。
	 * @author zhangjiwei
	 * @date Jun 29, 2017
	 * @param tagName
	 * @param order
	 * @param page
	 * @param pageSize
	 * @param uid
	 * @return
	 */
	Response<TagKingdomDto> tagKingdomList(String tagName, String order, int page, int pageSize, long uid);

    Response initSquareUpdateId();
    /**
     * 添加用户访问日志
     * @author zhangjiwei
     * @date Aug 8, 2017
     * @param uid
     * @param action
     * @param topicId
     */
    void addUserOprationLog(long uid,USER_OPRATE_TYPE action,long topicId);
    /**
     * 添加用户访问日志,任意类型日志。
     * @author zhangjiwei
     * @date Aug 8, 2017
     * @param uid
     * @param action
     * @param topicId
     */
    void addUserOprationLog(long uid,USER_OPRATE_TYPE action,String extra);
	/**
	 * 标签编辑界面-》 推荐子标签
	 * @author zhangjiwei
	 * @date Sep 13, 2017
	 * @param tag
	 * @return
	 */
	Response recommendSubTags(String tag);
	/**
	 * 广告位分页查询
	 * @author chenxiang
	 * @date 2017-09-19
	 * @param status page pageSize
	 * @return
	 */
    public Response searchAdBannerListPage(int status,int page, int pageSize);
	/**
	 * 构建王国列表，包括所有字段。
	 * @author zhangjiwei
	 * @date Sep 19, 2017
	 * @param uid 用户id.
	 * @param topicList 王国内容
	 * @param showType 1RMB,2米汤币
	 * @return
	 */
	List<NewKingdom> buildFullNewKingdom(long uid,List<Map<String,Object>> topicList, int showType);
	/**
	 * 构建基础王国列表，只包括 topic表字段。
	 * @author zhangjiwei
	 * @date Sep 19, 2017
	 * @param uid
	 * @param topicList
	 * @return
	 */
	List<NewKingdom> buildSimpleNewKingdom(long uid,List<Map<String,Object>> topicList);
	
	/**
	 * 保存广告位
	 * @param adBanner
	 * @return
	 */
	public int saveAdBanner(AdBanner adBanner);
	
	/**
	 * 更新广告位
	 * @param adBanner
	 * @return
	 */
	public int updateAdBanner(AdBanner adBanner);
	
	/**
	 * 获取广告位
	 * @param adBanner
	 * @return
	 */
	public AdBanner getAdBannerById(long id);
	
	
	/**
	 * 查询查询广告信息列表总数
	 * @param status
	 * @param bannerList
	 * @return
	 */
	public int getAdInfoCount(int status,List<Long> bannerList);
	/**
	 * 保存广告信息
	 * @param adInfo
	 * @return
	 */
	public int saveAdInfo(AdInfo adInfo);
	/**
	 * 更新广告信息
	 * @param adInfo
	 * @return
	 */
	public int updateAdInfo(AdInfo adInfo);
	
	/**
	 * 获取广告信息
	 * @param id
	 * @return
	 */
	public AdInfo getAdInfoById(long id);
	/**
	 * 获取所有广告信息
	 * @param id
	 * @return
	 */
	public List<AdBanner> getAllAdBannerList(int status);
	/**
	 * 查询广告信息列表
	 * @param status
	 * @param bannerId
	 * @param start
	 * @param pageSize
	 * @return
	 */
    public Response searchAdInfoListPage(int status,long bannerId,int page, int pageSize);
    
    /**
     * 获取广告信息列表
     * @param cid
     * @param uid
     * @return
     */
    public Response ad(long cid,long uid);
    
    /**
     * 上市王国集合查询接口
     * @param cid
     * @param uid
     * @return
     */
    public Response listingKingdomGroup(long cid,long uid);
    
	/**
	 * 用户组
	 * @param cid
	 * @param uid
	 * @return
	 */
	public Response userGroup(long cid, long uid);
	
	
	/**
	 * 标签集合
	 * @param cid
	 * @param uid
	 * @return
	 */
	public Response tagGroup(long cid, long uid);
	
	/**
	 * 首页热点接口
	 * @param page
	 * @param uid
	 * @return
	 */
	public Response hot(int page, long uid);
	
    /**
     * 保存广告点击记录接口
     * @param adRecord
     * @return
     */
    public Response adRead(long adid,long uid);
    
	/**
	 * 标签管理页查询
	 * @param type
	 * @param page
	 * @param uid
	 * @return
	 */
	public Response tagMgmtQuery(int type,int page,long uid) ;
	
	
	/**
	 * 查询其他单个除喜欢和不喜欢的标签
	 * @param uid
	 * @param tagId
	 * @return
	 */
	public com.me2me.content.dto.UserLikeDto getOtherNormalTag(long uid,long tagId) ;
}
