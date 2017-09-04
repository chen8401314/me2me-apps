package com.me2me.user.service;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public class Test {

	public static void main(String[] args){
		String ss = "{\n uid = 37926\n}".trim();
		ss = ss.substring(1, ss.length()-1).trim();
		System.out.println(ss);
		String[] tmp = ss.split("=");
		System.out.println(tmp[1].trim());
		
//		JSONObject openinstallDataJson = JSONObject.parseObject("{\"uid\": 67926}");
//		long refereeUid = 0;
//		if(null != openinstallDataJson.get("uid")){
//			refereeUid = openinstallDataJson.getLong("uid");
//		}
//		System.out.println(refereeUid);
	}
}
