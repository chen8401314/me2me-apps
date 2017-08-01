package com.me2me.mgmt.controller;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.me2me.cache.service.CacheService;
import com.me2me.common.Constant;
import com.me2me.common.page.PageBean;
import com.me2me.common.utils.ImportExcelUtil;
import com.me2me.common.web.Response;
import com.me2me.content.service.ContentService;
import com.me2me.live.dto.SearchQuotationListDto;
import com.me2me.live.dto.SearchRobotListDto;
import com.me2me.live.model.QuotationInfo;
import com.me2me.live.service.LiveService;
import com.me2me.mgmt.task.app.QuotationDaySignTask;
import com.me2me.mgmt.vo.DatatablePage;


@Controller
@RequestMapping("/quotation")
public class QuotationController {
	
	private static final Logger logger = LoggerFactory.getLogger(QuotationController.class);
	
	@Autowired
    private LiveService liveService;
	
	@Autowired
    private CacheService cacheService;
	
	@Autowired
	private QuotationDaySignTask quotationDaySignTask;
	@Autowired
    private ContentService contentService;
	@RequestMapping(value = "/robotList")
	public String robotList(HttpServletRequest request) throws Exception {
		return "quotation/list_robot";
	}
	@ResponseBody
	@RequestMapping(value = "/ajaxLoadRobotList")
	public DatatablePage ajaxLoadRobotList(HttpServletRequest request,DatatablePage page) throws Exception {
		String nickName = request.getParameter("nickName");
		String type = request.getParameter("type");
		SearchRobotListDto dto = new SearchRobotListDto();
		PageBean pb = page.toPageBean();
		Response resp = liveService.searchRobotListPage(nickName,Integer.parseInt(type), pb.getCurrentPage(),pb.getPageSize());
		if(null != resp && resp.getCode() == 200 && null != resp.getData()){
			dto = (SearchRobotListDto)resp.getData();
		}
		page.setData(dto.getResult());
		page.setRecordsTotal(dto.getTotalRecord());
		return page;
	}
	@RequestMapping(value = "/delRobot")
	@ResponseBody
	public String delRobot(long id,HttpServletRequest mrequest) throws Exception {
		try {
			contentService.executeSql("delete from robot_info where id ="+id);
			return "1";
		} catch (Exception e) {
			return "0";
		}
	}	
	@ResponseBody
	@RequestMapping(value = "/ajaxLoadUsers")
	private DatatablePage ajaxLoadUsers(HttpServletRequest request,DatatablePage dpage) throws Exception {
		String nickName = request.getParameter("nickName");
		StringBuffer totalRecordSql =new StringBuffer();
		totalRecordSql.append("select COUNT(1) as count FROM USER u ,user_profile upf WHERE u.uid = upf.uid AND u.status=0 AND u.uid NOT IN(SELECT uid FROM robot_info)");
		if(!StringUtils.isEmpty(nickName)){
			totalRecordSql.append(" and upf.nick_name like '%").append(nickName).append("%'");
		}
		Map<String,Object> totalRecordMap= contentService.queryForMap(totalRecordSql.toString(), null);
		int totalRecord = Integer.parseInt(totalRecordMap.get("count").toString());
		PageBean pb = dpage.toPageBean();
		int page = pb.getCurrentPage();
		int pageSize = pb.getPageSize();
    	int totalPage = (totalRecord + pageSize - 1) / pageSize;
    	if(page>totalPage){
    		page=totalPage;
    	}
    	if(page<1){
    		page=1;
    	}
    	int start = (page-1)*pageSize;
    	
		StringBuffer listSql =new StringBuffer();
		listSql.append("select u.uid,upf.nick_name,upf.avatar FROM USER u ,user_profile upf WHERE u.uid = upf.uid AND u.status=0 AND u.uid NOT IN(SELECT uid FROM robot_info) ");
		if(!StringUtils.isEmpty(nickName)){
			listSql.append(" and upf.nick_name like '%").append(nickName).append("%'");
		}
		listSql.append(" order by u.create_time limit ").append(start).append(",").append(pageSize);
    	List<Map<String, Object>> list =contentService.queryEvery(listSql.toString());
    	SearchRobotListDto dto = new SearchRobotListDto();
        dto.setTotalRecord(totalRecord);
        dto.setTotalPage(totalPage);
        for(Map<String, Object> map : list){
        	SearchRobotListDto.RobotElement e = dto.createTopicListedElement();
        	 e.setUid((Long)map.get("uid"));
        	 e.setNickName( map.get("nick_name").toString());
        	 e.setAvatar(Constant.QINIU_DOMAIN + "/" +  map.get("avatar").toString());
            dto.getResult().add(e);
        }
        dpage.setData(dto.getResult());
		dpage.setRecordsTotal(dto.getTotalRecord());
        return dpage;
	}
	@RequestMapping(value = "/addRobot")
	@ResponseBody
	public String addRobot(long uid,int type,HttpServletRequest mrequest) throws Exception {
		try {
			Map<String,Object> totalRecordMap= contentService.queryForMap("select count(1) as count from robot_info where uid="+uid, null);
			int totalRecord = Integer.parseInt(totalRecordMap.get("count").toString());
			if(totalRecord>0){
				return "2";
			}else{
				contentService.executeSql("insert into robot_info(uid,type) values("+uid+","+type+")");
			}
			return "1";
		} catch (Exception e) {
			return "0";
		}
	}	
	
	@RequestMapping(value = "/quotationList")
	public String quotationList(HttpServletRequest request) throws Exception {
		return "quotation/list_quotation";
	}
	@ResponseBody
	@RequestMapping(value = "/ajaxLoadQuotationList")
	public DatatablePage ajaxLoadQuotationList(HttpServletRequest request,DatatablePage page) throws Exception {
		String quotation = request.getParameter("searchQuotation");
		SearchQuotationListDto dto = new SearchQuotationListDto();
		PageBean pb = page.toPageBean();
		Response resp = liveService.searchQuotationListPage(quotation, pb.getCurrentPage(),pb.getPageSize());
		if(null != resp && resp.getCode() == 200 && null != resp.getData()){
			dto = (SearchQuotationListDto)resp.getData();
		}
		page.setData(dto.getResult());
		page.setRecordsTotal(dto.getTotalRecord());
		return page;
	}
	@RequestMapping(value = "/addQuotation")
	@ResponseBody
	public String addQuotation(QuotationInfo quotationInfo,HttpServletRequest mrequest) throws Exception {
		try {
		    if(quotationInfo.getId()==0){
		    	liveService.saveQuotationInfo(quotationInfo);
		    }else{
		    	liveService.updateQuotationInfo(quotationInfo);
		    }
			return "1";
		} catch (Exception e) {
			return "0";
		}
	}
	@RequestMapping(value = "/delQuotation")
	@ResponseBody
	public String delQuotation(long id,HttpServletRequest mrequest) throws Exception {
		try {
			liveService.delQuotationInfo(id);
			return "1";
		} catch (Exception e) {
			return "0";
		}
	}
	@RequestMapping(value = "/getQuotation")
	@ResponseBody
	public QuotationInfo getQuotation(long id,HttpServletRequest mrequest) throws Exception {
		try {
			return liveService.getQuotationInfoById(id);
		} catch (Exception e) {
			return null;
		}
	}
    /** 
     * 描述：通过 jquery.form.js 插件提供的ajax方式上传文件 
     * @param request 
     * @param response 
     * @throws Exception 
     */  
    @RequestMapping(value="/importExcel")  
    @ResponseBody  
    public  void  importExcel(MultipartHttpServletRequest request,HttpServletResponse response) throws Exception {  
        InputStream in =null;  
        List<List<Object>> listob = null;  
        MultipartFile file = request.getFile("upfile");  
        if(file.isEmpty()){  
            throw new Exception("文件不存在！");  
        }  
          
        in = file.getInputStream();  
        listob = new ImportExcelUtil().getBankListByExcel(in,file.getOriginalFilename());  
          
        //该处可调用service相应方法进行数据保存到数据库中，现只对数据输出  
        for (int i = 1; i < listob.size(); i++) {  
            List<Object> lo = listob.get(i);  
              if(StringUtils.isEmpty(String.valueOf(lo.get(0)))){
            	  break;
              }
              try{
              QuotationInfo quotationInfo = new QuotationInfo();
              quotationInfo.setQuotation(String.valueOf(lo.get(0)));
              quotationInfo.setType(Integer.parseInt(String.valueOf(lo.get(1))));
              liveService.saveQuotationInfo(quotationInfo);
              }catch(Exception e){
            	  continue;
              }
        }  
        PrintWriter out = null;  
        response.setCharacterEncoding("utf-8");  //防止ajax接受到的中文信息乱码  
        out = response.getWriter();  
        out.print("文件导入成功！");  
        out.flush();  
        out.close();  
    } 
	@ResponseBody
	@RequestMapping(value = "/runSignQuotationPushTask")
	public String runSignQuotationPushTask(){
		quotationDaySignTask.doTask();
		return "执行完成";
	}
}
