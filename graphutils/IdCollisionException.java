package graphutils;

public class IdCollisionException extends Exception {
	static final long serialVersionUID = 2347856289310101L;

	public IdCollisionException() {
		
	}

	public IdCollisionException(String arg0) {
		super(arg0);
	}

	public IdCollisionException(Throwable arg0) {
		super(arg0);
	}

	public IdCollisionException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
