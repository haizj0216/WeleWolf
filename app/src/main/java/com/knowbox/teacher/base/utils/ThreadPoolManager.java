package com.knowbox.teacher.base.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolManager {
	private ExecutorService service;

	private ThreadPoolManager() {
		service = Executors.newCachedThreadPool();
	}

	private static ThreadPoolManager manager;

	public synchronized static ThreadPoolManager getInstance() {
		if (manager == null) {
			manager = new ThreadPoolManager();
		}
		return manager;
	}

	public void addTask(Runnable runnable) {
		service.execute(runnable);
	}

	public void addTask(Thread thread) {
		service.execute(thread);
	}

	public void shoudownTask() {
		service.shutdown();
	}

	public boolean isShurtDown() {

		return service.isShutdown();
	}

}
