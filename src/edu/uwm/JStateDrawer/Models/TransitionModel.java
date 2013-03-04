package edu.uwm.JStateDrawer.Models;

public class TransitionModel {

	private StateFigureModel myStartState, myEndState;
	private String myEventTrigger;
	
	public TransitionModel()
	{
		myStartState = new StateFigureModel();
		myEndState = new StateFigureModel();
		myEventTrigger = "default";
	}
	
	public TransitionModel(String eventTrigger, StateFigureModel startState,
			StateFigureModel endState)
	{
		myEventTrigger = eventTrigger;
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
	
	
	
}
