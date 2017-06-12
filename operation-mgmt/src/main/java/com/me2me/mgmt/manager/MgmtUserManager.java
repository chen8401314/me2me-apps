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
	
	public List<MgmtUser> getPageByName(String name, int page, int pageSize){
		if(StringUtils.isBlank(name)){
			name = null;
		}
		if(page < 1){
			page = 1;
		}
		if(pageSize < 1){
			pageSize = 10;
		}
		int start = (page-1)*pageSize;
		List<MgmtUser> list = mgmtUserMapper.getPageByName(name, start, pageSize);
		return list;
	}
	
	public void saveMgmtUser(MgmtUser user){
		mgmtUserMapper.insert(user);
	}
	
	public MgmtUser getById(long id){
		List<MgmtUser> list = mgmtUserMapper.getById(id);
		if(null != list && list.size() > 0){
			return list.get(0);
		}
		return null;
	}
	
	public void updateMgmtUser(MgmtUser user){
		mgmtUserMapper.update(user);
	}
	
	public MgmtUser getByAppUid(String appUid){
		if(StringUtils.isBlank(appUid)){
			return null;
		}
		List<MgmtUser> list = mgmtUserMapper.getByAppUid(appUid);
		if(null != list && list.size() > 0){
			return list.get(0);
		}
		return null;
	}
}
