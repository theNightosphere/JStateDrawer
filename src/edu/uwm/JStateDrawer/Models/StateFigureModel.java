package edu.uwm.JStateDrawer.Models;

//TODO: Consider whether an exception should be throw when trying to add an action that already exists or it should just not be added.
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class StateFigureModel {
	
	protected String myName;
	protected HashSet<TransitionModel> myIncomingTransitions, myOutgoingTransitions;
	protected ArrayList<String> myActions;
	protected HashMap<String, TransitionModel> myTransitionTriggers;
	
	public StateFigureModel()
	{
		this("default", new HashSet<TransitionModel>(), new HashSet<TransitionModel>(),
				new ArrayList<String>(), new HashMap<String, TransitionModel>());
	}
	
	public StateFigureModel(String name, HashSet<TransitionModel> incomingTransitions,
			HashSet<TransitionModel> outgoingTransitions, ArrayList<String> actions,
			HashMap<String, TransitionModel> transitionTriggers)
	{
		myName = name;
		myIncomingTransitions = incomingTransitions;
		myOutgoingTransitions = outgoingTransitions;
		myActions = actions;
		myTransitionTriggers = transitionTriggers;
	}
	
	/**
	 * 
	 * @return The String representation of the state's name.
	 */
	public String getName()
	{
		return myName;
	}
	
	/**
	 * 
	 * @param newName A String to be used as the new name for the state.
	 * @throws Exception Thrown when the length of the newName variable is less than one. In other words, when newName is the empty string.
	 */
	public void setName(String newName) throws Exception
	{
		if(newName.length() < 1)
		{
			throw new IllegalArgumentException("The new name is not long enough");
		}
		
		myName = newName;
	}
	
	/**
	 * 
	 * @return Returns the HashSet containing the state's incoming transitions.
	 */
	public HashSet<TransitionModel> getIncomingTransitions()
	{
		return myIncomingTransitions;
	}
	
	/**
	 * 
	 * @return Returns the HashSet containing the state's outgoing transitions.
	 */
	public HashSet<TransitionModel> getOutgoingTransitions()
	{
		return myOutgoingTransitions;
	}
	
	/**
	 * Adds an Incoming {@link TransitionModel} to this StateModel.
	 * @param incomingTransition A {@link TransitionModel} that represents an incoming transition.
	 * @throws Throws an illegal argument exception if the incoming transition is null.
	 * @throws Throws an illegal argument exception if the incoming transition is already an outgoing transition.
	 */
	public void addIncomingTransition(TransitionModel incomingTransition)
	{
		if (incomingTransition == null)
		{
			throw new IllegalArgumentException("Cannot add a null incoming transition");
		}
		else if(myOutgoingTransitions.contains(incomingTransition))
		{
			throw new IllegalArgumentException("Cannot add an incoming transition that is already an outgoing transition to this State.");
		}
		
		myIncomingTransitions.add(incomingTransition);
	}
	
	/**
	 * Adds an outgoing {@link TransitionModel} to this StateModel.
	 * @param outgoingTransition A {@link TransitionModel} that represents an outgoing transition.
	 * @throws Throws an illegal argument exception if the outgoing transition is null.
	 * @throws Throws an illegal argument exception if the outgoing transition is already an incoming transition. 
	 */
	public void addOutgoingTransition(TransitionModel outgoingTransition)
	{
		if (outgoingTransition == null)
		{
			throw new IllegalArgumentException("Cannot add a null outgoing transition");
		}
		else if (myIncomingTransitions.contains(outgoingTransition))
		{
			throw new IllegalArgumentException("Cannot add an outgoing transition that is already an incoming transition to this state.");
		}
		
		myOutgoingTransitions.add(outgoingTransition);
	}
	
	/**
	 * Accesses a list of the state's actions.
	 * @return An ArrayList<String> of the actions this state performs upon entry.
	 */
	public ArrayList<String> getActions()
	{
		return myActions;
	}
	
	/**
	 * Adds a new Action to the State's list of actions.
	 * @param newAction A String representing an action.
	 * @throws Throws an illegal argument exception if the newAction string is not at least length 1.
	 */
	public void addAction(String newAction)
	{
		if (newAction.length() < 1)
		{
			throw new IllegalArgumentException("Actions must be a string of at least length 1");
		}
		
		if(!myActions.contains(newAction))
		{
			myActions.add(newAction);
		}
	}
	
	/**
	 * Accesses a Set of all the events that trigger transitions from the current state.
	 * @return Returns a Set<String> of all the events that trigger a transition from this State.
	 */
	public Set<String> getTransitionTriggers()
	{
		return myTransitionTriggers.keySet();
	}
	
}
