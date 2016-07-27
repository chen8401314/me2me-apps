package com.me2me.web;

import com.google.common.collect.Lists;
import com.me2me.activity.dto.CreateActivityDto;
import com.me2me.activity.dto.CreateActivityNoticeDto;
import com.me2me.activity.service.ActivityService;
import com.me2me.common.utils.CommonUtils;
import com.me2me.common.web.Response;
import com.me2me.content.dto.ContentDto;
import com.me2me.content.dto.EditorContentDto;
import com.me2me.content.dto.KingTopicDto;
import com.me2me.content.service.ContentService;
import com.me2me.user.dto.*;
import com.me2me.user.service.UserService;
import com.me2me.web.request.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/25.
 */
@Controller
@RequestMapping(value = "/api/console")
public class ApplicationConsole extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private ContentService contentService;

    @Autowired
    private ActivityService activityService;

    /**
     * 用户注册接口
     * @return
     */
    @RequestMapping(value = "/bindAccount",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response bindAccount(BindAccountRequest bindAccountRequest){
        UserSignUpDto userSignUpDto = new UserSignUpDto();
        userSignUpDto.setMobile(CommonUtils.getRandom("8",10));
        userSignUpDto.setGender(0);
        userSignUpDto.setEncrypt("123456");
        userSignUpDto.setNickName(bindAccountRequest.getNickName());
        return userService.signUp(userSignUpDto);
    }

    /**
     * 用户注册接口
     * @return
     */
    @RequestMapping(value = "/showContents",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response showContents(ShowContentsRequest showContentsRequest){
        EditorContentDto editorContentDto = new EditorContentDto();
        editorContentDto.setArticleType(showContentsRequest.getArticleType());
        editorContentDto.setPage(showContentsRequest.getPage());
        editorContentDto.setPageSize(showContentsRequest.getPageSize());
        editorContentDto.setKeyword(showContentsRequest.getKeyword());
        return contentService.showContents(editorContentDto);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    /**
     * 运营中心创建活动
     * @return
     */
    @RequestMapping(value = "/createActivity",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response createActivity(CreateActivityRequest request){
        CreateActivityDto createActivityDto = new CreateActivityDto();
        createActivityDto.setUid(request.getUid());
        createActivityDto.setIssue(request.getIssue());
        createActivityDto.setContent(request.getContent());
        createActivityDto.setCover(request.getCover());
        createActivityDto.setTitle(request.getTitle());
        createActivityDto.setHashTitle(request.getHashTitle());
        createActivityDto.setStartTime(request.getStartTime());
        createActivityDto.setEndTime(request.getEndTime());
        return activityService.createActivity(createActivityDto);
    }

    /**
     * 运营中心创建活动
     * @return
     */
    @RequestMapping(value = "/createActivityNotice",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response createActivityNotice(CreateActivityNoticeRequest request){
        CreateActivityNoticeDto createActivityNoticeDto = new CreateActivityNoticeDto();
        createActivityNoticeDto.setId(request.getId());
        createActivityNoticeDto.setActivityNoticeCover(request.getActivityNoticeCover());
        createActivityNoticeDto.setActivityResult(request.getActivityResult());
        createActivityNoticeDto.setActivityNoticeTitle(request.getActivityNoticeTitle());
        activityService.createActivityNotice(createActivityNoticeDto);
        return Response.success();
    }

    /**
     * 运营中心活动列表
     * @return
     */
    @RequestMapping(value = "/showActivity",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response showActivity(ShowActivityRequest request){
        return activityService.showActivity(request.getPage(),request.getPageSize(),request.getKeyword());
    }

    /**
     * 运营操作接口
     * @return
     */
    @RequestMapping(value = "/option",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response option(OptionRequest request){
        return contentService.option(request.getId(),request.getOptionAction(),request.getAction());
    }

    /**
     * 运营操作接口
     * @return
     */
    @RequestMapping(value = "/showDetails",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response showDetails(ShowDetailsRequest request){
        return contentService.showUGCDetails(request.getId());
    }

    /**
     * 运营操作接口
     * @return
     */
    @RequestMapping(value = "/modify",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response modify(PGCModifyRequest request){
        ContentDto contentDto = new ContentDto();
        contentDto.setUid(request.getUid());
        contentDto.setId(request.getId());
        contentDto.setContent(request.getContent());
        contentDto.setFeeling(request.getFeeling());
        contentDto.setContentType(request.getContentType());
        contentDto.setImageUrls(request.getImageUrls());
        contentDto.setType(request.getType());
        contentDto.setTitle(request.getTitle());
        contentDto.setRights(request.getRights());
        contentDto.setCoverImage(request.getCoverImage());
        contentDto.setForwardCid(request.getForwardCid());
        contentDto.setForWardUrl(request.getForwardUrl());
        contentDto.setForwardTitle(request.getForwardTitle());
        contentDto.setIsTop(request.getIsTop());
        contentDto.setAction(request.getAction());
        return contentService.modifyPGC(contentDto);
    }

    @RequestMapping(value = "/init",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response init(){
        List<String> s = Lists.newArrayList();
        s.add("Wurala");
        s.add("Deer,J");
        s.add("披萨");
        s.add("立春");
        s.add("Rob");
        s.add("andy");
        s.add("Amy");
        s.add("kiwi");
        s.add("如果我是圈美丽");
        s.add("瞄");
        s.add("千呼万唤爆出来");
        s.add("张小夫");
        s.add("木木女开");
        s.add("JuneJuneJune");
        s.add("追风");
        s.add("SunnyHuo");
        s.add("spray");
        s.add("两段三断");
        s.add("寒冷");
        s.add("hh");
        s.add("poki");
        s.add("蘑菇炒鸡蛋");
        s.add("sonoko");
        s.add("珠");
        s.add("最爱哆啦A梦");
        s.add("东方顽石");
        s.add("石榴");
        s.add("小贝");
        s.add("yummy");
        s.add("你是我的小青岛");
        s.add("一米阳光");
        s.add("洛萨");
        s.add("yellowbee");
        s.add("红尘烟");
        s.add("优小路的教师梦");
        s.add("cici");
        s.add("了了");
        s.add("小鱼DE理想");
        s.add("柔公子");
        s.add("蟹老板");
        s.add("北风");
        s.add("小福兰");
        s.add("简");
        s.add("小白菜");
        s.add("晨宝宝号大游轮");
        s.add("ssky");
        s.add("独行骷髅");
        s.add("齐天大圣");
        s.add("草木木夕洁");
        s.add("Amanda");
        s.add("克莱顿蝙蝠骑士");
        s.add("时锁");
        s.add("石头");
        s.add("多萝西君");
        s.add("Mia");
        s.add("纯粹写过");
        s.add("扮上帝的疯子");
        s.add("桃桃桃桃太郎");
        s.add("夏尹不是妞儿");
        s.add("时间差");
        s.add("铜墙铁壁的身躯");
        s.add("自由鱼儿");
        s.add("黄屎人");
        s.add("女子小骷髅来了");
        s.add("小珊哔");
        s.add("我就是我怎样啊");
        s.add("Kris是小肥皂");
        s.add("番薯");
        s.add("巧克力慕斯");
        s.add("疯狂小馒头");
        s.add("柠檬酸酸");
        s.add("Iqr");
        s.add("南瓜");
        s.add("黑鸦哦黑鸦");
        s.add("朱迪劳的扁担");
        s.add("极盗者");
        s.add("等风来");
        s.add("小猴子");
        s.add("把玩孤独");
        s.add("上弦月");
        s.add("周小呆");
        s.add("千森妹子");
        s.add("yha");
        s.add("色地sedi");
        s.add("李渣渣");
        s.add("Jenny");
        s.add("叶知秋");
        s.add("Penny");
        s.add("小瑞manna");
        s.add("在路上");
        s.add("知念");
        s.add("Betty");
        s.add("时间铺子");
        s.add("咕噜噜");
        s.add("七秒鱼");
        s.add("维维");
        s.add("Aly、小枫");
        s.add("可能是癌细胞");
        s.add("陌上花开");
        s.add("Abie");
        s.add("冰冰棒棒冰冰");
        s.add("Azad");
        s.add("飞跃");
        s.add("白夜森林");
        s.add("一片孤独的叶子");
        s.add("夏天");
        s.add("夜。");
        s.add("C");
        s.add("落小朵");
        s.add("七月未央");
        s.add("爱读书的小孩");
        s.add("自大的小丑");
        s.add("李四天");
        s.add("coco");
        s.add("傻逼晨");
        s.add("Snail");
        s.add("陈冬冬是好人");
        s.add("Dawn");
        s.add("芦苇");
        s.add("JJ－L");
        s.add("游遍世界的喵");
        s.add("征程");
        s.add("JoJo");
        s.add("MISS－兜");
        s.add("蓝森沫莓");
        s.add("浮生三日");
        s.add("shakespeat");
        s.add("夏芷素");
        s.add("小乖");
        s.add("肉肉小公举");
        s.add("西早");
        s.add("庄生晓梦");
        s.add("那个谁");
        s.add("vic");
        s.add("我有一头小斑驴");
        s.add("红鲤");
        s.add("ringer");
        s.add("十");
        s.add("浪花");
        s.add("飞翔的小狼狗");
        s.add("虫虫");
        s.add("Li历历");
        s.add("兰花");
        s.add("Ale");
        s.add("alexis");
        s.add("向日葵的温暖");
        s.add("鹿瓣");
        s.add("M.");
        s.add("鸡巴有点辣");
        s.add("溜溜");
        s.add("D.K");
        s.add("皇甫秀杰");
        s.add("白天");
        s.add("猫姑娘小妖");
        s.add("大田");
        s.add("追梦顾");
        s.add("过不一样的生活");
        s.add("Tina");
        s.add("灰太狼小姐");
        s.add("傻蛋");
        s.add("二两");
        s.add("初夏");
        s.add("早睡早起好青年");
        s.add("田不甜");
        s.add("mojito");
        s.add("朝雨");
        s.add("堽逼阿童木");
        s.add("奶酪儿");
        s.add("易先生");
        s.add("一度");
        s.add("青梅煮酒");
        s.add("年轻人");
        s.add("沙果");
        s.add("Rewind");
        s.add("毛线袜");
        s.add("朱敏之");
        s.add("Jessica");
        s.add("斌大大");
        s.add("逆旅、花开");
        s.add("梨涡浅笑");
        s.add("威猛萌妹纸");
        s.add("进无止境");
        s.add("沉默的溺死者");
        s.add("丫丫");
        s.add("雅雅");
        s.add("谷未央");
        s.add("把球给我");
        s.add("小样呵呵00");
        s.add("兜儿");
        s.add("阿童");
        s.add("灵灵");
        s.add("九爱");
        s.add("涩");
        s.add("那树");
        s.add("根号三");
        s.add("K");
        s.add("橙子皮");
        s.add("柚子姑娘");
        s.add("大大");
        s.add("骑车的胖妞");
        int i = 200;
        for(String nickName : s) {
            UserSignUpDto dto = new UserSignUpDto();
            dto.setNickName(nickName);
            int gender = nickName.length()%2==0?0:1;
            dto.setGender(gender);
            dto.setMobile("18900000"+i);
            dto.setEncrypt("123456");
            userService.signUp(dto);
            i++;
        }
        return Response.success();
    }

    /**
     * 国王直播相关数据
     * @param request
     * @return
     */
    @RequestMapping(value = "/kingTopic",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response kingTopic(TopicCountRequest request){
        KingTopicDto kingTopic = new KingTopicDto();
        kingTopic.setUid(request.getKingUid());
        kingTopic.setReviewCount(request.getReviewCount());
        kingTopic.setLikeCount(request.getLikeCount());
        kingTopic.setEndDate(request.getEndDate());
        kingTopic.setStartDate(request.getStartDate());
        kingTopic.setNickName(request.getNickName());
        return contentService.kingTopic(kingTopic);

    }

    /**
     * 国王直播相关数据
     * @param request
     * @return
     */
    @RequestMapping(value = "/promoter",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response promoter(PromoterRequest request){
        return userService.getPromoter(request.getNickName(),request.getStartDate(),request.getEndDate());
    }

    @RequestMapping(value = "/getPhoto",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response getPhoto(GetLivesRequest request){
        if(request.getSinceId() == -1){
            request.setSinceId(Long.MAX_VALUE);
        }
        return userService.getPhoto(request.getSinceId());
    }

}
