package com.me2me.mgmt.controller;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.elasticsearch.action.index.IndexAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

@Controller
@RequestMapping("/globalTask")
public class GlobalTaskMgr {
	private Logger logger = LoggerFactory.getLogger(IndexAction.class);
	private ExecutorService  threadPool =Executors.newSingleThreadExecutor();
	
	@RequestMapping(value = { "/index" })
	public String view(ModelMap model, HttpServletRequest request) throws Exception {
		// 加载系统任务列表
		WebApplicationContext wc = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
		Map<String, Object> beans = wc.getBeansWithAnnotation(Component.class);
		List<Map<String, Object>> tasks = new ArrayList<>();
		for (Object bean : beans.values()) {
			for (Method method : bean.getClass().getDeclaredMethods()) {
				Annotation an = method.getAnnotation(Scheduled.class);
				if (an != null) {
					Map<String, Object> taskInfo = new HashMap<>();
					Scheduled sch = (Scheduled) an;
					taskInfo.put("className", bean.getClass().getName());
					taskInfo.put("methodName", method.getName());
					taskInfo.put("cron", sch.cron());
					tasks.add(taskInfo);
				}
			}
		}

		request.setAttribute("taskList", tasks);
		return "globalTask";
	}

	@ResponseBody
	@RequestMapping(value = "/run_task")
	public String run_task(ModelMap model, HttpServletRequest request) {
		Object ts = request.getSession().getAttribute("MSG_LOG");
		if (ts == null) {
			request.getSession().setAttribute("MSG_LOG", new LinkedBlockingQueue<String>());
		}

		final LinkedBlockingQueue<String> msgQue = (LinkedBlockingQueue<String>) request.getSession()
				.getAttribute("MSG_LOG");
		String className = request.getParameter("className");
		String methodName = request.getParameter("methodName");
		
		try {
			Class cls = Class.forName(className);
			WebApplicationContext wc = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
			Object bean = wc.getBean(cls);
			Method method = bean.getClass().getDeclaredMethod(methodName);

			threadPool.execute(()->{
				try {
					msgQue.add("process " + methodName + " task");
					method.invoke(bean);
					msgQue.add(methodName + " task finished");
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
			return "task " + methodName + " start failed";
		}
		return "task " + methodName + " start successfully";
	}

	@ResponseBody
	@RequestMapping(value = "/getTaskStatus", produces = "text/html;charset=UTF-8")
	public String taskStatus(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
		StringBuffer sb = new StringBuffer();
		LinkedBlockingQueue<String> msgQue = (LinkedBlockingQueue<String>) request.getSession().getAttribute("MSG_LOG");
		if (msgQue != null) {
			while (!msgQue.isEmpty()) {
				String msg = msgQue.poll();
				sb.append(msg + "\n");
			}
		}
		return sb.toString();
	}


}
