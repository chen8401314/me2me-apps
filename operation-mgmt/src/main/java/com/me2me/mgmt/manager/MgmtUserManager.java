package com.me2me.mgmt.manager;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.me2me.mgmt.dal.entity.MgmtUser;
import com.me2me.mgmt.dal.mapper.MgmtUserMapper;

@Component
public class MgmtUserManager {

	@Autowired
	private MgmtUserMapper mgmtUserMapper;
	
	public MgmtUser getByUuid(String uuid){
		if(StringUtils.isBlank(uuid)){
			return null;
		}
		List<MgmtUser> list = mgmtUserMapper.getByUuid(uuid);
		if(null != list && list.size() > 0){
			return list.get(0);
		}
		return null;
	}
	
	public List<MgmtUser> getListByAppUids(List<String> appUids){
		if(null == appUids || appUids.size() == 0){
			return null;
		}
		return mgmtUserMapper.getListByAppUid(appUids);
	}
}
