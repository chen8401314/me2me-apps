package com.me2me.mgmt.manager;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.me2me.mgmt.dal.entity.MgmtSysLog;
import com.me2me.mgmt.dal.mapper.MgmtSysLogMapper;

@Component
public class MgmtSysLogManager {

	@Autowired
	private MgmtSysLogMapper mgmtSysLogMapper;
	
	public void insert(MgmtSysLog log){
		mgmtSysLogMapper.insert(log);
	}
	
	public List<MgmtSysLog> queryPageByDescAndTime(String desc, Date startTime, Date endTime, int page, int pageSize){
		if(StringUtils.isBlank(desc)){
			desc = null;
		}
		if(page < 1){
			page = 1;
		}
		if(pageSize < 1){
			pageSize = 10;
		}
		int start = (page-1)*pageSize;
		
		List<MgmtSysLog> list = mgmtSysLogMapper.queryPageByDescAndTime(desc, startTime, endTime, start, pageSize);
		
		return list;
	}
}
