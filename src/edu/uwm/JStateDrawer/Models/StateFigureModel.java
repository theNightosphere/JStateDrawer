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
	public void setName(String newName)
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
	 * Removes an incoming transition. If the state does not have the transition being removed,
	 * nothing happens.
	 * @param incomingTransition The {@link TransitionModel} to remove. 
	 */
	public void removeIncomingTransition(TransitionModel incomingTransition)
	{
		myIncomingTransitions.remove(incomingTransition);
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
	 * Removes an outgoing transition. If the state does not have the transition being removed,
	 * nothing happens.
	 * @param outgoingTransition The {@link TransitionModel} to remove. 
	 */
	public void removeOutgoingTransition(TransitionModel outgoingTransition)
	{
		myOutgoingTransitions.remove(outgoingTransition);
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
	 * Removes an action. If the state does not have the action being removed,
	 * nothing happens.
	 * @param actionToRemove A String representing the action to be removed. 
	 */
	public void removeAction(String actionToRemove)
	{
		myActions.remove(actionToRemove);
	}
	
	/**
	 * Accesses a Set of all the events that trigger transitions from the current state.
	 * @return Returns a Set<String> of all the events that trigger a transition from this State.
	 */
	public Set<String> getTransitionTriggers()
	{
		return myTransitionTriggers.keySet();
	}
	
	/**
	 * Takes an event string and returns its associated Transition.
	 * @param triggerEvent A String that triggers a transition.
	 * @return A {@link TransitionModel} that is triggered by the triggerEvent.
	 */
	public TransitionModel getTransitionByEvent(String triggerEvent)
	{
		return myTransitionTriggers.get(triggerEvent);
	}
	
	/**
	 * Takes an event string and a transition and adds them to the State's HashMap of Transitions.
	 * Nothing is added if the State already has a transition that is triggered by the string triggerEvent.
	 * @param triggerEvent A string that triggers a transition.
	 * @param t The {@link TransitionModel} that is triggered by the triggerEvent String.
	 */
	public void addTransition(String triggerEvent, TransitionModel t)
	{
		if (!myTransitionTriggers.containsKey(triggerEvent))
		{
			myTransitionTriggers.put(triggerEvent, t);
		}
	}
	
	/**
	 * Removes a transition. If the state does not have the transition being removed,
	 * nothing happens.
	 * @param triggerOfTransitionToRemove The String representing the trigger of the {@link TransitionModel} to be removed.
	 */
	public void removeTransitionAndTrigger(String triggerOfTransitionToRemove)
	{
		myTransitionTriggers.remove(triggerOfTransitionToRemove);
	}
	
	/**
	 * Creates a String representation of the StateFigureModel that can be used to
	 * generate the StateFigureModel again.
	 * @return A String representation of the StateFigureModel.
	 */
	public String exportXML()
	{
		String XMLString = String.format("<state name=%s>", myName);
		for(String action : myActions)
		{
			XMLString += String.format("<action>%s</action>", action);
		}
		
		for(String trigger : myTransitionTriggers.keySet())
		{
			XMLString += myTransitionTriggers.get(trigger).exportXML();
		}
		
		XMLString += "</state>";
		
		return XMLString;
	}
	
}
