package com.me2me.activity.dto;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class ShowLuckStatusDTO implements BaseEntity {

	private static final long serialVersionUID = 8260160492768161541L;

	private List<LuckStatusDTO> result = Lists.newArrayList();

}
