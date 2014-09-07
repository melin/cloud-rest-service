/**
 * Copyright (C) 2013-2014 科大讯飞股份有限公司 - All rights reserved.
 */
package com.iflytek.edu.cloud.frame.jetty;

import java.io.File;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iflytek.edu.cloud.frame.jetty.support.WebInfConfigurationExt;
import com.iflytek.edu.cloud.frame.utils.SystemPropertyUtil;

/**
 * 启动Jetty服务器。
 * 
 * @author libinsong1204@gmail.com
 *
 */
public class JettyServer {
	private static final Logger LOGGER = LoggerFactory.getLogger(JettyServer.class);
	
	public static void main(String[] args) {
		Thread.currentThread().setContextClassLoader(JettyServer.class.getClassLoader());
		
		loadPorperties();
		
		QueuedThreadPool pool = creatThreadPool();
		Server server = new Server(pool);
		
		WebAppContext context = new WebAppContext();
		context.setResourceBase(SystemPropertyUtil.get("BASE_HOME") + File.separator + "web");
		context.setContextPath("/");
		context.setConfigurations(new Configuration[]{ new WebInfConfigurationExt(), new AnnotationConfiguration()});
		context.setThrowUnavailableOnStartupException(true);
		context.setParentLoaderPriority(true);
		context.setClassLoader(JettyServer.class.getClassLoader());
		server.setHandler(context);
		
		createServerConnector(server);
		
		addShutdownHook(server);
		try {
			server.start();
			server.join();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 创建线程池
	 * @return
	 */
	private static QueuedThreadPool creatThreadPool() {
		QueuedThreadPool pool = new QueuedThreadPool();
		pool.setMaxThreads(JettyConfigUtil.getMaxThreads());
		pool.setMinThreads(JettyConfigUtil.getMinThreads());
		pool.setIdleTimeout(60000);
		pool.setDetailedDump(false);
		return pool;
	}
	
	private static void createServerConnector(Server server) {
		HttpConfiguration httpConfig = new HttpConfiguration();
		httpConfig.setSendServerVersion(false);
		HttpConnectionFactory factory = new HttpConnectionFactory(httpConfig);
		
		ServerConnector connector = new ServerConnector(server, factory);
		connector.setPort(JettyConfigUtil.getServerPort());
		connector.setAcceptQueueSize(1024);
		connector.setIdleTimeout(30000); //backlog值
		connector.setSoLingerTime(-1);
		
		server.addConnector(connector);
	}
	
	private static void loadPorperties() {
		
	}
	
	/**
     * 注册hook程序，保证线程能够完整执行。使用：kill -15 pid 关闭进程
     */
    private static void addShutdownHook(final Server server) {
        //为了保证TaskThread不在中途退出，添加ShutdownHook
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
            	LOGGER.info("收到关闭信号，hook起动，开始检测线程状态 ...");
            	
            	try {
					server.stop();
					server.destroy();
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
            	
            	System.out.println("================服务器停止成功================");
            }
        });
    }

}
