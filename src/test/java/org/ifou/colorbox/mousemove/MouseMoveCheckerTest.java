package org.ifou.colorbox.mousemove;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.MouseInfo;
import java.awt.Point;

import org.ifou.colorbox.mousemove.MouseMoveChecker;
import org.ifou.colorbox.mousemove.MouseMoveEvent;
import org.ifou.colorbox.mousemove.MouseMoveException;
import org.ifou.colorbox.mousemove.MouseMoveListener;
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
	
	
	//No move mouse during this test
	@Test
	public void fireMouseMovedTest() throws MouseMoveException{
		MockMouseMoveListener listener=new MockMouseMoveListener();
		checker.addMouseMoveListener(listener);
		assertNull(listener.getPoint());
		checker.startChecker();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		checker.stopChecker();
		checker.removeMouseMoveListener(listener);
		assertEquals(MouseInfo.getPointerInfo().getLocation(),listener.getPoint());
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
