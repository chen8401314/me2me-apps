package com.me2me.live.service;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import com.me2me.live.dto.SpeakDto;
import com.me2me.live.model.Topic;
import com.me2me.search.ThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
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
    static class ReplyTimes implements BaseEntity{

        private final int min;

        private final int max;

        public ReplyTimes(int min,int max){
            this.min = min;
            this.max = max;
        }
    }


    @Data
    static class ExecutePolicy implements BaseEntity{

        private Date createTime;

        private long topicId;

        private int lastHour;

        private int min;

        private int max;
    }

    public void splitTask(ExecutePolicy policy, ReplyTimes replyCount, SpeakDto speakDto) {
        try {
            Calendar finalTime = Calendar.getInstance();
            finalTime.setTime(policy.getCreateTime());
            finalTime.add(Calendar.HOUR_OF_DAY,policy.getLastHour());
//            System.out.println("final time : " + sdf.format(finalTime.getTime()));
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
                    long delay = calendar.getTimeInMillis() - System.currentTimeMillis();
                    ES.schedule(new Runnable() {
                        @Override
                        public void run() {
                            log.info("当前时间:"+sdf.format(new Date())+"，任务发出去了，机器人开始回复了。。。"+policy.getTopicId());
                            // 调用发言
                            liveService.speak(speakDto);
                        }
                    },delay,TimeUnit.MILLISECONDS);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void startWork(List<Long> ids,ExecutePolicy step1,ExecutePolicy step2) {

        for(Long id : ids){
            int sleep = RANDOM.nextInt(75)+45;
            // 获取王国创建时间
            Topic topic = liveService.getTopicById(id);
            step1.setCreateTime(topic.getCreateTime());
            step1.setTopicId(id);

            // 后24小时候的操作
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(topic.getCreateTime());
            calendar.add(Calendar.HOUR_OF_DAY,24);
            step2.setCreateTime(calendar.getTime());
            step2.setTopicId(id);
            // 构建留言对象
            SpeakDto speakDto = builderSpeakDto();

            ES.schedule(new Runnable() {
                @Override
                public void run() {
                    liveService.speak(speakDto);
                    // 指派24小时前任务
                    splitTask(step1,new ReplyTimes(2,3),speakDto);
                    // 指派24小时候的任务
                    splitTask(step2,new ReplyTimes(1,2),speakDto);
                }
            },sleep , TimeUnit.SECONDS);
        }

    }

    private SpeakDto builderSpeakDto() {
        SpeakDto speakDto = new SpeakDto();



        return speakDto;
    }
}
