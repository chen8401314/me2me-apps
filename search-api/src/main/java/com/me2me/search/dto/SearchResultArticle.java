package com.me2me.search.dto;

import java.io.Serializable;
import java.util.Date;

import com.me2me.search.enums.ApplicationType;

/**
 * 文章搜索结果
 * @author zhangjiwei
 * @date Feb 13, 2017
 *
 */
public class SearchResultArticle implements Serializable {

	private static final long serialVersionUID = 1L;
	private String typeName;
	private Integer id;
	private String title;			// 王国有，
	private String content;			
	private String coverImg;		// UGC的封面或者王国评论的封面
	private Date creationDate;
	private String url;
	private String author;
	private String category;
	private String cid;
	private String unionId;
	
	
	public SearchResultArticle() {
		this.setTypeName(ApplicationType.CRAWLER.getCode());
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCoverImg() {
		return coverImg;
	}
	public void setCoverImg(String coverImg) {
		this.coverImg = coverImg;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
	
}
