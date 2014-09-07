借鉴[ROP](https://github.com/itstamen/rop "ROP")的思路开发的rest服务框架，在Spring mvc基础上实现的。

# **DEMO** #

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