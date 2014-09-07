/**
 * 
 */
package com.iflytek.rest.demo;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;

/**
 * Create on @2014年7月30日 @下午5:05:52 
 * @author libinsong1204@gmail.com
 */
public class UserServiceTest {
	public static final String OAUTH_SERVER_URL = "http://localhost:9090/oauth2/oauth/token";
	//public static final String OAUTH_SERVER_URL = "http://192.168.63.173:8090/oauth2/oauth/token";
	public static void main(String[] args) {
		//String access_token = requestAccessToken();
		//System.out.println(access_token);
		requestRestApi("xxx");
	}
	public static void requestRestApi(String access_token) {
		RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
        form.add("method", "user.get");
        form.add("version", "1.0.0");
        form.add("locale", "zh_CN");
        form.add("format", "json");
        form.add("appkey", "Hb0YhmOo"); //-Dexcludes.appkey=Hb0YhmOo
        form.add("access_token", access_token);
        
        form.add("id", "10001");
        String result = restTemplate.postForObject("http://localhost:8090/api", form, String.class);
        System.out.println(result);
	}
	
	public static String requestAccessToken() {
		RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
        form.add("client_id", "Hb0YhmOo");
        form.add("client_secret", "R7odNVS0KPtgXJ1TKQbHAxFP6EHdSW5d");
        form.add("grant_type", "client_credentials"); //可选值为：password & client_credentials
        
        //grant_type为client_credentials，不需要提交username & password参数
        form.add("username", "admin_test_333");
        form.add("password", "passw0rd");

        String result = restTemplate.postForObject(OAUTH_SERVER_URL, form, String.class);
        System.out.println(result);
        String access_token = JSON.parseObject(result).getString("access_token");
        return access_token;
	}
}
