package pers.lbw.seckill.redis;

//Order模块的前缀
public class OrderRH extends BaseRedisHelper {

	private OrderRH(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}
	
	private OrderRH(String prefix) {
		super(prefix);
	}

	public static OrderRH getSeckillOrderByUserIdAndGoodsId=new OrderRH("gsobuag");
	
	public static OrderRH getSeckillResult=new OrderRH("gsr");
}
