package com.me2me.mgmt.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import com.me2me.mgmt.dal.entity.MgmtUser;

public class SystemUserQueryDTO {

	@Getter
    @Setter
	private String uname;
	
	@Getter
    @Setter
	private List<MgmtUser> result;
}
