package yzkf.app;

import java.util.regex.Pattern;

import yzkf.config.ConfigFactory;
import yzkf.config.RegexConfig;
import yzkf.exception.ParserConfigException;

/**
 * 正则表达式验证
 * v1.0.0 2011.11.24<br/>
 * 示例：
 * <pre>
 * try {
		ConfigFactory factory = ConfigFactory.getInstance();
		RegexConfig config = factory.newRegexConfig();
		Regex regex = new Regex(config);
		if(!regex.isMobile("13912345678"))
			System.out.println("Invalid mobile");
	} catch (ParserConfigException e) {
		e.printStackTrace();
	}
 * </pre>
 * @author qiulw
 *
 */
public class Regex {
	private RegexConfig config;
	/**
	 * 创建Regex对象
	 * @param config 正则表达式配置对象，不提供则使用默认配置文件
	 */
	public Regex(RegexConfig config){
		this.config = config;
	}
	/**
	 * 创建Regex对象
	 * - 该方法使Regex与RegexConfig耦合度增高，不建议使用，只为方便获取Regex对象
	 * @throws ParserConfigException
	 */
	public Regex() throws ParserConfigException{
		this((ConfigFactory.getInstance()).newRegexConfig());
	}	
	/**
	 * 查找配置文件中的正则表达式匹配输入字符串
	 * @param node 配置文件节点名称
	 * @param input 需要匹配的字符串
	 * @return
	 * @throws ParserConfigException
	 */
	public boolean isOther(String node,String input) throws ParserConfigException{
		return Pattern.matches(config.search(node), input);
	}
	/**
	 * 正则表达式验证，等同  Pattern.matches(regex, input)
	 * @param regex 正则表达式
	 * @param input 需验证的字符串
	 * @return
	 */
	public boolean isMatches(String regex,String input){
		return Pattern.matches(regex, input);
	}
	/**
	 * 是否手机号码
	 * @param input 不带86的手机号码
	 * @return
	 * @throws ParserConfigException
	 */
	public boolean isMobile(String input) throws ParserConfigException {
		return Pattern.matches(config.getMobile(), input);
	}
	/**
	 * 是否手机号码，可带86
	 * @param input 带86或不带86的手机号码
	 * @return
	 * @throws ParserConfigException
	 */
	public boolean isMobile86(String input) throws ParserConfigException {
		return Pattern.matches(config.getMobile86(), input);
	}
	/**
	 * 是否139邮箱别名
	 * @param input
	 * @return
	 * @throws ParserConfigException
	 */
	public boolean isMail139alias(String input) throws ParserConfigException {
		return Pattern.matches(config.getMail139alias(), input);
	}
	/**
	 * 是否手机号或者139邮箱别名
	 * @param input
	 * @return
	 * @throws ParserConfigException
	 */
	public boolean isMobile139alias(String input) throws ParserConfigException {
		return Pattern.matches(config.getMobile139alias(), input);
	}
	/**
	 * 是否139邮箱密码
	 * @param input
	 * @return
	 * @throws ParserConfigException
	 */
	public boolean isMailpassword(String input) throws ParserConfigException {
		return Pattern.matches(config.getMailpassword(), input);
	}
	/**
	 * 是否邮箱地址
	 * @param input
	 * @return
	 * @throws ParserConfigException
	 */
	public boolean isMailaddress(String input) throws ParserConfigException {
		return Pattern.matches(config.getMailaddress(), input);
	}
	/**
	 * 是否国内固话或小灵通（带区号）
	 * @param input
	 * @return
	 * @throws ParserConfigException
	 */
	public boolean isChinaphone(String input) throws ParserConfigException {
		return Pattern.matches(config.getChinaphone(), input);
	}
	/**
	 * 是否中国身份证
	 * @param input
	 * @return
	 * @throws ParserConfigException
	 */
	public boolean isChinaidcard(String input) throws ParserConfigException {
		return Pattern.matches(config.getChinaidcard(), input);
	}
	/**
	 * 是否邮编
	 * @param input
	 * @return
	 * @throws ParserConfigException
	 */
	public boolean isZipcode(String input) throws ParserConfigException {
		return Pattern.matches(config.getZipcode(), input);
	}
	/**
	 * 是否IP地址
	 * @param input
	 * @return
	 * @throws ParserConfigException
	 */
	public boolean isIpaddress(String input) throws ParserConfigException {
		return Pattern.matches(config.getIpaddress(), input);
	}
	/**
	 * 是否URL
	 * @param input
	 * @return
	 * @throws ParserConfigException
	 */
	public boolean isUrl(String input) throws ParserConfigException {
		return Pattern.matches(config.getUrl(), input);
	}
	/**
	 * 是否完整文件名（带扩展名）
	 * @param input
	 * @return
	 * @throws ParserConfigException
	 */
	public boolean isFilename(String input) throws ParserConfigException {
		return Pattern.matches(config.getFilename(), input);
	}
	/**
	 * 是否完整物理路径（带完整文件名）
	 * @param input
	 * @return
	 * @throws ParserConfigException
	 */
	public boolean isPath(String input) throws ParserConfigException {
		return Pattern.matches(config.getPath(), input);
	}
	/**
	 * 是否发送短信的SpNumber
	 * @param input SpNumber
	 * @return
	 */
	public boolean isSpNumber(String input){
		return Pattern.matches(config.getSpNumber(), input);
	}
	public boolean isSmsBody(String input){
		return Pattern.matches(config.getSmsBody(), input);
	}
	public boolean isMmsSubject(String input){
		return Pattern.matches(config.getMmsSubject(), input);
	}
}
