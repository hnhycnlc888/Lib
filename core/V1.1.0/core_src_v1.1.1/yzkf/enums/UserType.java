package yzkf.enums;

public enum UserType {
	Normal("1"),RedList("2"),BlackList("3");
	
	private String value;
	public String getValue() {
	    return this.value;
	}
	UserType(String value) {
	    this.value = value;
	}
	/**
	 * 将字符串转换为对应值的枚举
	 * @param value
	 * @return
	 */
	public static UserType parse(String value){
		for(UserType type : UserType.values()){
    		if(type.getValue().equals(value))
    			return type;
    	}
    	return null;
    	
    }
}
