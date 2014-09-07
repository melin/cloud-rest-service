/**
 * Copyright (C) 2013-2014 科大讯飞股份有限公司 - All rights reserved.
 */
package com.iflytek.edu.cloud.oauth2.support.metrics;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import jsr166e.LongAdder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.github.diamond.client.netty.NetUtils;
import com.github.diamond.client.util.NamedThreadFactory;
import com.iflytek.edu.cloud.oauth2.utils.JettyConfigUtil;

public class OauthAppStat {
	private static final Logger LOGGER = LoggerFactory.getLogger(OauthAppStat.class);
	
	private volatile int                               runningCount;
    private volatile int                               concurrentMax;
    final static AtomicIntegerFieldUpdater<OauthAppStat> runningCountUpdater = 
    		AtomicIntegerFieldUpdater.newUpdater(OauthAppStat.class, "runningCount");
    final static AtomicIntegerFieldUpdater<OauthAppStat> concurrentMaxUpdater = 
    		AtomicIntegerFieldUpdater.newUpdater(OauthAppStat.class, "concurrentMax");
    
    private final LongAdder requestCount = new LongAdder();
    private final ConcurrentMap<String, RequestTokenStat> requestTokenMap = new ConcurrentHashMap<String, RequestTokenStat>();

    private final ScheduledExecutorService expireExecutor = 
    		Executors.newScheduledThreadPool(1, new NamedThreadFactory("CloudOauthRegistryExpireTimer", true));
    private final ScheduledFuture<?> expireFuture;
    
    private static final String insert_sql = "INSERT INTO mon_oauth_stat(server_host,server_port,appkey,"
    		+ "grant_type,req_count,req_time,error_count,cycore_time,cycore_count,last_access_time,update_time) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
    
    private static final String update_sql = "UPDATE mon_oauth_stat SET req_count = req_count + ?, req_time = req_time + ?, "
    		+ "error_count = error_count + ?, cycore_time = cycore_time + ?, cycore_count = cycore_count + ?, last_access_time = ?, update_time = ? "
    		+ "WHERE server_host = ? AND server_port = ? AND appkey = ? AND grant_type = ?";
    
    public OauthAppStat(final JdbcTemplate jdbcTemplate){
    	final String serverHost = NetUtils.getLocalHost();
    	final int serverPort = JettyConfigUtil.getServerPort();
    	this.expireFuture = expireExecutor.scheduleWithFixedDelay(new Runnable() {
            public void run() {
            	for(RequestTokenStat requestToken : requestTokenMap.values()) {
            		long reqCount = requestToken.getRequestCountThenReset();
            		long reqTime = requestToken.getRequestTimeMillisThenReset();
            		long errorTime = requestToken.getErrorCountThenReset();
            		long cycoreTime = requestToken.getCycoreTimeMillisThenReset();
            		long cycoreCount = requestToken.getCycoreCountThenReset();
        			int result = jdbcTemplate.update(update_sql, reqCount, reqTime,
        					errorTime, cycoreTime, cycoreCount, requestToken.getLastAccessTime(), new Date(),
        					serverHost, serverPort, requestToken.getAppkey(), requestToken.getGrantType());
        			
        			if(result == 0) {
        				jdbcTemplate.update(insert_sql, serverHost, serverPort, requestToken.getAppkey(), requestToken.getGrantType(),
        						reqCount, reqTime,errorTime, cycoreTime, cycoreCount, requestToken.getLastAccessTime(), new Date());
        			}
            	}
            }
        }, 60, 60, TimeUnit.SECONDS);
    	
    	LOGGER.info("启动统计服务调用信息存储数据库");
    }

    public void beforeInvoke() {
        int running = runningCountUpdater.incrementAndGet(this);

        for (;;) {
            int max = concurrentMaxUpdater.get(this);
            if (running > max) {
                if (concurrentMaxUpdater.compareAndSet(this, max, running)) {
                    break;
                } else {
                    continue;
                }
            } else {
                break;
            }
        }

        requestCount.increment();
    }

    public RequestTokenStat getRequestToken(String appkey, String grantType) {
    	return requestTokenMap.putIfAbsent(appkey, new RequestTokenStat(appkey, grantType));
    }

    public void afterInvoke(Throwable error, long nanoSpan) {
    	runningCountUpdater.decrementAndGet(this);
    }
    
    public void destroy() {
        try {
            expireFuture.cancel(true);
        } catch (Throwable t) {
        	LOGGER.warn(t.getMessage(), t);
        }
    }

    public int getRunningCount() {
        return runningCount;
    }

    public long getConcurrentMax() {
        return concurrentMax;
    }

    public long getRequestCount() {
        return requestCount.sum();
    }
}
