package com.me2me.content.widget;

import com.google.gson.JsonObject;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.content.dao.ContentMybatisDao;
import com.me2me.content.dto.LikeDto;
import com.me2me.content.model.Content;
import com.me2me.content.model.ContentLikesDetails;
import com.me2me.content.service.ContentService;
import com.me2me.sms.service.JPushService;
import com.me2me.user.dto.ModifyUserCoinDto;
import com.me2me.user.rule.CoinRule;
import com.me2me.user.rule.Rules;
import com.me2me.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/7
 * Time :13:43
 */
@Slf4j
public class AbstractLikes {

    @Autowired
    protected ContentService contentService;

    @Autowired
    private JPushService jPushService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ContentMybatisDao contentMybatisDao;


    public Response likes(LikeDto likeDto) {
        log.info(" AbstractLikes likes start ...");
        Content content = contentService.getContentById(likeDto.getCid());
        if(content == null){
            log.info("content likes not exists");
            return Response.failure(ResponseStatus.CONTENT_LIKES_ERROR.status,ResponseStatus.CONTENT_LIKES_ERROR.message);
        }else{
            ContentLikesDetails contentLikesDetails = new ContentLikesDetails();
            contentLikesDetails.setUid(likeDto.getUid());
            contentLikesDetails.setCid(likeDto.getCid());
            //点赞
            if(likeDto.getAction() == Specification.IsLike.LIKE.index){
//                content.setLikeCount(content.getLikeCount() + 1);
//                contentService.updateContentById(content);
            	//疯狂点赞的时候会出问题。。所以改成直接数据库去保证原子
            	contentService.addContentLikeByCid(content.getId(), 1);
                contentService.createContentLikesDetails(contentLikesDetails);
                if(likeDto.getUid() != content.getUid()&&content.getType()!=Specification.ArticleType.LIVE.index) {
                    contentService.remind(content, likeDto.getUid(), Specification.UserNoticeType.LIKE.index, null);
                    log.info("content like push success");

                }
                log.info("content like success");
                //monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index,Specification.MonitorAction.LIKE.index,0,likeDto.getUid()));
//                log.info("content like monitor success");
                //自己的和王国的点赞不推送了，防止王国疯狂点赞，导致流量默默的没了。。
//                if(likeDto.getUid()!=content.getUid() && content.getType()!=Specification.ArticleType.LIVE.index) {
//                    JsonObject jsonObject = new JsonObject();
//                    jsonObject.addProperty("messageType", Specification.PushMessageType.LIKE.index);
//                    jsonObject.addProperty("type", Specification.PushObjectType.UGC.index);
//                    jsonObject.addProperty("cid", likeDto.getCid());
//                    String alias = String.valueOf(content.getUid());
//                    userService.pushWithExtra(alias, jsonObject.toString(), null);
//                }
           /*     CoinRule coinRule =  userService.getCoinRules().get(Rules.LIKES_UGC_KEY);
                coinRule.setExt(content.getId());
                ModifyUserCoinDto modifyUserCoinDto = userService.coinRule(likeDto.getUid(), coinRule);
                Response response = Response.success(ResponseStatus.LIKE_SUCCESS.status,ResponseStatus.LIKE_SUCCESS.message);
                response.setData(modifyUserCoinDto);*/
                return Response.success(ResponseStatus.LIKE_SUCCESS.status,ResponseStatus.LIKE_SUCCESS.message);
            }else{
            	ContentLikesDetails details = contentService.getContentLikesDetails(contentLikesDetails);
                if(details == null) {
                    Response.success(ResponseStatus.CONTENT_USER_LIKES_CANCEL_ALREADY.status,ResponseStatus.CONTENT_USER_LIKES_CANCEL_ALREADY.message);
                }else {
                    if ((content.getLikeCount() - 1) < 0) {
                        content.setLikeCount(0);
                    } else {
                        content.setLikeCount(content.getLikeCount() - 1);
                    }
                    contentMybatisDao.updateContentById(content);

                    contentService.deleteContentLikesDetails(contentLikesDetails);

                    log.info("cancel like success");
                }
                //monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index,Specification.MonitorAction.UN_LIKE.index,0,likeDto.getUid()));
                log.info("cancel content like monitor success");
                return Response.success(ResponseStatus.CONTENT_USER_CANCEL_LIKES_SUCCESS.status,ResponseStatus.CONTENT_USER_CANCEL_LIKES_SUCCESS.message);
            }
        }
    }
}
