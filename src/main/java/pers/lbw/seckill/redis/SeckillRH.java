package pers.lbw.seckill.redis;

public class SeckillRH extends BaseRedisHelper {

	public SeckillRH(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}

	public static SeckillRH getSeckillPath=new SeckillRH(60,"gsp");
	public static SeckillRH getVerifyCode=new SeckillRH(300,"gvc");

}
