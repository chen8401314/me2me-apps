package com.me2me.mgmt.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.me2me.activity.dto.CreateActivityDto;
import com.me2me.activity.dto.CreateActivityNoticeDto;
import com.me2me.activity.dto.ShowActivityDto;
import com.me2me.activity.service.ActivityService;
import com.me2me.common.security.SecurityUtils;
import com.me2me.common.utils.DateUtil;
import com.me2me.common.web.Response;
import com.me2me.content.service.ContentService;
import com.me2me.io.service.FileTransferService;
import com.me2me.mgmt.dal.entity.MgmtUser;
import com.me2me.mgmt.manager.MgmtUserManager;
import com.me2me.mgmt.request.CreateActivityNoticeRequest;
import com.me2me.mgmt.request.CreateActivityRequest;
import com.me2me.mgmt.request.KeywordPageRequest;
import com.me2me.mgmt.syslog.SystemControllerLog;
import com.plusnet.sso.api.vo.SSOUser;
import com.plusnet.sso.client.utils.AuthTool;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by 王一武 on 2016/9/20.
 */
@Controller
@RequestMapping("/activity")
public class ActivityController {
	
	private static final Logger logger = LoggerFactory.getLogger(ActivityController.class);
	
	@Autowired
    private ActivityService activityService;
	@Autowired
	private FileTransferService fileTransferService;
	@Autowired
	private MgmtUserManager mgmtUserManager;
	@Autowired
    private ContentService contentService;

    @SuppressWarnings("rawtypes")
	@RequestMapping(value="/query")
    @SystemControllerLog(description = "活动列表查询")
    public ModelAndView query(KeywordPageRequest request){
    	ModelAndView view = new ModelAndView("article/activityList");
    	Response resp = activityService.showActivity(1, 100, request.getKeyword());
        if(null != resp && resp.getCode() == 200){
        	ShowActivityDto data = (ShowActivityDto)resp.getData();
        	Map<String, String> appUid2MgmtNameMap = new HashMap<String, String>();
        	if(null != data && null !=data.getResult() && data.getResult().size() > 0){
        		List<String> appUidList = new ArrayList<String>();
        		String appUid = null;
        		for(ShowActivityDto.ActivityElement e : data.getResult()){
        			appUid = String.valueOf(e.getUid());
        			if(!appUidList.contains(appUid)){
        				appUidList.add(appUid);
        			}
        		}
        		
        		if(appUidList.size() > 0){
        			List<MgmtUser> list = mgmtUserManager.getListByAppUids(appUidList);
        			if(null != list && list.size() > 0){
        				for(MgmtUser u : list){
        					if(null != u){
        						appUid2MgmtNameMap.put(u.getAppUid(), u.getName());
        					}
        				}
        			}
        		}
        		for(ShowActivityDto.ActivityElement e : data.getResult()){
        			appUid = String.valueOf(e.getUid());
        			if(StringUtils.isBlank(appUid2MgmtNameMap.get(appUid))){
        				appUid2MgmtNameMap.put(appUid, "未知");
        			}
        		}
        	}
        	view.addObject("userMap", appUid2MgmtNameMap);
        	view.addObject("dataObj",data);
        }
        return view;
    }
    
    @SuppressWarnings("rawtypes")
	@RequestMapping(value="/create", method=RequestMethod.POST)
    @SystemControllerLog(description = "活动创建")
    public ModelAndView create(CreateActivityRequest request, HttpServletRequest req){
    	ModelAndView view = null;
    	try{
    		SSOUser user = AuthTool.getSessionUser(req);
    		if(null == user){
    			view = new ModelAndView("article/activityNew");
    			view.addObject("errMsg", "登陆用户已失效，请重新登陆");
    			return view;
    		}
    		
    		MgmtUser mUser = mgmtUserManager.getByUuid(String.valueOf(user.getUserId()));
    		if(null == mUser){
    			view = new ModelAndView("article/activityNew");
    			view.addObject("errMsg", "当前登陆用户未和APP前台用户绑定，不能进行活动创建操作");
    			return view;
    		}
    		
    		if(null == request.getFUCoverImg() || request.getFUCoverImg().getSize() <= 0){
    			view = new ModelAndView("article/activityNew");
    			view.addObject("errMsg", "封面必须上传");
    			return view;
    		}
    		
    		String imgName = SecurityUtils.md5(req.getSession().getId()+System.currentTimeMillis(), "1");
    		fileTransferService.upload(request.getFUCoverImg().getBytes(), imgName);
    		
    		CreateActivityDto createActivityDto = new CreateActivityDto();
    		createActivityDto.setUid(Long.valueOf(mUser.getAppUid()));
    		createActivityDto.setIssue(request.getTxtIssue());
    		createActivityDto.setContent(request.getTxtContent());
    		createActivityDto.setCover(imgName);
    		createActivityDto.setTitle(request.getTxtTitle());
    		createActivityDto.setHashTitle("#"+request.getTxtHashTitle()+"#");
    		createActivityDto.setStartTime(DateUtil.string2date(request.getTxtStarTime()+":18", "yyyy-MM-dd HH:mm:ss"));
    		createActivityDto.setEndTime(DateUtil.string2date(request.getTxtEndTime()+":18", "yyyy-MM-dd HH:mm:ss"));
    		Response response = activityService.createActivity(createActivityDto);
    		if(null != response && response.getCode() == 200){
    			view = new ModelAndView("redirect:/activity/query");
    		}else{
    			view = new ModelAndView("article/activityNew");
    			view.addObject("errMsg", "活动创建失败");
    		}
    	}catch(Exception e){
    		logger.error("保存活动失败", e);
    		view = new ModelAndView("article/activityNew");
    		view.addObject("errMsg", "系统异常");
    	}
    	
    	return view;
    }
    
    @RequestMapping(value="/option")
    @SystemControllerLog(description = "活动上下架操作")
    public ModelAndView option(HttpServletRequest req){
    	int optionAction = 2;
    	int action = Integer.valueOf(req.getParameter("a"));
    	long activityId = Long.valueOf(req.getParameter("i"));
    	contentService.option(activityId, optionAction, action);
    	ModelAndView view = new ModelAndView("redirect:/activity/query");
    	return view;
    }
    
	@RequestMapping(value="/notice/create", method=RequestMethod.POST)
	@SystemControllerLog(description = "添加活动公告")
    public ModelAndView createNotice(CreateActivityNoticeRequest request, HttpServletRequest req){
		ModelAndView view = null;
		try{
			String imgName = SecurityUtils.md5(req.getSession().getId()+System.currentTimeMillis(), "1");
    		fileTransferService.upload(request.getFUCoverImg().getBytes(), imgName);
			
			CreateActivityNoticeDto createActivityNoticeDto = new CreateActivityNoticeDto();
	        createActivityNoticeDto.setId(request.getActivityId());
	        createActivityNoticeDto.setActivityNoticeCover(imgName);
	        createActivityNoticeDto.setActivityResult(request.getTxtContent());
	        createActivityNoticeDto.setActivityNoticeTitle(request.getTxtTitle());
	        activityService.createActivityNotice(createActivityNoticeDto);
	        
	        view = new ModelAndView("redirect:/activity/query");
		}catch(Exception e){
			logger.error("保存活动公告失败", e);
			view = new ModelAndView("article/activityNoticeNew");
			view.addObject("errMsg", "系统异常");
			view.addObject("aid", request.getActivityId());
		}
		
		return view;
    }
}
