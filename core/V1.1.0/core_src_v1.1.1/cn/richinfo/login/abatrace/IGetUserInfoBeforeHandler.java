package cn.richinfo.login.abatrace;

import javax.servlet.http.HttpServletRequest;

import cn.richinfo.login.pojo.Result;

//获取用户登录信息前置方法
public interface IGetUserInfoBeforeHandler {

	public Result userInfoBeforeHandler(HttpServletRequest request);
}
