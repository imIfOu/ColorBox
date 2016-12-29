import java.awt.MouseInfo;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The Class MouseMoveChecker.
 */
public final class MouseMoveChecker {
	
	/**  The MouseMoveChecker singleton instance. */
	private static MouseMoveChecker CHECKER_INSTANCE = new MouseMoveChecker();

	/** The list of listeners. */
	private ArrayList<MouseMoveListener> list_listeners = new ArrayList<>();

	/** The checker. */
	private Checker checker = new Checker();

	/**
	 * Instantiates a new mouse move checker.
	 */
	private MouseMoveChecker() {
	}

	/**
	 * Gets the single instance of MouseMoveChecker.
	 *
	 * @return single instance of MouseMoveChecker
	 */
	public static MouseMoveChecker getInstance() {
		return CHECKER_INSTANCE;
	}

	/**
	 * Adds the mouse move listener.
	 *
	 * @param listener
	 *            the MouseMoveListener
	 */
	public void addMouseMoveListener(MouseMoveListener listener) {
		this.list_listeners.add(listener);
	}

	/**
	 * Removes the mouse move listener.
	 *
	 * @param listener
	 *            the MouseMoveListener
	 */
	public void removeMouseMoveListener(MouseMoveListener listener) {
		this.list_listeners.remove(listener);
	}

	/**
	 * Gets the listeners.
	 *
	 * @return the listeners
	 */
	public List<MouseMoveListener> getAllListener(){
		return new ArrayList<MouseMoveListener>(list_listeners);
	}
	
	
	/**
	 * Start checker.
	 *
	 * @throws MouseMoveException
	 *             the mouse move exception
	 */
	public void startChecker() throws MouseMoveException {
		if (checker != null && isRunning())
			throw new MouseMoveException("Mouse move checker is already running.");
		checker = new Checker();
		checker.setRunningValue(true);
		checker.start();
	}

	/**
	 * Stop checker.
	 *
	 */
	public void stopChecker() {
		if (checker != null && isRunning())
			checker.stopRun();
	}

	
	/**
	 * Checks if is running.
	 *
	 * @return true, if is running
	 */
	public boolean isRunning(){
		return checker.isAlive() && checker.getRunningValue();
	}
	
	/**
	 * Fire mouse moved.
	 *
	 * @param point
	 *            the point of event
	 */
	private void fireMouseMoved(Point point) {
		ArrayList<MouseMoveListener> list = new ArrayList<>(list_listeners);
		if (list.size() > 0) {
			MouseMoveEvent event = new MouseMoveEvent(point);
			for (MouseMoveListener listener : list)
				listener.mouseMoved(event);
		}
	}

	/**
	 * The Class Checker.
	 */
	private final class Checker extends Thread {

		/** The Constant TIMER. */
		private static final int TIMER = 400;

		/** The running. */
		private AtomicBoolean running = new AtomicBoolean(false);

		/** The current point. */
		private Point current_point;

		/**
		 * Instantiates a new checker.
		 */
		public Checker() {
		}

		/**
		 * Stop run.
		 */
		public void stopRun() {
			running.set(false);
		}

		/**
		 * Thread execution.
		 */
		@Override
		public void run() {
			while (running.get()) {
				try {
					Point point = MouseInfo.getPointerInfo().getLocation();
					if (!point.equals(current_point)) {
						fireMouseMoved(point);
						current_point = point;
					}
					sleep(TIMER);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		/**
		 * Gets the running value.
		 *
		 * @return the running value
		 */
		public boolean getRunningValue(){
			return running.get();
		}
		
		/**
		 * Sets the running value.
		 *
		 * @param v the new running value
		 */
		public void setRunningValue(boolean v){
			running.set(v);
		}
	}
}
