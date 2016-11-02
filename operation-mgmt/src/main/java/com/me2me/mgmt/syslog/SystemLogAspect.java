package com.me2me.mgmt.syslog;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.me2me.mgmt.dal.entity.MgmtSysLog;
import com.me2me.mgmt.manager.MgmtSysLogManager;
import com.plusnet.sso.api.vo.SSOUser;
import com.plusnet.sso.client.utils.AuthTool;
import com.plusnet.sso.client.utils.ClientIpUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * 切点类
 */
@Aspect
@Component
public class SystemLogAspect {

	@Resource
	private MgmtSysLogManager mgmtSysLogManager;
	// 本地异常日志记录对象
	private static final Logger logger = LoggerFactory.getLogger(SystemLogAspect.class);

	// Controller层切点
	@Pointcut("@annotation(com.me2me.mgmt.syslog.SystemControllerLog)")
	public void controllerAspect() {
	}

	/**
	 * 前置通知 用于拦截Controller层记录用户的操作
	 * 
	 * @param joinPoint	切点
	 */
	@Before("controllerAspect()")
	public void doBefore(JoinPoint joinPoint) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String requestPath = request.getRequestURI();
		SSOUser user = AuthTool.getSessionUser(request);
		String userName = "未知";
		long userId = -1;
		if (null != user) {
			userName = user.getUserName();
			userId = user.getUserId();
		}
		// 请求的IP
		String ip = ClientIpUtils.getClientIP(request);
		try {
			String desc = getControllerMethodDescription(joinPoint);
			String method = joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()";
			logger.debug("请求路径:" + requestPath);
			logger.debug("类方法名:" + method);
			logger.debug("方法描述:" + desc);
			logger.debug("操作人:" + userName + "[" + userId + "]");
			logger.debug("请求IP:" + ip);
			// *========数据库日志=========*//
			MgmtSysLog sysLog = new MgmtSysLog();
			sysLog.setCreateTime(new Date());
			sysLog.setDescription(desc);
			sysLog.setIp(ip);
			sysLog.setRequestPath(requestPath);
			sysLog.setMethod(method);
			sysLog.setType(0);// 操作日志
			sysLog.setUserId(userId);
			sysLog.setUserName(userName);
			mgmtSysLogManager.insert(sysLog);
		} catch (Exception e) {
			logger.error("记录操作日志失败", e);
		}
	}

	/**
	 * 获取注解中对方法的描述信息 用于Controller层注解
	 * 
	 * @param joinPoint	切点
	 * @return 			方法描述
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static String getControllerMethodDescription(JoinPoint joinPoint) throws Exception {
		String targetName = joinPoint.getTarget().getClass().getName();
		String methodName = joinPoint.getSignature().getName();
		Object[] arguments = joinPoint.getArgs();
		Class targetClass = Class.forName(targetName);
		Method[] methods = targetClass.getMethods();
		String description = "";
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				Class[] clazzs = method.getParameterTypes();
				if (clazzs.length == arguments.length) {
					description = method.getAnnotation(SystemControllerLog.class).description();
					break;
				}
			}
		}
		return description;
	}
}
