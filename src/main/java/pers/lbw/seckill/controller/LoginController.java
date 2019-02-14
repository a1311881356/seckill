package pers.lbw.seckill.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
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
	Result<String> doLogin(@Valid LoginVo lv,HttpServletResponse resp,BindingResult  bindingResult) {
		logger.info(lv.toString());
		if (bindingResult.hasErrors()) {
            List<ObjectError> errorList = bindingResult.getAllErrors();
		}
		String token=sus.login(resp,lv);
		return Result.success(token);
	}
	
}
