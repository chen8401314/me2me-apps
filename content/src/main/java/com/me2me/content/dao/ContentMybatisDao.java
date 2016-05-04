package com.me2me.content.dao;

import com.google.common.collect.Maps;
import com.me2me.activity.model.ActivityWithBLOBs;
import com.me2me.common.Constant;
import com.me2me.common.web.Specification;
import com.me2me.content.dto.*;
import com.me2me.content.mapper.*;
import com.me2me.content.model.*;
import com.me2me.user.model.UserFollow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.HashMap;
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
    private ContentUserLikesMapper contentUserLikesMapper;

    @Autowired
    private ContentTagsMapper contentTagsMapper;

    @Autowired
    private ContentTagLikesMapper contentTagLikesMapper;

    @Autowired
    private ContentUserLikesCountMapper contentUserLikesCountMapper;

    @Autowired
    private HighQualityContentMapper highQualityContentMapper;

    @Autowired
    private ContentTagsDetailsMapper contentTagsDetailsMapper;

    @Autowired
    private ContentReviewMapper contentReviewMapper;

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

    public void createContentUserLikes(ContentUserLikes contentUserLikes){
        contentUserLikesMapper.insertSelective(contentUserLikes);
    }

    public Content getContentById(long id){
        return contentMapper.selectByPrimaryKey(id);
    }

    public ContentUserLikes getContentUserLike(LikeDto likeDto){
        ContentUserLikesExample example = new ContentUserLikesExample();
        ContentUserLikesExample.Criteria criteria = example.createCriteria();
        criteria.andCidEqualTo(likeDto.getCid());
        criteria.andUidEqualTo(likeDto.getUid());
        //criteria.andTagIdEqualTo(likeDto.getTid());
        List<ContentUserLikes> list = contentUserLikesMapper.selectByExample(example);
        return list.size() > 0 ? list.get(0) : null;
    }

    public void deleteUserLikes(long id){
        contentUserLikesMapper.deleteByPrimaryKey(id);
    }

    public void updateContentById(Content content){
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
    public void createContentTagLikes(ContentTagLikes contentTagLikes ){
        contentTagLikesMapper.insertSelective(contentTagLikes);
    }

    public int isLike(long uid,long cid ,long tid){
        ContentUserLikesExample example = new ContentUserLikesExample();
        ContentUserLikesExample.Criteria criteria = example.createCriteria();
        criteria.andCidEqualTo(cid);
        criteria.andUidEqualTo(uid);
        criteria.andTagIdEqualTo(tid);
        List list = contentUserLikesMapper.selectByExample(example);
        return  (list != null && list.size() > 0 ) ? 1 : 0 ;
    }

    public List<Content>myPublish(long uid,int sinceId) {
        Map<String,Object> map = Maps.newHashMap();
        map.put("uid",uid);
        map.put("sinceId",sinceId);
        return contentMapper.loadMyPublishData(map);
    }

    public List<LoadAllFeelingDto>loadAllFeeling(long cid , int sinceId) {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("cid",cid);
        map.put("sinceId",sinceId);
        List<LoadAllFeelingDto> result = contentUserLikesMapper.loadAllFeeling(map);
        return result;
    }

    public List<ContentTagsDetails> getContentTagsDetails(long cid , long sinceId) {
        ContentTagsDetailsExample example = new ContentTagsDetailsExample();
        ContentTagsDetailsExample.Criteria criteria = example.createCriteria();
        criteria.andCidEqualTo(cid);
        criteria.andIdLessThan(sinceId);
        example.setOrderByClause(" create_time desc limit 5 ");
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

    public void addContentUserLikesCount(ContentUserLikesCount contentUserLikesCount){
        contentUserLikesCountMapper.insertSelective(contentUserLikesCount);
    }

    public void likeTagCount(ContentUserLikesCount contentUserLikesCount){
        ContentUserLikesCountExample example = new ContentUserLikesCountExample();
        ContentUserLikesCountExample.Criteria criteria = example.createCriteria();
        criteria.andCidEqualTo(contentUserLikesCount.getCid());
        criteria.andTidEqualTo(contentUserLikesCount.getTid());
        List<ContentUserLikesCount> likesCounts = contentUserLikesCountMapper.selectByExample(example);
        if(likesCounts != null && likesCounts.size() >0){
            contentUserLikesCount.setLikecount(likesCounts.get(0).getLikecount() + contentUserLikesCount.getLikecount());
            contentUserLikesCount.setId(likesCounts.get(0).getId());
            contentUserLikesCountMapper.updateByPrimaryKey(contentUserLikesCount);
        }else{
            contentUserLikesCountMapper.insert(contentUserLikesCount);
        }

    }

    public int getContentUserLikesCount(long cid ,long tid){
        ContentUserLikesCountExample example = new ContentUserLikesCountExample();
        ContentUserLikesCountExample.Criteria criteria = example.createCriteria();
        criteria.andCidEqualTo(cid);
        criteria.andTidEqualTo(tid);
        List<ContentUserLikesCount> likesCounts = contentUserLikesCountMapper.selectByExample(example);
        return (likesCounts != null && likesCounts.size() >0) ? likesCounts.get(0).getLikecount() : 0;
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



    public Content getContent(long cid,long uid){
        ContentExample example = new ContentExample();
        ContentExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andForwardCidEqualTo(cid);
        List<Content> list = contentMapper.selectByExampleWithBLOBs(example);
        return  (list != null && list.size() >0) ? list.get(0) : null;
    }

    public List<ContentTagLikes> getForwardContents(long cid){
        return contentTagLikesMapper.getContentTagTimeline(cid);
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
            ContentExample.Criteria criteria2 = example.createCriteria();
            criteria.andTitleLike("%"+editorContentDto.getKeyword()+"%");
            criteria2.andTypeEqualTo(Specification.ArticleType.ACTIVITY.index);
            example.or(criteria2);
        }else{
            // UGC
            criteria.andTypeEqualTo(Specification.ArticleType.ORIGIN.index);
        }
    }

    public int total(EditorContentDto editorContentDto){
        ContentExample example = new ContentExample();
        ContentExample.Criteria criteria = example.createCriteria();
        queryCondition(editorContentDto, example, criteria);
        return contentMapper.countByExample(example);
    }

    public int getTopicStatus(long topicId){
       return contentMapper.getTopicStatus(topicId);
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

    public int getContentImageCount(long cid){
        ContentImageExample example = new ContentImageExample();
        ContentImageExample.Criteria criteria = example.createCriteria();
        criteria.andCidEqualTo(cid);
        return contentImageMapper.countByExample(example);
    }

    public List<Content> getNewest(int sinceId){
        return contentMapper.loadNewestContent(sinceId);
    }

    public List<Content> getAttention(long sinceId , List<Long> userFollows){
        ContentExample example = new ContentExample();
        ContentExample.Criteria criteria = example.createCriteria();
        criteria.andStatusNotEqualTo(Specification.ContentStatus.DELETE.index);
        if(userFollows != null && userFollows.size() >0) {
            criteria.andUidIn(userFollows);
        }else{
            criteria.andUidEqualTo(-1L);
        }
        criteria.andRightsEqualTo(Specification.ContentRights.EVERY.index);
        criteria.andIdLessThan(sinceId);
        example.setOrderByClause(" id desc limit 10 ");
        return  contentMapper.selectByExampleWithBLOBs(example);


    }

    public void createContentTagsDetails(ContentTagsDetails contentTagsDetails){
        contentTagsDetailsMapper.insert(contentTagsDetails);
    }

    public void createReview(ContentReview review){
        contentReviewMapper.insert(review);
    }
}
