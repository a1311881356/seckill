package pers.lbw.seckill.controller;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import pers.lbw.seckill.redis.RedisService;
import pers.lbw.seckill.result.Result;
import pers.lbw.seckill.service.SeckillUserService;
import pers.lbw.seckill.vo.LoginVo;

@Controller
@RequestMapping("/login")
public class LoginController {
	
	private Logger logger=LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	SeckillUserService sus;
	
	@Autowired
	RedisService redisService;

	@RequestMapping("toLogin")
	String toLogin() {
		return "login";
	}
	
	@RequestMapping("doLogin")
	@ResponseBody
	Result<String> doLogin(@Valid LoginVo lv,HttpServletResponse resp) {
		logger.info(lv.toString());
		/*//参数校验
		String pwd = lv.getPassword();
		String mobile = lv.getMobile();
		if(StringUtils.isEmpty(pwd)) {
			return Result.error(CodeMsg.PASSWORD_EMPTY);
		}
		if(StringUtils.isEmpty(mobile)) {
			return Result.error(CodeMsg.MOBILE_EMPTY);
		}
		//判断手机号格式
		if(!ValidatorUtil.isMobile(mobile)) {
			return Result.error(CodeMsg.MOBILE_ERROR);
		}
		//登陆
		System.out.println(">>>>");*/
		String token=sus.login(resp,lv);
		return Result.success(token);
	}
	
}
