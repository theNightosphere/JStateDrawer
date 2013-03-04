package edu.uwm.JStateDrawer.Models;

import edu.uwm.JStateDrawer.figures.StateFigure;

public class TransitionModel {

	private StateFigureModel myStartState, myEndState;
	private String myEventTrigger;
	
	public TransitionModel()
	{
		myStartState = new StateFigureModel();
		myEndState = new StateFigureModel();
		myEventTrigger = "default";
	}
	
	public TransitionModel(String trigger, StateFigureModel startState,
			StateFigureModel endState)
	{
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
		myEventTrigger = trigger;
		myStartState = startState;
		myEndState = endState;
	}
	
	
	public String exportXML()
	{
		String XMLString = "";
		
		//XMLString += String.format("<transition trigger=%s><start>%s</start><end>%s</end></transition>", 
				//myEventTrigger, myStartState, myEndState);
		
		XMLString += String.format("<transition trigger=\"%s\"/>", myEventTrigger);
		
		return XMLString;
		
	}
	
	public void setStartState(StateFigureModel ss){
		if (ss == null)
		{
			throw new IllegalArgumentException("Cannot set a null startState");
		}
		myStartState = ss;
	}
	
	public void setEndState(StateFigureModel es){
		if (es == null)
		{
			throw new IllegalArgumentException("Cannot set a null endState");
		}
		myEndState = es;
	}
	
	public void setTrigger(String trigger){
		if(trigger.length() < 1)
		{
			throw new IllegalArgumentException("The new trigger is not long enough");
		}
		myEventTrigger = trigger;
	}
	
	public StateFigureModel getStartState(){
		return myStartState;
	}
	
	public StateFigureModel getEndState(){
		return myEndState;
	}
	
	public String getTrigger(){
		return myEventTrigger;
	}
}
