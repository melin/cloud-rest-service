/**
 * 
 */
package com.iflytek.edu.cloud.oauth2.support;

import io.netty.util.internal.SystemPropertyUtil;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;

import com.github.diamond.client.netty.NetUtils;
import com.github.diamond.client.util.NamedThreadFactory;
import com.iflytek.edu.cloud.oauth2.utils.EnvUtil;
import com.iflytek.edu.cloud.oauth2.utils.JettyConfigUtil;

/**
 * Create on @2014年8月8日 @下午4:32:11 
 * @author libinsong1204@gmail.com
 */
public class JdbcRegistry implements InitializingBean {
	private static final Logger LOGGER = LoggerFactory.getLogger(JdbcRegistry.class);
	
	public static final int DEFAULT_SESSION_TIMEOUT = 60;
	
	private final ScheduledExecutorService expireExecutor = Executors.newScheduledThreadPool(1, new NamedThreadFactory("CloudServiceRegistryExpireTimer", true));
	
	private JdbcTemplate jdbcTemplate;
	
	private int expirePeriod = DEFAULT_SESSION_TIMEOUT;
	private final ScheduledFuture<?> expireFuture;
	
	private final String appInstallDir;
	private final String serverHost;
	private final int serverPort;
	private final String profile;
	private final String projectName;
	private final String buildVersion;
	private final String buildTime;
	
	private static final String INSERT_SQL = "INSERT INTO mon_app_registry(server_host,server_port,app_install_dir,"
			+ "profile,projectName,buildVersion,buildTime,registry_time) VALUES (?,?,?,?,?,?,?,?)";
	private static final String UPDATE_SQL = "UPDATE mon_app_registry SET app_install_dir = ?, PROFILE = ?, projectName = ?, "
			+ "buildVersion = ?, buildTime = ?, registry_time = ? WHERE server_host = ? AND server_port = ?";
	private static final String DELETE_SQL = "DELETE FROM mon_app_registry WHERE server_host = ? AND server_port = ?";
	
	public JdbcRegistry() {
		appInstallDir = SystemPropertyUtil.get("BASE_HOME");
		serverHost = NetUtils.getLocalHost();
		serverPort = JettyConfigUtil.getServerPort();
		profile = StringUtils.join(EnvUtil.getSpringProfiles(), ",");
		projectName = EnvUtil.getProjectName();
		buildVersion = EnvUtil.getBuildVersion();
		buildTime = EnvUtil.getBuildTime();
		
        this.expireFuture = expireExecutor.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                try {
                    deferExpired(); // 延长过期时间
                } catch (Throwable t) { // 防御性容错
                	LOGGER.error("Unexpected exception occur at defer expire time, cause: " + t.getMessage(), t);
                }
            }
        }, expirePeriod, expirePeriod, TimeUnit.SECONDS);
        
        LOGGER.info("服务器注册：" + EnvUtil.getProjectName());
    }
	
	private void deferExpired() {
        try {
        	jdbcTemplate.update(UPDATE_SQL, appInstallDir, profile, projectName, buildVersion, 
        			buildTime, new Date(), serverHost, serverPort);
        } catch (Throwable t) {
        	LOGGER.warn("Failed to write provider heartbeat to redis registry. cause: " + t.getMessage(), t);
        }
    }
	
	public void afterPropertiesSet() throws java.lang.Exception {
        boolean success = false;
        RuntimeException exception = null;
        try {
        	jdbcTemplate.update(DELETE_SQL, serverHost, serverPort);
        	jdbcTemplate.update(INSERT_SQL, serverHost, serverPort, appInstallDir, 
        			profile, projectName, buildVersion, buildTime, new Date());
            success = true;
        } catch (Throwable t) {
            exception = new RuntimeException("Failed to register service to redis registry. registry", t);
        } 
        if (exception != null) {
            if (success) {
            	LOGGER.warn(exception.getMessage(), exception);
            } else {
                throw exception;
            }
        }
	}
	
    public void destroy() {
        try {
            expireFuture.cancel(true);
        } catch (Throwable t) {
        	LOGGER.warn(t.getMessage(), t);
        }
    }

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * 单位：秒
	 * @param expirePeriod
	 */
	public void setExpirePeriod(int expirePeriod) {
		this.expirePeriod = expirePeriod;
	}
}
