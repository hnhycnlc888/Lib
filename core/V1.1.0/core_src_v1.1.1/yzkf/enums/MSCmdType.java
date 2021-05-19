package yzkf.enums;

public enum MSCmdType {
	/**WEB自写短信*/
	WebSms(1),
	/**WAP自写短信*/
	WapSms(2),
	/**COCO自写短信*/
	CocoSms(3),
	/**WEB自写彩信*/
	WebMms(4),
	/**WAP自写彩信*/
	WapMms(5),
	/**COCO自写彩信*/
	CocoMms(6);
	
	private int value;
	MSCmdType(int value){
		this.value = value;
	}
	public int getValue(){
		return this.value;
	}
}
