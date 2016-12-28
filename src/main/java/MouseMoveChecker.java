import java.awt.MouseInfo;
import java.awt.Point;
import java.util.ArrayList;

/**
 * The Class MouseMoveChecker.
 */
public final class MouseMoveChecker {

	/** The MouseMoveChecker singleton instance */
	private static MouseMoveChecker CHECKER_INSTANCE = new MouseMoveChecker();

	/** The list of listeners. */
	private ArrayList<MouseMoveListener> list_listeners = new ArrayList<>();

	/** The checker. */
	private Checker checker;

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
	public MouseMoveChecker getInstance() {
		return CHECKER_INSTANCE;
	}

	/**
	 * Adds the mouse move listener.
	 *
	 * @param listener
	 *            the MouseMoveListener
	 */
	public synchronized void addMouseMoveListener(MouseMoveListener listener) {
		this.list_listeners.add(listener);
	}

	/**
	 * Removes the mouse move listener.
	 *
	 * @param listener
	 *            the MouseMoveListener
	 */
	public synchronized void removeMouseMoveListener(MouseMoveListener listener) {
		this.removeMouseMoveListener(listener);
	}

	/**
	 * Start checker.
	 *
	 * @throws MouseMoveException
	 *             the mouse move exception
	 */
	public void startChecker() throws MouseMoveException {
		if (checker != null && checker.isAlive())
			throw new MouseMoveException("Mouse move checker is already running.");
		checker = new Checker();
		checker.start();
	}

	/**
	 * Stop checker.
	 *
	 */
	public void stopChecker() {
		if (checker != null && checker.isAlive())
			checker.stopRun();
	}

	/**
	 * Fire mouse moved.
	 *
	 * @param point
	 *            the point of event
	 */
	public void fireMouseMoved(Point point) {
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
		private volatile boolean running = true;

		private Point current_point;

		public Checker() {
		}

		/**
		 * Stop run.
		 */
		public void stopRun() {
			running = false;
		}

		/**
		 * Thread execution.
		 */
		@Override
		public void run() {
			while (running) {
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
	}
}
