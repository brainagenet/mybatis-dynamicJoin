package dynamicJoin;



/**
 * Exception to be thrown if something goes wrong while obtaining the joins.
 * @author ggefaell
 *
 */
public class JoinException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public JoinException(String msg){
		super(msg);
	}
	
	public JoinException(String msg,Class clazz){
		super(msg.concat(" Entity: ".concat(clazz.getName())));
	}
	
	public JoinException(String msg, Throwable e){
		super(msg,e);
	}
}
