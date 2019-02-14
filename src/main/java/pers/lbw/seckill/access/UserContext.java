package pers.lbw.seckill.access;

import pers.lbw.seckill.domain.SeckillUser;

public class UserContext {
	private static ThreadLocal<SeckillUser> userHolder=new ThreadLocal<>();
	
	public static void setUser(SeckillUser user) {
		userHolder.set(user);
	}
	
	public static SeckillUser getUser() {
		return userHolder.get();
	}
}
