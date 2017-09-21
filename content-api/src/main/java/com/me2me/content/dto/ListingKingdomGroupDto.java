package com.me2me.content.dto;

import java.util.List;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

import lombok.Data;

/**
 * @author pc339
 *
 */
@Data
public class ListingKingdomGroupDto implements BaseEntity {

	private List<BasicKingdomInfo> listingKingdoms=Lists.newArrayList(); 


}
