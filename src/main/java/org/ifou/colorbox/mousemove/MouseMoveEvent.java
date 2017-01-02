package org.ifou.colorbox.mousemove;
import java.awt.Point;
import java.util.EventObject;

/**
 * The Class MouseMouveEvent
 */
public class MouseMoveEvent extends EventObject{

	/** The point of event */
	private Point point;
	
	/**
	 * Instantiates a new mouse move event.
	 *
	 * @param arg0 it's the point of event
	 */
	public MouseMoveEvent(Point arg0) {
		super(arg0);
		this.point=arg0;

	}

	/**
	 * Gets the point of the mouse move event.
	 *
	 * @return the point
	 */
	public Point getPoint() {
		return this.point;
	}
	
}
