package com.me2me.mgmt.manager;

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
}
