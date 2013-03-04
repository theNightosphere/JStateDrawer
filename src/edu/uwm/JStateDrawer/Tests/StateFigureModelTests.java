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
		ArrayList<String> actions = new ArrayList<String>();
		actions.add("Banana");
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
	 * Test that trying to add an incoming transition which is also an outgoing transition
	 * in the current state is not allowed. Essentially attempting to self-loop is not allowed currently.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testIncomingTransitionCannotAlreadyBeOutgoingFromSameState()
	{
		TransitionModel t = new TransitionModel();
		sm.addOutgoingTransition(t);
		sm.addIncomingTransition(t);
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
		sm.removeIncomingTransition(new TransitionModel("test", new StateFigureModel(),
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
	 * Tests that an outgoing transition is not already an incoming transition to the same state.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testOutgoingTransitionCannotAlreadyBeIncomingToState()
	{
		TransitionModel t = new TransitionModel();
		sm.addIncomingTransition(t);
		sm.addOutgoingTransition(t);
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
		sm.removeOutgoingTransition(new TransitionModel("test", new StateFigureModel(),
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
		sm.addAction("");
	}
	
	/**
	 * Tests that an action which a state already has cannot be added twice.
	 */
	@Test
	public void testCannotAddActionThatAlreadyExists()
	{
		assertEquals(sm.getActions().size(), 0);
		sm.addAction("test");
		assertEquals(sm.getActions().size(), 1);
		sm.addAction("test");
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
		sm.addAction("test");
		assertEquals(sm.getActions().size(), 1);
		assert(sm.getActions().contains("test"));
	}
	
	/**
	 * Tests that {@code removeAction}, does nothing when attempting to remove an action
	 * that does not exist.
	 */
	@Test
	public void testRemovingNonExistantActionsHasNoEffect()
	{
		sm.addAction("test");
		assertEquals(sm.getActions().size(), 1);
		sm.removeAction("not test");
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
		sm.addAction("test");
		assertEquals(sm.getActions().size(), 1);
		sm.removeAction("test");
		assert(sm.getActions().isEmpty());
	}
	
	/**
	 * Tests that attempting to add two transitions with the same trigger
	 */
	@Test
	public void testCannotAddTwoTransitionsWithSameTrigger()
	{
		assert(sm.getTransitionTriggers().isEmpty());
		sm.addTransition("test", new TransitionModel());
		assertEquals(sm.getTransitionTriggers().size(), 1);
		sm.addTransition("test", new TransitionModel("not default", new StateFigureModel(),
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
		TransitionModel t = new TransitionModel("test", new StateFigureModel(), new StateFigureModel());
		sm.addTransition("test", t);
		assertEquals(sm.getTransitionTriggers().size(), 1);
		sm.removeTransitionAndTrigger("not test");
		assertEquals(sm.getTransitionTriggers().size(), 1);
	}
	
	/**
	 * Tests that {@code removeTransitionAndTrigger} works properly when removing a transition
	 * that exists.
	 */
	@Test
	public void testRemoveTriggerAndTransitionWorksProperly()
	{
		TransitionModel t = new TransitionModel("test", new StateFigureModel(), new StateFigureModel());
		assert(sm.getTransitionTriggers().isEmpty());
		sm.addTransition("test", t);
		assertEquals(sm.getTransitionTriggers().size(), 1);
		sm.removeTransitionAndTrigger("test");
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
		sm.addAction("action1");
		sm.addAction("action2");
		sm.addAction("action3");
		assertEquals(sm.exportXML(), "<state name=default><action>action1</action><action>action2</action><action>action3</action></state>");
		
		sm.addTransition("transition1", new TransitionModel("transition1", 
				new StateFigureModel(), new StateFigureModel()));
		sm.addTransition("transition2", new TransitionModel("transition2",
				new StateFigureModel(), new StateFigureModel()));
		assertEquals(sm.exportXML(), "<state name=default><action>action1</action><action>action2</action><action>action3</action><transition trigger=\"transition1\"/><transition trigger=\"transition2\"/></state>");	
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
}
