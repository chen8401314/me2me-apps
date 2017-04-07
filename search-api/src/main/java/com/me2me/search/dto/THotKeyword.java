package com.me2me.search.dto;


import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * 热门关键词
 */
public class THotKeyword implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;		//    主键   自增长    必填    必须唯一 
	private String keyword;		//关键字  
	private Date creationDate;		//  
	private Integer orderNum;		//排序值  
	private Integer isValid;		//是否有效  
	private Integer isTop;		//是否置顶  
	
	/**
	* 设置 
	*/
	public void setId(Integer id){
		this.id=id;
	}
	
	/**
	* 获取 
	*/
	public Integer getId(){
		return this.id;
	}
	/**
	* 设置 关键字
	*/
	public void setKeyword(String keyword){
		this.keyword=keyword;
	}
	
	/**
	* 获取 关键字
	*/
	public String getKeyword(){
		return this.keyword;
	}
	/**
	* 设置 
	*/
	public void setCreationDate(Date creationDate){
		this.creationDate=creationDate;
	}
	
	/**
	* 获取 
	*/
	public Date getCreationDate(){
		return this.creationDate;
	}
	/**
	* 设置 排序值
	*/
	public void setOrderNum(Integer orderNum){
		this.orderNum=orderNum;
	}
	
	/**
	* 获取 排序值
	*/
	public Integer getOrderNum(){
		return this.orderNum;
	}
	/**
	* 设置 是否有效
	*/
	public void setIsValid(Integer isValid){
		this.isValid=isValid;
	}
	
	/**
	* 获取 是否有效
	*/
	public Integer getIsValid(){
		return this.isValid;
	}
	/**
	* 设置 是否置顶
	*/
	public void setIsTop(Integer isTop){
		this.isTop=isTop;
	}
	
	/**
	* 获取 是否置顶
	*/
	public Integer getIsTop(){
		return this.isTop;
	}
	
	

}