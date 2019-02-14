package pers.lbw.seckill.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import pers.lbw.seckill.domain.SeckillUser;
import pers.lbw.seckill.result.Result;
import pers.lbw.seckill.service.GoodsService;
import pers.lbw.seckill.service.SeckillUserService;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	SeckillUserService sus;
	
	@Autowired
	GoodsService gs;
	
	@RequestMapping("/info")
	@ResponseBody
	public Result<SeckillUser> info(SeckillUser su) {
		return Result.success(su);
	}
	
}
