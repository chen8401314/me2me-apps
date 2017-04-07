package com.me2me.web;

import com.me2me.common.web.Response;
import com.me2me.live.dto.*;
import com.me2me.live.service.LiveService;
import com.me2me.web.request.MobileLiveDetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by pc329 on 2017/4/5.
 */
@Controller
@RequestMapping("/api/mobile")
@CrossOrigin(origins = "*")
public class Mobile extends BaseController {


    private static final long DEFAULT_UID = 100;

    @Autowired
    private LiveService liveService;


    @RequestMapping(value = "/live-cover")
    @ResponseBody
    public Response liveCover(MobileLiveDetailRequest request){
        return liveService.liveCover(request.getTopicId(),DEFAULT_UID);
    }

    @RequestMapping(value = "/showLiveDetails")
    @ResponseBody
    public Response showLiveDetails(MobileLiveDetailRequest request){

        int offset = request.getOffset()==0?50:request.getOffset();
        int pageNo = request.getPageNo()==0?1:request.getPageNo();

        // 获取王国基本信息
        Response response = liveService.getLiveByCid(request.getTopicId(), DEFAULT_UID);
        ShowLiveDto showLiveDto = (ShowLiveDto) response.getData();
        // 总页数
        GetLiveUpdateDto getLiveUpdateDto = new GetLiveUpdateDto();
        getLiveUpdateDto.setOffset(offset);
        getLiveUpdateDto.setSinceId(request.getSinceId());
        getLiveUpdateDto.setTopicId(request.getTopicId());
        LiveUpdateDto pagination  = (LiveUpdateDto) liveService.getLiveUpdate(getLiveUpdateDto).getData();
        // 分页取出数据
        MobileLiveDetailsDto mobileLiveDetailsDto = new MobileLiveDetailsDto();
        mobileLiveDetailsDto.setLiveBasicData(showLiveDto);
        mobileLiveDetailsDto.setLivePaginationData(pagination);
        GetLiveDetailDto getLiveDetailDto = new GetLiveDetailDto();
        getLiveDetailDto.setTopicId(request.getTopicId());
        getLiveDetailDto.setSinceId(request.getSinceId());
        getLiveDetailDto.setDirection(request.getDirection());
        getLiveDetailDto.setPageNo(pageNo);
        getLiveDetailDto.setOffset(offset);
        getLiveDetailDto.setUid(DEFAULT_UID);
        LiveDetailDto liveDetailDto = (LiveDetailDto) liveService.getLiveDetail(getLiveDetailDto).getData();
        mobileLiveDetailsDto.setLiveDetailData(liveDetailDto);
        return Response.success(mobileLiveDetailsDto);
    }




}
