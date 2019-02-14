package pers.lbw.seckill.redis;

//User模块的前缀
public class GoodsRH extends BaseRedisHelper {
	
	public static final int TOKEN_EXPIRE=60;//60秒

	private GoodsRH(int expireSeconds,String prefix) {
		super(expireSeconds,prefix);
	}
	
	private GoodsRH(String prefix) {
		super(prefix);
	}
	
	public static GoodsRH getGoodsList=new GoodsRH(TOKEN_EXPIRE,"gl");
	public static GoodsRH getGoodsDetail=new GoodsRH(TOKEN_EXPIRE,"gd");
	public static GoodsRH getSeckillGoodsStock=new GoodsRH("sgs");

}
