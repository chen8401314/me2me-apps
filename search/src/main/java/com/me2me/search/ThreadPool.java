package com.me2me.search;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
	private static final ExecutorService pool = Executors.newCachedThreadPool();
	
	public static void execute(Runnable runnable){
		pool.execute(runnable);
	}
}
