package yzkf.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.commons.lang.StringUtils;

/**
 * Socket连接帮助类，静态类，使用短连接方式
 * @author qiulw
 *
 */
public class SocketClient {
	/**
	 * 连接socket服务器，发送字符串，返回输出字符串
	 * @param host 服务器地址
	 * @param port 服务器端口
	 * @param data	要发送的字符串
	 * @param encoding 读取输出流的字节编码
	 * @return 返回服务器返回的字符串
	 * @throws IOException
	 * @example
<pre>
String out = null;
String data = "<root><check>qwer1234你</check><method>GetIntegralRev</method><param><usernumber>13760709457</usernumber></param></root>";
try {
	out = SocketClient.SendString("192.168.9.197", 9099, data,"gb2312");
} catch (IOException e) {
	e.printStackTrace();
}
System.out.println(out);
</pre>
	 */
	public static String SendString(String host,int port,String data,String charsetName) throws IOException{
		Socket socket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		try{
			socket=new Socket(host,port);
			out =new PrintWriter(socket.getOutputStream());
			if(StringUtils.isEmpty(charsetName))
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			else
				in = new BufferedReader(new InputStreamReader(socket.getInputStream(),charsetName));
			out.println(data);
			out.flush();
			String outData = in.readLine();
			return outData;
		}finally{			
			if(out != null)
				out.close();
			if(in != null)
				in.close();
			if(socket !=null){
				try{socket.close();}catch(IOException e){};
			}
		}
	}
	/**
	 * 连接socket服务器，发送字符串，返回输出字符串
	 * @param host 服务器地址
	 * @param port 服务器端口
	 * @param data	要发送的字符串
	 * @return 返回服务器返回的字符串
	 * @throws IOException
	 * @see {@link #SendString(String, int, String, String)}
	 */
	public static String SendString(String host,int port,String data) throws IOException{
		return SendString(host,port,data,null);	
	}
}
