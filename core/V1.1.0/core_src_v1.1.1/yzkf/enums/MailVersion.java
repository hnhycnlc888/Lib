package yzkf.enums;

/**
 * 邮箱版本枚举
 * 
 * <p>
1  简版
2  炫版
3  触屏版，在pc上面是基础版
4   标准版
66  酷版
8  pad版
 * </p>
 * @author qiulw
 *
 */
public enum MailVersion {
	/**简版（适用：手机），Wap1.2，纯文本，功能机适用**/
	Wap1("1"),
	/**炫版（适用：手机），Wap1.2，纯文本，智能机适用**/
	Wap2("2"),	
	/**触屏版/基础版（适用：手机和PC） **/
	Base("3"),
	/**Web标准版（适用：PC） **/
	Web("4"),
	/**酷版（适用：手机），Wap2.0，智能机适用**/
	Cool("66"),
	/**PAD版（适用：PAD）**/
	Pad("8");
	
	private String value;
	MailVersion(String value){
		this.value = value;
	}
	public String getValue(){
		return this.value;
	}
	/**
	 * 将字符串转换为对应值的枚举
	 * @param value
	 * @return
	 */
	public static MailVersion parse(String value){
		for(MailVersion type : MailVersion.values()){
    		if(type.getValue().equals(value))
    			return type;
    	}
    	return null;    	
    }
}
