/**
 * 
 */
package com.iflytek.edu.cloud.frame.spring;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Create on @2014年8月1日 @上午11:32:55 
 * @author libinsong1204@gmail.com
 */
public class RedisTokenStore implements TokenStore {
	private static final Log LOG = LogFactory.getLog(RedisTokenStore.class);
	
	private JedisPool jedisPool;
	
	private static final byte[] ACCESS_TOKEN_KEY = toBytes("access_token_key");
	private static final byte[] ACCESS_TOKEN_AUTH_KEY = toBytes("access_token_auth_key");
	private static final byte[] ACCESS_REFRESH_CODE_KEY = toBytes("access_refresh_code_key");
	private static final byte[] ACCESS_AUTH_ID_KEY = toBytes("access_refresh_code_key");
	
	//private static final byte[] ACCESS_CLIENT_ID_KEY = toBytes("access_client_id_key");
	//private static final byte[] ACCESS_USERNAME_CLIENTID_KEY = toBytes("access_username_clientid_key");
	
	private static final byte[] REFRESH_TOKEN_KEY = toBytes("refresh_token_key");
	private static final byte[] REFRESH_TOKEN_AUTH_KEY = toBytes("refresh_token_auth_key");
	
	private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();
	
	public RedisTokenStore(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	@Override
	public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
		return readAuthentication(token.getValue());
	}

	@Override
	public OAuth2Authentication readAuthentication(String tokenValue) {
		OAuth2Authentication authentication = null;
		String tokenKey = extractTokenKey(tokenValue);
		Jedis jedis = jedisPool.getResource();
		try {
			byte[] values = jedis.hget(ACCESS_TOKEN_AUTH_KEY, toBytes(tokenKey));
			
			if(values != null)
				authentication = deserializeAuthentication(values);
			else {
				if (LOG.isInfoEnabled()) {
					LOG.info("Failed to find access token for token " + tokenValue);
				}
			}
		} catch (IllegalArgumentException e) {
			LOG.warn("Failed to deserialize authentication for " + tokenValue, e);
			removeAccessToken(tokenValue);
		} finally {
			jedisPool.returnResource(jedis);
		} 
		return authentication;
	}

	@Override
	public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
		String refreshToken = null;
		if (token.getRefreshToken() != null) {
			refreshToken = token.getRefreshToken().getValue();
		}
		
		if (readAccessToken(token.getValue()) != null) {
			removeAccessToken(token.getValue());
		}
		String tokenKey = extractTokenKey(token.getValue());
		
		Jedis jedis = jedisPool.getResource();
		try {
			if (token.getRefreshToken() != null) {
				refreshToken = token.getRefreshToken().getValue();
			}
			
			if (readAccessToken(token.getValue())!=null) {
				removeAccessToken(token.getValue());
			}

			jedis.hset(ACCESS_TOKEN_KEY, toBytes(tokenKey), serializeAccessToken(token));
			jedis.hset(ACCESS_TOKEN_AUTH_KEY, toBytes(tokenKey), serializeAuthentication(authentication));
			jedis.hset(ACCESS_REFRESH_CODE_KEY, toBytes(refreshToken), toBytes(token.getValue()));
			jedis.hset(ACCESS_AUTH_ID_KEY, toBytes(authenticationKeyGenerator.extractKey(authentication)), toBytes(token.getValue()));
		} finally {
			jedisPool.returnResource(jedis);
		} 
	}

	@Override
	public OAuth2AccessToken readAccessToken(String tokenValue) {
		OAuth2AccessToken accessToken = null;

		String tokenKey = extractTokenKey(tokenValue);
		Jedis jedis = jedisPool.getResource();
		try {
			byte[] values = jedis.hget(ACCESS_TOKEN_KEY, toBytes(tokenKey));
			if(values != null)
				accessToken = deserializeAccessToken(values);
			else {
				if (LOG.isInfoEnabled()) {
					LOG.info("Failed to find access token for token " + tokenValue);
				}
			}
		} catch (IllegalArgumentException e) {
			LOG.warn("Failed to deserialize access token for token " + tokenValue, e);
			removeAccessToken(tokenValue);
		} finally {
			jedisPool.returnResource(jedis);
		} 

		return accessToken;
	}

	@Override
	public void removeAccessToken(OAuth2AccessToken token) {
		removeAccessToken(token.getValue());
	}
	
	public void removeAccessToken(String tokenValue) {
		String tokenKey = extractTokenKey(tokenValue);
		
		OAuth2Authentication authentication = readAuthentication(tokenValue);
		OAuth2AccessToken token = readAccessToken(tokenValue);
		String refreshToken = token.getRefreshToken().getValue();
		
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.hdel(ACCESS_TOKEN_KEY, toBytes(tokenKey));
			jedis.hdel(ACCESS_TOKEN_AUTH_KEY, toBytes(tokenKey));
			jedis.hdel(ACCESS_REFRESH_CODE_KEY, toBytes(refreshToken));
			jedis.hdel(ACCESS_AUTH_ID_KEY, toBytes(authenticationKeyGenerator.extractKey(authentication)));
		} finally {
			jedisPool.returnResource(jedis);
		} 
	}

	@Override
	public void storeRefreshToken(OAuth2RefreshToken refreshToken,
			OAuth2Authentication authentication) {
		String refreshTokenKey = extractTokenKey(refreshToken.getValue());
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.hset(REFRESH_TOKEN_KEY, toBytes(refreshTokenKey), serializeRefreshToken(refreshToken));
			jedis.hset(REFRESH_TOKEN_AUTH_KEY, toBytes(refreshTokenKey), serializeAuthentication(authentication));
		} finally {
			jedisPool.returnResource(jedis);
		} 
	}

	@Override
	public OAuth2RefreshToken readRefreshToken(String tokenValue) {
		OAuth2RefreshToken refreshToken = null;
		
		String tokenKey = extractTokenKey(tokenValue);
		Jedis jedis = jedisPool.getResource();
		try {
			byte[] values = jedis.hget(REFRESH_TOKEN_KEY, toBytes(tokenKey));
			if(values != null)
				refreshToken = deserializeRefreshToken(values);
			else {
				if (LOG.isInfoEnabled()) {
					LOG.info("Failed to find refresh token for token " + tokenValue);
				}
			}
		} catch (IllegalArgumentException e) {
			LOG.warn("Failed to deserialize refresh token for token " + tokenValue, e);
			removeAccessToken(tokenValue);
		} finally {
			jedisPool.returnResource(jedis);
		} 

		return refreshToken;
	}

	@Override
	public OAuth2Authentication readAuthenticationForRefreshToken(
			OAuth2RefreshToken token) {
		return readAuthenticationForRefreshToken(token.getValue());
	}
	
	public OAuth2Authentication readAuthenticationForRefreshToken(String tokenValue) {
		OAuth2Authentication authentication = null;

		String tokenKey = extractTokenKey(tokenValue);
		Jedis jedis = jedisPool.getResource();
		try {
			byte[] values = jedis.hget(REFRESH_TOKEN_AUTH_KEY, toBytes(tokenKey));
			if(values != null)
				authentication = deserializeAuthentication(values);
			else {
				if (LOG.isInfoEnabled()) {
					LOG.info("Failed to find access token for token " + tokenValue);
				}
			}
		} catch (IllegalArgumentException e) {
			LOG.warn("Failed to deserialize access token for token " + tokenValue, e);
			removeAccessToken(tokenValue);
		} finally {
			jedisPool.returnResource(jedis);
		}

		return authentication;
	}

	@Override
	public void removeRefreshToken(OAuth2RefreshToken token) {
		removeRefreshToken(token.getValue());
	}

	public void removeRefreshToken(String tokenValue) {
		String tokenKey = extractTokenKey(tokenValue);
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.hdel(REFRESH_TOKEN_KEY, toBytes(tokenKey));
			jedis.hdel(REFRESH_TOKEN_AUTH_KEY, toBytes(tokenKey));
		} finally {
			jedisPool.returnResource(jedis);
		} 
	}

	@Override
	public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
		removeAccessTokenUsingRefreshToken(refreshToken.getValue());
	}

	public void removeAccessTokenUsingRefreshToken(String refreshToken) {
		Jedis jedis = jedisPool.getResource();
		try {
			byte[] tokenValue = jedis.hget(ACCESS_REFRESH_CODE_KEY, toBytes(refreshToken));
			removeAccessToken(new String(tokenValue, Charset.forName("UTF-8")));
		} finally {
			jedisPool.returnResource(jedis);
		} 
	}

	@Override
	public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
		OAuth2AccessToken accessToken = null;
		String key = authenticationKeyGenerator.extractKey(authentication);
		Jedis jedis = jedisPool.getResource();
		try {
			byte[] tokenValue = jedis.hget(ACCESS_AUTH_ID_KEY, toBytes(key));
			if(tokenValue != null)
				accessToken = readAccessToken(new String(tokenValue, Charset.forName("UTF-8")));
		} finally {
			jedisPool.returnResource(jedis);
		} 
		if (accessToken != null
				&& !key.equals(authenticationKeyGenerator.extractKey(readAuthentication(accessToken.getValue())))) {
			removeAccessToken(accessToken.getValue());
			storeAccessToken(accessToken, authentication);
		}
		return accessToken;
	}

	@Override
	public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(
			String clientId, String userName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
		throw new UnsupportedOperationException();
	}

	//----------------------------------------------------------------------------
	
	private static byte[] toBytes(String value) {
		return value.getBytes(Charset.forName("UTF-8"));
	}

	protected byte[] serializeAccessToken(OAuth2AccessToken token) {
		return SerializationUtils.serialize(token);
	}

	protected byte[] serializeRefreshToken(OAuth2RefreshToken token) {
		return SerializationUtils.serialize(token);
	}

	protected byte[] serializeAuthentication(OAuth2Authentication authentication) {
		return SerializationUtils.serialize(authentication);
	}

	protected OAuth2AccessToken deserializeAccessToken(byte[] token) {
		return SerializationUtils.deserialize(token);
	}

	protected OAuth2RefreshToken deserializeRefreshToken(byte[] token) {
		return SerializationUtils.deserialize(token);
	}

	protected OAuth2Authentication deserializeAuthentication(byte[] authentication) {
		return SerializationUtils.deserialize(authentication);
	}
	
	protected String extractTokenKey(String value) {
		if (value == null) {
			return null;
		}
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
		}

		try {
			byte[] bytes = digest.digest(value.getBytes("UTF-8"));
			return String.format("%032x", new BigInteger(1, bytes));
		}
		catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
		}
	}
}
