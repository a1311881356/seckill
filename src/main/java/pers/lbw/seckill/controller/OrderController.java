package pers.lbw.seckill.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import pers.lbw.seckill.domain.OrderInfo;
import pers.lbw.seckill.domain.SeckillUser;
import pers.lbw.seckill.redis.GoodsRH;
import pers.lbw.seckill.redis.RedisService;
import pers.lbw.seckill.result.CodeMsg;
import pers.lbw.seckill.result.Result;
import pers.lbw.seckill.service.GoodsService;
import pers.lbw.seckill.service.OrderService;
import pers.lbw.seckill.service.SeckillUserService;
import pers.lbw.seckill.vo.GoodsDetailVo;
import pers.lbw.seckill.vo.GoodsVo;
import pers.lbw.seckill.vo.OrderDetailVo;

@Controller
@RequestMapping("/order")
public class OrderController {
	
	@Autowired
	SeckillUserService sus;
	
	@Autowired
	GoodsService gs;
	
	@Autowired
	RedisService rs;
	
	@Autowired
	OrderService orderService;
	
	@RequestMapping(value="detail")
	@ResponseBody
	public Result<OrderDetailVo> toList(SeckillUser user,@RequestParam("orderId")long orderId) {
		if(user==null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		OrderInfo order=orderService.getOrderById(orderId);
		
		if(order==null) {
			return Result.error(CodeMsg.ORDER_NOT_EXIST);
		}
		
		Long goodsId = order.getGoodsId();
		GoodsVo goods = gs.getGoodsVoByGoodsId(goodsId);
		OrderDetailVo odv=new OrderDetailVo();
		odv.setGoods(goods);
		odv.setOrder(order);
		return Result.success(odv);
	}
}
