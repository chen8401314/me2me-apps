package com.me2me.live.service;


import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.me2me.common.web.BaseEntity;
import com.me2me.live.dto.SpeakDto;
import com.me2me.live.model.QuotationInfo;
import com.me2me.live.model.RobotInfo;
import com.me2me.live.model.RobotInfoExample;
import com.me2me.live.model.Topic;
import junit.framework.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lombok.Data;


/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2017/7/17.
 */
@Component
@Slf4j
public class KingdomRobot {


    private static final ScheduledExecutorService ES = Executors.newSingleThreadScheduledExecutor();

    private static final Random RANDOM = new Random();

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private LiveService liveService;


    @Data
    public static class ReplyTimes implements BaseEntity{

        private final int min;

        private final int max;

        public ReplyTimes(int min,int max){
            this.min = min;
            this.max = max;
        }
    }


    @Data
    public static class ExecutePolicy implements BaseEntity{

        private Date createTime;

        private long topicId;

        private int lastHour;

        private int min;

        private int max;
    }



    private List<Date> splitTask(ExecutePolicy policy, ReplyTimes replyCount) {
        try {
            List<Date> ret = Lists.newArrayList();
            Calendar finalTime = Calendar.getInstance();
            finalTime.setTime(policy.getCreateTime());
            finalTime.add(Calendar.HOUR_OF_DAY,policy.getLastHour());
            log.info("final time : " + sdf.format(finalTime.getTime()));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(policy.getCreateTime());
            while(calendar.before(finalTime)){
                int minute = RANDOM.nextInt(policy.getMax())+policy.getMin();
                int replyTimes = RANDOM.nextInt(replyCount.getMax())+replyCount.getMin();
                log.info("随机回复次数：{}",replyTimes);
                for(int i = 0;i<replyTimes;i++){
                    calendar.add(Calendar.MINUTE,minute);
                    log.info("every task execute time : " + sdf.format(calendar.getTime()));
                    ret.add(calendar.getTime());
                }
            }
            return ret;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void start(ExecutePolicy policy){
        // 第一次发言的逻辑
        int sleep = RANDOM.nextInt(75)+45;
        ES.schedule(new Runnable() {
            @Override
            public void run() {
                log.info("第一次发言。。。。@操作。。。。");
                liveService.speak(builderSpeakDto(policy.getTopicId()));
            }
        },sleep,TimeUnit.SECONDS);

        // 前24小时发言的逻辑
        task(policy,new ReplyTimes(2,3));



        // 后24小时发言
        // 调整策略
        // 重新new对象，防止线程间的对象共享造成的脏数据。
        ExecutePolicy policy2 = new ExecutePolicy();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(policy.getCreateTime());
        calendar.add(Calendar.HOUR_OF_DAY,policy.getLastHour());
        policy2.setCreateTime(calendar.getTime());
        policy2.setTopicId(policy.getTopicId());
        policy2.setLastHour(policy.getLastHour());
        policy2.setMin(60);
        policy2.setMax(120);
        task(policy2,new ReplyTimes(1,2));
    }

    private void task(final ExecutePolicy policy,ReplyTimes replyTimes) {
        List<Date> ret = splitTask(policy, replyTimes);
        Assert.assertNotNull("任务分割后的时间结果集合必须大于零。",ret);
        // 获取不重复的语录
        List<QuotationInfo> quotationInfos = liveService.selectQuotationByList(ret.size());

        for(int i = 0;i<ret.size();i++){
            long delay = ret.get(i).getTime() - System.currentTimeMillis();
            log.info("delay time is : " + delay);
            int finalI = i;
            ES.schedule(new Runnable() {
                @Override
                public void run() {
                    log.info("execute task to speak ..... ");
                    QuotationInfo info = quotationInfos.get(finalI);
                    SpeakDto speakDto = builderSpeakDto(policy.getTopicId());
                    speakDto.setFragment(info.getQuotation());
                    speakDto.setQuotationInfoId(info.getId());
                    liveService.speak(speakDto);
                }
            },delay, TimeUnit.MILLISECONDS);
        }
    }


    private SpeakDto builderSpeakDto(long topicId) {
        SpeakDto speakDto = new SpeakDto();
        // todo 填充参数
        RobotInfo robot = liveService.selectRobotInfo();
        QuotationInfo quotationInfo  = liveService.selectQuotation();
        speakDto.setQuotationInfoId(quotationInfo.getId());
        speakDto.setFragment(quotationInfo.getQuotation());
        speakDto.setContentType(0);
        speakDto.setTopicId(topicId);
        speakDto.setUid(robot.getUid());
        speakDto.setType(1);
        speakDto.setSource(1);
        return speakDto;
    }

    public static void main(String[] args) throws ParseException {

        Date createTime = sdf.parse("2017-07-25 21:46:00");

        KingdomRobot.ExecutePolicy step1 = new KingdomRobot.ExecutePolicy();
        step1.setCreateTime(createTime);
        step1.setTopicId(1000);
        step1.setLastHour(24);
        step1.setMin(60);
        step1.setMax(60);


        KingdomRobot.ExecutePolicy step2 = new KingdomRobot.ExecutePolicy();
        step2.setCreateTime(createTime);
        step2.setTopicId(1000);
        step2.setLastHour(24);
        step2.setMin(120);
        step2.setMax(60);

        new KingdomRobot().start(step1);
    }
}
