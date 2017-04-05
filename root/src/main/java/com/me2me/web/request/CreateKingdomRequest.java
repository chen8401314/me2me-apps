package com.me2me.web.request;

import lombok.Getter;
import lombok.Setter;

import com.me2me.common.web.Request;

public class CreateKingdomRequest extends Request {

	@Setter
    @Getter
	private String title;
	@Setter
    @Getter
	private String liveImage;
	@Setter
    @Getter
	private int contentType;
	@Setter
    @Getter
	private String fragment;
	@Setter
    @Getter
	private int source;
	@Setter
    @Getter
	private String extra;
	@Setter
    @Getter
	private int kType;
	@Setter
    @Getter
	private String cExtra;
	@Setter
    @Getter
	private String kConfig;
	@Setter
    @Getter
	private String tags;
}
