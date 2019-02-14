package pers.lbw.seckill.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import pers.lbw.seckill.access.AccessInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer{
	
	@Autowired
	UserArgumentResolver userArgumentResolver;
	
	@Autowired
	AccessInterceptor accessInterceptor;
	
	//注册解析器
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(userArgumentResolver);
	}
	
	//注册拦截器
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(accessInterceptor);
	}
}
