package org.ifou.colorbox.mousemove;

/**
 * The Class MouseMoveException.
 */
public class MouseMoveException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new mouse move exception.
	 */
	public MouseMoveException() {
		super();
	}
	
	/**
	 * Instantiates a new mouse move exception.
	 *
	 * @param message the message
	 */
	public MouseMoveException(String message) {
		super(message);
	}
	
	/**
	 * Instantiates a new mouse move exception.
	 *
	 * @param throwable the throwable
	 */
	public MouseMoveException(Throwable throwable) {
		super(throwable);
	}
	
	/**
	 * Instantiates a new mouse move exception.
	 *
	 * @param message the message
	 * @param throwable the throwable
	 */
	public MouseMoveException(String message,Throwable throwable) {
		super(message, throwable);
	}
}
