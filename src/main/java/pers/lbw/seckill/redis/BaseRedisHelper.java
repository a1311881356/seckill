package pers.lbw.seckill.redis;

public abstract class BaseRedisHelper implements RedisHelper {
	
	private int expireSeconds;
	private String prefix;
	
	public BaseRedisHelper(String prefix) {//默认0代表永远不过期
		this(0,prefix);
	}
	
	public BaseRedisHelper(int expireSeconds, String prefix) {
		this.expireSeconds = expireSeconds;
		this.prefix = prefix;
	}

	@Override
	public int expireSeconds() {//默认0代表永远不过期
		return expireSeconds;
	}

	@Override
	public String prefix() {
		String name = getClass().getSimpleName();
		return name+":"+prefix;
	}

}
