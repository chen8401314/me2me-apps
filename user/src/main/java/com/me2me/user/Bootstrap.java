package com.me2me.user;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.me2me.user.dao.UserMybatisDao;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/26.
 */
public class Bootstrap {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spring/user-context.xml");
        UserMybatisDao dao = ctx.getBean(UserMybatisDao.class);
        System.out.println(dao);


    }
}
