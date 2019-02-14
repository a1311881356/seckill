package pers.lbw.seckill.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pers.lbw.seckill.domain.OrderInfo;
import pers.lbw.seckill.domain.SeckillUser;
import pers.lbw.seckill.exception.GlobalException;
import pers.lbw.seckill.redis.OrderRH;
import pers.lbw.seckill.redis.RedisService;
import pers.lbw.seckill.redis.SeckillRH;
import pers.lbw.seckill.result.CodeMsg;
import pers.lbw.seckill.util.MD5Util;
import pers.lbw.seckill.util.UUIDUtil;
import pers.lbw.seckill.util.VerifyCodeImageUtil;
import pers.lbw.seckill.vo.GoodsVo;

@Service
public class SeckillService {

	@Autowired
	GoodsService gs;

	@Autowired
	OrderService os;
	
	@Autowired
	RedisService rs;
	
	@Autowired
	VerifyCodeImageUtil vciu;

	// 原子性的操作
	@Transactional
	public OrderInfo seckill(SeckillUser user, GoodsVo goods) {
		// 1.减少库存 2.下订单 3.写入秒杀订单：需要放置到事物中
		boolean res = gs.reduceStock(goods);

		if (res == false) {
			os.setSeckillResult(user.getId(),goods.getId(),-1);
			throw new GlobalException(CodeMsg.SECKILL_OVER);
		}

		// oder_info seckill_order
		return os.createOrder(user, goods);
	}

	// 处理成功返回订单id
	// 处理失败返回-1
	// 处理中或者未处理返回0
	public long getSeckillResult(Long userId, long goodsId) {
		boolean exists = rs.exists(OrderRH.getSeckillResult, userId+""+goodsId);
		if(exists) {
			Long failOrId = rs.get(OrderRH.getSeckillResult, userId+""+goodsId,long.class);
			return failOrId;
		}else {//还未处理，返回0
			return 0;
		}
	}

	public boolean checkPath(Long userId, long goodsId, String path) {
		String path_db = rs.get(SeckillRH.getSeckillPath, userId+""+goodsId,String.class);
		if(path_db==null)return false;
		return path_db.equals(path);
	}
	
	public String createSeckillPath(long userId,long goodsId) {
		String str=MD5Util.md5(UUIDUtil.uuid()+"123456");
		rs.set(SeckillRH.getSeckillPath, userId+""+goodsId, str);
		return str;
	}

	public String createVerifyCode(SeckillUser user, long goodsId) throws IOException {
		if(goodsId<=0) {
			return null;
		}
		
		//生成数学表达式
		String exp = vciu.genMEVerifyCode(10);
		
		//计算数学表达式
		int res=vciu.calc(exp);
		//存入redis
		rs.set(SeckillRH.getVerifyCode, user.getId()+"_"+goodsId, res);
		
		String base64ImgStr= vciu.getImageBase64(150, 30, exp+"=");
		return base64ImgStr;
	}

	public boolean checkCerifyCode(SeckillUser user, long goodsId, int verifyCode) {
		if(goodsId<0)return false;
		Integer db_vc = rs.get(SeckillRH.getVerifyCode, user.getId()+"_"+goodsId, Integer.class);
		if(db_vc==null||db_vc!=verifyCode)return false;
		rs.delete(SeckillRH.getVerifyCode, user.getId()+"_"+goodsId);
		return true;
	}
}
