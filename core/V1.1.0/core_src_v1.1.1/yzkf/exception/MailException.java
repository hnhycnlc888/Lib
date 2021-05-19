package yzkf.exception;
/**
 * 邮件操作异常
 * v1.0.0 2011.11.14
 * @author qiulw
 *
 */
public class MailException extends Exception {
	private static final long serialVersionUID = 822894650801297924L;
	final static String MESSAGE = "发送邮件发生错误.";
	public MailException() {
        super(MESSAGE);
    }
    public MailException(String msg) {
        super(msg);
    }
}
