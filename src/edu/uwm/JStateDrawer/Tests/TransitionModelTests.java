package edu.uwm.JStateDrawer.Tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import edu.uwm.JStateDrawer.Models.StateFigureModel;
import edu.uwm.JStateDrawer.Models.TransitionModel;

public class TransitionModelTests {
	
	private TransitionModel tm;
	
	@Before
	public void initDefaultTransitionModel()
	{
		tm = new TransitionModel();
	}
	
	/**
	 * Tests the default constructor.
	 */
	@Test
	public void testDefaultConstructor() {
		assertEquals(tm.getTrigger(), "default");
	}
	
	/**
	 * Test the constructor when passed values.
	 */
	@Test
	public void testNonDefaultConstructor()
	{
		String trigger = "test";
		StateFigureModel ef = new StateFigureModel();
		StateFigureModel sf = new StateFigureModel();
		tm = new TransitionModel(trigger, sf, ef);
		assertEquals(tm.getTrigger(), "test");
	}
	
	/**
	 * 2 Tests that transition's action cannot be set to the empty string.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetTooShortTriggerThrowsException()
	{
		tm.setTrigger("");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetTooShortTriggerConstructor()
	{
		String trigger = "";
		StateFigureModel ef = new StateFigureModel();
		StateFigureModel sf = new StateFigureModel();
		
		tm = new TransitionModel(trigger, sf, ef);
	}
	
	/**
	 * Test that when passed an allowable trigger (currently just non-empty string),
	 * the trigger is properly changed.
	 */
	@Test
	public void testSetNameProperlyChangesName()
	{
		assertEquals(tm.getTrigger(), "default");
		tm.setTrigger("not default");
		assertEquals(tm.getTrigger(), "not default");
	}
	
	/**
	  *2 Tests that start state cannot be null
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testStartFigureCannotBeNull()
	{
		tm.setStartState(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testStartFigureCannotBeNullConstructor()
	{
		String trigger = "";
		StateFigureModel ef = new StateFigureModel();
		StateFigureModel sf = null;
		
		tm = new TransitionModel(trigger, sf, ef);
	}
	
	/**
	  *2 Tests that end state cannot be null
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testEndFigureCannotBeNull()
	{
		tm.setEndState(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testEndFigureCannotBeNullConstructor()
	{
		String trigger = "";
		StateFigureModel ef = null;
		StateFigureModel sf = new StateFigureModel();
		
		tm = new TransitionModel(trigger, sf, ef);
	}
	
	
}
