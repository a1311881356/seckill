package pers.lbw.seckill.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pers.lbw.seckill.dao.OrderDao;
import pers.lbw.seckill.domain.OrderInfo;
import pers.lbw.seckill.domain.SeckillOrder;
import pers.lbw.seckill.domain.SeckillUser;
import pers.lbw.seckill.redis.OrderRH;
import pers.lbw.seckill.redis.RedisService;
import pers.lbw.seckill.vo.GoodsVo;

@Service
public class OrderService {

	@Autowired
	OrderDao od;

	@Autowired
	RedisService redisService;

	public SeckillOrder getSeckillOrderByUserIdAndGoodsId(Long userId, long goodsId) {
		// ADD:增加订单缓存功能
		SeckillOrder order = redisService.get(OrderRH.getSeckillOrderByUserIdAndGoodsId, userId + "_" + goodsId,
				SeckillOrder.class);
		if (order != null) {
			return order;
		}
		order = od.getSeckillOrderByUserIdAndGoodsId(userId, goodsId);
		if (order != null) {
			redisService.set(OrderRH.getSeckillOrderByUserIdAndGoodsId, "" + userId + "_" + goodsId, order);
		}
		return order;
	}

	@Transactional
	public OrderInfo createOrder(SeckillUser user, GoodsVo goods) {
		// order_info
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setCreateDate(new Date());
		orderInfo.setDeliveryAddrId(0L);
		orderInfo.setGoodsCount(1);
		orderInfo.setGoodsId(goods.getId());
		orderInfo.setGoodsName(goods.getGoodsName());
		orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
		orderInfo.setOrderChannel(1);
		orderInfo.setStatus(0);
		orderInfo.setUserId(user.getId());
		od.insertOrderInfo(orderInfo);

		// seckill_order
		SeckillOrder sOrder = new SeckillOrder();
		sOrder.setGoodsId(goods.getId());
		sOrder.setOrderId(orderInfo.getId());
		sOrder.setUserId(user.getId());
		od.insertSeckillOrder(sOrder);

		redisService.set(OrderRH.getSeckillOrderByUserIdAndGoodsId, user.getId() + "_" + goods.getId(), orderInfo);
		return orderInfo;
	}

	// 不存在表示排队中、-1表示失败，非-1表示成功
	public void setSeckillResult(long userId, long goodsId, long status) {
		redisService.set(OrderRH.getSeckillResult, userId + "" + goodsId, status);
	}

	public OrderInfo getOrderById(long orderId) {
		return od.getOrderById(orderId);
	}

}
