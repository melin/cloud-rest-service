借鉴[ROP](https://github.com/itstamen/rop "ROP")的思路开发的rest服务框架，在Spring mvc基础上实现的，集成了oauth2认证。

基于微服务架构服务平台（MSA），服务按照业务模块化划分粒度，支持服务快速开发、构建、部署。例如有多个业务服务，划分为多个java工程，一个工程独立部署，通过nginx+lua实现访问入口统一：

nginx+lua配置

```sh
location = /api {
    rewrite_by_lua '
        local reqType = ngx.req.get_method();
        local method = "";
        if reqType == "POST" then
            ngx.req.read_body();
            local params = ngx.req.get_post_args();
            method = params.method;
	        if not method then
                method = ngx.req.get_uri_args().method; 
	        end;
        else
            local params = ngx.req.get_uri_args();
            method = params.method;
        end
        
        if string.find(method, "pan") == 1 then
            ngx.req.set_uri("/pan_instance", true);
	    elseif string.find(method, "res") == 1 then
            ngx.req.set_uri("/res_instance", true);
        else
            ngx.req.set_uri("/core_instance", true);
        end
    ';
} 
```

# **rest-demo** #

## server ##
```java
@RestController
public class UserService {

	@ServiceMethod(value="user.get", version="1.0.0", 
			grantType={GrantType.CLIENT, GrantType.PASSWORD})
	public User findUser(long id) {
		User user = new User();
		user.setAddress("合肥");
		user.setPhone("055163592110");
		user.setUsername("melin");
		return user;
	}
}
```
## client ##
```java
public class UserServiceTest {
	public static void main(String[] args) {
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
}
```