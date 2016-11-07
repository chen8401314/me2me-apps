package com.me2me.mgmt.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.me2me.activity.dto.ShowLuckActsDTO;
import com.me2me.activity.dto.ShowLuckWinnersDTO;
import com.me2me.activity.service.ActivityService;
import com.me2me.common.web.Response;
import com.me2me.mgmt.request.LotteryOptDTO;
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
}
