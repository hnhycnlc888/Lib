package yzkf.exception;
/**
 * 配置信息异常
 * v1.0.0 2011.11.14
 * @author qiulw
 *
 */
public class ParserConfigException extends RuntimeException {
	private static final long serialVersionUID = 6095381962712002420L;
	final static String MESSAGE = "读取配置文件出错.";
	public ParserConfigException() {
        super(MESSAGE);
    }
    public ParserConfigException(String msg) {
        super(msg);
    }
}
