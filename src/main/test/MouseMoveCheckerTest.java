import static org.junit.Assert.*;


import java.awt.Point;
import java.util.List;

import org.junit.After;
import org.junit.Test;

public class MouseMoveCheckerTest {

	public MouseMoveChecker checker = MouseMoveChecker.getInstance();

	@After
	public void stopRun() {
		checker.stopChecker();
	}

	@Test
	public void isRunningBeforeStartTest(){
		assertFalse(checker.isRunning());
	}

	@Test
	public void isRunningAfterStartTest() throws MouseMoveException {
		checker.startChecker();
		assertTrue(checker.isRunning());
	}

	@Test
	public void isRunningAfterStopTest() throws MouseMoveException {
		checker.startChecker();
		checker.stopChecker();
		assertFalse(checker.isRunning());

	}

	@Test(expected = MouseMoveException.class)
	public void multistartCheckerRaiseExceptionTest() throws MouseMoveException {
		checker.startChecker();
		checker.startChecker();
		fail();
	}

	@Test
	public void multistartCheckerNoRaiseExceptionTest() throws MouseMoveException {
		checker.startChecker();
		checker.stopChecker();
		checker.startChecker();
	}

	@Test
	public void addListenerTest(){
		MockMouseMoveListener listener=new MockMouseMoveListener();
		checker.addMouseMoveListener(listener);
		assertEquals(listener,checker.getAllListener().get(0));
		checker.removeMouseMoveListener(listener);
	}
	
	@Test
	public void removeListenerTest(){
		MockMouseMoveListener listener=new MockMouseMoveListener();
		checker.addMouseMoveListener(listener);
		checker.removeMouseMoveListener(listener);
		assertEquals(0,checker.getAllListener().size());
	}	
	
	@Test
	public void fireMouseMovedTest(){
		
	}
	
	
	public final class MockMouseMoveListener implements MouseMoveListener{
		
		private Point point;
		
		@Override
		public void mouseMoved(MouseMoveEvent event) {
			point=event.getPoint();
		}
		
		public Point getPoint(){
			return point;
		}
	}
	
}
