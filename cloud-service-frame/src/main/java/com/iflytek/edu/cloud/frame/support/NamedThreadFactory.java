/**
 * Copyright (C) 2013-2014 科大讯飞股份有限公司 - All rights reserved.
 */
package com.iflytek.edu.cloud.frame.support;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author @author libinsong1204@gmail.com
 * @date 2012-11-11 上午9:46:36
 */
public class NamedThreadFactory implements ThreadFactory
{
	private static final AtomicInteger POOL_SEQ = new AtomicInteger(1);

	private final AtomicInteger mThreadNum = new AtomicInteger(1);

	private final String mPrefix;

	private final boolean mDaemo;

	private final ThreadGroup mGroup;

	public NamedThreadFactory()
	{
		this("pool-" + POOL_SEQ.getAndIncrement(),false);
	}

	public NamedThreadFactory(String prefix)
	{
		this(prefix,false);
	}

	public NamedThreadFactory(String prefix,boolean daemo)
	{
		mPrefix = prefix + "-thread-";
		mDaemo = daemo;
        SecurityManager s = System.getSecurityManager();
        mGroup = ( s == null ) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
	}

	public Thread newThread(Runnable runnable)
	{
		String name = mPrefix + mThreadNum.getAndIncrement();
        Thread ret = new Thread(mGroup,runnable,name,0);
        ret.setDaemon(mDaemo);
        return ret;
	}

	public ThreadGroup getThreadGroup()
	{
		return mGroup;
	}
}
