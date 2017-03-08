package com.me2me.content.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.me2me.common.web.Specification;
import com.me2me.content.dto.*;
import com.me2me.content.mapper.*;
import com.me2me.content.model.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/22.
 */
@Repository
public class ContentMybatisDao {

    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private ContentImageMapper contentImageMapper;

    @Autowired
    private ContentTagsMapper contentTagsMapper;

    @Autowired
    private HighQualityContentMapper highQualityContentMapper;

    @Autowired
    private ContentTagsDetailsMapper contentTagsDetailsMapper;

    @Autowired
    private ContentReviewMapper contentReviewMapper;

    @Autowired
    private ContentLikesDetailsMapper contentLikesDetailsMapper;

    @Autowired
    private ArticleLikesDetailsMapper articleLikesDetailsMapper;

    @Autowired
    private ArticleReviewMapper articleReviewMapper;

    @Autowired
    private ArticleTagsDetailsMapper articleTagsDetailsMapper;

    @Autowired
    private AtReviewMapper atReviewMapper;

    public List<Content> loadSquareData(int sinceId){
        return contentMapper.loadSquareData(sinceId);
    }

    public List<Content>highQuality(int sinceId){
        return contentMapper.loadHighQualityData(sinceId);
    }

    public void createContent(Content content){
        contentMapper.insertSelective(content);
    }

    public void createContentImage(ContentImage contentImage){
        contentImageMapper.insertSelective(contentImage);
    }

    public Content getContentById(long id){
        return contentMapper.selectByPrimaryKey(id);
    }

    public void updateContentById(Content content){
        contentMapper.updateByPrimaryKeySelective(content);
    }

    public void updateContent(Content content){
        contentMapper.updateByPrimaryKey(content);
    }

    public void createTag(ContentTags contentTags){
        ContentTagsExample example = new ContentTagsExample();
        ContentTagsExample.Criteria criteria = example.createCriteria();
        criteria.andTagEqualTo(contentTags.getTag());
        List<ContentTags> list = contentTagsMapper.selectByExample(example);
        if(list == null ||list.size() ==0) {
            contentTagsMapper.insertSelective(contentTags);
        }else{
            contentTags.setId(list.get(0).getId());
        }
    }

    //获取所有（包括UGC和直播等）
    public List<Content>myPublish(long uid,int sinceId) {
        Map<String,Object> map = Maps.newHashMap();
        map.put("uid",uid);
        map.put("sinceId",sinceId);
        return contentMapper.loadMyPublishData(map);
    }

    public List<Content>myPublishUgc(long uid,int sinceId,int flag) {
        Map<String,Object> map = Maps.newHashMap();
        map.put("uid",uid);
        map.put("sinceId",sinceId);
        map.put("flag", flag);
        return contentMapper.loadMyPublishUgcData(map);
    }

    public List<Content>myPublishLive(long uid,long updateTime) {
        Map<String,Object> map = Maps.newHashMap();
        map.put("uid",uid);
        map.put("updateTime",updateTime);
        return contentMapper.loadMyPublishLiveData(map);
    }

    public List<Content>myPublishLive2(long uid,int sinceId) {
        Map<String,Object> map = Maps.newHashMap();
        map.put("uid",uid);
        map.put("sinceId",sinceId);
        return contentMapper.loadMyPublishLiveData2(map);
    }

    public void modifyPGCById(Content content){
        contentMapper.updateByPrimaryKeySelective(content);
    }

    public List<ContentTagsDetails> getContentTagsDetails(long cid , Date createTime, long sinceId) {
        ContentTagsDetailsExample example = new ContentTagsDetailsExample();
        ContentTagsDetailsExample.Criteria criteria = example.createCriteria();
        criteria.andCidEqualTo(cid);
        criteria.andIdLessThan(sinceId);
        //由于自己的feeling也是感受，两个表存储有可能会有时间差，故这里想自己的去除。
        Calendar cal = Calendar.getInstance();
		cal.setTime(createTime);
		cal.add(Calendar.SECOND, 2);
		criteria.andCreateTimeGreaterThan(cal.getTime());
        example.setOrderByClause(" create_time desc ");
        return contentTagsDetailsMapper.selectByExample(example);
    }

    public List<ContentImage> getContentImages(long cid){
        ContentImageExample example = new ContentImageExample();
        ContentImageExample.Criteria criteria = example.createCriteria();
        criteria.andCidEqualTo(cid);
        return contentImageMapper.selectByExample(example);
    }

    public ContentImage getCoverImages(long cid){
        ContentImageExample example = new ContentImageExample();
        ContentImageExample.Criteria criteria = example.createCriteria();
        criteria.andCidEqualTo(cid);
        List<ContentImage> list =  contentImageMapper.selectByExample(example);
        for (ContentImage contentImage : list){
            if(contentImage.getCover() ==1){
                return contentImage;
            }
        }
        return null;
    }
    public ContentTags getContentTags(String feeling){
        ContentTagsExample example = new ContentTagsExample();
        ContentTagsExample.Criteria criteria = example.createCriteria();
        criteria.andTagEqualTo(feeling);
        List<ContentTags> list = contentTagsMapper.selectByExample(example);
        return (list !=null && list.size() >0 ) ? list.get(0) : null;
    }

    public ContentTags getContentTagsById(long tid){
       return contentTagsMapper.selectByPrimaryKey(tid);
    }


    public void createHighQualityContent(HighQualityContent highQualityContent){
        highQualityContentMapper.insertSelective(highQualityContent);
    }

    public void removeHighQualityContent(long id){
        highQualityContentMapper.deleteByPrimaryKey(id);
    }

    public HighQualityContent getHQuantityByCid(long cid){
        HighQualityContentExample example = new HighQualityContentExample();
        HighQualityContentExample.Criteria criteria = example.createCriteria();
        criteria.andCidEqualTo(cid);
        List<HighQualityContent> list = highQualityContentMapper.selectByExample(example);
        return list!=null&&list.size()>0?list.get(0):null;
    }

    public List<Content> loadSelectedData(int sinceId){
        return contentMapper.loadSelectedData(sinceId);
    }

    public List<Content> loadActivityData(int sinceId){
        return contentMapper.loadActivityData(sinceId);
    }

    public List<Content> showContentsByPage(EditorContentDto editorContentDto){
        ContentExample example = new ContentExample();
        ContentExample.Criteria criteria = example.createCriteria();
        queryCondition(editorContentDto, example, criteria);
        example.setOrderByClause("create_time desc limit "+((editorContentDto.getPage()-1)*editorContentDto.getPageSize())+","+editorContentDto.getPageSize()+"");
        return contentMapper.selectByExampleWithBLOBs(example);
    }

    private void queryCondition(EditorContentDto editorContentDto, ContentExample example, ContentExample.Criteria criteria) {
        if(editorContentDto.getArticleType()==1){
            // PGC
            criteria.andTypeEqualTo(Specification.ArticleType.EDITOR.index);
//            ContentExample.Criteria criteria2 = example.createCriteria();
            if(StringUtils.isNotBlank(editorContentDto.getKeyword())){
            	criteria.andTitleLike("%"+editorContentDto.getKeyword()+"%");
            }
//            criteria2.andTypeEqualTo(Specification.ArticleType.ACTIVITY.index);
            criteria.andStatusNotEqualTo(1);
//            example.or(criteria2);
        }else{
            // UGC
            criteria.andTypeEqualTo(Specification.ArticleType.ORIGIN.index);
//            ContentExample.Criteria criteria2 = example.createCriteria();
            if(StringUtils.isNotBlank(editorContentDto.getKeyword())){
            	criteria.andTitleLike("%"+editorContentDto.getKeyword()+"%");
            }
//            criteria2.andTypeEqualTo(Specification.ArticleType.LIVE.index);
            criteria.andStatusNotEqualTo(1);
//            example.or(criteria2);
        }
    }

    public int total(EditorContentDto editorContentDto){
        ContentExample example = new ContentExample();
        ContentExample.Criteria criteria = example.createCriteria();
        queryCondition(editorContentDto, example, criteria);
        return contentMapper.countByExample(example);
    }

    public Integer getTopicStatus(long topicId){
       Integer result = contentMapper.getTopicStatus(topicId);
        return  result == null ? 1 : result;
    }

    public void deleteTopicById(long topicId){
        contentMapper.deleteTopicById(topicId);
    }

    public int isFavorite(long topicId,long uid){
        IsFavoriteDto isFavoriteDto = new IsFavoriteDto();
        isFavoriteDto.setUid(uid);
        isFavoriteDto.setTopicId(topicId);
        return contentMapper.isFavorite(isFavoriteDto);
    }

    public List<Content> getHottestContent(int sinceId){
        return contentMapper.loadHottestContent(sinceId);
    }

    public List<Content> getHottestTopsContent(int flag){
        return contentMapper.loadHottestTopsContent(flag);
    }

    public int getContentImageCount(long cid){
        ContentImageExample example = new ContentImageExample();
        ContentImageExample.Criteria criteria = example.createCriteria();
        criteria.andCidEqualTo(cid);
        return contentImageMapper.countByExample(example);
    }

    public List<Content> getNewest(int sinceId, int vFlag){
        return contentMapper.loadNewestContent(sinceId, vFlag);
    }

    public List<Content> getAttention(long sinceId , long meUid, int vFlag){
        Map<String,Object> params = Maps.newHashMap();
        params.put("uid",meUid);
        params.put("sinceId",sinceId);
        params.put("flag", vFlag);
        return contentMapper.getAttention(params);
    }

    public void createContentTagsDetails(ContentTagsDetails contentTagsDetails){
        contentTagsDetailsMapper.insert(contentTagsDetails);
    }

    public void createReview(ContentReview review){
        contentReviewMapper.insertSelective(review);
    }

    public List<Content> getContentByTopicId(long topicId){
        ContentExample example = new ContentExample();
        ContentExample.Criteria criteria =example.createCriteria();
        criteria.andForwardCidEqualTo(topicId);
        criteria.andTypeEqualTo(Specification.ArticleType.LIVE.index);
        return contentMapper.selectByExample(example);
    }
    
    public List<Content> getContentByTopicIds(List<Long> topicIds){
    	ContentExample example = new ContentExample();
        ContentExample.Criteria criteria =example.createCriteria();
        criteria.andForwardCidIn(topicIds);
        criteria.andTypeEqualTo(Specification.ArticleType.LIVE.index);
        return contentMapper.selectByExample(example);
    }

    public List<ContentReview> getContentReviewByCid(long cid,long sinceId){
        ContentReviewExample example = new ContentReviewExample();
        ContentReviewExample.Criteria criteria = example.createCriteria();
        criteria.andCidEqualTo(cid);
        criteria.andIdLessThan(sinceId);
        criteria.andStatusNotEqualTo(Specification.ContentDelStatus.DELETE.index);//非删除的内容
        example.setOrderByClause(" create_time desc limit 20 ");
        return contentReviewMapper.selectByExample(example);
    }

    public List<ContentReview> getArticleReviewByCid(long cid,long sinceId){
        List<ContentReview> result = Lists.newArrayList();
        ArticleReviewExample example = new ArticleReviewExample();
        ArticleReviewExample.Criteria criteria = example.createCriteria();
        criteria.andArticleIdEqualTo(cid);
        criteria.andIdLessThan(sinceId);
        criteria.andStatusNotEqualTo(Specification.ContentDelStatus.DELETE.index);//非删除的内容
        example.setOrderByClause(" create_time desc limit 20 ");
        List<ArticleReview> list = articleReviewMapper.selectByExample(example);
        for(ArticleReview articleReview : list){
            ContentReview contentReview = new ContentReview();
            contentReview.setId(articleReview.getId());
            contentReview.setCid(articleReview.getArticleId());
            contentReview.setCreateTime(articleReview.getCreateTime());
            contentReview.setReview(articleReview.getReview());
            contentReview.setUid(articleReview.getUid());
            contentReview.setAtUid(articleReview.getAtUid());
            contentReview.setExtra(articleReview.getExtra());
            result.add(contentReview);
        }
        return result;
    }

    public List<ContentReview> getContentReviewTop3ByCid(long cid){
        ContentReviewExample example = new ContentReviewExample();
        ContentReviewExample.Criteria criteria = example.createCriteria();
        criteria.andCidEqualTo(cid);
        criteria.andStatusNotEqualTo(Specification.ContentDelStatus.DELETE.index);
        example.setOrderByClause(" create_time desc limit 20 ");
        return contentReviewMapper.selectByExample(example);
    }
    
    public int countContentReviewByCid(long cid){
    	ContentReviewExample example = new ContentReviewExample();
        ContentReviewExample.Criteria criteria = example.createCriteria();
        criteria.andCidEqualTo(cid);
        criteria.andStatusNotEqualTo(Specification.ContentDelStatus.DELETE.index);
        return contentReviewMapper.countByExample(example);
    }
    
    public List<ContentReview> getContentReviewPageByUid(long uid, int start, int pageSize){
    	ContentReviewExample example = new ContentReviewExample();
        ContentReviewExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        example.setOrderByClause(" id desc limit "+start+","+pageSize);
        return contentReviewMapper.selectByExample(example);
    }
    
    public int countContentReviewPageByUid(long uid){
    	ContentReviewExample example = new ContentReviewExample();
        ContentReviewExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        return contentReviewMapper.countByExample(example);
    }

    public int isLike(long cid, long uid){
        ContentLikesDetailsExample example = new ContentLikesDetailsExample();
        ContentLikesDetailsExample.Criteria criteria = example.createCriteria();
        criteria.andCidEqualTo(cid);
        criteria.andUidEqualTo(uid);
        int count = contentLikesDetailsMapper.countByExample(example);
        return  count > 0 ? 1 : 0;
    }

    public void createContentLikesDetails(ContentLikesDetails contentLikesDetails){
        contentLikesDetailsMapper.insertSelective(contentLikesDetails);
    }

    public void deleteContentLikesDetails(ContentLikesDetails contentLikesDetails){
        ContentLikesDetailsExample example = new ContentLikesDetailsExample();
        ContentLikesDetailsExample.Criteria criteria = example.createCriteria();
        criteria.andCidEqualTo(contentLikesDetails.getCid());
        criteria.andUidEqualTo(contentLikesDetails.getUid());
        contentLikesDetailsMapper.deleteByExample(example);
    }

    public ContentLikesDetails getContentLikesDetails(ContentLikesDetails contentLikesDetails){
        ContentLikesDetailsExample example = new ContentLikesDetailsExample();
        ContentLikesDetailsExample.Criteria criteria = example.createCriteria();
        criteria.andCidEqualTo(contentLikesDetails.getCid());
        criteria.andUidEqualTo(contentLikesDetails.getUid());
        List<ContentLikesDetails> list = contentLikesDetailsMapper.selectByExample(example);
        return list != null &&list.size() > 0 ? list.get(0) :null;
    }

    public ArticleLikesDetails getArticleLikesDetails(ArticleLikesDetails articleLikesDetails){
        ArticleLikesDetailsExample example = new ArticleLikesDetailsExample();
        ArticleLikesDetailsExample.Criteria criteria = example.createCriteria();
        criteria.andArticleIdEqualTo(articleLikesDetails.getArticleId());
        criteria.andUidEqualTo(articleLikesDetails.getUid());
        List<ArticleLikesDetails> list = articleLikesDetailsMapper.selectByExample(example);
        return list != null &&list.size() > 0 ? list.get(0) :null;
    }

    public void deleteArticleLikesDetails(ArticleLikesDetails articleLikesDetails){
        ArticleLikesDetailsExample example = new ArticleLikesDetailsExample();
        ArticleLikesDetailsExample.Criteria criteria = example.createCriteria();
        criteria.andArticleIdEqualTo(articleLikesDetails.getArticleId());
        criteria.andUidEqualTo(articleLikesDetails.getUid());
        articleLikesDetailsMapper.deleteByExample(example);
    }

    public int countFragment(long topicId,long uid){
        CountFragmentDto countFragmentDto = new CountFragmentDto();
        countFragmentDto.setTopicId(topicId);
        countFragmentDto.setUid(uid);
        return contentMapper.countFragment(countFragmentDto);
    }

    public void createArticleLike(LikeDto likeDto){
        ArticleLikesDetails articleLikesDetails = new ArticleLikesDetails();
        articleLikesDetails.setArticleId(likeDto.getCid());
        articleLikesDetails.setUid(likeDto.getUid());
        articleLikesDetailsMapper.insertSelective(articleLikesDetails);
    }

    public void createArticleReview(ReviewDto reviewDto){
        ArticleReview review = new ArticleReview();
        review.setArticleId(reviewDto.getCid());
        review.setReview(reviewDto.getReview());
        review.setUid(reviewDto.getUid());
        review.setAtUid(reviewDto.getAtUid());
        review.setStatus(Specification.ContentDelStatus.NORMAL.index);
        if(StringUtils.isNotEmpty(reviewDto.getExtra())){
            review.setExtra(reviewDto.getExtra());
        }
        articleReviewMapper.insertSelective(review);
    }

    public List<ArticleLikesDetails> getArticleLikesDetails(long id){
        ArticleLikesDetailsExample example = new ArticleLikesDetailsExample();
        ArticleLikesDetailsExample.Criteria criteria = example.createCriteria();
        criteria.andArticleIdEqualTo(id);
        return articleLikesDetailsMapper.selectByExample(example);
    }

    public List<ArticleReview> getArticleReviews(long id ,long sinceId){
        ArticleReviewExample example = new ArticleReviewExample();
        ArticleReviewExample.Criteria criteria = example.createCriteria();
        criteria.andArticleIdEqualTo(id);
        criteria.andIdLessThan(sinceId);
        criteria.andStatusNotEqualTo(Specification.ContentDelStatus.DELETE.index);
        example.setOrderByClause(" id desc limit 20 ");
        return articleReviewMapper.selectByExample(example);
    }
    
    public int countArticleReviews(long id){
    	ArticleReviewExample example = new ArticleReviewExample();
        ArticleReviewExample.Criteria criteria = example.createCriteria();
        criteria.andArticleIdEqualTo(id);
        criteria.andStatusNotEqualTo(Specification.ContentDelStatus.DELETE.index);
        return articleReviewMapper.countByExample(example);
    }
    
    public List<ArticleReview> getArticleReviewPageByUid(long uid, int start, int pageSize){
    	ArticleReviewExample example = new ArticleReviewExample();
        ArticleReviewExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        example.setOrderByClause(" id desc limit "+start+","+pageSize);
        return articleReviewMapper.selectByExample(example);
    }
    
    public int countArticleReviewPageByUid(long uid){
    	ArticleReviewExample example = new ArticleReviewExample();
        ArticleReviewExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        return articleReviewMapper.countByExample(example);
    }

    public void createContentArticleDetails(ArticleTagsDetails articleTagsDetails){
        articleTagsDetailsMapper.insertSelective(articleTagsDetails);
    }

    public List<ContentLikesDetails> getContentLikesDetails(long id){
        ContentLikesDetailsExample example = new ContentLikesDetailsExample();
        ContentLikesDetailsExample.Criteria criteria = example.createCriteria();
        criteria.andCidEqualTo(id);
        example.setOrderByClause(" create_time desc ");
        return contentLikesDetailsMapper.selectByExample(example);
    }

    public List<ArticleTagsDetails> getArticleTagsDetails(long id){
        ArticleTagsDetailsExample example = new ArticleTagsDetailsExample();
        ArticleTagsDetailsExample.Criteria criteria = example.createCriteria();
        criteria.andArticleIdEqualTo(id);
        return articleTagsDetailsMapper.selectByExample(example);
    }

    public List<ResultKingTopicDto> kingTopic(KingTopicDto kingTopic){
        return contentMapper.kingTopic(kingTopic);
    }

    public int getTopicCount(long topicId){
        return contentMapper.getTopicCount(topicId);
    }

    public Long getTopicLastUpdateTime(long topicId){
        Long result = contentMapper.getTopicLastUpdateTime(topicId);
        return result == null ? 0 : result;
    }

    public List<Content>myPublishByType(MyPublishDto myPublishDto) {
        return contentMapper.loadMyPublishDataByType(myPublishDto);
    }

    public int countMyPublishByType(MyPublishDto myPublishDto) {
        return contentMapper.countMyPublishByType(myPublishDto);
    }

    public void createAtReview(AtReview atReview){
        atReviewMapper.insertSelective(atReview);
    }


    public AtReview getAtReview(long reviewId, int type){
        AtReviewExample example = new AtReviewExample();
        AtReviewExample.Criteria criteria = example.createCriteria();
        criteria.andReviewIdEqualTo(reviewId);
        criteria.andReviewTypeEqualTo(type);
        List<AtReview> list = atReviewMapper.selectByExample(example);
        return com.me2me.common.utils.Lists.getSingle(list);
    }

    public void clearData(){
        contentMapper.clearData();
    }

    /**
     * 
     * @param sinceId
     * @param flag   0 2.2.0版本前
     * @return
     */
    public List<Content2Dto> getHottestContentByUpdateTime(int sinceId, int flag){
        return contentMapper.loadHottestContentByUpdateTime(sinceId, flag);
    }
    
    public List<Content2Dto> getHotContentByType(long sinceId, int type, int pageSize){
    	return contentMapper.getHotContentByType(sinceId, type, pageSize);
    }

    public int getUgcCount(long uid){
        ContentExample example = new ContentExample();
        ContentExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andTypeNotBetween(3,6);
        criteria.andStatusEqualTo(0);
        return contentMapper.countByExample(example);
    }

    public int getLiveCount(long uid){
        ContentExample example = new ContentExample();
        ContentExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andTypeBetween(3,6);
        criteria.andStatusEqualTo(0);
        return contentMapper.countByExample(example);
    }
    
    public ArticleReview getArticleReviewById(long id){
    	return articleReviewMapper.selectByPrimaryKey(id);
    }
    
    public void updateArticleReview(ArticleReview ar){
    	articleReviewMapper.updateByPrimaryKeySelective(ar);
    }
    
    public ContentReview getContentReviewById(long id){
    	return contentReviewMapper.selectByPrimaryKey(id);
    }
    
    public void updateContentReview(ContentReview cr){
    	contentReviewMapper.updateByPrimaryKeySelective(cr);
    }
    
    public List<Content> getUgcPageByUid(long uid, int start, int pageSize){
    	ContentExample example = new ContentExample();
        ContentExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andTypeEqualTo(Specification.ArticleType.ORIGIN.index);
        example.setOrderByClause(" id desc limit "+start+","+pageSize);
        List<Content> list = contentMapper.selectByExampleWithBLOBs(example);
    	return list;
    }
    
    public int countUgcPageByUid(long uid){
    	ContentExample example = new ContentExample();
        ContentExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andTypeEqualTo(Specification.ArticleType.ORIGIN.index);
        return contentMapper.countByExample(example);
    }
}
