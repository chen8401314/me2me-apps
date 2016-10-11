package com.me2me.mgmt.dal.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.me2me.mgmt.dal.entity.MgmtUser;

public interface MgmtUserMapper {

	List<MgmtUser> getByUuid(String uuid);
	
	List<MgmtUser> getListByAppUid(@Param(value="appUids") List<String> appUids);
}
