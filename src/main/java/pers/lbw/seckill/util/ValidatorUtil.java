package pers.lbw.seckill.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

public class ValidatorUtil {
	private static final Pattern mobile_pattern=Pattern.compile("1\\d{10}");//正则表达式匹配
	
	public static boolean isMobile(String phone) {
		if(StringUtils.isEmpty(phone)) {
			return false;
		}
		Matcher matcher = mobile_pattern.matcher(phone);
		return matcher.matches();
	}
}
