package yzkf.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.log4j.helpers.LogLog;

public class FileRW {
	/**
	 * 复制文件
	 * @param src 源文件路径
	 * @param dest 目标文件路径
	 * @throws IOException
	 */
	public static void copy(String src,String dest) throws IOException{
		FileInputStream input = null;
		FileOutputStream output = null;
		
		try {
			input=new FileInputStream(src);		
	        File file=new File(dest);        
	        if(!file.exists())
	        	newFile(file);
	        output=new FileOutputStream(file);

	        byte buffer[]=new byte[1024];
	        while((input.read(buffer))!=-1){
	        	output.write(buffer);    
	        }
		}finally{
			try{
				if(input != null)
					input.close();
			}finally{
				if(output != null)
					output.close();
			}
		}        
    }
	/**
	 * 读取文件返回字节内容
	 * @param fileName
	 * @return
	 * @throws IOException 
	 */
	public static byte[] readBytes(String fileName) throws IOException{ 
		File file = new File(fileName); 
		FileInputStream input = null; 
		try { 
			input = new FileInputStream(file); 
			byte[] buffer = new byte[(int)file.length()];
			input.read(buffer);
			return buffer;
		}finally{
			if(input!=null)
				input.close(); 
		}
	}
	/**
	 * 读取文件返回字符串内容
	 * @param fileName 文件路径
	 * @param charset 编码格式
	 * @return
	 * @throws IOException 
	 */
	public static String readString(String fileName,String charset) throws IOException{ 
		byte[] buffer = readBytes(fileName);
		if(charset == null){
			return new String(buffer);
		}else{
			return new String(buffer, charset);
		}
	}
	/**
	 * 读取文件返回字符串内容，使用默认编码格式
	 * @param fileName 文件路径
	 * @return
	 * @throws IOException
	 */
	public static String readString(String fileName) throws IOException{ 
		return readString(fileName, null);
	}
	/**
	 * 写文件(文件不存在则创建)
	 * @param fileName 文件路径
	 * @param content 内容
	 * @param isAppend 是否以追加的方式写入
	 * @param charset 编码格式
	 * @throws IOException
	 */
	protected static void write(String fileName,String content,boolean isAppend,String charset) throws IOException{
		FileOutputStream output = null;
		try {
	        File file=new File(fileName);  
	        if(!file.exists()){
	        	newFile(file);
	        }
	        output=new FileOutputStream(file,isAppend);	 
	        byte[] btyes;
	        if(charset == null){
	        	btyes = content.getBytes();
	        }else{
	        	btyes = content.getBytes(charset);
	        }
	        output.write(btyes);
	        output.flush();
		}finally{
			if(output != null)
				output.close();
		}        
	
	}
	/**
	 * 异步写入文件中，主线程不等待，但是非线程安全
	 * @param fileName
	 * @param content
	 * @param isAppend
	 * @param charset
	 */
	protected static void writeAsync(String fileName,String content,boolean isAppend,String charset){
		 if(charset == null){
			 charset = Charset.defaultCharset().name();
		 }
		AsyncFileWriter thread = new AsyncFileWriter();
		thread.setFileName(fileName);
		thread.setContent(content);
		thread.setAppend(isAppend);
		thread.setCharset(charset);
		thread.start();
	}
	/**
	 * 写文件(文件不存在则创建)，使用默认字符编码
	 * @param fileName 文件路径
	 * @param content 内容
	 * @throws IOException
	 */
	public static void write(String fileName,String content) throws IOException{
		write(fileName, content,false, null);
	}
	/**
	 * 写文件(文件不存在则创建)
	 * @param fileName 文件路径
	 * @param content 内容
	 * @param charset 编码格式
	 * @throws IOException
	 */
	public static void write(String fileName,String content,String charset) throws IOException{
		write(fileName, content, false, charset);
	}
	/**
	 * 向文件中追加文本(文件不存在则创建)
	 * @param fileName 文件路径
	 * @param content 内容
	 * @param charset 编码格式
	 * @throws IOException
	 */
	public static void append(String fileName,String content,String charset) throws IOException{
		write(fileName, content, true, charset);
	}
	public static void appendAsync(String fileName,String content,String charset){
		writeAsync(fileName, content, true, charset);
	}
	/**
	 * 向文件中追加文本(文件不存在则创建),使用默认编码格式
	 * @param fileName 文件路径
	 * @param content 内容
	 * @throws IOException
	 */
	public static void append(String fileName,String content) throws IOException{
		write(fileName, content, true, null);
	}
	/**
	 * 写入一行文本(文件不存在则创建)
	 * @param fileName 文件路径
	 * @param content 内容
	 * @param charset 编码格式
	 * @throws IOException
	 */
	public static void appendln(String fileName,String content,String charset) throws IOException{
		append(fileName, content + "\r\n",charset);	
	}
	/**
	 * 写入一行文本(文件不存在则创建)，异步操作，主线程不等待，但是非线程安全
	 * @param fileName
	 * @param content
	 * @param charset
	 */
	public static void appendlnAsync(String fileName,String content,String charset){
		appendAsync(fileName, content + "\r\n",charset);	
	}
	/**
	 * 写入一行文本(文件不存在则创建)，使用默认编码格式
	 * @param fileName 文件路径
	 * @param content 内容
	 * @throws IOException
	 */
	public static void appendln(String fileName,String content) throws IOException{
		appendln(fileName,content,null);	
	}
	/**
	 * 创建新文件
	 * @param fileName 文件路径
	 * @return true 文件不存在并创建成功，false 文件已存在
	 * @throws IOException
	 */
	public static boolean newFile(String fileName) throws IOException{
		File file = new File(fileName);
		return newFile(file);
	}
	/**
	 * 创建新文件
	 * @param file
	 * @return true 文件不存在并创建成功，false 文件已存在
	 * @throws IOException
	 */
	public static boolean newFile(File file) throws IOException{
		if(!file.exists()){	        	
        	File dir =  file.getParentFile();
        	if(!dir.exists())
        		dir.mkdirs();
            return file.createNewFile();
        }
		return false;
	}
}
class AsyncFileWriter extends Thread
{
	private String fileName;
	private String content;
	private boolean isAppend;
	private String charset = "UTF-8";
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public void setAppend(boolean isAppend) {
		this.isAppend = isAppend;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}

	public void run(){
		FileOutputStream output = null;
		try {
	        File file=new File(fileName);  
	        if(!file.exists()){
	        	newFile(file);
	        }
	        output=new FileOutputStream(file,isAppend);	 
	        byte[] btyes;
	        if(charset == null){
	        	btyes = content.getBytes();
	        }else{
	        	btyes = content.getBytes(charset);
	        }
	        output.write(btyes);
	        output.flush();
		}catch(Exception ex){
			LogLog.error("AsyncFileWriter Error", ex);
		}finally{
			if(output != null)
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		} 
	}
	boolean newFile(File file) throws IOException{
		if(!file.exists()){	        	
        	File dir =  file.getParentFile();
        	if(!dir.exists())
        		dir.mkdirs();
            return file.createNewFile();
        }
		return false;
	}
};
