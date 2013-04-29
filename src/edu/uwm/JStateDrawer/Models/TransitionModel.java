package edu.uwm.JStateDrawer.Models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;

import edu.uwm.JStateDrawer.figures.TransitionFigure;

public class TransitionModel {

	private StateFigureModel myStartState, myEndState;
	private String myEventTrigger, myTarget;
	// The following pattern is one that ensures the first letter is an uppercase letter followed by
	// 0 or more uppercase letters, numbers, or underscores.
	private Pattern p = Pattern.compile("[A-Z][A-Z0-9_]*+");
	private List<String> myActions;
	private TransitionFigure myFigure;
	
	public TransitionModel()
	{
		myStartState = new StateFigureModel();
		myEndState = new StateFigureModel();
		myEventTrigger = "DEFAULT";
		myTarget = "DEFAULT";
		myActions = new ArrayList<String>();
		myFigure = new TransitionFigure();
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
		myTarget = myEndState.getName();
		myActions = new ArrayList<String>();
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
	 * Sets the transition with the given start state. It also updates the associated {@link StateFigureModel}s
	 * that are the old and new start states.
	 * @param ss A {@link StateFigureModel} that will be set as the start state for this transition.
	 * @throws {@link IllegalArgumentException} if ss is null.
	 */
	public void setStartState(StateFigureModel ss){
		
		if (ss == null)
		{
			throw new IllegalArgumentException("Cannot set a null startState");
		}
		// Updates the start state model of this transition if it isn't null.
		if(myStartState != null)
		{
			myStartState.removeOutgoingTransition(this);
		}
		myStartState = ss;
		myStartState.addOutgoingTransition(this);
		myStartState.addTransition(myEventTrigger, this);
		
	}
	
	/**
	 * Sets the transition with the given end state. It also updates the associated {@link StateFigureModel}s
	 * which are the old and new end states.
	 * @param es A {@link StateFigureModel} that will be set as the end state for this transition.
	 * @throws {@link IllegalArgumentException} if es is null.
	 */
	public void setEndState(StateFigureModel es){
		if (es == null)
		{
			throw new IllegalArgumentException("Cannot set a null endState");
		}
		if(myEndState != null)
		{
			myEndState.removeIncomingTransition(this);
		}
		myEndState = es;
		myTarget = myEndState.getName();
		myEndState.addIncomingTransition(this);
	}
	
	/**
	 * Sets the transition with the given trigger.
	 * The trigger must start with a capitol letter and be followed by zero or more capitol letters,
	 * numbers, or underscores.
	 * @param trigger A String that is used to trigger the transition.
	 * @throws {@link IllegalArgumentException} if trigger is not at least length 1.
	 * @throws {@link IllegalArgumentException} if the trigger is not well-formed according to the definition in the method summary.
	 * @return true if the Transition's event is successfully updated (its name is valid and 
	 * its start state does not already contain a transition with the same triggering event)
	 */
	public boolean setEvent(String newEvent){
		Matcher m = p.matcher(newEvent);
		if(newEvent.length() < 1)
		{
			throw new IllegalArgumentException("The new trigger is not long enough");
		}
		else if(!m.matches())
		{
			String error = String.format("The new trigger '%s' is not formatted properly.", newEvent);
			error += " Triggers must start with an uppercase letter and be followed by zero or more uppercase letters, numbers, and underscores.";
			throw new IllegalArgumentException(error);
		}
		if(myStartState.changeTransitionEvent(myEventTrigger, newEvent, this))
		{
			myEventTrigger = newEvent;
			return true;
		}
		return false;
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
	public String getEvent(){
		return myEventTrigger;
	}
	
	/**
	 * 
	 * @return The name of this {@link TransitionModel}'s end state.
	 */
	public String getTarget()
	{
		return myTarget;
	}
	
	/**
	 * Sets the target of this {@link TransitionModel} to the string parameter passed as long as
	 * it isn't the empty string. 
	 * @param newTarget
	 */
	public void setTarget(String newTarget)
	{
		if(newTarget != "")
		{
			myTarget = newTarget;
		}
	}
	
	/**
	 * Writes the {@link TransitionModel} to a file using the {@link DOMOutput}.
	 * @param out
	 * @throws IOException
	 */
	public void write(DOMOutput out) throws IOException
	{
		
		out.openElement("name");
		out.writeObject(myEventTrigger);
		out.closeElement();
		out.openElement("start");
		out.writeObject(myStartState);
		out.closeElement();
		out.openElement("end");
		out.writeObject(myEndState);
		out.closeElement();
		
		out.openElement("actions");
		out.addAttribute("count", myActions.size());
		for(int i = 0; i < myActions.size(); i++)
		{
			out.openElement("action" + Integer.toString(i));
			out.writeObject(myActions.get(i));
			out.closeElement();
		}
		out.closeElement();
	}
	
	/**
	 * Reads the {@link TransitionModel} from  a file using the {@link DOMInput}.
	 * @param in
	 * @throws IOException
	 */
	public void read(DOMInput in) throws IOException
	{

		in.openElement("name");
		setEvent((String)in.readObject());
		in.closeElement();

		in.openElement("start");
		setStartState((StateFigureModel)in.readObject());
		in.closeElement();

		in.openElement("end");
		setEndState((StateFigureModel)in.readObject());
		in.closeElement();
		
		in.openElement("actions");
		int numActions = in.getAttribute("count", 0);
		for(int i = 0; i < numActions; i++)
		{
			in.openElement("action" + Integer.toString(i));
			addAction((String)in.readObject());
			in.closeElement();
		}
		in.closeElement();

	}
	
	/**
	 * Accesses a list of the transition's actions.
	 * @return A {@code List<String>} of all the actions.
	 */
	public List<String> getAllActions()
	{
		return myActions;
	}
	
	/**
	 * Deletes the specified action. If {@code actionToRemove} is not in the list of actions
	 * false is returned. If the action is successfully removed, true is returned.
	 * @param actionToRemove
	 * @return
	 */
	public boolean removeAction(String actionToRemove)
	{
		if(!myActions.contains(actionToRemove))
		{
			return false;
		}
		else
		{
			myActions.remove(actionToRemove);
			return true;
		}
	}
	
	//TODO Decide whether this needs to be a boolean return.
	/**
	 * Adds a new action to the list 
	 * @param newAction
	 * @return
	 */
	public boolean addAction(String newAction)
	{
			myActions.add(newAction);
			return true;
	}
	
	@Override
	public String toString()
	{
		return "TransitionModel#" + hashCode() + " " + myEventTrigger;
	}
	
	/**
	 * Sets the {@link TransitionFigure} associated with this {@link TransitionModel}
	 * so long as {@code fig} is not null.
	 * @param fig
	 */
	public void setFigure(TransitionFigure fig)
	{
		if (fig != null)
		{
			myFigure = fig;
		}
	}
	
	public TransitionFigure getFigure()
	{
		return myFigure;
	}
}
