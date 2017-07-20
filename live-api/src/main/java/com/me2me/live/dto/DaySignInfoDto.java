package com.me2me.live.dto;

import java.util.List;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: chenxiang
 * Date: 2017/7/19
 * Time :19:22
 */
@Data
public class DaySignInfoDto implements BaseEntity{
	private static final long serialVersionUID = 6362649571729593474L;

	//是否是新用户注册第一天 0否1是
	private int isFirstDay;
	
	//是否已保存日签信息 0否 1是
	private int isSave;
	
	//0有文字有图片1有文字无图片2有图片无文字3其他人日签
	private int status;
	
	//语录用户昵称
	private String nickName;
	
	//语录用户头像
	private String avatar;
	
	//语录日期
	private String signDate;
	
	//日签序号
	private int serialNumber;
	
	//语录王国ID
	private long topicId;
	
	//语录王国国王UID
	private long uid;
	
	//日签显示位置
	private int position;
	
	//日签文字内容
	private String fragment;
	
	//日签图片
	private String image;
	
	//日签王国名称
	private String topicTitle;
	
    //随机语录节点数组
    private List<Quotation> quotations = Lists.newArrayList();
    
    
    @Data
    public static class Quotation implements BaseEntity{
		private static final long serialVersionUID = 1465817396904072679L;
    	
		private long uid;//用户UID
		private String nickName;//用户昵称
		private String avatar;//用户头像
		private long quotationId;//语录ID
		private String quotation;//语录内容
    }
    
}
