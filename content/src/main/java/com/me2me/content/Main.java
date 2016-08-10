package com.me2me.content;


import java.util.Random;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/23.
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        Random random = new Random();

        for(int i = 0;i<10;i++){
            int value = random.nextInt(180000)+60000;
            System.out.println(value);
        }
//        ExecutorService executorService= Executors.newFixedThreadPool(100);
//        for(int i = 0;i<10;i++) {
//            Thread.sleep(1000);
//            final int finalI = i;
//            executorService.submit(new Runnable() {
//                @Override
//                public void run() {
//                    System.out.println(finalI);
//                }
//            });
//        }

    }

}
