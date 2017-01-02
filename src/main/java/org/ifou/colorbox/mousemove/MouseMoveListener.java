package org.ifou.colorbox.mousemove;
import java.util.EventListener;

/**
 * The listener interface for receiving mouseMove events.
 * The class that is interested in processing a mouseMove
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's addMouseMoveListener method. When
 * the mouseMove event occurs, that object's appropriate
 * method is invoked.
 *
 * @see MouseMoveEvent
 */
public interface MouseMoveListener extends EventListener{
	
	/**
	 * Mouse moved.
	 *
	 * @param event the event
	 */
	public void mouseMoved(MouseMoveEvent event);
}
