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
    private ContentUserLikeMapper contentUserLikeMapper;

    @Autowired
    private ContentTagsMapper contentTagsMapper;

    @Autowired
    private ContentTagLikesMapper contentTagLikesMapper;

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

    public void createContentUserLike(ContentUserLike contentUserLike){
        contentUserLikeMapper.insertSelective(contentUserLike);
    }

    public Content getContentById(long id){
        return contentMapper.selectByPrimaryKey(id);
    }

    public ContentUserLike getContentUserLike(LikeDto likeDto){
        ContentUserLikeExample example = new ContentUserLikeExample();
        ContentUserLikeExample.Criteria criteria = example.createCriteria();
        criteria.andCidEqualTo(likeDto.getCid());
        criteria.andUidEqualTo(likeDto.getUid());
        List<ContentUserLike> list = contentUserLikeMapper.selectByExample(example);
        return list.size() > 0 ? list.get(0) : null;
    }

    public void deleteUserLike(long id){
        contentUserLikeMapper.deleteByPrimaryKey(id);
    }

    public void updateContentById(Content content){
        contentMapper.updateByPrimaryKey(content);
    }

    public void createTag(WriteTagDto writeTagDto){
        ContentTags contentTags = new ContentTags();
        contentTags.setTag(writeTagDto.getTag());
        contentTagsMapper.insertSelective(contentTags);
        ContentTagLikes contentTagLikes = new ContentTagLikes();
        contentTagLikes.setTagId(contentTags.getId());
        contentTagLikes.setCid(writeTagDto.getCid());
        contentTagLikes.setUid(writeTagDto.getUid());
        contentTagLikesMapper.insertSelective(contentTagLikes);
    }

    public int isLike(long uid,long cid){
        ContentUserLikeExample example = new ContentUserLikeExample();
        ContentUserLikeExample.Criteria criteria = example.createCriteria();
        criteria.andCidEqualTo(cid);
        criteria.andUidEqualTo(uid);
        List list = contentUserLikeMapper.selectByExample(example);
        return  (list !=null&&list.size() > 0 ) ? 1 : 0 ;
    }

    public List<Content>myPublish(long uid,int sinceId) {
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("uid",uid);
        map.put("sinceId",sinceId);
        return contentMapper.loadMyPublishData(map);
    }

}
