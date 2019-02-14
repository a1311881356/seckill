package pers.lbw.seckill.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.util.StringUtils;

import pers.lbw.seckill.util.ValidatorUtil;
//自定义验证器
//IsMobile是被校验的注解，String是用的时候IsMobile修饰字段的类型，Constraint：约束
//所以这里是对String字段上加了IsMobile注解的进行参数校验
public class IsMobileValidator implements ConstraintValidator<IsMobile,String>{

	private boolean required;
	
	@Override
	//这里的value就是注解所在字段的值
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if(required) {
			return ValidatorUtil.isMobile(value);//偷个懒，直接用我们之前写的来校验
		}else {
			//非必须的话如果为空按照逻辑的话因该返回true，也就是合法
			if(StringUtils.isEmpty(value)) {
				return true;
			}else {
				return ValidatorUtil.isMobile(value);//偷个懒，直接用我们之前写的来校验
			}
		}
	}
	
	@Override
	public void initialize(IsMobile constraintAnnotation) {
		required=constraintAnnotation.reqired();
	}

}
