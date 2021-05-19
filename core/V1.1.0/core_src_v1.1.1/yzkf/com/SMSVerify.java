package yzkf.com;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.richinfo.core.mybatis.extend.dao.MybaitsDao;
import cn.richinfo.core.mybatis.extend.utils.MybatisDAOHelper;
import cn.richinfo.core.spring.SpringContext;

import yzkf.api.SMS;
import yzkf.api.VerifyCode;
import yzkf.api.result.Result;
import yzkf.api.result.SMSResult;
import yzkf.api.result.SMSVerifyResult;
import yzkf.api.result.VerifyResult;
import yzkf.app.Pattern;
import yzkf.config.ProjectConfig;
import yzkf.exception.ApiException;

/**
 * 短信验证码下发、校验类
 * @author qiulw
 * @version v1.0.0 2014.04.16
 */
public class SMSVerify {
	private static final Logger LOG = LoggerFactory.getLogger(SMSVerify.class);
	private SMSVerify(){
	}
	private static SMSVerify instance;
	/**
	 * 获取短信验证码类的单例
	 * @return 短信验证码类的单例
	 */
	public static SMSVerify getInstance() {
		if(instance == null)
			instance = new SMSVerify();
		return instance;
	}
	private char[] codeChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
            'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'P', 'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'Z', 'X', 'C', 'V', 'B', 'N', 'M', 
            'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'p', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'z', 'x', 'c', 'v', 'b', 'n', 'm' };
	/**
	 * 校验短信验证码是否正确
	 * @param mobile 手机号码
	 * @param smsvcode 用户输入短信验证码字符串
	 * @return 验证码结果，当{@link Result#isOK()} == true 时校验通过，否则校验失败。失败友好提示通过{@link Result#getDescr()}可以获取
	 */
	public Result check(String mobile,String smsvcode){
		if(!Pattern.isMobile(mobile))
			return SMSVerifyResult.InvalidMobile;
		if(StringUtils.isEmpty(smsvcode))
			return SMSVerifyResult.NullInput;
//		OutObject<BigInteger> outRet = new OutObject<BigInteger>(OracleTypes.VARCHAR);
//		DBAccess dao = new DBAccess();
		Map<String, Object> pm = new HashMap<String, Object>();
		try {
			pm.put("i_mobile", mobile);
			pm.put("i_randomcode", smsvcode);
			SpringContext.getBean(MybaitsDao.class).selectOne("Yzkf.smsVerifyCreateCode", pm);
//			dao.procedure("{call PKG_Y_PUBLIC_SMSVERIFY.CreateCode(?,?,?)}", 
//					mobile,
//					smsvcode,
//					outRet);
		} catch (Exception e) {
			LOG.error("记录登录信息异常", e);
			return SMSVerifyResult.Unknow;
		}
		//数据库返回值： (1) 验证码正确， (2) 验证码错误，（3） 验证码已失效
//		int dbResult = outRet.getValue().intValue();
		int dbResult = MybatisDAOHelper.getIntValue(pm, "O_Return");
		if(dbResult == 1)
			return SMSVerifyResult.OK;
		if(dbResult == 2)
			return SMSVerifyResult.Wrong;
		if(dbResult==3)
			return SMSVerifyResult.Expired;
		return SMSVerifyResult.Wrong;
	}
	/**
	 *  获取并下发短信验证码到用户手机，无需验证图形验证码
	 * @param mobile 用户手机号
	 * @param clientIP 用户IP地址
	 * @return 获取并下发验证码结果，当{@link Result#isOK()} == true 下发成功，否则发送失败。失败友好提示通过{@link Result#getDescr()}可以获取
	 * @throws ApiException
	 */
	public Result send(String mobile,String clientIP) throws ApiException
	{
		return send(mobile, clientIP, null, null, false);
	} 
	/**
	 *  获取并下发短信验证码到用户手机，需验证图形验证码
	 * @param mobile 用户手机号
	 * @param clientIP 用户IP地址
	 * @param agentID 图形验证码的标识，来自cookie
	 * @param imgVCode 图形验证码
	 * @return 获取并下发验证码结果，当{@link Result#isOK()} == true 下发成功，否则发送失败。失败友好提示通过{@link Result#getDescr()}可以获取
	 * @throws ApiException
	 */
	public Result send(String mobile,String clientIP, String agentID, String imgVCode) throws ApiException
	{
		return send(mobile, clientIP, agentID, imgVCode, true);
	}
	/**
	 * 获取并下发短信验证码到用户手机
	 * @param mobile 用户手机号
	 * @param clientIP 用户IP地址
	 * @param agentID 图形验证码的标识，来自cookie
	 * @param imgVCode 图形验证码
	 * @param isCheckImg 获取短信验证码是否需要校验图形验证码
	 * @return 获取并下发验证码结果，当{@link Result#isOK()} == true 下发成功，否则发送失败。失败友好提示通过{@link Result#getDescr()}可以获取
	 * @throws ApiException
	 */
	protected Result send(String mobile,String clientIP, String agentID, String imgVCode,boolean isCheckImg) throws ApiException
    {
		if(!Pattern.isMobile(mobile))
			return SMSVerifyResult.InvalidMobile;
        if (isCheckImg)
        {
        	VerifyResult imgVCResult = VerifyCode.validate(agentID, imgVCode, mobile, clientIP);
            if(!imgVCResult.isOK())
            	return imgVCResult;
        }
        ProjectConfig config = ProjectConfig.getInstance();
        String smsvcode = createCode(6);
        int expireMin = config.getSmsVerifyExpire();
//        DBAccess dao = new DBAccess();
//		OutObject<String> outRet = new OutObject<String>(OracleTypes.VARCHAR);
        Map<String, Object> pm = new HashMap<String, Object>();
		try {
			pm.put("i_mobile", mobile);
			pm.put("i_randomcode", smsvcode);
			pm.put("i_expiresec", expireMin * 60);
			pm.put("I_IP", clientIP);
			SpringContext.getBean(MybaitsDao.class).selectOne("Yzkf.smsVerifyCreateCode", pm);
//			dao.procedure("{call PKG_Y_PUBLIC_SMSVERIFY.CreateCode(?,?,?,?,?)}", 
//					mobile,
//					smsvcode,
//					expireMin * 60,
//					clientIP,
//					outRet);
		} catch (Exception e) {
			LOG.error("记录登录信息异常", e);
			return SMSVerifyResult.Unknow;
		}
//        if (!outRet.getValue().equals("0"))
        if (!MybatisDAOHelper.getStringValue(pm, "O_Return").equals("0"))
        {
            String content = config.getSmsVerifyContent().replace("{VC}", smsvcode).replace("{MIN}", String.valueOf(expireMin));
            //发送短信
            SMS api = new SMS();
			api.setClientIP(clientIP);
			api.setSendMobile(mobile);
			api.setSpNumber(config.getSmsVerifySpnumber());
			api.setSendMsg(content);
			api.addReceive(mobile);
			SMSResult result = api.send();

            return result;
        }
        else
        {
            return SMSVerifyResult.Illegal;	//获取频繁
        }

    }
	/**
	 * 生成一串随即的验证码字符串
	 * @param charNum 验证码字符串的字符数
	 * @return
	 */
	 private String createCode(int charNum)
     {

         StringBuilder vc = new StringBuilder();
         Random rand = new Random();

         for (int i = 0; i < charNum; i++)
         {
             int c = rand.nextInt(codeChars.length);
             vc.append(codeChars[c]);
         }
         return vc.toString();
     }
}
