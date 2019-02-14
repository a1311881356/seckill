package pers.lbw.seckill.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {
	public static String md5(String src) {
		return DigestUtils.md5Hex(src);
	}
	
	//第一次MD5加密是由网页的js加密的，这里只是方便压测
	//inputPass是用户输入的密码
	public static String FirstMD5(String inputPwd) {
		//可能的一种实现是：
		String salt="1a2b3c4d";//第一次加的盐，在网页前段加的
		String str=""+salt.charAt(0)+salt.charAt(2)+inputPwd+salt.charAt(5)+salt.charAt(4);
		return md5(str);
	}
	
	//在后台存数据库前的加密
	//receivePwd是后台接收到前端发来的密码,salt是随机生成的
	public static String SecondMD5(String receivePwd,String salt) {
		//可能的一种实现是：
		String str=""+salt.charAt(0)+salt.charAt(2)+receivePwd+salt.charAt(5)+salt.charAt(4);
		return md5(str);
	}
	
	public static void main(String[] args){
		System.out.println(FirstMD5("123456"));//注册过程中抓包查彩虹表得到的密码是：12123456c3，但如果又读懂了网站前端的源码的话就能破解
		System.out.println(SecondMD5(FirstMD5("123456"),"de45hrda4"));//存数据库前
	}

}
