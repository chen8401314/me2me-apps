package com.me2me.web;

import com.me2me.cache.service.CacheService;
import com.me2me.common.web.Response;
import com.me2me.live.dto.*;
import com.me2me.live.service.LiveService;
import com.me2me.web.dto.WxUser;
import com.me2me.web.request.MobileLiveDetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

    @Autowired
    private CacheService cacheService;


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

    /**
     * 微信授权
     * @param id
     * @return
     */
    @RequestMapping(value = "/wxOauth")
    public String wxOauth(String id){
        // 过期时间为5分钟
        cacheService.setex("wxOauth:",id,600);
        String api = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx06b8675378eb1a62&redirect_uri=https://webapp.me-to-me.com/wx_login_callback&response_type=code&scope=snsapi_login&state="+id+"#wechat_redirect";
        return "redirect:"+api;
    }
//
//    @RequestMapping(value = "/wx_login_callback" )
//    public void wxLoginCallback(HttpServletResponse response , HttpServletRequest request , HttpSession session) throws IOException {
//        Integer id= (Integer) session.getAttribute("liveDetailId");
//        if(id==null){
//            id=Integer.parseInt(request.getParameter("state"));
//        }
//        boolean loginResult = false;
//        //登录微信
//        try {
//            //未拆分的微信请求
//            String code = request.getParameter("code");
//
//      /*      String s1 = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+WxParam.APPID+"&secret="+ WxParam.SECRET+"&code="+code+"&grant_type=authorization_code";
//            JSONObject rst =JSON.parseObject(Request.Get(s1).execute().returnContent().asString());
//
//            if(rst.containsKey("errcode")) {
//                throw new RuntimeException("登录微信遇到错误:"+rst);
//            }
//
//            //取得返回的openid和token去请求用户信息
//            String userInfoUrl = "https://api.weixin.qq.com/sns/userinfo?" +"access_token="+rst.get("access_token")+"&openid="+rst.get("openid");
//            String userInfoUrlResult =new String(Request.Get(userInfoUrl).execute().returnContent().asBytes(),"utf-8");*/
//
//            SecurityWebController web = new SecurityWebController();
//            HttpPost post = new HttpPost(ApiUrl+"/api/io/getUserInfo");
//            //公参(uid token security)每次请求进来重新生成security
//            String security = web.getSecurity();
//            org.json.JSONObject jsonObject = web.getApiJson5(request,post,security);
//
//            WxUser wxUser =JSON.parseObject(jsonObject.toString(), WxUser.class);
//            wxUser.setAccess_token((String) jsonObject.get("access_token"));
//            Log.debug("weixin user logined:"+wxUser);
//           /* String headimgurl= wxUser.getHeadimgurl();
//            if(!StringUtils.isEmpty(headimgurl)) {
//                //获取到七牛key返回给前台
//                String QnKey = getQNImageKey(headimgurl);
//                wxUser.setHeadimgurl(QnKey);
//            }else{
//            	wxUser.setHeadimgurl("default.jpg");
//            }*/
//            // 登录me-to-me
//            loginResult= me2meLogin(wxUser,request);
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        String retUrl =  "https://webapp.me-to-me.com/ld/"+id;
//        if(!loginResult){
//            retUrl+="?err=1";
//        }
//        response.sendRedirect(retUrl);
//    }

//    private boolean me2meLogin(WxUser wxUser, HttpServletRequest request){
//        try{
//            HttpSession session = request.getSession();
//            Map<String,String> params = new HashMap<String, String>();
//            params.put("thirdPartOpenId" , wxUser.getOpenid());
//            params.put("thirdPartToken" , wxUser.getAccess_token());
//            params.put("avatar" , wxUser.getHeadimgurl());
//            params.put("thirdPartType" , "2");
//            params.put("nickName" , wxUser.getNickname());
//            //微信用户信息性别定义：0未知，1男，2女
//            //米汤内用户性别定义：0女（默认） 1男
//            if(null != wxUser.getSex() && "1".equals(wxUser.getSex())){
//                params.put("gender" , "1");
//            }else{
//                params.put("gender" , "0");
//            }
//
//            params.put("unionId" ,  wxUser.getUnionid());
//            params.put("h5type" , "1");
//
//
//            //第三方登录接口未完成对接
//            JSONObject result = executPostApi(this.ApiUrl+"/api/user/thirdPartLogin",params);
//            if(result.get("code").toString().equals("20062") || result.get("code").toString().equals("2001")){
//                //取出uid token设置session中
//                JSONObject s = result.getJSONObject("data");
//                Long uid = s.getLong("uid");
//                String token = s.getString("token");
//                params.clear();
//                params.put("uid",uid+"");
//                params.put("token", token);
//                // 获取userprofile
//                JSONObject obj = executPostApi(ApiUrl+"/api/user/getUserProfile",params);
//                // check userinfo
//                Object data = obj.get("data");
//                if(data instanceof java.util.Map){
//                    if(((java.util.Map) data).get("nickName")!=null){
//                        session.setAttribute("userProfile", JSON.toJSONString(obj.get("data")));
//                        session.setAttribute("uid",uid);
//                        session.setAttribute("token",token);
//                    }else{
//                        Log.error("未获取到用户[{}]昵称", uid);
//                    }
//                }
//                Log.debug("user logined."+obj);
//            }else{
//                // 出错了。
//                throw new RuntimeException("第三方登录出错："+result);
//            }
//
//        }catch(Exception e){
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }




}
