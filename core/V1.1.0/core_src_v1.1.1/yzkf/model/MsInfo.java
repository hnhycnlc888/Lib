package yzkf.model;

import java.io.Serializable;

/**
 * 短彩使用情况信息
 * 
 * @author qiulw
 * @version V4.0.0 2012.02.16
 */
public class MsInfo implements Serializable {
	private static final long serialVersionUID = -7189779561254737253L;
	private String type;
	private int monthTotal;
	private int monthRemain;
	private int dayRemain;
	private boolean feeSend;
	private int feeValue;
	/**
	 * 获取类型
	 * @return SMS/MMS
	 */
	public String getType() {
		return type;
	}
	/**
	 * 设置类型
	 * @param type SMS/MMS
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * 获取当月赠送免费总数
	 * @return
	 */
	public int getMonthTotal() {
		return monthTotal;
	}
	/**
	 * 设置当月赠送免费总数
	 * @param monthTotal
	 */
	public void setMonthTotal(int monthTotal) {
		this.monthTotal = monthTotal;
	}
	/**
	 * 获取当月剩余免费条数
	 * @return
	 */
	public int getMonthRemain() {
		return monthRemain;
	}
	/**
	 * 设置当月剩余免费条数
	 * @param monthTotal
	 */
	public void setMonthRemain(int monthRemain) {
		this.monthRemain = monthRemain;
	}
	/**
	 * 获取当天可发送条数
	 * @return
	 */
	public int getDayRemain() {
		return dayRemain;
	}
	/**
	 * 设置当天可发送条数
	 * @param dayRemain
	 */
	public void setDayRemain(int dayRemain) {
		this.dayRemain = dayRemain;
	}
	/**
	 * 获取是否允许付费发送
	 * <br/><br/>
	 * 免费短信或彩信发送完后是否允许继续发送短信或彩信
	 * @return
	 */
	public boolean isFeeSend() {
		return feeSend;
	}
	/**
	 * 设置是否允许付费发送
	 * <br/><br/>
	 * 免费短信或彩信发送完后是否允许继续发送短信或彩信
	 * @param feeSend
	 */
	public void setFeeSend(boolean feeSend) {
		this.feeSend = feeSend;
	}
	/**
	 * 获取收费短信或彩信每条的费用，单位为分
	 * @return
	 */
	public int getFeeValue() {
		return feeValue;
	}
	/**
	 * 设置收费短信或彩信每条的费用，单位为分
	 * @param feeValue
	 */
	public void setFeeValue(int feeValue) {
		this.feeValue = feeValue;
	}
	
}
