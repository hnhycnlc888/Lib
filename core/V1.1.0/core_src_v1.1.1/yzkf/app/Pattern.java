package yzkf.app;

import java.util.regex.Matcher;

import yzkf.exception.ParserConfigException;
/**
 * 预设正则表达式验证
 * v1.0.0 2011.11.24<br/>
 * 示例：
 * <pre>
 * try {
		if(!Pattern.isMobile("13912345678"))
			System.out.println("Invalid mobile");
	} catch (ParserConfigException e) {
		e.printStackTrace();
	}
 * </pre>
 * @author qiulw
 *
 */
public class Pattern {
	private static Regex yzkfRegex;
	private static Regex getRegex() throws ParserConfigException{
		if(yzkfRegex == null){
			yzkfRegex = new Regex();
		}
		return yzkfRegex;
	}
	/**
	 * 查找配置文件中的正则表达式匹配输入字符串
	 * @param node 配置文件节点名称
	 * @param input 需要匹配的字符串
	 * @return
	 * @throws ParserConfigException
	 */
	public static boolean isOther(String node,String input) throws ParserConfigException{
		return getRegex().isOther(node, input);
	}
	/**
	 * 正则表达式验证，等同  Pattern.matches(regex, input)
	 * @param regex 正则表达式
	 * @param input 需验证的字符串
	 * @return
	 */
	public static boolean isMatches(String regex,String input){
		return java.util.regex.Pattern.matches(regex, input);
	}
	/**
	 * <p>获取字符串中首次匹配的的子字符串</p>
	 * <p>注意,一些模式,例如一个*,匹配空字符串。这个方法将返回空字符串。</p>
	 * <p>该方法等同 {@link java.util.regex.Matcher <code>Matcher</code>} 的方法 {@link java.util.regex.Matcher#group group}</p>
	 * @param regex 正则表达式
	 * @param input 需要查找的字符串
	 * @return 返回查找字符串中匹配正则表达式的片段
	 */
	public static String getFirstMatch(String regex,String input){
		return getMatchString(regex, input, 1);
	}
	/**
	 * 获取正则表达匹配的子字符串
	 * @param regex 正在表达式
	 * @param input 需要查找的字符串
	 * @param group 查找的第几个匹配的组，0 第一个，<0 最后一个，>0 按顺序， >最大匹配数 则取最后一个匹配(相当于 <0)
	 * @return
	 */
	public static String getMatchString(String regex,String input,int group){
		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		if(matcher.find()){
			if(group == 0)
				return matcher.group();
			else if(group < 0 || group >= matcher.groupCount())
				return matcher.group(matcher.groupCount());
			else
				return matcher.group(group);
		}else{
			return "";
		}
	}
	/**
	 * 是否手机号码
	 * @param input
	 * @return
	 * @throws ParserConfigException
	 */
	public static boolean isMobile(String input) throws ParserConfigException{
		return getRegex().isMobile(input);
	}
	/**
	 * 是否手机号码，可带86
	 * @param input 带86或者不带86的手机号码
	 * @return
	 * @throws ParserConfigException
	 */
	public static boolean isMobile86(String input) throws ParserConfigException{
		return getRegex().isMobile86(input);
	}
	/**
	 * 是否139邮箱别名
	 * @param input
	 * @return
	 * @throws ParserConfigException
	 */
	public static boolean isMail139alias(String input) throws ParserConfigException{
		return getRegex().isMail139alias(input);
	}
	/**
	 * 是否手机号或者139邮箱别名
	 * @param input
	 * @return
	 * @throws ParserConfigException
	 */
	public static boolean isMobile139alias(String input) throws ParserConfigException{
		return getRegex().isMobile139alias(input);
	}
	/**
	 * 是否139邮箱密码
	 * @param input
	 * @return
	 * @throws ParserConfigException
	 */
	public static boolean isMailpassword(String input) throws ParserConfigException{
		return getRegex().isMailpassword(input);
	}
	/**
	 * 是否邮箱地址
	 * @param input
	 * @return
	 * @throws ParserConfigException
	 */
	public static boolean isMailaddress(String input) throws ParserConfigException{
		return getRegex().isMailaddress(input);
	}
	/**
	 * 是否国内固话或小灵通（带区号）
	 * @param input
	 * @return
	 * @throws ParserConfigException
	 */
	public static boolean isChinaphone(String input) throws ParserConfigException{
		return getRegex().isChinaphone(input);
	}
	/**
	 * 是否中国身份证
	 * @param input
	 * @return
	 * @throws ParserConfigException
	 */
	public static boolean isChinaidcard(String input) throws ParserConfigException{
		return getRegex().isChinaidcard(input);
	}
	/**
	 * 是否邮编
	 * @param input
	 * @return
	 * @throws ParserConfigException
	 */
	public static boolean isZipcode(String input) throws ParserConfigException{
		return getRegex().isZipcode(input);
	}
	/**
	 * 是否IP地址
	 * @param input
	 * @return
	 * @throws ParserConfigException
	 */
	public static boolean isIpaddress(String input) throws ParserConfigException{
		return getRegex().isIpaddress(input);
	}
	/**
	 * 是否URL
	 * @param input
	 * @return
	 * @throws ParserConfigException
	 */
	public static boolean isUrl(String input) throws ParserConfigException{
		return getRegex().isUrl(input);
	}
	/**
	 * 是否完整文件名（带扩展名）
	 * @param input
	 * @return
	 * @throws ParserConfigException
	 */
	public static boolean isFilename(String input) throws ParserConfigException{
		return getRegex().isFilename(input);
	}
	/**
	 * 是否完整物理路径（带完整文件名）
	 * @param input
	 * @return
	 * @throws ParserConfigException
	 */
	public static boolean isPath(String input) throws ParserConfigException{
		return getRegex().isPath(input);
	}
	/**
	 * 是否发送短信SpNumber
	 * @param input SpNumber
	 * @return
	 * @throws ParserConfigException 
	 */
	public static boolean isSpNumber(String input) throws ParserConfigException{
		return getRegex().isSpNumber(input);
	}
	public static boolean isSmsBody(String input) throws ParserConfigException{
		return getRegex().isSmsBody(input);
	}
	public static boolean isMmsSubject(String input) throws ParserConfigException{
		return getRegex().isMmsSubject(input);
	}
}
