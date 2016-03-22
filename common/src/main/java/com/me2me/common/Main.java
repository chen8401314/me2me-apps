package com.me2me.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/16.
 */
public class Main {

    // 1. 不需要参数,返回值为 5
    // () -> 5

// 2. 接收一个参数(数字类型),返回其2倍的值
    // x -> 2 * x

// 3. 接受2个参数(数字),并返回他们的差值
    // (x, y) -> x – y

// 4. 接收2个int型整数,返回他们的和
    // (int x, int y) -> x + y

// 5. 接受一个 string 对象,并在控制台打印,不返回任何值(看起来像是返回void)
    // (String s) -> System.out.print(s)
    public static void main(String[] args) {

        Action a = (String userName,String password) -> {
            if("admin".equals(userName)&&"123456".equals(password)){
                System.out.println("登录成功");
            }else{
                System.out.println("密码或用户名错误");
            }
        };
        a.login("admin","1233456");
        Runnable r = () ->{
            System.out.println("caotamei");

        };
        r.run();
//        List<Person> list = new ArrayList<>();
//        Person p = new Person();
//        p.setAge(10);
//        p.setName("小丁");
//        list.add(p);
//        p = new Person();
//        p.setAge(12);
//        p.setName("小芳");
//        list.add(p);
//        p = new Person();
//        p.setAge(20);
//        p.setName("小甘");
//        list.add(p);
//        p = new Person();
//        p.setAge(30);
//        p.setName("大丁");
//        list.add(p);
//
//        Stream<Person> s = list.stream();
//        s.filter(person -> person.getAge()>15).forEach(person1 -> {
//            System.out.println(person1);
//        });


    }

    interface Action{
        void login(String userName,String password);
    }
}
