package com.me2me.mgmt.task.app;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.me2me.cache.CacheConstant;
import com.me2me.cache.service.CacheService;
import com.me2me.common.utils.DateUtil;
import com.me2me.common.web.Specification;
import com.me2me.mgmt.dao.LocalJdbcDao;

@Component
public class UserRecInitTask {

	private static final Logger logger = LoggerFactory.getLogger(UserRecInitTask.class);
	
	private static final int LIMIT_NUM = 50;//每个属性限制条数
	private static final int LIMIT_DATE = 3;//限制天数(过去多少天)
	
	@Autowired
	private LocalJdbcDao localJdbcDao;
	@Autowired
	private CacheService cacheService;
	
	private static Map<String, String> emotionMap = new HashMap<String, String>();
	
	@PostConstruct
	public void init(){
		emotionMap.put(Specification.UserRecInitType.EMOTION_CHONGSHI.type, "充实");
		emotionMap.put(Specification.UserRecInitType.EMOTION_CHONGPEI.type, "充沛");
		emotionMap.put(Specification.UserRecInitType.EMOTION_KANGFENG.type, "亢奋");
		emotionMap.put(Specification.UserRecInitType.EMOTION_TONGKUAI.type, "痛快");
		emotionMap.put(Specification.UserRecInitType.EMOTION_ZIHAO.type, "自豪");
		emotionMap.put(Specification.UserRecInitType.EMOTION_FANGSI.type, "放肆");
		emotionMap.put(Specification.UserRecInitType.EMOTION_CHAOYUE.type, "超越");
		emotionMap.put(Specification.UserRecInitType.EMOTION_RECHEN.type, "热忱");
		emotionMap.put(Specification.UserRecInitType.EMOTION_KEWANG.type, "渴望");
		emotionMap.put(Specification.UserRecInitType.EMOTION_YUKUAI.type, "愉快");
		emotionMap.put(Specification.UserRecInitType.EMOTION_ZIZAI.type, "自在");
		emotionMap.put(Specification.UserRecInitType.EMOTION_ZIXIN.type, "自信");
		emotionMap.put(Specification.UserRecInitType.EMOTION_JINGYANG.type, "敬仰");
		emotionMap.put(Specification.UserRecInitType.EMOTION_CHUNCUI.type, "纯粹");
		emotionMap.put(Specification.UserRecInitType.EMOTION_AIMU.type, "爱慕");
		emotionMap.put(Specification.UserRecInitType.EMOTION_ZHUANZHU.type, "专注");
		emotionMap.put(Specification.UserRecInitType.EMOTION_QIDAI.type, "期待");
		emotionMap.put(Specification.UserRecInitType.EMOTION_PINGHE.type, "平和");
		emotionMap.put(Specification.UserRecInitType.EMOTION_XINREN.type, "信任");
		emotionMap.put(Specification.UserRecInitType.EMOTION_HEMU.type, "和睦");
		emotionMap.put(Specification.UserRecInitType.EMOTION_SHIRAN.type, "释然");
		emotionMap.put(Specification.UserRecInitType.EMOTION_MANGLU.type, "忙碌");
		emotionMap.put(Specification.UserRecInitType.EMOTION_XINDONG.type, "心动");
		emotionMap.put(Specification.UserRecInitType.EMOTION_BUAN.type, "不安");
		emotionMap.put(Specification.UserRecInitType.EMOTION_PINGJING.type, "平静");
		emotionMap.put(Specification.UserRecInitType.EMOTION_QINSONG.type, "轻松");
		emotionMap.put(Specification.UserRecInitType.EMOTION_FADAI.type, "发呆");
		emotionMap.put(Specification.UserRecInitType.EMOTION_YOUXIAN.type, "悠闲");
		emotionMap.put(Specification.UserRecInitType.EMOTION_ZHAOJI.type, "着急");
		emotionMap.put(Specification.UserRecInitType.EMOTION_JINZHANG.type, "紧张");
		emotionMap.put(Specification.UserRecInitType.EMOTION_YUMEN.type, "郁闷");
		emotionMap.put(Specification.UserRecInitType.EMOTION_YANJUAN.type, "厌倦");
		emotionMap.put(Specification.UserRecInitType.EMOTION_WULIAO.type, "无聊");
		emotionMap.put(Specification.UserRecInitType.EMOTION_CHOUCHANG.type, "惆怅");
		emotionMap.put(Specification.UserRecInitType.EMOTION_WUNAI.type, "无奈");
		emotionMap.put(Specification.UserRecInitType.EMOTION_JIAOLV.type, "焦虑");
		emotionMap.put(Specification.UserRecInitType.EMOTION_FANZAO.type, "烦躁");
		emotionMap.put(Specification.UserRecInitType.EMOTION_EXIN.type, "恶心");
		emotionMap.put(Specification.UserRecInitType.EMOTION_YOULV.type, "忧虑");
		emotionMap.put(Specification.UserRecInitType.EMOTION_HAIPA.type, "害怕");
		emotionMap.put(Specification.UserRecInitType.EMOTION_SHANGGAN.type, "伤感");
		emotionMap.put(Specification.UserRecInitType.EMOTION_TUIFEI.type, "颓废");
		emotionMap.put(Specification.UserRecInitType.EMOTION_KUANGZAO.type, "狂躁");
		emotionMap.put(Specification.UserRecInitType.EMOTION_YANWU.type, "厌恶");
		emotionMap.put(Specification.UserRecInitType.EMOTION_MAMU.type, "麻木");
		emotionMap.put(Specification.UserRecInitType.EMOTION_LENGMO.type, "冷漠");
		emotionMap.put(Specification.UserRecInitType.EMOTION_KONGJU.type, "恐惧");
		emotionMap.put(Specification.UserRecInitType.EMOTION_XIEQI.type, "泄气");
		emotionMap.put(Specification.UserRecInitType.EMOTION_JUEWANG.type, "绝望");
	}
	
	@Scheduled(cron="0 10 0 * * ?")
	public void doTask(){
		logger.info("用户推荐基础数据初始化任务开始");
		long s = System.currentTimeMillis();
		try{
			Date now = new Date();
			String limitDate = DateUtil.date2string(DateUtil.addDay(now, (0-LIMIT_DATE)), "yyyy-MM-dd");
			
			logger.info("1开始处理所有用户...");
			this.commonInit(limitDate);//所有用户--对应性别无限制
			logger.info("2开始处理性别...");
			this.sexInit(limitDate);//性别处理[男、女]--对应性取向
			logger.info("3开始处理年龄段...");
			this.ageInit(limitDate);//年龄段
			logger.info("4开始处理爱好...");
			this.hobbyInit(limitDate);//爱好
			logger.info("5开始处理MBTI...");
			this.mbtiInit(limitDate);//MBTI
			logger.info("6开始处理情绪...");
			this.emotionInit(limitDate);//情绪
			logger.info("7开始处理职业...");
			this.careerInit(limitDate);//职业
			
			//全部完成了，则刷新时间缓存
			String value = DateUtil.date2string(now, "yyyyMMdd");
			cacheService.set(CacheConstant.USER_REC_DATE_KEY, value);
		}catch(Exception e){
			logger.error("用户推荐基础数据初始化任务出错", e);
		}
		long e = System.currentTimeMillis();
		logger.info("用户推荐基础数据初始化任务结束，共耗时["+(e-s)/1000+"]秒");
	}
	
	private void commonInit(String limitDate){
		StringBuilder searchSql = new StringBuilder();
		searchSql.append("select u.uid,(select sum(r.coin) as coin from rule_log r");
		searchSql.append(" where r.uid=u.uid and r.create_time>='").append(limitDate);
		searchSql.append(" 00:00:00') as coin1,(select sum(s.stealed_coins) as coin");
		searchSql.append(" from user_steal_log s where s.uid=u.uid");
		searchSql.append(" and s.create_time>='").append(limitDate);
		searchSql.append(" 00:00:00') as coin2 from user_profile u");
		searchSql.append(" where u.uid in (select DISTINCT h.uid from topic_read_his h where h.create_time>'");
		searchSql.append(limitDate).append(" 00:00:00') order by (coin1+coin2) desc");
		searchSql.append(" limit ").append(LIMIT_NUM);
		
		this.genAndSave(searchSql.toString(), Specification.UserRecInitType.COMMON.type);
	}
	
	private void sexInit(String limitDate){
		logger.info("2-1 性别-男");
		StringBuilder searchSql = new StringBuilder();
		searchSql.append("select u.uid,(select sum(r.coin) as coin from rule_log r");
		searchSql.append(" where r.uid=u.uid and r.create_time>='").append(limitDate);
		searchSql.append(" 00:00:00') as coin1,(select sum(s.stealed_coins) as coin");
		searchSql.append(" from user_steal_log s where s.uid=u.uid");
		searchSql.append(" and s.create_time>='").append(limitDate);
		searchSql.append(" 00:00:00') as coin2 from user_profile u");
		searchSql.append(" where u.gender=1 and u.uid in (select DISTINCT h.uid from topic_read_his h where h.create_time>'");
		searchSql.append(limitDate).append(" 00:00:00') order by (coin1+coin2) desc");
		searchSql.append(" limit ").append(LIMIT_NUM);
		this.genAndSave(searchSql.toString(), Specification.UserRecInitType.SEX_MALE.type);
		logger.info("2-2 性别-女");
		searchSql = new StringBuilder();
		searchSql.append("select u.uid,(select sum(r.coin) as coin from rule_log r");
		searchSql.append(" where r.uid=u.uid and r.create_time>='").append(limitDate);
		searchSql.append(" 00:00:00') as coin1,(select sum(s.stealed_coins) as coin");
		searchSql.append(" from user_steal_log s where s.uid=u.uid");
		searchSql.append(" and s.create_time>='").append(limitDate);
		searchSql.append(" 00:00:00') as coin2 from user_profile u");
		searchSql.append(" where u.gender!=1 and u.uid in (select DISTINCT h.uid from topic_read_his h where h.create_time>'");
		searchSql.append(limitDate).append(" 00:00:00') order by (coin1+coin2) desc");
		searchSql.append(" limit ").append(LIMIT_NUM);
		this.genAndSave(searchSql.toString(), Specification.UserRecInitType.SEX_FEMALE.type);
	}
	
	private void ageInit(String limitDate){
		StringBuilder searchSql = new StringBuilder();
		searchSql.append("select u.uid,(select sum(r.coin) as coin from rule_log r");
		searchSql.append(" where r.uid=u.uid and r.create_time>='").append(limitDate);
		searchSql.append(" 00:00:00') as coin1,(select sum(s.stealed_coins) as coin");
		searchSql.append(" from user_steal_log s where s.uid=u.uid");
		searchSql.append(" and s.create_time>='").append(limitDate);
		searchSql.append(" 00:00:00') as coin2 from user_profile u");
		searchSql.append(" where u.age_group=#{ageGroup}# and u.uid in (select DISTINCT h.uid from topic_read_his h where h.create_time>'");
		searchSql.append(limitDate).append(" 00:00:00') order by (coin1+coin2) desc");
		searchSql.append(" limit ").append(LIMIT_NUM);

		logger.info("3-1 年龄段-00后");
		String sql = searchSql.toString().replace("#{ageGroup}#", "1");
		this.genAndSave(sql, Specification.UserRecInitType.AGE_00.type);
		logger.info("3-2年龄段-95后");
		sql = searchSql.toString().replace("#{ageGroup}#", "2");
		this.genAndSave(sql, Specification.UserRecInitType.AGE_95.type);
		logger.info("3-3 年龄段-90后");
		sql = searchSql.toString().replace("#{ageGroup}#", "3");
		this.genAndSave(sql, Specification.UserRecInitType.AGE_90.type);
		logger.info("3-4年龄段-85后");
		sql = searchSql.toString().replace("#{ageGroup}#", "4");
		this.genAndSave(sql, Specification.UserRecInitType.AGE_85.type);
		logger.info("3-5年龄段-80后");
		sql = searchSql.toString().replace("#{ageGroup}#", "5");
		this.genAndSave(sql, Specification.UserRecInitType.AGE_80.type);
		logger.info("3-6 年龄段-活化石");
		sql = searchSql.toString().replace("#{ageGroup}#", "6");
		this.genAndSave(sql, Specification.UserRecInitType.AGE_OLD.type);
	}
	
	private void hobbyInit(String limitDate){
		StringBuilder searchSql = new StringBuilder();
		searchSql.append("select u.uid,(select sum(r.coin) as coin from rule_log r");
		searchSql.append(" where r.uid=u.uid and r.create_time>='").append(limitDate);
		searchSql.append(" 00:00:00') as coin1,(select sum(s.stealed_coins) as coin");
		searchSql.append(" from user_steal_log s where s.uid=u.uid");
		searchSql.append(" and s.create_time>='").append(limitDate);
		searchSql.append(" 00:00:00') as coin2 from user_profile u,user_hobby h");
		searchSql.append(" where u.uid=h.uid and h.hobby=#{hobbtId}# and u.uid in (select DISTINCT h.uid from topic_read_his h where h.create_time>'");
		searchSql.append(limitDate).append(" 00:00:00') order by (coin1+coin2) desc");
		searchSql.append(" limit ").append(LIMIT_NUM);
		
		logger.info("4-1 爱好-旅行");
		String sql = searchSql.toString().replace("#{hobbtId}#", "96");
		this.genAndSave(sql, Specification.UserRecInitType.HOBBY_LVXING.type);
		logger.info("4-2 爱好-音乐");
		sql = searchSql.toString().replace("#{hobbtId}#", "80");
		this.genAndSave(sql, Specification.UserRecInitType.HOBBY_YINYUE.type);
		logger.info("4-3 爱好-美食");
		sql = searchSql.toString().replace("#{hobbtId}#", "81");
		this.genAndSave(sql, Specification.UserRecInitType.HOBBY_MEISHI.type);
		logger.info("4-4 爱好-动漫");
		sql = searchSql.toString().replace("#{hobbtId}#", "83");
		this.genAndSave(sql, Specification.UserRecInitType.HOBBY_DONGMAN.type);
		logger.info("4-5 爱好-美容美妆");
		sql = searchSql.toString().replace("#{hobbtId}#", "90");
		this.genAndSave(sql, Specification.UserRecInitType.HOBBY_MEIRONGMEIZHUANG.type);
		logger.info("4-6 爱好-健康养生");
		sql = searchSql.toString().replace("#{hobbtId}#", "85");
		this.genAndSave(sql, Specification.UserRecInitType.HOBBY_JIANKANGYANGSHENG.type);
		logger.info("4-7 爱好-时尚");
		sql = searchSql.toString().replace("#{hobbtId}#", "87");
		this.genAndSave(sql, Specification.UserRecInitType.HOBBY_SHISHANG.type);
		logger.info("4-8 爱好-游戏");
		sql = searchSql.toString().replace("#{hobbtId}#", "84");
		this.genAndSave(sql, Specification.UserRecInitType.HOBBY_YOUXI.type);
		logger.info("4-9 爱好-设计");
		sql = searchSql.toString().replace("#{hobbtId}#", "88");
		this.genAndSave(sql, Specification.UserRecInitType.HOBBY_SHEJI.type);
		logger.info("4-10 爱好-摄影");
		sql = searchSql.toString().replace("#{hobbtId}#", "89");
		this.genAndSave(sql, Specification.UserRecInitType.HOBBY_SHEYING.type);
		logger.info("4-11 爱好-汽车");
		sql = searchSql.toString().replace("#{hobbtId}#", "92");
		this.genAndSave(sql, Specification.UserRecInitType.HOBBY_QICHE.type);
		logger.info("4-12 爱好-娱乐");
		sql = searchSql.toString().replace("#{hobbtId}#", "97");
		this.genAndSave(sql, Specification.UserRecInitType.HOBBY_YULE.type);
		logger.info("4-13 爱好-军事");
		sql = searchSql.toString().replace("#{hobbtId}#", "93");
		this.genAndSave(sql, Specification.UserRecInitType.HOBBY_JUNSHI.type);
		logger.info("4-14 爱好-历史");
		sql = searchSql.toString().replace("#{hobbtId}#", "101");
		this.genAndSave(sql, Specification.UserRecInitType.HOBBY_LISHI.type);
		logger.info("4-15 爱好-探索");
		sql = searchSql.toString().replace("#{hobbtId}#", "102");
		this.genAndSave(sql, Specification.UserRecInitType.HOBBY_TANSUO.type);
		logger.info("4-16 爱好-热点新闻");
		sql = searchSql.toString().replace("#{hobbtId}#", "94");
		this.genAndSave(sql, Specification.UserRecInitType.HOBBY_REDIANXINWEN.type);
		logger.info("4-17 爱好-趣味");
		sql = searchSql.toString().replace("#{hobbtId}#", "104");
		this.genAndSave(sql, Specification.UserRecInitType.HOBBY_QUWEI.type);
		logger.info("4-18 爱好-居家");
		sql = searchSql.toString().replace("#{hobbtId}#", "82");
		this.genAndSave(sql, Specification.UserRecInitType.HOBBY_JUJIA.type);
		logger.info("4-19 爱好-科技");
		sql = searchSql.toString().replace("#{hobbtId}#", "91");
		this.genAndSave(sql, Specification.UserRecInitType.HOBBY_KEJI.type);
		logger.info("4-20 爱好-美文");
		sql = searchSql.toString().replace("#{hobbtId}#", "103");
		this.genAndSave(sql, Specification.UserRecInitType.HOBBY_MEIWEN.type);
		logger.info("4-21 爱好-教育");
		sql = searchSql.toString().replace("#{hobbtId}#", "98");
		this.genAndSave(sql, Specification.UserRecInitType.HOBBY_JIAOYU.type);
		logger.info("4-22 爱好-经管");
		sql = searchSql.toString().replace("#{hobbtId}#", "99");
		this.genAndSave(sql, Specification.UserRecInitType.HOBBY_JINGGUAN.type);
		logger.info("4-23 爱好-文化");
		sql = searchSql.toString().replace("#{hobbtId}#", "100");
		this.genAndSave(sql, Specification.UserRecInitType.HOBBY_WENHUA.type);
		logger.info("4-24 爱好-文体活动");
		sql = searchSql.toString().replace("#{hobbtId}#", "86");
		this.genAndSave(sql, Specification.UserRecInitType.HOBBY_WENTIHUODONG.type);
		logger.info("4-25 爱好-宠物");
		sql = searchSql.toString().replace("#{hobbtId}#", "154");
		this.genAndSave(sql, Specification.UserRecInitType.HOBBY_CHONGWU.type);
	}
	
	private void mbtiInit(String limitDate){
		StringBuilder searchSql = new StringBuilder();
		searchSql.append("select u.uid,(select sum(r.coin) as coin from rule_log r");
		searchSql.append(" where r.uid=u.uid and r.create_time>='").append(limitDate);
		searchSql.append(" 00:00:00') as coin1,(select sum(s.stealed_coins) as coin");
		searchSql.append(" from user_steal_log s where s.uid=u.uid");
		searchSql.append(" and s.create_time>='").append(limitDate);
		searchSql.append(" 00:00:00') as coin2 from user_profile u");
		searchSql.append(" where u.mbti='#{mbtiCode}#' and u.uid in (select DISTINCT h.uid from topic_read_his h where h.create_time>'");
		searchSql.append(limitDate).append(" 00:00:00') order by (coin1+coin2) desc");
		searchSql.append(" limit ").append(LIMIT_NUM);

		logger.info("5-1 MBTI_INTJ");
		String sql = searchSql.toString().replace("#{mbtiCode}#", "INTJ");
		this.genAndSave(sql, Specification.UserRecInitType.MBTI_INTJ.type);
		logger.info("5-2 MBTI_ENTJ");
		sql = searchSql.toString().replace("#{mbtiCode}#", "ENTJ");
		this.genAndSave(sql, Specification.UserRecInitType.MBTI_ENTJ.type);
		logger.info("5-3 MBTI_ENTJ");
		sql = searchSql.toString().replace("#{mbtiCode}#", "ENTJ");
		this.genAndSave(sql, Specification.UserRecInitType.MBTI_ENTJ.type);
		logger.info("5-4 MBTI_ENTP");
		sql = searchSql.toString().replace("#{mbtiCode}#", "ENTP");
		this.genAndSave(sql, Specification.UserRecInitType.MBTI_ENTP.type);
		logger.info("5-5 MBTI_INFJ");
		sql = searchSql.toString().replace("#{mbtiCode}#", "INFJ");
		this.genAndSave(sql, Specification.UserRecInitType.MBTI_INFJ.type);
		logger.info("5-6 MBTI_INFP");
		sql = searchSql.toString().replace("#{mbtiCode}#", "INFP");
		this.genAndSave(sql, Specification.UserRecInitType.MBTI_INFP.type);
		logger.info("5-7 MBTI_ENFJ");
		sql = searchSql.toString().replace("#{mbtiCode}#", "ENFJ");
		this.genAndSave(sql, Specification.UserRecInitType.MBTI_ENFJ.type);
		logger.info("5-8 MBTI_ENFP");
		sql = searchSql.toString().replace("#{mbtiCode}#", "ENFP");
		this.genAndSave(sql, Specification.UserRecInitType.MBTI_ENFP.type);
		logger.info("5-9 MBTI_ISTJ");
		sql = searchSql.toString().replace("#{mbtiCode}#", "ISTJ");
		this.genAndSave(sql, Specification.UserRecInitType.MBTI_ISTJ.type);
		logger.info("5-10 MBTI_ISFJ");
		sql = searchSql.toString().replace("#{mbtiCode}#", "ISFJ");
		this.genAndSave(sql, Specification.UserRecInitType.MBTI_ISFJ.type);
		logger.info("5-11 MBTI_ESTJ");
		sql = searchSql.toString().replace("#{mbtiCode}#", "ESTJ");
		this.genAndSave(sql, Specification.UserRecInitType.MBTI_ESTJ.type);
		logger.info("5-12 MBTI_ESFJ");
		sql = searchSql.toString().replace("#{mbtiCode}#", "ESFJ");
		this.genAndSave(sql, Specification.UserRecInitType.MBTI_ESFJ.type);
		logger.info("5-13 MBTI_ISTP");
		sql = searchSql.toString().replace("#{mbtiCode}#", "ISTP");
		this.genAndSave(sql, Specification.UserRecInitType.MBTI_ISTP.type);
		logger.info("5-14 MBTI_ISFP");
		sql = searchSql.toString().replace("#{mbtiCode}#", "ISFP");
		this.genAndSave(sql, Specification.UserRecInitType.MBTI_ISFP.type);
		logger.info("5-15 MBTI_ESTP");
		sql = searchSql.toString().replace("#{mbtiCode}#", "ESTP");
		this.genAndSave(sql, Specification.UserRecInitType.MBTI_ESTP.type);
		logger.info("5-16 MBTI_ESFP");
		sql = searchSql.toString().replace("#{mbtiCode}#", "ESFP");
		this.genAndSave(sql, Specification.UserRecInitType.MBTI_ESFP.type);
	}
	
	private void emotionInit(String limitDate){
		StringBuilder searchSql = new StringBuilder();
		searchSql.append("select u.uid,(select sum(r.coin) as coin from rule_log r");
		searchSql.append(" where r.uid=u.uid and r.create_time>='").append(limitDate);
		searchSql.append(" 00:00:00') as coin1,(select sum(s.stealed_coins) as coin");
		searchSql.append(" from user_steal_log s where s.uid=u.uid");
		searchSql.append(" and s.create_time>='").append(limitDate);
		searchSql.append(" 00:00:00') as coin2 from user_profile u,");
		searchSql.append("(select r.uid,max(r.id) as rid from emotion_record r group by r.uid) m,");
		searchSql.append("emotion_record r2,emotion_info i where u.uid=m.uid and m.rid=r2.id");
		searchSql.append(" and r2.emotionId=i.id and i.emotionName='#{eName}#'");
		searchSql.append(" and u.uid in (select DISTINCT h.uid from topic_read_his h where h.create_time>'");
		searchSql.append(limitDate).append(" 00:00:00') order by (coin1+coin2) desc");
		searchSql.append(" limit ").append(LIMIT_NUM);
		
		int i=1;
		String type = null;
		String emotion = null;
		String sql = null;
		for(Map.Entry<String, String> entry : emotionMap.entrySet()){
			type = entry.getKey();
			emotion = entry.getValue();
			logger.info("6-"+i+"情绪_"+emotion);
			sql = searchSql.toString().replace("#{eName}#", emotion);
			this.genAndSave(sql, type);
			i++;
		}
	}
	
	private void careerInit(String limitDate){
		StringBuilder searchSql = new StringBuilder();
		searchSql.append("select u.uid,(select sum(r.coin) as coin from rule_log r");
		searchSql.append(" where r.uid=u.uid and r.create_time>='").append(limitDate);
		searchSql.append(" 00:00:00') as coin1,(select sum(s.stealed_coins) as coin");
		searchSql.append(" from user_steal_log s where s.uid=u.uid");
		searchSql.append(" and s.create_time>='").append(limitDate);
		searchSql.append(" 00:00:00') as coin2 from user_profile u");
		searchSql.append(" where u.occupation='#{career}#' and u.uid in (select DISTINCT h.uid from topic_read_his h where h.create_time>'");
		searchSql.append(limitDate).append(" 00:00:00') order by (coin1+coin2) desc");
		searchSql.append(" limit ").append(LIMIT_NUM);
		
		logger.info("7-1 职业_作业党");
		String sql = searchSql.toString().replace("#{career}#", "1");
		this.genAndSave(sql, Specification.UserRecInitType.CAREER_ZUOYEDANG.type);
		logger.info("7-2 职业_码农");
		sql = searchSql.toString().replace("#{career}#", "2");
		this.genAndSave(sql, Specification.UserRecInitType.CAREER_MANONG.type);
		logger.info("7-3 职业_家里蹲");
		sql = searchSql.toString().replace("#{career}#", "3");
		this.genAndSave(sql, Specification.UserRecInitType.CAREER_JIALIDUN.type);
		logger.info("7-4 职业_手艺人");
		sql = searchSql.toString().replace("#{career}#", "4");
		this.genAndSave(sql, Specification.UserRecInitType.CAREER_SHOUYIREN.type);
		logger.info("7-5 职业_霸道总裁");
		sql = searchSql.toString().replace("#{career}#", "5");
		this.genAndSave(sql, Specification.UserRecInitType.CAREER_BADAOZONGCAI.type);
		logger.info("7-6 职业_编辑狗");
		sql = searchSql.toString().replace("#{career}#", "6");
		this.genAndSave(sql, Specification.UserRecInitType.CAREER_BIANJIGOU.type);
		logger.info("7-7 职业_靠嘴吃饭");
		sql = searchSql.toString().replace("#{career}#", "7");
		this.genAndSave(sql, Specification.UserRecInitType.CAREER_KAOZUICHIFAN.type);
		logger.info("7-8 职业_白衣天使");
		sql = searchSql.toString().replace("#{career}#", "8");
		this.genAndSave(sql, Specification.UserRecInitType.CAREER_BAIYITIANSHI.type);
		logger.info("7-9 职业_园丁");
		sql = searchSql.toString().replace("#{career}#", "9");
		this.genAndSave(sql, Specification.UserRecInitType.CAREER_YUANDING.type);
		logger.info("7-10 职业_为人民服务");
		sql = searchSql.toString().replace("#{career}#", "10");
		this.genAndSave(sql, Specification.UserRecInitType.CAREER_WEIRENMINFUWU.type);
	}
	
	//select uid,coin1,coin2 from ...
	private void genAndSave(String sql, String type){
		//先将数据库中该type的数据删除
		String delSql = "delete from user_rec_init where type='" + type + "'";
		localJdbcDao.executeSql(delSql);
		
		StringBuilder cacheTopicIds = new StringBuilder();
		//再将查出来的数据插入到数据库中
		List<Map<String, Object>> list = localJdbcDao.queryEvery(sql);
		if(null != list && list.size() > 0){
			String insertSql = "insert into user_rec_init(type,uid,create_time) values('"+type+"',#{uid}#,now())";
			Long uid = null;
			for(Map<String, Object> u : list){
				uid = (Long)u.get("uid");
				cacheTopicIds.append(",").append(uid.toString());
				localJdbcDao.executeSql(insertSql.replace("#{uid}#", uid.toString()));
			}
		}
		
		//最后将新的用户列表插入到缓存中
		String value = cacheTopicIds.toString();
		if(value.length() > 0){
			value = value.substring(1);
		}
		cacheService.set(CacheConstant.USER_REC_INIT_LIST_KEY_PRE + type, value);
	}
}
