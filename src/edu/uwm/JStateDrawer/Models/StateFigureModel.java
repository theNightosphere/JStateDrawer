package edu.uwm.JStateDrawer.Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StateFigureModel {
	
	protected String myName;
	protected HashSet<TransitionModel> myIncomingTransitions, myOutgoingTransitions;
	protected HashMap<String, String> myActions;
	protected HashMap<String, TransitionModel> myTransitionTriggers;
	private ArrayList<StateFigureModel> myInternalStates;
	private Pattern p = Pattern.compile("[A-Z][A-Z0-9_]*+");
	
	
	public StateFigureModel()
	{
		this("default", new HashSet<TransitionModel>(), new HashSet<TransitionModel>(),
				new HashMap<String, String>(), new HashMap<String, TransitionModel>(),
				new ArrayList<StateFigureModel>());
	}	
	
	/**
	 * Explicit constructor for StateFigureModel that is initialized with the given name while all other
	 * parameters are set to default.
	 * @param name A String name for the StateFigureModel.
	 * @throws {@link IllegalArgumentException} if name is the empty string. 
	 */
	public StateFigureModel(String name)
	{
		this(name, new HashSet<TransitionModel>(), new HashSet<TransitionModel>(),
				new HashMap<String, String>(), new HashMap<String, TransitionModel>(),
				new ArrayList<StateFigureModel>());
	}
	
	/**
	 * Explicit Constructor for StateFigureModel.
	 * @param name A String of length 1 or greater.
	 * @param incomingTransitions A HashSet of {@link TransitionModel}s that are the incoming transitions to the StateFigureModel.
	 * @param outgoingTransitions A HashSet of {@link TransitionModel}s that are the outgoing transitions from the StateFigureModel.
	 * @param actions A HashMap<String, String> of Action Triggers to Actions.
	 * @param transitionTriggers A HashMap<String, {@link TransitionModel}> of transition triggers to {@link TransitionModel}s.
	 * @param internalStates An ArrayList of StateFigureModels that represent the internal states of this StateFigureModel.
	 * @throws {@link IllegalArgumentException} If incomingTransitions, outgoingTransitions, actions, transitionTriggers, or internalStates is null.
	 * @throws {@link IllegalArgumentException} If name is the empty string.
	 */
	public StateFigureModel(String name, HashSet<TransitionModel> incomingTransitions,
			HashSet<TransitionModel> outgoingTransitions, HashMap<String, String> actions,
			HashMap<String, TransitionModel> transitionTriggers,
			ArrayList<StateFigureModel> internalStates)
	{
		if(name.length() < 1)
		{
			throw new IllegalArgumentException("State name must be at least length 1");
		}
		myName = name;
		if(incomingTransitions == null)
		{
			throw new IllegalArgumentException("incomingTransitions must be non null.");
		}
		myIncomingTransitions = incomingTransitions;
		if(outgoingTransitions == null)
		{
			throw new IllegalArgumentException("outgoingTransitions must be non null.");
		}
		myOutgoingTransitions = outgoingTransitions;
		if(actions == null)
		{
			throw new IllegalArgumentException("actions must be non null.");
		}
		myActions = actions;
		if(transitionTriggers == null)
		{
			throw new IllegalArgumentException("transitionTriggers must be non null.");
		}
		myTransitionTriggers = transitionTriggers;
		if(internalStates == null)
		{
			throw new IllegalArgumentException("internalStates must be non null.");
		}
		myInternalStates = internalStates;
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
	 */
	public void addIncomingTransition(TransitionModel incomingTransition)
	{
		if (incomingTransition == null)
		{
			throw new IllegalArgumentException("Cannot add a null incoming transition");
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
	 */
	public void addOutgoingTransition(TransitionModel outgoingTransition)
	{
		if (outgoingTransition == null)
		{
			throw new IllegalArgumentException("Cannot add a null outgoing transition");
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
	public HashMap<String, String> getActions()
	{
		return myActions;
	}
	
	/**
	 * Adds a new Action to the State's list of actions.
	 * @param actionTriggerEvent A string representing the action trigger. This must be all uppercase letters, start with an uppercase letter, numbers and underscores.
	 * @param newAction A String representing an action.
	 * @throws {@link IllegalArgumentException} if the newAction string is not at least length 1.
	 * @throws {@link IllegalArgumentException} if the actionTriggerEvent string is not at least length 1.
	 * @throws {@link IllegalArgumentException} if the actionTriggerEvent contains any characters that are not uppercase letters, numbers, or underscores and does not start with an uppercase letter.
	 */
	public void addAction(String actionTriggerEvent, String newAction)
	{
		Matcher m = p.matcher(actionTriggerEvent);
		
		if (newAction.length() < 1)
		{
			throw new IllegalArgumentException("Actions must be a string of at least length 1");
		}
		else if(actionTriggerEvent.length() < 1)
		{
			throw new IllegalArgumentException("Action triggers must be a string of at least length 1");
		}
		else if(!m.matches())
		{
			String error = String.format("%s is not a valid event trigger. Event triggers must be composed of all capital letters with underscores.",
					actionTriggerEvent);
			throw new IllegalArgumentException(error);
		}
		
		myActions.put(actionTriggerEvent, newAction);
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
		String XMLString = String.format("<state name='%s'>", myName);
		for(String action : myActions.keySet())
		{
			XMLString += String.format("<action trigger='%s'>%s</action>",  action, myActions.get(action));
		}
		
		for(StateFigureModel sfm : myInternalStates)
		{
			XMLString += sfm.exportXML();
		}
		
		for(String trigger : myTransitionTriggers.keySet())
		{
			XMLString += myTransitionTriggers.get(trigger).exportXML();
		}
		
		XMLString += "</state>";
		
		return XMLString;
	}
	
	/**
	 * Returns the list of internal StateFigureModels
	 * @return An ArrayList of {@link StateFigureModel}s.
	 */
	public ArrayList<StateFigureModel> getInternalStates()
	{
		return myInternalStates;
	}
	
	/**
	 * Adds a new internal {@link StateFigureModel}. If the {@link StateFigureModel}
	 * is already an internal state, it is not added again.
	 * @param newInternalState The {@link StateFigureModel} that is being added to the list of internal states.
	 * @throws {@link IllegalArgumentException} if newInternalState is null.
	 */
	public void addInternalState(StateFigureModel newInternalState)
	{
		if (newInternalState == null)
		{
			throw new IllegalArgumentException("Cannot add a null internal state.");
		}
		else if (!myInternalStates.contains(newInternalState))
		{
			myInternalStates.add(newInternalState);
		}
	}
	
	/**
	 * Removes an internal {@link StateFigureModel}. If the internalStateToRemove is not in the list of
	 * internal states, nothing happens.
	 * @param internalStateToRemove The {@link StateFigureModel}  to remove from the list of internal states.
	 */
	public void removeInternalState(StateFigureModel internalStateToRemove)
	{
		myInternalStates.remove(internalStateToRemove);
	}
	
}