package edu.uwm.JStateDrawer.Tests;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jhotdraw.xml.NanoXMLDOMInput;
import org.jhotdraw.xml.NanoXMLDOMOutput;
import org.junit.Before;
import org.junit.Test;

import edu.uwm.JStateDrawer.DrawerFactory;
import edu.uwm.JStateDrawer.Models.StateFigureModel;
import edu.uwm.JStateDrawer.Models.TransitionModel;

public class TransitionModelTests {
	
	private TransitionModel tm;
	private StateFigureModel sf, ef;
	
	@Before
	public void initDefaultTransitionModel()
	{
		tm = new TransitionModel();
		sf = new StateFigureModel();
		ef = new StateFigureModel();
		sf.addOutgoingTransition(tm);
		ef.addIncomingTransition(tm);
	}
	
	/**
	 * Tests the default constructor.
	 */
	@Test
	public void testDefaultConstructor() {
		assertEquals(tm.getEvent(), "DEFAULT");
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
		assertEquals(tm.getEvent(), "TEST");
	}
	
	/**
	 * 2 Tests that transition's action cannot be set to the empty string.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetTooShortTriggerThrowsException()
	{
		tm.setEvent("");
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
			tm.setEvent("0OTHERWISE_OKAY");
			fail("Didn't throw exception when starting with number");
		}
		catch(Exception e)
		{
			// Success!
		}
		try
		{
			tm.setEvent("aOTHERWISE_OKAY");
			fail("Didn't throw exception when starting with lowercase letter.");
		}
		catch(Exception e)
		{
			// Success!
		}
		try
		{
			tm.setEvent(" OTHERWISE_OKAY");
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
			tm.setEvent("Anot_GOOD");
			fail("Didn't throw exception when trigger had lowercase letters.");
		}
		catch(Exception e)
		{
			// Success!
		}
		try
		{
			tm.setEvent("OTHERWISE OKAY");
			fail("Didn't throw exception when trigger had whitespace");
		}
		catch(Exception e)
		{
			// Success!
		}
		try
		{
			tm.setEvent("HEY\\/BAD*;CHARACTERS");
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
		assertEquals(tm.getEvent(), "DEFAULT");
		// Ensures that start figure has a transition stored under "DEFAULT"
		assert(!(sf.getTransitionByEvent("DEFAULT") == null));
		assert(sf.getTransitionByEvent("NOT_DEFAULT") == null);

		tm.setEvent("NOT_DEFAULT");
		assertEquals(tm.getEvent(), "NOT_DEFAULT");
		// Ensures that setTrigger properly updated the start state's recorded trigger.
		assert(sf.getTransitionByEvent("DEFAULT") == null);
		assert(!(sf.getTransitionByEvent("NOT_DEFAULT") == null));
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
	
	/**
	 * Tests that exporting and importing XML properly saves and retrieves TransitionModel data. 
	 * @throws IOException
	 */
	@Test
	public void testExportXML() throws IOException
	{
		tm = new TransitionModel("TEST", new StateFigureModel("start"),
				new StateFigureModel("end"));
		tm.addAction("test1");
		tm.addAction("test2");
		DrawerFactory factory = new DrawerFactory();
		NanoXMLDOMOutput out = new NanoXMLDOMOutput(factory);
		String uri = "src/edu/uwm/JStateDrawer/Tests/testTransition.xml";
		out.openElement("test");
		// Create the XML representation of the TransitionModel
		try {
			// Factory.write will call tm.write(out) which exports the XML.
			// factory.write ensures that the XML tags are placed around the TransitionModel that will
			// allow it to be read via the readObject method later.
			out.openElement("transitionModel");
			tm.write(out);
			out.closeElement();
			out.closeElement();
		} catch (IOException e) {
			fail("Exporting XML caused some sort of problem.");
			e.printStackTrace();
		}
		//URI uri = new URI("testTransition.xml");
		BufferedOutputStream output = new BufferedOutputStream(
				new FileOutputStream(new File(uri)));
		// Writes the XML to a file.
		try{
			out.save(output);
			out.dispose();
		}
		catch(FileNotFoundException e)
		{
			fail("File not created correctly.");
		} catch (IOException e) {
			fail("Something failed in out.save(output)");
			e.printStackTrace();
		}
		finally
		{
			output.close();
		}
		
		// Open the file
		
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(uri)));
		
		NanoXMLDOMInput domIn = new NanoXMLDOMInput(factory, in);
		
		try
		{
			domIn.openElement("test");
			TransitionModel fileTransition = (TransitionModel) domIn.readObject();
			domIn.closeElement();
			assertEquals(fileTransition.getEvent(), "TEST");
			assertEquals(fileTransition.getStartState().getName(), "start");
			assertEquals(fileTransition.getEndState().getName(), "end");
			assert(fileTransition.getStartState().getOutgoingTransitions().contains(fileTransition));
			assertEquals(fileTransition.getStartState().getTransitionByEvent("TEST"),fileTransition);
			assert(fileTransition.getAllActions().size() == 2);
			assert(fileTransition.getAllActions().contains("test1"));
			assert(fileTransition.getAllActions().contains("test2"));
			
		}
		catch(IOException e)
		{
			fail("Attempted to access an element that does not exist");
			e.printStackTrace();
		}
		finally
		{
			in.close();
		}	
	}
	
	
	@Test
	public void testAddTransitionActionWorksProperly()
	{
		assert(tm.getAllActions().isEmpty());
		tm.addAction("test1");
		assert(!tm.getAllActions().isEmpty());
		assert(tm.getAllActions().contains("test1"));
		tm.addAction("test2");
		assertEquals(tm.getAllActions().size(), 2);
		assert(tm.getAllActions().contains("test2"));
	}
	
	@Test
	public void testRemoveTransitionActionWorksProperly()
	{
		assert(tm.getAllActions().isEmpty());
		tm.addAction("test1");
		assert(!tm.getAllActions().isEmpty());
		assert(tm.getAllActions().contains("test1"));
		tm.addAction("test2");
		assertEquals(tm.getAllActions().size(), 2);
		assert(tm.getAllActions().contains("test2"));
		tm.removeAction("test2");
		assertEquals(tm.getAllActions().size(), 1);
		assert(!tm.getAllActions().contains("test2"));
		tm.removeAction("test1");
		assert(tm.getAllActions().isEmpty());
		assert(!tm.getAllActions().contains("test1"));
	}
}
