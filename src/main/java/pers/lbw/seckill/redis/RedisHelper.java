package pers.lbw.seckill.redis;

public interface RedisHelper {
	
	//获得过期时间
	int expireSeconds();
	
	//获得前缀
	String prefix();
}
