package yzkf.enums;

public enum NotifyType {
	Close(0,"关闭通知"),
	Sms(1,"普通短信"),
	Mms(2,"彩信"),
	WapPush(3,"Wap Push"),
	LongSms(4,"长短信"),
	HandsFree(5,"免提短信"); 
	 
    private int value;  
    private String describe; 
      
    NotifyType(int value,String describe){  
        this.value = value;  
        this.describe = describe;  
    }
    /**
     * 获取邮件提醒类型对应的值
     * @return 0/1/2/3/4/5
     */
    public int getValue(){
    	return this.value;
    }
    /**
     * 获取邮件提醒类型的描述
     * @return
     */
    public String getDescribe(){
    	return this.describe;
    }
    /**
     * 将整形转换成邮件提醒枚举类型
     * @param value 0/1/2/3/4/5
     * @return 邮件提醒枚举对象 NotifyType
     */
    public static NotifyType parse(int value){
    	for(NotifyType type : NotifyType.values()){
    		if(type.getValue() == value)
    			return type;
    	}
    	return NotifyType.Close;
    }
    /**
     * 将整形转换成邮件提醒枚举类型
     * @param value 0/1/2/3/4/5
     * @return 邮件提醒枚举对象 NotifyType
     */
    public static NotifyType parse(String value){
    	try{
    		int intValue = Integer.parseInt(value);
    		return parse(intValue);
    	}catch(NumberFormatException e){
    		return NotifyType.Close;
    	}
    	
    }
}
