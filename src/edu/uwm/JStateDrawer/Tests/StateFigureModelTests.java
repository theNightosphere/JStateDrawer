package edu.uwm.JStateDrawer.Tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import edu.uwm.JStateDrawer.Models.EndStateModel;
import edu.uwm.JStateDrawer.Models.StartStateModel;
import edu.uwm.JStateDrawer.Models.StateFigureModel;
import edu.uwm.JStateDrawer.Models.TransitionModel;

public class StateFigureModelTests {

	private StateFigureModel sm;
	
	@Before
	public void initDefaultStateFigure()
	{
		sm = new StateFigureModel();
	}
	
	/**
	 * Tests the default constructor.
	 */
	@Test
	public void testDefaultConstructor() {
		assertEquals(sm.getName(), "default");
		assert(sm.getIncomingTransitions().isEmpty());
		assert(sm.getOutgoingTransitions().isEmpty());
		assert(sm.getActions().isEmpty());
		assert(sm.getTransitionTriggers().isEmpty());
		assert(sm.getIncomingTransitions().isEmpty());
	}
	
	/**
	 * Test the constructor when passed values.
	 */
	@Test
	public void testNonDefaultConstructor()
	{
		HashSet<TransitionModel> incoming = new HashSet<TransitionModel>();
		HashSet<TransitionModel> outgoing = new HashSet<TransitionModel>();
		incoming.add(new TransitionModel());
		outgoing.add(new TransitionModel());
		HashMap<String, String> actions = new HashMap<String, String>();
		actions.put("TRIGGER_BANANA","Banana");
		HashMap<String, TransitionModel> triggers = new HashMap<String, TransitionModel>();
		triggers.put("test trigger", new TransitionModel());
		ArrayList<StateFigureModel> internalStates = new ArrayList<StateFigureModel>();
		internalStates.add(new StateFigureModel());
		
		StateFigureModel sm = new StateFigureModel("Awesome",incoming, outgoing, actions,
				triggers, internalStates);
		
		assertEquals(sm.getName(), "Awesome");
		assert(!sm.getIncomingTransitions().isEmpty());
		assert(!sm.getOutgoingTransitions().isEmpty());
		assert(!sm.getActions().isEmpty());
		assert(!sm.getTransitionTriggers().isEmpty());
		assert(!sm.getInternalStates().isEmpty());
	}
	
	/**
	 * Tests that the explicit constructor throws an error when passed null as any
	 * collection parameter or if the name is the empty string.
	 */
	@Test
	public void testConstructorThrowsExceptionOnAllErrors()
	{
		HashSet<TransitionModel> incoming = new HashSet<TransitionModel>();
		HashSet<TransitionModel> outgoing = new HashSet<TransitionModel>();
		incoming.add(new TransitionModel());
		outgoing.add(new TransitionModel());
		HashMap<String, String> actions = new HashMap<String, String>();
		actions.put("TRIGGER_BANANA","Banana");
		HashMap<String, TransitionModel> triggers = new HashMap<String, TransitionModel>();
		triggers.put("test trigger", new TransitionModel());
		ArrayList<StateFigureModel> internalStates = new ArrayList<StateFigureModel>();
		internalStates.add(new StateFigureModel());
		
		try
		{
			StateFigureModel badName = new StateFigureModel("", incoming, outgoing, actions,
					triggers, internalStates);
			fail("Constructor did not throw exception when passed empty string for name value in constructor");
		}
		catch(Exception e)
		{
			// Success!
		}
		try
		{
			StateFigureModel nullIncomingTransitions = new StateFigureModel("Good", null,
					outgoing, actions, triggers, internalStates);
			fail("Constructor did not throw exception when passed a null HashSet for incoming transitions");
		}
		catch(Exception e)
		{
			// Success!
		}
		try
		{
			StateFigureModel nullOutgoingTransitions = new StateFigureModel("Good", incoming,
					null, actions, triggers, internalStates);
			fail("Constructor did not throw exception when passed a null HashSet for outgoing transitions");
		}
		catch(Exception e)
		{
			// Success!
		}
		try
		{
			StateFigureModel nullActions = new StateFigureModel("Good", incoming, outgoing,
					null, triggers, internalStates);
			fail("Constructor did not throw exception when passed a null HashMap for actions");
		}
		catch(Exception e)
		{
			// Success!
		}
		try
		{
			StateFigureModel nullTriggers = new StateFigureModel("Good", incoming, outgoing,
					actions, null, internalStates);
		}
		catch(Exception e)
		{
			// Success!
		}
		try
		{
			StateFigureModel nullInternalStates = new StateFigureModel("Good", incoming, outgoing,
					actions, triggers, null);
		}
		catch(Exception e)
		{
			// Success!
		}
		// All succeeded!
	}
	
	/**
	 * Test that state's name cannot be set to the empty string.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetTooShortNameThrowsException()
	{
		sm.setName("");
	}
	
	/**
	 * Test that when passed an allowable name (currently just non-empty string),
	 * the name is properly changed.
	 */
	@Test
	public void testSetNameProperlyChangesName()
	{
		assertEquals(sm.getName(), "default");
		sm.setName("not default");
		assertEquals(sm.getName(), "not default");
	}
	
	/**
	 * Tests that trying to add a null incoming transition properly throws an exception.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddingNullIncomingTransitionThrowsException()
	{
		sm.addIncomingTransition(null);
	}
	
	/**
	 * Tests whether or not {@code addIncomingTransition} is properly updating a 
	 * state's list of incoming transitions.
	 */
	@Test
	public void testIncomingTransitionIsProperlyAdded()
	{
		TransitionModel t = new TransitionModel();
		sm.addIncomingTransition(t);
		assert(sm.getIncomingTransitions().contains(t));
	}
	
	/**
	 * Tests whether or not attempting to remove an incoming transition that does not
	 * exist does nothing (like it should).
	 */
	@Test
	public void testRemovingNonExistantIncomingTransitionHasNoEffect()
	{
		sm.addIncomingTransition(new TransitionModel());
		assertEquals(sm.getIncomingTransitions().size(), 1);
		sm.removeIncomingTransition(new TransitionModel("TEST", new StateFigureModel(),
				new StateFigureModel()));
		assertEquals(sm.getIncomingTransitions().size(), 1);
	}
	
	/**
	 * Tests that removing an existing incoming transition works properly.
	 */
	@Test
	public void testRemoveIncomingTransitionsWorksProperly()
	{
		TransitionModel t = new TransitionModel();
		assert(sm.getIncomingTransitions().isEmpty());
		sm.addIncomingTransition(t);
		assertEquals(sm.getIncomingTransitions().size(), 1);
		sm.removeIncomingTransition(t);
		assert(sm.getIncomingTransitions().isEmpty());
	}
	
	/**
	 * Tests that an exception is thrown when attemping to add an outgoing transition
	 * that is null.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddedOutgoingTransitionCannotBeNull()
	{
		sm.addOutgoingTransition(null);
	}
	
	/**
	 * Tests that {@code testOutgoingTransition} properly updates a state's list of
	 * outgoing transitions.
	 */
	@Test
	public void testOutgoingTransitionIsProperlyAdded()
	{
		TransitionModel t= new TransitionModel();
		sm.addOutgoingTransition(t);
		assert(sm.getOutgoingTransitions().contains(t));
	}
	
	/**
	 * Tests that removing a non-existent outgoing transition has no effect.
	 */
	@Test
	public void testRemovingNonExistantOutgoingTransitionHasNoEffect()
	{
		sm.addOutgoingTransition(new TransitionModel());
		assertEquals(sm.getOutgoingTransitions().size(), 1);
		sm.removeOutgoingTransition(new TransitionModel("TEST", new StateFigureModel(),
				new StateFigureModel()));
		assertEquals(sm.getOutgoingTransitions().size(), 1);
	}
	
	/**
	 * Tests that {@code removeOutgoingTransition} works properly.
	 */
	@Test
	public void testRemoveOutgoingTransitionsWorksProperly()
	{
		TransitionModel t = new TransitionModel();
		assert(sm.getOutgoingTransitions().isEmpty());
		sm.addOutgoingTransition(t);
		assertEquals(sm.getOutgoingTransitions().size(), 1);
		sm.removeOutgoingTransition(t);
		assert(sm.getOutgoingTransitions().isEmpty());
	}
	
	/**
	 * Tests that an exception is thrown when an action is added that is the empty string.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCannotAddEmptyStringAsAction()
	{
		sm.addAction("BAD_ACTION","");
	}
	
	/**
	 * Tests that an exception is thrown when an trigger action has whitespace.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testActionTriggerCannotHaveWhitespace()
	{
		sm.addAction("BAD ACTION", "Not good");
	}
	
	/**
	 * Tests that an exception is thrown when a trigger action has a non-alphanumeric 
	 * character or non-underscore character.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testActionTriggerCannotHaveNonAlphanumericOrUnderscore()
	{
		sm.addAction("BAD\\/A#CTION", "Not good");
	}
	
	/**
	 * Tests that an exception is thrown when lowercase letters are used in the action trigger.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testActionTriggerCannotBeLowercase()
	{
		sm.addAction("Bad_action", "Not good");
	}
	
	/**
	 * Tests that an action trigger cannot have a number, underscore, lowercase letter or space
	 * as the first character in a trigger string.
	 */
	@Test
	public void testActionTriggerFirstLetterNotCorrectThrowsException()
	{
		try
		{
			sm.addAction("0OTHERWISE_OKAY", "not great");
			fail("Didn't throw exception when starting with number");
		}
		catch(Exception e)
		{
			// Success!
		}
		try
		{
			sm.addAction("aOTHERWISE_OKAY", "not great");
			fail("Didn't throw exception when starting with lowercase letter.");
		}
		catch(Exception e)
		{
			// Success!
		}
		try
		{
			sm.addAction(" OTHERWISE_OKAY", "not great");
			fail("Didn't throw exception when starting with whitespace");
		}
		catch(Exception e)
		{
			// Success!
		}
	}
	
	
	/**
	 * Tests that an action trigger which a state already has cannot be added twice.
	 * The action trigger is the key part of the myActions HashMap.
	 */
	@Test
	public void testCannotAddActionTriggerThatAlreadyExists()
	{
		assertEquals(sm.getActions().size(), 0);
		sm.addAction("TEST","test");
		assertEquals(sm.getActions().size(), 1);
		sm.addAction("TEST","test");
		// Assert that the test action was not added twice.
		assertEquals(sm.getActions().size(), 1);
	}
	
	/**
	 * Tests that {@code addAction} works properly.
	 */
	@Test
	public void testActionsAreAddedProperly()
	{
		assert(sm.getActions().isEmpty());
		sm.addAction("TEST_CASE","test");
		assertEquals(sm.getActions().size(), 1);
		assert(sm.getActions().containsKey("TEST_CASE"));
		// Next assertion ensures that the key and value are not being switched in 
		// addAction(String, String).
		assert(sm.getActions().containsKey("test"));
	}
	
	/**
	 * Tests that {@code removeAction}, does nothing when attempting to remove an action trigger
	 * that does not exist.
	 */
	@Test
	public void testRemovingNonExistantActionTriggerHasNoEffect()
	{
		sm.addAction("TEST_CASE","test");
		assertEquals(sm.getActions().size(), 1);
		sm.removeAction("NOT_TEST_CASE");
		assertEquals(sm.getActions().size(), 1);
	}
	
	/**
	 * Tests that {@code removeAction} properly updates a state's list of actions 
	 * when removing an existing action.
	 */
	@Test
	public void testRemoveActionsWorksProperly()
	{
		assert(sm.getActions().isEmpty());
		sm.addAction("TEST_CASE","test");
		assertEquals(sm.getActions().size(), 1);
		sm.removeAction("TEST_CASE");
		assert(sm.getActions().isEmpty());
	}
	
	/**
	 * Tests that attempting to add two transitions with the same trigger
	 */
	@Test
	public void testCannotAddTwoTransitionsWithSameTrigger()
	{
		assert(sm.getTransitionTriggers().isEmpty());
		sm.addTransition("TEST", new TransitionModel());
		assertEquals(sm.getTransitionTriggers().size(), 1);
		sm.addTransition("TEST", new TransitionModel("NOT_DEFAULT", new StateFigureModel(),
				new StateFigureModel()));
		assertEquals(sm.getTransitionTriggers().size(), 1);
	}
	
	/**
	 * Tests that attempting to add the same transition with the same trigger twice does not
	 * cause the second transition/trigger combo to be added.
	 */
	@Test
	public void testCannotAddSameTransitionAndTriggerTwice()
	{
		assert(sm.getTransitionTriggers().isEmpty());
		TransitionModel t = new TransitionModel();
		sm.addTransition("test", t);
		assertEquals(sm.getTransitionTriggers().size(), 1);
		sm.addTransition("test", t);
		assertEquals(sm.getTransitionTriggers().size(), 1);
	}
	
	/**
	 * Tests that {@code removeTransitionAndTrigger} has no effect when the transition does not exist.
	 */
	@Test
	public void testRemovingNonExistantTriggerHasNoEffect()
	{
		TransitionModel t = new TransitionModel("TEST", new StateFigureModel(), new StateFigureModel());
		sm.addTransition("TEST", t);
		assertEquals(sm.getTransitionTriggers().size(), 1);
		sm.removeTransitionAndTrigger("NOT_TEST");
		assertEquals(sm.getTransitionTriggers().size(), 1);
	}
	
	/**
	 * Tests that {@code removeTransitionAndTrigger} works properly when removing a transition
	 * that exists.
	 */
	@Test
	public void testRemoveTriggerAndTransitionWorksProperly()
	{
		TransitionModel t = new TransitionModel("TEST", new StateFigureModel(), new StateFigureModel());
		assert(sm.getTransitionTriggers().isEmpty());
		sm.addTransition("TEST", t);
		assertEquals(sm.getTransitionTriggers().size(), 1);
		sm.removeTransitionAndTrigger("TEST");
		assert(sm.getTransitionTriggers().isEmpty());
	}
	
	/**
	 * Tests the start state to ensure it is not allowing incoming transitions
	 */
	@Test
	public void testStartStateTransition(){
		StateFigureModel sn = new StartStateModel();
		assert(sn.getIncomingTransitions().isEmpty());
		sn.addIncomingTransition(new TransitionModel());
		assert(sn.getIncomingTransitions().isEmpty());
	}

	/**
	 * Tests the end state to ensure it is not allowing outgoing transitions
	 */
	@Test
	public void testEndStateTransition(){
		StateFigureModel em = new EndStateModel();
		assert(em.getOutgoingTransitions().isEmpty());
		em.addOutgoingTransition(new TransitionModel());
		assert(em.getOutgoingTransitions().isEmpty());
	}
	
	/**
	 * Tests the exporting of XML.
	 */
	@Test
	public void testExportXML()
	{
		sm.addAction("ACTION_TRIGGER1","action1");
		sm.addAction("ACTION_TRIGGER2","action2");
		sm.addAction("ACTION_TRIGGER3","action3");
	
		assertEquals(sm.exportXML(),
				"<state name='default'><action trigger='ACTION_TRIGGER3'>action3</action><action trigger='ACTION_TRIGGER2'>action2</action><action trigger='ACTION_TRIGGER1'>action1</action></state>");
		
		sm.addTransition("TRANSITION1", new TransitionModel("TRANSITION1", 
				new StateFigureModel(), new StateFigureModel()));
		sm.addTransition("TRANSITION2", new TransitionModel("TRANSITION2",
				new StateFigureModel(), new StateFigureModel()));
		
		assertEquals(sm.exportXML(),
				"<state name='default'><action trigger='ACTION_TRIGGER3'>action3</action><action trigger='ACTION_TRIGGER2'>action2</action><action trigger='ACTION_TRIGGER1'>action1</action><transition trigger='TRANSITION1' target='default'/><transition trigger='TRANSITION2' target='default'/></state>");
		
		sm.addInternalState(new StateFigureModel("innerState1"));
		sm.addInternalState(new StateFigureModel("innerState2"));
		assertEquals(sm.exportXML(),
				"<state name='default'><action trigger='ACTION_TRIGGER3'>action3</action><action trigger='ACTION_TRIGGER2'>action2</action><action trigger='ACTION_TRIGGER1'>action1</action><state name='innerState1'></state><state name='innerState2'></state><transition trigger='TRANSITION1' target='default'/><transition trigger='TRANSITION2' target='default'/></state>");
	}
	
	@Test
	public void testAddInternalStatesWorksProperly()
	{
		StateFigureModel test1 = new StateFigureModel();
		test1.setName("test1");
		StateFigureModel test2 = new StateFigureModel();
		test2.setName("test2");
		assert(sm.getInternalStates().isEmpty());
		
		sm.addInternalState(test1);
		assert(!sm.getInternalStates().isEmpty());
		assert(sm.getInternalStates().contains(test1));
		
		sm.addInternalState(test2);
		assert(!sm.getInternalStates().isEmpty());
		assert(sm.getInternalStates().contains(test2));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testAddingNullInternalStateThrowsException()
	{
		sm.addInternalState(null);
	}
	
	@Test
	public void testRemoveInternalStatesWorksProperly()
	{
		StateFigureModel test1 = new StateFigureModel();
		test1.setName("test1");
		StateFigureModel test2 = new StateFigureModel();
		test2.setName("test2");
		assert(sm.getInternalStates().isEmpty());
		
		sm.addInternalState(test1);
		assert(!sm.getInternalStates().isEmpty());
		assert(sm.getInternalStates().contains(test1));
		
		sm.addInternalState(test2);
		assert(!sm.getInternalStates().isEmpty());
		assert(sm.getInternalStates().contains(test2));
		
		sm.removeInternalState(test2);
		assert(!sm.getInternalStates().isEmpty());
		assert(!sm.getInternalStates().contains(test2));
		
		sm.removeInternalState(test1);
		assert(sm.getInternalStates().isEmpty());
		assert(!sm.getInternalStates().contains(test1));
	}
	
	@Test
	public void testRemoveNonExistentInternalStatesDoesNothing()
	{
		StateFigureModel test1 = new StateFigureModel();
		test1.setName("test1");
		StateFigureModel test2 = new StateFigureModel();
		test2.setName("test2");
		assert(sm.getInternalStates().isEmpty());
		
		sm.addInternalState(test1);
		assert(!sm.getInternalStates().isEmpty());
		assert(sm.getInternalStates().contains(test1));
		
		
		sm.removeInternalState(test2);
		// Nothing should have happened.
		assert(!sm.getInternalStates().isEmpty());
		assert(sm.getInternalStates().contains(test2));
		
	}
	
	@Test
	public void testNameConstructors(){
		sm = new StateFigureModel("yay");
		assertEquals(sm.getName(), "yay");
		sm = new StartStateModel();
		assertEquals(sm.getName(), "Start");
		sm = new EndStateModel();
		assertEquals(sm.getName(), "End");
		
		
	}
}
