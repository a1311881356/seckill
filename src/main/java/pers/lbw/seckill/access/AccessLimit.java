package pers.lbw.seckill.access;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
public @interface AccessLimit {
	
	//每多少秒
	int seconds();
	
	//限流多少次
	int maxCount();
	
	//是否需要登陆
	boolean needLogin() default true;
}
