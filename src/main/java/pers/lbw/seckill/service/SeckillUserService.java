package pers.lbw.seckill.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import pers.lbw.seckill.util.MD5Util;
import pers.lbw.seckill.util.UUIDUtil;
import pers.lbw.seckill.vo.LoginVo;
import pers.lbw.seckill.dao.SeckillUserDao;
import pers.lbw.seckill.domain.SeckillUser;
import pers.lbw.seckill.exception.GlobalException;
import pers.lbw.seckill.redis.RedisService;
import pers.lbw.seckill.redis.SeckillUserRH;
import pers.lbw.seckill.result.CodeMsg;

@Service
public class SeckillUserService {
	public static final String COOKIE_NAME_TOKEN="token";
	
	
	@Autowired
	SeckillUserDao sud;
	
	@Autowired
	RedisService redisService;

	/**
	 * 对象缓存
	 * @param id
	 * @return
	 */
	public SeckillUser getById(long id) {
		//取缓存，永久缓存，只在更新时后移除并更新
		SeckillUser user = redisService.get(SeckillUserRH.getById, ""+id, SeckillUser.class);
		if(user!=null) {
			return user;
		}
		
		//取数据库
		user = sud.getById(id);
		
		//存缓存
		if(user!=null) {
			redisService.set(SeckillUserRH.getById, ""+id, user);
		}
		return user;
	}
	
	public boolean updatePassWord(String token,long id,String passwordNew) {
		SeckillUser user = sud.getById(id);
		if(user==null) {
			throw new GlobalException(CodeMsg.USER_NO_EXIST);
		}
		
		//更新数据库
		SeckillUser newUser=new SeckillUser();
		newUser.setId(id);
		newUser.setPassword(MD5Util.SecondMD5(passwordNew, user.getSalt()));
		sud.update(newUser);
		
		//修改缓存（包括用token为键的缓存和以id为键的缓存）
		redisService.delete(SeckillUserRH.getById, ""+id);
		user.setPassword(newUser.getPassword());
		redisService.set(SeckillUserRH.token, token,user);
		return true;
	}
	

	public String login(HttpServletResponse resp,LoginVo lv) {
		if (lv == null) {
			throw new GlobalException(CodeMsg.SERVER_ERROR);
		}
		String mobile = lv.getMobile();
		String password = lv.getPassword();
		// 判断手机号是否存在
		SeckillUser su = getById(Long.parseLong(mobile));
		if (su == null) {
			throw new GlobalException(CodeMsg.USER_NO_EXIST);
		}
		// 验证密码
		String db_pass = su.getPassword();
		String salt = su.getSalt();
		String secondMD5 = MD5Util.SecondMD5(password, salt);
		if (!secondMD5.equals(db_pass)) {
			throw new GlobalException(CodeMsg.PASSWORD_ERROR);
		}

		// 生成cookie
		String token = UUIDUtil.uuid();
		addCookie(su,token, resp);
		return token;
	}
	
	private void addCookie(SeckillUser su,String token,HttpServletResponse resp) {
		redisService.set(SeckillUserRH.token, token, su);
		Cookie cookie=new Cookie(COOKIE_NAME_TOKEN, token);
		cookie.setMaxAge(SeckillUserRH.token.expireSeconds());
		cookie.setPath("/");
		resp.addCookie(cookie);
	}

	public SeckillUser getUserByToken(String token,HttpServletResponse resp) {
		if(StringUtils.isEmpty(token)) {
			return null;
		}
		//延长有效期，要达到用户自最后一次上线后session过固定的时间后才过期，而不是根据登陆时过固定时间过期
		SeckillUser su = redisService.get(SeckillUserRH.token, token, SeckillUser.class);
		if(su!=null) {
			addCookie(su,token, resp);
		}
		return su;
	}
}
