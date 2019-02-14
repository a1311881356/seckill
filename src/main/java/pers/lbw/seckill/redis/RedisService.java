package pers.lbw.seckill.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RedisService {

	@Autowired
	JedisPool jedisPool;

	public <T> T get(RedisHelper rh, String key, Class<T> clazz) {
		if (clazz == null||key==null||rh==null) {
			return null;
		}
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			// 生成真正的key
			String realKey = rh.prefix() + key;
			String res = jedis.get(realKey);
			T t = parseStringToBean(res, clazz);
			return t;
		} finally {// 使用完后返回到连接池
			returnToPool(jedis);
		}
	}

	public <T> boolean set(RedisHelper rh, String key, T value) {
		if (value == null||key==null||rh==null) {
			return false;
		}
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			String str_value = parseBeanToString(value);
			// 生成真正的key
			String realKey = rh.prefix() + key;
			int seconds = rh.expireSeconds();
			if(seconds<=0) {
				jedis.set(realKey, str_value);
			}else {
				jedis.setex(realKey, seconds, str_value);
			}
			return true;
		} finally {// 使用完后返回到连接池
			returnToPool(jedis);
		}
	}
	
	public boolean exists(RedisHelper rh,String key) {
		if (key==null||rh==null) {
			return false;
		}
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			// 生成真正的key
			String realKey = rh.prefix() + key;
			return jedis.exists(realKey);
		} finally {// 使用完后返回到连接池
			returnToPool(jedis);
		}
	}
	
	public boolean delete(RedisHelper rh,String key) {
		if (key==null||rh==null) {
			return false;
		}
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			// 生成真正的key
			String realKey = rh.prefix() + key;
			Long del = jedis.del(realKey);
			return del>0;
		} finally {// 使用完后返回到连接池
			returnToPool(jedis);
		}
	}
	
	public Long inc(RedisHelper rh, String key) {
		if (key==null||rh==null) {
			return null;
		}
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			// 生成真正的key
			String realKey = rh.prefix() + key;
			return jedis.incr(realKey);
		} finally {// 使用完后返回到连接池
			returnToPool(jedis);
		}
	}
	
	public Long decr(RedisHelper rh, String key) {
		if (key==null||rh==null) {
			return null;
		}
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			// 生成真正的key
			String realKey = rh.prefix() + key;
			return jedis.decr(realKey);
		} finally {// 使用完后返回到连接池
			returnToPool(jedis);
		}
	}

	public static <T> String parseBeanToString(T value) {
		Class<?> clazz = value.getClass();
		if (clazz == String.class) {
			return value.toString();
		} else if (clazz == int.class || clazz == Integer.class) {
			return value.toString();
		} else if (clazz == long.class || clazz == Long.class) {
			return value.toString();
		} else if (clazz == double.class || clazz == Double.class) {
			return value.toString();
		} else if (clazz == float.class || clazz == Float.class) {
			return value.toString();
		} else if (clazz == byte.class || clazz == Byte.class) {
			return value.toString();
		} else if (clazz == char.class || clazz == Character.class) {
			return value.toString();
		} else if (clazz == boolean.class || clazz == Boolean.class) {
			return value.toString();
		} else {
			// 其他情况视为可转换为json字符串的对象,用fastjson转成字符串
			return JSON.toJSONString(value);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T parseStringToBean(String res, Class<T> clazz) {
		if(res==null)return null;
		if (clazz == String.class) {
			return (T) res;
		} else if (clazz == int.class || clazz == Integer.class) {
			return (T) Integer.valueOf(res);
		} else if (clazz == long.class || clazz == Long.class) {
			return (T) Long.valueOf(res);
		} else if (clazz == double.class || clazz == Double.class) {
			return (T) Double.valueOf(res);
		} else if (clazz == float.class || clazz == Float.class) {
			return (T) Float.valueOf(res);
		} else if (clazz == byte.class || clazz == Byte.class) {
			return (T) Byte.valueOf(res);
		} else if (clazz == short.class || clazz == Short.class) {
			return (T) Short.valueOf(res);
		} else if (clazz == char.class || clazz == Character.class) {
			return (T) Character.valueOf(res.charAt(0));
		} else if (clazz == boolean.class || clazz == Boolean.class) {
			return (T) Boolean.valueOf(res);
		} else {
			// 其他情况视为字符串可转换成bean，使用fastjson把字符串转成bean
			return JSON.toJavaObject(JSON.parseObject(res), clazz);
		}
	}

	private void returnToPool(Jedis jedis) {
		if (jedis != null) {
			jedis.close();
		}
	}

}
