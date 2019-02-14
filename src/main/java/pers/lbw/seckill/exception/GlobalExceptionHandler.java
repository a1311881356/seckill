package pers.lbw.seckill.exception;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

import pers.lbw.seckill.result.CodeMsg;
import pers.lbw.seckill.result.Result;

//全局异常拦截器
@ControllerAdvice
public class GlobalExceptionHandler {
	
	//对JSR校验错误抛出的异常进行拦截，不然的话后台只会返回个400给前端
	@ExceptionHandler(value=Exception.class)
	@ResponseBody
	public Result<String> exceptionHandler(HttpServletRequest req,Exception ex){
		//打印异常
		ex.printStackTrace();
		//处理自定义的异常
		if(ex instanceof GlobalException) {
			GlobalException ge=(GlobalException) ex;
			return Result.error(ge.getCm());
		}else if(ex instanceof BindException) {
			BindException bex=(BindException) ex;
			List<ObjectError> allErrors = bex.getAllErrors();
			String arg = allErrors.get(0).getDefaultMessage();
			return Result.error(CodeMsg.BIND_ERROR.fillArgs(arg));
		} else {
			return Result.error(CodeMsg.SERVER_ERROR);
		}
	}
	
}
