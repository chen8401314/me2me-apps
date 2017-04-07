package com.me2me.search.dto;

import java.io.Serializable;
import java.util.Date;

import com.me2me.search.enums.ApplicationType;
import com.me2me.search.enums.ContentType;


public class SearchResultKingdom  implements Serializable{
	private static final long serialVersionUID = 1L;
	private String typeName;
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	private Integer id;
	private Integer uid;
	private String userName;
	private String title;			// 王国有，
	private String content;			
	private String coverImg;		// UGC的封面或者王国评论的封面
	private String kingdomImg;		// 如果本条内容是王国评论，那么它将是本条评论对应王国的封面图。
	private Integer kingdomId;		// 如果本条内容是王国评论，那么它将是本条评论对应王国的ID
	private Date creationDate;
	private String url;
	private String category;
	private ContentType contentType;		
	private String predictType;
	private long cid;
	private long contentId;
	private String unionId;
	public SearchResultKingdom() {
		this.setTypeName(ApplicationType.KINGDOM.getCode());
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getUid() {
		return uid;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
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
	public String getKingdomImg() {
		return kingdomImg;
	}
	public void setKingdomImg(String kingdomImg) {
		this.kingdomImg = kingdomImg;
	}
	public Integer getKingdomId() {
		return kingdomId;
	}
	public void setKingdomId(Integer kingdomId) {
		this.kingdomId = kingdomId;
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
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public ContentType getContentType() {
		return contentType;
	}
	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}
	public String getPredictType() {
		return predictType;
	}
	public void setPredictType(String predictType) {
		this.predictType = predictType;
	}
	public long getCid() {
		return cid;
	}
	public void setCid(long cid) {
		this.cid = cid;
	}
	public long getContentId() {
		return contentId;
	}
	public void setContentId(long contentId) {
		this.contentId = contentId;
	}
	public String getUnionId() {
		return unionId;
	}
	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}
	
	
}
