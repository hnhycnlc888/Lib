package cn.richinfo.login.abatrace;

import javax.servlet.http.HttpServletRequest;

import cn.richinfo.login.pojo.Result;
import cn.richinfo.login.pojo.UserInfo;

//获取用户登录信息后置方法
public interface IGetUserInfoPostHandler {
	public Result userInfoPostHandler(HttpServletRequest request,UserInfo userInfo);
}
