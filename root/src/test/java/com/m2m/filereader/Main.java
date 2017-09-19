package com.m2m.filereader;

import java.io.File;

public class Main {
    public static void main(String[] args){
        try {
            File file = Home.findInHome("devJson/api.live.kingdomByCategory.json");
            String out = Home.source(file);
            System.out.println(out);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("end");
    }
}
