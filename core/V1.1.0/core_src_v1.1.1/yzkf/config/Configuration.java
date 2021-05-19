package yzkf.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

import cn.richinfo.core.utils.MD5;
import yzkf.exception.ParserConfigException;
import yzkf.utils.LocalCache;
import yzkf.utils.Xml;
/**
 * 配置文件超类
 *  
 * @author qiulw
 * @version v1.0.0 2011.11.14
 */
class Configuration {
	private Xml xml;
	/**
	 * 读取指定路径的XML文件创建Config对象
	 * @param path XML文件路径
	 * @throws ParserConfigException
	 */
	Configuration(String path) throws ParserConfigException{
		setXml(path);
	}
	/**
	 * 读取指定URL的XML文件创建Config对象
	 * @param url 指向XML文件的URL
	 * @throws ParserConfigException
	 */
	Configuration(URL url) throws ParserConfigException{
		setXml(url);
	}
	Configuration(){
		
	}
	/**
	 * 设置XML对象，通过输入流
	 * @param is
	 * @throws ParserConfigException
	 */
	void setXml(InputStream is) throws ParserConfigException{
		try {
			this.xml = new Xml(is);
		} catch (SAXException e) {
			ParserConfigException ex= new ParserConfigException("配置文件InputStream读取失败");
			ex.initCause(e);
			throw ex;
		} catch (IOException e) {
			ParserConfigException ex= new ParserConfigException("配置文件InputStream读取失败");
			ex.initCause(e);
			throw ex;
		}
	}
	/**
	 * 设置Xml对象
	 * <p>不试用缓存</p>
	 * @param url 指向XML文件的URL
	 * @throws ParserConfigException
	 */
	void setXml(URL url) throws ParserConfigException{
		setXml(url, false);
	}
	/**
	 * 设置Xml对象
	 * @param url 指向XML文件的URL
	 * @param useCache 是否试用缓存
	 * @throws ParserConfigException
	 */
	void setXml(URL url,boolean useCache) throws ParserConfigException{		
		if(url == null){
			throw new NullPointerException("配置文件路径不能为null");
		}
		try {
			setXml(new File(url.toURI()), useCache);
		} catch (URISyntaxException e) {
			ParserConfigException ex= new ParserConfigException("配置文件URL读取失败");
			ex.initCause(e);
			throw ex;
		}
	}
	/**
	 * 设置Xml对象
	 * <p>不使用缓存</p>
	 * @param path XML文件绝对路径
	 * @throws ParserConfigException
	 */
	void setXml(String path) throws ParserConfigException{
		setXml(path,false);
	}
	/**
	 * 设置Xml对象
	 * @param path XML文件绝对路径
	 * @param useCache 是否试用缓存，true 缓存IO文件，false 不缓存
	 * @throws ParserConfigException
	 */
	void setXml(String path,boolean useCache) throws ParserConfigException{
		if(StringUtils.isEmpty(path)){
			throw new NullPointerException("配置文件路径不能为null");
		}
		setXml(new File(path), false);		
	}
	/**
	 * 设置Xml对象
	 * @param file xml文件对象
	 * @param useCache 是否试用本地缓存
	 * @throws ParserConfigException
	 */
	void setXml(File file,boolean useCache) throws ParserConfigException{
		try{
			if(file == null || !file.exists()){
				throw new NullPointerException("配置文件路径不存在");
			}
			if(useCache){
				String path = file.getPath();			
				String cacheKey = MD5.encode(MD5.encode(path));
				Object xmlCache = LocalCache.getCache(cacheKey);
				if(xmlCache == null){
					xmlCache = new Xml(file);
					LocalCache.setCache(cacheKey, xmlCache);
				}
				this.xml = (Xml)xmlCache;
			}else{
				this.xml = new Xml(file);
			}			
		}catch(SAXException e){
			ParserConfigException pce = new ParserConfigException("读取配置文件时发生SAX压缩错误");
			pce.initCause(e);
			throw pce;
		}catch(IOException e){
			ParserConfigException pce = new ParserConfigException("读取配置文件发生IO读取失败");
			pce.initCause(e);
			throw pce;
		}		
	}
	/**
	 * 获取Xml对象
	 * @return
	 */
	public Xml getXml() {
		return xml;
	}
	/**
	 * 获取指定XPath节点，返回字符串
	 * @param expression XPath表达式
	 * @return
	 * @throws ParserConfigException
	 */
	protected String getXPathValue(String expression) throws ParserConfigException{
		try{
			return xml.evaluate(expression);
		}catch(XPathExpressionException e){			
			ParserConfigException pce = new ParserConfigException("读取配置项时发生XPath解析错误");
			pce.initCause(e);
			throw pce;
		}
	}
}
