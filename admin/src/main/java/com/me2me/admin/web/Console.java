package com.me2me.admin.web;

import com.me2me.activity.dto.ActivityH5Dto;
import com.me2me.activity.service.ActivityService;
import com.me2me.admin.web.request.ContentForwardRequest;
import com.me2me.admin.web.request.RegisterRequest;
import com.me2me.common.web.Response;
import com.me2me.common.web.Specification;
import com.me2me.content.dto.ContentH5Dto;
import com.me2me.content.service.ContentService;
import com.me2me.live.service.LiveService;
import com.me2me.sms.dto.VerifyDto;
import com.me2me.user.dto.UserProfile4H5Dto;
import com.me2me.user.dto.UserRefereeSignUpDto;
import com.me2me.user.service.UserService;
import com.plusnet.common.util.StringEscapeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/25.
 */
@RequestMapping("/console")
@Controller
public class Console  {

    @Autowired
    private ContentService contentService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/data")
    public ModelAndView data(){
        ModelAndView mv = new ModelAndView("data");
        List<String> list = new ArrayList<String>();
        for(int i = 0;i<100; i++){
            list.add(i+"");
        }
        mv.addObject("data",list);
        return mv;
    }

    @RequestMapping(value = "/{viewName}")
    public String publish(@PathVariable("viewName") String viewName){
        return viewName;
    }

    @RequestMapping(value = "/forward")
    public ModelAndView forward(ContentForwardRequest request){
        ModelAndView mv = new ModelAndView("forward");
        ContentH5Dto content = contentService.contentH5(request.getId());
        if(content!=null) {
            if (content.getType() != Specification.ArticleType.EDITOR.index) {
                // 处理特殊字符
                String cx = content.getContent();
                cx = StringEscapeUtil.escapeHtml(cx);
                cx = cx.replace("\n", "<br/>");
                cx = cx.replaceAll("http://cdn\\.me-to-me\\.com", "//cdn.me-to-me.com");
                cx = cx.replaceAll("http://7xs9q4\\.com1\\.z0\\.glb\\.clouddn\\.com", "//cdn.me-to-me.com");
                content.setContent(cx);
                
                String img = content.getCoverImage();
                if(null != img && img.startsWith("http://") && img.length() > 7){
                	content.setCoverImage(img.substring(5));
                }
            }
            
            mv.addObject("root",content);
            mv.addObject("share",request.getShared());
        }else{
            mv.setViewName("error");
        }
        return mv;
    }

    @RequestMapping(value = "/activity_detail")
    public ModelAndView activity_detail(ContentForwardRequest request){
        ModelAndView mv = new ModelAndView("activity_detail");
        ActivityH5Dto content = activityService.getActivityH5(request.getId());
        if(content!=null) {
            mv.addObject("root",content);
            mv.addObject("share",request.getShared());
        }else{
            mv.setViewName("error");
        }
        return mv;
    }

    @RequestMapping(value = "/reg_web")
    public ModelAndView reg_web(RegisterRequest request){
        ModelAndView mv = new ModelAndView("reg_web");
        UserProfile4H5Dto content = userService.getUserProfile4H5(request.getUid());
        if(content!=null) {
            mv.addObject("userProfile",content);
        }else{
            mv.setViewName("error");
        }
        return mv;
    }

    @RequestMapping(value = "/signUp")
    @ResponseBody
    public Response signUp(RegisterRequest request, HttpServletRequest httpServletRequest){
        UserRefereeSignUpDto dto = new UserRefereeSignUpDto();
        dto.setMobile(request.getMobile());
        dto.setEncrypt(request.getEncrypt());
        dto.setRefereeUid(request.getUid());
        dto.setNickName(request.getNickName());
        String code = request.getCode();

        // 服务端校验逻辑
        VerifyDto verifyDto = new VerifyDto();
        verifyDto.setMobile(request.getMobile());
        verifyDto.setAction(1);
        verifyDto.setVerifyCode(code);
        if(!(verify(verifyDto).getCode()+"").startsWith("200")){
            return Response.failure(500,"验证码错误!");
        }

        Response response =  userService.refereeSignUp(dto);
        String status = response.getCode()+"";
        if(status.startsWith("200")){
            String value = httpServletRequest.getHeader("User-Agent");
            if(value.contains("iPhone")){
                return Response.success(200,"iphone");
            }else{
                return Response.success(200,"android");
            }
        }else{
            return Response.failure(500,response.getMessage());
        }

    }

    @RequestMapping(value = "/verify")
    @ResponseBody
    public Response verify(VerifyDto verifyDto){
        return userService.verify(verifyDto);
    }
    @RequestMapping(value = "/share_live")
    @ResponseBody
    public Response shareLive(VerifyDto verifyDto){
        // return liveService.verify(verifyDto);
        return null;
    }

}
