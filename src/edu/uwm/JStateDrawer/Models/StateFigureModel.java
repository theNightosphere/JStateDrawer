package edu.uwm.JStateDrawer.Models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;

public class StateFigureModel {
	
	public final String DEFAULT_ACTION_TRIGGER = "EVENT";
	public final String DEFAULT_ACTION_NAME = "action";
	protected String myName;
	protected HashSet<TransitionModel> myIncomingTransitions, myOutgoingTransitions;
	protected HashMap<String, List<String>> myActions;
	protected HashMap<String, TransitionModel> myTransitionEvents;
	private ArrayList<StateFigureModel> myInternalStates;
	private Pattern p = Pattern.compile("[A-Z][A-Z0-9_]*+");
	
	public StateFigureModel()
	{
		this("default", new HashSet<TransitionModel>(), new HashSet<TransitionModel>(),
				new HashMap<String, List<String>>(), new HashMap<String, TransitionModel>(),
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
				new HashMap<String, List<String>>(), new HashMap<String, TransitionModel>(),
				new ArrayList<StateFigureModel>());
	}
	
	/**
	 * Explicit Constructor for StateFigureModel.
	 * @param name A String of length 1 or greater.
	 * @param incomingTransitions A HashSet of {@link TransitionModel}s that are the incoming transitions to the StateFigureModel.
	 * @param outgoingTransitions A HashSet of {@link TransitionModel}s that are the outgoing transitions from the StateFigureModel.
	 * @param actions A HashMap<String, List<String>> of Action Triggers to Actions.
	 * @param transitionTriggers A HashMap<String, {@link TransitionModel}> of transition triggers to {@link TransitionModel}s.
	 * @param internalStates An ArrayList of StateFigureModels that represent the internal states of this StateFigureModel.
	 * @throws {@link IllegalArgumentException} If incomingTransitions, outgoingTransitions, actions, transitionTriggers, or internalStates is null.
	 * @throws {@link IllegalArgumentException} If name is the empty string.
	 */
	public StateFigureModel(String name, HashSet<TransitionModel> incomingTransitions,
			HashSet<TransitionModel> outgoingTransitions, HashMap<String, List<String>> actions,
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
		myTransitionEvents = transitionTriggers;
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
	 * Adds an outgoing {@link TransitionModel} to this StateModel.If successful, the Transition and its trigger
	 * are added to list of transition triggers.
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
		myTransitionEvents.put(outgoingTransition.getEvent(), outgoingTransition);
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
	 * @return A {@code HashMap<String, List<String>>} of all the actions and their associated triggers held by a state.
	 */
	public HashMap<String, List<String>> getAllActions()
	{
		return myActions;
	}
	
	/**
	 * Gets a list of actions prompted by a specific trigger.
	 * @param event
	 * @return A list of strings associated with the trigger passed as a parameter. Null is returned if no such trigger exists.
	 */
	public List<String> getActionsByEvent(String event)
	{
		if(myActions.containsKey(event))
		{
			return myActions.get(event);
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Adds a new Action to the State's list of actions. If the trigger already exists, the action is added to the trigger's
	 * list of actions.
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
		
		// If the trigger already exists in map of triggers, add the action to the trigger's list of actions.
		if(myActions.containsKey(actionTriggerEvent))
		{
			List<String> existingTriggersActions = myActions.get(actionTriggerEvent);
			existingTriggersActions.add(newAction);
		}
		else{
			ArrayList<String> newListOfActions = new ArrayList<String>();
			newListOfActions.add(newAction);
			myActions.put(actionTriggerEvent, newListOfActions);
		}
	}
	
	/**
	 * Adds a list of new Actions to the State's list of actions. If the trigger already exists, the actions are added to the trigger's
	 * list of actions. A convenience method that is, in reality, multiple calls to {@link addAction}
	 * @param actionTriggerEvent A string representing the action trigger. This must be all uppercase letters, start with an uppercase letter, numbers and underscores.
	 * @param listOfActions A List<String> representing multiple actions
	 * @throws {@link IllegalArgumentException} if any of the new action strings are not at least length 1.
	 * @throws {@link IllegalArgumentException} if listOfActions is null
	 * @throws {@link IllegalArgumentException} if the actionTriggerEvent string is not at least length 1.
	 * @throws {@link IllegalArgumentException} if the actionTriggerEvent contains any characters that are not uppercase letters, numbers, or underscores and does not start with an uppercase letter.
	 */
	public void addActionsForEvent(String actionTriggerEvent, List<String> listOfActions)
	{
		if(listOfActions != null)
		{
			for(String action : listOfActions)
			{
				addAction(actionTriggerEvent, action);
			}
		}
	}
	
	/**
	 * Removes an action. If the state does not have the action being removed, false is returned.
	 * If removing an action leaves a trigger with no associated actions, the trigger is also removed
	 * from the map of triggers to actions.
	 * @param eventOfAction A string representing the trigger of the action to be removed.
	 * @param actionToRemove A String representing the action to be removed.
	 * @return Returns true if the action is successfully removed, false if it is not. 
	 */
	public boolean removeAction(String eventOfAction, String actionToRemove )
	{
		if(myActions.containsKey(eventOfAction))
		{
			List<String> listOfActions = myActions.get(eventOfAction);
			if(listOfActions.contains(actionToRemove))
			{
				listOfActions.remove(actionToRemove);
				if(listOfActions.isEmpty())
				{
					myActions.remove(eventOfAction);
				}
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Removes all the actions associated with a particular trigger. A convenience method.
	 * @param eventOfActions A String trigger that has one or more actions associated with it.
	 * @return true if trigger and its actions are removed successfully, false if there is no trigger to remove.
	 */
	public boolean removeAllActionsOfEvent(String eventOfActions)
	{
		if(myActions.containsKey(eventOfActions))
		{
			myActions.remove(eventOfActions);
			return true;
		}
		return false;
	}
	
	/**
	 * Accesses a Set of all the events that trigger transitions from the current state.
	 * @return Returns a Set<String> of all the events that trigger a transition from this State.
	 */
	public Set<String> getTransitionEvents()
	{
		return myTransitionEvents.keySet();
	}
	
	/**
	 * Takes an event string and returns its associated Transition.
	 * @param triggerEvent A String that triggers a transition.
	 * @return A {@link TransitionModel} that is triggered by the triggerEvent.
	 */
	public TransitionModel getTransitionByEvent(String triggerEvent)
	{
		return myTransitionEvents.get(triggerEvent);
	}
	
	/**
	 * Takes an event string and a transition and adds them to the State's HashMap of Transitions.
	 * Nothing is added if the State already has a transition that is triggered by the string triggerEvent.
	 * If addTransition is successful, the transition model is also added to the list of outgoing transitions.
	 * @param triggerEvent A string that triggers a transition.
	 * @param t The {@link TransitionModel} that is triggered by the triggerEvent String.
	 */
	public void addTransition(String triggerEvent, TransitionModel t)
	{
		if (!myTransitionEvents.containsKey(triggerEvent))
		{
			myTransitionEvents.put(triggerEvent, t);
			myOutgoingTransitions.add(t);
		}
	}
	
	/**
	 * Removes a transition. If the state does not have the transition being removed,
	 * nothing happens.
	 * @param triggerOfTransitionToRemove The String representing the trigger of the {@link TransitionModel} to be removed.
	 */
	public void removeTransitionAndEvent(String triggerOfTransitionToRemove)
	{
		myTransitionEvents.remove(triggerOfTransitionToRemove);
	}
	
	/**
	 * Updates the {@link TransitionModel} pointed to by oldTrigger to have the 
	 * trigger string specified by newTrigger
	 * @param oldEvent A string that triggers the associated {@link TransitionModel}
	 * @param newEvent The new desired trigger string for the {@link TransitionModel} associated with oldTrigger.
	 * @param transitionToUpdate TODO
	 * @return true if trigger is successfully updated, false if it is not.
	 */
	public boolean changeTransitionEvent(String oldEvent,
			String newEvent, TransitionModel transitionToUpdate)
	{
		if(validateEvent(newEvent))
		{
			if(myTransitionEvents.containsKey(oldEvent))
			{
				TransitionModel transToUpdate = myTransitionEvents.remove(oldEvent);

				myTransitionEvents.put(newEvent, transitionToUpdate);
				return true;
			}
			// If the old value is not in the list of transition triggers, add it.
			// Consider it like 'put'
			else if(!myTransitionEvents.containsValue(oldEvent))
			{
				myTransitionEvents.put(newEvent, transitionToUpdate);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Ensures that the Trigger for the transition is of the proper format.
	 * Namely, the trigger starts with an upper case letter and is followed
	 * by only upper case letters, numbers, or underscores.
	 * @param eventToValidate String trigger to validate.
	 * @return true if trigger string is valid, false if it is not.
	 */
	private boolean validateEvent(String eventToValidate)
	{
		Pattern p = Pattern.compile("[A-Z][A-Z0-9_]*+");
		Matcher m = p.matcher(eventToValidate);
		if(m.matches())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Counts the number of actions associated with the {@link StateFigureModel}
	 * and returns that number.
	 * @return
	 */
	public int getNumberOfActions()
	{
		int totalNumberOfActions = 0;
		for(String event : myActions.keySet())
		{
			totalNumberOfActions += myActions.get(event).size();
		}
		
		return totalNumberOfActions;
	}
	
	/**
	 * Creates a String representation of the StateFigureModel that can be used to
	 * generate the StateFigureModel again.
	 * @return A String representation of the StateFigureModel.
	 */
	public String exportXML()
	{
		String XMLString = String.format("<state name='%s'>", myName);
		for(String event : myActions.keySet())
		{
			// XMLString += String.format("<action trigger='%s'>",  trigger);
			for(String action : myActions.get(event))
			{
				XMLString += String.format("<action trigger='%s'>%s</action>", event, action);
			}
		}
		
		for(StateFigureModel sfm : myInternalStates)
		{
			XMLString += sfm.exportXML();
		}
		
		for(String event : myTransitionEvents.keySet())
		{
			XMLString += myTransitionEvents.get(event).exportXML();
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
	
	
	@Override
	public String toString()
	{
		return "StateFigureModel#" + hashCode() + " " + myName;
	}
	
	/**
	 * Writes the {@link StateFigureModel} to a file using the {@link DOMOutput}.
	 * @param out
	 * @throws IOException
	 */
	public void write(DOMOutput out) throws IOException
	{
		out.openElement("state");
		out.openElement("name");
		out.writeObject(myName);
		out.closeElement();

		out.openElement("actions");
		out.addAttribute("triggers", myActions.keySet().size());
		int i = 0;
		for(String trigger : myActions.keySet())
		{
			// I number the actions so I can read multiple 'action's without screwing up the XML parser
			out.openElement("action" + Integer.toString(i));
			out.addAttribute("trigger", trigger);
			out.addAttribute("numActions", myActions.get(trigger).size());
			for(String action : myActions.get(trigger))
			{

				out.writeObject(action);

			}
			out.closeElement();
			i++;
		}
		out.closeElement();
		
		out.closeElement();
	}
	
	/**
	 * Reads the {@link StateFigureModel} from a file using the {@link DOMInput}. 
	 * @param in
	 * @throws IOException
	 */
	public void read(DOMInput in) throws IOException
	{
		in.openElement("state");
        in.openElement("name");
        setName((String)in.readObject());
        in.closeElement();
        
        in.openElement("actions");
        int numberOfTriggers = in.getAttribute("triggers", 0);
        for(int i = 0; i < numberOfTriggers; i++)
        {
        	in.openElement("action" + Integer.toString(i));
        	String trigger = in.getAttribute("trigger", "DEFAULT");
        	int numActionsForTrigger = in.getAttribute("numActions", 0);
        	for(int j = 0; j < numActionsForTrigger; j++)
        	{
        		String action = (String) in.readObject();
        		addAction(trigger, action);
        	}
        	
        	in.closeElement();
        }

        in.closeElement();
        
        in.closeElement();
	}
}
