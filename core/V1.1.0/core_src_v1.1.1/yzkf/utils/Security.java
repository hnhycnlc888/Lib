package yzkf.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import cn.richinfo.core.utils.MD5;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Security {	
  
    /** 
     * BASE64加密 
     * @param key 
     * @return 
     * @throws Exception 
     */  
    public static String encryptBASE64(byte[] key) throws Exception {  
        return (new BASE64Encoder()).encodeBuffer(key);  
    }
    /**
     * BASE64解密 
     * @param key
     * @return
     * @throws Exception
     */
    public static String decryptBASE64(String key) throws Exception {  
        return new String((new BASE64Decoder()).decodeBuffer(key));  
    }
    /**
	 * 将文件转换为Base64编码字符串
	 * @param pathname 文件路径
	 * @return
	 * @throws IOException 文件不存在或读取异常
	 */
	public static String encryptFileToBase64(String pathname) throws IOException{
		File file= new File(pathname);
		FileInputStream fis = new FileInputStream(file);
		byte[] buffer = new byte[(int)file.length()];
		fis.read(buffer);
		fis.close();
		return new BASE64Encoder().encode(buffer);
	}
	/**
	 * 将Base64字符串解码为文件并保存到指定路径
	 * @param base64String Base64字符串
	 * @param pathname 文件保存路径
	 * @throws IOException
	 */
	public static void decryptBase64ToFile(String base64String,String pathname) throws IOException{
		byte[] buffer = new BASE64Decoder().decodeBuffer(base64String);
		FileOutputStream fos = new FileOutputStream(pathname);
		fos.write(buffer);
		fos.close();
	}
	/**
	 * MD5数据签名
	 * @param originString
	 * @return
	 */
    public static String encryptMD5(String originString){   
       return MD5.encode(originString);
    }  
    /** 
     * SHA加密 
     *  
     * @param source 
     * @return 
     * @throws Exception 
     */  
    public static String encryptSHA(byte[] source) throws Exception {  
  
        MessageDigest sha = MessageDigest.getInstance("SHA");  
        sha.update(source);  
  
        return new String(sha.digest());  
  
    } 
    /**
     * DES加密偏移量
     */
    private static final byte[] DESIV = { 30, (byte) 0xa2, 0x61, 0x5f,
		(byte) 0xd0, 60, (byte) 0x99, (byte) 0xbb };
    /**
     * DES加密CBC模式
     * @deprecated 请参考{@link #encryptDES(String, String, String) encryptDES(source, key, charSetName)}
     * @param source 需要加密的字符串
     * @param key 秘钥
     * @return 加密后的字符串
     * @throws Exception
     */
    public static String encryptDES(String source,String key) throws Exception {
		Cipher enCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");// 得到加密对象Cipher	
		
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		DESKeySpec keySpec = new DESKeySpec(key.getBytes("utf-8"));
		SecretKey deskey = keyFactory.generateSecret(keySpec);
		IvParameterSpec iv = new IvParameterSpec(DESIV);		
		enCipher.init(Cipher.ENCRYPT_MODE, deskey, iv);
		
		byte[] pasByte = enCipher.doFinal(source.getBytes("utf-8"));
		BASE64Encoder base64Encoder = new BASE64Encoder();
		return base64Encoder.encode(pasByte);
	}
    /**
     * 
     * DES解密CBC模式
     * @deprecated 请参考{@link #encryptDES(String, String, String) encryptDES(source, key, charSetName)} 和 {@link #decryptDES(String, String, String) decryptDES(source, key, charSetName)}
     * @param source 需要解密的字符串
     * @param key 秘钥
     * @return 解密后的字符串
     * @throws Exception
     */
	public static String decryptDES(String source,String key) throws Exception {
		Cipher deCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		DESKeySpec keySpec = new DESKeySpec(key.getBytes("utf-8"));
		SecretKey deskey = keyFactory.generateSecret(keySpec);
		IvParameterSpec iv = new IvParameterSpec(DESIV);	
		deCipher.init(Cipher.DECRYPT_MODE, deskey, iv);
		
		BASE64Decoder base64Decoder = new BASE64Decoder();		
		byte[] pasByte = deCipher.doFinal(base64Decoder.decodeBuffer(source));
		return new String(pasByte, "utf-8");
	}

	/**
	 * [常用]DES加密，ECB模式，返回16进制密文
	 * @param source 需要加密的字符串
	 * @param key 密钥
	 * @param charSetName 字符编码
	 * @return 返回加密后的16禁止密文
	 */
	public static String encryptDES(String source, String key, String charSetName)
	{
		if (source == null) return null;
		try
		{
			return byte2hex(encryptDES(source.getBytes(charSetName), key.getBytes(charSetName),"DES/ECB/PKCS5Padding"));
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	/**
	 * [常用]DES解密，ECB模式，输入为16进制密文
	 * <p>对应加密方法{@link #encryptDES(String, String, String) encryptDES(source, key, charSetName)}</p>
	 * @param source 需要解密的字符串，16进制密文
	 * @param key 密钥
	 * @param charSetName 字符编码
	 * @return 返回解密后的字符串
	 */
	public static String decryptDES(String source, String key, String charSetName)
	{
		if (source == null) return null;
		try
		{
			return new String(decryptDES(hex2byte(source.getBytes(charSetName)), key.getBytes(charSetName),"DES/ECB/PKCS5Padding"));	}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	/**
	 * DES加密ECB模式,返回加密后字符串的base64编码
	 * @param source
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encryptDESECB(String source,String key) throws Exception{
//		Cipher enCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");  
//        
//		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
//		DESKeySpec keySpec = new DESKeySpec(key.getBytes("gb2312"));
//		SecretKey deskey = keyFactory.generateSecret(keySpec);
//		enCipher.init(Cipher.ENCRYPT_MODE, deskey);
//		
//		byte[] pasByte = enCipher.doFinal(source.getBytes("gb2312"));
//		BASE64Encoder base64Encoder = new BASE64Encoder();
//		return base64Encoder.encode(pasByte);
		byte[] pasByte = encryptDES(source.getBytes("gb2312"),key.getBytes("gb2312"),"DES/ECB/PKCS5Padding");
		BASE64Encoder base64Encoder = new BASE64Encoder();
		return base64Encoder.encode(pasByte);
	}
	
	/**
	 * DES解密ECB模式，密文采用base64编码的密文字符串
	 * @param source
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String decryptDESECB(String source,String key) throws Exception{
//		Cipher enCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");  
//        
//		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
//		DESKeySpec keySpec = new DESKeySpec(key.getBytes("gb2312"));
//		SecretKey deskey = keyFactory.generateSecret(keySpec);
//		enCipher.init(Cipher.DECRYPT_MODE, deskey);
//		
//		BASE64Decoder base64Decoder = new BASE64Decoder();
//		byte[] pasByte = enCipher.doFinal(base64Decoder.decodeBuffer(source));
//		return new String(pasByte, "gb2312");
		BASE64Decoder base64Decoder = new BASE64Decoder();
		byte[] pasByte = decryptDES(base64Decoder.decodeBuffer(source), key.getBytes("gb2312"), "DES/ECB/PKCS5Padding");
		return new String(pasByte, "gb2312");
	}
	/**
	 * DES加密
	 * @param src 数据源
	 * @param key 密钥，长度必须是8的倍数
	 * @return    返回加密后的数据
	 * @throws RuntimeException
	 */
	public static byte[] encryptDES(byte[] src, byte[] key,String transformation) throws RuntimeException
	{
		return des(src, key, transformation, Cipher.ENCRYPT_MODE,null,null);
	}
	public static byte[] encryptDES(byte[] src, byte[] key,String transformation,AlgorithmParameterSpec params) throws RuntimeException
	{
		return des(src, key, transformation, Cipher.ENCRYPT_MODE,params,null);
	}
	public static byte[] encryptDES(byte[] src, byte[] key,String transformation,SecureRandom random) throws RuntimeException
	{
		return des(src, key, transformation, Cipher.ENCRYPT_MODE,null,random);
	}
	/**
	 * 解密
	 * @param src   数据源
	 * @param key   密钥，长度必须是8的倍数
	 * @return      返回解密后的原始数据
	 * @throws RuntimeException
	 */
	public static byte[] decryptDES(byte[] src, byte[] key,String transformation) throws RuntimeException
	{
		return des(src, key, transformation, Cipher.DECRYPT_MODE,null,null);
	}
	public static byte[] decryptDES(byte[] src, byte[] key,String transformation,AlgorithmParameterSpec params) throws RuntimeException
	{
		return des(src, key, transformation, Cipher.DECRYPT_MODE,params,null);
	}
	public static byte[] decryptDES(byte[] src, byte[] key,String transformation,SecureRandom random) throws RuntimeException
	{
		return des(src, key, transformation, Cipher.DECRYPT_MODE,null,random);
	}
	private static byte[] des(byte[] src, byte[] key,String transformation,int opmode,AlgorithmParameterSpec params,SecureRandom random){		
		try
		{
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			DESKeySpec keySpec = new DESKeySpec(key);
			SecretKey securekey = keyFactory.generateSecret(keySpec);
			Cipher cipher = Cipher.getInstance(transformation);
			cipher.init(opmode, securekey);
			cipher.init(opmode, securekey, params, random);
			return cipher.doFinal(src);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	/**
	 * 二行制转十六进制
	 * @param b
	 * @return
	 */
	public static String byte2hex(byte[] b)
	{
		StringBuilder hs = new StringBuilder();
		String stmp;
		for (int n = 0; b != null && n < b.length; n++)
		{
			stmp = Integer.toHexString(b[n] & 0XFF);
			if (stmp.length() == 1)
				hs.append('0');
			hs.append(stmp);
		}
		return hs.toString().toUpperCase();
	}
	/**
	 * 十六进制转换为二进制
	 * @param b
	 * @return
	 */
	public static byte[] hex2byte(byte[] b)
	{
		if ((b.length % 2) != 0)
			throw new IllegalArgumentException();
		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length; n += 2)
		{
			String item = new String(b, n, 2);
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}
		return b2;
	}
}
