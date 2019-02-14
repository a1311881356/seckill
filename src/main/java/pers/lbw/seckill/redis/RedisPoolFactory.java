package pers.lbw.seckill.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisPoolFactory {

	@Autowired
	RedisConfig redisConfig;

	// 创建一个bean，这样就可以直接被注入到其他地方了
	@Bean
	public JedisPool JedisPoolFactory() {
		JedisPoolConfig jpc = new JedisPoolConfig();
		jpc.setMaxIdle(redisConfig.getPoolMaxIdle());
		jpc.setMaxTotal(redisConfig.getPoolMaxTotal());
		jpc.setMaxWaitMillis(redisConfig.getPoolMaxWait() * 1000);// 这里的单位由秒变毫秒
		JedisPool jp = new JedisPool(jpc, redisConfig.getHost(), redisConfig.getPort(), redisConfig.getTimeout() * 1000,
				redisConfig.getPassword(), 0);// 0表示使用redis的0号库
		return jp;
	}
}
