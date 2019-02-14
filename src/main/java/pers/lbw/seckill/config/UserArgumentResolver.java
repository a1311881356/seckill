package pers.lbw.seckill.config;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import pers.lbw.seckill.access.UserContext;
import pers.lbw.seckill.domain.SeckillUser;
import pers.lbw.seckill.service.SeckillUserService;

//在参数注入之前配置参数的解析
@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver{
	
	@Autowired
	SeckillUserService sus;

	//对那种类型进行参数解析
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> type = parameter.getParameterType();
		return type==SeckillUser.class;
	}

	//解析参数
	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		SeckillUser user = UserContext.getUser();
		if(user!=null) {
			return user;
		}
		
		HttpServletRequest req = webRequest.getNativeRequest(HttpServletRequest.class);
		HttpServletResponse resp = webRequest.getNativeResponse(HttpServletResponse.class);
		String paramToken = req.getParameter(SeckillUserService.COOKIE_NAME_TOKEN);
		String cookieToken = getCookieValue(req, SeckillUserService.COOKIE_NAME_TOKEN);
		if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
			return null;
		}
		String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
		return sus.getUserByToken(token,resp);
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
