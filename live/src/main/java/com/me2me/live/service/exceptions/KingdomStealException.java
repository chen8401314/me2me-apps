package com.me2me.live.service.exceptions;
/**
 * 王国已经被偷过
 * @author zhangjiwei
 * @date Jun 15, 2017
 */
public class KingdomStealException extends RuntimeException {
	/**
	 * 已经偷过了
	 */
	public static final int KINGDOM_STEALED=-1;
	/**
	 * 王国没有可偷价值了
	 */
	public static final int KINGDOM_NO_REMAIN=-2;
	/**
	 * 用户没有剩余次数了
	 */
	public static final int USER_MAX=-3;
	/**
	 *
	 */
	private int errorCode;
	
	
	private static final long serialVersionUID = 1L;
	public KingdomStealException(String message) {
		super(message);
	}
	public KingdomStealException(int code,String message) {
		super(message);
		this.errorCode=code;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
}
