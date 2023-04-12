package com.sortinghat.funny.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadFactoryUtil {

	private final static String TAG = "ThreadFactory.java";

	private ThreadFactoryUtil() {
		threadPool = getThread();
	}

	private static ThreadFactoryUtil instance;
	private ExecutorService threadPool;

	public static ThreadFactoryUtil getInstance() {
		if (instance == null) {
			instance = new ThreadFactoryUtil();
		}
		return instance;
	}

	public ExecutorService getThread() {
		if (threadPool == null) {
			threadPool = Executors.newCachedThreadPool();
		}
		return threadPool;
	}
}
