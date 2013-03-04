package edu.uwm.JStateDrawer.Models;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TODO: Add regex checking of trigger name.
public class TransitionModel {

	private StateFigureModel myStartState, myEndState;
	private String myEventTrigger;
	// The following pattern is one that ensures the first letter is an uppercase letter followed by
	// 0 or more uppercase letters, numbers, or underscores.
	private Pattern p = Pattern.compile("[A-Z][A-Z0-9_]*+");
	
	
	public TransitionModel()
	{
		myStartState = new StateFigureModel();
		myEndState = new StateFigureModel();
		myEventTrigger = "DEFAULT";
	}
	
	/**
	 * 
	 * @param trigger A String representing the event that triggers this transition.
	 * @param startState A {@link StateFigureModel} that is the source of this TransitionModel.
	 * @param endState A {@link StateFigureModel} that is the destination of this TransitionModel.
	 * @throws {@link IllegalArgumentException} if startState is null.
	 * @throws {@link IllegalArgumentException} if endState is null.
	 * @throws {@link IllegalArgumentException} if trigger is not at least length 1.
	 * @throws {@link IllegalArgumentException} if the trigger is not in the format of starting with one capitol letter followed by zero or more capitol letters, numbers, or underscores.
	 */
	public TransitionModel(String trigger, StateFigureModel startState,
			StateFigureModel endState)
	{
		Matcher m = p.matcher(trigger);
		
		if (startState == null)
		{
			throw new IllegalArgumentException("Cannot create with a null startState");
		}
		else if (endState == null)
		{
			throw new IllegalArgumentException("Cannot create with a null endstate.");
		}
		if(trigger.length() < 1)
		{
			throw new IllegalArgumentException("The new trigger is not long enough");
		}
		else if(!m.matches())
		{
			String error = String.format("The new trigger '%s' is not formatted properly.", trigger);
			error += " Triggers must start with an uppercase letter and be followed by zero or more uppercase letters, numbers, and underscores.";
			throw new IllegalArgumentException(error);
		}
		myEventTrigger = trigger;
		myStartState = startState;
		myEndState = endState;
	}
	
	/**
	 * Creates and exports a String XML representation of the transition.
	 * @return A String XML representation of the transition.
	 */
	public String exportXML()
	{
		String XMLString = "";
		
		XMLString += String.format("<transition trigger='%s' target='%s'/>", 
				myEventTrigger, myEndState.getName());
		
		return XMLString;
		
	}
	
	/**
	 * Sets the transition with the given start state.
	 * @param ss A {@link StateFigureModel} that will be set as the start state for this transition.
	 * @throws {@link IllegalArgumentException} if ss is null.
	 */
	public void setStartState(StateFigureModel ss){
		if (ss == null)
		{
			throw new IllegalArgumentException("Cannot set a null startState");
		}
		myStartState = ss;
	}
	
	/**
	 * Sets the transition with the given end state.
	 * @param es A {@link StateFigureModel} that will be set as the end state for this transition.
	 * @throws {@link IllegalArgumentException} if es is null.
	 */
	public void setEndState(StateFigureModel es){
		if (es == null)
		{
			throw new IllegalArgumentException("Cannot set a null endState");
		}
		myEndState = es;
	}
	
	/**
	 * Sets the transition with the given trigger.
	 * The trigger must start with a capitol letter and be followed by zero or more capitol letters,
	 * numbers, or underscores.
	 * @param trigger A String that is used to trigger the transition.
	 * @throws {@link IllegalArgumentException} if trigger is not at least length 1.
	 * @throws {@link IllegalArgumentException} if the trigger is not well-formed according to the definition in the method summary.
	 */
	public void setTrigger(String trigger){
		Matcher m = p.matcher(trigger);
		if(trigger.length() < 1)
		{
			throw new IllegalArgumentException("The new trigger is not long enough");
		}
		else if(!m.matches())
		{
			String error = String.format("The new trigger '%s' is not formatted properly.", trigger);
			error += " Triggers must start with an uppercase letter and be followed by zero or more uppercase letters, numbers, and underscores.";
			throw new IllegalArgumentException(error);
		}
		myEventTrigger = trigger;
	}
	
	/**
	 * 
	 * @return This TransitionModel's starting {@link StateFigureModel}
	 */
	public StateFigureModel getStartState(){
		return myStartState;
	}
	
	/**
	 * 
	 * @return This TransitionModel's ending {@link StateFigureModel}
	 */
	public StateFigureModel getEndState(){
		return myEndState;
	}
	
	/**
	 * 
	 * @return This TransitionModel's trigger string.
	 */
	public String getTrigger(){
		return myEventTrigger;
	}
}
