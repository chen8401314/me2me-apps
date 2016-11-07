package com.me2me.user.dto;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class ShowVersionControlDto implements BaseEntity {

	private static final long serialVersionUID = -6522752140060520611L;

	private List<VersionControlDto> result = Lists.newArrayList();
}
