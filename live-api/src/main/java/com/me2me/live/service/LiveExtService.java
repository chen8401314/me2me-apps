package com.me2me.live.service;

import java.util.List;
import java.util.Map;

import com.me2me.common.page.PageBean;
import com.me2me.common.web.Response;
import com.me2me.content.dto.ContentDto;
import com.me2me.live.dto.AggregationOptDto;
import com.me2me.live.dto.CreateKingdomDto;
import com.me2me.live.dto.CreateLiveDto;
import com.me2me.live.dto.CreateVoteDto;
import com.me2me.live.dto.GetLiveDetailDto;
import com.me2me.live.dto.GetLiveTimeLineDto;
import com.me2me.live.dto.GetLiveTimeLineDto2;
import com.me2me.live.dto.GetLiveUpdateDto;
import com.me2me.live.dto.KingdomSearchDTO;
import com.me2me.live.dto.Live4H5Dto;
import com.me2me.live.dto.LiveBarrageDto;
import com.me2me.live.dto.SearchDropAroundTopicDto;
import com.me2me.live.dto.SearchTopicDto;
import com.me2me.live.dto.SettingModifyDto;
import com.me2me.live.dto.SpeakDto;
import com.me2me.live.dto.StealResultDto;
import com.me2me.live.dto.TestApiDto;
import com.me2me.live.dto.UserAtListDTO;
import com.me2me.live.model.GiftInfo;
import com.me2me.live.model.LiveFavorite;
import com.me2me.live.model.LotteryInfo;
import com.me2me.live.model.QuotationInfo;
import com.me2me.live.model.RobotInfo;
import com.me2me.live.model.TeaseInfo;
import com.me2me.live.model.Topic;
import com.me2me.live.model.TopicDroparound;
import com.me2me.live.model.TopicFragmentTemplate;
import com.me2me.live.model.TopicFragmentWithBLOBs;
import com.me2me.live.model.TopicListed;
import com.me2me.live.model.TopicPriceSubsidyConfig;
import com.me2me.live.model.TopicTag;
import com.me2me.live.model.TopicTagDetail;
import com.me2me.live.model.TopicUserConfig;
import com.me2me.user.dto.RechargeToKingdomDto;

/**
 * liveService 太大了，这个是拆分扩展方法。
 * @author zhangjiwei
 * @date Sep 19, 2017
 */
public interface LiveExtService {
}
