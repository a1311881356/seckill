package pers.lbw.seckill.result;

public class CodeMsg {
	private int code;
	public static CodeMsg getSUCCESS() {
		return SUCCESS;
	}

	private String msg;

	// 通用异常
	public static CodeMsg SUCCESS = new CodeMsg(0, "成功");
	public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务器异常！");
	public static CodeMsg BIND_ERROR = new CodeMsg(500101, "参数校验异常:%s");
	public static CodeMsg REQUEST_ILLEGAL = new CodeMsg(500102, "验证码错误");
	public static CodeMsg ACCESS_LIMIT_REACHED = new CodeMsg(500103, "访问太频繁了");

	// 登陆模块 5002xx
	public static CodeMsg SESSION_ERROR = new CodeMsg(500201, "Session不存在或者实效");
	public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500202, "密码不能为空");
	public static CodeMsg MOBILE_EMPTY = new CodeMsg(500203, "手机号不能为空");
	public static CodeMsg MOBILE_ERROR = new CodeMsg(500204, "手机号错误");
	public static CodeMsg USER_NO_EXIST = new CodeMsg(500205, "手机号不存在");
	public static CodeMsg PASSWORD_ERROR = new CodeMsg(500206, "密码错误");

	// 商品模块 5003xx

	// 订单模块 5004xx
	public static CodeMsg ORDER_NOT_EXIST =new CodeMsg(500400, "订单不存在");

	// 秒杀模块 5005xx
	public static CodeMsg SECKILL_OVER =new CodeMsg(500500, "商品秒杀完毕");
	public static CodeMsg SECKILL_REPEATE =new CodeMsg(500501, "不能重复秒杀");
	public static CodeMsg SECKILL_FAIL =new CodeMsg(500502, "秒杀失败");

	private CodeMsg(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	
	//这里是新创建一个对象返回，不因该在原来的对象上修改返回，因为原来的对象是静态的，而且只有一个，改了下次就用不了了
	public CodeMsg fillArgs(Object...args) {
		String new_msg=String.format(msg, args);
		return new CodeMsg(code, new_msg);
	}

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

}
