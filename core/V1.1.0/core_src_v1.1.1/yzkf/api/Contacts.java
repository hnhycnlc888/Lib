package yzkf.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.richinfo.core.utils.web.HttpClient;
import yzkf.api.result.ContactResult;
import yzkf.app.Pattern;
import yzkf.config.ApiConfig;
import yzkf.config.ConfigFactory;
import yzkf.exception.ApiException;
import yzkf.exception.AppException;
import yzkf.exception.ParserConfigException;
import yzkf.model.ContactInfo;
import yzkf.utils.TryParse;
import yzkf.utils.Xml;

/**
 * 139邮箱通讯录接口类
 * @author qiulw
 * @version V4.0.0
 */
public class Contacts {
	private ApiConfig config;
	public Contacts() throws ParserConfigException{
		this(ConfigFactory.getInstance().newApiConfig());
	}
	public Contacts(ApiConfig config){
		this.config = config;
		ContactInfo.ImageUrlBase = this.config.Contact_HeaderImageBaseUrl;
	}
	private static final String DATA_GETALL = "<?xml version=\"1.0\" encoding=\"{encoding}\"?><QueryAllContacts Page=\"{Page}\" Record=\"{Record}\"><UserNumber>{Mobile}</UserNumber></QueryAllContacts>";
	private static final String DATA_GETALLBYNAME = "<?xml version=\"1.0\" encoding=\"{encoding}\"?><QueryContactsByName Page=\"{Page}\" Record=\"{Record}\"><UserNumber>{Mobile}</UserNumber><FirstNameword>{FirstNameword}</FirstNameword></QueryContactsByName>";
	private int totalRecord;
	private List<ContactInfo> list;
	/**
	 * @return 通讯录总记录数，必须先执行 {@link #getAll(String, int, int)} 后才会有数值
	 */
	public int getTotalRecord() {
		return totalRecord;
	}
	/**
	 * @return 通讯录列表，必须执行查询方法后才会有值
	 */
	public List<ContactInfo> getList(){
		return list;
	}
	/**
	 * 增加用户自身信息
	 * @param mobile 用户自己的手机号码
	 * @param info 需要增加的信息
	 * @return 返回接口执行结果{@link ContactResult} 
	 * @see #setMySelfInfo(String, ContactInfo, boolean)
	 * @throws ApiException
	 * <pre>
Contacts api = new Contacts();
ContactResult result = null;
try {
	ContactInfo bean = new ContactInfo();
	bean.setOtherEmail("abc@qq.com");
	bean.setBusinessMobile("13900001111");
	bean.setAddrFirstName("ModFirstName");
	bean.setAddrNickName("ModeNickName");
	bean.setAddrSecondName("ModSecondName");
	result = api.addMySelfInfo("13730124054", bean,true);
} catch (ApiException e) {
	e.printStackTrace();
}
if(result.isOK()){
	System.out.println("接口执行成功");
}else{
	System.out.println("接口执行失败："+result.getDescr());
	 * </pre>
	 */
	public ContactResult addMySelfInfo(String mobile,ContactInfo info) throws ApiException{
		return setMySelfInfo(mobile, info, true);
	}
	/**
	 * 修改用户自身信息
	 * @param mobile 用户自己的手机号码
	 * @param info 需要修改的信息
	 * @return 返回接口执行结果{@link ContactResult} 
	 * @see #setMySelfInfo(String, ContactInfo, boolean)
	 * @throws ApiException
	 * <pre>
Contacts api = new Contacts();
ContactResult result = null;
try {
	ContactInfo bean = new ContactInfo();
	bean.setOtherEmail("abc@qq.com");
	bean.setBusinessMobile("13900001111");
	bean.setAddrFirstName("ModFirstName");
	bean.setAddrNickName("ModeNickName");
	bean.setAddrSecondName("ModSecondName");
	result = api.modMySelfInfo("13730124054", bean,true);
} catch (ApiException e) {
	e.printStackTrace();
}
if(result.isOK()){
	System.out.println("接口执行成功");
}else{
	System.out.println("接口执行失败："+result.getDescr());
	 * </pre>
	 */
	public ContactResult modMySelfInfo(String mobile,ContactInfo info) throws ApiException{
		return setMySelfInfo(mobile, info, false);
	}
	/**
	 * 增加或修改用户自身信息
	 * @param mobile 用户手机号码
	 * @param bean 用户信息对象
	 * @param isAdd true 增加，false 修改
	 * @return 返回接口执行结果{@link ContactResult} 。当返回值  {@link ContactResult#isOK()} == true ，则标识接口执行成功，否则失败，通过 {@link ContactResult#getDescr()} 获取失败描述
	 * @throws ApiException
	 * @example
	 * <pre>
Contacts api = new Contacts();
ContactResult result = null;
try {
	ContactInfo bean = new ContactInfo();
	bean.setOtherEmail("abc@qq.com");
	bean.setBusinessMobile("13900001111");
	bean.setAddrFirstName("ModFirstName");
	bean.setAddrNickName("ModeNickName");
	bean.setAddrSecondName("ModSecondName");
	result = api.setMySelfInfo("13730124054", bean,true);
} catch (ApiException e) {
	e.printStackTrace();
}
if(result.isOK()){
	System.out.println("接口执行成功");
}else{
	System.out.println("接口执行失败："+result.getDescr());
	 * </pre>
	 */
	public ContactResult setMySelfInfo(String mobile,ContactInfo info,boolean isAdd) throws ApiException{
		if(StringUtils.isEmpty(mobile))
			return ContactResult.EmptyMobile;	//空账户		
		if(!Pattern.isMobile139alias(mobile))
			return ContactResult.InvalidMobile;	//无效账号
		info.setUserNumber(mobile);
		StringBuilder strRequestData = new StringBuilder();
		try {
			strRequestData.append("<?xml version=\"1.0\" encoding=\""+config.Contact_Encoding+"\"?>");	
			strRequestData.append(Xml.beanToXmlString(info, ContactInfo.class, (isAdd?"AddUserInfo":"ModUserInfo")));
//			strRequestData.insert(13,"<UserNumber>"+mobile+"</UserNumber>");
//			strRequestData.insert(0,"<?xml version=\"1.0\" encoding=\""+config.Contact_Encoding+"\"?>");			
		} catch (AppException e) {
			ApiException ex = new ApiException("增加或修改用户自身信息接口将ContactInfo序列化为XML时异常");				
			ex.initCause(e);
			throw ex;
		}
		String out = null;
		try {
			out = HttpClient.post(isAdd?config.Contact_AddUserInfo:config.Contact_ModUserInfo,strRequestData.toString(),config.Contact_Encoding);
			//out = uncompressOutSimpleXml(out);
		} catch (IOException e) {
			ApiException ex = new ApiException("增加或修改用户自身信息接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}
		Xml outXml = null;
		try {
			outXml = Xml.parseXml(out);
		}catch(Exception e){
			ApiException ex = new ApiException("增加或修改用户自身信息接口解析返回值为XML时发生异常");				
			ex.initCause(e);
			throw ex;
		}	
		String outRetCode = "";
		String outRetMsg = "";
		try {
			outRetCode = outXml.evaluate("//ResultCode");
			outRetMsg = outXml.evaluate("//ResultMsg");
			
			if(outRetCode.equals("0")){			
				return ContactResult.OK;
			}else{
				//返回未知结果
				return ContactResult.Other.setValue("["+outRetCode+"]"+outRetMsg);
			}
		} catch (XPathExpressionException e) {
			ApiException ex = new ApiException("增加或修改用户自身信息接口解获取返回XML指定属性时发生异常："+out);				
			ex.initCause(e);
			throw ex;
		}
		
	}
	/**
	 * 获取用户自身信息
	 * @param mobile 用户自己的手机号码
	 * @return 返回{@link ContactResult} 。当返回值  {@link ContactResult#isOK()} == true ，则可以通过ContactInfo outBean = {@link ContactResult#getValue(ContactInfo.Class)} 获取到返回的联系人信息，outBean有可能会是null，当为null时表示无用户自身信息
	 * @throws ApiException
	 * @Example
	 * <pre>
Contacts api = new Contacts();
ContactResult result = null;
try {
	result = api.getMySelfInfo(mobile);
} catch (ApiException e) {
	e.printStackTrace();
	return;
}
try{
	if(result.isOK()){
		ContactInfo info = result.getValue(ContactInfo.class);
		if(info != null){
			BeanInfo beanInfo = Introspector.getBeanInfo(ContactInfo.class);
			PropertyDescriptor[] pd = beanInfo.getPropertyDescriptors();
			for(PropertyDescriptor p : pd){
				System.out.println(p.getName() + "=" + p.getReadMethod().invoke(info) + ";");
			}
		}
	}else{
		System.out.println("接口执行失败："+result.getDescr());
	}
}catch(Exception ex){
	ex.printStackTrace();
}
	 * </pre>
	 */
	public ContactResult getMySelfInfo(String mobile) throws ApiException{
		if(StringUtils.isEmpty(mobile))
			return ContactResult.EmptyMobile;	//空账户		
		if(!Pattern.isMobile139alias(mobile))
			return ContactResult.InvalidMobile;	//无效账号
		
		String strRequestData = "<QueryUserInfo><UserNumber>"+mobile+"</UserNumber></QueryUserInfo>";

		String out = null;
		try {
			out = HttpClient.post(config.Contact_QueryUserInfo,strRequestData,config.Contact_Encoding);
			out = uncompressOutSimpleXml(out);
		} catch (IOException e) {
			ApiException ex = new ApiException("获取用户自身信息接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}
		Xml outXml = null;
		try {
			outXml = Xml.parseXml(out);
		}catch(Exception e){
			ApiException ex = new ApiException("获取用户自身信息接口解析返回值为XML时发生异常");				
			ex.initCause(e);
			throw ex;
		}	
		String outRetCode = "";
		String outRetMsg = "";
		try {

			outRetCode = outXml.evaluate("/QueryUserInfoResp/ResultCode");
			outRetMsg = outXml.evaluate("/QueryUserInfoResp/ResultMsg");
			//QueryContactsByNameResp
			
			if(outRetCode.equals("0")){			

				Node node = (Node) outXml.evaluate("/QueryUserInfoResp/UserInfo", XPathConstants.NODE);
				if(node == null)
					return ContactResult.OK.setValue(null);
				ContactInfo info = Xml.nodeToBean(node, ContactInfo.class);				
				return ContactResult.OK.setValue(info);
			}else{
				//返回未知结果
				return ContactResult.Other.setValue("["+outRetCode+"]"+outRetMsg);
			}
		} catch (XPathExpressionException e) {
			ApiException ex = new ApiException("获取用户自身信息接口获取返回XML指定属性时发生异常："+out);				
			ex.initCause(e);
			throw ex;
		} catch (AppException e) {
			ApiException ex = new ApiException("获取用户自身信息接口将返回值XML序列化为ContactInfo时异常："+out);				
			ex.initCause(e);
			throw ex;
		}
	}
	/**
	 * 查找用户通讯录中含139邮箱地址的联系人信息列表
	 * @see {@link Contacts#search(String, String, int, long, int, int) search(mobile, "@139.com", 1, 0, page, size)}
	 * @param mobile 用户手机号
	 * @param page 分页查询的页面索引
	 * @param size 分页查询的页面记录数
	 * @return 接口执行结果枚举{@link ContactResult}。当{@link ContactResult#isOK()} == true 时， {@link ArrayList} list = {@link ContactResult#getValue(Class) ContactResult.getValue(ArrayList<ContactInfo>.class)} 可获取到联系人列表信息
	 * @throws ApiException
	 * @example
	 * <pre>
Contacts api = new Contacts();
ContactResult result = null;
try {
	result = api.get139List("13730124054", 1, 100);
} catch (ApiException e) {
	e.printStackTrace();
	return;
}
try{
	if(result.isOK()){
		System.out.println("通讯录总数：" +api.getTotalRecord());//只有查询第一页时才返回
		ArrayList<ContactInfo> list = result.getValue(ArrayList.class);
		Iterator<ContactInfo> itr = list.iterator();
		BeanInfo beanInfo = Introspector.getBeanInfo(ContactInfo.class);
		PropertyDescriptor[] pd = beanInfo.getPropertyDescriptors();
		while(itr.hasNext()){
			ContactInfo info = itr.next();
			for(PropertyDescriptor p : pd){
				System.out.print(p.getName() + "=" + p.getReadMethod().invoke(info) + ";");
			}
			System.out.println("");
		}
	}else{
		System.out.println("接口执行失败："+result.getDescr());
	}
}catch(Exception ex){
	ex.printStackTrace();
}
	 * </pre>
	 */
	public ContactResult get139List(String mobile,int page,int size) throws ApiException{
		return search(mobile, "139.com", 1, 0, page, size);
	}
	/**
	 * 搜索联系人信息（精简字段）
	 * @see #search(String, String, int, long, int, int)
	 * @param mobile 用户手机号码
	 * @param searchContent 搜索关键字，可以为空
	 * @param page 分页索引
	 * @param size 每页记录数
	 * @return 接口执行结果枚举{@link ContactResult}。当{@link ContactResult#isOK()} == true 时， {@link ArrayList} list = {@link ContactResult#getValue(Class) ContactResult.getValue(ArrayList<ContactInfo>.class)} 可获取到联系人列表信息
	 * @throws ApiException
	 * 
	 */
	public ContactResult search(String mobile,String searchContent,int page,int size) throws ApiException{
		return search(mobile, searchContent, 0, 0, page, size);
	}
	/**
	 * 搜索联系人信息（精简字段）
	 * @see #search(String, String, int, long, int, int)
	 * @param mobile 用户手机号码
	 * @param searchContent 搜索关键字，可以为空
	 * @param sourceType 搜索烈性：[0] 不指定类型，[1]表示搜索邮件地址不为空的联系人，[2]表示搜索手机号码不为空的联系人
	 * @param page 分页索引
	 * @param size 每页记录数
	 * @return 接口执行结果枚举{@link ContactResult}。当{@link ContactResult#isOK()} == true 时， {@link ArrayList} list = {@link ContactResult#getValue(Class) ContactResult.getValue(ArrayList<ContactInfo>.class)} 可获取到联系人列表信息
	 * @throws ApiException
	 * 
	 */
	public ContactResult search(String mobile,String searchContent,int sourceType,int page,int size) throws ApiException{
		return search(mobile, searchContent, sourceType, 0, page, size);
	}
	/**
	 * 搜索联系人信息（精简字段）
	 * @param mobile 用户手机号码
	 * @param searchContent 搜索关键字，可以为空
	 * @param sourceType 搜索烈性：[0] 不指定类型，[1]表示搜索邮件地址不为空的联系人，[2]表示搜索手机号码不为空的联系人
	 * @param groupId 联系人组编号，为[0]时不指定组编号
	 * @param page 分页索引
	 * @param size 每页记录数
	 * @return 接口执行结果枚举{@link ContactResult}。当{@link ContactResult#isOK()} == true 时， {@link ArrayList} list = {@link ContactResult#getValue(Class) ContactResult.getValue(ArrayList<ContactInfo>.class)} 可获取到联系人列表信息
	 * @throws ApiException
	 * @example
	 * <pre>
Contacts api = new Contacts();
ContactResult result = null;
try {
	result = api.search("13730124054", "@139.com", 1, 0, 1, 100);//搜索有邮箱地址并且包含@139.com的通讯录信息
} catch (ApiException e) {
	e.printStackTrace();
	return;
}
try{
	if(result.isOK()){
		System.out.println("通讯录总数：" +api.getTotalRecord());
		ArrayList<ContactInfo> list = result.getValue(ArrayList.class);
		Iterator<ContactInfo> itr = list.iterator();
		BeanInfo beanInfo = Introspector.getBeanInfo(ContactInfo.class);
		PropertyDescriptor[] pd = beanInfo.getPropertyDescriptors();
		while(itr.hasNext()){
			ContactInfo info = itr.next();
			for(PropertyDescriptor p : pd){
				System.out.print(p.getName() + "=" + p.getReadMethod().invoke(info) + ";");
			}
			System.out.println("");
		}
	}else{
		System.out.println("接口执行失败："+result.getDescr());
	}
}catch(Exception ex){
	ex.printStackTrace();
}
	 * </pre>
	 */
	public ContactResult search(String mobile,String searchContent,int sourceType,long groupId,int page,int size) throws ApiException{
		if(StringUtils.isEmpty(mobile))
			return ContactResult.EmptyMobile;	//空账户		
		if(!Pattern.isMobile139alias(mobile))
			return ContactResult.InvalidMobile;	//无效账号
		StringBuilder strRequestData = new StringBuilder();

		strRequestData.append("<?xml version=\"1.0\" encoding=\"");
		strRequestData.append(config.Contact_Encoding);
		strRequestData.append("\"?><SearchContacts");
		
		if(page>=1 && size>=1){
			strRequestData.append(" Page=\""+String.valueOf(page)+"\" Record=\""+String.valueOf(size)+"\"");
		}
		strRequestData.append("><UserNumber>");
		strRequestData.append(mobile);
		strRequestData.append("</UserNumber>");
		if(!StringUtils.isEmpty(searchContent)){
			strRequestData.append("<SearchContent>");
			strRequestData.append(searchContent);
			strRequestData.append("</SearchContent>");
		}else{
			strRequestData.append("<SearchContent/>");
		}
		if(groupId != 0 ){
			strRequestData.append("<GroupId>");
			strRequestData.append(groupId);
			strRequestData.append("</GroupId>");
		}else{
			strRequestData.append("<GroupId/>");
		}
		if(sourceType != 0 ){
			strRequestData.append("<SourceType>");
			strRequestData.append(sourceType);
			strRequestData.append("</SourceType>");
		}else{
			strRequestData.append("<SourceType/>");
		}
		

		String out = null;
		try {
			out = HttpClient.post(config.Contact_SearchContacts,strRequestData.toString(),config.Contact_Encoding);
			out = uncompressOutSimpleXml(out);
		} catch (IOException e) {
			ApiException ex = new ApiException("搜索联系人接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}
		Xml outXml = null;
		try {
			outXml = Xml.parseXml(out);
		}catch(Exception e){
			ApiException ex = new ApiException("搜索联系人接口解析返回值为XML时发生异常");				
			ex.initCause(e);
			throw ex;
		}	
		String outRetCode = "";
		String outRetMsg = "";
		try {
			String outXmlRoot ="SearchContactsResp";
			
			outRetCode = outXml.evaluate("/"+outXmlRoot+"/ResultCode");
			outRetMsg = outXml.evaluate("/"+outXmlRoot+"/ResultMsg");
			//QueryContactsByNameResp
			
			if(outRetCode.equals("0")){			
				if(page == 1){
					totalRecord = TryParse.toInt(outXml.evaluate("/"+outXmlRoot+"/TotalRecord"));
				}
				NodeList nodeList = (NodeList) outXml.evaluate("/"+outXmlRoot+"/ContactsInfo", XPathConstants.NODESET);
				ArrayList<ContactInfo> list = Xml.nodeToBeanList(nodeList, ContactInfo.class);
				return ContactResult.OK.setValue(list);
			}else{
				//返回未知结果
				return ContactResult.Other.setValue("["+outRetCode+"]"+outRetMsg);
			}
		} catch (XPathExpressionException e) {
			ApiException ex = new ApiException("搜索有联系人接口获取返回XML指定属性时发生异常："+out);				
			ex.initCause(e);
			throw ex;
		} catch (AppException e) {
			ApiException ex = new ApiException("搜索联系人接口将返回值XML序列化为ContactInfo时异常："+out);				
			ex.initCause(e);
			throw ex;
		}
	}
	/**
	 * 查询所有联系人接口(精简字段)
	 * @param mobile 用户手机号码
	 * @param page 分页查询的页数
	 * @param size 分页查询的每页记录数
	 * @return
	 * @throws ApiException
	 */
	public ContactResult getAll(String mobile,int page,int size) throws ApiException{
		return getAll(mobile, null, page, size);
	}
	
	/**
	 *  查询指定姓名首字母的所有联系人接口(精简字段)
	 * @param mobile 用户手机号码
	 * @param firstNameword 查询需查询联系人的首字母，例如：L
	 * @param page 分页查询的页数
	 * @param size 分页查询的每页记录数
	 * @return
	 * @throws ApiException
	 * @example
	 * <pre>
Contacts api = new Contacts();
ContactResult result = null;
try {
	result = api.getAll("13730124054", 1, 10);
} catch (ApiException e) {
	e.printStackTrace();
	return;
}
try{
	if(result.isOK()){
		System.out.println("通讯录总数：" +api.getTotalRecord());
		ArrayList<ContactInfo> list = result.getValue(ArrayList.class);
		Iterator<ContactInfo> itr = list.iterator();
		BeanInfo beanInfo = Introspector.getBeanInfo(ContactInfo.class);
		PropertyDescriptor[] pd = beanInfo.getPropertyDescriptors();
		while(itr.hasNext()){
			ContactInfo info = itr.next();
			for(PropertyDescriptor p : pd){
				System.out.print(p.getName() + "=" + p.getReadMethod().invoke(info) + ";");
			}
			System.out.println("");
		}
	}else{
		System.out.println("接口执行失败："+result.getDescr());
	}
}catch(Exception ex){
	ex.printStackTrace();
}
	 * </pre>
	 */
	public ContactResult getAll(String mobile,String firstNameword,int page,int size) throws ApiException{
		if(StringUtils.isEmpty(mobile))
			return ContactResult.EmptyMobile;	//空账户		
		if(!Pattern.isMobile139alias(mobile))
			return ContactResult.InvalidMobile;	//无效账号
		boolean isfilterFNW = false;
		if(!StringUtils.isEmpty(firstNameword)) isfilterFNW = true;
		
		String strRequestData = (isfilterFNW ? DATA_GETALLBYNAME : DATA_GETALL)
				.replace("{encoding}", config.Contact_Encoding)
				.replace("{Page}", String.valueOf(page))
                .replace("{Record}", String.valueOf(size))
                .replace("{Mobile}", mobile);
		if(isfilterFNW)
			strRequestData = strRequestData.replace("{FirstNameword}", firstNameword);

		String out = null;
		try {
			out = HttpClient.post(isfilterFNW ? config.Contact_QueryContactsByName : config.Contact_QueryAllContacts,strRequestData,config.Contact_Encoding);
			out = uncompressOutSimpleXml(out);
		} catch (IOException e) {
			ApiException ex = new ApiException("获取所有联系人接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}
		Xml outXml = null;
		try {
			outXml = Xml.parseXml(out);
		}catch(Exception e){
			ApiException ex = new ApiException("获取所有联系人接口解析返回值为XML时发生异常");				
			ex.initCause(e);
			throw ex;
		}	
		String outRetCode = "";
		String outRetMsg = "";
		try {
			String outXmlRoot = isfilterFNW ? "QueryContactsByNameResp" : "QueryAllContactsResp";
			
			outRetCode = outXml.evaluate("/"+outXmlRoot+"/ResultCode");
			outRetMsg = outXml.evaluate("/"+outXmlRoot+"/ResultMsg");
			//QueryContactsByNameResp
			
			if(outRetCode.equals("0")){			
				if(page == 1){
					totalRecord = TryParse.toInt(outXml.evaluate("/"+outXmlRoot+"/TotalRecord"));
				}
				NodeList nodeList = (NodeList) outXml.evaluate("/"+outXmlRoot+"/ContactsInfo", XPathConstants.NODESET);
				ArrayList<ContactInfo> list = Xml.nodeToBeanList(nodeList, ContactInfo.class);
				return ContactResult.OK.setValue(list);
			}else{
				//返回未知结果
				return ContactResult.Other.setValue("["+outRetCode+"]"+outRetMsg);
			}
		} catch (XPathExpressionException e) {
			ApiException ex = new ApiException("获取所有联系人接口获取返回XML指定属性时发生异常："+out);				
			ex.initCause(e);
			throw ex;
		} catch (AppException e) {
			ApiException ex = new ApiException("获取所有联系人接口将返回值XML序列化为ContactInfo时异常："+out);				
			ex.initCause(e);
			throw ex;
		}
	}
	/**
	 * 分页查询联系人接口/查询指定联系人/查询指定首字母联系人，包含所有字段
	 * @param mobile 用户手机号码，返回该用户的联系人信息
	 * @param serialId 查询指定联系人的编号
	 * @param firstNameword 查询需查询联系人的首字母
	 * @param page 分页查询的页码索引
	 * @param size 分页查询的每页记录数
	 * @return
	 * @throws ApiException
	 * <pre>
Contacts api = new Contacts();
ContactResult result = null;
try {
	result = api.getAllWithDetail("13730124054","L", 1, 10);
	//result = api.getAllWithDetail("13730124054","2147536333",null, 1, 10);			
} catch (ApiException e) {
	e.printStackTrace();
	return;
}
try{
	if(result.isOK()){
		System.out.println("通讯录总数：" +api.getTotalRecord());
		List<ContactInfo> list = (List<ContactInfo>) result.getValue();
		Iterator<ContactInfo> itr = list.iterator();
		BeanInfo beanInfo = Introspector.getBeanInfo(ContactInfo.class);
		PropertyDescriptor[] pd = beanInfo.getPropertyDescriptors();
		while(itr.hasNext()){
			ContactInfo info = itr.next();
			for(PropertyDescriptor p : pd){
				System.out.print(p.getName() + "=" + p.getReadMethod().invoke(info) + ";");
			}
			System.out.println("");
		}
	}else{
		System.out.println("接口执行失败："+result.getDescr());
	}
}catch(Exception ex){
	ex.printStackTrace();
}
	 * </pre>
	 */
	protected ContactResult getAllFields(String mobile,String serialId,String firstNameword,int page,int size) throws ApiException{
		if(StringUtils.isEmpty(mobile))
			return ContactResult.EmptyMobile;	//空账户
		
		if(!Pattern.isMobile139alias(mobile))
			return ContactResult.InvalidMobile;	//无效账号
		
		StringBuilder strRequestData = new StringBuilder();
		strRequestData.append("<?xml version=\"1.0\" encoding=\"");
		strRequestData.append(config.Contact_Encoding);
		strRequestData.append("\"?><QueryContacts");
		
		if(page>=1 && size>=1){
			strRequestData.append(" Page=\""+String.valueOf(page)+"\" Record=\""+String.valueOf(size)+"\"");
		}
		strRequestData.append("><UserNumber>");
		strRequestData.append(mobile);
		strRequestData.append("</UserNumber>");
		if(!StringUtils.isEmpty(serialId)){
			strRequestData.append("<SerialId>");
			strRequestData.append(serialId);
			strRequestData.append("</SerialId>");
		}
		if(!StringUtils.isEmpty(firstNameword)){
			strRequestData.append("<FirstNameword>");
			strRequestData.append(firstNameword);
			strRequestData.append("</FirstNameword>");
		}
		
		String out = null;
		try {
			out = HttpClient.post(config.Contact_QueryContacts,strRequestData.toString(),config.Contact_Encoding);
			out = uncompressOutSimpleXml(out);
		} catch (IOException e) {
			ApiException ex = new ApiException("查询联系人（所有字段）接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}
		Xml outXml = null;
		try {
			outXml = Xml.parseXml(out);
		}catch(Exception e){
			ApiException ex = new ApiException("查询联系人（所有字段）接口解析返回值为XML时发生异常");				
			ex.initCause(e);
			throw ex;
		}	
		String outRetCode = "";
		String outRetMsg = "";
		try {
			outRetCode = outXml.evaluate("/QueryContactsResp/ResultCode");
			outRetMsg = outXml.evaluate("/QueryContactsResp/ResultMsg");
			
			if(outRetCode.equals("0")){			
				if(page == 1){
					totalRecord = TryParse.toInt(outXml.evaluate("/QueryContactsResp/TotalRecord"));
				}
				NodeList nodeList = (NodeList) outXml.evaluate("/QueryContactsResp/ContactsInfo", XPathConstants.NODESET);
				ArrayList<ContactInfo> list = Xml.nodeToBeanList(nodeList, ContactInfo.class);
				
				return ContactResult.OK.setValue(list);
			}else{
				//返回未知结果
				return ContactResult.Other.setValue("["+outRetCode+"]"+outRetMsg);
			}
		} catch (XPathExpressionException e) {
			ApiException ex = new ApiException("查询联系人（所有字段）接口获取返回XML指定属性时发生异常："+out);				
			ex.initCause(e);
			throw ex;
		} catch (AppException e) {
			ApiException ex = new ApiException("查询联系人（所有字段）接口将返回值XML序列化为ContactInfo时异常："+out);				
			ex.initCause(e);
			throw ex;
		}
	}

	/**
	 * 分页查询联系人接口（所有字段）
	 * @see #getAllFields(String, String, String, int, int)
	 * @param mobile 用户手机号码，返回该用户的联系人信息
	 * @param page 分页查询的页码索引
	 * @param size 分页查询的每页记录数
	 * @return {@link ContactResult}
	 * @throws ApiException
	 * @example
	 * <pre>
Contacts api = new Contacts();
ContactResult result = null;
try {
	result = api.getAllWithDetail("13730124054", 1, 10);			
} catch (ApiException e) {
	e.printStackTrace();
	return;
}
try{
	if(result.isOK()){
		List<ContactInfo> list = (List<ContactInfo>) result.getValue();
		Iterator<ContactInfo> itr = list.iterator();
		BeanInfo beanInfo = Introspector.getBeanInfo(ContactInfo.class);
		PropertyDescriptor[] pd = beanInfo.getPropertyDescriptors();
		while(itr.hasNext()){
			ContactInfo info = itr.next();
			for(PropertyDescriptor p : pd){
				System.out.print(p.getName() + "=" + p.getReadMethod().invoke(info) + ";");
			}
			System.out.println("");
		}
	}else{
		System.out.println("接口执行失败："+result.getDescr());
	}
}catch(Exception ex){
	ex.printStackTrace();
}
	 * </pre>
	 */
	public ContactResult getAllWithDetail(String mobile,int page,int size) throws ApiException{
		return getAllFields(mobile, null, null, page, size);
	}
	/**
	 * 查询指定首字母联系人（所有字段）
	 * @see #getAllFields(String, String, String, int, int) 
	 * @param mobile 用户手机号码，返回该用户的联系人信息
	 * @param firstNameword 查询需查询联系人的首字母，例如：L
	 * @param page 分页查询的页码索引
	 * @param size 分页查询的每页记录数
	 * @return
	 * @throws ApiException
	 */
	public ContactResult getAllWithDetail(String mobile,String firstNameword,int page,int size) throws ApiException{
		return getAllFields(mobile, null, firstNameword, page, size);
	}
	/**
	 * 查询指定联系人（所有字段）
	 * @see #getAllFields(String, String, String, int, int)
	 * @param mobile 用户手机号码，返回该用户的联系人信息
	 * @param serialId 查询指定联系人的编号
	 * @return  {@link ContactResult} 。当返回值  {@link ContactResult#isOK()} == true ，则可以通过List list = (List) {@link ContactResult#getValue(ContactInfo.Class)} 获取到返回的联系人列表信息
	 * @throws ApiException
	 */
	public ContactResult getDetail(String mobile,String serialId) throws ApiException{
		ContactResult result = getAllFields(mobile, serialId, null, 0, 0);
		if(!result.isOK()) return result;
		@SuppressWarnings("unchecked")
		ArrayList<ContactInfo> list = result.getValue(ArrayList.class);
		ContactInfo info = null;
		if(list.size() >=1)
			info = list.get(0);
		return result.setValue(info);
	}
	/**
	 * 获取最近和紧密联系人
	 * @param mobile 用户手机号码，返回该用户的联系人信息
	 * @return {@link ContactResult} 。当返回值  {@link ContactResult#isOK()} == true ，则可以通过 {@link ContactResult#getValue()} 获取到返回的最近联系人和紧密联系人信息，详细方式请查看示例代码
	 * @throws ApiException 接口执行异常
	 * @example
	 * <pre>
Contacts api = new Contacts();
ContactResult result = null;
try {
	result = api.getLastContact("13730124054");
} catch (ApiException e) {
	e.printStackTrace();
	return;
}
try{
	if(result.isOK()){
		HashMap<String, ArrayList<ContactInfo>> map = (HashMap<String, ArrayList<ContactInfo>>) result.getValue();
		List<ContactInfo> last = map.get("Last");	//获取最近联系人
		List<ContactInfo> close = map.get("Close");		//获取紧密联系人
		
		
		BeanInfo beanInfo = Introspector.getBeanInfo(ContactInfo.class);
		PropertyDescriptor[] pd = beanInfo.getPropertyDescriptors();
		System.out.println("==============最近联系人==============");
		//输出最贱联系人
		Iterator<ContactInfo> itr = last.iterator();
		while(itr.hasNext()){
			ContactInfo info = itr.next();
			System.out.println("SerialId=" + info.getSerialId()+";" + "Name=" + info.getName() + ";"+"Type=" + info.getAddrType() + ";"+"Content=" + info.getFamilyEmail() + ";");
			System.out.println("");
		}
		System.out.println("==============紧密联系人==============");
		//输出紧密联系人
		Iterator<ContactInfo> itr2 = close.iterator();
		while(itr2.hasNext()){
			ContactInfo info = itr2.next();
			System.out.println("SerialId=" + info.getSerialId()+";" + "Name=" + info.getName() + ";"+"Type=" + info.getAddrType() + ";"+"Content=" + info.getFamilyEmail() + ";");
		}
	}else{
		System.out.println("接口执行失败："+result.getDescr());
	}
}catch(Exception ex){
	ex.printStackTrace();
}
	 * </pre>
	 */
	public ContactResult getLastContact(String mobile) throws ApiException{
		if(StringUtils.isEmpty(mobile))
			return ContactResult.EmptyMobile;	//空账户
		
		if(!Pattern.isMobile139alias(mobile))
			return ContactResult.InvalidMobile;	//无效账号

		String strRequestData = "<?xml version=\"1.0\" encoding=\""+config.Contact_Encoding+"\"?><GetLCContacts><UserNumber>"+mobile+"</UserNumber></GetLCContacts>";
		
		String out = null;
		try {
			out = HttpClient.post(config.Contact_GetLCContacts,strRequestData.toString(),config.Contact_Encoding);
			out = uncompressOutSimpleXml(out);
		} catch (IOException e) {
			ApiException ex = new ApiException("查询最近/紧密联系人接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}
		String outRetCode = "";
		String outRetMsg = "";
		
		if(out.startsWith("GetLCContactsResp")){
			out = out.substring(out.indexOf("{"));
			JSONObject json = JSONObject.parseObject(out);
			outRetCode = json.getString("ResultCode");
			outRetMsg = json.getString("ResultMsg");
			if(outRetCode.equals("0")){				
				ArrayList<ContactInfo> listLast = new ArrayList<ContactInfo>();
				ArrayList<ContactInfo> listClose = new ArrayList<ContactInfo>();
				
				JSONArray jsonLast = json.getJSONArray("LastContacts");
				for(int i=0;i<jsonLast.size();i++){
					JSONObject jsonInfo = jsonLast.getJSONObject(i);
					ContactInfo info = new ContactInfo();
					info.setSerialId(TryParse.toLong(jsonInfo.getString("sd")));
					info.setName(jsonInfo.getString("an"));
					info.setAddrType(jsonInfo.getString("at"));
					info.setEmail(jsonInfo.getString("ac"));
					listLast.add(info);
				}
				JSONArray jsonclose = json.getJSONArray("CloseContacts");
				for(int i=0;i<jsonclose.size();i++){
					JSONObject jsonInfo = jsonclose.getJSONObject(i);
					ContactInfo info = new ContactInfo();
					info.setSerialId(TryParse.toLong(jsonInfo.getString("sd")));
					info.setName(jsonInfo.getString("an"));
					info.setAddrType(jsonInfo.getString("at"));
					info.setEmail(jsonInfo.getString("ac"));
					listClose.add(info);
				}
				
				HashMap<String, ArrayList<ContactInfo>> map = new HashMap<String, ArrayList<ContactInfo>>();
				map.put("Last", listLast);
				map.put("Close", listClose);
				
				return ContactResult.OK.setValue(map);
			}
		}else{
			Xml outXml = null;
			try {
				outXml = Xml.parseXml(out);
			}catch(Exception e){
				ApiException ex = new ApiException("查询最近/紧密联系人接口解析返回值为XML时发生异常");				
				ex.initCause(e);
				throw ex;
			}	
			
			try {
				outRetCode = outXml.evaluate("/GetLCContactsResp/ResultCode");
				outRetMsg = outXml.evaluate("/GetLCContactsResp/ResultMsg");
				
				if(outRetCode.equals("0")){			
					NodeList nodeLast = (NodeList) outXml.evaluate("/GetLCContactsResp/LastContactsInfo", XPathConstants.NODESET);				
					ArrayList<ContactInfo> listLast = Xml.nodeToBeanList(nodeLast, ContactInfo.class);
					
					NodeList nodeClose = (NodeList) outXml.evaluate("/GetLCContactsResp/LastContactsInfo", XPathConstants.NODESET);
					ArrayList<ContactInfo> listClose = Xml.nodeToBeanList(nodeClose, ContactInfo.class);
					
					HashMap<String, ArrayList<ContactInfo>> map = new HashMap<String, ArrayList<ContactInfo>>();
					map.put("Last", listLast);
					map.put("Close", listClose);
					
					return ContactResult.OK.setValue(map);
				}
			} catch (XPathExpressionException e) {
				ApiException ex = new ApiException("查询最近/紧密联系人接口获取返回XML指定属性时发生异常："+out);				
				ex.initCause(e);
				throw ex;
			} catch (AppException e) {
				ApiException ex = new ApiException("查询最近/紧密联系人接口将返回值XML序列化为ContactInfo时异常："+out);				
				ex.initCause(e);
				throw ex;
			}
		}
		//返回未知结果
		return ContactResult.Other.setValue("["+outRetCode+"]"+outRetMsg);
	}
	/**
	 * 新增联系人
	 * @param mobile 用户号码
	 * @param info 需要添加的联系人实例
	 * @return {@link ContactResult} 。当返回值  {@link ContactResult#isOK()} == true ，则可以通过ContactInfo outBean = {@link ContactResult#getValue(ContactInfo.Class)} 获取到返回的联系人信息
	 * @throws ApiException
	 * @example
	 * <pre>
Contacts api = new Contacts();
ContactResult result = null;
ContactInfo info = new ContactInfo();
//info.setAddrFirstName("王");
info.setAddrSecondName("小二");
info.setMobilePhone("13730124010");
//info.setFamilyEmail("13730124010@139.com");
try {
	result = api.add("13730124054", info);
} catch (ApiException e) {
	e.printStackTrace();
}
if(result.isOK()){
	ContactInfo outBean = result.getValue(ContactInfo.class);
	System.out.println("SerialId="+outBean.getSerialId());
	System.out.println("FirstNameword="+outBean.getFirstNameword());
	System.out.println("FullNameword="+outBean.getFullNameword());
	System.out.println("FirstWord="+outBean.getFirstWord());
}else{
	System.out.println("接口执行失败："+result.getDescr());
}
	 * </pre>
	 */
	public ContactResult add(String mobile, ContactInfo info) throws ApiException{
		if(StringUtils.isEmpty(mobile))
			return ContactResult.EmptyMobile;	//空账户
		if(!Pattern.isMobile139alias(mobile))
			return ContactResult.InvalidMobile;	//无效账号
		if(info == null)
			return ContactResult.EmptyParam;	//无效账号
		if(info.getMobile() ==null && info.getEmail() == null)
			return ContactResult.EmptyParam;	//手机号码和邮箱地址必须填一个
		
		String strRequestData = null;
		try {
			strRequestData = Xml.beanToXmlString(info, ContactInfo.class,"AddContacts");
			strRequestData = "<?xml version=\"1.0\" encoding=\""+config.Contact_Encoding+"\"?><AddContacts><UserNumber>"+mobile+"</UserNumber><UserType/><SourceType/>"+strRequestData.substring(13);
			//strRequestData.replace("\\<AddContacts\\>", "<?xml version=\"1.0\" encoding=\""+config.Contact_Encoding+"\"?><AddContacts><UserNumber>"+mobile+"</UserNumber><UserType/><SourceType/>");
		} catch (AppException e) {
			ApiException ex = new ApiException("添加接口将ContactInfo序列化为XML时异常");				
			ex.initCause(e);
			throw ex;
		}
		String out = null;
		try {
			out = HttpClient.post(config.Contact_AddContacts,strRequestData,config.Contact_Encoding);
			out = uncompressOutSimpleXml(out);
		} catch (IOException e) {
			ApiException ex = new ApiException("添加联系人接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}
		Xml outXml = null;
		try {
			outXml = Xml.parseXml(out);
		}catch(Exception e){
			ApiException ex = new ApiException("添加接口解析返回值为XML时发生异常");				
			ex.initCause(e);
			throw ex;
		}	
		String outRetCode = "";
		String outRetMsg = "";
		try {
			outRetCode = outXml.evaluate("/AddContactsResp/ResultCode");
			outRetMsg = outXml.evaluate("/AddContactsResp/ResultMsg");
			
			if(outRetCode.equals("0")){			
				Node node = (Node) outXml.evaluate("/AddContactsResp/ContactsInfo", XPathConstants.NODE);				
				ContactInfo outBean = Xml.nodeToBean(node, ContactInfo.class);
				info.setSerialId(outBean.getSerialId());
				info.setFirstNameword(outBean.getFirstNameword());
				info.setFullNameword(outBean.getFullNameword());
				info.setFirstWord(outBean.getFirstWord());
				
				return ContactResult.OK.setValue(info);
			}else{
				//返回未知结果
				return ContactResult.Other.setValue("["+outRetCode+"]"+outRetMsg);
			}
		} catch (XPathExpressionException e) {
			ApiException ex = new ApiException("添加接口解获取返回XML指定属性时发生异常："+out);				
			ex.initCause(e);
			throw ex;
		} catch (AppException e) {
			ApiException ex = new ApiException("添加接口解将返回值XML序列化为ContactInfo时异常："+out);				
			ex.initCause(e);
			throw ex;
		}
	}
	/**
	 * 获取10天内即将过生日的联系人
	 * @param mobile 用户手机号码，返回该用户的联系人信息	
	 * @param page 分页查询的页码索引
	 * @param size 分页查询的每页记录数
	 * @return
	 * @throws ApiException
	 * <pre>
Contacts api = new Contacts();
ContactResult result = null;
try {
	result = api.getRecentBirday("13730124054", 1, 10);		
} catch (ApiException e) {
	e.printStackTrace();
	return;
}
try{
	if(result.isOK()){
		System.out.println("即将过生日的联系人总数：" +api.getTotalRecord());
		List<ContactInfo> list = (List<ContactInfo>) result.getValue();
		Iterator<ContactInfo> itr = list.iterator();
		BeanInfo beanInfo = Introspector.getBeanInfo(ContactInfo.class);
		PropertyDescriptor[] pd = beanInfo.getPropertyDescriptors();
		while(itr.hasNext()){
			ContactInfo info = itr.next();
			for(PropertyDescriptor p : pd){
				System.out.print(p.getName() + "=" + p.getReadMethod().invoke(info) + ";");
			}
			System.out.println("");
		}
	}else{
		System.out.println("接口执行失败："+result.getDescr());
	}
}catch(Exception ex){
	ex.printStackTrace();
}
	 * </pre>
	 */
	public ContactResult getRecentBirday(String mobile,int page,int size) throws ApiException{
		if(StringUtils.isEmpty(mobile))
			return ContactResult.EmptyMobile;	//空账户
		
		if(!Pattern.isMobile139alias(mobile))
			return ContactResult.InvalidMobile;	//无效账号

		StringBuilder strRequestData = new StringBuilder();
		strRequestData.append("<?xml version=\"1.0\" encoding=\""+config.Contact_Encoding+"\"?><GetContactsBirday");		
		if(page>=1 && size>=1){
			strRequestData.append(" Page=\""+String.valueOf(page)+"\" Record=\""+String.valueOf(size)+"\"");
		}
		strRequestData.append("><UserNumber>"+mobile+"</UserNumber></GetContactsBirday>");
		
		String out = null;
		try {
			out = HttpClient.post(config.Contact_GetRecentBirday,strRequestData.toString(),config.Contact_Encoding);
			out = uncompressOutSimpleXml(out);
		} catch (IOException e) {
			ApiException ex = new ApiException("查询将过生日联系人接口发生IO异常");
			ex.initCause(e);
			throw ex;
		}
		String outRetCode = "";
		String outRetMsg = "";
		
		Xml outXml = null;
		try {
			outXml = Xml.parseXml(out);
		}catch(Exception e){
			ApiException ex = new ApiException("查询将过生日联系人接口解析返回值为XML时发生异常");				
			ex.initCause(e);
			throw ex;
		}	
		try {
			outRetCode = outXml.evaluate("/GetContactsBirdayResp/ResultCode");
			outRetMsg = outXml.evaluate("/GetContactsBirdayResp/ResultMsg");
			
			if(outRetCode.equals("0")){			
				if(page == 1){
					totalRecord = TryParse.toInt(outXml.evaluate("/GetContactsBirdayResp/TotalRecord"));
				}
				NodeList nodeList = (NodeList) outXml.evaluate("/GetContactsBirdayResp/BirthdayContactInfo", XPathConstants.NODESET);
				ArrayList<ContactInfo> list = Xml.nodeToBeanList(nodeList, ContactInfo.class);
				
				return ContactResult.OK.setValue(list);
			}else{
				//返回未知结果
				return ContactResult.Other.setValue("["+outRetCode+"]"+outRetMsg);
			}
		} catch (XPathExpressionException e) {
			ApiException ex = new ApiException("查询将过生日联系人接口获取返回XML指定属性时发生异常："+out);				
			ex.initCause(e);
			throw ex;
		} catch (AppException e) {
			ApiException ex = new ApiException("查询将过生日联系人接口值XML序列化为ContactInfo时异常："+out);				
			ex.initCause(e);
			throw ex;
		}
	}
	/**
	 * 将通讯录接口返回值中简写的xml标签转换为全拼
	 * @param outSimpleXml
	 * @return
	 */
	protected String uncompressOutSimpleXml(String outSimpleXml){		
		Iterator<Entry<String, String>> itr = getXmlTagDict().entrySet().iterator();
		while(itr.hasNext()){
			Entry<String, String> tag = itr.next();
			outSimpleXml = outSimpleXml.replaceAll("<"+tag.getKey()+">", "<"+tag.getValue()+">");
			outSimpleXml = outSimpleXml.replaceAll("</"+tag.getKey()+">", "</"+tag.getValue()+">");
		}
		return outSimpleXml;
	}
	
	private HashMap<String, String> xmlTagDict;	
	
	/**
	 * @return the 返回通讯录接口返回值XML的精简标签和全名标签对比字典
	 */
	protected HashMap<String, String> getXmlTagDict() {
		if(xmlTagDict == null){
			xmlTagDict = new HashMap<String, String>();
			xmlTagDict.put("a", "UserType");
			xmlTagDict.put("b", "SourceType");
			xmlTagDict.put("c", "AddrFirstName");
			xmlTagDict.put("d", "AddrSecondName");
			xmlTagDict.put("e", "AddrNickName");
			xmlTagDict.put("f", "UserSex");
			xmlTagDict.put("g", "CountryCode");
			xmlTagDict.put("h", "ProvCode");
			xmlTagDict.put("i", "AreaCode");
			xmlTagDict.put("j", "CityCode");
			xmlTagDict.put("k", "StreetCode");
			xmlTagDict.put("l", "ZipCode");
			xmlTagDict.put("m", "HomeAddress");
			xmlTagDict.put("n", "MobilePhoneType");
			xmlTagDict.put("o", "BirDay");
			xmlTagDict.put("p", "MobilePhone");
			xmlTagDict.put("q", "BusinessMobile");
			xmlTagDict.put("r", "BusinessPhone");
			xmlTagDict.put("s", "FamilyPhone");
			xmlTagDict.put("t", "BusinessFax");
			xmlTagDict.put("u", "FamilyFax");
			xmlTagDict.put("v", "OtherPhone");
			xmlTagDict.put("w", "OtherMobilePhone");
			xmlTagDict.put("x", "OtherFax");
			xmlTagDict.put("y", "FamilyEmail");
			xmlTagDict.put("z", "BusinessEmail");
			xmlTagDict.put("c2", "OtherEmail");
			xmlTagDict.put("c3", "PersonalWeb");
			xmlTagDict.put("c4", "CompanyWeb");
			xmlTagDict.put("c5", "OtherWeb");
			xmlTagDict.put("c6", "OICQ");
			xmlTagDict.put("c7", "MSN");
			xmlTagDict.put("c8", "OtherIm");
			xmlTagDict.put("c9", "CPCountryCode");
			xmlTagDict.put("d0", "CPProvCode");
			xmlTagDict.put("d1", "CPAreaCode");
			xmlTagDict.put("a0", "CPCityCode");
			xmlTagDict.put("a1", "CPStreetCode");
			xmlTagDict.put("a2", "CPZipCode");
			xmlTagDict.put("a3", "CPAddress");
			xmlTagDict.put("a4", "CPName");
			xmlTagDict.put("a5", "CPDepartName");
			xmlTagDict.put("a6", "Memo");
			xmlTagDict.put("a7", "ContactCount");
			xmlTagDict.put("a8", "ContactType");
			xmlTagDict.put("a9", "ContactFlag");
			xmlTagDict.put("b0", "SynFlag");
			xmlTagDict.put("b1", "SynId");
			xmlTagDict.put("b2", "RecordSeq");
			xmlTagDict.put("b3", "FirstNameword");
			xmlTagDict.put("b4", "CountMsg");
			xmlTagDict.put("b5", "StartCode");
			xmlTagDict.put("b6", "BloodCode");
			xmlTagDict.put("b7", "StateCode");
			xmlTagDict.put("b8", "ImageUrl");
			xmlTagDict.put("b9", "SchoolName");
			xmlTagDict.put("c0", "BokeUrl");
			xmlTagDict.put("c1", "UserJob");
			xmlTagDict.put("e1", "FamilyPhoneBrand");
			xmlTagDict.put("e2", "BusinessPhoneBrand");
			xmlTagDict.put("e3", "OtherPhoneBrand");
			xmlTagDict.put("e4", "FamilyPhoneType");
			xmlTagDict.put("e5", "BusinessPhoneType");
			xmlTagDict.put("e6", "OtherPhoneType");
			xmlTagDict.put("e7", "EduLevel");
			xmlTagDict.put("e8", "Marriage");
			xmlTagDict.put("e9", "NetAge");
			xmlTagDict.put("e0", "Profession");
			xmlTagDict.put("f1", "Income");
			xmlTagDict.put("f2", "Interest");
			xmlTagDict.put("f3", "MoConsume");
			xmlTagDict.put("f4", "ExpMode");
			xmlTagDict.put("f5", "ExpTime");
			xmlTagDict.put("f6", "ContactMode");
			xmlTagDict.put("f7", "Purpose");
			xmlTagDict.put("f8", "Brief");
			xmlTagDict.put("f9", "FavoEmail");
			xmlTagDict.put("f0", "FavoBook");
			xmlTagDict.put("g1", "FavoMusic");
			xmlTagDict.put("g2", "FavoMovie");
			xmlTagDict.put("g3", "FavoTv");
			xmlTagDict.put("g4", "FavoSport");
			xmlTagDict.put("g5", "FavoGame");
			xmlTagDict.put("g6", "FavoPeople");
			xmlTagDict.put("g7", "FavoWord");
			xmlTagDict.put("g8", "Character");
			xmlTagDict.put("g9", "MakeFriend");
			xmlTagDict.put("ui", "UserInfo");
			xmlTagDict.put("un", "UserNumber");
			xmlTagDict.put("sd", "SerialId");
			xmlTagDict.put("gd", "GroupId");
			xmlTagDict.put("gp", "Group");
			xmlTagDict.put("gi", "GroupInfo");
			xmlTagDict.put("ct", "Contacts");
			xmlTagDict.put("ci", "ContactsInfo");
			xmlTagDict.put("gl", "GroupList");
			xmlTagDict.put("li", "GroupListInfo");
			xmlTagDict.put("tr", "TotalRecord");
			xmlTagDict.put("rc", "ResultCode");
			xmlTagDict.put("rm", "ResultMsg");
			xmlTagDict.put("gn", "GroupName");
			xmlTagDict.put("cn", "CntNum");
			xmlTagDict.put("ri", "RepeatInfo");
			xmlTagDict.put("lct", "LastContacts");
			xmlTagDict.put("lctd", "LastContactsDetail");
			xmlTagDict.put("lci", "LastContactsInfo");
			xmlTagDict.put("cct", "CloseContacts");
			xmlTagDict.put("cci", "CloseContactsInfo");
			xmlTagDict.put("an", "AddrName");
			xmlTagDict.put("at", "AddrType");
			xmlTagDict.put("ac", "AddrContent");
			xmlTagDict.put("us", "UserSerialId");
			xmlTagDict.put("ai", "AddrId");
			xmlTagDict.put("lid", "LastId");
			xmlTagDict.put("ate", "AddrTitle");
			xmlTagDict.put("trg", "TotalRecordGroup");
			xmlTagDict.put("trr", "TotalRecordRelation");
			xmlTagDict.put("cf", "ComeFrom");
			xmlTagDict.put("cte", "CreateTime");
			xmlTagDict.put("trg", "TotalRecordGroup");
			xmlTagDict.put("trr", "TotalRecordRelation");
			xmlTagDict.put("Bct", "BirthdayContacts");
			xmlTagDict.put("bci", "BirthdayContactInfo");
		}
		return xmlTagDict;
	}
}
