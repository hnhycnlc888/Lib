package cn.richinfo.core.utils;
import java.security.MessageDigest;
public class MD5 {
	 //十六进制下数字到字符的映射数组
    private final static String[] hexDigits = {"0", "1", "2", "3", "4",
        "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
    
    /**
     * 验证输入的密码是否正确
     * @param password    真正的密码（加密后的真密码）
     * @param inputString    输入的字符串
     * @return    验证结果，boolean类型
     */
    public static boolean authenticatePassword(String password, String inputString) {
        if(password.equals(encode(inputString))) {
            return true;
        } else {
            return false;
        }
    }
    
    /** 对字符串进行MD5加密     */
    public static String encode(String originString){
        if (originString != null){
            try{
                //创建具有指定算法名称的信息摘要
                MessageDigest md = MessageDigest.getInstance("MD5");
                //使用指定的字节数组对摘要进行最后更新，然后完成摘要计算
                byte[] results = md.digest(originString.getBytes());
                //将得到的字节数组变成字符串返回
                String resultString = byteArrayToHexString(results);
                return resultString.toUpperCase();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
    /**
     * 生成对字符串的MD5签名
     * @param originString 源字符串
     * @param charsetName 字符串编码字符
     * @return
     */
    public static String encode(String originString,String charsetName){
        if (originString != null){
            try{
                //创建具有指定算法名称的信息摘要
                MessageDigest md = MessageDigest.getInstance("MD5");
                //使用指定的字节数组对摘要进行最后更新，然后完成摘要计算
                byte[] results = md.digest(originString.getBytes(charsetName));
                //将得到的字节数组变成字符串返回
                String resultString = byteArrayToHexString(results);
                return resultString.toUpperCase();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
    
    /**
     * 转换字节数组为十六进制字符串
     * @param b    字节数组
     * @return    十六进制字符串
     */
    private static String byteArrayToHexString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }
    
    /**
     * 将一个字节转化成十六进制形式的字符串
     * @param b
     * @return
     */
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;        
        return hexDigits[d1] + hexDigits[d2];
        //return Integer.toHexString(d1) + Integer.toHexString(d2);
    }
    /**
     * 将16进制字符串转换为字节数组
     * @param s
     * @return
     */
    public static byte[] hexStringToByteArray(String s){
    	int len = s.length();
    	byte[] arr = new byte[len/2]; 
    	for(int i=0;i<len;i+=2){
    		int d1 = Integer.parseInt(s.substring(i, i+1),16);
    		int d2 = Integer.parseInt(s.substring(i+1, i+2),16);
    		int n = d1*16 + d2;
    		arr[i/2] = (byte) n;
    	}
    	return arr;
    }
}
