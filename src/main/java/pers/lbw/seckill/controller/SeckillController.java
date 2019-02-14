package pers.lbw.seckill.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import pers.lbw.seckill.access.AccessLimit;
import pers.lbw.seckill.domain.OrderInfo;
import pers.lbw.seckill.domain.SeckillOrder;
import pers.lbw.seckill.domain.SeckillUser;
import pers.lbw.seckill.rabbitmq.MQSender;
import pers.lbw.seckill.rabbitmq.SeckillMessage;
import pers.lbw.seckill.redis.GoodsRH;
import pers.lbw.seckill.redis.OrderRH;
import pers.lbw.seckill.redis.RedisService;
import pers.lbw.seckill.result.CodeMsg;
import pers.lbw.seckill.result.Result;
import pers.lbw.seckill.service.GoodsService;
import pers.lbw.seckill.service.OrderService;
import pers.lbw.seckill.service.SeckillService;
import pers.lbw.seckill.vo.GoodsVo;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean{
	
	@Autowired
	GoodsService gs;
	
	@Autowired
	OrderService os;
	
	@Autowired
	SeckillService ss;
	
	@Autowired
	RedisService rs;
	
	@Autowired
	MQSender mQSender;
	
	@Autowired
	JedisPool jedisPool;
	
	private Map<Long,Boolean> localOverMap=new HashMap<>();

	//ADD
	@PostMapping(value="/{path}/doSeckill")
	@ResponseBody
	public Result<Integer> doSeckill(SeckillUser user,@RequestParam("goodsId")long goodsId,@PathVariable("path")String path) {
		if(user==null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		
		//验证path
		if(ss.checkPath(user.getId(),goodsId,path)==false) {
			return Result.error(CodeMsg.REQUEST_ILLEGAL);
		}
		
		//预先减库存
		long stock=rs.decr(GoodsRH.getSeckillGoodsStock, goodsId+"");
		if(stock<0) {
			return Result.error(CodeMsg.SECKILL_OVER);
		}
		
		//判断是否已经秒杀过了
		SeckillOrder order=os.getSeckillOrderByUserIdAndGoodsId(user.getId(),goodsId);
		if(order!=null) {
			return Result.error(CodeMsg.SECKILL_REPEATE);
		}
		
		//请求入队
		SeckillMessage sm=new SeckillMessage();
		sm.setUser(user);
		sm.setGoodsId(goodsId);
		mQSender.sendSeckillMsg(sm);
		
		//排队中
		return Result.success(0);
	}
	//ADD
	/**
	 * 系统初始化时加载秒杀到redis中
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		//将秒杀商品加载到redis缓存中
		List<GoodsVo> goodsVoList = gs.getGoodsVoList();
		if(goodsVoList==null) {
			return;
		}
		for (GoodsVo goodsVo : goodsVoList) {
			rs.set(GoodsRH.getSeckillGoodsStock, goodsVo.getId()+"", goodsVo.getStockCount());
			localOverMap.put(goodsVo.getId(), false);
		}
	}
	
	//ADD
	//成功返回订单id
	//失败返回-1
	//排队中返回0
	@RequestMapping("getSeckillResult")
	@ResponseBody
	public Result<Long> getSeckillResult(SeckillUser user,@RequestParam("goodsId")long goodsId){
		if(user==null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		
		long result=ss.getSeckillResult(user.getId(),goodsId);
		return Result.success(result);
	}
	
	@AccessLimit(seconds=5,maxCount=5,needLogin=true)
	@RequestMapping("getSeckillPath")
	@ResponseBody
	public Result<String> getSeckillPath(HttpServletRequest req,SeckillUser user,@RequestParam("goodsId")long goodsId
			,@RequestParam("verifyCode")int verifyCode){
		if(user==null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		
		//限流防刷：限制访问次数
		//设置五秒钟最多访问2次
		/*String uri = req.getRequestURI();
		String key=uri+"_"+user.getId();
		Integer count = rs.get(AccessKey.access, key, Integer.class);
		if(count==null) {
			rs.set(AccessKey.access, key, 1);
		}else if(count<=2) {
			rs.inc(AccessKey.access, key);
		}else {
			return Result.error(CodeMsg.ACCESS_LIMIT_REACHED);
		}*/
		
		//检查验证码
		if(ss.checkCerifyCode(user,goodsId,verifyCode)==false) {
			return Result.error(CodeMsg.REQUEST_ILLEGAL);
		}
		
		String str=ss.createSeckillPath(user.getId(),goodsId);
		return Result.success(str);
	}
	
	@RequestMapping("clean")
	@ResponseBody
	public Result<String> clean() throws Exception{
		afterPropertiesSet();
		Jedis jedis = jedisPool.getResource();
		Set<String> keys = jedis.keys("*"+OrderRH.getSeckillOrderByUserIdAndGoodsId.prefix()+"*");
		for (String key : keys) {
			jedis.del(key);
		}
		Set<String> keys2 = jedis.keys("*"+OrderRH.getSeckillResult.prefix()+"*");
		for (String key : keys2) {
			jedis.del(key);
		}
		return Result.success("清除秒杀成功。");
	}
	
	@RequestMapping("verifyCode")
	@ResponseBody
	public Result<String> verifyCode(SeckillUser user,@RequestParam("goodsId")long goodsId){
		if(user==null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		String base64ImgStr;
		try {
			base64ImgStr = ss.createVerifyCode(user,goodsId);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error(CodeMsg.SECKILL_FAIL);
		}
		return Result.success(base64ImgStr);
	}
}
