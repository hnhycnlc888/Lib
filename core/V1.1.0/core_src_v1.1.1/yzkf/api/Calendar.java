package yzkf.api;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;

import cn.richinfo.core.utils.web.HttpClient;
import yzkf.api.result.CalendarResult;
import yzkf.app.Pattern;
import yzkf.config.ApiConfig;
import yzkf.config.ConfigFactory;
import yzkf.exception.ApiException;
import yzkf.exception.ParserConfigException;

public class Calendar {
	private ApiConfig config;
	public Calendar() throws ParserConfigException{
		ConfigFactory factory = ConfigFactory.getInstance();
		this.config = factory.newApiConfig();
	}
	public Calendar(ApiConfig config){
		this.config = config;
	}
	/**
	 * 订阅日程
	 * @param mobile 用户手机号
	 * @param labelId 日程编号
	 * @return
	 * @throws ApiException
	 * @exmaple
	 * <pre>
		Calendar api = new Calendar();
		try {
			CalendarResult result = api.subscribeLabel("13418161431", 5380);
			if(result.isOK()){
				System.out.println("订阅成功");
			}else{
				System.out.println("订阅成失败:"+result.getDescr());
			}
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 * </pre>
	 */
	public CalendarResult subscribeLabel(String mobile,int labelId) throws ApiException{
		if(StringUtils.isEmpty(mobile))
			return CalendarResult.EmptyMobile;	//空账户		
		if(!Pattern.isMobile139alias(mobile))
			return CalendarResult.InvalidMobile;	//无效账号
		
		String strRequestData = MessageFormat.format("<object><int name=\"comeFrom\">{0}</int><int name=\"labelId\">{1}</int><string name=\"color\">#ff0000</string></object>", 
				this.config.Calendar_Comefrom,labelId);
		String url = config.Calendar_BaseUrl+"api:subscribeLabel&account="+mobile+"&rnd="+new Random().nextFloat();
		String out = null;
		try {
			out = HttpClient.post(url,strRequestData,config.Calendar_Encoding);
		} catch (IOException e) {
			ApiException ex = new ApiException("订阅日程接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}
		JSONObject outJson = JSONObject.parseObject(out);

		String outRetCode = outJson.getString("code");;
		String outRetMsg = outJson.getString("summary");;

		if(outRetCode.equalsIgnoreCase("S_OK")){				
			return CalendarResult.OK;
		}else{
			//返回未知结果
			return CalendarResult.Other.setValue("["+outRetCode+"]"+outRetMsg);
		}	
	}
}
