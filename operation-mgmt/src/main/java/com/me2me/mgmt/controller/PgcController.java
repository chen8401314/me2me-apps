package com.me2me.mgmt.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.me2me.common.security.SecurityUtils;
import com.me2me.common.web.Response;
import com.me2me.content.dto.ContentDto;
import com.me2me.content.dto.EditorContentDto;
import com.me2me.content.dto.ShowContentDto;
import com.me2me.content.dto.ShowUGCDetailsDto;
import com.me2me.content.service.ContentService;
import com.me2me.io.service.FileTransferService;
import com.me2me.mgmt.dal.entity.MgmtUser;
import com.me2me.mgmt.manager.MgmtUserManager;
import com.me2me.mgmt.request.CreatePgcRequest;
import com.me2me.mgmt.request.EditPgcRequest;
import com.me2me.mgmt.request.KeywordPageRequest;
import com.plusnet.sso.api.vo.SSOUser;
import com.plusnet.sso.client.utils.AuthTool;

@Controller
@RequestMapping("/pgc")
public class PgcController {

	private static final Logger logger = LoggerFactory.getLogger(PgcController.class);
	
	@Autowired
    private ContentService contentService;
	@Autowired
	private MgmtUserManager mgmtUserManager;
	@Autowired
	private FileTransferService fileTransferService;
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/query")
    public ModelAndView query(KeywordPageRequest request){
		ModelAndView view = new ModelAndView("article/pgcList");
		EditorContentDto editorContentDto = new EditorContentDto();
        editorContentDto.setArticleType(1);//pgc
        editorContentDto.setPage(1);
        editorContentDto.setPageSize(200);
        String keyword = request.getKeyword();
        if(null == keyword){
        	keyword = "";
        }
        editorContentDto.setKeyword(keyword);
        Response resp = contentService.showContents(editorContentDto);
        if(null != resp && resp.getCode() == 200){
        	ShowContentDto data = (ShowContentDto)resp.getData();
        	view.addObject("dataObj", data);
        }
		return view;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/create", method=RequestMethod.POST)
    public ModelAndView create(CreatePgcRequest request, HttpServletRequest req){
		ModelAndView view = null;
    	try{
    		SSOUser user = AuthTool.getSessionUser(req);
    		if(null == user){
    			view = new ModelAndView("article/pgcNew");
    			view.addObject("errMsg", "登陆用户已失效，请重新登陆");
    			return view;
    		}
    		
    		MgmtUser mUser = mgmtUserManager.getByUuid(String.valueOf(user.getUserId()));
    		if(null == mUser){
    			view = new ModelAndView("article/pgcNew");
    			view.addObject("errMsg", "当前登陆用户未和APP前台用户绑定，不能进行PGC创建操作");
    			return view;
    		}
    		
    		if(null == request.getFuCoverImg() || request.getFuCoverImg().getSize() <= 0){
    			view = new ModelAndView("article/pgcNew");
    			view.addObject("errMsg", "封面必须上传");
    			return view;
    		}
    		
    		String imgName = SecurityUtils.md5(req.getSession().getId()+System.currentTimeMillis(), "1");
    		fileTransferService.upload(request.getFuCoverImg().getBytes(), imgName);
    		
    		ContentDto contentDto = new ContentDto();
            contentDto.setUid(Long.valueOf(mUser.getAppUid()));
            contentDto.setContent(request.getTxtContent());
            String tags = request.getTxtTags();
            if(StringUtils.isNotBlank(tags)){
            	tags.replaceAll(",", ";");
            }
            contentDto.setFeeling(tags);
            contentDto.setContentType(0);//默认0，文章
            contentDto.setImageUrls(imgName);
            contentDto.setType(2);//2是UGC
            contentDto.setTitle(request.getTxtTitle());
            contentDto.setRights(1);//默认公开
            contentDto.setCoverImage(imgName);
            contentDto.setForwardCid(0);
            contentDto.setForWardUrl(null);
            contentDto.setForwardTitle(null);
            Response resp = contentService.editorPublish(contentDto);
            if(null != resp && (resp.getCode() == 200 || resp.getCode() == 20014)){
            	view = new ModelAndView("redirect:/pgc/query");
            }else{
            	view = new ModelAndView("article/pgcNew");
    			view.addObject("errMsg", "PGC创建失败");
            }
    	}catch(Exception e){
    		logger.error("保存PGC失败", e);
    		view = new ModelAndView("article/pgcNew");
    		view.addObject("errMsg", "系统异常");
    	}
    	return view;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/find/{pid}")
	public ModelAndView editInit(@PathVariable long pid){
		ModelAndView view = new ModelAndView("article/pgcEdit");
		
		Response resp = contentService.showUGCDetails(pid);
		if(null != resp && resp.getCode() == 200 && null != resp.getData()){
			ShowUGCDetailsDto showUGCDetailsDto = (ShowUGCDetailsDto)resp.getData();
			EditPgcRequest r = new EditPgcRequest();
			r.setCoverImgUrl(showUGCDetailsDto.getCover());
			r.setTxtContent(showUGCDetailsDto.getContent());
			String tags = showUGCDetailsDto.getFeelings();
			if(StringUtils.isNotBlank(tags)){
				tags = tags.replaceAll(";", ",");
			}
			r.setTxtTags(tags);
			r.setTxtTitle(showUGCDetailsDto.getTitle());
			r.setPgcId(showUGCDetailsDto.getId());
			String qiniuImg = "";
			if(StringUtils.isNotBlank(showUGCDetailsDto.getCover()) 
					&& showUGCDetailsDto.getCover().startsWith("http://cdn.me-to-me.com")){
				qiniuImg = showUGCDetailsDto.getCover().substring(24);
			}
			r.setQiniuImg(qiniuImg);
			view.addObject("dataObj", r);
		}
		
		return view;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/editSave", method=RequestMethod.POST)
    public ModelAndView editSave(EditPgcRequest request, HttpServletRequest req){
		ModelAndView view = null;
    	try{
    		SSOUser user = AuthTool.getSessionUser(req);
    		if(null == user){
    			view = new ModelAndView("article/pgcEdit");
    			view.addObject("errMsg", "登陆用户已失效，请重新登陆");
    			return view;
    		}
    		
    		MgmtUser mUser = mgmtUserManager.getByUuid(String.valueOf(user.getUserId()));
    		if(null == mUser){
    			view = new ModelAndView("article/pgcNew");
    			view.addObject("errMsg", "当前登陆用户未和APP前台用户绑定，不能进行PGC修改操作");
    			return view;
    		}
    		
    		String coverImage = request.getQiniuImg();
    		if(null != request.getFuCoverImg() && request.getFuCoverImg().getSize() > 0){
    			coverImage = SecurityUtils.md5(req.getSession().getId()+System.currentTimeMillis(), "1");
        		fileTransferService.upload(request.getFuCoverImg().getBytes(), coverImage);
    		}
    		
    		ContentDto contentDto = new ContentDto();
            contentDto.setUid(Long.valueOf(mUser.getAppUid()));
            contentDto.setId(request.getPgcId());
            contentDto.setContent(request.getTxtContent());
            String tags = request.getTxtTags();
            if(StringUtils.isNotBlank(tags)){
            	tags.replaceAll(",", ";");
            }
            contentDto.setFeeling(tags);
            contentDto.setTitle(request.getTxtTitle());
            contentDto.setCoverImage(coverImage);
            contentDto.setAction(0);
            Response resp = contentService.modifyPGC(contentDto);
            if(null != resp && (resp.getCode() == 200 || resp.getCode() == 20014)){
            	view = new ModelAndView("redirect:/pgc/query");
            }else{
            	view = new ModelAndView("article/pgcEdit");
    			view.addObject("errMsg", "PGC创建失败");
            }
    	}catch(Exception e){
    		logger.error("更新PGC失败", e);
    		view = new ModelAndView("article/pgcEdit");
    		view.addObject("errMsg", "系统异常");
    	}
    	return view;
	}
	
	@RequestMapping(value="/option/hot")
	public ModelAndView optionHot(HttpServletRequest req){
		int action = Integer.valueOf(req.getParameter("a"));
    	long pgcId = Long.valueOf(req.getParameter("i"));
    	contentService.option(pgcId,1,action);
		
		ModelAndView view = new ModelAndView("redirect:/pgc/query");
    	return view;
	}
	
	@RequestMapping(value="/option/top")
	public ModelAndView optionTop(HttpServletRequest req){
		int action = Integer.valueOf(req.getParameter("a"));
    	long pgcId = Long.valueOf(req.getParameter("i"));
    	
    	ContentDto contentDto = new ContentDto();
    	contentDto.setAction(1);//设置/取消置顶
    	contentDto.setId(pgcId);
    	contentDto.setIsTop(action);
    	contentService.modifyPGC(contentDto);
		
		ModelAndView view = new ModelAndView("redirect:/pgc/query");
    	return view;
	}
}
