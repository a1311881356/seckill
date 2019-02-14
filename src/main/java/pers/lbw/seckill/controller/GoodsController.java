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
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import pers.lbw.seckill.domain.SeckillUser;
import pers.lbw.seckill.redis.GoodsRH;
import pers.lbw.seckill.redis.RedisService;
import pers.lbw.seckill.result.Result;
import pers.lbw.seckill.service.GoodsService;
import pers.lbw.seckill.service.SeckillUserService;
import pers.lbw.seckill.vo.GoodsDetailVo;
import pers.lbw.seckill.vo.GoodsVo;

@Controller
@RequestMapping("/goods")
public class GoodsController {
	
	@Autowired
	SeckillUserService sus;
	
	@Autowired
	GoodsService gs;
	
	@Autowired
	RedisService rs;
	
	@Autowired
	ThymeleafViewResolver tvr;
	
	/*
	 * 优化前：
	 * QPS：108.8
	 * Throughput：2000*10
	 * 
	 * 页面缓存：没有参数，只需要单纯的缓存一个页面
	 * 优化后：
	 * QPS：965.4
	 * Throughput：2000*10
	 */
	@RequestMapping(value="toList",produces="text/html")
	@ResponseBody
	public String toList(Model model,SeckillUser su,HttpServletRequest req,
			HttpServletResponse resp) {
		//取缓存
		String html = rs.get(GoodsRH.getGoodsList, "", String.class);
		if(html!=null) {
			return html;
		}
		
		//查询数据库商品列表
		List<GoodsVo> goodsList = gs.getGoodsVoList();
		model.addAttribute("goodsList", goodsList);
		model.addAttribute("user", su);
		
		//手动渲染
		WebContext wc= new WebContext(req,resp,req.getServletContext(),req.getLocale(),model.asMap());
		html = tvr.getTemplateEngine().process("goodsList", wc);
		//存入redis缓存
		if(!StringUtils.isEmpty(html)) {
			rs.set(GoodsRH.getGoodsList, "", html);
		}
		return html;
	}
	
	/*
	 * URL缓存：更具参数的不同缓存了不同的页面
	 */
	@RequestMapping(value="toDetail2/{goodsId}",produces="text/html")
	@ResponseBody
	@Deprecated
	public String toDetail2(Model model,SeckillUser su,
			@PathVariable("goodsId")long goodsId,HttpServletRequest req,
			HttpServletResponse resp) {
		//取缓存，不同的详情页面又不同的缓存
		String html = rs.get(GoodsRH.getGoodsDetail, ""+goodsId, String.class);
		if(html!=null) {
			return html;
		}
		
		//snowflake生成订单
		model.addAttribute("user", su);
		GoodsVo gv=gs.getGoodsVoByGoodsId(goodsId);
		
		long startAt = gv.getStartDate().getTime();
		long endAt = gv.getEndDate().getTime();
		long now=System.currentTimeMillis();
		
		int seckillStatus=0;
		int remainSeconds=0;
		
		if(now<startAt) {//未开始
			seckillStatus=0;
			remainSeconds=(int) ((startAt-now)/1000);
		}else if(now>endAt) {//已结束
			seckillStatus=2;
			remainSeconds=-1;
		}else {//进行中
			seckillStatus=1;
			remainSeconds=0;
		}
		model.addAttribute("goods", gv);
		model.addAttribute("seckillStatus", seckillStatus);
		model.addAttribute("remainSeconds", remainSeconds);
		
		//手动渲染
		WebContext wc= new WebContext(req,resp,req.getServletContext(),req.getLocale(),model.asMap());
		html = tvr.getTemplateEngine().process("goods_detail", wc);
		//存入redis缓存
		if(!StringUtils.isEmpty(html)) {
			rs.set(GoodsRH.getGoodsDetail, ""+goodsId, html);
		}
		return html;
	}
	
	/*
	 * 页面静态化：
	 */
	@RequestMapping(value="getDetail/{goodsId}")
	@ResponseBody
	public Result<GoodsDetailVo> getDetail(SeckillUser su,@PathVariable("goodsId")long goodsId) {
		GoodsVo gv=gs.getGoodsVoByGoodsId(goodsId);
		
		long startAt = gv.getStartDate().getTime();
		long endAt = gv.getEndDate().getTime();
		long now=System.currentTimeMillis();
		
		int seckillStatus=0;
		int remainSeconds=0;
		
		if(now<startAt) {//未开始
			seckillStatus=0;
			remainSeconds=(int) ((startAt-now)/1000);
		}else if(now>endAt) {//已结束
			seckillStatus=2;
			remainSeconds=-1;
		}else {//进行中
			seckillStatus=1;
			remainSeconds=0;
		}
		
		GoodsDetailVo gdv=new GoodsDetailVo();
		gdv.setRemainSeconds(remainSeconds);
		gdv.setSeckillStatus(seckillStatus);
		gdv.setGoodsVo(gv);
		gdv.setSeckillUser(su);
		
		return Result.success(gdv);
	}
	
}
