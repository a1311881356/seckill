package pers.lbw.seckill.redis;

//User模块的前缀
public class SeckillUserRH extends BaseRedisHelper {
	
	public static final int TOKEN_EXPIRE=3600*24*2;//两天

	private SeckillUserRH(int expireSeconds,String prefix) {
		super(expireSeconds,prefix);
	}
	
	public static SeckillUserRH token=new SeckillUserRH(TOKEN_EXPIRE,"tk");
	public static SeckillUserRH getById=new SeckillUserRH(0,"id");

}
