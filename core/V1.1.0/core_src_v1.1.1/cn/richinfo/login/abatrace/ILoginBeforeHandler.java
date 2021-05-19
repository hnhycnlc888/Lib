package cn.richinfo.login.abatrace;

import javax.servlet.http.HttpServletRequest;

import cn.richinfo.login.pojo.Result;


//登录前置方法
public interface ILoginBeforeHandler {

	/**
	 * 登录前置操作
	 * @return 
	 */
	public Result loginBeforeHandle(HttpServletRequest request,String verifyCode,String loginName,String loginType);
	
}
