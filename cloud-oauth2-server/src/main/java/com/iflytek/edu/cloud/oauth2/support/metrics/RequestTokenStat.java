/**
 * Copyright (C) 2013-2014 科大讯飞股份有限公司 - All rights reserved.
 */
package com.iflytek.edu.cloud.oauth2.support.metrics;

import java.util.Date;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import jsr166e.LongAdder;

import com.iflytek.edu.cloud.oauth2.utils.OauthContextHolder;

public class RequestTokenStat {
	private final String appkey;
	private final String grantType;

    private volatile int                               runningCount;
    final static AtomicIntegerFieldUpdater<RequestTokenStat> runningCountUpdater = 
    		AtomicIntegerFieldUpdater.newUpdater(RequestTokenStat.class, "runningCount");
    
    private volatile LongAdder requestCount = new LongAdder();
    private volatile LongAdder requestTimeNano = new LongAdder();
    private volatile LongAdder errorCount = new LongAdder();
    private volatile LongAdder cycoreCount = new LongAdder();
    private volatile LongAdder cycoreTimeMillis = new LongAdder();
    private volatile long lastAccessTimeMillis = -1;
    
    public RequestTokenStat(String appkey, String grantType){
    	this.grantType = grantType;
    	this.appkey = appkey;
    }
    
    public String getAppkey() {
    	return appkey;
    }

    public String getGrantType() {
		return grantType;
	}

	public void beforeInvoke(long startMillis) {
        runningCountUpdater.incrementAndGet(this);
        requestCount.increment();
        this.setLastAccessTimeMillis(startMillis);
    }

    public void afterInvoke(Throwable error, long nanos) {
        runningCountUpdater.decrementAndGet(this);
        requestTimeNano.add(nanos);
        cycoreCount.add(OauthContextHolder.getContext().getCallCycoreCount());
        cycoreTimeMillis.add(OauthContextHolder.getContext().getCallCycoreTime());
        if (error != null) {
            errorCount.increment();
        }
    }

    public int getRunningCount() {
        return this.runningCount;
    }

    public long getRequestCountThenReset() {
        return requestCount.sumThenReset();
    }

    public long getRequestTimeMillisThenReset() {
        return requestTimeNano.sumThenReset() / (1000 * 1000);
    }
    
    public long getCycoreCountThenReset() {
        return cycoreCount.sumThenReset();
    }

    public long getCycoreTimeMillisThenReset() {
        return cycoreTimeMillis.sumThenReset();
    }

    public void setLastAccessTimeMillis(long lastAccessTimeMillis) {
        this.lastAccessTimeMillis = lastAccessTimeMillis;
    }

    public Date getLastAccessTime() {
        if (lastAccessTimeMillis < 0L) {
            return null;
        }

        return new Date(lastAccessTimeMillis);
    }

    public long getLastAccessTimeMillis() {
        return lastAccessTimeMillis;
    }

    public long getErrorCountThenReset() {
        return errorCount.sumThenReset();
    }

}
