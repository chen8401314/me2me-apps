package com.me2me.content.dao;

import com.me2me.content.dto.LikeDto;
import com.me2me.content.dto.WriteTagDto;
import com.me2me.content.mapper.*;
import com.me2me.content.model.*;
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
        criteria.andTagIdEqualTo(likeDto.getTid());
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
        }
    }
    public void createContentTagLikes(ContentTagLikes contentTagLikes ){
        contentTagLikesMapper.insertSelective(contentTagLikes);
    }

    public int isLike(long uid,long cid){
        ContentUserLikesExample example = new ContentUserLikesExample();
        ContentUserLikesExample.Criteria criteria = example.createCriteria();
        criteria.andCidEqualTo(cid);
        criteria.andUidEqualTo(uid);
        List list = contentUserLikesMapper.selectByExample(example);
        return  (list !=null&&list.size() > 0 ) ? 1 : 0 ;
    }

    public List<Content>myPublish(long uid,int sinceId) {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("uid",uid);
        map.put("sinceId",sinceId);
        return contentMapper.loadMyPublishData(map);
    }

    public List<Map<String,String>>loadAllFeeling(long cid ,int sinceId) {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("cid",cid);
        map.put("sinceId",sinceId);
        List<Map<String,String>> result = contentUserLikesMapper.loadAllFeeling(map);
        return result;
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

        contentUserLikesCountMapper.insertSelective(contentUserLikesCount);
    }


}
