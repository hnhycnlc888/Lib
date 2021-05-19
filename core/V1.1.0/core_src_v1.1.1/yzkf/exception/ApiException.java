package yzkf.exception;
/**
 * API调用异常类
 * @author qiulw
 * @version V4.0.0
 *
 */
public class ApiException extends Exception {
	private static final long serialVersionUID = -5539238086782117945L;
	final static String MESSAGE = "调用接口发生错误.";
	public ApiException() {
        super(MESSAGE);
    }
    public ApiException(String msg) {
        super(msg);
    }
}
