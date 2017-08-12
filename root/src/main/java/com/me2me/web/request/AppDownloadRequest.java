package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by pc329 on 2017/3/17.
 */
@Data
public class AppDownloadRequest extends Request {
	@Getter
	@Setter
	private long fromUid;
	@Getter
	@Setter
	private long requestUid;

}
