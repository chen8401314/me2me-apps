package com.me2me.mgmt.controller;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.me2me.activity.dto.LuckStatusDTO;
import com.me2me.activity.dto.ShowLuckActStatDTO;
import com.me2me.activity.dto.ShowLuckActsDTO;
import com.me2me.activity.dto.ShowLuckPrizeDTO;
import com.me2me.activity.dto.ShowLuckStatusDTO;
import com.me2me.activity.dto.ShowLuckWinnersDTO;
import com.me2me.activity.service.ActivityService;
import com.me2me.common.utils.DateUtil;
import com.me2me.common.web.Response;
import com.me2me.mgmt.request.LotteryDTO;
import com.me2me.mgmt.request.LotteryOptDTO;
import com.me2me.mgmt.request.LotteryPrizeQueryDTO;
import com.me2me.mgmt.request.LotteryQueryDTO;
import com.me2me.mgmt.request.LotteryStatusQueryDTO;
import com.me2me.mgmt.syslog.SystemControllerLog;

@Controller
@RequestMapping("/lottery")
public class LotteryController {

	@Autowired
    private ActivityService activityService;
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/winnerQuery")
	@SystemControllerLog(description = "抽奖活动获奖用户查询查询")
	public ModelAndView winnerQuery(LotteryOptDTO dto) {
		ModelAndView view = new ModelAndView("lottery/winnerList");
		if(dto.getActive() <= 0){
			dto.setActive(1);//默认为小米活动
		}
		
		Response resp = activityService.getWinners(dto.getActive());
		if(null != resp && resp.getCode() == 200 && null != resp.getData()){
			ShowLuckActsDTO data = (ShowLuckActsDTO)resp.getData();
			if(null != data.getResult() && data.getResult().size() > 0){
				Map<String, ShowLuckWinnersDTO.LuckWinnersElement> map = new HashMap<String, ShowLuckWinnersDTO.LuckWinnersElement>();
				Response resp2 = activityService.getWinnersCommitInfo(dto.getActive());
				if(null != resp2 && resp2.getCode() == 200 && null != resp2.getData()){
					ShowLuckWinnersDTO wdto = (ShowLuckWinnersDTO)resp2.getData();
					if(null != wdto.getResult() && wdto.getResult().size() > 0){
						for(ShowLuckWinnersDTO.LuckWinnersElement e : wdto.getResult()){
							map.put(String.valueOf(e.getUid()), e);
						}
					}
				}
				ShowLuckWinnersDTO.LuckWinnersElement lw = null;
				for(ShowLuckActsDTO.LuckActElement e : data.getResult()){
					lw = map.get(String.valueOf(e.getUid()));
					if(null != lw){
						e.setMobile(lw.getMobile());
					}else{
						e.setMobile("未提交");
					}
				}
			}
			dto.setData(data);
		}
		view.addObject("dataObj",dto);
		
		return view;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/statusStatQuery")
	@SystemControllerLog(description = "抽奖状态查询")
	public ModelAndView statusStatQuery(LotteryStatusQueryDTO dto){
		ModelAndView view = new ModelAndView("lottery/statusStat");
		if(dto.getActive() <= 0){
			dto.setActive(1);//默认为小米活动
		}
		Response resp = activityService.getLuckActStatList(dto.getActive());
		if(null != resp && resp.getCode() == 200 && null != resp.getData()){
			ShowLuckActStatDTO slasDTO = (ShowLuckActStatDTO) resp.getData();
			if(null != slasDTO.getResult() && slasDTO.getResult().size() > 0){
				for(ShowLuckActStatDTO.LuckActStatElement e : slasDTO.getResult()){
					if(StringUtils.isNotBlank(e.getPrizeNames())){//奖品分行展示
						e.setPrizeNames(e.getPrizeNames().replaceAll(";", "<br/>"));
					}
				}
			}
			dto.setData(slasDTO);
		}
		view.addObject("dataObj",dto);
		return view;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/query")
	@SystemControllerLog(description = "抽奖活动查询")
	public ModelAndView query(LotteryQueryDTO dto){
		ModelAndView view = new ModelAndView("lottery/lotteryList");
		if(dto.getActive() < 0){
			dto.setActive(0);//默认为小米活动
		}
		Response resp = activityService.getAwardStatusList(dto.getActive());
		if(null != resp && resp.getCode() == 200 && null != resp.getData()){
			ShowLuckStatusDTO slsdto = (ShowLuckStatusDTO)resp.getData();
			dto.setData(slsdto);
		}
		view.addObject("dataObj",dto);
		return view;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/find/{id}")
	public ModelAndView find(@PathVariable int id){
		ModelAndView view = new ModelAndView("lottery/lotteryEdit");
		
		Response resp = activityService.getLuckStatusById(id);
		if(null != resp && resp.getCode() == 200 && null != resp.getData()){
			LuckStatusDTO lsdto = (LuckStatusDTO)resp.getData();
			LotteryDTO dto = new LotteryDTO();
			dto.setActivityName(lsdto.getActivityName());
			dto.setActivityNameStr(lsdto.getActivityNameStr());
			dto.setAwardStatus(lsdto.getAwardStatus());
			dto.setAwardSumChance(lsdto.getAwardSumChance());
			dto.setChannel(lsdto.getChannel());
			dto.setEndTime(DateUtil.date2string(lsdto.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
			dto.setId(lsdto.getId());
			dto.setOperateMobile(lsdto.getOperateMobile());
			dto.setStartTime(DateUtil.date2string(lsdto.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
			dto.setVersion(lsdto.getVersion());
			
			view.addObject("dataObj",dto);
		}
		
		return view;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/editSave")
	@SystemControllerLog(description = "抽奖活动更新")
	public ModelAndView editSave(LotteryDTO dto) throws ParseException{
		ModelAndView view = null;
		if(dto.getId() <= 0){
			view = new ModelAndView("lottery/lotteryEdit");
			view.addObject("dataObj",dto);
			view.addObject("errMsg","保存失败");
			return view;
		}
		if(StringUtils.isBlank(dto.getChannel()) || StringUtils.isBlank(dto.getEndTime())
				|| StringUtils.isBlank(dto.getOperateMobile())
				|| StringUtils.isBlank(dto.getStartTime())
				|| StringUtils.isBlank(dto.getVersion())
				|| dto.getAwardSumChance() == 0){
			view = new ModelAndView("lottery/lotteryEdit");
			view.addObject("dataObj",dto);
			view.addObject("errMsg","参数缺失");
			return view;
		}
		
		Response resp = activityService.getLuckStatusById(dto.getId());
		LuckStatusDTO lsdto = null;
		if(null != resp && resp.getCode() == 200 && null != resp.getData()){
			lsdto = (LuckStatusDTO)resp.getData();
		}
		
		if(null == lsdto){
			view = new ModelAndView("lottery/lotteryEdit");
			view.addObject("dataObj",dto);
			view.addObject("errMsg","保存失败，数据不存在");
			return view;
		}
		
		lsdto.setChannel(dto.getChannel());
		lsdto.setVersion(dto.getVersion());
		lsdto.setStartTime(DateUtil.string2date(dto.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
		lsdto.setEndTime(DateUtil.string2date(dto.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
		lsdto.setOperateMobile(dto.getOperateMobile());
		lsdto.setAwardStatus(dto.getAwardStatus());
		lsdto.setAwardSumChance(dto.getAwardSumChance());
		
		activityService.updateLuckStatus(lsdto);
		
		view = new ModelAndView("redirect:/lottery/query");
		
		return view;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/prizeQuery")
	@SystemControllerLog(description = "抽奖活动奖品查询")
	public ModelAndView prizeQuery(LotteryPrizeQueryDTO dto){
		if(dto.getActive() <= 0){
			dto.setActive(1);
		}
		ModelAndView view = new ModelAndView("lottery/prizeList");
		
		Response resp = activityService.getLuckPrizeList(dto.getActive());
		if(null != resp && resp.getCode() == 200 && null != resp.getData()){
			ShowLuckPrizeDTO slpdto = (ShowLuckPrizeDTO)resp.getData();
			dto.setData(slpdto);
		}
		view.addObject("dataObj",dto);
		
		return view;
	}
}
