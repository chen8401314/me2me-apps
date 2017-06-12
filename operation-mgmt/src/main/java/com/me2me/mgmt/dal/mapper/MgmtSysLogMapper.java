package com.me2me.mgmt.dal.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.me2me.mgmt.dal.entity.MgmtSysLog;

public interface MgmtSysLogMapper {

	int insert(MgmtSysLog log);
	
	List<MgmtSysLog> queryPageByDescAndTime(@Param(value="description")String description, @Param(value="startTime")Date startTime, @Param(value="endTime")Date endTime, @Param(value="start")int start, @Param(value="pageSize")int pageSize);
}
