package cn.richinfo.login.abatrace;

import javax.servlet.http.HttpServletRequest;

import cn.richinfo.login.pojo.Result;
import cn.richinfo.login.pojo.UserInfo;

////登录后置方法
public interface ILoginPostHandler {

	public Result loginPostHandle(HttpServletRequest request, UserInfo userInfo, String loginName,
			String loginType);

}
