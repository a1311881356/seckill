package pers.lbw.seckill.redis;

//访问控制redis存储帮助者
public class AccessRH extends BaseRedisHelper {
	
	private AccessRH(int expireSeconds,String prefix) {
		super(expireSeconds,prefix);
	}
	
	private AccessRH(String prefix) {
		super(prefix);
	}
	
	public static AccessRH withExpire(int expireSeconds) {
		return new AccessRH(expireSeconds,"access");
	}

}
