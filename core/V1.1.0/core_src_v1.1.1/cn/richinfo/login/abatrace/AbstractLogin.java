package cn.richinfo.login.abatrace;

import java.util.Calendar;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.druid.util.StringUtils;

import yzkf.app.Memcached;
import yzkf.utils.Utility;

import cn.richinfo.core.utils.lang.CollectionHelper;
import cn.richinfo.core.utils.web.WebUtils;
import cn.richinfo.login.ConfigHelper;
import cn.richinfo.login.pojo.Result;
import cn.richinfo.login.pojo.UserInfo;

public abstract class AbstractLogin {

	private List<ILoginBeforeHandler> loginBeforeHandlerList;
	private List<ILoginPostHandler> loginPostHandlerList;

	private List<IGetUserInfoBeforeHandler> getUserInfoBeforeHandlerList;
	private List<IGetUserInfoPostHandler> getUserInfoPostHandlerList;

	public void setLoginBeforeHandlerList(List<ILoginBeforeHandler> loginBeforeHandlerList) {
		this.loginBeforeHandlerList = loginBeforeHandlerList;
	}

	public void setLoginPostHandlerList(List<ILoginPostHandler> loginPostHandlerList) {
		this.loginPostHandlerList = loginPostHandlerList;
	}

	public void setGetUserInfoBeforeHandlerList(List<IGetUserInfoBeforeHandler> getUserInfoBeforeHandlerList) {
		this.getUserInfoBeforeHandlerList = getUserInfoBeforeHandlerList;
	}

	public void setGetUserInfoPostHandlerList(List<IGetUserInfoPostHandler> getUserInfoPostHandlerList) {
		this.getUserInfoPostHandlerList = getUserInfoPostHandlerList;
	}

	protected abstract Result doLogin(HttpServletRequest request, HttpServletResponse response,
			String loginName, String loginPassword, String verifyCode, String loginType);

	protected abstract void doLogOut(HttpServletRequest request, HttpServletResponse responese);

	protected abstract UserInfo doGetUserInfo(HttpServletRequest request);

	protected abstract UserInfo doGetUserInfo(HttpServletRequest request, String sid, String rmkey);

	protected abstract Result doGetSMS(HttpServletRequest request, String loginName);

	public Result login(HttpServletRequest request, HttpServletResponse response, String loginName,
			String loginPassword, String verifyCode, String loginType) {
		Result loginResult = new Result();

		// 登录前置条件的判断
		if (CollectionHelper.isNotEmpty(loginBeforeHandlerList)) {
			for (ILoginBeforeHandler beforeHandler : this.loginBeforeHandlerList) {
				if (beforeHandler != null) {
					Result beforeResult = beforeHandler.loginBeforeHandle(request, verifyCode, loginName,
							loginType);
					if (beforeResult.isOK() == false) {
						return beforeResult;
					}
				}
			}
		}

		// 登录方法
		loginResult = this.doLogin(request, response, loginName, loginPassword, verifyCode, loginType);
		if (!loginResult.isOK()) {
			this.addErrorTimes(loginName);
			return loginResult;
		}

		// 登录后置条件的判断
		if (CollectionHelper.isNotEmpty(loginPostHandlerList)) {
			for (ILoginPostHandler handler : loginPostHandlerList) {
				if (handler != null) {
					Result postResult = handler.loginPostHandle(request, loginResult.getUserInfo(),
							loginName, loginType);
					if (postResult.getUserInfo() != null) {
						loginResult.setUserInfo(postResult.getUserInfo());
					}
					if (postResult.isOK() == false) {
						return postResult;
					}
				}
			}
		}
		// 记录cookie
		this.addCookie(request, response, loginResult.getUserInfo());

		return loginResult;
	}

	public void logout(HttpServletRequest request, HttpServletResponse response) {
		this.doLogOut(request, response);
	}

	public UserInfo getUserInfo(HttpServletRequest request, HttpServletResponse response, String sid, String rmkey) {
		// 获取用户信息前置条件的判断
		if (CollectionHelper.isNotEmpty(getUserInfoBeforeHandlerList)) {
			for (IGetUserInfoBeforeHandler beforehandler : getUserInfoBeforeHandlerList) {
				if (beforehandler != null) {
					Result beforeResult = beforehandler.userInfoBeforeHandler(request);
					if (beforeResult.isOK() == false)
						return null;
				}
			}
		}

		// 获取用户信息方法
		UserInfo userInfo = this.doGetUserInfo(request, sid, rmkey);
		if (userInfo == null)
			return userInfo;

		// 获取用户信息后置条件的判断
		if (CollectionHelper.isNotEmpty(getUserInfoPostHandlerList)) {
			for (IGetUserInfoPostHandler handler : getUserInfoPostHandlerList) {
				if (handler != null) {
					Result postResult = handler.userInfoPostHandler(request, userInfo);
					if (postResult.getUserInfo() != null) {
						userInfo = postResult.getUserInfo();
					}
					if (postResult.isOK() == false)
						return null;
				}
			}
		}
		// 记录cookie
		this.addCookie(request, response, userInfo);
		return userInfo;
	}

	public UserInfo getUserInfo(HttpServletRequest request, HttpServletResponse response) {
		// 获取用户信息前置条件的判断
		if (CollectionHelper.isNotEmpty(getUserInfoBeforeHandlerList)) {
			for (IGetUserInfoBeforeHandler beforehandler : getUserInfoBeforeHandlerList) {
				if (beforehandler != null) {
					Result result = beforehandler.userInfoBeforeHandler(request);
					if (result.isOK() == false)
						return null;
				}
			}
		}

		// 获取用户信息方法
		UserInfo userInfo = this.doGetUserInfo(request);
		if (userInfo == null)
			return userInfo;

		// 获取用户信息后置条件的判断
		if (CollectionHelper.isNotEmpty(getUserInfoPostHandlerList)) {
			for (IGetUserInfoPostHandler handler : getUserInfoPostHandlerList) {
				if (handler != null) {
					Result postResult = handler.userInfoPostHandler(request, userInfo);
					if (postResult.getUserInfo() != null) {
						userInfo = postResult.getUserInfo();
					}
					if (postResult.isOK() == false)
						return null;
				}
			}
		}
		// 记录cookie
		this.addCookie(request, response, userInfo);
		return userInfo;
	}

	public Result getSms(HttpServletRequest request, String loginName) {
		return this.doGetSMS(request, loginName);
	}

	protected boolean addCookie(HttpServletRequest request, HttpServletResponse response, UserInfo userInfo) {
		if (!StringUtils.isEmpty(userInfo.getRmkey())
				&& !WebUtils.getCookieValue(request, "RMKEY").equals(userInfo.getRmkey())) {
			String mailDomain = ConfigHelper.getInstance().readLogin("Login/mailDomain");
			String[] arrDomain = mailDomain.split(",");
			for (int i = 0; i < arrDomain.length; i++) {
				// 将rmkey写入cookie
				Cookie cookie = new Cookie("RMKEY", userInfo.getRmkey());
				cookie.setPath("/");
				cookie.setDomain(arrDomain[i]);
				cookie.setMaxAge(24 * 60 * 60);
				cookie.setHttpOnly(true);
				response.addCookie(cookie);
			}
		}
		return true;
	}

	protected void addErrorTimes(String loginName) {
		String CACHE_FAILED_TIMES = "login_failed_times_";
		Memcached.getInstance().addOrIncr(CACHE_FAILED_TIMES + loginName, 1L,
				Utility.getDateWithoutTime(Calendar.DAY_OF_YEAR, 1));
	}

}
