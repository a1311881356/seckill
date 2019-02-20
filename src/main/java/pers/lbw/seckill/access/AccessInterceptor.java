package pers.lbw.seckill.access;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;

import pers.lbw.seckill.domain.SeckillUser;
import pers.lbw.seckill.redis.AccessRH;
import pers.lbw.seckill.redis.RedisService;
import pers.lbw.seckill.result.CodeMsg;
import pers.lbw.seckill.result.Result;
import pers.lbw.seckill.service.SeckillUserService;

@Service
//访问拦截器
public class AccessInterceptor extends HandlerInterceptorAdapter{
	
	@Autowired
	SeckillUserService sus;
	
	@Autowired
	RedisService rs;
	
	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler)
			throws Exception {
		if(handler instanceof HandlerMethod) {
			HandlerMethod hm=(HandlerMethod) handler;
			AccessLimit al = hm.getMethodAnnotation(AccessLimit.class);
			if(al==null) {
				return true;
			}
			boolean needLogin = al.needLogin();
			String key=req.getRequestURI();
			if(needLogin) {
				SeckillUser user= getSeckillUser(req,resp);
				if(user==null) {
					render(resp,CodeMsg.SECKILL_FAIL);
					return false;
				}
				UserContext.setUser(user);
				key+=("_"+user.getId());
			}
			int seconds = al.seconds();
			int maxCount = al.maxCount();
			
			//对于没登陆的用户，也会限流，次数是所有匿名用户共享的
			AccessRH access = AccessRH.withExpire(seconds);
			Integer count = rs.get(access, key, Integer.class);
			if(count==null) {
				rs.set(access, key, 1);
			}else if(count<=maxCount) {
				rs.inc(access, key);
			}else {
				render(resp,CodeMsg.ACCESS_LIMIT_REACHED);
				return false;
			}
		}
		return true;
	}
	
	//render:给予; 使成为; 递交; 表达;
	private void render(HttpServletResponse resp, CodeMsg cm) throws Exception{
		resp.setContentType("application/json;charset=UTF-8");
		ServletOutputStream out = resp.getOutputStream();
		String str = JSON.toJSONString(Result.error(cm));
		out.write(str.getBytes("UTF-8"));
		out.flush();
		out.close();
	}

	private SeckillUser getSeckillUser(HttpServletRequest req, HttpServletResponse resp) {
		String paraToken = req.getParameter(SeckillUserService.COOKIE_NAME_TOKEN);//兼容手机客户端
		String cookieToken = getCookieValue(req,SeckillUserService.COOKIE_NAME_TOKEN);
		if(StringUtils.isEmpty(paraToken)&&StringUtils.isEmpty(cookieToken)) {
			return null;
		}
		String token=StringUtils.isEmpty(paraToken)?cookieToken:paraToken;
		SeckillUser u=sus.getUserByToken(token,resp);
		return u;
	}
	
	private String getCookieValue(HttpServletRequest req, String cookieName) {
		Cookie[] cookies = req.getCookies();
		if(cookies!=null) {
			for (Cookie c : cookies) {
				if(c.getName().equals(cookieName))return c.getValue();
			}
		}
		return null;
	}
}
