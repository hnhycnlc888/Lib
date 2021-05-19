package yzkf.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import yzkf.exception.AppException;
/**
 * XML操作类<br/>
 * 
 * 示例
 * <pre>
 * try {
		Xml xml = new Xml("c:\\test.xml");
		System.out.println(xml.evaluate("root/node"));
	} catch (Exception e) {
		e.printStackTrace();
	} 
 * </pre>
 * @author qiulw
 * @version V4.0.0 2011.11.28
 */
public class Xml implements Serializable {	
	private static final long serialVersionUID = 3844883382243697138L;
	private Document doc;
	private XPath xpath;
	private String _charset = "";//"utf-8";
	Xml(){		
		this.xpath=XPathFactory.newInstance().newXPath();
	}
	/**
	 * 创建Xml对象
	 * @param filePath XML文件路径
	 * @throws SAXException
	 * @throws IOException
	 */
	public Xml(String filePath) throws SAXException, FileNotFoundException, IOException{
		this();
		this.load(filePath);
	}
	public Xml(String filePath,String charset) throws SAXException, FileNotFoundException, IOException{
		this();
		this._charset = charset;
		this.load(filePath);
	}
	/**
	 * 创建XML对象
	 * @param file XML文件对象
	 * @throws SAXException
	 * @throws IOException
	 */
	public Xml(File file) throws SAXException, FileNotFoundException, IOException{
		this();
		this.load(file);
	}
	public Xml(File file,String charset) throws SAXException, FileNotFoundException, IOException{
		this();
		this._charset = charset;
		this.load(file);
	}
	public Xml(InputStream is) throws SAXException, IOException{
		this();
		this.load(is);
	}
	public Xml(InputStream is,String charset) throws SAXException, IOException{
		this();
		this._charset = charset;
		this.load(is);
	}
	public Xml(URL url) throws SAXException, IOException{
		this();
		this.load(url);
	}
	public Xml(URL url,String charset) throws SAXException, IOException{
		this();
		this._charset = charset;
		this.load(url);
	}
	/**
	 * 创建XML对象
	 * @param doc Document对象
	 */
	public Xml(Document doc){
		this();
		this.load(doc);
	}
	/**
	 * 加载XML文件，返回Xml对象
	 * @param filePath XML文件路径
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 */
	public Xml load(String filePath) throws SAXException, FileNotFoundException, IOException{
		File file = new File(filePath);
		return this.load(file);
	}
	/**
	 * 加载XML文件，返回Xml对象
	 * @param file XML文件对象
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 */
	public Xml load(File file) throws SAXException, FileNotFoundException, IOException{		
		java.io.FileInputStream is= new java.io.FileInputStream(file);
		return this.load(is);
	}
	public Xml load(URL url) throws SAXException, FileNotFoundException, IOException{		
		InputStream is= url.openStream();
		return this.load(is);
	}
	/**
	 * 加载XML文件
	 * @param is 文件流
	 * @return Xml文件对象
	 * @throws SAXException
	 * @throws IOException
	 */
	public Xml load(InputStream is) throws SAXException, IOException{
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}
		InputSource in = new InputSource(is);
		if(!StringUtils.isEmpty(_charset))
			in.setEncoding(_charset);
		return this.load(db.parse(in));
	}
	/**
	 * 加载XML文件，返回Xml对象
	 * @param doc Document对象
	 * @return
	 */
	public Xml load(Document doc){
		this.doc = doc;
		return this;
	}
	/**
	 * 转换为Document对象
	 * @return
	 */
	public Document parseDocument(){
		return this.doc;
	}	
	/**
	 * 查找指定XPath节点，返回字符串
	 * @param expression XPath表达式
	 * @return
	 * @throws XPathExpressionException
	 */
	public String evaluate(String expression) throws XPathExpressionException{
		return xpath.evaluate(expression, this.doc);
	}
	/**
	 * 查找指定XPath节点
	 * @param expression XPath表达式
	 * @param returnType 返回类型，例如：XPathConstants.NODE
	 * @return 返回指定类型
	 * @throws XPathExpressionException
	 */
	public Object evaluate(String expression,QName returnType) throws XPathExpressionException{
		return xpath.evaluate(expression, this.doc,returnType);
	}
	/**
	 * 将XML的节点转换为bean对象
	 * @param node 节点对象，节点格式为：<p>
	 * &lt;ClassName&gt;<br/>
	 * &nbsp;&nbsp;&lt;PropertyName_1&gt;PropertyValue_1&lt;/PropertyName_1&gt;<br/>
	 * &nbsp;&nbsp;&lt;PropertyName_2&gt;PropertyValue_2&lt;/PropertyName_2&gt;<br/>
	 * &nbsp;&nbsp;......<br/>
	 * &lt;/ClassName&gt;</p>
	* @param type Bean对象的类型：bean.getClass()
	 * @return Bean对象一个实例
	 * @throws AppException
	 */
	public static <T> T nodeToBean(Node node,Class<T> type) throws AppException{
		T info = Utility.newInstance(type);

		NodeList childNodes = node.getChildNodes();
		
		if(childNodes.getLength() <= 0) return null;
		
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(type);
		} catch (IntrospectionException e) {
			throw new AppException("Cannot get  " + type.getName() + " beanInfo");
		}
		PropertyDescriptor[] pd = beanInfo.getPropertyDescriptors();
		
		for(int j=0;j<childNodes.getLength();j++)
		{
			Node childNode = childNodes.item(j);
			if(childNode.getNodeType() == Node.ELEMENT_NODE){				
				for(PropertyDescriptor p : pd){
					if(p.getName().equalsIgnoreCase(childNode.getNodeName())){
						Method writeMethod =p.getWriteMethod();						
						String methodName = p.getPropertyType().getSimpleName();
						if(writeMethod!=null){
							try {								
								if(methodName.equalsIgnoreCase("int"))
									writeMethod.invoke(info, Integer.valueOf(childNode.getTextContent()));
								else if(methodName.equalsIgnoreCase("long"))															
									writeMethod.invoke(info, Long.valueOf(childNode.getTextContent()));
								else if(methodName.equalsIgnoreCase("boolean"))
									writeMethod.invoke(info, Boolean.valueOf(childNode.getTextContent()));
								else if(methodName.equalsIgnoreCase("float"))															
									writeMethod.invoke(info, Float.valueOf(childNode.getTextContent()));
								else if(methodName.equalsIgnoreCase("double"))															
									writeMethod.invoke(info, Double.valueOf(childNode.getTextContent()));
								else
									writeMethod.invoke(info, childNode.getTextContent());
							} catch (Exception  e) {
								continue;
							}
						}
					}
				}
				
			}
		}			
		return info;
	}
	/**
	 * 将Xml的节点列表转换为BeanList
	 * @param nodeList 节点列表对象，格式为：
	 * <p>&lt;ClassName_1&gt;<br/>
	 * &nbsp;&nbsp;&lt;PropertyName_1&gt;PropertyValue_1&lt;/PropertyName_1&gt;<br/>
	 * &nbsp;&nbsp;&lt;PropertyName_2&gt;PropertyValue_2&lt;/PropertyName_2&gt;<br/>
	 * &nbsp;&nbsp;......<br/>
	 * &lt;/ClassName_1&gt;</br>
	 * &lt;ClassName_2&gt;<br/>
	 * &nbsp;&nbsp;&lt;PropertyName_1&gt;PropertyValue_1&lt;/PropertyName_1&gt;<br/>
	 * &nbsp;&nbsp;&lt;PropertyName_2&gt;PropertyValue_2&lt;/PropertyName_2&gt;<br/>
	 * &nbsp;&nbsp;......<br/>
	 * &lt;/ClassName_2&gt;<br/>
	 * ...</p>
	 * @param type Bean对象的类型：bean.getClass()
	 * @return Bean对象列表
	 * @throws AppException
	 */
	public static <T> ArrayList<T> nodeToBeanList(NodeList nodeList,Class<T> type) throws AppException{
		ArrayList<T> list = new ArrayList<T>();
		for(int i=0;i<nodeList.getLength();i++)
		{
			Node node = nodeList.item(i);
			T info = nodeToBean(node,type);
			list.add(info);
		}
		return list;
	}
	public static <T extends Serializable> String beanToXmlString(T bean,Class<T> type) throws AppException{
		return beanToXmlString(bean, type, null);				
	}
	/**
	 * 将bean对象序列化为xml字符串
	 * @param bean bean对象
	 * @param type 对象类型，T.class
	 * @param rootTagName 跟节点名称
	 * @return
	 * @throws AppException
	 */
	public static <T extends Serializable> String beanToXmlString(T bean,Class<T> type,String rootTagName) throws AppException{
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(type);
		} catch (IntrospectionException e) {
			throw new AppException("Cannot get  " + type.getName() + " beanInfo");
		}
		if(StringUtils.isEmpty(rootTagName))
			rootTagName = type.getSimpleName();
		
		StringBuilder sb = new StringBuilder();
		sb.append("<");
		sb.append(rootTagName);
		sb.append(">");		
		PropertyDescriptor[] pd = beanInfo.getPropertyDescriptors();
		for(PropertyDescriptor p : pd){
			if(p.getWriteMethod() == null) continue;
			Method readMethod =p.getReadMethod();
			if(readMethod!=null){				
				Object methodValue = null;
				try {					
					methodValue = readMethod.invoke(bean);					
				} catch (Exception  e) {
					continue;
				}
				if(methodValue == null) continue;
				
				String tabName = readMethod.getName().substring(3);
				sb.append("<");
				sb.append(tabName);
				sb.append(">");
				sb.append(String.valueOf(methodValue));
				sb.append("</");
				sb.append(tabName);
				sb.append(">");
			}
		}
		sb.append("</");
		sb.append(rootTagName);
		sb.append(">");
		return sb.toString();
	}
	/**
	 * 将bean对象转换为xml字符串
	 * @param obj bean对象
	 * @param charsetName 字符编码
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static <T extends Serializable> String objectToXml(T obj,String charsetName) throws UnsupportedEncodingException{
		 XMLEncoder xmlEncoder = null;	       
		 ByteArrayOutputStream byteArrayOut = null;
		 BufferedOutputStream bufferOut = null;
        try{
        	byteArrayOut = new ByteArrayOutputStream();
            bufferOut = new BufferedOutputStream(byteArrayOut);
            xmlEncoder = new XMLEncoder(bufferOut);
            xmlEncoder.writeObject(obj);
            xmlEncoder.flush();
            byte[] bytes = byteArrayOut.toByteArray();
            return new String(bytes, charsetName);
            //return byteArrayOut.toString(charsetName);            
        }finally{
            if(null != xmlEncoder)
                xmlEncoder.close();
            if(null != bufferOut)
            	try{bufferOut.close();}catch(IOException e){};
            if(null != byteArrayOut)
            	try{byteArrayOut.close();}catch(IOException e){};
        }
	}
	/**
	 * 将xml字符串转换为bean对象
	 * @param xmlString
	 * @param charsetName
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T xmlToObject(String xmlString,String charsetName) throws UnsupportedEncodingException{
		XMLDecoder xmlDecoder = null;	       
		 ByteArrayInputStream byteArrayIn = null;
		 BufferedInputStream bufferIn = null;
       try{
    	   byteArrayIn = new ByteArrayInputStream(xmlString.getBytes(charsetName));
           bufferIn = new BufferedInputStream(byteArrayIn);
           xmlDecoder = new XMLDecoder(bufferIn);
           return (T) xmlDecoder.readObject();         
       }finally{
           if(null != xmlDecoder)
        	   xmlDecoder.close();
           if(null != bufferIn)
           	try{bufferIn.close();}catch(IOException e){};
           if(null != byteArrayIn)
           	try{byteArrayIn.close();}catch(IOException e){};
       }
	}
	/**
	 * 将String转换为yzkf.utils.Xml
	 * @return yzkf.utils.Xml
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public static Xml parseXml(String strXml) throws ParserConfigurationException, SAXException, IOException{
		Document doc = parseDocument(strXml);		
		return new Xml(doc);
	}
	/**
	 * 将String转换成Document
	 * 
	 * @param xmlString
	 *            XML字符串
	 * @return
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public static Document parseDocument(String xmlString) throws ParserConfigurationException, SAXException, IOException {
		// 创建Document工厂
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		// 读取字符串来源
		InputSource is = new InputSource(new StringReader(xmlString));
		// 装载xml串
		return db.parse(is);
	}
	/**
	 * 加载指定路径的XML文件，返回Document
	 * @param filePath XML路径
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static Document loadDocument(String filePath) throws ParserConfigurationException,SAXException, FileNotFoundException,IOException{		
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();	
		InputStream is=new java.io.FileInputStream(new File(filePath));
		return db.parse(is);
	}
	/**
	 * 查找指定XPath节点，返回字符串
	 * @param expression XPath表达式
	 * @param source 要查找节点的Document对象
	 * @return
	 * @throws XPathExpressionException
	 */
	public static String xpathEvaluate(String expression,Document source) throws XPathExpressionException
	{
		return XPathFactory.newInstance().newXPath().evaluate(expression, source);
	}
	/**
	 * 查找指定XPath节点，返回字符串
	 * @param expression XPath表达式
	 * @param source 要查找节点的Document对象
	 * @param returnType 返回值类型，例如：XPathConstants.NODE
	 * @return 返回指定的类型
	 * @throws XPathExpressionException
	 */
	public static Object xpathEvaluate(String expression,Document source,QName returnType) throws XPathExpressionException
	{
		return XPathFactory.newInstance().newXPath().evaluate(expression, source,returnType);
	}
	
}
