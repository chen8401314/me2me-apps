package com.me2me.mgmt.request;

import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class IosWapxQueryDTO {

	@Getter
    @Setter
	private String startTime;
	@Getter
    @Setter
	private String endTime;
	@Getter
	@Setter
	private int page = 1;
	@Getter
	@Setter
	private int pageSize = 10;
	
	
	@Getter
    @Setter
	private int totalCount = 0;
	@Getter
    @Setter
	private int totalPage = 0;
	@Getter
    @Setter
	private TotalItem totalItem = new TotalItem();
	@Getter
    @Setter
	private List<Item> result = Lists.newArrayList();
	
	@Data
	public class TotalItem{
		private long totalNoticeCount = 0;
		private long totalActiveCount = 0;
	}
	
	@Data
	public static class Item{
		private String udid;
		private String app;
		private String idfa;
		private String openudid;
		private String os;
		private String callbackurl;
		private String ip;
		private int status;
		private Date optTime;
		private long uid;
	}
}
