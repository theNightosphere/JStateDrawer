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
		assertEquals(tm.getTrigger(), "DEFAULT");
	}
	
	/**
	 * Test the constructor when passed values.
	 */
	@Test
	public void testNonDefaultConstructor()
	{
		String trigger = "TEST";
		StateFigureModel ef = new StateFigureModel();
		StateFigureModel sf = new StateFigureModel();
		tm = new TransitionModel(trigger, sf, ef);
		assertEquals(tm.getTrigger(), "TEST");
	}
	
	/**
	 * 2 Tests that transition's action cannot be set to the empty string.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetTooShortTriggerThrowsException()
	{
		tm.setTrigger("");
	}
	
	/**
	 * Tests that a trigger cannot have a number, underscore, lowercase letter or space
	 * as the first character in a trigger string.
	 */
	@Test
	public void testTriggerFirstLetterNotCorrectThrowsException()
	{
		try
		{
			tm.setTrigger("0OTHERWISE_OKAY");
			fail("Didn't throw exception when starting with number");
		}
		catch(Exception e)
		{
			// Success!
		}
		try
		{
			tm.setTrigger("aOTHERWISE_OKAY");
			fail("Didn't throw exception when starting with lowercase letter.");
		}
		catch(Exception e)
		{
			// Success!
		}
		try
		{
			tm.setTrigger(" OTHERWISE_OKAY");
			fail("Didn't throw exception when starting with whitespace");
		}
		catch(Exception e)
		{
			// Success!
		}
	}
	
	/**
	 * Tests that invalid characters (as defined in the constructor's summary)
	 * cause setTrigger to throw an exception.
	 */
	@Test
	public void testBadCharactersInLocOtherThanZeroThrowsException()
	{
		try
		{
			tm.setTrigger("Anot_GOOD");
			fail("Didn't throw exception when trigger had lowercase letters.");
		}
		catch(Exception e)
		{
			// Success!
		}
		try
		{
			tm.setTrigger("OTHERWISE OKAY");
			fail("Didn't throw exception when trigger had whitespace");
		}
		catch(Exception e)
		{
			// Success!
		}
		try
		{
			tm.setTrigger("HEY\\/BAD*;CHARACTERS");
			fail("Didn't throw exception when trigger had invalid characters.");
		}
		catch(Exception e)
		{
			// Success!
		}
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
		assertEquals(tm.getTrigger(), "DEFAULT");
		tm.setTrigger("NOT_DEFAULT");
		assertEquals(tm.getTrigger(), "NOT_DEFAULT");
	}
	
	/**
	 * 2 Tests that start state cannot be null
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
