package yzkf.exception;

/**
 * yzkf包中程序异常
 * @author qiulw
 * @version v1.0.0	2014.3.21
 */
public class AppException extends Exception {
	private static final long serialVersionUID = 2729992367039578684L;
	final static String MESSAGE = "应用程序发生错误.";
	public AppException() {
        super(MESSAGE);
    }
    public AppException(String msg) {
        super(msg);
    }
}
