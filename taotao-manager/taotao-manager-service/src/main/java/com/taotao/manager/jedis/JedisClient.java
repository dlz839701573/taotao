package com.taotao.manager.jedis;

public interface JedisClient {

	String set(String key, String value);
	String get(String key);
	Boolean exists(String key);
	//过期时间设置
	Long expire(String key, int seconds);
	//查看过期时间还有多少
	Long ttl(String key);
	Long incr(String key);
	Long hset(String key, String field, String value);
	String hget(String key, String field);	
	Long hdel(String key,String... field);//删除hkey,...表示随便写
	
}
