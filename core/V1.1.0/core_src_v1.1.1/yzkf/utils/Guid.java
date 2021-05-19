package yzkf.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
/**
 * GUID生成类
 * @author qiulw
 * @version V4.0.0
 */
public class Guid {
	private String valueBeforeMD5 = "";
    private String valueAfterMD5 = "";
    private static Random myRand;
    private static SecureRandom mySecureRand;
    private static String s_id;
    private boolean secure;

    static {
        mySecureRand = new SecureRandom();
        long secureInitializer = mySecureRand.nextLong();
        myRand = new Random(secureInitializer);
        try {
            s_id = InetAddress.getLocalHost().toString();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }
    /**
     * 创建GUID对象，使用一般算法
     */
    public Guid() {
    	this.secure = false;
    }
    /**
     * 创建GUID对象
     * @param secure 是否使用更强壮的生成算法，false 一般算法，效率高，true 强壮算法，效率低
     */
    public Guid(boolean secure) {
    	this.secure = secure;
    }
    /**
     * 生成标准GUID字符串
     * @return
     */
    public String newGuid(){
    	return newGuid(true);
    }
    /**
     * 生成GUID字符串
     * @param split 是否使用 "-" 符号分隔，true 使用分割，false 不分隔
     * @return GUID字符串
     */
    public String newGuid(boolean split) {
        MessageDigest md5 = null;
        StringBuffer sbValueBeforeMD5 = new StringBuffer();

        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error: " + e);
        }

        try {
            long time = System.currentTimeMillis();
            long rand = 0;

            if (this.secure) {
                rand = mySecureRand.nextLong();
            } else {
                rand = myRand.nextLong();
            }
            
            sbValueBeforeMD5.append(s_id);
            sbValueBeforeMD5.append(":");
            sbValueBeforeMD5.append(Long.toString(time));
            sbValueBeforeMD5.append(":");
            sbValueBeforeMD5.append(Long.toString(rand));

            valueBeforeMD5 = sbValueBeforeMD5.toString();
            md5.update(valueBeforeMD5.getBytes());

            byte[] array = md5.digest();
            StringBuffer sb = new StringBuffer();
            for (int j = 0; j < array.length; ++j) {
                int b = array[j] & 0xFF;
                if (b < 0x10) sb.append('0');
                sb.append(Integer.toHexString(b));
            }

            valueAfterMD5 = sb.toString();

        } catch (Exception e) {
            System.out.println("Error:" + e);
        }
        
        String raw = valueAfterMD5.toUpperCase();
        if(split){
        	StringBuffer sb = new StringBuffer();
            sb.append(raw.substring(0, 8));
            sb.append("-");
            sb.append(raw.substring(8, 12));
            sb.append("-");
            sb.append(raw.substring(12, 16));
            sb.append("-");
            sb.append(raw.substring(16, 20));
            sb.append("-");
            sb.append(raw.substring(20));
            return sb.toString();
        }else{
        	return raw;
        }
    }
}
